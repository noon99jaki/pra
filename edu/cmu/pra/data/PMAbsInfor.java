package edu.cmu.pra.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.text.FPattern;
import edu.cmu.lti.util.text.FString;

/**
 * This data structure correspond to information in a 
 * PubMed abstract  
 * @author nlao
 *
 */
public class PMAbsInfor implements IParseLine{
	public static String getAbsURL(String pmid){
		return String.format(
			"http://www.ncbi.nlm.nih.gov/pubmed/%s?dopt=Abstract"	, pmid);
	}
	
	public String pmid=null;
	public String year = null;			
	public String journal = null;
	
	public String page = null;
	
	//String affiliation = null;
	public String affiliation=null;
	public String email = null;
	
	public VectorS vAuthors = new VectorS();
	public VectorS vChemicals = new VectorS();
	
	//mesh heading
	public VectorS vMHDescriptor = new VectorS();
	public VectorS vMHQualifier = new VectorS();

	//major headings
	public VectorS vMHDescriptorM = new VectorS();
	public VectorS vMHQualifierM = new VectorS();
	
	public String title = null;			
	public String abst=null;
	
	public String getCitation(){
		return	// vAuthors.join(" ")+
			" ("+year+") "+ title+" "+ journal;
	}
	public static Seq<PMAbsInfor> reader(String fn){
		return reader(fn, false);
	}
	public static Seq<PMAbsInfor> reader(String fn,boolean bSkipTitle){
		return new SeqTransform<PMAbsInfor>(	PMAbsInfor.class, fn, bSkipTitle);
	}
	 
	
	public PMAbsInfor(){
	}
	public PMAbsInfor(String line){
		parseLine(line);
	}
	public String toString(){
		return this.getCitation();
	}
	/**
	 * print schema of the above information
	 * @param bw
	 */
	public static void writePRASchema(BufferedWriter bw){
	
		FFile.write(bw, "Year(paper,year)\n");
		FFile.write(bw, "Journal(paper,journal)\n");
	
		FFile.write(bw, "Author(paper,author)\n");
		FFile.write(bw, "FAuthor(paper,author)\n");
		FFile.write(bw, "LAuthor(paper,author)\n");
	
		FFile.write(bw, "Title(paper,word)\n");
		
		FFile.write(bw, "Aff(paper,affiliation)\n");
		FFile.write(bw, "Chem(paper,chemical)\n");
		
		FFile.write(bw, "DHead(paper,heading)\n");
		FFile.write(bw, "QHead(paper,heading)\n");
		FFile.write(bw, "DmHead(paper,heading)\n");
		FFile.write(bw, "QmHead(paper,heading)\n");
		FFile.flush(bw);
	}
	
	

	/**
	 * normalize stuff to be entity IDs
	 * @param bw	
	 * @param mAff	set of acceptable affiliations, 
	 * 	accept all if mAff==null 
	 */
	public void normalize(SetS mAff){
		
		title=FPaper.processTitle(title);
		affiliation= extractNormalizeAff(affiliation);
		if (mAff!=null)
			if (!mAff.contains(affiliation))
				affiliation= "";
		
		/*
		PRA.normalizeV(vAuthors);
		PRA.normalizeV(vChemicals);
		PRA.normalizeV(vMHDescriptor);
		PRA.normalizeV(vMHQualifier);
		PRA.normalizeV(vMHDescriptorM);
		PRA.normalizeV(vMHQualifierM);
*/
		
		journal =PRA.normalizeName( journal);

		return ;
	}
	/** convert affiliation string to an identifier */
	public static String normalizeAff(String s){
		//a=FString.toTitleCase(a);
		s=s.replaceAll("[\\.\\d]",  "");
		s=s.trim();
		if (s.length()>150) return sEmpty;
		//s=s.replaceAll(" ",  "_");
		s=PRA.normalizeName(s);
		return s;
	} 
	public static String extractNormalizeAff(String s){
		return normalizeAff(extractAff(s));
	}

