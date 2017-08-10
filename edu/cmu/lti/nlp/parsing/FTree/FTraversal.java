package edu.cmu.lti.nlp.parsing.FTree;

import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.nlp.parsing.tree.Tree;

public class FTraversal {
	private static  FTraversal instance =null;
	public static FTraversal getInstance() {
		if (instance==null) 	 instance = new FTraversal();			
		return instance;
	}

	VectorI vi;
	Tree tree;
	boolean HeadFirst;
	private void visitRecur(int id){	
		if (HeadFirst)
			vi.add(id);
		for (int ic:tree.getNode(id).vc)
			visitRecur(ic);
		if (!HeadFirst)
			vi.add(id);
		return;			
	}
	//0=HeadFirst,  1=HeadLast,x=HeadMiddle,
	public VectorI visit(Tree tree, boolean HeadFirst){// iStyle){
		this.tree = tree;
		this.HeadFirst = HeadFirst;
		vi = new VectorI();
		vi.ensureCapacity(tree.vNode.size());
		visitRecur(tree.root);
		return vi;		
	}
}
