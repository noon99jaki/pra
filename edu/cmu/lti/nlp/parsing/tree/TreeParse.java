package edu.cmu.lti.nlp.parsing.tree;

/**
 * a parser tree
 * @author nlao
 * (S1 (S (S (NP (DT The) (NNP NYSE)) (VP (AUX is) (ADJP (JJ prepared) (S (VP (TO to) (VP (VB open) (NP (NN tomorrow)) (PP (IN on) (NP (NN generator) (NN power))) (SBAR (IN if) (FRAG (ADJP (JJ necessary)))))))))) (NP (DT the) (NN statement)) (VP (VBD said))))
 */
import java.io.Serializable;
import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetObjByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.MapXX;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorB;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.CLang;


public class TreeParse extends Tree 
	implements Serializable , Cloneable, ICloneable
	, IGetObjByStr, IGetIntByStr{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Object getObj(String name){
		if (name.equals(CTag.rule_count))
			return  getRuleCount();		
		if (name.equals(CTag.rule_example))
			return  getRuleExample();		
		return null;
	}
	public String getRule(Node n){
		return n.t.getPOS()+"="
		+ vNode.getVS(CTag.pos, n.vc).join(" ");
	}
/*	public VectorS getTexts(VectorI vi){
		VectorS vs = new VectorS();
		vs.ensureCapacity(vi.size());
		for (int id: vi)
			vs.add(getNode(id).t.getPOS()
				+"("+getText(id)+")");
		return vs;
	}*/

	public MapSS getRuleExample(){
		MapSS m = new MapSS();
		for (int id=0; id<this.vNode.size(); ++id){
			Node n = this.getNode(id);
			if (n.isLeaf()) continue;
			
			VectorS vs = new VectorS();
			vs.ensureCapacity(n.vc.size());
			for (int i: n.vc)
				vs.add(getNode(i).t.getPOS()
					+"("+getText(i)+")");
			
			m.plusOn(getRule(n), n.t.getPOS()+"="+vs.join(" ")+"<br>");
		}			
		return m;
	}
	
	/** count frequency of CFG rules in this tree */
	public MapSI getRuleCount(){
		MapSI m = new MapSI();
		for (int id=0; id<this.vNode.size(); ++id){
			Node n = this.getNode(id);
			if (n.isLeaf()) continue;
			m.plusOn(getRule(n));
		}			
		return m;
	}

	//public MyVector<Term> v_term = new MyVector<Term>();
	
	//iword-->inode
	public VectorI viNode= new VectorI();
	public VectorS vWord= new VectorS();
	//public TVector<Token> v_token = new TVector<Token>(Token.class);
	public Node addTerminal(String text, String pos) {
		return addTerminal(new Token(text,pos));
	}	
	public Node addTerminal(Token t) {
		viNode.add(vNode.size());
		vWord.add(t.getText());
		Node n= addNode(t);
		n.ie = vWord.size();
		n.ib = n.ie-1;
		return n;
	}
	
	public Node addNonTerminal(VectorI vc, String pos) {
		Node n= addNode(new Token(null,pos));
		n.vc = vc;
		n.ib = getNode(vc.firstElement()).ib;
		n.ie = getNode(vc.lastElement()).ie;
	  for (int ic: vc){
	  	if (ic==this.root)
	  		root=n.id;
	   	getNode(ic).iparent = n.id;
	  }
		return n;
	}	

	public Node addNode(Token t) {
		return (Node) addNode(new Node(t));
		//n.t = t.clone();
		//return n;
	}


	public TreeParse() {
	}

/*	public TreeParse(TreeParse t) {
		//super.copy(t);
		copy(t);
	}*/
	//public TreeParse newInstance(){return new TreeParse();	}	
	@Override
	public TreeParse clone() {//throws CloneNotSupportedException {
		TreeParse t  = (TreeParse)super.clone();
//	Tree		TreeParse t = new TreeParse();
		t.viNode =(VectorI) viNode.clone();
		t.vWord = (VectorS) vWord.clone();
		return t;
	}
	
	public void setLevelsRecur(int id){
		Node n= getNode(id);
		int level=0;
		if (id!=root){
			if (n.iparent <0){
				//TODO: fix it
				System.out.println(vNode.getVI("iparent"));
				return;
			}
			level= getNode(n.iparent).getInt(CTag.level)+1;
		}
		n.putInt(CTag.level, level);
		for (int ic: n.vc)	
			setLevelsRecur(ic);
	}
	public void setLevels(){
		setLevelsRecur(root);		
	}	

	public FeaturedGraph toFeaturedGraph(VectorS filter) {
		FeaturedGraph fg = new FeaturedGraph();
		for (int i = 0; i < vNode.size(); ++i) {
			Node n = getNode(i);
			if (n.iparent >= 0) {
				fg.v_link.add(fg.new Link(i, n.iparent));
			}
			fg.v_node.add(n.toFeaturedNode(filter) );
		}
		return fg;
	}

	public void clear() {
		super.clear();
		// is=0;
		//v_term.clear();
		viNode.clear();
		vWord.clear();
	}

	public Node newNode() {
		return new Node();
	}
	public Node getRoot() {
		return getNode(this.root);
	}
	public Node getNode(int id) {
		// return v_node.get(id);
		if (id < 0 || id >= vNode.size())
			return null;
		return (Node) (vNode.get(id));
	}
	public VectorX<Node> getNodes(VectorI vi) {	
		VectorX<Node> vn = new VectorX<Node>(Node.class);
		vn.ensureCapacity(vi.size());
		for (int id: vi)
			vn.add((Node) vNode.get(id));
		return vn;
	}
	public String getText(int id) {
		Node n = getNode(id);
		return vWord.sub(n.ib, n.ie).join(" ");
		//return v_word.sub(n.range).join(" ");
	}
	

	public static class Node extends Tree.Node 
		implements IGetStrByStr, IGetIntByStr
		,IGetObjByStr, Serializable, Cloneable, ICloneable{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public Token t=new Token();
		public Node(Token t){
			this.t = t;
			//this.t = t.clone();
		}
		public Node(){
		}
		
		public int ib = -1, ie = -1; //the word it dominate
		public int length(){return ie-ib;}
		
		public Node newInstance(){
			return  new Node();
		}
		
		public Node clone(){
			Node n= (Node) super.clone();//new Node();
			n.ib = ib;
			n.ie = ie;
			n.t = (Token) t.clone();			
			return n; 
		}
		public Integer getHead(){
			return t.getInt(CTag.head);
		}
		public Integer getHeadChild(){
			return t.getInt(CTag.headChild);
		}
		public Integer getHeadChildIdx(){
			return t.getInt(CTag.headChildIdx);
		}
		public String toString(){			
			StringBuffer sb = new StringBuffer();
			sb.append("("+id+") ip="+this.iparent)
				.append(" vc="+vc.join(","))
				.append(" token= " +t.toString(4));
			return (sb.toString());			
		}
		public String toString(int level){
			StringBuffer sb = new StringBuffer();
			sb.append("("+id+")"+ib+"~"+ie
					+", p="+this.iparent 
					+", ");
			//sb.append(String.format("(%d)%d~%d,%d~%d,",id, icl, icr, ib, ie));
					
			//if (level>=1) sb.append("["+getString(CTag.h_text)+"] ");
			//if (level>=1) sb.append(getString(CTag.text)+" ");
			sb.append(t.toString(level));
			return (sb.toString());			
		}
		public String getString(String k) {
			return t.ms.get(k);
		}		
		public void put(String k, String v) {
			t.ms.put(k, v);
		}		
		public void add( String v) {
			t.m.add(v);
		}	
		//public String get(String k) {	return getString(k);	}
//		public void put(String k, String v) {			put(k, v);		}		
		public Integer getInt(String k) {
			if (k.equals("iparent")) return this.iparent;
			return t.mi.get(k);
		}
		public Object getObj(String name) {
			if (name.equals("token")) return this.t;
			//if (name.equals("vtoken"))		return this.t;
			return null;
		}
				

		public void putInt(String k, Integer v) {
			t.mi.put(k, v);
		}		

		public boolean parseContent(String s) {
			// id = _id; ++_id;
			return true;
		}
		public FeaturedGraph.Node toFeaturedNode(VectorS filter){
			FeaturedGraph.Node gn = new FeaturedGraph.Node();
			//gn.tag = n.m.get(_srl);
			if (filter==null)
				gn.ms = t.ms;
			else
				gn.ms = t.ms.sub(filter);
			return gn;
		}
	}

	public static class  Tag implements Serializable, Cloneable{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public int i_node, depth;
		public String type;
		public String txt;
		public String toString(){
			return i_node+"/"+depth+"/"+txt;
		}
		public Tag(String type){
			this.type = type;
		}
		public Tag(){}
		public Tag parse(String s) {return this;}
	}


/*	
	public MyVector<Integer> doTruncate(MyVector<Integer> vkeep){
		vkeep = super.doTruncate(vkeep);
		//v_term.updateIdx(vkeep);
		return vkeep;
	}*/



	public int findNodeByTag(Tag t){
		int id=viNode.get(t.i_node);
		for (int i=0; i< t.depth;++i){
			id = getNode(id).iparent;
			if (id<0){
				System.out.println("cannot find tag "+t);
				return -1; 
			}
		}
		return id;
	}
	public int findNodeByFeatureFirst(String k, String v){
		for (int i=0; i< vNode.size();++i){
			Node n=getNode(i);
			if (v.equals(n.getString(k))) return i;
		}
		//for (Node n: v_node) 			if (v.equals(n.get(k))			return n.id;
		return -1; 
	}	
	public Node setTag(Tag t){
		int id= findNodeByTag(t);
		if (id <0) 	return null;
		Node n= getNode(id);
		n.put(t.type, t.txt);
		return n;
	}	

	/**
	 * set node text according to v_inode
	 */
	public void resetTexts(){
		MapXX<Integer, VectorI> mv = viNode.idxEqualToX();
		for ( Map.Entry<Integer, VectorI> e : mv.entrySet() ) {
			int id = e.getKey();
			VectorI vi = e.getValue();
			String txt = vWord.sub(vi).join(" ");
			Token t = getNode(id).t;
			t.setText(txt);
			t.mi.put(CTag.iwb, vi.firstElement());
			t.mi.put(CTag.iwe, vi.lastElement());
		}
	}


	public Node setTagGentle(Tag t){
		int id= findNodeByTag(t);
		if (id <0) 	return null;
		Node n= getNode(id);
		if (n.getString(t.type)!=null){
			System.out.println("tag already exist" +t);
			return null; 		
		}
		n.put(t.type, t.txt);
		return n;
	}	

	/**
	 * responsible to move v_inode pointers up the tree
	 * if they are not updated when vkeep are generated
	 */
	public VectorI doTruncate(VectorB vkeep){
		vkeep.set(root, true);

		//move v_inode up the tree, 
		//not sure if it is needed anywhere
		for (int i=0;i<viNode.size(); ++i){
			int id= viNode.get(i);
			for(;!vkeep.get(id); id = getNode(id).iparent );
			viNode.set(i,id);
		}
		VectorI vi = super.doTruncate(vkeep);
		//v_inode.reorder(vi);
		viNode = vi.sub(viNode);
		return vi;
	}	
	public void addFeature2Stack(int id, String feature){
		VectorI vi = this.getStack(id);
		for (int i: vi)
			getNode(i).add(feature);
	}
	public String bracketString;
	public boolean parseBracketString(String s){
		bracketString=s;
		return super.parseBracketString(s);
	}
	
	public String printBracketString(){
		bracketString= super.printBracketString();
		return bracketString;
	}
	
	public String getSent(String lang){
		if (lang.equals(CLang.en_US))
			return vWord.join(" ");
		return vWord.join("");		
	}
	public VectorToken getTerminals(){
		//TVector<Token> vt =new TVector<Token>(Token.class);
		VectorToken vt = new VectorToken();
		for (int id: this.viNode)
			vt.add(getNode(id).t);
		return vt;
	}
	
	//v is a order of the original id
	public VectorI reorder(VectorI v){
		if (v.size()!= this.vNode.size()){
			System.err.println("tree reorder failed");
			System.exit(-1);
		}
		VectorI vi=super.reorder(v);
		viNode = vi.sub( viNode);
		return vi;
	}
	
	protected String printNode(int id){
		Node n = getNode(id);
		if (n.isLeaf())
			return n.t.getPOS() +" "+n.t.getText();
		else
			return n.t.getPOS();
	}
	
	//add PU feature to node n
	public void addFeaturePU(Node n){
		int PU=0;
		if (n.isLeaf()){
			if (n.t.getPOS().equals(CPOS.PU))
				PU=1;
			else
				PU=0;
		}
		else
			PU = getNodes(n.vc).getVI(CPOS.PU).sum();		
		n.t.mi.put(CPOS.PU, PU);
	}
	
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
	public void removePOS() {
		for (int id=0; id<vNode.size(); ++id){
			Node n = getNode(id);
			if (n.isLeaf())
				n.t.setPOS(null);
		}
	}
	public void setAllPOS(String pos){
		for (int id=0; id<vNode.size(); ++id){
			Node n = getNode(id);
				n.t.setPOS(pos);
		}			
	}
	// IGetIntByString 
	public Integer getInt(String name){
		if (name.equals(CTag.length))
			return this.vWord.size();
		return null;
	}
}
