package edu.cmu.pra.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.text.AcronymGenerator;
import edu.cmu.lti.util.text.FString;
import edu.cmu.lti.util.text.VectorPattern;

public class SGD {
	
	public static void dumpSGDAbstracts() {
		System.out.println("dumpSGDAbstracts()");
		Pattern paFile = Pattern.compile("reference.pl\\?pmid=(\\d*)");
		String fdAbstract = "/usb3/aarnold_backup/yeast/sgd/sgd_abstract_html/";
		BufferedWriter bw = FFile.newWriter("SGD.abstract.lines");
		for (String file : FFile.getFileNames(fdAbstract)) {
			Matcher maFile = paFile.matcher(file);
			if (!maFile.matches()) {
				System.out.println("bad file name: " + file);
				continue;
			}
			String pmid = maFile.group(1);
			BufferedReader br = FFile.newReader(fdAbstract + file);
			String line = null;
			while ((line = FFile.readLine(br)) != null) {
				if (!line.startsWith("<span class='b'>")) continue;
				FFile.write(bw, pmid + "\t" + line + "\n");
				break;
			}
			FFile.close(br);
		}
		FFile.close(bw);
	}
	

	
	// <span class='b'>Watzele M, Klis F, Tanner
	// W&nbsp;(1988)</span>
	// Purification and characterization of the inducible a
	// agglutinin of Saccharomyces cerevisiae.
	// <span class='i'>EMBO J</span> 7(5):1483-8
	// private static void parseSGDAbstracts(String
	// fn,BufferedWriter bw){ }
	/*
	 * static VectorPattern vPaLine =new VectorPattern( new
	 * String []{
	 * "<span class='b'>.*</span> (.*) <span class='i'>(.*)</span>.*"
	 * ,"<span class='b'>.*</span> (.*)\\. ([A-Z].*) \\d.*"
	 * ,"<span class='b'>.*</span> (.*)\\? ([A-Z].*) \\d.*"
	 * ,"<span class='b'>.*</span> \\[(.*)\\] (.*) \\d.*" });
	 */
	static VectorPattern vPaLine = new VectorPattern(	new String[] { 
					"(.*)\\t<span class='b'>(.*)&nbsp;\\((\\d*)\\)</span> (.*)" 
			});
	static VectorPattern vPaTitle = new VectorPattern(new String[] {
			"\\[(.*)\\] ([A-Z<].*) [\\d|([A-Z]].*"
			, "(.*)<span class='i'>(.*)</span>.*"
			,"(.*)\\. ([A-Z].*) [\\d|([A-Z]].*"
			, "(.*)\\? ([A-Z].*) [\\d|([A-Z]].*" });

