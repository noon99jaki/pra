package edu.cmu.pra.data;

import javax.swing.text.Element;

import edu.cmu.lti.util.html.FSwingHtml;
import edu.cmu.lti.util.html.TxtElementExtractor;
import edu.cmu.lti.util.html.TxtElementExtractor.Node.CallBack;

public class PMAbsHtmlExtractor extends TxtElementExtractor{//extends PMAbsInfor{
	PMAbsInfor abs= new PMAbsInfor();
	public void postProcess(){
		return;
	};


	public PMAbsHtmlExtractor(){			
		root = new Node(null,null,new CallBack() {public boolean extract(Element e){
			return true;}});
		
		root.newClass( "citation",new CallBack() {	public boolean extract(Element e){
			abs.addJournal( FSwingHtml.getText(e.getElement(0)) );
			abs.addPage(FSwingHtml.getText(e.getElement(1)));
			return true;
			}});
		
		root.newClass( "title",new CallBack() {public boolean extract(Element e){
			abs.title=FSwingHtml.getText(e);
			return true;}});

		root.newClass( "aff",new CallBack() {public boolean extract(Element e){
			abs.affiliation=FSwingHtml.getText(e);
			return true;}});
		
		root.newClass( "abstract_text",new CallBack() {public boolean extract(Element e){
			abs.abst=FSwingHtml.getText(e);
			return true;}});
		
		root.newClass( "auth_list",new CallBack() {public boolean extract(Element e){
			abs.vAuthors.clear();//=FPattern.matchAll(s,"><b>(.*?)</b></a>");
			for (int i=0;i< e.getElementCount(); i+=2){
				String name= FSwingHtml.getText(e.getElement(i));
				abs.vAuthors.add(name.replaceAll(" ","_"));
			}
			return true;}});
		root.newClass( "pmid",new CallBack() {public boolean extract(Element e){
			return true;}});
		return;
	}
	/*public PMAbsInfo(){			
		root = new Node(null,null,new CallBack() {public void extract(Element e){
			for (int i=0; i<e.getElementCount(); ++i)
				System.out.println("e"+i+"="+FHtml.getText( e.getElement(i)));
			return;}});
		
		root.newClass( "articleInfo",new CallBack() {	public void extract(Element e){
			journal=FHtml.getText(e);}});
		
		Node nT=root.newClass("p-implied").newClass( "title",new CallBack() {public void extract(Element e){
			title=FHtml.getAlt(e.getElement(0).getElement(0));}});

		System.out.println("schema=\n"+this);
		return;
	}*/

	/**
	static int nContact=0;
	 * 	static Pattern paH1 = Pattern.compile(//"<dl>(.*)</dl>");
	"<[dD][lL]>(.*?)</[dD][lL]>");
	static Pattern paC1 = Pattern.compile(
	"class=\"articleInfo\".*<a href=.*>(.*?)</a>"
		+"</span>(.*?)</div><div class=\"relArticles\""
		+".*<dd class=\"title\">.*<b>(.*?)</b></font>"
		+"<br /><br />(.*)"
		//?)<br /><br />(.*?)<br /><br />(.*)
		//.*PMID: (\\d+) \\["
		);

	public boolean processHtml1(String html)	{
		Matcher mH= paH1.matcher(html);		
		if (!mH.find())
			return false;
		
		
		String txt= mH.group(1);
		Matcher mC= paC1.matcher(txt);
		if (!mC.find()){
			System.out.println("failed to parse pmid="+ae.pmid);
			return false;
		}		
		ae.addJournal(mC.group(1));
		ae.addPage( mC.group(2));
		ae.title= mC.group(3);
		String list=  mC.group(4);
		String[] vs= list.split("<br /><br />");
		for (String s: vs){
			
			if (s.contains("Pubmed_RVAbstract")){//WEAK FEATURE?
				ae.vAuthors=FPattern.matchAll(s,"><b>(.*?)</b></a>");
				for (int i=0; i<ae.vAuthors.size();++i)
					ae.vAuthors.set(i,
						ae.vAuthors.get(i).replaceAll(" ","_"));
			}
			else if (s.contains("@")){//WEAK FEATURE?
				ae.addAffiliation(s);
				++nContact;
			}
			else if (s.length()>100){
				ae.abst= s;
			}			
		//for (int i=5; i<=mC.toMatchResult().groupCount(); ++i){
			//String s= mC.group(i);
		}
		return true;
	}

	
	static Pattern paH2 = Pattern.compile(//"<dl>(.*)</dl>");
	"<div class=\"rprt abstract\">(.*)"
		+"</div>\\s*<div class=\"title_and_pager bottom");
	public boolean processHtml2(String html)	{
		Matcher mH= paH2.matcher(html);		
		if (!mH.find())
			return false;
		Element e= FHtml.loadElement(mH.group(1));
		if (e==null) return false;
		
		ae.extract(e);

		return true;
	}
	//static PMAbsHtml ae= new PMAbsHtml();
	 * */
	 

}