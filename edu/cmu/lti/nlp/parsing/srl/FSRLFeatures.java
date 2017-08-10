/**
 * 
 */
package edu.cmu.lti.nlp.parsing.srl;

import edu.cmu.lti.nlp.parsing.FTree.FParseTreeFeatures;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;

/**
 * @author nlao
 *
 */
public class FSRLFeatures extends FParseTreeFeatures{	
	
	
	public static  class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public boolean bNeighbor = true;
		public boolean bPath = true;//1;
		public boolean bFrameSet= true;
		
		public Param(Class c) {
			super(c);//FSRL.class);
			parse();
		}
		public String code;
		public void parse() {
//			lang = getString("lang","zh_CN");
			//ctbTreeFile = getString("ctbTreeFile");
			bNeighbor = getBoolean("bNeighbor",false);
			bPath = getBoolean("bPath",true);
			bFrameSet = getBoolean("bFrameSet",true);
			
			code = "";
			if (bNeighbor)code+= ".Nei";
			if (bPath)code+= ".Pa";
			if (bFrameSet)code+= ".fs";
		}
	}	
	public static Param p = null;
	public FSRLFeatures(Class c){
		p= new Param(c);
	}
	
	
	private boolean addPathFeatures(int id, String tag) {
		Node n = t.getNode(id);		
		String s1=t.vNode.getVS(tag, vi1).join(">");
		String s2=t.vNode.getVS(tag, vi2).join("<");
		n.put("p."+tag+".1", s1);
		n.put("p."+tag+".2", s2);
		n.put("p."+tag, s1+s2);		
		return true;
	}
	private boolean addPathFeatures(int id) {
		//int ianc = t.findCommonAncestor(ipred, id);
		addPathFeatures(id,CTag.pos_hw);
		addPathFeatures(id,CTag.pos);
		addPathFeatures(id,CTag.posx);
		addPathFeatures(id,CTag.nvi);
	
		return true;
	}
	//public static String verb;
	private boolean addLocalFeatures(int id) {
		Node n= t.getNode(id);
		String pos =n.getString(CTag.pos);
		String nvi= getNVI(pos);
		String w =n.getString(CTag.hw);
		n.put(CTag.nvi, nvi);
		n.put(CTag.pos_hw, nvi+"+"+w);
		n.put(CTag.level, n.getInt(CTag.level).toString());//level
		n.put(CTag.verb,meta.verb);//  verb);//level
		if (id < ip)	n.put(CTag.atPred, CTag.left);
		if (id > ip)	n.put(CTag.atPred, CTag.right);
		
		if (p.bFrameSet)
			n.put(CTag.fs, String.format(
					"%s.%d",meta.verb,meta.i_subcat));

		return true;
	}
	private PBMeta meta;
	private int ip;//predicate


	public boolean addSrlFeatures(TreeParse _t, PBMeta _meta){
			//TreeSRL tree) {
		t=_t; 
		meta=_meta;
		//t= (TreeSyntax) tree.t;
		//meta=tree.pbMeta;
		//ip = t.findNodeByFeatureFirst(CTag.target, CTag.y);
		
		ip = t.viNode.get(meta.i_pred);
		if (ip<0)	return false;
		
		v2 = t.getStack(ip);		
		t.setLevels();
		
		for (int id=0; id<t.vNode.size(); ++id){//for (Node n: t.v_node) 
			if (p.bNeighbor)
				addNeighborFeatures(id);
			addLocalFeatures(id);
		}
		
		if (p.bPath)
		for (int id=0; id<t.vNode.size(); ++id){
			Node n= t.getNode(id);
			findPath(id, ip);
			if (id == ic) n.put(CTag.atPred, CTag.cover);
			addPathFeatures(id);	
		}
		return true;
	}	
	
	private void addNeighborFeatures(String pre, Node nf, Node nt) {
		nt.put(pre+CTag.pos, nf.getString(CTag.pos));
		//nt.put(pre+CTag.text, nf.getString(CTag.text));
		nt.put(pre+CTag.hw, nf.getString(CTag.hw));
		return;
	}
	private boolean addNeighborFeatures(int id) {
		Node n = t.getNode(id);	
		if (n.iparent <0)
			return false;
		Node nM = t.getNode(n.iparent);	
		addNeighborFeatures("m.", nM, n);
		int ic = nM.vc.findExact(id);
		if (ic<0) 
			return true;
		if (ic>0){
			Node nl= t.getNode(nM.vc.get(ic-1));
			addNeighborFeatures("l.", nl, n);
		}
		if (ic< nM.vc.size()-1){
			Node nr= t.getNode(nM.vc.get(ic+1));
			addNeighborFeatures("r.", nr, n);
		}
		return true;
	}
}
