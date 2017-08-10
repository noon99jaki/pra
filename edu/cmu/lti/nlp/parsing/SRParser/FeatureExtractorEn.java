package edu.cmu.lti.nlp.parsing.SRParser;

import edu.cmu.lti.algorithm.structure.MyQueue;
import edu.cmu.lti.algorithm.structure.MyStack;
import edu.cmu.lti.nlp.chinese.util.Tree;
import edu.cmu.lti.nlp.chinese.util.TreeHelper;

public class FeatureExtractorEn {

	

  /**
  * Extract features from the current Stack and Queue for English Parser
  * @param stack the stack containing already constructed partial parse tree
  * @param queue the queue containing items waiting to be parsed
  * 
  * @return String the feature set
  */
  public static String extractFeatureForEnglish(MyStack<Tree> stack, MyQueue<Tree> queue){
    StringBuffer feature = new StringBuffer(); 
    //the linear distance between the head-words of S(0) and S(1)
    //-1000 if either of S(0) and S(1) doesn't exist
    //1=1, (2-3)=2, (4-)=3
    //feature 1
    Tree sZero = stack.peek(0);
    Tree sOne = stack.peek(1);
    if(sZero == null || sOne == null)
      feature.append("-1 ");
    else{
      int sequenceZero = sZero.getHeadNode().sequenceNo;
      int sequenceOne = sOne.getHeadNode().sequenceNo;
      if(sequenceZero == -1 || sequenceOne == -1)
        System.err.println("Error in FeatureExtractor: sequnceNo of headnodes equal to -1");
      int dist = sequenceZero-sequenceOne;
      if(dist == 1){
      }else if(dist == 2 || dist == 3){
        dist = 2;
      }else if(dist >= 4 && dist <= 7)
        dist = 3;
      else
        dist = 4;
      feature.append(dist+" ");
    }  
    //the number of lexical items(words) that have been found (so far) to be dependent of the head-words of S(0) S(1)
    //doesn't include the lexical item itself
    //-1 if S(0) or S(1) doesn't exist
    //feature 6-7
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        int dependentCount = TreeHelper.countDependents(item);
        feature.append(dependentCount+" ");
      }else{
        feature.append("-1 ");
      }
    }
    //the head-word (and its POS) of S(0),S(1),S(2),S(3)
    for(int i=0;i<4;i++){
      Tree item = stack.peek(i);
      if(item != null){
        feature.append(item.getHeadWord()+" ");
        feature.append(item.getHeadWordPOS()+" ");
      }else{
        feature.append("NOWORD ");
        feature.append("NOPOS ");
      }
    }
    //the head-word (and its POS) of W(0),W(1),W(2),W(3)
    for(int i=0;i<4;i++){
      Tree item = queue.peek(i);
      if(item != null){
        feature.append(item.getHeadWord()+" ");
        feature.append(item.getHeadWordPOS()+" ");
      }else{
        feature.append("NOWORD ");
        feature.append("NOPOS ");
      }
    }
    //the non-terminal node of the root of S(0) and S(1)
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        feature.append(item.getNormalizedLabel()+" ");
      }else{
        feature.append("NONEXIST ");
      }
    }
    //the non-terminal node, headword and POS of the left child of the root of S(0) and S(1)
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        if(item.isPreterminal()){
          feature.append("NOCHILD ");
          feature.append("NOWORD ");
          feature.append("NOPOS ");
        }else{
          Tree leftchild = item.getChild(0);
          feature.append(leftchild.getNormalizedLabel()+" ");
          feature.append(leftchild.getHeadWord()+" ");
          feature.append(leftchild.getHeadWordPOS()+" ");
        }
      }else{
        feature.append("NONEXIST ");
        feature.append("NONEXIST ");
        feature.append("NONEXIST ");
      }
    }
    //the non-terminal node of the right child of the root of S(0) and S(1)
    //if the root only has one child, consider this as not having right child
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        //if(item.isPreterminal() || item.numOfChildren() < 2){
        //  feature.append("NOCHILD ");
        //}else{
        //  feature.append(item.getChild(1).getNormalizedLabel()+" ");
        //}
        if(item.isPreterminal()){
          feature.append("NOCHILD ");
          feature.append("NOWORD " );
          feature.append("NOPOS ");
        }else{
          int index = 1;
          if(item.numOfChildren() < 2)
            index = 0; 
          Tree rightchild = item.getChild(index);
          feature.append(rightchild.getNormalizedLabel()+" ");
          feature.append(rightchild.getHeadWord()+" ");
          feature.append(rightchild.getHeadWordPOS()+" ");
        }
      }else{
        feature.append("NONEXIST ");
        feature.append("NONEXIST ");
        feature.append("NONEXIST ");
      }
    }
    //the most recently found lexical dependent(word) and its POS of the head-word of S(0),S(1) that is to the left of S(0) S(1)'s head
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        //Tree dependent = TreeHelper.findLeftDependent(item.getHeadNode(),item);
        Tree dependent = TreeHelper.findLeftDependent(item);
        if(dependent == null){
          feature.append("NOWORD ");
          feature.append("NOPOS ");
        }else{
          feature.append(dependent.getHeadWord()+" ");
          feature.append(dependent.getHeadWordPOS()+" ");
        }
      }else{
        feature.append("NONEXIST ");
        feature.append("NONEXIST ");
      }
    }
    //the most recently found lexical dependent(word) and its POS of the head-word of S(0),S(1) that is to the right of S(0) S(1)'s head
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        //Tree dependent = TreeHelper.findRightDependent(item.getHeadNode(),item);
        Tree dependent = TreeHelper.findRightDependent(item);
        if(dependent == null){
          feature.append("NOWORD ");
          feature.append("NOPOS ");
        }else{
          feature.append(dependent.getHeadWord()+" ");
          feature.append(dependent.getHeadWordPOS()+" ");
        }
      }else{
        feature.append("NONEXIST ");
        feature.append("NONEXIST ");
      }
    }
    return feature.toString();
  }

}
