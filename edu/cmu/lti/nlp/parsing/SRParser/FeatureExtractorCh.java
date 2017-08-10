package edu.cmu.lti.nlp.parsing.SRParser;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.nlp.parsing.tree.CPOS;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;

//TODO: take care of getNormalizedLabel
public class FeatureExtractorCh extends FeatureExtractor{
	private static FeatureExtractorCh instance = null;

	public static FeatureExtractorCh getInstance() {
		if (instance == null) instance = new FeatureExtractorCh();
		return instance;
	}

	public FeatureExtractorCh() {
		//super(FeatureExtractorCh.class);
		//fex = new FeatureExtractorCh();
	}		
	protected void addWordPOS(String prefix, int id){// Node n){
		if (id>=0)
			addWordPOS(prefix, tree.getNode(id).t);
		else
			addWordPOS(prefix, null);
	}
	protected void addWordPOS(String prefix, Token t){// Node n){
    if(t==null){//id<0){//n==null){
  	 	feature.put(prefix+".w", "-");
  	 	feature.put(prefix+".pos", "-");
    	return;
    }
	 	feature.put(prefix+".w", t.getText());
	 	feature.put(prefix+".pos", t.getPOS());		
	}	
	 
  //Node s0, s1;
  Token q0;
	Token feature;//  = new Token();
	TreeParse tree;
	//TVector<Token> queue;
	//VectorI stack;
	//addSingleStackFeatures
	
	
	private void addDistFeature(Node n0, Node n1){
    //the linear distance between the head-words of S(0) and S(1)
    //-1000 if either of S(0) and S(1) doesn't exist
    //1~1, (2~3)=2, (4~7)=3, (7+)=4
    int dist = n0.ib-n1.ib;
    if(dist == 1){
    }else if(dist == 2 || dist == 3){
      dist = 2;
    }else if(dist >= 4 && dist <= 7)
      dist = 3;
    else
      dist = 4;
    feature.ms.put("s.dist",dist+"");
		
	}

	
  protected void addConcatFeature(String prefix, int i1, int i2){
  	addConcatFeature(prefix, tree.getNode(i1).t, tree.getNode(i2).t);
  }
  protected void addConcatFeature(String prefix, Token t1, Token t2){
    feature.put(prefix+".pos",
    		t1.getPOS()+"-"+t2.getPOS() );
  }
  /**
  * Extract features from the current Stack and Queue for Chinese Parser
  * Naming convention:  s2,s1,s0, q0,q1...
  * @param stack the stack containing already constructed partial parse tree
  * @param queue the queue containing items waiting to be parsed 
  * @return String the feature set
  */
  public SetS extractFeature(VectorX<Token> queue ,VectorI stack, TreeParse tree){
  	//init variables
  	this.tree = tree;
  	//this.stack = stack;
  	//this.queue = queue;
  	feature  = new Token();
  	//s0 = stack.size()>0?tree.getNode(stack.get(0)):null;
  	//s1 = stack.size()>1?tree.getNode(stack.get(1)):null;
  	q0 = queue.size()>0?queue.lastElement():null; 	
   	
  	//int idx;
  	VectorX<Token> vq = queue.reverse();
  	VectorX<Node> vn = tree.getNodes((VectorI) stack.reverse());

    //checks if the queue is empty
    //if so, give 0, otherwise, give 1
    if(queue.isEmpty())
      feature.put("|queue|","0");
    else
      feature.put("|queue|","1");
    //check if we are expecting an closing bracket
    if(bracketCount > 0)
      feature.put("#bracket","1");
    else
      feature.put("#bracket","0");
 
    feature.put("lastA",lastAction);
    
    //simple queue features
    for (int iq=0;iq<queue.size() && iq< 4; ++iq){
    	Token q = queue.get(iq); 
			addWordPOS("q"+iq+".h", q);//word and POS
		}	

    //simple stack features
		for (int is=0;is<vn.size() && is< 4; ++is){
			Node n = vn.get(is);
			addWordPOS("s"+is+"r", n.id);//word and POS
			addWordPOS("s"+is+".h", n.getHead());//word and POS of its head
		}  	
		
		if (vn.size()>=1) {
			Node s0= vn.get(0);
			Node h0= tree.getNode(s0.getInt(CTag.head));
			
			if (queue.size()>=1){
				Token q0 = queue.get(0);
				addConcatFeature("h0q0", h0.t, q0);
				
				Integer idx = s0.getHeadChildIdx();//-1;
				if (idx !=null)
					if (idx <s0.vc.size()-1)	{
					Node rd = tree.getNode( s0.vc.get(idx+1));
					addConcatFeature("rdq0", rd.t, q0);
				}
			}		
			
			if (vn.size()>=2) {
		    //concatenated features
				VectorX<Node> vn01 = vn.sub(0,2);
				Node s1= vn.get(1);

				addDistFeature(s0, s1);

				Node h1= tree.getNode(s1.getInt(CTag.head));
				addConcatFeature("h0h1", h0.t, h1.t);				
				
				if (queue.size()!=0){
					Token q0 = queue.get(0);
					addConcatFeature("h1q0", h1.t, q0);					
				}
			}
		}
		
		for (int is=0;is<vn.size() && is< 2; ++is){
			Node n = vn.get(is);
			//the punctuation count of S(0) and S(1)
	    //need to change it so that it calculates the number of puncs between two headwords
	    //0=0, 1=1, (2-)=2, -1 if S(0) or S(1) doesn't exist
	  	int c = n.getInt(CPOS.PU);
	    feature.put("s"+is+".#PU",Math.min(c,2)+"");
	    
	    //if there a comma immediately preceding S(0), 
	    //in other words, is the first word of S(0) a comma?
	    if(is==0)  	if (tree.getNode(n.ib).t.getPOS().equals(CPOS.PU))
	        feature.put("PU_s0","1");   		
	    
	    //if there a comma immediately following S(1), 
	    //in other words, is the last word of S(1) a comma?
	    if(is==1)  	if (tree.getNode(n.ie-1).t.getPOS().equals(CPOS.PU))
	        feature.put("s1_PU","1");
	    
	    //the rhythm features of S(0) and S(1),
	    //only on NP, VP, NN, NT, NR, VV, VE, VA, VC
	    //feature 4-5
	  	feature.put("s"+is+".rhythm", Math.min(3, n.length())+"");

			//if(s0 == null || s1 == null) return;
	    //final String paNV= "NP|VP|NN|NT|NR|VV|VE|VA|VC";    
	    //normalization is by removing any functional-marker
	    //(e.g -PN, =2) from the label;
	    //if (! s0.t.getPOS().matches(paNV)) return; 
	    //  s1.getNormalizedLabel().matches("NP|VP|NN|NT|NR|VV|VE|VA|VC")){

	    //the number of lexical items(words) that have been found (so far)
	    //to be dependent of the head-words of S(0) S(1)
	    //doesn't include the lexical item itself
	    //-1 if S(0) or S(1) doesn't exist
		 //not easy(worthy) to implement
	  	
			//Node h = tree.getNode(n.getHead());
		 	//feature.put("s"+is+".NumHDependWord", h.length()+"");

	    //the number of lexical items(words) that 
	    //are dominated by S(0) S(1)
	    //-1 if S(0) or S(1) doesn't exist
		 	feature.put("s"+is+".#DomW", n.length()+"");
		 	
		 	
		 	//addLeftChildFeatures
	    if(n.isLeaf())
	  		addWordPOS("s"+is+".lc", -1);
	    else
	  		addWordPOS("s"+is+".lc", n.vc.get(0));
	  
	   //addRightChildFeatures
	    //the non-terminal node of the right child of the root of S(0) and S(1)
	    //if the root only has one child, consider this as not having right child
	    if(n.isLeaf())
	  		addWordPOS("s"+is+".rc", -1);
	    else
	  		addWordPOS("s"+is+".rc", n.vc.lastElement());
	      
	    
			Integer idx = n.getHeadChildIdx();//-1;
			if (idx!=null){
				//addLeftDependent
		    // the most recently found lexical dependent(word)
				// and its POS of the head-word of S(0),S(1)
				// that is to the left of S(0) S(1)'s head
					int ild = idx>=1?  n.vc.get(idx-1): -1;
					addWordPOS("s"+is+".ld", ild);
		  
		    //addRightDependent
		    // the most recently found lexical dependent(word)
				// and its POS of the head-word of S(0),S(1)
				// that is to the left of S(0) S(1)'s head
				int ird = idx<n.vc.size()-1?  n.vc.get(idx+1): -1;
	  		addWordPOS("s"+is+".rd", ird);		
			}

		}
    return feature.ms.toStringPairSet();
  }


}
