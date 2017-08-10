package edu.cmu.lti.nlp.parsing.tree;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.VectorB;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.parsing.FTree.FindHead;
import edu.cmu.lti.util.text.FString;

/**
 * assume head first, all words attached on the leaf nodes
 * @author nlao
 *
 */
public class TreeSyntax 
	extends TreeParse implements Serializable, Cloneable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TreeSyntax(){	}


/*	public TreeSyntax(TreeParse t){
		super(t);
	}*/
	
	//public TreeParse newInstance(){return new TreeParse();	}	

	
	//find dominant span of each node on the word sequence
	//assuming the left most child is listed as the first in vc
	//and the right most child is listed as the last in vc
	public boolean setSpans(boolean setTerminals) {
		if (setTerminals){//terminal nodes has no span yet
			for (int i = 0; i < vWord.size(); ++i) {
				Node n = getNode(viNode.get(i));
				n.ib = i;
				n.ie = i+1;
			}
		}
		else{//terminal nodes has span already
			
		}
		// for (Tree.Node n : t.v_node) {
		for (int i = vNode.size() - 1; i >= 0; --i) {
			Node n = getNode(i);
			if (!n.isLeaf()) {
				Node nf = getNode(n.vc.firstElement());
				n.ib = nf.ib;
				Node nl = getNode(n.vc.lastElement());
				n.ie = nl.ie;
			}
		}
		return true;
	}
	
	public boolean parseBracketString(String s){//throws Exception{
		if (super.parseBracketString(s)) {
    	//fn.tree = fhc.findHead( fn.tree);
    	//doAssembleText();
			if (setSpans(true))
				return true;
		}
		System.out.println("parseBracketString failed");
		return false;
	}	
	public static TreeSyntax parseNew(String line){
		TreeSyntax tree0 = new TreeSyntax();
		if (!tree0.parseBracketString(line)) return null;
		return tree0;
	}
	/**
	 * it assumes the node are ordered head first
	 * */
	protected String toStringRecur(int id, String prefix, int level){
		StringBuffer sb = new StringBuffer();
		Node n=getNode(id);
		sb.append(prefix);
		//sb.append(+"(");
		sb.append(n.toString(level) );
		if (n.vc.size()>0) sb.append("\n");
		for (int i = 0; i < n.vc.size(); i++) {
			if (i>0) sb.append("\n");
			sb.append(toStringRecur(n.vc.get(i), prefix+"|\t", level));
		}		
		//sb.append(")");
		return (sb.toString());
	}	
	/*
	 * it assumes the node are ordered head first
	 * */
	public String toString(int level){
		return vWord.join(" ")+"\n"
		+ toStringRecur(root, "", level)+"\n\n";
	}	
	public String toString(){
		return toString(6);
	}
	/**
	 * put text on non-terminals
	 * it assumes the node are ordered head first
	 * */
	public void assembleText() {
		// for (Tree.Node n : t.v_node) {
		for (int i = vNode.size() - 1; i >= 0; --i) {
			Node n = getNode(i);
			//if (n.isLeaf()) continue;
				//n.set(_text, v_term)
			//MyVector<String> vs = 
			//String s = v_node.getString(CTag._text, n.vc).join(" ");
			String s = vWord.sub(n.ib,n.ie).join(" ");
			n.put(CTag.text, s);
		}
		return;
	}
	/**
	 * parse PennTreeBank bracketed format
	 */
	protected boolean parseNode(String s, int id) {
		Node n=getNode(id); 
		s= s.trim();
		
		//if (s.equals("-NONE-")) s= "NONE";
		if (s.startsWith("-"))
			if (s.endsWith("-"))
				s=s.substring(1,s.length()-1);
			else
				System.err.print("error?");
			
		
		int i= s.indexOf('=');
		if (i >=0)	// index of semantics e.g. SBJ=1
			s = s.substring(0, i);
		
		i= s.indexOf('-');
		if (i >=0){
			n.put(CTag.posx, s.substring(i+1));
			s = s.substring(0, i);
		}
		n.put(CTag.pos, s);
		return true;
	}

	/**
	 * parse PennTreeBank bracketed format
	 */
	protected boolean parseLeaf(String s, int id) {
		Node n=getNode(id); 
		String[] vs = s.split(" ");
		parseNode( vs[0],  id);
		n.put(CTag.text, vs[1]);
		//if (!n.get(_pos).equals("NONE"))
		//v_term.add(new Term(id, vs[1]));
		//if (vs[1].equals("*PRO*")) return true;//startsWith
		viNode.add(id);
		vWord.add(vs[1]);			
		return true;
	}
	
