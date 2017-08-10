package edu.cmu.lti.nlp.parsing.FTree;



/**
 * A headfinder implementing Dan Bikel's head rules.
 * March 2005: Updated to match the head-finding rules found in
 *  Bikel's thesis (2004).
 *
 * @author Galen Andrew
 * @author Christopher Manning.
 * @author Ni Lao
 */
public class FindHeadChB  extends FindHead{
	//singlten pattern with lazy loading
	private static  FindHeadChB instance =null;
	public static FindHeadChB getInstance() {
		if (instance==null) 	 instance = new FindHeadChB();			
		return instance;
	}
	
	public void initHeadRules(){
	    _default_rule = new String[]{_right};
	    // ROOT is not always unary for chinese -- PAIR is a special notation
	    // that the Irish people use for non-unary ones....
	    m_head_rules.put("ROOT", new String[][]{{"left", "IP"}});
	    m_head_rules.put("PAIR", new String[][]{{"left", "IP"}});

	    // Major syntactic categories
	    m_head_rules.put("ADJP", new String[][]{{"right", "ADJP", "JJ"}, {"right", "AD", "NN", "CS"}});
	    m_head_rules.put("ADVP", new String[][]{{"right", "ADVP", "AD"}});

	    m_head_rules.put("CLP", new String[][]{{"right", "CLP", "M"}});
	    m_head_rules.put("CP", new String[][]{{"right", "DEC", "SP"}, {"left", "ADVP", "CS"}, {"right", "CP", "IP"}});

	    m_head_rules.put("DNP", new String[][]{{"right", "DNP", "DEG"}, {"right", "DEC"}});
	    m_head_rules.put("DP", new String[][]{{"left", "DP", "DT"}});
	    m_head_rules.put("DVP", new String[][]{{"right", "DVP", "DEV"}});

	    m_head_rules.put("FRAG", new String[][]{{"right", "VV", "NR", "NN"}});
	    m_head_rules.put("INTJ", new String[][]{{"right", "INTJ", "IJ"}});
	    m_head_rules.put("IP", new String[][]{{"right", "IP", "VP"}, {"right", "VV"}});
	    m_head_rules.put("LCP", new String[][]{{"right", "LCP", "LC"}});
	    m_head_rules.put("LST", new String[][]{{"left", "LST", "CD", "OD"}});

	    m_head_rules.put("NP", new String[][]{{"right", "NP", "NN", "NT", "NR", "QP"}});

	    m_head_rules.put("PP", new String[][]{{"left", "PP", "P"}});
	    m_head_rules.put("PRN", new String[][]{{"right", "NP", "IP", "VP", "NT", "NR", "NN"}});
	    m_head_rules.put("QP", new String[][]{{"right", "QP", "CLP", "CD", "OD"}});
	    m_head_rules.put("UCP", new String[][]{{"right"}});

	    m_head_rules.put("VP", new String[][]{{"left", "VP", "VA", "VC", "VE", "VV", "BA", "LB", "VCD", "VSB", "VRD", "VNV", "VCP"}});
	    m_head_rules.put("VCD", new String[][]{{"right", "VCD", "VV", "VA", "VC", "VE"}});
	    m_head_rules.put("VCP", new String[][]{{"right", "VCP", "VV", "VA", "VC", "VE"}});
	    m_head_rules.put("VRD", new String[][]{{"right", "VRD", "VV", "VA", "VC", "VE"}});
	    m_head_rules.put("VSB", new String[][]{{"right", "VSB", "VV", "VA", "VC", "VE"}});
	    m_head_rules.put("VNV", new String[][]{{"right", "VNV", "VV", "VA", "VC", "VE"}});
	    m_head_rules.put("VPT", new String[][]{{"right", "VNV", "VV", "VA", "VC", "VE"}}); // VNV typo for VPT? None of either in ctb4.
	    m_head_rules.put("WHNP", new String[][]{{"right", "WHNP", "NP", "NN", "NT", "NR", "QP"}});
	    m_head_rules.put("WHPP", new String[][]{{"left", "WHPP", "PP", "P"}});

	    // some POS tags apparently sit where phrases are supposed to be
	    m_head_rules.put("CD", new String[][]{{"right", "CD"}});
	    m_head_rules.put("NN", new String[][]{{"right", "NN"}});
	    m_head_rules.put("NR", new String[][]{{"right", "NR"}});
	    // parsing.  It shouldn't affect anything else because heads of preterminals are not
	    // generally queried - GMA
	    m_head_rules.put("VV", new String[][]{{"left"}});
	    m_head_rules.put("VA", new String[][]{{"left"}});
	    m_head_rules.put("VC", new String[][]{{"left"}});
	    m_head_rules.put("VE", new String[][]{{"left"}});
	    //ParseTreeFindHead.initHeadRules();
	    super.initHeadRules();
	}
	
}
