/**
 * 
 */
package edu.cmu.lti.nlp.parsing.FTree;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;


/**
 * @author nlao
 *
 */
public class FParseTreeFeatures {	

	public static String getNVI(String pos){
		if (pos.startsWith("N")) return CTag.N;
		if (pos.startsWith("V")) return CTag.V;
		return CTag.I;
	}
	public TreeParse t;
	
	public VectorI v2,v1;//store stacks for path features
	public VectorX<Integer> vi1,vi2; //first and second part of the path
	public Integer ic; //id of the common ancestor
//	protected static String s1,s2;//first and second part of the path
	public void findPath(int id1,int id2){
		v1 = t.getStack(id1);
		v2 = t.getStack(id2);
		if (v1==null || v2==null) return;
		int lc= v1.idxCommonPrefix(v2)-1;
		ic= v2.get(lc);		
		vi1=v1.mid(lc).reverseOn();
		vi2=v2.mid(lc);
		return;
	}
	public String getPathFeature(String tag) {
		String s1=t.vNode.getVS(tag, vi1).join(">");
		String s2=t.vNode.getVS(tag, vi2).join("<");
		return s1+s2;
	}
}

