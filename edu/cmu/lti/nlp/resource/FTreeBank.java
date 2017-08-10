package edu.cmu.lti.nlp.resource;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.TMapIVecSa;
import edu.cmu.lti.algorithm.container.MapSVecI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.TMapXVecX;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.MapIW;
import edu.cmu.lti.util.file.MapSW;
import edu.cmu.lti.util.text.FString;

/**
 * why use Perl to preprocess treeBank?
 * Isnt Java more organizable?
 * @author nlao *
 */
public class FTreeBank {
	public static  class Param extends edu.cmu.lti.util.run.Param{
		public String ctbFolder;
		public String etbFolder;
		public String lang;
		public boolean noFRAG;//whether use the FRAG sentences 
		public boolean noNP;//whether use the FRAG sentences 
		public boolean IPonly;//whether use the FRAG sentences 
		public void parse(){
			//lang = System.getProperty("lang");
			ctbFolder =getString("ctbFolder"
				,"/usr2/nlao/resources/LDC/ctb6.0/ctb_v6/data/utf8");
			etbFolder =getString("etbFolder"
				,"/usr2/nlao/resources/LDC/TREEBANK_3/PARSED/MRG/WSJ");
			noFRAG = getBoolean("noFRAG", false);
			noNP = getBoolean("noNP", false);
			IPonly = getBoolean("IPonly", true);
		}
		public Param(){
			super(FTreeBank.class);
			parse();
		}
	}
	public static Param p = new Param();

	public static String[] collinsPunctTags = {"''", "``", ".", ":", ","};

	public static String[] pennPunctTags = {"''", "``", "-LRB-", "-RRB-", ".", ":", ","};
	public static String[] pennPunctWords = {"''", "'", "``", "`", "-LRB-", "-RRB-", "-LCB-", "-RCB-", ".", "?", "!", ",", ":", "-", "--", "...", ";"};
	
	public static String[] pennSFPunctTags = {"."};
	public static String[] pennSFPunctWords = {".", "!", "?"};

	public static MapSS mPunRulesEn;
	static{
		mPunRulesEn=new MapSS();
		mPunRulesEn
			//.addOn("'", "''")
			.addOn("''", "''")
			.addOn(",", ",")
			.addOn(".",  ".").addOn("!",  ".").addOn("?", ".")
			//.addOn("'", ":")
			.addOn("-", ":").addOn("--", ":")
			.addOn("...", ":").addOn(":", ":").addOn(";", ":")
			.addOn("",  "-LCB-").addOn("",  "-RCB-")
			.addOn("",  "-LRB-").addOn("",  "-RRB-")
			.addOn("'", "POS")
			.addOn("``", "``").addOn("`", "``");

		/*	counted from english PTB
 		''>'	148	
		''>''	8335
		,>,	59888
		.>!	74
		.>.	47832
		.>?	513
		:>'	3
		:>-	97
		:>--	2549
		:>...	238
		:>:	1654
		:>;	1490
		>-LCB-	244
		>-LRB-	1408
		>-RCB-	247
		>-RRB-	1423
		POS>'	760
		``>`	138
		``>``	8565*/

	}
	
	public static String mergeFiles(
			String ctbFolder,String type, int ib, int ie){
		//chtb_0001.fid
		String fn=String.format("%s/%s.%d-%d", ctbFolder,type,ib,ie);
		BufferedWriter bw=FFile.newWriter(fn	);
		for (int i=ib; i<=ie; ++i){
			String fn1 =String.format("%s/%s/chtb_%04d.fid", ctbFolder,type,i);
			if (!FFile.exist(fn1))
				continue;
			System.out.println("merge file ID="+i);
			FFile.write(bw,FFile.loadString(fn1));
		}
		FFile.close(bw);
		return fn;
	}	
	
	/**
	 * 
	 * @param fn: full path to the file
	 */
	public static void pretty2oneLineTreeCh(String fn){
		BufferedReader br=FFile.newReader(fn);
		BufferedWriter bw=FFile.newWriter(fn +".1line");
		String line = null;
		int n=0;
		while ((line =FFile.readLine(br)) != null) {
			if (!line.startsWith("<S ")) //skip the tags 
				continue;
			StringBuffer sb = new StringBuffer();
			while (true) {
				line = FFile.readLine(br);
				if (line == null) {
					System.err.println(
							"Ill-formated input tree file, <S> tag not closed by </S>");
					// System.exit(1);
				}
				if (line.equals("</S>")) 
					break;
				sb.append(line.trim()+" ");
			}
			if (sb.length()<4) continue;
			String str = sb.substring(1, sb.length()-2);
			str =  str.trim()+"\n";
			FFile.write(bw, str);
			++n;
		}
		FFile.close(bw);
		System.out.println(n+" lines converted");
	}

