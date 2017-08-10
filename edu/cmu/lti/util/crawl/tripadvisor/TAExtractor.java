package edu.cmu.lti.util.crawl.tripadvisor;

import javax.swing.text.Element;
import javax.swing.text.html.HTML;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.crawl.Extractor;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.FSwingHtml;
import edu.cmu.lti.util.html.TxtElementExtractor;
import edu.cmu.lti.util.html.TxtElementExtractor.Node.CallBack;

public class TAExtractor  extends Extractor{

	public TAExtractor(){
		super(TAExtractor.class);
	}
	public static class ReviewExtractor extends TxtElementExtractor{
		String RID=null;
		String quote=null;
		String rating=null;
		
		String user=null;
		String UID=null;
		
		String location=null;
		String date=null;
		String TripType=null;		
	
		String fndhlp=null;
		String review=null;
		VectorS vsRating=new VectorS(); 
	
		String stayNfo=null;
		String stayFor=null;
		String stayWith=null;
		
		String recoQstn=null;
		
		public void clear1(){
			RID=null;
			quote=null;
			rating=null;
			
			user=null;
			UID=null;
			
			location=null;
			date=null;
			TripType=null;		
	
			fndhlp=null;
			review=null;
			vsRating.clear(); 
			
			stayNfo=null;
			stayFor=null;
			stayWith=null;
			
			recoQstn=null;
		};
		static String sEmpty="";
		public void clear(){
			RID=sEmpty;
			quote=sEmpty;
			rating=sEmpty;
			
			user=sEmpty;
			UID=sEmpty;
			
			location=sEmpty;
			date=sEmpty;
			TripType=sEmpty;		
	
			fndhlp=sEmpty;
			review=sEmpty;
			vsRating.clear(); 
			
			stayNfo=sEmpty;
			stayFor=sEmpty;
			stayWith=sEmpty;
			
			recoQstn=sEmpty;
		};
		
		public static String getFirstTerm(String s){
			int i=s.indexOf(' ');
			if (i==-1) 
				return s;
			else
				return s.substring(0,i);
		}
		public static String getlastTerm(String s){
			int i=s.lastIndexOf(' ');
			if (i==-1)
				return s;
			else
				return s.substring(+1);
		}
		public void postProcess(){
			RID=RID.substring(2);
			if (fndhlp.length()>0)//!=null)
				fndhlp=getFirstTerm(fndhlp);
			int i1= date.indexOf('|');
			if (i1>0){
				TripType= date.substring(i1+13);
				date= date.substring(0,i1-1);
			}
			review= review.replaceAll("\\s", " ");
			user=getFirstTerm(user);
			rating=getFirstTerm(rating);
			for (int i=0; i<vsRating.size(); ++i)
				vsRating.set(i,getFirstTerm(vsRating.get(i)));
			
			for (String s: this.stayNfo.split("\n")){
				if(s.startsWith("Visit was for "))
					stayFor=s.substring(14);
				else if (s.startsWith("Traveled with "))
					stayWith=s.substring(14);
			}
			recoQstn = getlastTerm(recoQstn);
			return;
		};
		public String print(){
			return String.format(
				"%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s\t%s"
			,RID, quote,	rating,	user,	UID
			,location,	date,	TripType
			,vsRating.join(" "),fndhlp
			,stayFor, stayWith	
			,review);
			
		}
		/*public void extract(Element e){
			clear();
			super.extract(e);
			postProcess();
		}*/
	
