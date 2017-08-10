package edu.cmu.lti.nlp.parsing.FTree;

import java.io.BufferedReader;

import edu.cmu.lti.algorithm.container.VectorB;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;
import edu.cmu.lti.util.file.FFile;

public class FTransformBinary {
	public enum EMode{
		HeadRule	//might have error in it
		, RightBranching
		, LeftBranching//good for incremental
	}
	public EMode mode= EMode.LeftBranching;
	private TreeSyntax tree;
	//private TreeSyntax tree1;
	private FindHead fh = FindHead.getInstance();

	private static  FTransformBinary instance =null;
	public static FTransformBinary getInstance() {
		if (instance==null) 	 
			instance = new FTransformBinary();			
		return instance;
	}
	/* 
	private int createNode1(Node n, VectorI vc){
  	String pos = n.t.getPOS();
  	Node n1=null;
    if (vc.size()<= 2){
    	if (vc.size()==0){ //deal with a terminal node
    		n1 = tree1.addTerminal(n.t);
    	}
    	else{
    		n1 = tree1.addNonTerminal(vc, pos);
    	}
    }
    else{//need binarization

	    VectorS vpos = (VectorS)tree1.v_node.sub(vc).getVString("pos");
	    int ihidx =fh.findHeadChild(pos, vpos) ;
	
	    VectorI vc1= new VectorI(2);
	    //int i1, i2;
	    if (ihidx==-1) 
	    	return -1;
	    if(ihidx == 0){//the head is left-most, 
	      //strip off the right-most child
	    	vc1.set(1, vc.pop());
	    	vc1.set(0, createNode1(n, vc));
	    }else{//the head is right-most or somewhere in the middle
	      //strip off the left-most child
	    	vc1.set(0, vc.popFrontOn());
	    	vc1.set(1, createNode1(n, vc));	    	
	    }
	  	n1 = tree1.addNonTerminal(vc1, "*"+pos);
    }
    
   	return n1.id;
  }


	 // @param id: node ID in the old tree
	 //@return  node ID in the new tree
  private  int binarizeRecur1(int id){
  	Node n = tree.getNode(id);
  	String pos = n.t.getPOS();
    //now recursively transform the new tree's children  
  	VectorI vc= new VectorI();
    for(int ic:n.vc){
    	//n1.
    	vc.add(binarizeRecur1(ic));
    }    
    int id1= createNode1(n, vc);
    tree1.getNode(id1).t.setPOS(pos);//reverse the * on pos
  	return id1;
  }
  private TreeSyntax binarize1(TreeSyntax _tree){
  	tree = _tree;
  	tree1 = new TreeSyntax();
  	//fh.t = tree1;
  	tree1.root= binarizeRecur1(tree.root);
  	tree1.v_word = tree.v_word;
  	tree1.reorder();  	
  	tree1.setSpans(false);
  	return tree1;
  }
*/
  
  //check if extra node is needed to binarize Node n 
  protected int createNode(Node n, String pos){//, VectorI vc){
    if (n.vc.size()<= 2)//nothing need to be done
    	return n.id;
    
    //need binarization
    String pos1=null;
//  	if(pos.matches("NP|IP|VP|PRN|QP|UCP|VPT|CP|ADJP|PP|VNV|FRAG"))
  		pos1 = CTag.star+pos; 
//    else   	pos1 = CTag.star+"TEMP";
    
  	Boolean bStripLeft=null; 
  	switch(mode){
  	case HeadRule:
      VectorS vpos = (VectorS)tree.vNode.sub(n.vc).getVS("pos");
      int ihidx =fh.findHeadChild(pos, vpos) ;
      if(ihidx == 0)
      	bStripLeft=false;
      else 
      	bStripLeft=true;
      break;
  	case RightBranching:    	bStripLeft=true;     	break; 		
  	case LeftBranching:  	bStripLeft=false;     	break; 		
  	}
  		
	
    VectorI vc1= new VectorI(2);//new children list (binary)
    Node n1 =null;
    //chinese is head last
    if(!bStripLeft){
    	//the head is left-most, 
      //strip off the right-most child
    	vc1.set(1, n.vc.pop());
	  	n1 = tree.addNonTerminal(n.vc, pos1);
    	vc1.set(0, n1.id);
    }else{
    	//the head is right-most or somewhere in the middle
      //strip off the left-most child
    	vc1.set(0, n.vc.popFrontOn());
	  	n1 = tree.addNonTerminal(n.vc, pos1);
    	vc1.set(1, n1.id);	    	
    }    
    createNode(n1,pos);//check if further binarization is need
    n1.iparent = n.id;
    n.vc = vc1;    
   	return n.id;
  }
  
