/**
 * 
 */
package edu.cmu.lti.nlp.parsing.FTree;

import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;

/**
 * @author nlao
 *
 */
public class FindNodeBySpan {
	public static VectorX<MapII> vm_span_id=null;
	public static TreeParse t;
	public static int findNodeBySpan(int ib, int ie) {
		MapII m = vm_span_id.get(ib);
		if (!m.containsKey(ie))
			return -1;
		return m.get(ie);
	}
	public static int findNodeBySpanSoft(int ib, int ie) {
		int id= findNodeBySpan(ib,ie);
		if (id <0) id= findNodeBySpan(ib,ie-1);
		if (id <0) id= findNodeBySpan(ib+1,ie);
		if (id <0) id= findNodeBySpan(ib+1,ie-1);
		if (id <0) id= findNodeBySpan(ib,ie+1);
		if (id <0) id= findNodeBySpan(ib-1,ie);
		if (id <0) id= findNodeBySpan(ib-1,ie+1);
		if (id <0) 
			return id;
		return id;
	}	
	public static boolean setTree(TreeParse _t) {
		t = _t;
		vm_span_id = new VectorX<MapII>(t.vWord.size(),MapII.class);
		// for (Tree.Node n : t.v_node) {
		for (int i = t.vNode.size() - 1; i >= 0; --i) {
			Node n = t.getNode(i);
			if ( n.ie> n.ib)
				vm_span_id.get(n.ib).put(n.ie, i);
		}
		return true;
	}	
	public static Node setTag(int ib, int ie, String tag, String value){
		int id= findNodeBySpanSoft(ib,ie);
		if (id <0) {
			System.out.println("cannot find tag "+ib+","+ie);
			return null;
		}
		Node n= t.getNode(id);
		n.put(tag, value);
		return n;
	}		
}
