/**
 * 
 */
package edu.cmu.lti.nlp.parsing.FTree;

/**
 * @author nlao
 *
 */

public class FindHeadEn extends FindHead{
	//singlten pattern with lazy loading
	private static  FindHeadEn instance =null;
	public static FindHeadEn getInstance() {
		if (instance==null) 	 instance = new FindHeadEn();			
		return instance;
	}
	
	public void initHeadRules(){
	    _default_rule = new String[]{_left};
	    m_head_rules.clear();
	    // This version from Collins' diss (1999: 236-238)
	    m_head_rules.put("ADJP", new String[][]{{"left", "NNS", "QP", "NN", "$", "ADVP", "JJ", "VBN", "VBG", "ADJP", "JJR", "NP", "JJS", "DT", "FW", "RBR", "RBS", "SBAR", "RB"}});
	    m_head_rules.put("ADVP", new String[][]{{"right", "RB", "RBR", "RBS", "FW", "ADVP", "TO", "CD", "JJR", "JJ", "IN", "NP", "JJS", "NN"}});
	    m_head_rules.put("CONJP", new String[][]{{"right", "CC", "RB", "IN"}});
	    m_head_rules.put("EDITED", new String[][] {{"left"}});  // crap rule for Switchboard (if don't delete EDITED nodes)
	    m_head_rules.put("FRAG", new String[][]{{"right"}}); // crap
	    m_head_rules.put("INTJ", new String[][]{{"left"}});
	    m_head_rules.put("LST", new String[][]{{"right", "LS", ":"}});
	    m_head_rules.put("NAC", new String[][]{{"left", "NN", "NNS", "NNP", "NNPS", "NP", "NAC", "EX", "$", "CD", "QP", "PRP", "VBG", "JJ", "JJS", "JJR", "ADJP", "FW"}});
	    m_head_rules.put("NP", new String[][]{{"rightdis", "NN", "NNP", "NNPS", "NNS", "NX", "POS", "JJR"}, {"left", "NP"}, {"rightdis", "$", "ADJP", "PRN"}, {"right", "CD"}, {"rightdis", "JJ", "JJS", "RB", "QP"}});
	    m_head_rules.put("NX", new String[][]{{"left"}}); // crap
	    m_head_rules.put("PP", new String[][]{{"right", "IN", "TO", "VBG", "VBN", "RP", "FW"}});
	    // should prefer JJ? (PP (JJ such) (IN as) (NP (NN crocidolite)))
	    m_head_rules.put("PRN", new String[][]{{"left"}});
	    m_head_rules.put("PRT", new String[][]{{"right", "RP"}});
	    m_head_rules.put("QP", new String[][]{{"left", "$", "IN", "NNS", "NN", "JJ", "RB", "DT", "CD", "NCD", "QP", "JJR", "JJS"}});
	    m_head_rules.put("RRC", new String[][]{{"right", "VP", "NP", "ADVP", "ADJP", "PP"}});
	    m_head_rules.put("S", new String[][]{{"left", "TO", "IN", "VP", "S", "SBAR", "ADJP", "UCP", "NP"}});
	    m_head_rules.put("S1", new String[][]{{"left"}});
	    m_head_rules.put("SBAR", new String[][]{{"left", "WHNP", "WHPP", "WHADVP", "WHADJP", "IN", "DT", "S", "SQ", "SINV", "SBAR", "FRAG"}});
	    m_head_rules.put("SBARQ", new String[][]{{"left", "SQ", "S", "SINV", "SBARQ", "FRAG"}});
	    m_head_rules.put("SINV", new String[][]{{"left", "VBZ", "VBD", "VBP", "VB", "MD", "VP", "S", "SINV", "ADJP", "NP"}});
	    m_head_rules.put("SQ", new String[][]{{"left", "AUX","VBZ", "VBD", "VBP", "VB", "MD", "VP", "SQ"}});
	    m_head_rules.put("TYPO", new String[][] {{"left"}}); // another crap rule, for Brown (Roger)
	    m_head_rules.put("UCP", new String[][]{{"right"}});
	    m_head_rules.put("VP", new String[][]{{"left", "TO", "VBD", "VBN", "MD", "VBZ", "VB", "VBG", "VBP", "AUX", "AUXG", "VP", "ADJP", "NN", "NNS", "NP"}});
	    m_head_rules.put("WHADJP", new String[][]{{"left", "CC", "WRB", "JJ", "ADJP"}});
	    m_head_rules.put("WHADVP", new String[][]{{"right", "CC", "WRB"}});
	    m_head_rules.put("WHNP", new String[][]{{"left", "WDT", "WP", "WP$", "WHADJP", "WHPP", "WHNP"}});
	    m_head_rules.put("WHPP", new String[][]{{"right", "IN", "TO", "FW"}});
	    m_head_rules.put("X", new String[][]{{"right"}}); // crap rule
	    m_head_rules.put("XS", new String[][] {{"right", "IN"}}); // rule for new structure in QP
	    //ParseTreeFindHead.initHeadRules();
	    super.initHeadRules();
	}
		  /**
	   * Go through trees and determine their heads and print them.
	   * Just for debuggin'. <br>
	   * Usage: <code>
	   * java edu.stanford.nlp.trees.CollinsHeadFinder treebankFilePath
	   * </code>
	   *
	   * @param args The treebankFilePath
	   */
	/*
	 * 
	  public static void main(String[] args) {
	    Treebank treebank = new DiskTreebank();
	    CategoryWordTag.suppressTerminalDetails = true;
	    treebank.loadPath(args[0]);
	    final HeadFinder chf = new CollinsHeadFinder();
	    treebank.apply(new TreeVisitor() {
	      public void visitTree(Tree pt) {
	        pt.percolateHeads(chf);
	        pt.pennPrint();
	        System.out.println();
	      }
	    });
	    */
	 
}
