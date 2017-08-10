/**
 * Language independent NLP module
 *  Develop an umbrella class for all NLP methods. Intend to be easily parameterized 
 *  by language. So QA algorithms in J3 can be mostly language independent
 *  Ni Lao 2008.3.3
 */
package edu.cmu.lti.nlp;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VecVecS;
import edu.cmu.lti.nlp.Interfaces.INLP;
import edu.cmu.lti.nlp.Interfaces.ISegWord;
import edu.cmu.lti.nlp.Interfaces.ISynxParseTaggedSent;
import edu.cmu.lti.nlp.Interfaces.ITagNE;
import edu.cmu.lti.nlp.Interfaces.ITagPOS;
import edu.cmu.lti.nlp.Interfaces.Tag;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.nlp.sent.SentAnalysis;

/**
 * you may want to directly use the nlp tools 
 * (e.g. parser, tagger, segmentor etc.)
 * but here we provide quick fix mechanism to all NLP operations
 * (e.g. fixSeg, fixTag, fixParse etc.)
 * @author nlao
 *
 */
public abstract class NLP implements INLP{
	/**
	 * TODO: should let caller to choose language, or directly choose lang based on Param?
	 * since people can always do NLPCh.getInstance()
	 * I will not let caller to choose here
	 * no, just give them both choices?
	 */
	public static NLP getInstance() {
		return getInstance(Param.ms.get("lang"));//p.lang);
	}
	public static NLP getInstance(String lang) {
		if (lang==null) lang = CLang.zh_CN;
		if (lang.equals(CLang.zh_CN))	return NLPCh.getInstance();
		//if (lang.equals(CLang.ja_JP))	return NLPJp.getInstance();			
		if (lang.equals(CLang.en_US))	return NLPEn.getInstance();			
		return null;
	}	
	
	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String lang;
		public boolean refinePOS;
		public boolean refineSeg;
		public boolean refineSegByNE;
		
		public Param() {
			super(NLP.class);
			parse();
		}
		
