package edu.cmu.lti.nlp.chinese.parser;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.algorithm.structure.MyQueue;
import edu.cmu.lti.algorithm.structure.MyStack;
import edu.cmu.lti.nlp.chinese.util.Tree;
import edu.cmu.lti.nlp.chinese.util.TreeHelper;

/**
*  This class contains a collection of utility methods
*/

public class FeatureExtractor{
  //private static Logger log = Logger.getLogger( FeatureExtractor.class );
  
  public static String lastAction = "NONE";
  public static int bracketCount = 0;

  /**
  * Extract features from the current Stack and Queue for Chinese Parser
  * @param stack the stack containing already constructed partial parse tree
  * @param queue the queue containing items waiting to be parsed
  * 
  * @return String the feature set
  */
  public static String extractFeatureForChinese(MyStack<Tree> stack, MyQueue<Tree> queue){
    List<String> features = extractFeatureListForChinese(stack,queue);
    StringBuffer featureStr = new StringBuffer(); 
    int i=0;
    for(String str: features){
      featureStr.append(str);
      featureStr.append(" ");
    }
    return featureStr.toString();
  }
  
  public static List<String> extractFeatureListForChinese(MyStack<Tree> stack, MyQueue<Tree> queue){
    List<String> feature = new ArrayList<String>(55); 
    //the linear distance between the head-words of S(0) and S(1)
    //-1000 if either of S(0) and S(1) doesn't exist
    //1=1, (2-3)=2, (4-7)=3, (7-)=4
    //feature 1
    Tree sZero = stack.peek(0);
    Tree sOne = stack.peek(1);
    Tree wZero = queue.peek(0);
    if(sZero == null || sOne == null)
      feature.add("-1");
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
      /*
      if(dist == 1 || dist == 2){
      }else if(dist >= 3 && dist <= 5)
        dist = 3;
      else
        dist = 4;
      */  
      feature.add(dist+"");
    }  

    //checks if the queue is empty
    //if so, give 0, otherwise, give 1
    if(queue.empty())
      feature.add("0");
    else
      feature.add("1");

    //check if we are expecting an closing bracket
    if(bracketCount > 0)
      feature.add("1");
    else
      feature.add("0");
    
    //check open close punctuation
    /*
    Tree queueItem = queue.peek();
    if(queueItem == null)
      feature.append("0 ");
    else{
      if(queueItem.getNormalizedLabel().equals("PU")){
        if(queueItem.getHeadWord().matches("[‘“〔〈《「『（［｛〖【]")){
          feature.append("1 ");
        }else if(queueItem.getHeadWord().matches("[’”〕〉》」』）］｝〗】]")){
          feature.append("2 ");
        }else{
          feature.append("0 ");
        }
      }else{
        feature.append("0 ");
      }
    }
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item == null){
        feature.append("0 ");
      }else{
        MyQueue<Tree> q = TreeHelper.getPreterminals(item);
        List<Tree> qList = q.asList();
        boolean found = false;
        for(int j=qList.size()-1;j>=0;j--){
          queueItem = qList.get(j);
          if(queueItem.getNormalizedLabel().equals("PU")){
            if(queueItem.getHeadWord().matches("[‘“〔〈《「『（［｛〖【]")){
              feature.append("1 ");
              found = true;
              break;
            }else if(queueItem.getHeadWord().matches("[’”〕〉》」』）］｝〗】]")){
              feature.append("2 ");
              found = true;
              break;
            }
          }
        }
        if(!found)
          feature.append("0 ");
      }
    }
    */

    //last action feature
    feature.add(lastAction);
    
