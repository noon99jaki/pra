package edu.cmu.pra.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.text.FPattern;
import edu.cmu.lti.util.text.FString;

public class PMC {
	
	/* obsoleted, we are crawling htmls into a single file now 
	 */
	private static void dumpPMCRefFolder() {
		System.out.println("dumpPMCRef()");
		//Pattern paFile = Pattern.compile("reference.pl\\?pmid=(\\d*)");
		BufferedWriter bw = FFile.newWriter("SGD.PMC.ref.lines.200910");
		
		for (String pmid : FFile.getFileNames(fdAbstract,"\\d*")) {

			BufferedReader br = FFile.newReader(fdAbstract + pmid);
			String line = null;
			while ((line = FFile.readLine(br)) != null) {
				if (line.indexOf("div class=\"rprt\"")==-1)		continue;
				if (!line.startsWith("        <div><div class=\"rprt\"")) 
					continue;
				FFile.write(bw, pmid + "\t" + line + "\n");
				break;
			}
			FFile.close(br);
		}
		FFile.close(bw);
	}

	/* obsoleted, we are crawling htmls into a single file now 
	 */
	private static void parsePMCRefFolder(){
		System.out.println("parsePMCRef()");

		Pattern paFile = Pattern.compile("pmid_(\\d*)");
		BufferedWriter bw = FFile.newWriter("PMC.ref.txt");
		//BufferedWriter bwP = FFile.bufferedWriter("PMC.ref.pair.txt");
		
		int nPaper=0;
		int nRef=0;
		for (String pmid : FFile.getFileNames(fdAbstract,"\\d*")) {
			FFile.write(bw, pmid +"\t");
			
			String txt =FFile.loadString(fdAbstract + pmid);
			VectorS vs=FPattern.matchAll(txt, 
					"div class=\"rprt\" id=\"pmid_(\\d+)");
				++nPaper;
				nRef += vs.size();
				//for (String id: vs)
					//FFile.write(bwP, pmid+"\t"+ id+"\n");
			FFile.write(bw, pmid+"\t"+vs.join(" ")+"\n");
		}
		FFile.close(bw);
		//FFile.close(bwP);
		System.out.format("\n%d papers, %d references parsed", nPaper, nRef);
	}
	
	
	
	private static void filterPMCReftoSGD() {
		System.out.println("filterPMCReftoSGD()");
		
		VectorI vi= new VectorI();
		vi.loadSet("pmid_sgd.txt",100000);
		
		
		BufferedReader br = FFile.newReader("PMC.ref.txt");
		BufferedWriter bw = FFile.newWriter("PMC.ref.SGD.txt");
		//FFile.readLine(br);
		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split("\t| ");
			if (vs[1].length()==0)
				continue;
			
			VectorS vsRef = new VectorS();
			for (int i=1; i<vs.length; ++i){
				int pmid = Integer.parseInt(vs[i]);
				if (vi.getE(pmid)==0)
					continue;
				vsRef.add(vs[i]);
			}
			if (vsRef.size()==0)
				continue;
			FFile.write(bw, vs[0]+"\t" +vsRef.join(" ")+"\n");
		}
		FFile.close(br);
		FFile.close(bw);

	}
	
	private static void filterPMCRefPairtoSGD() {
		System.out.println("filterPMCRefPairtoSGD()");
		
		VectorI vi= new VectorI();
		vi.loadSet("pmid_sgd.txt",100000);
		
		BufferedReader br = FFile.newReader("PMC.ref.pair.txt");
		BufferedWriter bw = FFile.newWriter("PMC.ref.pair.SGD.txt");
		//FFile.readLine(br);
		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split("\t");
			int pmid = Integer.parseInt(vs[1]);
			if (vi.getE(pmid)==0)
				continue;
			FFile.write(bw, vs[0]+"\t" +vs[1]+"\n");
		}
		FFile.close(br);
		FFile.close(bw);

	}
	
	/**
	 * IN: a file with each row = pmid,pmids
	 * OUT: a .db file with each row = Paper(pmid,pmids)
	 * @param fn
	 */
	public static void citation2praFile(String fn, String fnOut) {
		System.out.println("citation2praFile()");
		BufferedReader br = FFile.newReader(fn);

		BufferedWriter bw = FFile.newWriter(fnOut);//citations.db

		SetS ms= new SetS();

		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = FString.split(line,"\t");
			if (vs[1].length() ==0) continue;
			FFile.write(bw, "Cites("+vs[0]+"," +vs[1]+")\n");
			ms.add(vs[0]);
			ms.addAll(vs[1].split(" "));
		}
		FFile.close(br);
		FFile.close(bw);
		ms.save("pmid.cited");

	}
	/**
	 * IN: a file with each row = pmid,pmids
	 * OUT: a .db file with each row = Paper(pmid,pmids)
	 * @param fn
	 *  intersection with id's of a database
	 */
	public static void citation2praFile(String fn, String fnOut, String fnFilter) {
		System.out.println("citation2praFile()");
	
		BufferedReader br = FFile.newReader(fn);
		BufferedWriter bw = FFile.newWriter(fnOut);//citations.db
		SetS mSGD= SetS.fromFile(fnFilter);

		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = FString.split(line,"\t");
			if (vs[1].length() ==0) continue;
			VectorS vPMID= FString.splitVS(vs[1]," ");
			
			VectorS v=vPMID.intersect(mSGD);
			if (v.size()==0) continue;
			
			FFile.write(bw, "Cites("+vs[0]+"," +v.join(" ")+")\n");
		}
		FFile.close(br);
		FFile.close(bw);

	}

	

	
	//static String fdAbstract = "/usr0/home/nlao/yeast/data/sgd_pmc/abstracts/";
	static String fdAbstract = "/usb3/nlao/yeast/data/sgd_pmc/abstracts/";
	public static void main(String args[]) {
		
		//dumpPMCRef();
		
		//parsePMCRef();
		// 200910:		18192 papers, 748590 references parsed
		//(new YeastData()).run();
		
		//filterPMCReftoSGD();
		//filterPMCRefPairtoSGD();
		
/*		
 * wget ftp://ftp.ncbi.nlm.nih.gov/pub/pmc/PMC-ids.csv.gz
gunzip PMC-ids.csv.gz


 * */ 

		//perl ../../crawl.pmc.pl pmid.pmc
		//perl ../../extract.pmc.pl pmid.pmc.crawl

		//perl ../../crawl.pm.pl pmid
		//perl ../../extract.pl pmid.crawl

		
		//PMC.citation2praFile("pmid.pmc.sgd.crawl.ex","RefSGD.db","pmid.sgd");		
				//FFile.saveString(PMAbsInfor.getTitle(),"abs.title" );
		
		//(new PMExtractor()).processOutputs("pmid.sgd.crawl");
			//perl ../../extract.pm.pl pmid.sgd.crawl
		String fn="pmid.sgd.crawl.ex";
		//FFile.countNonempltyCells(fn);
		//statAffiliation(fn);
		
		//stat(fn);
		
		//normalizeFields(fn);	//|mGene|=50071,  Sequence parse failure =848/50068
		//generateSenarios(fn+".norm", "scenarios");	//17146 //17361 scenarios
		
		
		
		//addAbstractInfor("Abstract.db");
	}
	
}
