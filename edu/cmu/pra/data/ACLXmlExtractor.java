package edu.cmu.pra.data;

import org.w3c.dom.Node;

import edu.cmu.lti.util.xml.DomElementExtractor;
import edu.cmu.lti.util.xml.FDom;
import edu.cmu.lti.util.xml.DomElementExtractor.ParseNode.CallBack;


/**
 * The format of ACL xml files are lousy
 * 
 * @author nlao
 *
 */
public class ACLXmlExtractor extends DomElementExtractor{

	
	///usr0/nlao/code_java/ni/run/acl/prep/acl-arc-090501d1/metadata/anthology-XML

	ACLMetaInfo info= new ACLMetaInfo();
	
	public void postProcess(){
		return;
	};
	
	
	public void clear(){
		info.clear();
	};
	
	public String sName;
	public String sLastName;
	public String sFirstName;
	public String sDesc;

	public ACLXmlExtractor(){			
		root = new ParseNode("paper",new CallBack(){public boolean extract(Node e){
			info.PaperID=FDom.getAttribute(e,"id");	
			return true;}});	

/*		ParseNode b=root.newChild("volume",new CallBack(){public boolean extract(Node e){
			ACLMetaInfo.CorpusID=FDom.getAttribute(e,"id");	
			return true;}});	


		ParseNode c=b.newChild("paper",new CallBack(){public boolean extract(Node e){
			info.PaperID=FDom.getAttribute(e,"id");	
			return true;}});	
		*/
		ParseNode c=root;
		c.newChild("title",new CallBack(){public boolean extract(Node e){
			info.title=e.getTextContent();	return true;}});				

		ParseNode a= c.newChild( "author",new CallBack() {public boolean  extract(Node e){
			sName=e.getTextContent();
			if (sName.length()==0) return true;
			//sName=sName.replaceAll("[\\. ,]","");
			sName=sName.replaceAll("\\W","");
			//sName=sName.replaceAll(" ","_");
			info.vAuthors.add(sName);
			return true;}});	
		
	/*	a.newChild( "last",new CallBack() {public boolean  extract(Node e){
			sLastName=e.getTextContent();
			
			info.vAuthors.add(sFirstName+"_"+sLastName);
			return true;}});			

		a.newChild( "first",new CallBack() {public boolean  extract(Node e){
			sFirstName= e.getTextContent();
			return true;}});*/
		
		c.newChild("booktitle",new CallBack() {public boolean extract(Node e){
			info.bookTitle=e.getTextContent();
			return true;}});		
		
		c.newChild("year",new CallBack() {public boolean extract(Node e){
			info.year=e.getTextContent();
			return true;}});				
		
		c.newChild("month",new CallBack() {public boolean extract(Node e){
			info.month=e.getTextContent();
			return true;}});		
		
		c.newChild("address",new CallBack() {public boolean extract(Node e){
			info.address=e.getTextContent();
			return true;}});				
			
		a.newChild( "publisher",new CallBack() {public boolean  extract(Node e){
			info.publisher=e.getTextContent();
			return true;}});		
		
		a.newChild( "pages",new CallBack() {public boolean  extract(Node e){
			info.pages=e.getTextContent();
			return true;}});		
		
		a.newChild( "bibkey",new CallBack() {public boolean  extract(Node e){
			info.bibkey=e.getTextContent();
			return true;}});	
		
		a.newChild( "bibtype",new CallBack() {public boolean  extract(Node e){
			info.bibtype=e.getTextContent();
			return true;}});			
		
	}
	

}

