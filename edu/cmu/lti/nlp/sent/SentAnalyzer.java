package edu.cmu.lti.nlp.sent;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.CLang;
import edu.cmu.lti.nlp.NLP;
import edu.cmu.lti.nlp.parsing.FTree.FParseTreeFeatures;
import edu.cmu.lti.nlp.parsing.FTree.FindHead;
import edu.cmu.lti.nlp.parsing.FTree.Trim;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;


/**
 * @author nlao
 * at Predicate mode, this data structure represent a predicate(clause)
 */
public abstract class SentAnalyzer {
	public static SentAnalyzer getInstance() {
		return getInstance(p.lang);
	}
	
	public static SentAnalyzer getInstance(String lang) {
		if (lang.equals(CLang.zh_CN)) {
			return SentAnalyzerCh.getInstance();
		} 
		else if (lang.equals(CLang.ja_JP)) {
			return SentAnalyzerJp.getInstance();
		} 
		else if ( lang.equals(CLang.en_US) ) {
			return SentAnalyzerEn.getInstance();
		}
		return null;
	}
	public String lang;
	public FindHead fh=null;
	public SentAnalyzer(String lang){
		this.lang = lang;
		nlp = NLP.getInstance(lang);
		fh=FindHead.getInstance(this.lang);
	}
	public static class CTag{
		public static final String E = "E";//entity id
		public static final String P = "P";//this is pronoun 
		public static final String Q = "Q";//this is question 
		public static final String text = "text";//
		public static final String pos = "pos";//overall pos
		public static final String posF = "posF";//pos of the first word
		public static final String posR = "posR";//pos of the root
		public static final String posH = "posH";//pos of the head
		public static final String apposition="apposition";
		public static final String possession="pessession";//fix it
		
		
		public static final String family="family";
		public static final String past="past";
		public static final String born="born";
		public static final String die="die";
		public static final String be2="be2";
		public static final String be1="be";//fix it
		
		public static final String lead_to="lead_to";
		public static final String cause="cause";
		public static final String because="because";
		public static final String during="during";
		public static final String therefore="therefore";
		public static final String effect="effect";
		public static final String and="and";
		public static final String if_="if_";
		public static final String moreover="moreover";
		public static final String to="to";

		public static final String called="called";
		public static final String is="is";
		public static final String one_of="one_of";
		public static final String event="event";

		public static final String[] vFT=
			new String[]{
			family,past,born,die,be2,be1,
			lead_to,cause,because,during,therefore,effect
			,and,if_,moreover,to
			,called,is,one_of,event

		};

		public static final String KT1="KT1";
		public static final String KT2="KT2";
		public static final String KT3="KT3";
		public static final String KT4="KT4";

		public static final String InitCap="InitCap";	//a word starts with capital letter
		public static final String syn="syn";	//synonym

		public static final String BeginDoc="ParaFirst";//the first sentence/clause of a document
		public static final String BeginSent="BeginSent";//the first sentence of a paragraph
		public static final String BeginPara="BeginPara";//the first clause of a sentence
		

		public static final String ANAPH="ANAPH";
		public static final String COREF="COREF";
		public static final String SUBJ="SUBJ";
		
	}
	
	public NLP nlp;


	
	public static class Param	extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String lang;
		//public boolean caching;
		public int dbg;
		
		//public boolean shallow;
		//public boolean caching;
		public Param(Class c) {
			super(c);
			parse();
		}
		
		public void parse(){	
			lang = getString("lang","zh_CN");
			//caching=getBoolean("caching", true);
			dbg=getInt("dbg", 0);

		}	
	}	
	public static Param p = new Param(SentAnalyzer.class);
	
	


	//protected SetS m_questionDet = new SetS(); 
	//protected SetS m_questionPron = new SetS(); 

	/*
	 * 		//TreeDep treeDep= sa.treeSyx.toDepTree();
		//if (p.dbg>0) System.out.print(treeDep);
		//treeDep.reorder();
		//if (p.dbg>0) System.out.print(treeDep);

	//if (p.toDepTree==1)			
sa.treeDep= sa.treeSyx.toDepTree();
if (p.dbg>0) System.out.print(sa.treeDep);
sa.treeDep.reorder();
if (p.dbg>0) System.out.print(sa.treeDep);

//sa.treeSyx.doAssembleText();
//if (p.truncateToPhrase==1)
sa.treeSyxPhrase = sa.treeSyx.clone();
Trim.truncateToPhrase(sa.treeSyxPhrase);
FindHead.getInstance().findHead(sa.treeSyxPhrase);
if (p.dbg>0) System.out.print(sa.treeSyxPhrase.toString());
*/

