package edu.cmu.pra.data;

import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.system.FSystem;

public class PM {


	static int nParsed=0;
	static int nContact=0;
	//19824122?dopt=Abstract
	public static void rename() {
		System.out.println("rename()");
		
		Pattern paFile = Pattern.compile("(\\d*)\\?dopt=Abstract");
		for (String fn : FFile.getFileNames(fdAbs)) {
			Matcher maRef = paFile.matcher(fn);
			if  (!maRef.find()) {
				//System.out.println("bad file name="+fn);
				continue;
			}
			String fn1= maRef.group(1);
			FFile.renameFile(fdAbs+fn, fdAbs+fn1);
		}

	}
	static SetS mAuthor= new SetS();
	static SetS mJournal= new SetS();
	static SetS mGene= new SetS();

	public static void toPMAbsGhirl() {
		PMAbsInfor ae= new PMAbsInfor();
		
		System.out.println("toGhirl()");
		BufferedWriter bw = FFile.newWriter("PM.abstract.ghirl");
		MapSI mEmail= new MapSI();
		
		VectorI vi= new VectorI();
		vi.loadSet("pmid_sgd.txt",100000);
		//AbstractExtractor ae= new AbstractExtractor();
		
		for (String line: FFile.enuLines("PM.abstract.txt")) {
			++nParsed;
			if (nParsed %100==0){
				System.out.print("t");
				if (nParsed %5000==0){
					System.gc();
					System.out.print("\nnParsed="+nParsed+" "+FSystem.printMemoryUsageRate());
				}
			}
			
			ae.parseLine(line);
			int id = Integer.parseInt(ae.pmid);
			if (vi.get(id)==0)
				continue;
			
			mJournal.add(ae.journal);
			if (ae.email.length()>0)
				//vEmail.add(ae.email);
				mEmail.plusOn(ae.email.replaceAll("\\.$",""));
			for (String a: ae.vAuthors){
				String b= a.replaceAll("_"," ");
				FFile.write(bw, "node TEXT$%s %s\n",a,b);			
				FFile.write(bw, "edge hasKeywords %s TEXT$%s\n",a,a);
				FFile.write(bw, "edge paperIsWrittenByAuthor %s %s\n",ae.pmid,a);
				mAuthor.add(a);
			}
		}
		FFile.close(bw);
		mAuthor.save("authors.txt");
		mJournal.save("journals.txt");
		//vEmail.save("emails.txt");
		mEmail.save("mEmails.txt");
		return;
	}
	public static void _mergePMAbstracts() {
		System.out.println("mergePMAbstracts()");
		

		BufferedWriter bw = FFile.newWriter("crawl");
		BufferedWriter bwED = FFile.newWriter("crawled");
		for (String pmid : FFile.getFileNames(fdAbs)) {
			
			int id = Integer.parseInt(pmid);
			++nParsed;
			FFile.write(bw	,"\nURL=[%s]\n",pmid);
			String txt =FFile.loadString(fdAbs+pmid);
			FFile.write(bw, txt);
			FFile.writeln(bwED, pmid);
		}
		System.out.println( "#parsed="+nParsed);
		FFile.close(bw);
		FFile.close(bwED);
	}
	
	public static String getAbsURL(String pmid){
		return String.format("http://www.ncbi.nlm.nih.gov/pubmed/%s?dopt=Abstract"		,pmid);
	}
	
	

	//Parse Failure =848/50068
	public static void normalizeFields(String fn) {
		System.out.println("normalizeFields()");
		
		SetS mAff=new SetS(FFile.enuACol("aff.signif", 1));
			//SetS.fromFile("aff.signif", 1);
		
		BufferedWriter bw= FFile.newWriter(fn+".norm");
		for (PMAbsInfor abs:  PMAbsInfor.reader(fn)){
			abs.normalize(mAff);
			FFile.writeln(bw, abs.print());
		}
		FFile.close(bw);
		
	}
	

	protected static void statAffiliation(String fn){
		FTable.subColumn(fn, 4, "affiliation");
		
		BufferedWriter bw= FFile.newWriter("aff");
		
		for (String s: FFile.enuLines("affiliation")){
			String a= PMAbsInfor.extractNormalizeAff(s);
			FFile.writeln(bw, a);
		}
		FFile.close(bw);
		PMAbsInfor.vAcnym.save("aff.acnym");
		PMAbsInfor.vFailed.save("aff.failed");
		PMAbsInfor.vLocation.save("aff.loc");
		
		
		return;
	}
	protected static void stat(String fn){
		//FTable.countCellsFreq("aff");
		//FTable.countCellsFreq("aff.fq0");
		//FTable.filterByFreq("aff.fq0", 0,3,"aff.signif");

		//FTable.subColumn(fn, 6, "chemistry");
		//FTable.splitItems("chemistry","chem");
		//FTable.countCellsFreq("chem");
		
		//FTable.mergeColumns(fn, VectorI.seq(7,4), "heading");
		//FTable.countCellsFreq("heading");
		
		//FTable.mergeColumn(fn, 6, "chemistry");

	}
	
	
	// pmid0, year1, journal2, author3, title4
	//0	1	2	3	4	5	6	7	8	9	10	11	12
	//pmid	year	journal	email	affiliation	Authors	Chemicals	MHDescriptorM	MHQualifierM	MHDescriptor	MHQualifier	title	abstract

	public static void addAbstractInfor(String fn,String fnOut) {
		System.out.println("addAbstractInfor()");
		
		BufferedWriter bw= FFile.newWriter(fnOut);//fn+".db");
		
		for (PMAbsInfor abs:  PMAbsInfor.reader(fn)){
			abs.normalize(null);
			abs.write2PRA(bw);
		}
		FFile.close(bw);
		
		PMAbsInfor.writePRASchema(FFile.newWriter("abs.sch"));
	}

	public static String fdPM="../../PubMed/";
	protected static void pmid(){
		FTable.subColumn(fdPM+"PMC-ids.csv",9, "pmc",",");
		FTable.filterByColumn("pmc", "pmid.pmc", 0	,SetS.fromFile("pmid"));

	}


	
	//48641
	//static String fdAbstract ="/usb3/aarnold_backup/yeast/sgd/fetched_pubmed_abstract_html";
	static String fdAbs ="/usr0/nlao/code_java/ni/run/yeast/tmp/abstracts/";
		//= "/usb3/nlao/yeast/data/pm/abstracts/";

	public static void main(String args[]) {
		
		//rename();
		//parsePMAbstracts();
		//toPMAbsGhirl();
		
		//mergePMAbstracts();
		
		
		//addAbstractInfor("pmid.crawl.ex", "../data/papers.db");


	}
}
