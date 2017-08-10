package edu.cmu.pra.data;


import org.w3c.dom.Node;

import edu.cmu.lti.util.xml.DomElementExtractor;
import edu.cmu.lti.util.xml.FDom;
import edu.cmu.lti.util.xml.DomElementExtractor.ParseNode.CallBack;


public class PMAbsXmlExtractor extends DomElementExtractor{//extends PMAbsInfor{
	PMAbsInfor abs= new PMAbsInfor();
	
	public void postProcess(){
		return;
	};	
	public void clear(){
		abs.clear();
	};
	
	public String sLastName;
	public String sDesc;

	public PMAbsXmlExtractor(){			
		root = new ParseNode();
		
		ParseNode c=root.newChild( "PubmedArticle")
			.newChild( "MedlineCitation");
		
		c.newChild("PMID",new CallBack() {public boolean extract(Node e){
			abs.pmid=e.getTextContent();
			return true;}});				
		
		c.newChild("DateCreated").newChild("Year",new CallBack() {public boolean extract(Node e){
			abs.year=e.getTextContent();
			return true;}});				
		
		c.newChild( "ChemicalList").newChild( "Chemical")
			.newChild( "NameOfSubstance",new CallBack() {public boolean  extract(Node e){
			abs.addChemical(e.getTextContent());
			return true;}});				

		
		ParseNode mh=c.newChild("MeshHeadingList").newChild("MeshHeading");
		mh.newChild( "DescriptorName",new CallBack() {public boolean  extract(Node e){
			boolean bMajor=FDom.getFeatureYN(e,"MajorTopicYN");
			sDesc= abs.addDesc(e.getTextContent(), bMajor);
			return true;}});				

		mh.newChild( "QualifierName",new CallBack() {public boolean  extract(Node e){
			boolean bMajor=FDom.getFeatureYN(e, "MajorTopicYN");
			abs.addQual(e.getTextContent(),sDesc, bMajor);
			return true;}});				

		ParseNode a=c.newChild( "Article");
		
		a.newChild( "Journal").newChild( "ISOAbbreviation",new CallBack() {public boolean  extract(Node e){
			abs.addJournal(e.getTextContent());
			return true;}});				
		
		a.newChild( "ArticleTitle",new CallBack() {public boolean  extract(Node e){
			abs.title=e.getTextContent();
			return true;}});				

		a.newChild( "Abstract").newChild( "AbstractText",new CallBack() {public boolean  extract(Node e){
			abs.addAbstract(e.getTextContent());
			return true;}});				
		
		a.newChild( "Affiliation",new CallBack() {public boolean  extract(Node e){
			abs.addAffiliation(e.getTextContent());
			return true;}});				
		
		ParseNode aa= a.newChild( "AuthorList").newChild( "Author");
		//NodeList nl=e.getChildNodes();	nl.item(index)
		aa.newChild( "LastName",new CallBack() {public boolean  extract(Node e){
			sLastName= e.getTextContent();
			return true;}});	
		
		aa.newChild( "Initials",new CallBack() {public boolean  extract(Node e){
			String author= sLastName+"_"+e.getTextContent();
			abs.vAuthors.add(author);
			return true;}});				

	}
}
