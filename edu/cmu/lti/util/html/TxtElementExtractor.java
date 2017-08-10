package edu.cmu.lti.util.html;

import javax.swing.text.Element;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTML.Attribute;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.text.FString;

/**
 * given an element, extract certain things from certain branches 
 * @author nlao
 *	TODO: can we merge this with DomElementExtractor?
 */
public abstract class TxtElementExtractor {
	public static class Node{
		//constraint checking
		public int iSeq;
		public Attribute ab;
		public String value;
		public CallBack f;
		public VectorX<Node> vnC= new VectorX<Node>(Node.class);
		public String toString(){
			if (f==null)
				return ab+"="+value;
			else
				return ab+"="+value+"(E)";
		}
		
		public String toStringRecur(int nTab){
			StringBuffer sb= new StringBuffer();
			sb.append(FString.repeat(" ",nTab));
			sb.append(toString()).append("\n");
			for (Node c:vnC)
				sb.append(c.toStringRecur(nTab+1));
			return sb.toString();
		}
		public boolean checkConstraint(Element e){
			if (ab==null) return true;
			return e.getAttributes().containsAttribute(ab,value);
		}
		public void addChild(Node n){
			vnC.add(n);
		}
		public Node(Attribute ab, String value, CallBack f){
			this.ab=ab;
			this.value=value;
			this.f=f;
		}		
		public Node(Attribute ab, String value){
			this(ab,value,null);
		}	
		public Node(){
			this(null,null);
		}
		public static interface CallBack {
			boolean extract(Element e);
		}
		public Node newChild(Attribute ab, String value, CallBack f){
			Node n= new Node(ab,value,f);
			vnC.add(n);
			return n;
		}
		public Node newChild(Attribute ab, String value){
			return newChild(ab,value,null);
		}	
		
		public Node newClass( String value, CallBack f){
			return newChild(HTML.Attribute.CLASS,value,f);
		}
		public Node newID( String value, CallBack f){
			return newChild(HTML.Attribute.ID,value,f);
		}
		public Node newName( String value, CallBack f){
			return newChild(HTML.Attribute.NAME,value,f);
		}
		public Node newClass( String value){return newClass(value,null);}
		public Node newID( String value){	return newID(value,null);	}
		public Node newName( String value){	return newName(value,null);	}

		/*public void extractRecur(Element e){//, Node p){
			//if (!p.checkConstraint(e))	return;
			if (f!=null)
				f.extract(e);
			if (vnC.size()==0)
				return;
			
			for (int i=0; i<e.getElementCount(); ++i){
				Element eC= e.getElement(i);
				//System.out.println("name="+eC.getName());
				System.out.println(i+") class="+FHtml.getClass(eC));
				for (Node nC: vnC )
					if (nC.checkConstraint(eC))
						nC.extractRecur(eC);
			}
			return;
		}*/
	}
	public Node root;
	public String toString(){
		return root.toStringRecur(0);
	}
	//public abstract void clear();
	public abstract void postProcess();
	public void extract(Element e){
		
		//clear();
		//super.extract(e);
		extractRecur(root,e);
		//root.extractRecur(e);
		postProcess();
		
	}
	
	public void extractRecur( Node p, Element e){//, Node p){
		//if (!p.checkConstraint(e))	return;
		if (p.f!=null)
			if (!p.f.extract(e)) //extraction failed
				return;
		
		if (p.vnC.size()==0)
			return;
		
		for (int i=0; i<e.getElementCount(); ++i){
			Element eC= e.getElement(i);
			//System.out.println("name="+eC.getName());
			//System.out.println(i+") class="+FHtml.getClass(eC));
			for (Node nC: p.vnC )
				if (nC.checkConstraint(eC))
					extractRecur(nC, eC);
		}
		return;
	}

}