/*
TreeSyntax treeSyxPhrase = sa.treeSyx.clone();
Trim.truncateToPhrase(treeSyxPhrase, isQuestion);
FindHead.getInstance().findHead(treeSyxPhrase);
if (p.dbg>0) System.out.print(treeSyxPhrase.toString());

sa.treeDepPhrase= treeSyxPhrase.toDepTree();

if (p.dbg>0) System.out.print(sa.treeDepPhrase);
sa.treeDepPhrase.reorder();*/
	//sa.treeDepPhrase = sa.treeDep.clone();
	//Trim.truncateToPhrase(sa.treeDepPhrase);
	public static void tagEntities(SentAnalysis sa){
		TreeParse t = sa.treeSyx;
		sa.vi_entity.clear();
		//TreeParse t = sa.treeSyx;
		for (int i=0; i<t.vNode.size(); ++i){
			Node n = t.getNode(i);
			//if (!n.isLeaf()) continue;
			if (!n.t.getPOS().equals("NP")) continue;
			//n.put(CTag.E, "E"+sa.vi_entity.size());
			n.put(CTag.E, "E"+i);
			sa.vi_entity.add(i);
		}	
		return;
	}

	public SetS m_QWord = new SetS(); //question word


	public static void findAndTag(TreeParse t,
			SetS m, String tag, boolean tagStack){
		// = sa.treeDepPhrase;
		for (int i=0; i<t.vWord.size(); ++i){
			String w = t.vWord.get(i);
		//for (String w: t.v_word){
			if (! m.contains(w)) continue;
			
			int id = t.viNode.get(i);
			if (tagStack)
				t.addFeature2Stack(id, tag);	
			else
				t.getNode(id).add(tag);
		}
		return;
	}


	

	public static boolean tagQuestionEntity(SentAnalysis sa){
		TreeParse t = sa.treeSyx;
		for (int i=1; i< sa.vi_entity.size(); ++i){
			Node n = t.getNode(i);
			if (n.t.m.contains(CTag.Q)){
				sa.tFeature.mi.put("i_QEntity", i);
				//sa.i_QEntity = i;
				return true;
			}
		}		
		//sa.i_QEntity = sa.vi_entity.size()-1;
		sa.tFeature.mi.put("i_QEntity", sa.vi_entity.size()-1);
		return false;
	}
	FParseTreeFeatures ptf= new FParseTreeFeatures();
	public void tagPathFeature(SentAnalysis sa,String tagNode, String tagPath){
		TreeParse t = sa.treeSyx;
		ptf.t=t;
		VectorS vs=t.vNode.getVS(tagNode, sa.vi_entity);
		for (int i=1; i< sa.vi_entity.size(); ++i){
			ptf.findPath(sa.vi_entity.get(i-1), sa.vi_entity.get(i));
			String path = ptf.getPathFeature(tagPath);
			String pathE = String.format("(%s)%s(%s)"
				,vs.get(i-1), path, vs.get(i));
			sa.tFeature.m.add(pathE);
		}
	}
	/**
	 * Light weight analysis without parsing 
	 * @param sent
	 * @return sentence analysis 
	 * @author hideki
	 */
/*	public SentAnalysis analyzeSent( String sent ){
		sa = new SentAnalysis(sent);
		sa.v_token = new VectorToken( nlp.segWord( sent ));
		//this.sa = sa;
		return sa;
	}*/

	/**
	 * generate pharse level dep tree
	 */
	public SentAnalysis analyzeSent(String sent, boolean isQuestion){
		TreeSyntax  tree = nlp.synxParseSent(sent);
		sa = analyzeSent(sent, new VectorToken(nlp.vt), tree);
		if (isQuestion)
			this.analyzeQuestion(sa);
		return sa;
	}
	
//	public String getSent(VectorS v_word){		return v_word.join("");	}
	//support processing subclass of SentAnalysis
	public SentAnalysis sa;//TODO: remove it to make code OO
	public SentAnalysis analyzeSent(
			String sent, VectorToken vt, TreeSyntax treeSyx){
		sa =  new SentAnalysis(sent);		
		try{
			if (vt==null) return sa;
			tagShallowFeatures(vt);
			
			if (treeSyx==null) return sa;
			sa.treeSyx = treeSyx;//.clone();		

			if (treeSyx.vNode==null) return sa;
			tagDeepFeatures(treeSyx);
		
	  } catch ( Exception e ) {	 //wrap filthy stuff here 	
			e.printStackTrace();
			System.err.println("error analysing sentence");
			System.err.println(sent);
			System.err.println(vt);
			System.err.println(treeSyx);
		}
		return sa;
	}	
	//features that need only pos
	protected void tagShallowFeatures(VectorToken vt){
		sa.v_token = vt;
		if (vt.size()==0) return;
		sa.tFeature.ms.put(CTag.posF, vt.get(0).getPOS());

	}

	protected void tagDeepFeatures(TreeSyntax treeSyx){
		if ( treeSyx == null ) return; 
		sa.treeSyx = treeSyx;//.clone();		
		if (treeSyx.vWord.size()==0) return;
		
		if (treeSyx.getRoot().ib==-1)//I dont know where this bug come from
			treeSyx.setSpans(true);
		
		fh.findHead(sa.treeSyx);			//t.addHeadFeatures();
		if (p.dbg>0) System.out.print(sa.treeSyx);		
		
		sa.treeDepPhrase= sa.treeSyx.toDepTree();
		if (p.dbg>0) System.out.print(sa.treeDepPhrase);
		sa.treeDepPhrase.reorder();
		if (p.dbg>0) System.out.print(sa.treeDepPhrase);
		Trim.getInstance().trim2Phrase(sa.treeDepPhrase, false);// isQuestion);
		if (p.dbg>0)  System.out.print(sa.treeDepPhrase);
		
		Node r = sa.treeSyx.getRoot();
		Node h = sa.treeSyx.getNode(r.getHead());
		
		sa.tFeature.ms.put(CTag.posR, r.t.getPOS());
		sa.tFeature.ms.put(CTag.posH, h.t.getPOS());
		
		//tagPathFeature(sa.treeSyx, CTag.E, CTag.pos);
		//tagPathFeature(sa.treeSyx, CTag.text, CTag.pos);
		//try{}	catch(Exception e){			e.printStackTrace();			return null;		}
		tagEntities(sa);

		return;
	}
	
	protected void tagQuestionWord(SentAnalysis sa){
		//findAndTag(sa.treeSyx,m_QWord,  CTag.Q, false);
		findAndTag(sa.treeSyx,m_QWord,  CTag.Q, false);
		
	}
	public SentAnalysis analyzeQuestion(SentAnalysis sa){
		tagQuestionWord(sa);
		tagQuestionEntity(sa);			
		return sa;
	}
}
