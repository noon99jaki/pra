package edu.cmu.lti.util.crawl.tripadvisor;

import java.util.regex.Pattern;

import edu.cmu.lti.util.crawl.Crawler;
import edu.cmu.lti.util.text.VectorPattern;


public class TACrawler extends Crawler{

	/*
	 * http://www.tripadvisor.com
	 * /Tourism-g4-Europe-Vacations.html
	 * /Tourism-g187768-Italy-Vacations.html
	 * /Tourism-g187791-Rome_Lazio-Vacations.html
	 * /Tourism-g187789-Lazio-Vacations.html
	 * /Hotels-g187791-Rome_Lazio-Hotels.html
	 * /Hotel_Review-g187791-d191099-Reviews-or10-Albergo_del_Senato-Rome_Lazio.html#REVIEWS
	 * /ShowUserReviews-g187791-d191099-r43497707-Albergo_del_Senato-Rome_Lazio.html#REVIEWS
	 * 
	 * 
	 * /Hotels-g29220-Maui_Hawaii-Hotels.html
	 * /Hotel_Review-g187791-d191099-Reviews-Albergo_del_Senato-Rome_Lazio.html
	 * /Hotel_Review-g187791-d615017-Reviews-Welrome_Hotel-Rome_Lazio.html
	 * 
	 * 
	 * /Hotel_Review-g187791-d191099-Reviews-or10-Albergo_del_Senato-Rome_Lazio.html#REVIEWS
	 * /ShowUserReviews-g187791-d191099-r45452170-Albergo_del_Senato-Rome_Lazio.html
	 * 
	 * /Tourism-g187870-Venice_Veneto-Vacations.html
	 * /Hotels-g187870-Venice_Veneto-Hotels.html
	 * 
	 * /Hotels-g187768-Italy-Hotels.html
	 * /Hotels-g187768-oa20-Italy-Hotels.html
	 * /Hotels-g187855-Turin_Torino_Province_Piedmont-Hotels.html
	 * 
	 */
	static VectorPattern vPaURL = new VectorPattern(new String[] {
			"/Tourism-(g\\d+)-(.+)-Vacations.html",
			"/Hotels-(g\\d+)-(.+)-Hotels.html",
			"/Hotel_Review-(g\\d+)-(d\\d+)-(.+).html",
			"/ShowUserReviews-(g\\d+)-(d\\d+)-(r\\d+)-(.+).html"});

	public boolean isValidURL(String url){
		if (url.equals(p.rootURL)) return true;
		if (!url.startsWith("/"))return false;
		return vPaURL.match(url)!=null;
		//url.startsWith(p.rootURL);
	}	
	Pattern paG0 = Pattern.compile(
		"/Tourism-(g\\d+)-(.+)-Vacations.html");
	//Tourism-g4-Europe-Vacations.html
	//Hotels-g4-Europe-Hotels.html

	Pattern paG1 = Pattern.compile(
		"/Hotels-(g\\d+)-(.+)-Hotels.html");
	//Hotels-g187768-oa20-Italy-Hotels.html
	
	Pattern paD0 = Pattern.compile(
		"/Hotel_Review-(g\\d+)-(d\\d+)-(.+).html");
	//Hotel_Review-g187791-d191099-Reviews-Albergo_del_Senato-Rome_Lazio.html
	//Hotel_Review-g187791-d191099-Reviews-or10-Albergo_del_Senato-Rome_Lazio.html#REVIEWS
	
	Pattern paR1 = Pattern.compile(
		"/ShowUserReviews-(g\\d+)-(d\\d+)-(r\\d+)-(.+)-Hotels.html");
	///ShowUserReviews-g187791-d191099-r43497707-Albergo_del_Senato-Rome_Lazio.html#REVIEWS

	private static String splitFileName(String g){
		StringBuffer sb= new StringBuffer();
		for (int i=0; i<g.length(); ++i){
		//	if (i%2==1 && i>=3)
			if (i%2==0 && i>=2)
				sb.append('/');
			sb.append(g.charAt(i));
		}			
		return sb.toString();
	}
	public String getFileName(String url){
		if (url.equals(p.rootURL))
			return "default.html";
		
		String[] vs=url.split("[-\\.]");
		
		if (vs[0].equals("/Tourism")){
			//Tourism-g4-Europe-Vacations.html
			String g=vs[1];	String G=vs[2];
			if (vs.length==5){}
			else	return null;
			return splitFileName(g)+"-"+G+"/Tourism.html";
		}

		if (vs[0].equals("/Hotels")){
			//Hotels-g4-Europe-Hotels.html
			String g=vs[1];	String G=vs[2];String i="";
			if (vs.length==5){	}
			else if (vs.length==6){	i=vs[2]; G=vs[3];	}
			else	return null;
			return splitFileName(g)+"-"+G+"/Hotels."+i+".html";
		}
		
		if (vs[0].equals("/Hotel_Review")){
			//Hotel_Review-g187791-d191099-Reviews-or10-Albergo_del_Senato-Rome_Lazio.html#REVIEWS
			String g=vs[1];	String d=vs[2];	
			String D=vs[4];String G=vs[5];String i="";
			if (vs.length==7){	}
			else if (vs.length==8){	i=vs[4]; D=vs[5];G=vs[6];}
			else return null;
			return splitFileName(g)+"-"+G+"/"+d+"-"+D+"/Reviews."+i+".html";
		}
		
		if (vs[0].equals("/ShowUserReviews")){
			///ShowUserReviews-g187791-d191099-r43497707-Albergo_del_Senato-Rome_Lazio.html#REVIEWS
			///ShowUserReviews-g187791-d191099-r43497707-Albergo_del_Senato-Rome_Lazio.html#REVIEWS
			String g=vs[1];	String d=vs[2];	String r=vs[3];	
			String D=vs[4];String G=vs[5];		//String i="";
			if (vs.length==7){	}
			else return null;
			return splitFileName(g)+"-"+G+"/"+d+"-"+D+"/"+r+".html";
		}

		return null;
	}
	public TACrawler(){
		super(TACrawler.class);
		
		p.rootURL="http://www.tripadvisor.com";
		//p.rootURL="http://www.carsurvey.org/";
		//p.nThread=4;
	}
	

	
	public static void main(String args[]) {
		//TripAdvisor crawler = new TripAdvisor();
		//crawler.start();
		
	}
}
