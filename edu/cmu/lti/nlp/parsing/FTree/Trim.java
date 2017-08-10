/**
 * 
 */
package edu.cmu.lti.nlp.parsing.FTree;

import java.util.Map;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorB;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;

/**
 * @author nlao
 *
 */
public class Trim {
	private static  Trim instance =null;
	public static Trim getInstance() {
		if (instance==null) 	 instance = new Trim();			
		return instance;
	}
	
	/**
	 * merge leafs to phrase level
	 */
	public static SetS m_phrase_pos = new SetS(
			new String[]{"NP", "VP","PP","ADJP","ADVP","S","SBAR"});

	protected static SetS m_move_up_feature = new SetS(
			new String[]{"srl"});

	public Trim(){
		//if (m_phrase_pos.size()==0)  initPhrasePos();		
		//if (m_move_up_feature.size()==0)  initMoveUpFeature();		
	}
	
	
	protected static void moveFeatureUp(Node n, TreeParse t){
		if (n.iparent <0) return;;
		Node p = t.getNode(n.iparent);
		for ( Map.Entry<String,String> e : n.t.ms.entrySet() ) {
			//for (Map.Entry <String, String> e : entrySet())  {			
			String key = e.getKey();
			String val = e.getValue();
			if (! m_move_up_feature.contains(key))	continue;			
			if (p.t.ms.containsKey(key)) continue;
			p.put(key, val);
		}					
		return;
	}

	
	
	public VectorB vkeep;
	public TreeParse tree;
	/**
	 * @isQuestion do top-down approach for questions
	 */
	public VectorB trim2Phrase(TreeParse _tree, boolean isQuestion) {
		tree=_tree;	
		int k= tree.vNode.size();
		vkeep= new VectorB(k, true);
		shrinkNodesWithTag(CTag.pos, "NP", isQuestion);
		tree.doTruncate(vkeep);
		tree.resetTexts();
		return vkeep;
	}

	protected void deleteNodeRecur(int id){
		Node n = tree.getNode(id);
		for (int ic: n.vc) 	deleteNodeRecur(ic) ;
		vkeep.set(id,false);
	}
	
	/**
	 * find all NP tags and delete nodes below them
	 * @aggresive whether to keep NP which already dominate some other NP
	 **/
	protected VectorB shrinkNodesWithTag(	
			String name, String value, boolean aggresive){
		shrinkNodesWithTagRecur(name,  value, tree.root, aggresive);
		return vkeep;
	}	
	/**
	 * 
	 * @param name
	 * @param value
	 * @param id
	 * @return if nodes under id actually has tag name=value
	 */
	protected boolean shrinkNodesWithTagRecur(
			String name, String value, int id, boolean aggresive) {
		Node n = tree.getNode(id);
		
		boolean has=false;
		for (int ic: n.vc){
			if (shrinkNodesWithTagRecur(name, value,ic, aggresive))
				has=true;
		}
		
		if (!n.getString(name).equals(value))	return has;
		if (has==false || aggresive){
			deleteNodeRecur(id);
			vkeep.set(id,true);
		}
		return true;
	}			
	
	
	protected boolean trim2PhraseRecur(int id) {
		Node n = tree.getNode(id);
		for (int ic: n.vc) 	trim2PhraseRecur(ic) ;
		if (!vkeep.get(id)){
			String pos = n.getString(CTag.pos);
			//keep phrases and verbs
			if (!m_phrase_pos.contains(pos) && pos.charAt(0)!='V'){					
				return false;
			}
			vkeep.set(id,true);
		}		
		if (n.iparent >=0) vkeep.set(n.iparent,true);
		return true;
	}	
	//keep Ancestors and id itself
	protected VectorB keepAncestors(int id){
		for (; id>=0; id=tree.getNode(id).iparent){
			//if (vkeep.get(id))	break;
			vkeep.set(id,true);
		}
		return vkeep;
	}	
	
	/**
	 * find all NP tags and keep  Ancestors bottom-up 
	 * @mean do not keep those who's parent already has the tag
	 **/
	protected VectorB keepNodesWithTag(	
			String name, String value, boolean mean){// boolean keepAncestors) {
		for (int id=0;id< tree.vNode.size(); ++id){
			Node n = tree.getNode(id);
			if (!n.getString(name).equals(value))
				continue;
			if(mean){
				if (n.iparent>=0){
					Node np=tree.getNode(n.iparent);
					if (np.getString(name).equals(value))
						continue;
				}
			}
			//vkeep.set(id,true);
			//if (keepAncestors)
			keepAncestors(id);
		}
		return vkeep;
	}
	
	

	
	//top-down search, until NP tag is found
	protected VectorB keepNodesUntilTag(
			String name, String value){
		keepNodesUntilTagRecur(name,value, tree.root);
		return vkeep;
	}
	private void keepNodesUntilTagRecur(
			String name, String value, int id){
		Node n = tree.getNode(id);
		vkeep.set(id,true);
		//n.t.m.add("E");
		if (n.getString(name).equals(value)) {
			//t.resetText(id);
			return;
		}
		for (int ic: n.vc)
			keepNodesUntilTagRecur(name,value, ic);
	}
	
/*	public ParseTree t;
	public ParseTree Trim(ParseTree _t) {
		t = _t;
		return t;		
	}*/
	private int ip;//predicate
	protected VectorI vp= null ,v=null;
	
	public VectorB trim2PredcateStack(TreeParse _t) {
		tree=_t;
		ip = tree.findNodeByFeatureFirst(CTag.target, CTag.y);
		if (ip<0)	return null;
		vp = tree.getStack(ip);		

		VectorB vkeep= new VectorB(tree.vNode.size());
		for (int id: vp){
			Node n=tree.getNode(id);
			vkeep.set(n.vc,true);
		}
		vkeep.set(tree.root,true);
		
		for (int i=0; i<tree.vNode.size();++i){
			if (vkeep.get(i)) continue;
			Node n= tree.getNode(i);
			if (n.getString(CTag.srl)!=null){
				System.out.println("srl label at node "+i+" should not be truncated");
				return null;
			}
		}
		tree.doTruncate(vkeep);
		return vkeep;
	}	


}
