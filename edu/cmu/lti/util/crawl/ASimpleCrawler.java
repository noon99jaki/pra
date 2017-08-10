package edu.cmu.lti.util.crawl;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;


/**
 * single thread crawler
 * @author nlao
 *
 */
public abstract class ASimpleCrawler {
	public static String download(String url){
		return downloadByJava(url);
		//return downloadByWget(url);
	}
	public static void download(String url, String fn){

		downloadByJava(url,fn);
		//downloadByWget(url,fn);
	}
	public static String preprocessURL(String url){
		//url = url.replaceAll("\\&","\\\\&");
		//url = "\""+url+"\"";
		//url = EscapeChars.forURL(url);		
		//url = EscapeChars.forHrefAmpersand(url);
		//url = url.replaceAll("%","%25");
		url = url.replaceAll(" ","%20");
		return url;
	}
	public static void downloadByWget(String url, String fn){
		FSystem.cmd("wget --quiet --tries=1 --output-document="
				+fn+" "+preprocessURL(url)+"" ); 
	}
	public static String downloadByWget(String url){
		return FSystem.cmd("wget --quiet --tries=1 -O- "
				+preprocessURL(url)+"" );
	}
	public static void downloadByJava(String url, String fn){
		FFile.saveString(fn, downloadByJava(url));
	}

	public static String downloadByJava(String url){
		System.out.print(".");
		
		StringBuffer sb=new StringBuffer();
		InputStream is=null ;
		URL con=null;
		try {
			con= new URL(preprocessURL(url));
			is = con.openStream();  // throws an IOException
			DataInputStream dis = new DataInputStream(new BufferedInputStream(is));
			String line;
	    while ((line = dis.readLine()) != null) {
	    	sb.append(line).append("\n");
	    	line=null;
	    }	    
		} catch (Exception e) {
			e.printStackTrace();
		  return null;
		} 
		finally{
			if (is!=null){
				try {	is.close(); 	}	catch (IOException ioe) {	} 
			}
			is=null;
		}		
    con=null;
		return sb.toString();
	}
	
	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		//public String name;
		public String rootURL;
		
		//public String domain;
		//public int maxLevel=5;
		
		public int nThread=5;
		public int nReportInterval=20;
		public int nGCInterval=100;
		public boolean bSingleFile=false;
		public int nSleepMSec=100;
		//public boolean bWithInDomain=true;

		public Param(Class c) {
			super(c);
			nThread = getInt("nThread", 10);
			nReportInterval = getInt("nReportInterval", 50);
			nGCInterval= getInt("nGCInterval", 200);
			bSingleFile = getBoolean("bSingleFile", false);
			nSleepMSec = getInt("nSleepMSec", 100);
		}
	}
	public Param p = null;// new Param();
	public ASimpleCrawler(Class c) {
		p = new Param(c);
	}
	
	//public BufferedWriter bwQueued;
	public BufferedWriter bwParsed;
	public BufferedWriter bwOutput;
	public BufferedWriter bwErr;

	int nParsed=0;

	/**
	 * Crawl a set of documents by ID.  The URL to crawl for 
	 * each document is given by getURL(id). If fnQueue.crawled 
	 * exists, it will skip the documents listed in the .crawled file.
	 * 
	 * Creates fnQueue.crawled (listing crawled ids) and 
	 * (optionally) fnQueue.crawl (listing the URL and HTML 
	 * fetched for each document ID)
	 * 
	 * @param fnQueue Input file containing one document ID per line
	 */
	public void start(String fnQueue){
		System.out.println("load old jobs");
		SetS mParsedURLs= SetS.fromFile(fnQueue+".crawled");
		nParsed=mParsedURLs.size();

		bwOutput= FFile.newWriterCA(fnQueue+".crawl");
		bwParsed= FFile.newWriterCA(fnQueue+".crawled");
		
		
		for (String line: FFile.enuLines(fnQueue)){
			if (mParsedURLs.contains(line))
				continue;
			String url= getURL(line);
			//download(url, line);
			String html=download(url);
			
			FFile.writeln(bwParsed, line);
			FFile.flush(bwParsed);

			if (html==null) continue;

			FFile.write(bwOutput, "\nURL=["+line+"]\n");
			FFile.writeln(bwOutput, html);
			FFile.flush(bwOutput);
			++nParsed;
			if (nParsed % p.nReportInterval==0)
				System.out.print("\nParsed="+nParsed);//,FSystem.memoryUsageRate()));			
			FSystem.sleep(p.nSleepMSec);
		}
		
		//mParsedURLs.clear();
		FFile.close(bwOutput);
		FFile.close(bwParsed);
		return;
	}
	abstract public String getURL(String id);
	
	
	//private String getURL(String id){	return id;	}
	
	/*public static void main(String args[]) {
		
		SimpleCrawler crawler = new SimpleCrawler();
		crawler.start();
		
	}*/
}
