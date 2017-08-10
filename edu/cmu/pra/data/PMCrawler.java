package edu.cmu.pra.data;

import edu.cmu.lti.util.crawl.ASimpleCrawler;
import edu.cmu.lti.util.system.FSystem;

/**
 * Example page:
 * http://www.ncbi.nlm.nih.gov/pubmed/795806?dopt=Abstract
 * http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?tool=querendipity&email=krivard@cs.cmu.edu&db=pubmed&id=795806&retmode=xml
 * @author nlao
 *
 */
//
public class PMCrawler extends ASimpleCrawler{
	public PMCrawler(){
		super(PMCrawler.class);
	}
	String email= "nlao@cs.cmu.edu";
	public String getURL(String id){
		return String.format(
			"http://eutils.ncbi.nlm.nih.gov/entrez/eutils/efetch.fcgi?email=%s&db=pubmed&id=%s&retmode=xml"
				//tool=querendipity&
				,email	,id);
//		"http://www.ncbi.nlm.nih.gov/pubmed/%d?dopt=Abstract"	,id);
	}
	
	
	/** 
	 * INPUT FILES:
	 *   pmids.pm: text file with one PMID per line for a total of N lines.
	 * 
	 * OUTPUT FILES:
	 *   pmids.pm.crawled: text file with one PMID per line (showing 
	 *   downloaded papers) for a total of M<=N lines (skips PMIDs it 
	 *   finds in a pre-existing .crawled file)
	 *   
	 *   pmids.pm.crawl: text file of P<=M entries of the following form:
	 *    	url of the paper
	 *      html of the url
	 *      
	 * Log messages go to stdout.
	 * 
	 * Parameters are set by the file	./conf 
	 * it has format similar to the following  
	 * nThread=10
		 nReportInterval=50
		 # this is a comments
		 nSleepMSec=100
	 * (more detail in edu.cmu.lti.util.run.Param)	  
	 */
	public static void main(String args[]) {
		//intersectWithPMC("pmids");
		if (args.length!=1)
			FSystem.die("need pmid file");
				
		Param.overwriteFrom("conf");
		
		PMCrawler crawler = new PMCrawler();
		crawler.start(args[0]);//"pmids.pmc");
	}
	
}