	public static void parseSGDAbstracts() {
		System.out.println("parseSGDAbstracts()");
		BufferedReader br = FFile.newReader("SGD.abstract.lines");
		
		BufferedWriter bw = FFile.newWriter("SGD.abstract.txt");
		BufferedWriter bwErr = FFile.newWriter("SGD.abstract.err");
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			Matcher maLine = vPaLine.match(line);
			if (maLine == null) {
				System.out.print(".");
				System.out.println("bad line: " + line);
				FFile.write(bwErr, line + "\n");
				continue;
			}
			String pmid = maLine.group(1);
			
			String author = maLine.group(2).trim();
			author = author.replaceAll(", ", ",").replaceAll(" ", "_");
			author = author.replace(",<span_class='i'>et_al.</span>", "");
			author = author.replaceAll(",", " ");
		
			String year = maLine.group(3);
			
			String title = maLine.group(4);
			Matcher maTitle = vPaTitle.match(title);
			if (maTitle == null) {
				System.out.print(".");
				System.out.println("bad title: " + title);
				FFile.write(bwErr, title + "\n");
				continue;
			}
			title = maTitle.group(1).trim();
			title = title.replaceAll("<span class='i'>|</span>", "");
			
			String journal = maTitle.group(2);		// if (journal.indexOf(""))
			journal = journal.trim().replaceAll(" ", "_");
			journal = journal.replaceAll("<span_class='i'>|</span>", "");
			
			
			FFile.write(bw, pmid + "\t" + year + "\t" + journal + "\t" + author
					+ "\t" + title + "\n");
		}
		FFile.close(br);
		FFile.close(bw);
		FFile.close(bwErr);
	}


	public static void parseGeneCitation() {//FromFile
		System.out.println("parseGeneCitation()");
		BufferedReader br = FFile.newReader("gene_literature.tab");

		BufferedWriter bwC= FFile.newWriter("GeneCitation.txt");
		BufferedWriter bwA= FFile.newWriter("GeneAspect.txt");
		AcronymGenerator ag = new AcronymGenerator();
		
		String pmidLast=null;
		//StringBuffer sb = new StringBuffer();
		String pmid =null;
		VectorS vsGene= new VectorS();
		
		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split("\t");			
			pmid = vs[0];
			String gene= vs[2];
			if (pmid.length()==0 || gene.length()==0)
				continue;
			VectorS vsCode=ag.getCode(vs[4].split("\\|"));
			//addTitleJournal(Integer.parseInt(vs[0]), vs[2], vs[4]);
			FFile.write(bwA, String.format(	"%s\t%s\t%s\n"
				,pmid, gene, vsCode.join(" ")));
			
			vsGene.add(gene);
			//sb.append(gene);
			if (pmidLast!=null){
				if (!pmid.equals(pmidLast)){
					FFile.write(bwC, String.format(
						"%s\t%s\n"
						,pmid, vsGene.join(" ")));					
					vsGene.clear();//sb.setLength(0);
				}					
			}
			pmidLast=pmid;
		}
		
		FFile.write(bwC, String.format(
				"%s\t%s\n"
				,pmid, vsGene.join(" ")));					
		
		ag.mTopicCode.save("mTopicCode.txt");
		ag.mCodeTopic.save("mCodeTopic.txt");
		FFile.close(br);
		FFile.close(bwA);
		FFile.close(bwC);
	}
	
	protected static void getPaperIDsFromFile() {//FromFile
		SetI mGene= new SetI();
		System.out.println("getPaperIDsFromFile()");
		
		BufferedReader br = FFile.newReader("gene_literature.tab");
		//StringBuffer sb = new StringBuffer();
		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split("\t");			
			String pmid = vs[0]; 
			
			String citation= vs[1];
			if (pmid.length()==0 )
				continue;
			
			int id= Integer.parseInt(pmid);
			if (!mGene.contains(id)){
					mGene.add(id);
			}
			
		}
		FFile.close(br);
		mGene.save("pmid_sgd.txt");
	}

	protected static void intersectWithPMC() {//FromFile
		System.out.println("intersectWithPMC()");
		VectorI vi= new VectorI();
		vi.loadSet("pmid_sgd.txt",100000);

		BufferedReader br = FFile.newReader("PMC-ids.csv");
		BufferedWriter bw = FFile.newWriter("pmid_sgd_pmc.txt");
		

		
		FFile.readLine(br);
		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split(",");
			String pmid = vs[9];
			//String pmcid= vs[8];
			//if (pmid.length()<3 || pmcid.length()<3)		continue;
			if (pmid.indexOf('P')>=0)			continue;
			int id=-1;
			try{
				id= Integer.parseInt(pmid);
			}
			catch (Exception e){
				System.err.println("bad line="+line);
				continue;
			}
			if (vi.getE(id)==0)
				continue;
			FFile.write(bw, pmid+"\n");
		}
		FFile.close(br);
		FFile.close(bw);
	}
	protected void addGeneAssociations() {
		System.out.println("addGeneAssociations()");

		BufferedWriter bw= FFile.newWriter("GeneAssoc.db");

		for (String line: FFile.enuLines("associations_per_gene.txt")){
			String[] vs = line.split(",");
			String gene=vs[0];
			String genes=FString.join(vs," ", 1);
			FFile.write(bw, "RelateTo(" + gene + "," + genes + ")\n");
		}
		FFile.close(bw);
	}
	
	//not a very good idea
/*	protected void addGeneAssociations1() {

		new PipeSS() {
			public String transform(String line){
				String[] vs = line.split(",");
				String gene=vs[0];
				String genes=FString.join(vs," ", 1);
				return "RelateTo(" + gene + "," + genes + ")";
			}
		};
	}
*/
	public static void main(String args[]) {
		

		intersectWithPMC();
		
		//(new YeastData()).run();
	}
}