		public ReviewExtractor(){			
			root = new Node(HTML.Attribute.ID, "REVIEWS",new CallBack() {public boolean extract(Element e){
				RID=FSwingHtml.getID(e);
				return  true;}});
			
			root.newChild(HTML.Attribute.CLASS, "quote",new CallBack() {	public boolean extract(Element e){
				quote=FSwingHtml.getText(e);
				return  true;}});
			
			Node nW= root.newClass("wrap forSave");
			
			Node nP= nW.newChild(HTML.Attribute.CLASS, "profile");
			nP.newClass("rating",new CallBack() {public boolean extract(Element e){
				rating=FSwingHtml.getAlt(e.getElement(0).getElement(0));
				return  true;}});
			
			Node nU=nP.newClass("username mo",new CallBack() {	public boolean extract(Element e){
				UID=FSwingHtml.getID(e);
				user=FSwingHtml.getText(e);
				return  true;}});
			
			nU.newName("span",new CallBack() {public boolean extract(Element e){
				user=FSwingHtml.getText(e);
				return  true;}});
		
			nP.newClass("location",	new CallBack() {public boolean extract(Element e){
				location=FSwingHtml.getText(e);
				return  true;}});
	
			nP.newClass("date new",new CallBack() {public boolean extract(Element e){
				date=FSwingHtml.getText(e);
				return  true;}});
			nP.newClass("date ",new CallBack() {public boolean extract(Element e){
				date=FSwingHtml.getText(e);
				return  true;}});
	
			//nD.newClass("triptype",				new CallBack() {public void extract(Element e){
				//TripType=FHtml.getText(e);}});
			//<SPAN class=triptype>Trip type: Couples </SPAN>
			
			Node nS=root.newClass("summary");
			nS.newClass("fndhlp",					new CallBack() {public boolean extract(Element e){
				fndhlp=FSwingHtml.getText(e);
				return  true;}});
			Node nE=nS.newClass("entry",					new CallBack() {public boolean extract(Element e){
				review=FSwingHtml.getText(e);
				return  true;}});
			root.addChild(nE);
			
			//.newClass("recommend").newName("li")
			Node nR= root.newClass("rating-list",new CallBack() {	public boolean extract(Element e){
				FSwingHtml.extractAll(e,HTML.Attribute.ALT,HTML.Attribute.ALT, vsRating);
				return  true;}});
				//vsRating=FPattern.matchAll(FHtml.getText(e), "rate ss(\\d+)\"");}});
	
			Node nF=root.newClass("stayNfo",new CallBack() {public boolean extract(Element e){
				stayNfo=FSwingHtml.getText(e);
				return  true;}});
			root.newClass("recoQstn",new CallBack() {	public boolean extract(Element e){
				recoQstn=FSwingHtml.getText(e);
				return  true;}});
			
			
			System.out.println("schema=\n"+this);
			return;
		}
	
/*	public class AEHtmlExtractor extends HtmlExtractor{
		 	ReviewExtractor extractor= new ReviewExtractor();
		 	//public AEHtmlExtractor(){ super();	 	}
		 	public void processAHtml(String url,	String html){
				if (!url.startsWith("/ShowUserReviews"))
					return;		
				
				String[] vs=url.split("[-\\.]");
				String g=vs[1];	String d=vs[2];	String r=vs[3];	
				String D=vs[4];String G=vs[5];		//String i="";
	
				int iG= Integer.parseInt(g.substring(1));
				int iD= Integer.parseInt(d.substring(1));
				int iR= Integer.parseInt(r.substring(1));
				
				HTMLDocument doc= FHtml.loadHtml(html);
				//Document doc= FXml.loadTxt(html);
				Element e= FHtml.findElement(doc, HTML.Attribute.ID
						,"REVIEWS");//, "reviews");//
				if (e==null){
					System.out.print("cannot find REVIEWS from page="+url);
					return;
				}
				for (Element c: FHtml.matchChild(e, HTML.Attribute.ID, "UR\\d+")){
					//processAReview(c);
					extractor.extract(c);
					synchronized(tp.queue){//bwReviews){
						FFile.write(bwOutput
							,iG+"\t"+iD+"\t"+extractor.print()+"\n");
						FFile.flush(bwOutput);
					}
				}
				synchronized(nParsed){
					++nParsed;
					if (nParsed % 20==0)
						System.out.print(".");
					
					if (nParsed % 1000==0){
						System.gc();
						System.out.print("\nParsed="+nParsed+" "+FSystem.memoryUsageS());
					}
				}
				return;
			}
		}*/
 	protected WorkThread newWorkThread(int i){
		//return tp.new Extractor.WorkThread(i, new AEHtmlExtractor());
 		return null;
	}
}
	public static void main(String args[]) {
		TAExtractor extractor= new TAExtractor();
		extractor.processOutputs();
		
		//postProcess();
	}
}