    //the punctuation count of S(0) and S(1)
    //need to change it so that it calculates the number of puncs between two headwords
    //0=0, 1=1, (2-)=2, -1 if S(0) or S(1) doesn't exist
    //feature 2-3
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item == null){
        feature.add("-1");
      }else{
        int count = TreeHelper.countPunctuation(item);
        if(count > 1)
          count = 2;
        feature.add(count+"");
      }
    }

    //count the punctuations in between the headwords of S0 and S1
    /*
    if(sZero != null && sOne != null){
      int leftToSZero = TreeHelper.countPunctuation(sZero, "L");
      int rightToSOne = TreeHelper.countPunctuation(sOne, "R");
      int count = leftToSZero+rightToSOne;
      if(count > 1)
        count = 2;
      feature.append(count+" ");
    }else{
      feature.append("0 ");
    }
    */

    //new first word last word feature
    //if there a comma immediately preceding S(0), in other words, is the first word of S(0) a comma?
    //but combining into one feature
    int first = 0;
    if(sZero != null){
      Tree firstOfSZero = TreeHelper.getFirstPreterminal(sZero);
      if(firstOfSZero.getNormalizedLabel().equals("PU")){
        if(firstOfSZero.getChild(0).getNormalizedLabel().matches("，|：|、")){
          first = 1;    
        }
      }
    }
    //if there a comma immediately following S(1), in other words, is the last word of S(1) a comma?
    int last = 0;
    if(sOne != null){
      Tree lastOfSOne = TreeHelper.getLastPreterminal(sOne);
      if(lastOfSOne.getNormalizedLabel().equals("PU")){
        if(lastOfSOne.getChild(0).getNormalizedLabel().matches("，|：|、")){
          last = 1;    
        }
      }  
    }  
    if(first == 1 || last == 1)
      feature.add("1");  
    else
      feature.add("0");


    //old first word last word feature
    //if there a comma immediately preceding S(0), in other words, is the first word of S(0) a comma?
    /*
    int first = -1;
    if(sZero != null){
      Tree firstOfSZero = TreeHelper.getFirstPreterminal(sZero);
      if(firstOfSZero.getNormalizedLabel().equals("PU")){
        if(firstOfSZero.getChild(0).getNormalizedLabel().matches("，|：|、")){
          first = 1;    
        }
      }
      if(first == -1)
        first = 0;
    }
    feature.append(first+" ");  
    //if there a comma immediately following S(1), in other words, is the last word of S(1) a comma?
    int last = -1;
    if(sOne != null){
      Tree lastOfSOne = TreeHelper.getLastPreterminal(sOne);
      if(lastOfSOne.getNormalizedLabel().equals("PU")){
        if(lastOfSOne.getChild(0).getNormalizedLabel().matches("，|：|、")){
          last = 1;    
        }
      }  
      if(last == -1)
        last = 0;
    }  
    feature.append(last +" ");  
    */

    //the rhythm features of S(0) and S(1), only on NP, VP, NN, NT, NR, VV, VE, VA, VC
    //feature 4-5
    if(sZero != null && sOne != null && sZero.getNormalizedLabel().matches("NP|VP|NN|NT|NR|VV|VE|VA|VC") &&
      sOne.getNormalizedLabel().matches("NP|VP|NN|NT|NR|VV|VE|VA|VC")){
      for(int i=0;i<2;i++){
        Tree item = stack.peek(i);
        if(item.getNormalizedLabel().matches("NP|VP")){
          int num = TreeHelper.countPreterminals(item);
          if(num > 2)
            num = 3;
          feature.add(num+"");
        }else{
          String word = item.getHeadWord();
          int num = word.codePointCount(0, word.length());
          if(num > 2)
            num = 3;
          feature.add(num+"");
        }
      }
    }else{
      feature.add("-1");
      feature.add("-1");
    }

    //second rhythm feature try
    /*
    if(sZero != null && sOne != null && sZero.getNormalizedLabel().matches("NP|VP|NN|NT|NR|VV|VE|VA|VC|(VP\\*)|(NP\\*)") &&
      sOne.getNormalizedLabel().matches("NP|VP|NN|NT|NR|VV|VE|VA|VC|(VP\\*)|(NP\\*)")){
      for(int i=0;i<2;i++){
        Tree item = stack.peek(i);
        if(item.getNormalizedLabel().matches("NP|VP|(VP\\*)|(NP\\*)")){
          //if(item.getNormalizedLabel().matches("(VP\\*)|(NP\\*)"))
          //  System.err.println("label "+item.getNormalizedLabel()+" matches VP* NP* NP VP");
          int num = TreeHelper.countPreterminals(item);
          if(num > 3)
            num = 4;
          feature.append(num+" ");
        }else{
          String word = item.getHeadWord();
          int num = word.codePointCount(0, word.length());
          if(num > 3)
            num = 4;
          feature.append(num+" ");
        }
      }
    }else{
      feature.append("-1 ");
      feature.append("-1 ");
    }
    */
    //the number of lexical items(words) that have been found (so far) to be dependent of the head-words of S(0) S(1)
    //doesn't include the lexical item itself
    //-1 if S(0) or S(1) doesn't exist
    //feature 6-7
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        int dependentCount = TreeHelper.countDependents(item);
        feature.add(dependentCount+"");
      }else{
        feature.add("-1");
      }
    }
    
    //the number of lexical items(words) that are dominated by S(0) S(1)
    //-1 if S(0) or S(1) doesn't exist
    //feature 8-10
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        int preterminalCount = TreeHelper.countPreterminals(item);
        feature.add(preterminalCount+"");
      }else{
        feature.add("-1");
      }
    }

    //the head-word (and its POS) of S(0),S(1),S(2),S(3)
    for(int i=0;i<4;i++){
      Tree item = stack.peek(i);
      if(item != null){
        feature.add(item.getHeadWord());
        feature.add(item.getHeadWordPOS());
      }else{
        feature.add("NOW");
        feature.add("NOP");
      }
    }
    //the head-word (and its POS) of W(0),W(1),W(2),W(3)
    for(int i=0;i<4;i++){
      Tree item = queue.peek(i);
      if(item != null){
        feature.add(item.getHeadWord());
        feature.add(item.getHeadWordPOS());
      }else{
        feature.add("NOW");
        feature.add("NOP");
      }
    }
    //the non-terminal node of the root of S(0) and S(1)
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        feature.add(item.getNormalizedLabel());
      }else{
        feature.add("NON");
      }
    }

    //the number of children of S(0) and S(1)
    //-1 if S(0) S(1) don't exist
    //0 if S(0) S(1) is preterminal
    /*
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        if(item.isPreterminal())
          feature.append("0 ");
        else{ 
          feature.append(item.numOfChildren()+" ");
        }  
      }else{
        feature.append("-1 ");
      }
    }
    */

    //the non-terminal node, headword and POS of the left child of the root of S(0) and S(1)
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        if(item.isPreterminal()){
          feature.add("NOC");
          feature.add("NOW");
          feature.add("NOP");
        }else{
          Tree leftchild = item.getChild(0);
          feature.add(leftchild.getNormalizedLabel());
          feature.add(leftchild.getHeadWord());
          feature.add(leftchild.getHeadWordPOS());
        }
      }else{
        feature.add("NON");
        feature.add("NON");
        feature.add("NON");
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
          feature.add("NOC");
          feature.add("NOW");
          feature.add("NOP");
        }else{
          int index = 1;
          if(item.numOfChildren() < 2)
            index = 0; 
          Tree rightchild = item.getChild(index);
          feature.add(rightchild.getNormalizedLabel());
          feature.add(rightchild.getHeadWord());
          feature.add(rightchild.getHeadWordPOS());
        }
      }else{
        feature.add("NON");
        feature.add("NON");
        feature.add("NON");
      }
    }
    ////the headword and POS of the left child of the root of S(0) and S(1)
    //for(int i=0;i<2;i++){
    //  Tree item = stack.peek(i);
    //  if(item != null){
    //    if(item.isPreterminal()){
    //      feature.append("NOCHILD ");
    //      feature.append("NOCHILD ");
    //    }else{
    //      feature.append(item.getChild(0).getHeadWord()+" ");
    //      feature.append(item.getChild(0).getHeadWordPOS()+" ");
    //    }
    //  }else{
    //    feature.append("NONEXIST ");
    //    feature.append("NONEXIST ");
    //  }
    //}
    ////the headword and POS of the right child of the root of S(0) and S(1)
    ////if the root only has one child, take it as the right-child
    //for(int i=0;i<2;i++){
    //  Tree item = stack.peek(i);
    //  if(item != null){
    //    if(item.isPreterminal()){
    //      feature.append("NOCHILD ");
    //      feature.append("NOCHILD ");
    //    }else{
    //      int index = 0;
    //      if(item.numOfChildren() == 2)
    //        index = 1;
    //      feature.append(item.getChild(index).getHeadWord()+" ");
    //      feature.append(item.getChild(index).getHeadWordPOS()+" ");
    //    }
    //  }else{
    //    feature.append("NONEXIST ");
    //    feature.append("NONEXIST ");
    //  }
    //}

    //the most recently found lexical dependent(word) and its POS of the head-word of S(0),S(1) that is to the left of S(0) S(1)'s head
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        //Tree dependent = TreeHelper.findLeftDependent(item.getHeadNode(),item);
        Tree dependent = TreeHelper.findLeftDependent(item);
        if(dependent == null){
          feature.add("NOW");
          feature.add("NOP");
        }else{
          feature.add(dependent.getHeadWord());
          feature.add(dependent.getHeadWordPOS());
        }
      }else{
        feature.add("NON");
        feature.add("NON");
      }
    }
    //the most recently found lexical dependent(word) and its POS of the head-word of S(0),S(1) that is to the right of S(0) S(1)'s head
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        //Tree dependent = TreeHelper.findRightDependent(item.getHeadNode(),item);
        Tree dependent = TreeHelper.findRightDependent(item);
        if(dependent == null){
          feature.add("NOW");
          feature.add("NOP");
        }else{
          feature.add(dependent.getHeadWord());
          feature.add(dependent.getHeadWordPOS());
        }
      }else{
        feature.add("NON");
        feature.add("NON");
      }
    }
    //checks if the queue is empty
    //if so, give 1, otherwise, give 0
    //int stackSize = stack.size();
    //if(stackSize == 0)
    //  feature.add("0");
    //else if(stackSize == 1)
    //  feature.add("1");
    //else
    //  feature.add("2");

    //concatenated features  
    //the concatenation of headword POS tags of S(0),S(1)
    String str = "";
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        str+=(item.getHeadWordPOS());
      }else{
        str+=("NOP");
      }
      str+="-";
    }
    feature.add(str.substring(0,str.length()-1));
    //the concatenation of root non-terminal of S(0) and S(1)
    str = "";
    for(int i=0;i<2;i++){
      Tree item = stack.peek(i);
      if(item != null){
        str+=(item.getNormalizedLabel());
      }else{
        str+=("NON");
      }
      str+="-";
    }
    feature.add(str.substring(0,str.length()-1));
    //the concatenation of the headword POS of S(0) and W(0)
    str = "";
    if(sZero == null)
      str+= "NOP-";
    else
      str += sZero.getHeadWordPOS()+"-";
    if(wZero!= null){
      str += (wZero.getHeadWordPOS());
    }else{
      str += ("NOP");
    }
    feature.add(str);
    //the concatenation of the headword POS of S(1) and W(0)
    str = "";
    if(sOne == null)
      str+= "NOP-";
    else
      str += sOne.getHeadWordPOS()+"-";
    if(wZero != null){
      str += (wZero.getHeadWordPOS());
    }else{
      str += ("NOP");
    }
    feature.add(str);
    //the concatenation of the POS of the most recently found right lexical dependent of the head of S(0) and POS of W(0)
    str = "";
    if(sZero != null){
      //Tree dependent = TreeHelper.findRightDependent(item.getHeadNode(),item);
      Tree dependent = TreeHelper.findRightDependent(sZero);
      if(dependent == null){
        str += ("NOP-");
      }else{
        str += (dependent.getHeadWordPOS()+"-");
      }
    }else{
      str += ("NON-");
    }
    
    if(wZero != null){
      str += (wZero.getHeadWordPOS());
    }else{
      str += ("NOP");
    }
    feature.add(str);
      
    return feature;
  }

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

  public static List<String> extractFeatureList(MyStack<Tree> stack, MyQueue<Tree> queue){
    int lang = 0;
    if(stack.size() > 0)
      lang = stack.peek(0).getLanguage();
    else if(queue.size() > 0)
      lang = queue.peek(0).getLanguage();

    if(lang == Tree.CHINESE)
      return extractFeatureListForChinese(stack, queue);
    else if(lang == Tree.ENGLISH){
      System.err.println("ENGLISH not supported yet");
    }else
      System.err.println("Error in FeatureExtractor: LANG not defined");
    return null;
  }  

  public static String extractFeature(
  		MyStack<Tree> stack, MyQueue<Tree> queue){
    int lang = 0;
    if(stack.size() > 0)
      lang = stack.peek(0).getLanguage();
    else if(queue.size() > 0)
      lang = queue.peek(0).getLanguage();

    if(lang == Tree.CHINESE)
      return extractFeatureForChinese(stack, queue);
    else if(lang == Tree.ENGLISH)
      return extractFeatureForEnglish(stack, queue);
    else
      System.err.println("Error in FeatureExtractor: LANG not defined");
    return null;
  }  
  
}
