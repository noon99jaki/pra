package edu.cmu.lti.util.crawl;

import edu.cmu.lti.util.system.ThreadPool;
import edu.cmu.lti.util.system.ThreadPool.Job;

/**
 * multi-threaded html extractor
 * @author nlao
 *
 */
public abstract class Extractor extends ASimpleExtractor{// extends ThreadPool{
		public static ThreadPool tp= new ThreadPool();

		public Extractor(Class c){
			super(c);
		}
		public abstract class HtmlExtractor {
		 	public abstract void processAHtml(String url,	String html);
		}
		
		public class WorkThread extends ThreadPool.WorkThread{
		  public WorkThread(int id, HtmlExtractor extractor){
		  	tp.super(id);
		  	this.extractor= extractor;
		  }
			public HtmlExtractor extractor=null;		
		}
		
		protected void workOnJob(ThreadPool.WorkThread t, Job job){
			WorkThread th= (WorkThread)t;
			th.extractor.processAHtml(job.key_,(String) job.data_);
		}
		
		public static Integer nParsed=0;
		
		//int nParse=0;
		public boolean processADoc(String url,	String html){
				//BufferedReader br){//String html){
			tp.waitQueue(p.nThread*2);	//overload the threads
			tp.addJob(tp.new Job(-1, url, html));
			return true;
		}
		public void processOutputs(){
			tp.startThreads(p.nThread);
			super.processOutputs();
			tp.killThreads();
			return ;
		}
		
}
