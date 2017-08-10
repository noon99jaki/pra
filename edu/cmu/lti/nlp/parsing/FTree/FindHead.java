/**
 * 
 */
package edu.cmu.lti.nlp.parsing.FTree;

import java.util.ArrayList;
import java.util.Map;
import java.util.TreeMap;

import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.CLang;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;

/**
 * @author nlao
 * you call this class, but it may decide to behave as another
 * using all static menbers seems not enough
 * resort to singleton
 */
public class FindHead {// extends ParseTree {
	public static FindHead getInstance() {
		return getInstance(p.lang);
	}
	public static FindHead getInstance(String lang) {
		if (lang.equals(CLang.zh_CN)) 
			return FindHeadChB.getInstance();
		if (lang.equals(CLang.en_US)) 
			return FindHeadEn.getInstance();
		return null;
	}
	public static class Param extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String lang;
		public void parse() {
			//lang = System.getProperty("lang");
			//if (lang == null) lang = "en";
			lang = getString("lang", CLang.zh_CN);
		}

		public Param() {
			super(FindHead.class);
			parse();
		}
	}
	public static Param p = new Param();

	protected FindHead() {
		if (default_rule == null)
			initHeadRules();
	}
	
	//public static String _head = "head";
	//public static String _head_child = "head_child";
	public static String _left = "left";
	public static String _right = "right";
	public static String _rightdis = "rightdis";
	public static String _leftdis = "leftdis";
	public static String _pos = "pos";

	protected String[] _default_rule = null;
	protected Rule default_rule = null;//new Rule();//

	protected TreeMap<String, String[][]> m_head_rules 
		=  new TreeMap<String, String[][]>();

	protected TreeMap<String, ArrayList<Rule>> mv_head_rules 
		= new TreeMap<String, ArrayList<Rule>>();

	class Rule {
		TreeMap<String, Integer> m = new TreeMap<String, Integer>();
		String type;
		String dir;
		boolean priority;
		public void from(String[] rule) {
			type = rule[0];
			// for (Map.Entry <String, String> e : entrySet()) {
			if (type.equals(_left)) {
				dir = _left;
				priority = true;
			} else if (type.equals(_right)) {
				dir = _right;
				priority = true;
			} else if (type.equals(_leftdis)) {
				dir = _left;
				priority = false;
			} else if (type.equals(_rightdis)) {
				dir = _right;
				priority = false;
			} else {
				throw new RuntimeException(
						"ERROR: invalid direction type to nonTerminalInfo map in AbstractCollinsHeadFinder.");
			}

			for (int i = 1; i < rule.length; i++) {
				if (!m.containsKey(rule[i]))
					m.put(rule[i], i);
			}
		}
	}
	public void initHeadRules() {
		default_rule = new Rule();
		default_rule.from(_default_rule);
		mv_head_rules = new TreeMap<String, ArrayList<Rule>>();
		for (Map.Entry<String, String[][]> e : m_head_rules.entrySet()) {
			String pos = e.getKey();
			String[][] vrule = e.getValue();
			ArrayList<Rule> v = new ArrayList<Rule>();
			for (String[] r : vrule) {
				Rule rule = new Rule();
				rule.from(r);
				v.add(rule);
			}
			mv_head_rules.put(pos, v);
			// if (!mv_head_rules.(arg0))
		}
	}

	//private int findHeadChild( VectorI vc, Rule r) {	}
	
	/**
	 * VectorS vpos: pos of the children
	 * Rule r:  a rule
	 */
	protected int findHeadChild( VectorS vpos, Rule r) {
		int idx = -1;
		if (r.m.size()==0){
			if (r.dir.equals(_right))
				return 0;//id = vc.lastElement();
			else
				return vpos.size()-1;//return id = vc.firstElement();
		}
		
		//VectorS vpos= (VectorS) t.v_node.getVString("pos", vc);
		VectorI vi = VectorI.seq(vpos.size());
		if (r.dir.equals(_right)) vi.reverseOn();
	
		int priority = 9999;
		for (int i: vi){
//			for (int i = 0; i < vc.size(); ++i) {		
			//int id = v.get(i);		Node c = t.getNode(id);
			String posc= vpos.get(i);
			if (!r.m.containsKey(posc))
				continue;
			if (!r.priority)
				return i;// c.text;
	
			int pri = r.m.get(posc);
			if (pri >= priority)
				continue;
			priority = pri;
			idx = i;// c.text;
		}
		// headIdx = postOperationFix(headIdx, daughterTrees);
		return idx;
	}
	
	/**	 * 
	 * @param n
	 * @return : index of the head child
	 */
	protected int findHeadChild(Node n) {
		return findHeadChild(n.getString(_pos)
			,(VectorS) t.vNode.getVS("pos", n.vc) );
	}
	
	/**
	 * 
	 * @param pos: pos of the mother
	 * @param vpos: pos of the children
	 * @return: index of the head child
	 */
	protected int findHeadChild(String pos, VectorS vpos) {
		//ugly hack to deal with star marked pos
		if (pos.startsWith(CTag.star))
			pos =pos.substring(1);
		for (int i=0; i<vpos.size();++i)
			if (vpos.get(i).startsWith(CTag.star)){
				return i;
			//	vpos.set(i,vpos.get(i).substring(1));
			}
		
		int idx=-1;
		if (vpos.size()==0) return -1;
		if (vpos.size()==1) return 0;//vc.get(0);
		
		//if (pos.equals("NONE")) return null;
		ArrayList<Rule> v_rule = mv_head_rules.get(pos);
		if (v_rule != null) {
			for (Rule r : v_rule) {
				idx = findHeadChild( vpos, r);
				if (idx>=0)	return idx;
			}
		}
		else{
			System.err.println("Warning: No rule found for " +pos
					+ " (first char: " + pos.charAt(0) + ")");
			System.err.println("Known nonterms are: "
					+ m_head_rules.keySet());
		}
		idx = findHeadChild(vpos, default_rule);
		return idx;
	}

	public TreeParse t;
	
	public void setHead(int id){//Node n){
		Node n= t.getNode(id);
		int idx= findHeadChild(n);
		if (idx >=0){
			int ih = n.vc.get(idx);
			n.putInt(CTag.headChildIdx,idx);
			n.putInt(CTag.headChild,ih);
			n.putInt(CTag.head,t.getNode(ih).getInt(CTag.head));
		}
		else{
			n.putInt(CTag.head,id);				
			n.putInt(CTag.headChild,id);				
		}		
	}
	//assume head first tree
	public boolean findHead(TreeParse _t) {
		t = _t;
		//for (Tree.Node n : t.v_node) {
		for (int i=t.vNode.size()-1; i>=0; --i){			
			setHead(i);
		}
		return true;
	}
	public void setHead(TreeParse tree, int id){//Node n){
		t=tree;
		setHead(id);
	}


}
