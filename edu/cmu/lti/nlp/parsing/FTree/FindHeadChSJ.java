package edu.cmu.lti.nlp.parsing.FTree;

/**
 * A headfinder for Chinese based on rules described in Sun/Jurafsky NAACL '04. *
 * @author Galen Andrew
 * @version Jul 12, 2004  
 * * @author nlao *
 */
public class FindHeadChSJ extends FindHead{
	//singlten pattern with lazy loading
	private static  FindHeadChSJ instance =null;
	public static FindHeadChSJ getInstance() {
		if (instance==null) 	 instance = new FindHeadChSJ();			
		return instance;
	}
	
	public void initHeadRules(){
	    _default_rule = new String[]{_right};

	    m_head_rules.put("ROOT", new String[][]{{"left", "IP"}});
	    m_head_rules.put("PAIR", new String[][]{{"left", "IP"}});

	    m_head_rules.put("ADJP", new String[][]{{"right", "ADJP", "JJ", "AD"}});
	    m_head_rules.put("ADVP", new String[][]{{"right", "ADVP", "AD", "CS", "JJ", "NP", "PP", "P", "VA", "VV"}});
	    m_head_rules.put("CLP", new String[][]{{"right", "CLP", "M", "NN", "NP"}});
	    m_head_rules.put("CP", new String[][]{{"right", "CP", "IP", "VP"}});
	    m_head_rules.put("DNP", new String[][]{{"right", "DEG", "DNP", "DEC", "QP"}});
	    m_head_rules.put("DP", new String[][]{{"left", "M", "DP", "DT", "OD"}});
	    m_head_rules.put("DVP", new String[][]{{"right", "DEV", "AD", "VP"}});
	    m_head_rules.put("IP", new String[][]{{"right", "VP", "IP", "NP"}});
	    m_head_rules.put("LCP", new String[][]{{"right", "LCP", "LC"}});
	    m_head_rules.put("LST", new String[][]{{"right", "CD", "NP", "QP"}});
	    m_head_rules.put("NP", new String[][]{{"right", "NP", "NN", "IP", "NR", "NT"}});
	    m_head_rules.put("PP", new String[][]{{"left", "P", "PP"}});
	    m_head_rules.put("PRN", new String[][]{{"left", "PU"}});
	    m_head_rules.put("QP", new String[][]{{"right", "QP", "CLP", "CD"}});
	    m_head_rules.put("UCP", new String[][]{{"left", "IP", "NP", "VP"}});
	    m_head_rules.put("VCD", new String[][]{{"left", "VV", "VA", "VE"}});
	    m_head_rules.put("VP", new String[][]{{"left", "VE", "VC", "VV", "VNV", "VPT", "VRD", "VSB", "VCD", "VP"}});
	    m_head_rules.put("VPT", new String[][]{{"left", "VA", "VV"}});
	    m_head_rules.put("VCP", new String[][]{{"left"}});
	    m_head_rules.put("VNV", new String[][]{{"left"}});
	    m_head_rules.put("VRD", new String[][]{{"left", "VV", "VA"}});
	    m_head_rules.put("VSB", new String[][]{{"right", "VV", "VE"}});
	    m_head_rules.put("FRAG", new String[][]{{"right", "VV", "NN"}}); //FRAG seems only to be used for bits at the beginnings of articles: "Xinwenshe<DATE>" and "(wan)"

	    // some POS tags apparently sit where phrases are supposed to be
	    m_head_rules.put("CD", new String[][]{{"right", "CD"}});
	    m_head_rules.put("NN", new String[][]{{"right", "NN"}});
	    m_head_rules.put("NR", new String[][]{{"right", "NR"}});

	    // I'm adding these POS tags to do primitive morphology for character-level
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
