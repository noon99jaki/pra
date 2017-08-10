package edu.cmu.lti.util.crawl;

import edu.cmu.lti.util.crawl.tripadvisor.TACrawler;

public class CarSurvey extends Crawler{
	public CarSurvey(){
		super(Crawler.class);
		
		p.rootURL="http://www.carsurvey.org/";
		p.nThread=5;
	}
	
	public static void main(String args[]) {
		TACrawler crawler = new TACrawler();
		crawler.start();
	}
}
