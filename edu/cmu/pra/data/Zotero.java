package edu.cmu.pra.data;

import java.io.BufferedWriter;
import java.util.Map;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.TMapIVecSa;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.ir.SimpleIndex;
import edu.cmu.lti.algorithm.ir.SimpleIndex.Doc;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.xml.FDom;

public class Zotero {
	static SetS mIgnoreTag= new SetS(new String[]{
			"#text","bib:Book","bib:Journal","z:Attachment"	, "bib:Memo"});
	static int nParsed;
	static int nMatched;
	
	private void parseNode(Node e){
		String type=e.getNodeName();
		if (mIgnoreTag.contains(type)) return;
		
		String id="";
		String title="";
		String link="";
		String url="";		
		String about=e.getAttributes().getNamedItem("rdf:about").getTextContent();
		
		if (type.equals("bib:Document"))				
			url=about;
		else			
			id= about;			
		
		for (Node e1=e.getFirstChild(); e1!=null; e1=e1.getNextSibling()){
			if (e1.getNodeName().equals("dc:title"))
				title= e1.getTextContent().toLowerCase();
			if (e1.getNodeName().equals("link:link"))
				link= e1.getTextContent();					
		}
		int p= title.indexOf(" --");
		if (p>0) 
			title=title.substring(0,p);
		
		if (title.startsWith("ingentaconnect"))
			title= title.substring(15);
		
		p= title.lastIndexOf(" : ");
		if (p>0) 
			title=title.substring(p+3);
		
		
		if (!title.endsWith(".")) title= title+".";

		//+"\t"+id  +"\t"+link
		++nParsed;
		Integer pmid= mTitles.get(title);
		if (pmid!=null){
			//viMatched.add(pmid);
			vsMatched.add(pmid+"");
			++nMatched;
			//System.out.println(nMatched+"\t"+title);
		}
		else{
			System.out.println("unmatched"+"\t"+type+"\t"+title);
			FFile.writeln(bwUnM, title);
		}
	}
	
	
	VectorS vsMatched= new VectorS();
	//VectorI viMatched= new VectorI();
	//MapII mYear= new MapII();	//pmid-->year
	MapSI mTitles= new MapSI();// title-->pmid, 
	//pubmedid, intermine_year, journal,firstauthor, title

	
	//[solved]
	//211/364 ( : ) (ingentaconnect)
	//204/364(--)	
	//187/364(case)		
	//153/364
	
	//[remaining]
	//identification of a functional core in the rna component of rnase...
	//3'to 5'degradation 			3'-to-5'
	//dhr1p, a putative deah-box rna helicase, is associated with the box c+ d snornp u3.
	
	BufferedWriter bwUnM=null;
	public boolean parseXML(){
		mTitles.fromFile(fnTitle,1,0, true); //loadTitles();

		bwUnM = FFile.newWriter(fnZotero+".failed");	
		
		Element e=FDom.loadFile(fnZotero);
		nParsed=0;
		for (Node n =  e.getFirstChild(); n!=null; n=n.getNextSibling()){
			parseNode(n);
		}
		System.out.printf("%d/%d",nMatched,nParsed);
		FTable.filterByColumn(fnTitle, 0, vsMatched.toSet());
		FFile.close(bwUnM);
	
		return true;
	}
	
	
	public static void matchHardCases(){
		double th=0.2;
		nMatched=0;
		nParsed=0;
		SimpleIndex idx = new SimpleIndex();
		idx.loadDocs(fnTitle);
		
		BufferedWriter bw = FFile.newWriter(
				fnTitle+".idf"+th);

		
		for (String title:  SetS.fromFile(fnZotero+".failed")){
			++nParsed;
			title= title.toLowerCase();
			if (title.contains("::")) continue;
			

			MapID rlt=  (title.contains("..."))?
					idx.retrieve(title, th/2):idx.retrieve(title, th);
			if (rlt ==null) 
				continue;
			
			int n=idx.rltDoc.size();			
			if (n ==0) continue;
			++nMatched;
			
			
			System.out.println();
			System.out.println(nMatched+"\t"+title);

			for (int i=0; i<n && i<5; ++i ){
				Doc d= idx.vDoc.get(idx.rltDoc.get(i));				
				System.out.printf("%.3f\t%s\t%s\n"
					,idx.rltScore.get(i), d.eid, d.txt);
				
				if (i==0){
					FFile.writeln(bw, "\n         \t"+title);
					FFile.writeln(bw, d.eid +"\t"+ d.txt);
				}
			}
		}
		FFile.close(bw);
		System.out.printf("%d/%d",nMatched,nParsed);
		return;
	}
	
	
	//3491	1976	J_Bacteriol	Sumrada_R Gorski_M Cooper_T	
	//Urea transport-defective strains of Saccharomyces cerevisiae.

	public static void generateSenarioFile(String fnMatched){
		
		
		TMapIVecSa mvs= TMapIVecSa.newColumn(fnMatched+".abs",1,0);
		//year--> pmids
		
		BufferedWriter bwDB = FFile.newWriter(user+".db");	
		BufferedWriter bwS = FFile.newWriter("scenarios."+user);
		
		FFile.writeln(bwDB, "Read("+user+","
				+mvs.toVectorV().join(" ")+")" );
		
		for (Map.Entry<Integer, VectorX<String>>e: mvs.entrySet()){
			int year =e.getKey();
			VectorS vs = (VectorS) e.getValue();
			FFile.writeln(bwDB, "YRead("+year+","+vs.join(" ")+")" );
			FFile.writeln(bwS,(year+1)+","+year+","+user+","+vs.join(" "));
		}

		FFile.close(bwDB);
		FFile.close(bwS);
	}
	

	private static void abstract2Titles(){
		BufferedWriter bw = FFile.newWriter("title.yeast");	
		for (String line: FFile.enuLines("abstract.PM.yeast")){
			String vs[] = line.split("\t");
			String title=vs[5];
			FFile.write(bw,vs[0]+"\t"+vs[1]+"\t"+vs[2]+"\t"+vs[4]+"\t"+title+"\n");
		} 
		FFile.close(bw);
	}
	
	static String user="Woolford_JL";

	static String fnTitle="title.yeast";
	static String fnZotero="woolford.xml";
	public static void main(String[] args){
				//abstract2Titles();
				//FFile.toLower(titleFile);
				//titleFile=titleFile+".lower";
		//(new Zotero()).parseXML();
		//matchHardCases();
		generateSenarioFile("matched");
	}
}
