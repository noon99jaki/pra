package edu.cmu.pra.data;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.TMapSVecSa;
import edu.cmu.lti.algorithm.container.MapSVecS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.run.Param;
import edu.cmu.pra.CTag;
import edu.cmu.pra.postprocess.Latex;
import edu.cmu.pra.postprocess.Recom;


public class ACL {

	static void paperInfor(){
		FFile.mkdirs(fdData);
		ACLMetaInfo.reader("extracted").selectS(CTag.PRA)
			.save(fdData+"papers.db",null);
		
		System.out.println(ACLMetaInfo.printSchema());
	}
	
	static void parseCitations(){
		TMapSVecSa mv= new TMapSVecSa();
		
		BufferedWriter bw = FFile.newWriter(fdData+"Ref.db");
		for (String l: FFile.enuLines(fdLinks+fnCite)){
			String vs[]=l.split(" ==> ");
			String id=vs[0];
			String ref=vs[1];
			
			if (ref.length()<10){
				mv.getC(id).add(ref);
			}
			else{
				if (ref.startsWith("[RELATED]"))
					ref=ref.substring(10);
				ref=FPaper.processTitle(ref);
				FFile.writeln(bw, "Refer("+id+","+ref+")");
			}
		}
		FFile.close(bw);
		mv.save("citations");
		
		BufferedWriter bwC = FFile.newWriter(fdData+"Citation.db");
		for (String id: mv.keySet())
			FFile.writeln(bwC,"Cites("+id+","+mv.get(id)+")" );
		FFile.close(bwC);

		System.out.print("Refer(paper,word)\n");
		System.out.print("Cites(paper,paper)\n");

	}
	
	static void createScenarious(){
		MapSVecS mv= new MapSVecS();
		mv.loadFile("citations");
		
		MapSI mYearPaperCount=new MapSI();

		MapSVecS mvYearAuthorCitation= new MapSVecS();
		
		int n=0;;
//		BufferedWriter bwF = FFile.newWriter(fdData+"sce.FAuthor");
		BufferedWriter bwA = FFile.newWriter(fdData+"sce.A");

		for (ACLMetaInfo i: ACLMetaInfo.reader("extracted")){
			if (i.vAuthors.size()==0) continue;
			VectorS vC= mv.get(i.PaperID);
			if (vC==null) continue;
			++n;
			FFile.writeln(bwA,i.year+","+i.vAuthors.join(" ")+","+vC.join(" ") );
//			FFile.writeln(bwF,i.year+","+i.vAuthors.firstElement()+","+vC.join(" ") );
			
			String key=i.year+","+i.vAuthors.firstElement();
			mvYearAuthorCitation.getC(key).addAll(vC);
			//if (i.year.length())
			mYearPaperCount.plusOn(i.year);
			
		}
//		FFile.close(bwF);
		FFile.close(bwA);
		
		System.out.println("n="+n);
		mYearPaperCount.save("mYearPaperCount");
		mvYearAuthorCitation.save(fdData+"sce.FA",",");
	}
	
	static void filter0506(String fn){		
		FTable.filterByColumn(fdData+fn, fdData+fn+".0506", 0
				, new SetS("2005 2006"),",");		
	}
	static void statAuthors(){
		MapSI mAuthorCount=new MapSI();
		for (ACLMetaInfo i: ACLMetaInfo.reader("extracted"))
			for (String a: i.vAuthors)
				mAuthorCount.plusOn(a);
		mAuthorCount.saveSortedByValue("mAuthorCount");
		
		MapII mCountHist= new MapII();
		for (Integer c: mAuthorCount.values())
			mCountHist.plusOn(c);
		System.out.print(mCountHist.join("\t","\n"));
	}
	