	public void write2PRA(BufferedWriter bw){
		if (year.length()==0){
			System.err.print("unkown publication year for pmid="+pmid );
			return;
		}
			FFile.write(bw, "Year(" + pmid + "," + year + ")\n");
		if (journal.length()>0)
			FFile.write(bw, "Journal(" + pmid + "," + journal + ")\n");
		if (affiliation.length()>0)
			FFile.write(bw, "Aff(" + pmid + "," + affiliation + ")\n");
			
		if (title.length()>0)
			FFile.write(bw, "Title("+pmid+","+ title+ ")\n");

		if (vAuthors.size()>0){
			FFile.write(bw,"Author("+pmid+","+vAuthors + ")\n");
			FFile.write(bw,"FAuthor("+pmid+","+vAuthors.firstElement()+")\n");
			FFile.write(bw,"LAuthor("+pmid+","+vAuthors.lastElement()+")\n");
		}
		
		if (vChemicals.size()>0)
			FFile.write(bw,"Chem("+pmid+","+vChemicals + ")\n");
		
		if (vMHDescriptor.size()>0)
			FFile.write(bw,"DHead("+pmid+","+vMHDescriptor + ")\n");
		if (vMHQualifier.size()>0)
			FFile.write(bw,"QHead("+pmid+","+vMHQualifier + ")\n");
		
		if (vMHDescriptorM.size()>0)
			FFile.write(bw,"DmHead("+pmid+","+vMHDescriptorM + ")\n");
		if (vMHQualifierM.size()>0)
			FFile.write(bw,"QmHead("+pmid+","+vMHQualifierM + ")\n");

	}
	

	
	static String sEmpty="";//null;//
	public void clear(){
		pmid=sEmpty;
		year = sEmpty;			
		journal = sEmpty;
		page = sEmpty;

		affiliation = sEmpty;
		email = sEmpty;
		
		title = sEmpty;			
		abst=sEmpty;	
		
		vAuthors.clear(); 
		vChemicals.clear(); 
		vMHDescriptor.clear(); 
		vMHQualifier.clear(); 
		vMHDescriptorM.clear(); 
		vMHQualifierM.clear(); 
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
	public String print(){
		return	pmid
				+"\t"+ year
				+"\t"+ journal
				
				+"\t"+ email
				+"\t"+ affiliation
				
				+"\t"+ vAuthors.join(" ")
				
				+"\t"+ vChemicals.join(" ")
				+"\t"+ vMHDescriptorM.join(" ")
				+"\t"+ vMHQualifierM.join(" ")
				+"\t"+ vMHDescriptor.join(" ")
				+"\t"+ vMHQualifier.join(" ")
				
				+"\t"+ title
				+"\t"+ abst;
	}
	//private void Senario(BufferedWriter writer, SetS mAff){
	public String printSenario(String citations, String genes){
		if (genes==null) genes="";
		if (citations==null) citations="";
		return pmid
			+","+ year
			+","+ journal		
			
			+","+ vAuthors.join(" ")
			+","+ title
			+","+ citations
			+","+ genes
			
			+","+ affiliation
			+","+ vChemicals.join(" ")
			+","+ vMHDescriptorM.join(" ")
			+","+ vMHQualifierM.join(" ")
			+","+ vMHDescriptor.join(" ")
			+","+ vMHQualifier.join(" ")
			;//paper,year,journal,author,word,paper,gene

	}
	public static String getScenTitle(){
		return	"paper"
		+"\tyear"
		+"\tjournal"
		
		
		+"\tauthor"
		+"\tword"
		+"\tpaper"
		+"\tgene"
		
		+"\tinstitute"
		
		+"\tchemical"
		+"\tdescriptor"
		+"\tqualifier"
		+"\tdescriptor"
		+"\tqualifier"
		;
	}
	
	/**
	 * load from plain table format
	 */
	public Integer iPMID;
	public boolean parseLine(String line){
		VectorS vs= FString.splitVS(line,"\t");
		int i=0;
		pmid=vs.get(i);++i;
		if (pmid.length()==0) return false;
		
		iPMID=Integer.parseInt(pmid);
		
		year=vs.get(i);++i;		
		journal=vs.get(i);++i;
		
		email=vs.get(i);++i;
		affiliation=vs.get(i);++i;
		
		vAuthors=FString.splitVS(vs.get(i).trim()," ");++i;
		
		vChemicals=FString.splitVS(vs.get(i).trim()," ");++i;
		vMHDescriptorM=FString.splitVS(vs.get(i).trim()," ");++i;
		vMHQualifierM=FString.splitVS(vs.get(i).trim()," ");++i;
		vMHDescriptor=FString.splitVS(vs.get(i).trim()," ");++i;
		vMHQualifier=FString.splitVS(vs.get(i).trim()," ");++i;
		
		title=vs.get(i);++i;
		abst=vs.get(i);++i;
		if (i!=vs.size())
			return false;
		return true;
	}
	
	// key words indicating Affiliations
	static String vKey[]=new String[]{
		"Univ","School","College","Academy"
		,"Institu","Center","Laborato"
		,"Hospital","Group","Company"
		,"Inc.","Ltd","Corpora", "Co.","Corp."
		,"Foundation"
		,"Centr","Zentrum"
		,"Istituto","Instytu"
		, "Consiglio","Consejo"	
		, "Unit","Pharmaceutic"};  
	static SetS mBadAff= new SetS(new String[]{
			"USA"});//,"Inc","Inc."
	static int nFailure=0;
	static VectorS vAcnym=new VectorS();
	static VectorS vLocation=new VectorS();
	static VectorS vFailed=new VectorS();
/*	
	public static String extractAffiliation(String s){
		s= extractAff(s);
		return PMAbsInfor.normalizeAffiliation(s);
	}*/
	

	
	// known irregular affiliations
	static SetS mGoodAff= new SetS(new String[]{"Max-Planck"});
	
	
	/** extract affiliation from and address string*/
	private static String extractAff(String s){
		if (s.length()==0) return sEmpty;
		
		VectorS vs= FString.splitVS(s, ", *");
		int i;;
		if ((i=vs.indexFist(vKey))>=0){
			String name = vs.get(i);
			if (mGoodAff.match(name)!=null){
				return mGoodAff.match(name);
			}
			
			if (i>=1)
				if (vs.get(i).equals("Inc.") || vs.get(i).equals("Inc"))
					return vs.get(i-1)+" "+vs.get(i);
			if (i<vs.size()-1)
				if (vs.get(i).equals("University of California"))
					return vs.get(i)+" "+vs.get(i+1);
			if (!mBadAff.contains(vs.get(i)))
				return vs.get(i);	
			
		}
		
		String acnym;
		for (String s1: vs)
			if ( (acnym=FString.findAcronym(s1))!=null )
				if (!mBadAff.contains(acnym)){
					vAcnym.add(acnym);
					return acnym;
				}
		
		int n=vs.size()-1;
		String loc=vs.get(n);;
		if (n>=1)
		if (FString.isSingleWord(vs.get(n))){
			loc= vs.get(n-1)+" "+vs.get(n);
		}
		vLocation.add(loc);
		
		vFailed.add(s);
		//System.out.println("[failure "+nFailure+"] "+s);
		++nFailure;
		return loc;
		
	}

	/** convert a MeshHeading to an identifier */
	public static String normalizeMHeading(String s){
		//s=s.replace(" &amp; ", "&");
		s=s.replace(" & ", "&");
		s=s.replace(") ", ")");
		s=s.replace(" (", "(");
		s=s.replaceAll("[ ,]", "_");
		s=s.replace("__", "_");
		return s;
	}
	
	public String addDesc(String s, boolean bMajor){
		s=normalizeMHeading(s);
		if (bMajor)
			vMHDescriptorM.add(s);
		else
			vMHDescriptor.add(s);
		return s;
	}
	public String addQual(String s, String desc, boolean bMajor){
		s=desc+"::"+normalizeMHeading(s);
		if (bMajor)
			vMHQualifierM.add(s);
		else
			vMHQualifier.add(s);
		return s;
	}
	
	public void addChemical(String c){
		vChemicals.add(normalizeMHeading(c));
	}
	public void addAbstract(String j){
		abst=j.replaceAll("\\p{Cntrl}", " ");		
	}
	public void addJournal(String j){
		journal=j.replaceAll("\\.", "").replaceAll(" ","_")
			.replaceAll("\\p{Cntrl}", " ");			
	}
	public void addPage(String p){
		page=p;
		year=FPattern.match(page,"^ *(\\d+)\\D");
	}
	public void addAffiliation(String a){
		affiliation= a;
		email=FPattern.match(a,"(\\S*@\\S*)");
		if (email!=null){
			int e=a.length()-email.length()-1;
			e=Math.max(e,0);
			affiliation= a.substring(0,e);
		}
		else 
			email= sEmpty;
		return;
	}
	
	public static MapSX<PMAbsInfor> loadAbstracts(String fn){
		MapSX<PMAbsInfor> mInf= new MapSX<PMAbsInfor>(PMAbsInfor.class);
		
		BufferedReader br = FFile.newReader(fn);	
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			PMAbsInfor ab= new PMAbsInfor(line);
			mInf.put(ab.pmid, ab);			
		}		
		FFile.close(br);
		return mInf;
	}
}