/*	private boolean toDepTreeNode(SetI mi) {	
		if (mi.size()==0)
			return false;
		
		return true;
	}*/
	public int moveNodeUp(int id){
		Node n = getNode(id);
		int ip = n.iparent;
		if (ip <0) return -1;		
		Node p=getNode(ip);
		for (Integer ic: n.vc) 
			getNode(ic).iparent = ip;		
		p.vc = (VectorI) p.vc.replace(id, n.vc);
		return ip;
	}
	public TreeDep toDepTree() {	
		TreeDep t=new TreeDep(this);
		//TreeDep t=  this.clone();
		
		//find head if not yet
		if (getNode(0).getInt(CTag.head)==null)//FindHead._head
			FindHead.getInstance().findHead(t);

		VectorB vkeep= new VectorB(t.vNode.size(),true);
		
		for (int id:t.viNode.toSet()){
			while (true){//move this node as up as possible
				Node n = t.getNode(id);
				int ip = n.iparent;
				if (ip <0) break;
				
				Node p=t.getNode(ip);
				int ih =p.getInt(CTag.headChild);
				if (ih != id)
					break;
				
				t.moveNodeUp(id);
				
				p.t.ms=(MapSS) n.t.ms.addOn(p.t.ms);
				vkeep.set(id,false);
				//t.v_inode.replace(id,ip);//if not updated here, it will be updated in doTruncate()
				p.t.setText(n.t.getText());
				id=ip;		
			}
		}	
		t.doTruncate(vkeep);
		//t.setSpans();
		//return true;		
		return t;
	}
	//assume head first tree
	public boolean addHeadFeatures() {
		//for (Tree.Node n : t.v_node) {
		for (int i=vNode.size()-1; i>=0; --i){
			Node n= getNode(i);
			if (n.isLeaf()) continue;
			
			int ih = n.getInt(CTag.head);//FindHead._head);
			Node nh = getNode(ih);
			n.t.ms.addOn(nh.t.ms.addKeyPrefix("h."));
		}
		return true;
	}
	/**
	 * reorder tree nodes to head first visit
	 * only applys to syntax tree
	 */
	public VectorI reorder(){
		return super.reorder(visit(true));
	}
	public VectorB collapseAAUnary() {
		int k= vNode.size();
		VectorB vkeep= new VectorB(k, true);
		for (int id=0; id<vNode.size(); ++id){
			Node n = getNode(id);
			if (n.iparent<0) continue;
			Node p=  getNode(n.iparent);
			if (p.vc.size()>1) continue;
			if (!p.t.getPOS().equals(n.t.getPOS())) continue;
			
			//delete the child
			vkeep.set(id,false);			
			p.vc=n.vc;
			for (int ic: n.vc)
				getNode(ic).iparent = n.iparent;

		}		
		VectorI vMapping =doTruncate(vkeep);	
		return vkeep;
	}
	
	/** replace unary productions NNS->NP->S 
	 * as a single tag "NNS->NP->S"
	 * assume head first node order
	 * @param tree
	 * @return
	 */
	public VectorB collapseUnary() {
		int k= vNode.size();
		VectorB vkeep= new VectorB(k, true);
		for (int id=vNode.size()-1;id>=0;--id){
			Node n = getNode(id);
			//n.t.m.add(n.t.getPOS());
			if (n.iparent==-1) continue;
			Node p= getNode(n.iparent);
			if (p.vc.size()!=1)	continue;

			//delete the child
			vkeep.set(id,false);			
			p.vc=n.vc;
			p.t.setText(n.t.getText());
			//if (n.t.getPOS() != null)
			//p.t.m.add(n.t.getPOS());
			p.t.setPOS(p.t.getPOS()+">"+n.t.getPOS());
			for (int ic: n.vc)
				getNode(ic).iparent = p.id;
		}	

		VectorI vMapping =doTruncate(vkeep);	
		return vkeep;
	}
	/**
	 * reverse of collapseUnary
	 */	
	public VectorI expandUnary() {
		for (int id=0; id<vNode.size(); ++id){
			Node n = getNode(id);
			String pos=n.t.getPOS();
			VectorS vs=FString.toVS(pos, ">");
			if (vs.size()==1)continue;
				
			for (int i=vs.size()-1; i>=0; --i){
				if (i==vs.size()-1)
					n.t.setPOS(vs.get(i));
				else{			
					int ip= n.iparent;
					Node n1=addNonTerminal(new VectorI(1,n.id), vs.get(i));
					if (ip>=0)
						getNode(ip).vc.replaceOn(n.id, n1.id);
					n1.iparent= ip;
					n=n1;
				}
			}
		}		
		return reorder();
	}
		
	//only keep the top most unary tag
	public VectorB removeUnary() {
		int k= vNode.size();
		VectorB vkeep= new VectorB(k, true);
		for (int id=0; id<vNode.size(); ++id){
			Node n = getNode(id);
			n.t.m.add(n.t.getPOS());
			Node a=(Node) findUnaryAncestor(n);
			if (a.id==n.id)
				continue;
			//delete the child
			vkeep.set(id,false);			
			a.vc=n.vc;
			a.t.setText(n.t.getText());
			if (n.t.getPOS() != null)
			a.t.m.add(n.t.getPOS());
			for (int ic: n.vc)
				getNode(ic).iparent = a.id;
		}	

		VectorI vMapping =doTruncate(vkeep);	
	/*	for (int id=0; id<tree.vNode.size(); ++id){
			Node n = tree.getNode(id);
			if (n.isLeaf())
			n.t.setPOS("S");
		}*/
		return vkeep;
	}
	protected VectorB trimAncestors(VectorB vkeep, int id){
		for (; id>=0; id=getNode(id).iparent){
			Node n = getNode(id);			
			if (n.vc.size()>1)	break;
			vkeep.set(id,false);
		}
		return vkeep;
	}	
  /**
	  * Remove all the empty nodes in the given tree
	  * @param tree the tree to remove empty nodes from
	  */
	public VectorB trimEmptyNodes() {
		int k= vNode.size();
		VectorB vkeep= new VectorB(k, true);

		boolean deleted=false;
		VectorI vi = new VectorI();//mask for the v_inode to keep
		vi.ensureCapacity(viNode.size());		
		for (int id: viNode){
			Node n = getNode(id);
			if( n.t.getPOS().equals("NONE")){
				vi.add(0);
				trimAncestors(vkeep, id);
				deleted= true;
			}
			else{
				vi.add(1);				
			}				
		}
		if (!deleted) return vkeep;
		
		VectorI vMapping =doTruncate(vkeep);	
		vWord=  (VectorS)vWord.subByMask(vi);
		viNode= (VectorI)	viNode.subByMask(vi);
		resetTexts();
		return vkeep;
	}	
}
