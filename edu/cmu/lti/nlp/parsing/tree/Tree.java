/**
 * 
 */
package edu.cmu.lti.nlp.parsing.tree;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IUpdateIdx;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorB;
import edu.cmu.lti.algorithm.container.VectorI;

/**
 * @author nlao
 * 
 */
public class  Tree implements Serializable, Cloneable//, ICopyable
		,IUpdateIdx{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public int root=-1;
	public VectorX<Node> vNode = new VectorX<Node>(Node.class);

	// public ArrayList<Node> vt=new ArrayList<Term>(0); //terms
	//public static int dbg = 1;

	public Tree() {
	}

	//public Tree newInstance(){return new Tree();	}	
	@Override
	public Tree clone(){
		//Tree t= newInstance();
		
		try{
			Tree t  = (Tree)super.clone();
			//Tree t=new Tree();
			t.root = root;
			t.vNode = vNode.clone();
			return t;
		} catch ( Exception e ) {
			return null;
		}
	}
	
	
	public VectorI getStack(int id){
		VectorI v = new VectorI();
		for(;id >=0; id= getNode(id).iparent){
			v.add(id);
			if (v.size()>100) return null;//loop found, TODO fix it
		}
		v.reverseOn();
		return v;
	}

	/*
	 * return the id of common ancestor
	 */	
	//public int findCommonAncestor(VectorI v1, VectorI v2){
		//return v1.get(v1.idxCommonPrefix(v2)-1);	}

	/**
	 * T root= new T(); T a; a.fun(); java template cannot be used c++ style
	 * seems java template is completly useless user virtual funcitons instead
	 * to be over written
	 */
	public Node newNode() {
		return new Node();
	}

	public Node getNode(int id) {
		return (Node) (vNode.get(id));
	}

	
	public void updateIdx(VectorI vi){//Vector<Integer> vi){
		root = vi.get(root);
		//VectorI vi){//
		vNode.updateIdx(vi);
		
		//for (Node n: v_node)n.updateIdx(vi);	
		return;
	}
	

	
	public VectorI doTruncate(VectorB vkeep){
		if (!vkeep.get(root)) {
			System.out.println("cannot truncate root");
			return null;
		}
		VectorI vn =vkeep.idxEqualTo(true);
		VectorI vi = vn.reversIdx(vkeep.size()); 
		vNode=vNode.sub(vn);
		updateIdx(vi);
		return vi;
	}
	
	//v is a order of the original id
	public VectorI reorder(VectorI v){
		VectorI vi=v.reversIdx(vNode.size());
		vNode=vNode.sub(v);
		updateIdx(vi);
		return vi;
	}
	
	private void findChildBoundRecur(int id){
		Node n= getNode(id);
		n.icl= id;n.icr=id;
		if (n.isLeaf())		return;

		for (int ic: n.vc){
			findChildBoundRecur(ic);
			Node nc= getNode(ic);			
			if (nc.icl < n.icl) n.icl = nc.icl;
			if (nc.icr > n.icr) n.icr = nc.icr;
		}
	}
	public void findChildBound(){
		findChildBoundRecur(root);
	}
	public static class Node 
		implements  IUpdateIdx, Serializable, Cloneable, ICloneable{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public int iparent=-1, id=-1;
		public int icl=-1, icr=-1; //left/right most dominated decendent 
		//public TVector<Integer> vc = new TVector<Integer>(Integer.class); // children
		public VectorI vc = new VectorI(); // children
		//public Node newInstance(){	return  new Node();	}
		public Node clone(){
			try{
				Node e  = (Node)super.clone();
				//Node n =newInstance();
				e.iparent = iparent;
				e.id = id;
				e.icr = icr;
				e.icl = icl;
				e.vc = (VectorI)  vc.clone();			
				return e;
			} catch ( Exception e ) {
				e.printStackTrace();
				System.exit(-1);
				return null;
			}
		}
		
		public void updateIdx(VectorI vi){
			if (iparent >=0)	
				iparent=vi.get(iparent);
			vc =(VectorI) vi.sub(vc).removeValue(-1);//		vc.reorder(vi);
			if (vi.get(id) <0){
				System.err.println("id should never be -1");
			}
			id= vi.get(id);
			return;
			//for (Node n: v_node)
		}
		public boolean isLeaf() {
			return vc.size() == 0;
		}
		public boolean ParseContent(String s) {
			return true;
		}
	}

	protected boolean parseNode(String s, int id) {
		return true;
	}

	protected boolean parseLeaf(String s, int id) {
		return true;
	}

	private void add_link(int id, int iparent) {
		getNode(id).iparent = iparent;
		if (iparent >= 0) {
			vNode.get(iparent).vc.add(id);
		}
	}
	public Node addNode(Node n) {
		n.id = vNode.size();
		vNode.add(n);
		return n;
	}	
	/** * 
	 * @param s: the string
	 * @param is: current position
	 * @param iparent
	 * @return: new position
	 */
	private int parseBracketStringRecur(
			String s, int is, int iparent){//	throws Exception{
		int id = addNode(newNode()).id;
		
		add_link(id, iparent);
		int ib = is;
		boolean get_content = false; // int ie= s.length();
		char c;
		for (; is < s.length(); ++is) {
			c = s.charAt(is);
			if (c == ')') {
				if (!get_content)	
					parseLeaf(s.substring(ib, is), id);
				return is ;
			}
			if (c == '(') {
				if (!get_content) {
					parseNode(s.substring(ib, is), id);
					get_content = true;
				}
				is = parseBracketStringRecur(s, is + 1, id);
				if (is == -1) 
					return -1 ;
			}
		}
		System.out.println("unmatched bracket?");
		//throw new Exception("unmatched bracket?");
		return -1 ;
	}

	public void clear() {
		// is=0;
		root=-1;
		vNode.clear();
	}

	public boolean parseBracketString(String s){
		clear();
		root=0;
		if (parseBracketStringRecur(s, 1, -1)==-1)
			return false;
		return true;
	}
	public String printBracketString(){
		return printBracketStringRecur(this.root);
	}
	private String printBracketStringRecur(int id){
		StringBuilder sb = new StringBuilder();
		if (id !=root)
			sb.append(" ");
		sb.append("(").append(printNode(id));
		for (int ic: vNode.get(id).vc)
			sb.append(printBracketStringRecur(ic));
		return sb.append(")").toString();
	}
	protected String printNode(int id){
		return vNode.get(id).id +"";
	}
	private void visitRecur(int id, boolean HeadFirst,VectorI vi){	
		if (HeadFirst)
			vi.add(id);
		for (int ic:getNode(id).vc)
			visitRecur(ic, HeadFirst, vi);
		if (!HeadFirst)
			vi.add(id);
		return;			
	}
	//0=HeadFirst,  1=HeadLast,x=HeadMiddle,
	public VectorI visit( boolean HeadFirst){// iStyle){
		VectorI vi = new VectorI();
		vi.ensureCapacity(vNode.size());
		visitRecur(root, HeadFirst, vi);
		return vi;		
	}
	public Node findUnaryAncestor(Node n){
		while (n.iparent >=0){
			Node p=this.getNode(n.iparent);
			if (p.vc.size()>1)
				break;
			n=p;
		}
		return n;
	}

}