		@Override
		public void parse(){	
			lang=getString("lang", CLang.zh_CN);
			refinePOS=getBoolean("refinePOS", true);
			refineSeg=getBoolean("refineSeg", true);
			refineSegByNE=getBoolean("refineSegByNE", false);//true);
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append( "lang = "+lang+"\n" );
			sb.append( "refinePOS = "+refinePOS+"\n" );
			sb.append( "refineSeg = "+refineSeg+"\n" );
			sb.append( "refineSegByNE = "+refineSegByNE+"\n" );
			return sb.toString();
		}
	}	
	public Param p=new Param();
	//public static Param p;//=new Param();
	protected NLP(){
		//p=new Param();
	}
	


	protected VecVecS vvs_fixSeg = new VecVecS();//to merge
	//protected MapVectorSS mvs_fixSeg = new MapVectorSS();//to split
	protected VectorToken refineSeg(VectorToken vt){
		VectorS vs = vt.getVS("text");
		for (VectorS pat: vvs_fixSeg){			
			int idx=0;
			for(;true;){
				//to find
				idx= vs.findSeq(pat, idx);
				if (idx<0) break;
				
				//to merge
				String txt = pat.join("");
				vt=new VectorToken(
						vt.replace(idx, pat.size(), new Token(txt)));
				vs=(VectorS) vs.replace(idx, pat.size(), txt);
				//vt.get(idx).setPOS(m_fixTag.get(txt));
			}		
		}
    return vt;
  }
  /** code for reconstructing segmenter output based on BBN and wiki and lexicon */
  protected VectorToken refineSegByNE(VectorToken vt){
  	String input = vt.getVS("text").join(" ");     
    VectorX<Tag> vNE = taggerNE.tagNE(input);
	  return refineSegbyTag(vt, vNE);
	}  

	//refine MSRSeg based on BBN Named Entity
	protected VectorToken refineSegbyTag(
			VectorToken vt, VectorX<Tag> vTag) {
    if(vTag.size() == 0)
    	return vt; 

    VectorToken vt1 = new VectorToken();
    
    StringBuffer txtRaw = new StringBuffer();
    int iNE=0;
    for (int i=0; i< vt.size(); ++i){
    	int ib = txtRaw.length();
    	Tag tag = vTag.get(iNE);
    	if (tag.ib != ib){//normally just copy
    		vt1.add(vt.get(i));
    		continue;
    	}
    	
    	Token t = new Token(tag.text);
    	t.put(CTag.NE, tag.type);
    	vt1.add(t);
  		while(true){ //skip several tokens
      	//Token t =vt.get(i) ;
      	txtRaw.append(vt.get(i).getText()+" ");
      	int ie = txtRaw.length();
      	if (ie>= tag.ie) break;
  		}
    }
    return vt1;
  }
	/*			
  for(int i=0; i<vt.size(); ++i){
  	Token t= vt.get(i);
  	String pos = m_fixTag.get(t.getText());
  	if (pos != null)t.setPOS(pos);
  }*/ 
	
	ISegWord segmentor= null;
	/**
	 * Segment a given sentence into tokens (i.e. words)
	 * @param sent
	 * @return segmented sentence as a token vector
	 */
	public VectorToken segWord( String sent ){	 
		//	dont do it, since we cannot fix the corpus?
		//its OK, since we can create query like #2(A B C)
		System.out.print("s");
		vt =segmentor.segWord(sent);
		if (p.refineSeg)		vt = refineSeg(vt);
		if (p.refineSegByNE)	vt = refineSegByNE(vt);		
		return vt;
	}
	public VectorX<VectorToken> tagDoc( String doc ){	 
		VectorX<VectorToken> vvt =segDoc(doc);
		for (int i=0; i< vvt.size(); ++i){
			vvt.set(i, tagPOS(vvt.get(i)));
		}
		return vvt;
		
	}
	public VectorX<VectorToken> segDoc( String doc ){	 
		VectorX<VectorToken> vvt = new VectorX<VectorToken>(VectorToken.class);
		System.out.print("s");
		vt =segmentor.segWord(doc);
		if (p.refineSeg)		vt = refineSeg(vt);
		if (p.refineSegByNE)	vt = refineSegByNE(vt);
		VectorI vi = 	vt.getVS(CTag.text).idxMatches(regexBreakSent);
		//vi.pushFrontOn(-1);
		for (int i=0; i<vi.size(); ++i){
			if (i==0)
				vvt.add(new VectorToken(vt.sub(0,vi.get(i)+1)));
			else 
				vvt.add(new VectorToken(vt.sub(vi.get(i-1)+1,vi.get(i)+1)));
		}
		return vvt;
	}
	
	public String regexBreakSent = null; 
	public VectorS breakSent(String text) {
		// [\s] is equivalent to [ \t\n\x0B\f\r] 		
		text.replaceAll("\\s+", " ");		
		VectorS vs= new VectorS(text.split(regexBreakSent));
		VectorS vs1= new VectorS();
		vs1.ensureCapacity(vs.size());
		for ( String s : vs ){
			String x = s.trim();
			if (x.length()>=1)
				vs1.add(x);
		}
		return vs1;
		//return FString.splitKeep(text, rgxBreakSent);
	}	
	
	/**
	 * Unsegment text considering English phrases mixed into asian language
	 * @param text
	 * @return unsegmented text
	 */
	public static String unsegWord( String text ) {
		return text.replaceAll("(?<![\\p{Ll}\\p{Lu}]) (?![\\p{Ll}\\p{Lu}])", "");
	}
	
	SetS msNoun = new SetS("NN NR CD ");
	/**
	 * Segment a given sentence and extract NPs as a vector of tokens (i.e. words)
	 * @param text
	 * @return NPs as a token vector
	 */
	public VectorX<Token> extractNP( SentAnalysis sa ) {		
		VectorS vpos = sa.v_token.getVS(CTag.pos);
		VectorI vi = vpos.idxIn(msNoun);
		return sa.v_token.sub(vi);
	}

	protected MapSS m_fixPOS=new MapSS();
	protected VectorToken refinePOS(VectorToken vt) {	
    for(Token t: vt){
    	String pos = m_fixPOS.get(t.getText());
    	if (pos != null)
    		t.setPOS(pos);
    } 
    return vt;
	}
	ITagPOS taggerPOS=null;
	ITagNE taggerNE=null;
	
	public VectorToken tagPOS(VectorToken vt ) {
		// In case POS analysis is not implemented/enabled
		if ( taggerPOS == null ) 
			return null;		
		if (p.refinePOS)
			return refinePOS(taggerPOS.tagPOS(vt));
		else
			return taggerPOS.tagPOS(vt);
	}	
	public VectorToken tagPOS(String sent) {
		if (sent==null)	
			return null;
		return tagPOS(segWord(sent));
	}
	
	
	public ISynxParseTaggedSent parser = null;
	public TreeSyntax synxParseTaggedSent(VectorToken vt) {
		// In case a parser is not implemented/enabled
		if ( parser == null ) return null;		
		if (vt.size()==0) return new TreeSyntax();
		System.out.print("p");
		return parser.synxParseTaggedSent(vt);		
	}	
	
	public VectorToken vt ;//as an embeded output
	public TreeSyntax synxParseSent(String sent) {
		if (sent.equals("")) return new TreeSyntax();
		return synxParseTaggedSent(tagPOS(sent));
	}

/*	public ParsedText parseText(String text) {
		ParsedText pt = new ParsedText();
		VectorS vs= breakSent(text);
		for (String txt: vs){
			pt.vTree.add(synxParseSent(txt));
		}
		return pt;
	}		
*/

	
}