	static void createScenBigGuys(int thNPaper){
		MapSVecS mv= new MapSVecS();
		mv.loadFile("citations");
		
		SetS mBigGuys= new SetS();
		for (VectorS vs: FFile.enuRows("mAuthorCount"))
			if (Integer.parseInt(vs.get(0))>=thNPaper)
				mBigGuys.add(vs.get(1));
		
		
		int nE=0;;
		int nG=0;;
		BufferedWriter bwE = FFile.newWriter(fdData+"sce.Big"+thNPaper+".each");
		BufferedWriter bwG = FFile.newWriter(fdData+"sce.Big"+thNPaper+".group");

		for (ACLMetaInfo i: ACLMetaInfo.reader("extracted")){
			if (i.vAuthors.size()==0) continue;
			VectorS vC= mv.get(i.PaperID);
			if (vC==null) continue;
			String citation=vC.join(" ")+","+i.PaperID;
			if (citation.length()<2){
				System.out.println("skip a record with no citations "+i.PaperID);
				continue;
			}
			
			//for (String a: i.vAuthors){
			for (int iA=0;iA<i.vAuthors.size();++iA){
				String a=i.vAuthors.get(iA);
				if (!mBigGuys.contains(a)) continue;
				++nE;
				FFile.writeln(bwE,i.year+","+a+","
						+i.vAuthors.joinEx(" ", iA)+","+ citation);
			}
			
			for (String a: i.vAuthors){
				if (!mBigGuys.contains(a)) continue;
				++nG;
				FFile.writeln(bwG,i.year+","+i.vAuthors.join(" ")+","+citation);
				break;
			}
			
			
			String key=i.year+","+i.vAuthors.firstElement();
		}
		FFile.close(bwE);
		FFile.close(bwG);
		System.out.printf("thNPaper=%d |mBigGuys|=%d nLinesE=%d nLinesG=%d\n"
				,thNPaper ,mBigGuys.size(),nE,nG);
	}
	/*
	thNPaper=10 |mBigGuys|=443 nLinesE=5721 nLinesG=4038
	thNPaper=20 |mBigGuys|=120 nLinesE=2559 nLinesG=2184
	thNPaper=30 |mBigGuys|=29 nLinesE=891 nLinesG=842
	thNPaper=40 |mBigGuys|=12 nLinesE=459 nLinesG=456

	thNPaper=10 |mBigGuys|=443 nLinesE=5472 nLinesG=3877
thNPaper=20 |mBigGuys|=120 nLinesE=2412 nLinesG=2083
rthNPaper=30 |mBigGuys|=29 nLinesE=834 nLinesG=786
thNPaper=40 |mBigGuys|=12 nLinesE=425 nLinesG=422

    7906 sce.A
    1567 sce.A.0506
    5721 sce.Big10.each
    4038 sce.Big10.group
    3800 sce.Big15.each
    3016 sce.Big15.group
    2559 sce.Big20.each
    2184 sce.Big20.group
    6912 sce.FA
    1307 sce.FA.0506	
    
    5543 cite.nP10
    1335 cite.nP10.1A
     120 cite.nP10.1A.midY
     288 cite.nP15.1A
    2049 cite.nP20
      83 cite.nP20.1A
     549 cite.nP30
      29 cite.nP30.1A
     243 cite.nP40
      14 cite.nP40.1A
    4012 cite.nP5.1A

*/


	static String fdData="../data/";
	static String fdLinks="acl-arc-090501d1/interlink/aan/";
	static String fnCite="everything.20080325.net";
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		//ACLExtractor.extract();
		//paperInfor();
		//parseCitations();
		
		//createScenarious();		
		//filter0506("sce.FA");	filter0506("sce.A");
		//statAuthors();
		
		
		//createScenBigGuys(10);
		
		//Cite2Read.generateReadings(true, new Integer[]{5,10,15,20});//
		//Cite2Read.generateReadings(false, new Integer[]{10,20,30,40});//
		//SmallJobs.dumpANode("author", "WilliamCMann");
		
	/*	FFile.enuRows(fdData+fnScen,sep).groupBy(new PipeVSS(2))
		.select(Pipes.pipePickMid).select(Pipes.pipeJoinVS)
		.save(fdData+fnScen+".midY");*/
		
		
		//SmallJobs.dumpNodeLinkCountsByTime();
		//Param.overwrite("e0Gap=1,maxE0Step=4,sMode=P,minParticle=0.0001");
		//EntityRank.run(false);

		
		//String fd="/usr0/nlao/code_java/ni/run/acl/run.yya/sch.yya/cite.nP20/L3_P1e-04/b1e-01P_Sqr_L1=1e-04_L2=3e-03/00/";
		//String fd="/usr0/nlao/code_java/ni/run/acl/run.yya/sch.yya/cite.nP20/L5_P1e-04/b1e-01P_Sqr_L1=1e-04_L2=3e-03/00/";
		//String fd="/usr0/nlao/code_java/ni/run/acl/run.yya/sch.yya/cite.nP20/L4_P1e-04/b1e-01P_Sqr_L1=1e-04_L2=3e-03/00/";
		//Latex.attachComments(fd+"model.avg");
		
		//Latex.latexPathYeast("Models");

		
		Param.overwrite("recomMode=CH,scSelf=0");
		Param.overwrite("collaborateK=1");		Recom.run();
		Param.overwrite("collaborateK=3");		Recom.run();
		Param.overwrite("collaborateK=10");		Recom.run();
		Param.overwrite("collaborateK=30");		Recom.run();
		Param.overwrite("collaborateK=100");		Recom.run();
		
		Param.overwrite("recomMode=CH,scSelf=1");
		Param.overwrite("collaborateK=1");		Recom.run();
		Param.overwrite("collaborateK=3");		Recom.run();
		Param.overwrite("collaborateK=10");		Recom.run();
		Param.overwrite("collaborateK=30");		Recom.run();
		Param.overwrite("collaborateK=100");		Recom.run();

		//Param.overwrite("recomMode=C");		Recom.run();
	
	}
	
	static String fnScen="cite.nP10.1A";
	static String sep=",";

	
}
