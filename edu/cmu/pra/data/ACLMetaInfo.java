package edu.cmu.pra.data;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.CTag;

public class ACLMetaInfo implements IParseLine, IGetStrByStr{
	
	public String title = null;			
	public VectorS vAuthors = new VectorS();
	public String bookTitle=null;
	public String month=null;
	public String year = null;			
	public String address=null;
	public String publisher = null;	
	public String pages = null;	
	public String bibtype = null;
	public String bibkey = null;
	
	

	public String getCitation(){
		return	 vAuthors.join(" ")+	" ("+year+") "+ title;//+" "+ publisher;
	}
	public static Seq<ACLMetaInfo> reader(String fn){
		return reader(fn, false);
	}
	public static Seq<ACLMetaInfo> reader(String fn,boolean bSkipTitle){
		return new SeqTransform<ACLMetaInfo>(	ACLMetaInfo.class, fn, bSkipTitle);
	}
	 
	
	public ACLMetaInfo(){
	}
	public ACLMetaInfo(String line){
		parseLine(line);
	}
	public String toString(){
		return this.getCitation();
	}

	
	/**
	 * print affiliation information
	 * @param bw	
	 * @param mAff	set of acceptable affiliations, 
	 * 	accept all if mAff==null 
	 */
	public void normalize(){
		
		title=FPaper.processTitle(title);
		//journal = journal.replaceAll("[();:\\.,\\?\\!]", "");
		
		for (String a : this.vAuthors){
			
			//if (a.indexOf('.')>=0)		System.err.print("bad name="+a);
		}
		
		PaperID=CorpusID+"-"+PaperID;
		int y= Integer.parseInt(CorpusID.substring(1));
		if (y>50) y+=1900;
		else	y+=2000;
		this.year=y+"";
		return ;
	}
	
	public static String CorpusID="";
	
	@Override public String getString(String tag){
		if (tag.equals(CTag.PRA))	return this.printPRA();		
		return null;
	}
	
	/**
	 * print schema of the above information
	 * @param bw
	 */
	public static String printSchema(){
		StringBuffer sb= new StringBuffer();
		sb.append("Title(paper,word)\n");
		sb.append("Year(paper,year)\n");
		
		sb.append("Author(paper,author)\n");
		sb.append("FAuthor(paper,author)\n");
		sb.append("LAuthor(paper,author)\n");
		
		sb.append("Book(paper,book)\n");
		sb.append("Address(paper,address)\n");
		return sb.toString();
	}
	
	//public void write2PRA(BufferedWriter bw){	}
	public String printPRA(){
		StringBuffer sb= new StringBuffer();
		if (year.length()>0)
			sb.append( "Year(" + PaperID + "," + year + ")\n");
		else
			System.out.println("year not defined for paper="+this.PaperID);
		
		if (title.length()>0)	
			sb.append( "Title("+PaperID+","+ title+ ")\n");

		if (vAuthors.size()>0){
			sb.append("Author("+PaperID+","+vAuthors.join(" ") + ")\n");
			sb.append("FAuthor("+PaperID+","+vAuthors.firstElement()+")\n");
			sb.append("LAuthor("+PaperID+","+vAuthors.lastElement()+")\n");
		}
		
		
		if (bookTitle.length()>0){
			bookTitle=processBook(bookTitle);
			sb.append( "Book(" + PaperID + "," + bookTitle + ")\n");
			//System.out.println(bookTitle);
		}
	//	if (publisher.length()>0)
		//	sb.append("Publisher(" + PaperID + "," + publisher + ")\n");
			
		if (address.length()>0){
			address=processAddress(address);
			sb.append( "Address(" + PaperID + "," + address + ")\n");
			//System.out.println(address);
		}
		return sb.toString();
	}
	static String processBook(String s){
		s=s.replaceAll("\\W", "_");
		return s;
	}
	static String processAddress(String s){
		int p=s.indexOf(',');
		if (p>=0)
		s=s.substring(0,p);
		s=s.replace(' ','_');
		return s;
	}
	
	
	
	static String sEmpty="";//null;//
	public void clear(){
		PaperID=sEmpty;

		title = sEmpty;			
		vAuthors.clear(); 
		bookTitle= sEmpty;

		month = sEmpty;
		year = sEmpty;			
		address=sEmpty;	
		
		publisher = sEmpty;
		pages = sEmpty;
		bibtype = sEmpty;

		bibkey = sEmpty;
		
		

	};
	
	
	/**
	 * title line of the plain table format
	 */
	public static String getTitle(){
		return	"pmid"
				+"\tyear"
				+"\tjournal"
				
				+"\temail"
				+"\taffiliation"
				
				+"\tAuthors"
				
				+"\tChemicals"
				+"\tMHDescriptorM"
				+"\tMHQualifierM"
				+"\tMHDescriptor"
				+"\tMHQualifier"
				
				+"\ttitle"
				+"\tabstract";
	}
	

	/**
	 * print in plain table format
	 */

	//private void Senario(BufferedWriter writer, SetS mAff){
	public String printSenario(String citations, String genes){
		if (genes==null) genes="";
		if (citations==null) citations="";
		return PaperID
			+","+ year
			
			+","+ vAuthors.join(" ")
			+","+ title
			+","+ citations
			+","+ genes
			
			;//paper,year,journal,author,word,paper,gene

	}
	public static String getScenTitle(){
		return	"pmid"
			+"\tmonth"
			+"\tyear"
			+"\tbibtype"
			+"\tbibkey"
			+"\taddress"		
			+"\tauthors"
			+"\ttitle"
			+"\tbookTitle";
	}
	
	public String print(){
		return	PaperID
				+"\t"+ month
				+"\t"+ year
				+"\t"+ bibtype
				+"\t"+ bibkey
				+"\t"+ address				
				+"\t"+ vAuthors.join(" ")
				+"\t"+ title
				+"\t"+ bookTitle;
				
//		public String publisher = null;	
//		public String pages = null;	
	}
	/**
	 * load from plain table format
	 */
	public String PaperID;
	public boolean parseLine(String line){
		VectorS vs= FString.splitVS(line,"\t");
		int i=0;
		PaperID=vs.get(i);++i;
//		if (pmid.length()==0) return false;
		month=vs.get(i);++i;		
		year=vs.get(i);++i;		
		bibtype=vs.get(i);++i;
		bibkey=vs.get(i);++i;
		address=vs.get(i);++i;
		vAuthors=FString.splitVS(vs.get(i).trim()," ");++i;
		title=vs.get(i);++i;
		bookTitle=vs.get(i);++i;
		return true;
	}
	
	static int nFailure=0;

}