	/**
	 * 
	 * @param fn: full path to the file
	 */
	public static void pretty2oneLineTreeEn(String fn){
		BufferedReader br=FFile.newReader(fn);
		BufferedWriter bw=FFile.newWriter(fn +".1L");
		String line = null;
		int n=0;
		StringBuffer sb = new StringBuffer();
		while ((line =FFile.readLine(br)) != null) {
			if (line.startsWith("( (")) {
				if (sb.length()>4){
					FFile.write(bw, sb.substring(2, sb.length()-2));
					FFile.write(bw, "\n");
					++n;
				}
				sb = new StringBuffer();
			}
			sb.append(line.trim()+" ");
		}
		if (sb.length()>4){
			FFile.write(bw, sb.substring(1, sb.length()-2));
			FFile.write(bw, "\n");
			++n;					
		}
		FFile.close(bw);
		System.out.println("\n"+n+" lines converted");
	}
	//private void popLine(BufferedWriter writer, StringBuffer sb){	}
	
	
	
	//final String[] vs_type 
	//= new String[]{"40","30","20","10","6", "0"};
	static final int[] viTh = new int[]{40,20,10,5};
	//get sentences shorter than these thresholds
	/**
	 * split tree bank according to the sentence length
	 * @param fn1L
	 */
	public static void splitByLength(String fn1L){
		BufferedReader br=FFile.newReader(fn1L);
	
		MapIW mBW= new MapIW(fn1L+".");

		String line = null;
		while ((line =FFile.readLine(br)) != null) {
			int size = FString.count(line, '(');
			size = (size+1)/2;
			
			for (int th: viTh){
				if (size>th) continue;				
				FFile.write(mBW.getBW(th),line+"\n");
			}		
		}
		mBW.closeAll();

	}
	/**
	 * split tree bank according to the top tag of a sentence
	 * @param oneLineParseFile
	 */
	public static void splitByTopTag(String oneLineParseFile){
		BufferedReader br=FFile.newReader(oneLineParseFile);
		
		MapSW mBW= new MapSW(oneLineParseFile+".");

		
		String line = null;
		while ((line =FFile.readLine(br)) != null) {
			//this is fragile, whatever
			String type = line.substring(1, line.indexOf(' '));
			FFile.write(mBW.getBW(type),line+"\n");
		}
		mBW.closeAll();
		
	}


	
	/**
	 * remove short trees 
	 * trim empty nodes, collapse Unary nodes in the form of A -> A 
	 * @param line
	 * @return
	 */
	protected static TreeSyntax preprocessTree(String line){
		if (line.startsWith("<")) 
			return null;
		if (line.startsWith(" ")) 
			return null;
		if (p.noFRAG)
			if (line.startsWith("(FRAG")) return null;
		if (p.noNP)
			if (line.startsWith("(NP")) return null;
		if (line.length()<5) return null;
//		if (line.trim().equals("")) continue;
		TreeSyntax tree0 = new TreeSyntax();
		if (!tree0.parseBracketString(line)) return null;
		if (tree0.vNode.size() == 0) return null;
		tree0.trimEmptyNodes();
		tree0.collapseAAUnary();
		return tree0;
	}
	
