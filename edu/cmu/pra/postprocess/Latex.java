package edu.cmu.pra.postprocess;

import java.io.BufferedWriter;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.foil.RelationLine;

public class Latex {

	// transform path to latex format
	public static void latexPath(String fn,MapSS mLatex){
		//BufferedWriter bw=FFile.newWriter(fn+".lex");
		
		int n=0;
		for (VectorS vss : FFile.enuRows(fn)){
			if (vss.size()>0)
			if (vss.get(0).length()==0)
				continue;
			if (vss.size()<3){
				System.out.println(vss.join(" "));//FFile.line_);
				continue;
			}
			VectorS vs=FString.splitAndKeep(vss.get(1),"()");	
			for (int i=0; i<vs.size(); ++i){
				String s= mLatex.get(vs.get(i));
				if (s==null)
					s=vs.get(i);
				if (s.startsWith("_"))
					s=s.substring(1)+"^{-1}";
				vs.set(i, s);
			}			
			String head= (vss.get(0).startsWith("-"))?
					"Disfavor ":"Prefer ";
			head="";
			
			++n;
			System.out.println(
					n+"\t&"+vss.get(0)+"\t&${\\rm "+vs.join("")
					+"}$\t&"+head+vss.get(2)+"\\\\");
		}
	}
	
	public static void latexPathWK(String fn){
		MapSS mLatex= MapSS.fromStrings(new String[]{
				"(","\\xrightarrow{"
				,")","}"
				});
		
		//mLatex.putAll(.relation_printName_);
		for (Map.Entry<String, RelationLine> it: 
			RelationLine.loadRelationInfor().entrySet()) {
			mLatex.put(it.getKey(), it.getValue().name);
		}
		latexPath(fn,mLatex);
	}


	public static void latexPathYeast(String fn){

		MapSS mLatex= MapSS.fromStrings(new String[]{
			"feature",""
			,"(","\\xrightarrow{"	//\\rm 
			,")","}"
			
			,"y","year","p","paper","j","journal"
			,"g","gene","a","author","w","word"
			,"i","institute","c","chemical","h","topic"
			,"T","e^*"
			,"bias","bias"
			
			,"jour","AnyJournal"
			,"gene","AnyGene"
			,"year","AnyYear"
			,"inst","AnyInstitute"
			,"pape","AnyPaper"
			,"head","AnyTopic"

			,"Afte","After"
			,"Jour","In"
			,"_Jou","In^{-1}"
			,"_Cit","Cite^{-1}"
			,"_Cites","Cite^{-1}"
			
			,"Gene","HasGene"
			,"_Gen","HasGene^{-1}"
			,"_Gene","HasGene^{-1}"
			
			,"Auth","Write^{-1}"
			,"Author","Write^{-1}"
			,"_Aut","Write"
			,"_Author","Write"
			
			,"Titl","HasTitle"
			,"Title","HasTitle"
			,"_Tit","HasTitle^{-1}"
			,"_Title","HasTitle^{-1}"
			,"Year","In"
			,"_Yea","In^{-1}"
			,"_Year","In^{-1}"
			
			,"YRead","Read"
			
			,"Aff","Affiliation"
			,"_Aff","Affiliation^{-1}"
			
			,"Chem","HasChem."
			,"_Che","HasChem.^{-1}"
			,"_Chem","HasChem.^{-1}"
			
			,"DHea","HasMD"
			,"DHead","HasMD"
			,"_DHe","HasMD^{-1}"
			,"_DHead","HasMD^{-1}"
			
			,"QHea","HasMQ"
			,"QHead","HasMQ"
			,"_QHe","HasMQ^{-1}"
			,"_QHead","HasMQ^{-1}"
			
			,"DmHe","HasMajorMD"
			,"DmHead","HasMajorMD"
			,"_DmH","HasMajorMD^{-1}"
			,"_DmHead","HasMajorMD^{-1}"
			
			,"QmHe","HasMajorMQ"
			,"QmHead","HasMajorMQ"
			,"_QmH","HasMajorMQ^{-1}"
			,"_QmHead","HasMajorMQ^{-1}"
			
		//	,"Refer","Refererence"
			,"_Refer","Refer^{-1}"

			,"YRea","Read"
			,"Raut","Write"
		
			});
		latexPath(fn, mLatex);
	}
	public static void attachComments(String fnModel){
		System.out.println("attachComments to\n"+fnModel);
		
		MapSS mCom= MapSS.fromFile(Param.ms.get("fnFComments"));
		BufferedWriter bw= FFile.newWriter(fnModel+".cmt");
		for (VectorS vs: FFile.enuRows(fnModel)){
			if (mCom.containsKey(vs.get(1)))
				vs.set(2, mCom.get(vs.get(1)));
			FFile.writeln(bw, vs.join("\t"));
		}
		FFile.close(bw);
	}
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");

		//latexPathYeast("path.read.3.ER3");
		//latexPathYeast("path.cite.3.ER3");
		//latexPathWK("path.lifted");
	}
}