  public TreeSyntax binarizeOn(TreeSyntax tree){
  	this.tree = tree;
  	if (mode.equals(EMode.HeadRule))
  		fh.findHead(tree);
  	int m = tree.vNode.size();
  	for (int i=0; i<m; ++i){
  		Node n = tree.getNode(i);
     	createNode(n, n.t.getPOS());
  	}
  	tree.reorder();  	
  	tree.setSpans(false);
		fh.findHead(tree);
  	return tree;
  }
  /**
   * Detransform the tree from binary and unary branching only to multi-branching
   * @param tree the tree to be detransformed
   */
   public  TreeSyntax debinarizeOn(TreeSyntax tree){
   	this.tree = tree;
  	int m = tree.vNode.size();
		VectorB vkeep= new VectorB(m,true);
  	for (int id=0; id<m; ++id){
  		Node n = tree.getNode(id);
  		String pos = n.t.getPOS();
  		if (pos.startsWith(CTag.star)){
  			tree.moveNodeUp(id);
				vkeep.set(id,false);  			
  		}
  	}
		tree.doTruncate(vkeep);
  	tree.reorder();  	
  	tree.setSpans(false);  	
   	return tree;
   }
   
   
/*  
  // Detransform the tree from binary and unary branching only to multi-branching
  public  TreeSyntax debinarize(TreeSyntax tree){
  	this.tree = tree;
  	tree1 = new TreeSyntax();
  	tree1.root = debinarizeRecur(tree.root).get(0);
  	return tree1;
  }
  
  //if it is a *node, 
  //debinarizing will result in more than one id 
  protected  VectorI debinarizeRecur(int id){
  	Node n = tree.getNode(id);
  	if (n.isLeaf()){//leaf case
  		Node n1 =tree1.addTerminal(n.t);
  		return new VectorI(1,n1.id);
  	}
  	
  	VectorI vc= new VectorI();
  	for (int ic: n.vc)
  		vc.addAll(debinarizeRecur(ic));
  	
  	String pos = n.t.getPOS();
  	
  	//internal node does not create new node
  	if (pos.startsWith("*")) 
  		return vc;
  	else{
  		Node n1 = tree1.addNonTerminal(vc, pos);
  		return new VectorI(1,n1.id);
  	}
  }
  */
	public static void main(String[] args) {
		try {
			FTransformBinary tb=FTransformBinary.getInstance();
//			Param.overwrite(args);
			String folder = "/usr2/nlao/code_javelin/j3/run/srParser/";
			String treeFile="bracketed.271-300.1line.prep";
			BufferedReader br = FFile.newReader(folder+treeFile);
			String line = null;int nSent=0;
			while ((line = FFile.readLine(br)) != null) {
				++nSent;	if (nSent % 10==0)		System.out.print("t");
				TreeSyntax tree0=TreeSyntax.parseNew(line);
				if (tree0==null) continue;
				//if (p.dbg>0)		System.out.println(tree0);			
			
				TreeSyntax tree1 = tb.binarizeOn((TreeSyntax)tree0.clone());
				//if (p.dbg>0)		System.out.println(tree);

				TreeSyntax tree2 = tb.debinarizeOn((TreeSyntax)tree1.clone());
				String str0 = tree0.printBracketString();
				String str2 = tree2.printBracketString();
				if (str0.equals(str2))
					continue;
				System.out.println("failed transformation\n"		+str0+"\n"+str2+"\n"
						+tree0+"\n"+tree1+"\n"+tree2+"\n");
			}

		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
