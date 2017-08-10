package edu.cmu.pra.data;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapIS;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.text.AcronymGenerator;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.SmallJobs;
import edu.cmu.pra.postprocess.Recom;

/**
 * this class transform crawled data into tab separated db files
 * @author nlao
 *
 */
public class Yeast {


	



	/**
1) PubMed ID (optional)		- the unique PubMed identifer for a reference
2) citation (mandatory)		- the citation for the publication, as stored in SGD
3) gene name (optional)		- Gene name, if one exists
4) feature (optional)		- Systematic name, if one exists
5) literature_topic (mandatory)	- all associated Literature Topics of the SGD Literature Guide
				  relevant to this gene/feature within this paper
				  Multiple literature topics are separated by a '|' character.
6) SGDID (mandatory)		- the SGDID, unique database identifier, for the gene/feature

47
Hartman FC, et al. (1975) The influence of pH on the interaction of inhibitors with triosephosphate isomerase and determination of the pKa of the active-site carboxyl group. Biochemistry 14(24):5274-9
TPI1
YDR050C
Other Features|Regulation of
S000002457


	to DB(thus big table), and to the graph file
	 */
	//public boolean bSingleFile=false;
	
	
	public  static void parseGeneLiterature() {
		
		System.out.println("parseGeneLiterature()");
		BufferedWriter bw=//bSingleFile?bwDB:
			 FFile.newWriter("../data/GeneCitation.db");
		
		//FFile.write(bwSchema, "Gene(paper,gene)\n");
		//FFile.write(bwSchema, "Aspect(paper,gene,*R)\n");
		AcronymGenerator ag = new AcronymGenerator();
		
		String pmidLast=null;
		//StringBuffer sb = new StringBuffer();
		String pmid =null;
		VectorS vsGene= new VectorS();
		SetI mGene= new SetI();
		
		for (String line: FFile.enuLines("gene_literature.tab")) {
			String[] vs = line.split("\t");			
			pmid = vs[0];
			String gene= vs[2].replace(',','_')
				.replace('(','_').replace(')','_');
			if (pmid.length()==0 || gene.length()==0)
				continue;
			VectorS vsCode=ag.getCode(vs[4].split("\\|"));

			FFile.write(bw, String.format(	"Aspect(%s,%s,%s)\n"
				,pmid, gene, vsCode.join(" ")));
			
			vsGene.add(gene);

			if (pmidLast!=null){
				if (!pmid.equals(pmidLast)){
					FFile.write(bw, String.format("Gene(%s,%s)\n"
						,pmid, vsGene.join(" ")));					
					vsGene.clear();
				}					
			}
			int id= Integer.parseInt(pmid);
			mGene.add(id);
			pmidLast=pmid;
		}
		
		mGene.save("pmid.sgd");
		
		FFile.write(bw, String.format("Gene(%s,%s)\n"
				,pmid, vsGene.join(" ")));//sb.toString()));					
		
		ag.mTopicCode.save("mTopicCode.txt");
		ag.mCodeTopic.save("mCodeTopic.txt");
		
		//if (!bSingleFile)		
		FFile.close(bw);
	}

	


	//Aspect(1253,HXK1,PPP SLC PNAS)
	//Gene(1253,HXK1)
	public static MapIS loadGeneCitation(String dbFile){
		MapIS m= new MapIS();
		for (String line: FFile.enuLines(dbFile)){
			String vs[]= line.split("[(,)]");
			if (!vs[0].equals("Gene")) continue;
			if (vs[1].length()==0) continue;
			if (vs[2].length()==0) continue;

			Integer i=Integer.parseInt(vs[1]);
			String genes= vs[2];
			
			m.put(i,genes);
		}
			
		return m;
	}
	//Cites(11134535,10841563 10693811 ...)

