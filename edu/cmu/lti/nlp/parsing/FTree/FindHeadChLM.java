/**
 * 
 */
package edu.cmu.lti.nlp.parsing.FTree;

/**
 * HeadFinder for the Penn Chinese Treebank.  Adapted from
 * CollinsHeadFinder. This is the version used in Levy and Manning (2003).
  * @author nlao
 *
 */
public class FindHeadChLM extends FindHead{
	//singlten pattern with lazy loading
	private static  FindHeadChLM instance =null;
	public static FindHeadChLM getInstance() {
		if (instance==null) 	 instance = new FindHeadChLM();			
		return instance;
	}
	
	public void initHeadRules(){
	    _default_rule = new String[]{_right};

	    // ROOT is not always unary for chinese -- PAIR is a special notation
	    // that the Irish people use for non-unary ones....

	    // Major syntactic categories
	    m_head_rules.put("ADJP", new String[][]{{_left, "JJ", "ADJP"}}); // there is one ADJP unary rewrite to AD but otherwise all have JJ or ADJP
	    m_head_rules.put("ADVP", new String[][]{{_left, "AD", "CS", "ADVP", "JJ"}}); // CS is a subordinating conjunctor, and there are a couple of ADVP->JJ unary rewrites
	    m_head_rules.put("CLP", new String[][]{{_right, "M", "CLP"}});
	    //m_head_rules.put("CP", new String[][] {{left, "WHNP","IP","CP","VP"}}); // this is complicated; see bracketing guide p. 34.  Actually, all WHNP are empty.  IP/CP seems to be the best semantic head; syntax would dictate DEC/ADVP. Using IP/CP/VP/M is INCREDIBLY bad for Dep parser - lose 3% absolute.
	    m_head_rules.put("CP", new String[][]{{_right, "DEC", "WHNP", "WHPP"}}); // the (syntax-oriented) right-first head rule
	    // m_head_rules.put("CP", new String[][]{{right, "DEC", "ADVP", "CP", "IP", "VP", "M"}}); // the (syntax-oriented) right-first head rule
	    m_head_rules.put("DNP", new String[][]{{_right, "DEG"}}); // according to tgrep2, first preparation, all DNPs have a DEG daughter
	    m_head_rules.put("DP", new String[][]{{_left, "DT", "DP"}}); // there's one instance of DP adjunction
	    m_head_rules.put("DVP", new String[][]{{_right, "DEV"}}); // DVP always has DEV under it
	    m_head_rules.put("FRAG", new String[][]{{_right, "VV", "NN"}}); //FRAG seems only to be used for bits at the beginnings of articles: "Xinwenshe<DATE>" and "(wan)"
	    m_head_rules.put("INTJ", new String[][]{{_right, "INTJ", "IJ", "SP"}});
	    m_head_rules.put("IP", new String[][]{{_left, "IP", "VP"}});  // seems to cover everything
	    m_head_rules.put("LCP", new String[][]{{_right, "LC", "LCP"}}); // there's a bit of LCP adjunction
	    m_head_rules.put("LST", new String[][]{{_right, "CD", "PU"}}); // covers all examples
	    m_head_rules.put("NP", new String[][]{{_right, "NN", "NR", "NT", "NP", "PN", "CP"}}); // Basic heads are NN/NR/NT/NP; PN is pronoun.  Some NPs are nominalized relative clauses without overt nominal material; these are NP->CP unary rewrites.  Finally, note that this doesn't give any special treatment of coordination.

	    // add OD?
	    m_head_rules.put("PAIR", new String[][]{{_left, "IP"}});
	    m_head_rules.put("PP", new String[][]{{_left, "P", "PP"}}); // in the manual there's an example of VV heading PP but I couldn't find such an example with tgrep2
	    // cdm 2006: PRN changed to not choose punctuation.  Helped parsing (if not significantly)
	    // m_head_rules.put("PRN", new String[][]{{left, "PU"}}); //presumably left/right doesn't matter
	    m_head_rules.put("PRN", new String[][]{{_left, "NP", "VP", "IP", "QP", "PP", "ADJP", "CLP", "LCP"}, {_rightdis, "NN", "NR", "NT", "FW"}}); 
	    // cdm 2006: QP: add OD -- occurs some; occasionally NP, NT, M; parsing performance no-op
	    m_head_rules.put("QP", new String[][]{{_right, "QP", "CLP", "CD", "OD", "NP", "NT", "M"}}); // there's some QP adjunction
	    m_head_rules.put("ROOT", new String[][]{{_left, "IP"}});
	    m_head_rules.put("UCP", new String[][]{{_left, }}); //an alternative would be "PU","CC"

	    m_head_rules.put("VP", new String[][]{{_left, "VP", "VPT", "VV", "VA", "VC", "VE", "IP"}});//note that ba and long bei introduce IP-OBJ small clauses; short bei introduces VP
	    // add BA, LB, VCD, VSB, VRD, VNV, VCP as needed
	    // verb compounds
	    m_head_rules.put("VCD", new String[][]{{_left, "VCD", "VV", "VA", "VC", "VE"}}); // could easily be right instead
	    m_head_rules.put("VCP", new String[][]{{_left, "VCD", "VV", "VA", "VC", "VE"}}); // not much info from documentation
	    m_head_rules.put("VRD", new String[][]{{_left, "VCD", "VRD", "VV", "VA", "VC", "VE"}}); // definitely left
	    m_head_rules.put("VSB", new String[][]{{_right, "VCD", "VSB", "VV", "VA", "VC", "VE"}}); // definitely right, though some examples look questionably classified (na2lai2 zhi1fu4)
	    m_head_rules.put("VNV", new String[][]{{_left, "VV", "VA", "VC", "VE"}}); // left/right doesn't matter
	    m_head_rules.put("VPT", new String[][]{{_left, "VV", "VA", "VC", "VE"}}); // activity verb is to the left

	    // some POS tags apparently sit where phrases are supposed to be
	    m_head_rules.put("CD", new String[][]{{_right, "CD"}});
	    m_head_rules.put("NN", new String[][]{{_right, "NN"}});
	    m_head_rules.put("NR", new String[][]{{_right, "NR"}});

	    // I'm adding these POS tags to do primitive morphology for character-level
	    // parsing.  It shouldn't affect anything else because heads of preterminals are not
	    // generally queried - GMA
	    m_head_rules.put("VV", new String[][]{{_left}});
	    m_head_rules.put("VA", new String[][]{{_left}});
	    m_head_rules.put("VC", new String[][]{{_left}});
	    m_head_rules.put("VE", new String[][]{{_left}});	
	    //ParseTreeFindHead.initHeadRules();
	    super.initHeadRules();
	}
}
