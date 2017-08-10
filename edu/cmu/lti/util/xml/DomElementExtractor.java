package edu.cmu.lti.util.xml;

import org.w3c.dom.Node;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.text.FString;

/**
 * given an DOM element, extract certain things from certain branches 
 * @author nlao
 *
 */
public abstract class DomElementExtractor {
	public static class ParseNode{
		//constraint checking
		//public int iSeq;
		public String name;
		public CallBack f;
		public VectorX<ParseNode> vpnC= new VectorX<ParseNode>(ParseNode.class);
		public String toString(){
			if (f==null)
				return "name="+name;
			else
				return "name="+name+"(Ex)";
		}
		
		public String toStringRecur(int nTab){
			StringBuffer sb= new StringBuffer();
			sb.append(FString.repeat(" ",nTab));
			sb.append(toString()).append("\n");
			for (ParseNode c:vpnC)
				sb.append(c.toStringRecur(nTab+1));
			return sb.toString();
		}
		public boolean checkConstraint(Node e){
			if (name==null) return true;
			String en=e.getNodeName();
			return en.equals(name);
		}
		public void addChild(ParseNode n){
			vpnC.add(n);
		}
		public ParseNode( String value, CallBack f){
			this.name=value;
			this.f=f;
		}		
		public ParseNode( String value){
			this(value,null);
		}	
		public ParseNode(){
			this(null,null);
		}
		public static interface CallBack {
			boolean extract(Node e);
		}
		public ParseNode newChild(String value, CallBack f){
			ParseNode n= new ParseNode(value,f);
			vpnC.add(n);
			return n;
		}
		public ParseNode newChild(String value){
			return newChild(value,null);
		}	
	}
	public ParseNode root;
	public String toString(){
		return root.toStringRecur(0);
	}
	public abstract void clear();
	public abstract void postProcess();
	public boolean extract(Node e){ //Element
		//System.out.println("root="+e.getNodeName());
		clear();
		if (!root.checkConstraint(e))	
			return false;
		extractRecur(root,e);
		//root.extractRecur(e);
		postProcess();
		return true;
	}
	
	private void extractRecur( ParseNode p, Node n){//, Node p){

		if (p.f!=null)
			if (!	p.f.extract(n))
				return;
				
		if (p.vpnC.size()==0)
			return;
		
		for (Node nC= n.getFirstChild(); nC!=null; nC=nC.getNextSibling()){
			//System.out.println("child="+nC.getNodeName());
			//System.out.println(i+") class="+FHtml.getClass(eC));
			for (ParseNode pn: p.vpnC )
				if (pn.checkConstraint(nC))
					extractRecur(pn, nC);
		}
		return;
	}
}