	/**
	 * preprocess a set of trees
	 * @param fn
	 */
	public static void preprocessTrees(String fn){
		BufferedReader br = FFile.newReader(fn);
		BufferedWriter bw = FFile.newWriter(fn + ".prep");
		String line = null;int nSent=0;
		while ((line = FFile.readLine(br)) != null) {
			TreeSyntax tree=FTreeBank.preprocessTree(line);
			if (tree==null) continue;
//			if (p.dbg>0)	System.out.println(tree0);		
			FFile.write(bw, tree.printBracketString());
			FFile.write(bw,"\n");
			++nSent;	
			//if (nSent % 100==0)		System.out.print("h");
		}
		FFile.close(bw);
		FFile.close(br);
	}
	public static void stripBracket(String fn){
		BufferedWriter bw = FFile.newWriter(fn + ".txt");
		
		TreeSyntax tree = new TreeSyntax();
		for (String line: FFile.enuLines(fn)){
			if (!tree.parseBracketString(line)) continue;
			if (tree.vNode.size() == 0) continue;
			FFile.writeln(bw, tree.vWord.join(" "));
		}
		FFile.close(bw);
	}
	/**
	 * from pretty printing tree file 
	 * to file for training models
	 * @param fn
	 */
	//protected static void processFile(String fn){	}
	public static void processCTB(){
		/*
		FSystem.cmd("cat postagged/* > all.postagged");
		FSystem.cmd("cat segmented/* > all.segmented");
		FSystem.cmd("cat raw/* > all.raw");
		FSystem.cmd("cat bracketed/* > all.bracketed");
		*/
		
		pretty2oneLineTreeCh(p.ctbFolder+"/all.bracketed");	
		//p.noFRAG=true;p.noNP=true;
		FTreeBank.preprocessTrees(p.ctbFolder+"/all.bracketed.1line");
		splitByTopTag(p.ctbFolder+"/all.bracketed.1line.prep");
		splitByLength(p.ctbFolder+"/all.bracketed.1line.prep.IP");
		
		/** Sections 001-270 (3484 sentences, 84,873 words) were used for training, 
		271-300 (348 sentences, 7980 words) for development, and testing.	 */
		/*chtb_0001.fid
		String f1=mergeFiles(p.ctbFolder, "bracketed", 1, 270);
		convert2oneLineParse(f1);
		//FTreeBank.preprocessTrees(p.ctbFolder+"/bracketed.1-270.1line");
		
		String f2=mergeFiles(p.ctbFolder, "bracketed",271, 300);		
		convert2oneLineParse(f2);
		//FTreeBank.preprocessTrees(p.ctbFolder+"/bracketed.271-300.1line");
		*/
		
			
	}
	
	protected static void process(String fn, boolean bSplitTag){		
		pretty2oneLineTreeEn(fn);		
		//p.noFRAG=true;p.noNP=true;
		preprocessTrees(fn+".1L");
		stripBracket(fn+".1L.prep");		
		
		if (bSplitTag){
			splitByLength(fn+".1L.prep");		
			splitByTopTag(fn+".1L.prep");
			splitByLength(fn+".1L.prep.S");
		}
	}
	
	/** 	
	Performance measure: PARSEVAL - the evalb program 
	Training data: sections 2-22 of Wall Street Journal corpus 
	Testing data: section 23 of Wall Street Journal corpus
	 */
	public static void processETB(){
		/*
		for (int i=0; i<=24;++i){
			String id= String.format("%02d",i);
			String fn = p.etbFolder+"/"+id+".MRG";
			FFile.mergeFiles(p.etbFolder+"/"+id, ".*MRG", fn);			
		}
		String fn=p.etbFolder+"/"+"all";
		FFile.mergeFiles(p.etbFolder, ".*MRG", fn);		
		process(fn, true);
		*/
		
		
		//String fn=p.etbFolder+"/02-21";
		/*
		VectorS vs=new VectorS();
		for (int i=2; i<=21;++i)
			vs.add(String.format("%02d",i));
		FFile.mergeFiles(p.etbFolder, ".*MRG", fn);
		*/
		
		//String fd="/usr2/nlao/code_java/j3/run/nlsa/data/prepare";
		String fd="/usr2/nlao/data/resources/LDC/TREEBANK_3/PARSED/MRG/WSJ";
		//String fn=fd+"/all.1L.prep";
		//process(fd+"/all", true);
		

		//splitByTopTag(fn);
		//splitByLength(fn+".S");

		process(fd+"/21.MRG", false);
		process(fd+"/22.MRG", false);	//p.etbFolder
		process(fd+"/23.MRG", false);	
	}

