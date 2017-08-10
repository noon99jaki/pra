package edu.cmu.lti.util.crawl;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.system.ThreadPool;
import edu.cmu.lti.util.text.FPattern;

public class Crawler extends ThreadPool{
	//protected PrefixTree ptQueuedURLs= new PrefixTree();
	protected SetS mQueuedURLs= new SetS();
	//protected SetS mParsedURLs= new SetS();
	//protected HSetS mQueuedURLs= new HSetS();
	//protected HSetS mParsedURLs= new HSetS();

	protected String getFileName(String url){
		if (p.rootURL.equals(url))	return "default.html";
		//String fn= url.substring(p.rootURL.length());
		
		String fn= url.replaceFirst("^http://","")
			.replaceFirst("^/", "");
		if (fn.endsWith("/") || fn.length()==0)
			fn =fn+"default.html";
		return fn;
	}
	protected boolean isValidURL(String url){
		return true;//url.startsWith(p.rootURL);
	}
	public void workOnJob(WorkThread t, Job job){
		String html=null;
		if (p.bSingleFile){
			if (job.key_.equals(p.rootURL))
				html=ASimpleCrawler.download(job.key_);
			else
				html=ASimpleCrawler.download(p.rootURL+job.key_);
			synchronized(bwOutput){
				FFile.write(bwOutput, "\nURL=["+job.key_+"]\n");
				FFile.write(bwOutput, html);
				FFile.flush(bwOutput);		
			}
		}
		else{
			String fn= "crawl/"+getFileName(job.key_);
			if (fn==null){
				System.err.print("weird url="+job.key_);
				FFile.write(bwErr, job.key_+"\n");
				FFile.flush(bwErr);			
			}		
			
			if (!FFile.exist(fn)){
				
				if (job.key_.equals(p.rootURL))
					ASimpleCrawler.download(job.key_,fn);
				else
					ASimpleCrawler.download(p.rootURL+job.key_,fn);
			}
			html= FFile.loadString(fn);
		}
		
		//"http://www.carsurvey.org"
		VectorS vs = FPattern.matchAll(html, "href=\"([^\"]+)\"");
		for (String url: vs){			
			if (!isValidURL(url))
				continue;				
			postJob(url);
		}

		//++nDownloaded;
		//if (nDownloaded % p.nGCInterval==0)
		for (int i=0;i<vs.size(); ++i)
			vs.set(i,null);
		vs.clear();		vs=null;
		html=null;
		
		synchronized(nParsed){
			//mParsedURLs.add(job.key);		
			++nParsed;
			if (nParsed % p.nReportInterval==0){
				System.out.print(String.format(
					"\nQueued=%d,Parsed=%d,%s"
						,mQueuedURLs.size(), 	nParsed
						,FSystem.printMemoryUsageRate()));
			}
			if (nParsed % p.nGCInterval==0){
				System.gc();
				if (FSystem.memoryRate()>0.8)
					FSystem.die("low on memeory");
			}
			FFile.write(bwParsed, job.key_+"\n");
			FFile.flush(bwParsed);

		}

	}
	
	//public int nQueued=0;
	//public int nDownloaded=0;
	public Integer nParsed=0;
	
	protected void postJob(String url){//Job j){//
    //allow some collisions here for parallelization?
		//not working
  	//if (mQueuedURLs.contains(url)) return;
  	//mQueuedURLs.add(url);

  	if (mQueuedURLs.contains(url)) return;

		synchronized(queue_) {
	   	mQueuedURLs.add(url);
	  	
	  	FFile.write(bwQueued,url+"\n");
			FFile.flush(bwQueued);
     	postJobCore(url); 

    	queue_.notify();
		}
	}
	protected void postJobCore(String url){//Job j){//
		//mQueuedURLs.size()
  	Job j=new Job(-1,url);//nQueued
  	//prefer depth first search to reduce queue size
  	//queue.addFirst(j);	    	//queue.addLast(j);
  	queue_.add(j);
  	//++nQueued;
	}
	
	public static String getBaseURL(String url){
		if (!url.startsWith("http://"))
			url = "http://"+ url;
		String base= url.replaceAll("[^/]+$","");
		if (base.equals("http://"))
			return url;
		return base; 
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
		//public boolean bWithInDomain=true;

		public Param(Class c) {
			super(c);
			nThread = getInt("nThread", 10);
			nReportInterval = getInt("nReportInterval", 20);
			nGCInterval= getInt("nGCInterval", 200);
			bSingleFile = getBoolean("bSingleFile", false);
		}
	}
	public Param p = null;// new Param();
	public Crawler(Class c) {
		p = new Param(c);
	}
	public BufferedWriter bwErr;
	public BufferedWriter bwQueued;
	public BufferedWriter bwParsed;
	public BufferedWriter bwOutput;

	private void loadOldJobs(){
		System.out.println("load old jobs");
		SetS mParsedURLs= SetS.fromFile("parsed");
		nParsed=mParsedURLs.size();
		
		VectorS vQ= FFile.loadLines("queued");
		mQueuedURLs.addAll(vQ);
		//mQueuedURLs.load("queued");
		
		System.out.println(FSystem.printMemoryUsageRate());
		
		System.out.println("send old jobs");
		for (int i=vQ.size()-1;i>=0; --i){// prefer reversed order?
			String url= vQ.get(i);
		//for (String url: this.mQueuedURLs){
			if (mParsedURLs.contains(url))
				continue;
			this.postJobCore(url);
		}
		mParsedURLs.clear();
		System.gc();
		System.out.println(FSystem.printMemoryUsageRate());
	}
	
	public void start(){
		loadOldJobs();
		bwQueued= FFile.newWriterCA("queued");
		bwParsed= FFile.newWriterCA("parsed");
		bwErr= FFile.newWriterCA("error");
		if (p.bSingleFile)
			bwOutput= FFile.newWriterCA("output");
		
		startThreads(p.nThread);
		if (queue_.size()==0)
			postJob(p.rootURL);
		//else	queue.notify();

		waitJobs();
		killThreads();
		
		FFile.close(bwQueued);
		FFile.close(bwParsed);
		FFile.close(bwErr);
		if (p.bSingleFile)
			FFile.close(bwOutput);
	}
}
