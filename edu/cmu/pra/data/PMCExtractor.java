package edu.cmu.pra.data;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.crawl.ASimpleExtractor;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FPattern;

public class PMCExtractor extends ASimpleExtractor{
	public PMCExtractor(){
		super(PMCExtractor.class);
	}
	
	static int nRef=0;
	static int nPaper=0;
	public boolean processADoc(String pmid,	String html)	{
		//String pmid=url;
		//FFile.saveString(html, pmid+".html");
		VectorS vs=FPattern.matchAll(html,	"PubMed PMID: (\\d+)");
			//"div class=\"rprt\" id=\"pmid_(\\d+)");
		++nPaper;
		nRef+=vs.size();
		FFile.write(bwOutput, pmid+"\t"+vs.join(" ")+"\n");
		return true;
	}
	
	/** 
	 * INPUT FILES:
	 *   pmids.pmc.crawl: text file of P<=M entries of the following form:
	 *    	url of the paper
	 *      html of the url
	 * 
	 * OUTPUT FILES:
	 *   pmids.pmc.crawl.ex: text file of paper references
	 *    	each line is a paper and the set of papers it cites
	 *    	e.g.  pmid\tpmid pmid pmid...
	 *      
	 * Log messages go to stdout.
	 */
	public static void main(String args[]) {
		if (args.length!=1)
			FSystem.die("need crawl file");

		
		PMCExtractor extractor = new PMCExtractor();
		extractor.processOutputs(args[0]);//"pmids.pmc.crawl");

		//39036 papers made 1,227,258references
		System.out.println(nPaper +" papers made " +nRef +"references");
	}
}