	public static void countUnaryProduction(String oneLineParseFile){
		BufferedReader br =FFile.newReader(oneLineParseFile);
		MapSI m=new MapSI();
		String line = null;
		while ((line =FFile.readLine(br)) != null) {
			//this is fragile, whatever
			String type = line.substring(1, line.indexOf(' '));
			TreeSyntax tree=TreeSyntax.parseNew(line);
			for (int i=0;i<tree.vNode.size();++i){
				Node n= tree.getNode(i);
				if (n.vc.size()==1) continue;
				//tree.findUnaryAncestor(n);
				int len=0; String s=n.t.getPOS();
				while (n.iparent >=0){
					Node p=tree.getNode(n.iparent);
					if (p.vc.size()>1)
						break;
					n=p;
					++len;
					s+= "->"+n.t.getPOS();
				}
				if (len>=1)
					m.plusOn(s);					
			}
		}
		FFile.close(br);
		//MapVectorIS 
		TMapXVecX<Integer, String> mv=m.ValueKeyToMapVec();
		FFile.saveString(	oneLineParseFile +".unary",mv.join(": ", "\n"));
	}

	public static void countPunRules(String oneLineParseFile
			, String[] vsPunWords){
		SetS m = new SetS(vsPunWords);
		
		BufferedReader br =FFile.newReader(oneLineParseFile);
		MapSI mC=new MapSI();
		String line = null;
		while ((line =FFile.readLine(br)) != null) {
			String type = line.substring(1, line.indexOf(' '));
			TreeSyntax tree=TreeSyntax.parseNew(line);
			for (int i=0;i<tree.vNode.size();++i){
				Node n= tree.getNode(i);
				if (!n.isLeaf())		continue;
				String w=n.t.getText();
				if (!m.contains(w))		continue;
				
				String t=n.t.getPOS();
				mC.plusOn(t+">"+w);					
			}
		}
		FFile.close(br);
		mC.save(oneLineParseFile +".PunRule");
	}
	
	public static void reorderCorpus(String treeFile){
		//String treeFile=super.p.trainFile;
		//FFile.mkdirs(treeFile+".idx");
		
		System.out.println("reorder Corpus "+treeFile);
		
		/** first scan read in sentences, and index the word*/
		VectorX<TreeSyntax > vSent	=new VectorX<TreeSyntax >(TreeSyntax.class);
		BufferedReader br = FFile.newReader(treeFile);
		String line = null;int nSent=0;
		MapSVecI mvIndex= new MapSVecI();
		while ((line = FFile.readLine(br)) != null) {
			TreeSyntax tree=TreeSyntax.parseNew(line);
			if (tree==null) continue;
			vSent.addOn(tree);
			for (String w: tree.vWord)
				mvIndex.getC(w).addOn(nSent);
			++nSent;	
			//if (nSent % 100==0)			System.out.print("h");
		}
		FFile.close(br);
		
		
		
		/** sort the word in decreasing frequency */
		MapSI mFreq= (MapSI) mvIndex.getMInt("size");
		//mFreq.write(FFile.bufferedWriter(treeFile+".freq"));
		
		TMapIVecSa mvFreq =(TMapIVecSa) mFreq.ValueKeyToMapVec();
		//TMapVector<Integer,String> mv= mFreq.toMapVectorValueKey();		
		
		//VectorI vLen= new VectorI(vSent.size());
		VectorI vLen= vSent.getVI("length");
		
		VectorS vWord = (VectorS) mvFreq.toVectorV();//.reverseOn();
		VectorI vFreq = (VectorI) mvFreq.toVectorK();//.reverseOn();
		
		
		/** second scan sort the sentences, and print*/
		BufferedWriter bwF = FFile.newWriter(treeFile +".freq");
		BufferedWriter bw = FFile.newWriter(treeFile +".reorder");
		//for (String word: vWord ){
		for (int i=vWord.size()-1; i>=0; --i){
			FFile.write(bwF, vWord.get(i)+"\t"+vFreq.get(i)+"\n");
			
			VectorI vi = (VectorI) mvIndex.get(vWord.get(i));
			for (int iS: vi){
				int len=vLen.get(iS)-1;
				vLen.set(iS, len);
				if (len>0)continue;
				FFile.write(bw, vSent.get(iS).bracketString);
				FFile.write(bw, "\n");					
			}
		}
		FFile.close(bw);
		FFile.close(bwF);
		
		System.out.println("... done");
		return ;
	}

	public static void main(String[] args) {
		//FFile.writeReminder=1000;
		//processCTB();
		processETB();
		//countUnaryProduction("run/nlsa/count.unary/02-21");//S.0.0-29
		//	"D:/work/code_java\j3\run\nlsa\count.unary");
		//reorderCorpus("run/nlsa/run/02-21");
		//countPunRules("run/nlsa/tmp/02-21", pennPunctWords);//S.0.0-29
	}
}