	public static MapIS loadCitation(String dbFile){
		MapIS m= new MapIS();
		for (String line: FFile.enuLines(dbFile)){
			String vs[]= line.split("[(,)]");
			if (!vs[0].equals("Cites")) {
				System.out.println("wrong format="+line);
				return m;
			}
			if (vs[1].length()==0) continue;
			if (vs[2].length()==0) continue;

			Integer i=Integer.parseInt(vs[1]);
			String genes= vs[2];
			
			m.put(i,genes);
		}
			
		return m;
	}
	public static void generateSenarios(String fn, String fn1){
		//merge information of GeneCitation.db, Abstract.db, RefSGD.db
		VectorS vsT=FString.splitVS(PMAbsInfor.getScenTitle(),"\t");
		FFile.saveString(	"scenarios.title"
				,PMAbsInfor.getScenTitle().replace('\t',',')	);

		FFile.saveString("scenarios.titleID",	vsT.joinIndexed("=", "\n"));

		
		MapIS mGene= loadGeneCitation("../data/GeneCitation.db");
		MapIS mRef= loadCitation("../data/RefSGD.db");
		//MapIS mRef= MapIS.fromFile("pmid.pmc.sgd.crawl.ex");
		
		System.out.println("|mGene|="+mGene.size());
		System.out.println("|mRef|="+mRef.size());
		System.out.println("generateSenarios()");
		
		int n=0;int nS=0;
		BufferedWriter bw= FFile.newWriter(fn1);
		for (PMAbsInfor abs:  PMAbsInfor.reader(fn)){
			if (abs.year==null) continue;
			++n;
			String ref=mRef.get(abs.iPMID);
			if (ref==null) continue;
			if (ref.length()==0) continue;
			
			String gene=mGene.get(abs.iPMID);
			if (gene==null) continue;
			if (gene.length()==0) continue;
			
			if (abs.vAuthors.size()==0) continue;
			
			if (abs.journal.length()==0) continue;
			
			FFile.writeln(bw, abs.printSenario(ref, gene));++nS;
		}
		FFile.close(bw);
		
		System.out.println("#abstract="+n);
		System.out.println("#scenario="+nS);
		//SetS.fromFile("pmid.pmc.sgd.crawl.ex",0).save("upmid");
		//SetS.fromFile("pmid.sgd.crawl.ex",0).save("upmid.abs");
		
		return ;
	}
	/*
    53182 cite.nP10
     3011 cite.nP10.1A
    25384 cite.nP20
      478 cite.nP20.1A
    15524 cite.nP30
      126 cite.nP30.1A
        0 cite.nP40
     6530 cite.nP50
        0 cite.nP50.1A
        0 cite.nP60
     3034 cite.nP70
        0 cite.nP70.1A
        0 cite.nP80
        0 cite.nP90        
     1143 cite.nP100
        0 cite.nP100.1A
        
  39826 cite.nP10
  20360 cite.nP20
  12843 cite.nP30
   8429 cite.nP40
   5763 cite.nP50
   4083 cite.nP60
   2824 cite.nP70
   2009 cite.nP80
   1549 cite.nP90
   1234 cite.nP100
  19127 Read.db
   */

	public static void exp(){
		//Param.overwrite("e0Gap=1,maxE0Step=4,sMode=P,minParticle=0.0001");
		//EntityRank.run(false);

		//String fd="/usr0/nlao/code_java/ni/run/yeast2/run.yya/sch.cite.yya/cite.nP100/L3_P1e-04/b1e-01P_Sqr_L1=1e-04_L2=1e-01/00/";
		//String fd="/usr0/nlao/code_java/ni/run/yeast2/run.yya/sch.cite.yya/cite.nP100/L3_P1e-04/b1e-01P_Sqr_L1=3e-02_L2=1e-01/00/";
		//Latex.attachComments(fd+"model.avg");

		//Latex.latexPathYeast("Models");
		
/*		Param.overwrite("recomMode=CH,scSelf=0");
		Param.overwrite("collaborateK=1");		Recom.run();
		Param.overwrite("collaborateK=3");		Recom.run();
		Param.overwrite("collaborateK=10");		Recom.run();
		Param.overwrite("collaborateK=30");		Recom.run();
		Param.overwrite("collaborateK=100");		Recom.run();
	*/	
		Param.overwrite("recomMode=CH,scSelf=1");
		Param.overwrite("collaborateK=1");		Recom.run();
		Param.overwrite("collaborateK=3");		Recom.run();
		Param.overwrite("collaborateK=10");		Recom.run();
//		Param.overwrite("collaborateK=30");		Recom.run();
//		Param.overwrite("collaborateK=100");		Recom.run();

	//	Param.overwrite("recomMode=C");		Recom.run();
		
	}
	public static void data(){
		FTable.subColumn("gene_literature.tab",0, "pmid.sgd");
		//parseGeneLiterature();
		//PMAbsInfor.writePRASchema(FFile.bufferedWriter("abs.sch"));
		PM.addAbstractInfor("pmid.crawl.ex", "../data/papers.db");
		PMC.citation2praFile("pmid.pmc.crawl.ex","../data/Ref.db");		

		//FTable.subColumns(fn, new VectorI(new Integer[]{0,11}), "title.yeast");
	
				//FFile.subColumn("pm.sgd.crawl.ex",0, "pm.exed");
				//SetS.loadFile("pm.exed").save("pm.exed.uniq");	
				//(new YeastData()).run();
		//SmallJobs.dump2PCTFormat();		
		
		//Cite2Read.bRequireUnderScore=true;	
		//Cite2Read.generateReadings(true,new Integer[]{10,20,30,50,70,100});//
		
		
		
	}
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		//data();
		//FFile.mkdirs("../data");
		//PM.addAbstractInfor("pmid.crawl.ex", "../data/papers.db");
		//PMC.citation2praFile("pmid.pmc.crawl.ex","../data/Ref.db");
		//parseGeneLiterature();

		//Cite2Read.statAuthors();
		//Cite2Read.generateReadings(false,new Integer[]{10,20,30,50,70,100});//

//		PRA.generateTextEdges("heading chemical gene");

		//SmallJobs.dump2PCTFormat();
		SmallJobs.dump2ghirlFormat(	new SetS("Title wheading wgene wchemical"));
		return;
	}
}
