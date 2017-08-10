package edu.cmu.pra.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.util.crawl.ASimpleCrawler;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

/*
 * Example page: 
 * http://www.ncbi.nlm.nih.gov/sites/entrez?Db=pubmed&Cmd=Link&LinkName=pubmed_pubmed_refs&LinkReadableName=Cited%20Articles&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.Presentation=uilist&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.Format=text&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.PageSize=200&ordinalpos=1&IdsFromResult=11134527
 * 
 * "High-Volume Retrievals
If you are using a script that makes more than 100 requests of any
kind, please run it outside of the PMC system's peak hours. Do not
make more than one request every 3 seconds, even at off-peak times.
Peak hours are Monday to Friday, 5:00 AM to 9:00 PM, U.S. Eastern
time. "
 */
public class PMCCrawler extends ASimpleCrawler{
	public PMCCrawler(){
		super(PMCCrawler.class);
	}
	
	//static String baseURL="http://www.ncbi.nlm.nih.gov/sites/entrez?Db=pubmed&Cmd=Link&LinkName=pubmed_pubmed_refs&LinkReadableName=Cited Articles&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.Presentation=uilist&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.Format=text&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.PageSize=200&ordinalpos=1&IdsFromResult=11134527
	static String baseURL="http://www.ncbi.nlm.nih.gov/sites/entrez?";
	static {
		baseURL+="Db=pubmed&Cmd=Link&LinkName=pubmed_pubmed_refs&LinkReadableName=Cited Articles";
		baseURL+="&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.Presentation=uilist";
		baseURL+="&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.Format=text";
		baseURL+="&EntrezSystem2.PEntrez.Pubmed.Pubmed_ResultsPanel.Pubmed_DisplayBar.PageSize=200";
		baseURL+="&ordinalpos=1&IdsFromResult=";//11134527
	}
	
	public String getURL(String id){
		return baseURL+id;//+"\"";
	}
	
	protected static void intersectWithPMC(String fn) {//FromFile
		System.out.println("intersectWithPMC()");
		SetS ms=SetS.fromFile(fn);

		BufferedReader br = FFile.newReader("PMC-ids.csv");
		BufferedWriter bw = FFile.newWriter(fn+".pmc");
		
		FFile.readLine(br);
		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split(",");
			String pmid = vs[9];
			String pmcid= vs[8];
			//if (pmid.length()<3 || pmcid.length()<3)		continue;
			if (pmid.indexOf('P')>=0)			continue;
			if (!ms.contains(pmid))
				continue;
			FFile.write(bw, pmid+"\n");
		}
		FFile.close(br);
		FFile.close(bw);
	}
	
	/** 
	 * INPUT FILES:
	 *   pmids.pmc: text file with one PMID per line for a total of N lines.
	 * 
	 * OUTPUT FILES:
	 *   pmids.pmc.crawled: text file with one PMID per line (showing 
	 *   downloaded papers) for a total of M<=N lines (skips PMIDs it 
	 *   finds in a pre-existing .crawled file)
	 *   pmids.pmc.crawl: text file of P<=M entries of the following form:
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
		
		PMCCrawler crawler = new PMCCrawler();
		crawler.start(args[0]);//"pmids.pmc");
	}
	
}
