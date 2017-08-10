package edu.cmu.lti.nlp.chinese.util;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;

import edu.cmu.lti.algorithm.structure.MyQueue;

/**
*  This class contains a collection of utility methods
*/

public class TreeHelper{

  //private static Logger log = Logger.getLogger( TreeHelper.class );
  
  public static HashMap<String,String[]> headRulePriorityListForC = new HashMap<String,String[]>(30);
  public static HashMap<String,Integer> headRuleDirectionForC = new HashMap<String,Integer>(30);
  public static HashMap<String,String[]> headRulePriorityListForE = new HashMap<String,String[]>(30);
  public static HashMap<String,Integer> headRuleDirectionForE = new HashMap<String,Integer>(30);
  /* vertical seperator width used in pretty printing */
  public final static int V_SEPERATOR_HEIGHT = 4;
  private static Writer writer = null;

  // private field used by getPreterminals() method
  private static MyQueue<Tree> preterminalQueue= null;
  // private field used by countPreterminals() method
  private static int preterminalCount = 0;
  // private field used by countPunctuation() method
  private static int punctuationCount = 0;

  static{
    //For Chinese
    headRulePriorityListForC.put("IP", new String[]{"IP","VP","VV"});
    headRulePriorityListForC.put("ADJP", new String[]{"ADJP","JJ","AD","NN","CS"});
    headRulePriorityListForC.put("ADVP", new String[]{"ADVP","AD","CS","JJ","NP","PP","P","VA","VV"});
    headRulePriorityListForC.put("CLP", new String[]{"CLP","M"});
    headRulePriorityListForC.put("CP", new String[]{});
    headRulePriorityListForC.put("DNP", new String[]{"DNP","DEG","DEC"});
    headRulePriorityListForC.put("DP", new String[]{"DP","DT"});
    headRulePriorityListForC.put("DVP", new String[]{"DVP","DEV"});
    headRulePriorityListForC.put("FRAG", new String[]{"VV","NR","NN"});
    headRulePriorityListForC.put("LCP", new String[]{"LCP","LC"});
    headRulePriorityListForC.put("LST", new String[]{"LST","CD","OD"});
    headRulePriorityListForC.put("NP", new String[]{"NP","NN","NT","NR","QP"});
    headRulePriorityListForC.put("PP", new String[]{"PP","P"});
    headRulePriorityListForC.put("PRN", new String[]{"NP","IP","VP","NT","NR","NN"});
    headRulePriorityListForC.put("QP", new String[]{"QP","CLP","CD","OD"});
    headRulePriorityListForC.put("UCP", new String[]{});
    headRulePriorityListForC.put("VP", new String[]{"VP","VA","VC","VE","VV","BA","LB","VCD","VSB","VRD","VNV","VCP"});
    headRulePriorityListForC.put("VCD", new String[]{"VCD","VV","VA","VC","VE"});
    headRulePriorityListForC.put("VRD", new String[]{"VRD","VV","VA","VC","VE"});
    headRulePriorityListForC.put("VSB", new String[]{"VSB","VV","VA","VC","VE"});
    headRulePriorityListForC.put("VCP", new String[]{"VCP","VV","VA","VC","VE"});
    headRulePriorityListForC.put("VNV", new String[]{"VNV","VV","VA","VC","VE"});
    headRulePriorityListForC.put("VPT", new String[]{"VPT","VV","VA","VC","VE"});
    //error prone ones
    headRulePriorityListForC.put("INTJ", new String[]{});
    headRulePriorityListForC.put("NN", new String[]{});
    headRulePriorityListForC.put("MSP", new String[]{});
    headRulePriorityListForC.put("TEMP", new String[]{});

    //For Chinese
    //1 means right, 0 mean left
    headRuleDirectionForC.put("IP",  1); 
    headRuleDirectionForC.put("ADJP",1);  
    headRuleDirectionForC.put("ADVP",1); 
    headRuleDirectionForC.put("CLP", 1); 
    headRuleDirectionForC.put("DNP", 1); 
    headRuleDirectionForC.put("DP",  0); 
    headRuleDirectionForC.put("DVP", 1); 
    headRuleDirectionForC.put("FRAG", 1); 
    headRuleDirectionForC.put("LCP", 1); 
    headRuleDirectionForC.put("LST", 0); 
    headRuleDirectionForC.put("CP",  1); 
    headRuleDirectionForC.put("NP",  1); 
    headRuleDirectionForC.put("PP",  0); 
    headRuleDirectionForC.put("PRN", 1); 
    headRuleDirectionForC.put("QP",  1); 
    headRuleDirectionForC.put("UCP", 1); 
    headRuleDirectionForC.put("VP",  0);  
    headRuleDirectionForC.put("VCD", 1); 
    headRuleDirectionForC.put("VRD", 1); 
    headRuleDirectionForC.put("VSB", 1); 
    headRuleDirectionForC.put("VCP", 1); 
    headRuleDirectionForC.put("VNV", 1); 
    headRuleDirectionForC.put("VPT", 1); 
    //error prone ones
    headRuleDirectionForC.put("INTJ", 0); 
    headRuleDirectionForC.put("NN", 1); 
    headRuleDirectionForC.put("MSP", 0); 
    headRuleDirectionForC.put("TEMP", 1); 
    //For English
    headRulePriorityListForE.put("ADJP", new String[]{"NNS","QP","NN","NP","$","ADVP","JJ","VBN","VBG","ADJP","JJR","JJS","DT","FW","RBR","RBS","SBAR","RB"});
    headRulePriorityListForE.put("ADVP", new String[]{"RB","RBR","RBS","FW","ADVP","TO","CD","JJR","JJ","IN","NP","JJS","NN"});
    headRulePriorityListForE.put("CONJP", new String[]{"CC","RB","IN"});
    headRulePriorityListForE.put("FRAG", new String[]{});
    headRulePriorityListForE.put("INTJ", new String[]{});
    headRulePriorityListForE.put("LST", new String[]{"LS"});
    headRulePriorityListForE.put("NAC", new String[]{"NN","NNS","NNP","NNPS","NP","NAC","EX","CD","QP","PRP","VBG","JJ","JJS","JJR","ADJP","FW"});
    headRulePriorityListForE.put("PP", new String[]{"IN","TO","VBG","VBN","RP","FW"});
    headRulePriorityListForE.put("PRN", new String[]{});
    headRulePriorityListForE.put("PRT", new String[]{"RP"});
    headRulePriorityListForE.put("QP", new String[]{"$","IN","NNS","NN","JJ","RB","DT","CD","NCD","QP","JJR","JJS"});
    headRulePriorityListForE.put("RRC", new String[]{"VP","NP","ADVP","ADJP","PP"});
    headRulePriorityListForE.put("S", new String[]{"TO","IN","VP","S","SBAR","ADJP","UCP","NP"});
    headRulePriorityListForE.put("SBAR", new String[]{"WHNP","WHPP","WHADVP","WHADJP","IN","DT","S","SQ","SINV","SBAR","FRAG"});
    headRulePriorityListForE.put("SBARQ", new String[]{"SQ","S","SINV","SBARQ","FRAG"});
    headRulePriorityListForE.put("SINV", new String[]{"VBZ","VBD","VBP","VB","MD","VP","S","SINV","ADJP","NP"});
    headRulePriorityListForE.put("SQ", new String[]{"VBZ","VBD","VBP","VB","MD","VP","SQ","AUX"});
    headRulePriorityListForE.put("UCP", new String[]{});
    headRulePriorityListForE.put("VP", new String[]{"TO","VBD","VBN","MD","VBZ","VB","VBG","VBP","VP","ADJP","NN","NNS","NP"});
    headRulePriorityListForE.put("WHADJP", new String[]{"CC","WRB","JJ","ADJP"});
    headRulePriorityListForE.put("WHADVP", new String[]{"CC","NP","WRB"});
    headRulePriorityListForE.put("WHNP", new String[]{"NN","NNS","NP","JJ","WDT","WP","WP$","$","WHADJP","WHPP","WHNP"});
    headRulePriorityListForE.put("WHPP", new String[]{"IN","TO","FW"});
    //error prone ones
    headRulePriorityListForE.put("NX", new String[]{"NNP","NN","NNS","NP"});
    headRulePriorityListForE.put("X", new String[]{});
    headRulePriorityListForE.put("TEMP", new String[]{});

    //For English
    //1 means right, 0 mean left
    headRuleDirectionForE.put("NP",  1); 
    headRuleDirectionForE.put("ADJP",0);  
    headRuleDirectionForE.put("ADVP",1); 
    headRuleDirectionForE.put("CONJP", 1); 
    headRuleDirectionForE.put("FRAG", 1); 
    headRuleDirectionForE.put("INTJ", 0); 
    headRuleDirectionForE.put("LST", 1); 
    headRuleDirectionForE.put("NAC", 0); 
    headRuleDirectionForE.put("PP", 1); 
    headRuleDirectionForE.put("PRN", 0); 
    headRuleDirectionForE.put("PRT", 1); 
    headRuleDirectionForE.put("QP", 0); 
    headRuleDirectionForE.put("RRC", 1); 
    headRuleDirectionForE.put("S", 0); 
    headRuleDirectionForE.put("SBAR", 0); 
    headRuleDirectionForE.put("SBARQ", 0); 
    headRuleDirectionForE.put("SINV", 0); 
    headRuleDirectionForE.put("SQ", 0); 
    headRuleDirectionForE.put("UCP", 1); 
    headRuleDirectionForE.put("VP", 0); 
    headRuleDirectionForE.put("WHADJP", 0); 
    headRuleDirectionForE.put("WHADVP", 1); 
    headRuleDirectionForE.put("WHNP", 1); 
    headRuleDirectionForE.put("WHPP", 1); 
    //error prone ones
    headRuleDirectionForE.put("NX", 1); 
    headRuleDirectionForE.put("X", 1); 
    headRuleDirectionForE.put("TEMP", 1); 
  }  


 /**
 * Constructs a Tree from a parenthesized String representation of a parse tree.
 * Preterminal nodes with labels preceded by "*" (e.g. "*NP") are set as marked.
 * 
 * @param str the String representation of a parse tree
 * @param lang the language of the original text
 * @return the constructed Tree
 */
public static Tree buildTree(String str, int lang){
    str = str.trim();
    int leftBracketIndex = str.indexOf('(');
    int rightBracketIndex = str.lastIndexOf(')');
    if(leftBracketIndex != 0 || rightBracketIndex != str.length()-1){
      System.err.println("treestr -"+str+"- not beginning and ending with the correct brackets");  
      return null;
    }

    while(true){
      String[] topSubtreeStrs = extractSubtreeStrs(str);
      if(topSubtreeStrs != null){
        if(topSubtreeStrs.length == 1){
          str = str.substring(1, str.length()-1).trim();
        }else{
          //log.debug("tree contains multiple top-level node, the tree is: ");
          //log.debug(str);
          Tree[] children = new Tree[topSubtreeStrs.length];
          for(int i=0;i< topSubtreeStrs.length;i++){
            children[i] = buildTree(topSubtreeStrs[i], lang);
            if(children[i] == null){
              System.err.println("failed to construct subtree from subtreestr-"+topSubtreeStrs[i]);
              return null;
            }  
          }
          Tree newTreeNode = Tree.newNode(children[0].getLabel(), children);
          System.err.println("construct a pseudo-tree using first child's label, the tree is: ");
          //newTreeNode.printForDebugging();
          return newTreeNode;
        }
      }else
        break;
    }
    
    leftBracketIndex = str.indexOf('(');
    rightBracketIndex = str.lastIndexOf(')');
    if(leftBracketIndex == -1){
      if(rightBracketIndex == -1){
        //this treeNode is a pre-terminal node
        int indexOfSpace = str.indexOf(' ');
        if(indexOfSpace == -1){
          System.err.println("pre-terminal node's label is illegal, doesn't contain space that separates POS and the actual word");  
          return null;
        }   
        String label = str.substring(0,indexOfSpace);
        String word = str.substring(indexOfSpace+1);
        if(lang == Tree.CHINESE){
          if(label.matches("[0-9A-Z[-=|]]+")){
            return Tree.newPreterminal(label, word, lang);
          }else{
            System.err.println("the label for pre-terminal node is illegal");
          }
        }else{
            Tree result = Tree.newPreterminal(label, word, lang);
            if (label.matches("\\*.*")) {
                result.setMarked(true);
                result.setLabel(label.replaceAll("^\\*", ""));
            }
            return result;
        }
      }else{
        System.err.println("leftBracket not found, but right bracket found");
      }
    }else{//(leftBracketIndex != -1)
      if(rightBracketIndex == -1){
        System.err.println("leftBracket found, but right bracket not found");
      }else{
        //extract the label
        String label = str.substring(0,leftBracketIndex).trim();
        if(!label.matches("[0-9A-Z[-=|]]+")){
          System.err.println("the label-"+label+"- for an intermediate node is illegal, the str was -"+str);
          return null;
        }  
        str = str.substring(leftBracketIndex,rightBracketIndex+1);

        String[] subtreeStrs = extractSubtreeStrs(str);
        if(subtreeStrs == null){
          System.err.println("failed to retrieve subtreestrs");
          return null;
        }  
        Tree[] children = new Tree[subtreeStrs.length];
        for(int i=0;i< subtreeStrs.length;i++){
          children[i] = buildTree(subtreeStrs[i], lang);
          if(children[i] == null){
            System.err.println("failed to construct subtree from subtreestr-"+subtreeStrs[i]);
            return null;
          }  
        }
        return Tree.newNode(label, children);
      }
    }
    return null;
  }

  /**
  * Extract subtree strings from a string that contains several well-bracketed strings
  * for example: (A) (B) (C (D))
  * space between these well-bracketed strings is optional
  * 
  */
  private static String[] extractSubtreeStrs(String str){
    ArrayList<String> subtreeStrs = new ArrayList<String>();
    int startIndex = 0;
    int endIndex = 0;
    int leftBracketCount = 0;
    str = str.trim();

    while(str.length() > 0 ){
      startIndex = str.indexOf('(');
      if(startIndex != 0){
        //System.err.println("the str to extract substrs from-"+str+"- doesn't start with (");
        return null;
      }
      leftBracketCount = 1;
      for(endIndex=startIndex+1; endIndex<str.length(); endIndex++){
        if(str.charAt(endIndex) == ')'){
          leftBracketCount--;
          if(leftBracketCount == 0){
            subtreeStrs.add(str.substring(startIndex, endIndex+1));
            str = str.substring(endIndex+1).trim();
            break;
          }
        }else if(str.charAt(endIndex) == '('){
          leftBracketCount++;
        }
      }
    }
    if(str.length() > 0 || subtreeStrs.size() == 0){
      System.err.println("after substrs were extracted, the original str is not empty, or substrs size equals to 0");
      return null;
    }  
    else{
      return subtreeStrs.toArray(new String[subtreeStrs.size()]);
    }
  }

  /**
  * Transform the tree into binary and unary branching only
  * @param tree the tree to be transformed
  */
  public static void transform(Tree tree){
    if(tree.getLanguage() == Tree.CHINESE)
      transformForChinese(tree);
    else
      transformForEnglish(tree);
  }

  public static void transformForEnglish(Tree tree){
    if(tree.isPreterminal())
      return;
    if(tree.numOfChildren() > 2){
      //transforming the current node
      int headNodeChildIndex = tree.getHeadNodeChildIndex();
      if(headNodeChildIndex == -1){
        System.err.println("head node child index not set for a non-preterminal node");
        System.exit(1);
      }

      Tree headNode = tree.getHeadNode();
      Tree[] children = tree.getChildren();
      Tree[] temp = new Tree[children.length-1];
      
      String label = tree.getNormalizedLabel();
      label = "TEMP";

      if(headNodeChildIndex == 0){//the head is left-most
        //strip off the right-most child
        for(int i=0;i<children.length-1;i++){
          temp[i] = children[i];
        }
        Tree newTreeNode = Tree.newNode(label, temp);
        newTreeNode.setHeadNode(headNode);
        newTreeNode.setHeadNodeChildIndex(0);
        //the headword and headNodeChildIndex of the old tree doesn't change
        tree.setChildren(new Tree[]{newTreeNode, children[children.length-1]});
        tree.setHeadNodeChildIndex(0);
      }else{//the head is right-most or somewhere in the middle
        //strip off the left-most child
        for(int i=1;i<children.length;i++){
          temp[i-1] = children[i];
        }
        Tree newTreeNode = Tree.newNode(label, temp);
        newTreeNode.setHeadNode(headNode);
        newTreeNode.setHeadNodeChildIndex(headNodeChildIndex-1);
        //the headword and headNodeChildIndex of the old tree doesn't change
        tree.setChildren(new Tree[]{children[0], newTreeNode});
        tree.setHeadNodeChildIndex(1);
      }
    }
    //now recursively transform the new tree's children
    for(Tree child: tree.getChildren()){
      transform(child);
    }
  }

  public static void transformForChinese(Tree tree){
    if(tree.isPreterminal())
      return;
    if(tree.numOfChildren() > 2){
      //transforming the current node
      int headNodeChildIndex = tree.getHeadNodeChildIndex();
      if(headNodeChildIndex == -1){
        System.err.println("head node child index not set for a non-preterminal node");
        System.exit(1);
      }

      Tree headNode = tree.getHeadNode();
      Tree[] children = tree.getChildren();
      Tree[] temp = new Tree[children.length-1];
      
      String label = tree.getLabelRoot();
      if(label.endsWith("*") || label.equals("TEMP")){
        label = label+"";
      }else if(label.matches("NP|IP|VP|PRN|QP|UCP|VPT|CP|ADJP|PP|VNV|FRAG")){
        label = label+"*"; 
      }else{
        label = "TEMP";
      }

      if(headNodeChildIndex == 0){//the head is left-most
        //strip off the right-most child
        for(int i=0;i<children.length-1;i++){
          temp[i] = children[i];
        }
        Tree newTreeNode = Tree.newNode(label, temp);
        newTreeNode.setHeadNode(headNode);
        newTreeNode.setHeadNodeChildIndex(0);
        tree.setChildren(new Tree[]{newTreeNode, children[children.length-1]});
        tree.setHeadNodeChildIndex(0);
      }else{//the head is right-most or somewhere in the middle
        //strip off the left-most child
        for(int i=1;i<children.length;i++){
          temp[i-1] = children[i];
        }
        Tree newTreeNode = Tree.newNode(label, temp);
        newTreeNode.setHeadNode(headNode);
        newTreeNode.setHeadNodeChildIndex(headNodeChildIndex-1);
        tree.setChildren(new Tree[]{children[0], newTreeNode});
        tree.setHeadNodeChildIndex(1);
      }
    }
    //now recursively transform the new tree's children
    for(Tree child: tree.getChildren()){
      transform(child);
    }
  }

  /**
  * Detransform the tree from binary and unary branching only to multi-branching
  * @param tree the tree to be detransformed
  */
  public static void detransform(Tree tree){
    int numOfChildren = tree.numOfChildren();
    Tree[] children = tree.getChildren();
    boolean noNeedToDetransform = true;
    for(int i=0;i<numOfChildren;i++){
      if(isTempNode(children[i])){
        noNeedToDetransform = false;
        break;
      }
    }
    if(noNeedToDetransform){
      for(Tree child : children){
        detransform(child);
      }
    }else{
      List<Tree> newChildren = new ArrayList<Tree>();
      int headNodeChildIndex = 0;
      Tree headNode = null;
      Tree headNodeChild = null;
      String parentLabel = tree.getNormalizedLabel();
      for(int i=0;i<numOfChildren;i++){
        Tree child = children[i];
        detransform(child);
        if(isTempNode(child)){
          //without temp node correction
          if(tree.getHeadNodeChildIndex() == i){
            headNodeChildIndex = newChildren.size()+child.getHeadNodeChildIndex();
            headNode = child.getHeadNode();
            headNodeChild = child;
          }
          for(Tree node : child.getChildren())
            newChildren.add(node);
        }else{
          if(tree.getHeadNodeChildIndex() == i){
            headNodeChildIndex = newChildren.size();
            headNode = child.getHeadNode();
            headNodeChild = child;
          }  
          newChildren.add(child);
        }
      }
      if(headNode == null){
        System.err.println("in detransformation, tree's new headnode is null");
        System.err.println("The tree to detransform has "+numOfChildren+" children");
        System.err.println("the tree was: ");
        tree.printForDebugging();
      }
      tree.setChildren(newChildren);
      tree.setHeadNode(headNode);
      tree.setHeadNodeChildIndex(headNodeChildIndex);
    }
    if(tree.isRoot()){
      String label = tree.getNormalizedLabel();
      //System.out.println("rootlabel:"+label+":end");
      //System.out.println("root numofchild:"+tree.numOfChildren());
      //if(tree.numOfChildren() > 1){
      //  System.out.println("child 1: "+tree.getChild(1).getNormalizedLabel());
      //}
      if(label.endsWith("*")){
        tree.setLabel(label.substring(0,label.length()-1)); 
      }else if(label.equals("TEMP")){
        System.err.println("Root tree's label is TEMP");
      }
    }
  }


  /**
  * Checks if the given tree node is a temporary node produced during binary-transformation
  * @param node the node to check
  * @return boolean true if the node is a temporary node, false otherwise
  */
  private static boolean isTempNode(Tree node){
    if(node.isLeaf())
      return false;	
    String label = node.getNormalizedLabel();
    if( label.endsWith("*")  || label.equals("TEMP"))
      return true;
    return false;  
  }

  /**
  * An internal method used for convenience of switching between different printing outputs
  */
  private static void print(String str){
    if(writer != null){
      try{
        writer.write(str);
      }catch(IOException ex){
        ex.printStackTrace();
      }
    }else
      System.out.print(str);
  }

  /**
  * Print the tree in a vertical easy-to-ready format, using a Writer 
  * @param tree the tree(or subtree) to be printed
  * @param w the writer to use for printing
  */
  public static void prettyPrintTree(Tree tree, Writer w){
    writer = w;
    prettyPrintTree(tree);
    writer = null;
  }

  /**
  * Print the tree in a vertical easy-to-ready format
  * @param tree the tree(or subtree) to be printed
  */
  public static void prettyPrintTree(Tree tree){
    tree.calcDist();
    //int totalDist = tree.distToLeftmostBottomChild + tree.distToRightmostBottomChild;
    //tree.printIndent = totalDist/2+totalDist%2;
    tree.printIndent = tree.getChild(0).distToLeftmostBottomChild + Math.abs(tree.distToEachDirectChild[0]);
    ArrayList<Tree> currentLayer = new ArrayList<Tree>(100);
    ArrayList<Tree> nextLayer = new ArrayList<Tree>(100);

    currentLayer.add(tree);
    while(currentLayer.size() != 0){
      int currentPos = 0;
      for(Tree node: currentLayer){
        int distToPrint = node.printIndent-currentPos;

        //log.debug("node: "+node.getNormalizedLabelWithHead()+" has printIndent "+node.printIndent);
        //log.debug("dTL: "+node.distToLeftmostBottomChild+" dTR: "+node.distToRightmostBottomChild);
        //log.debug("dTChild: ");
        //for(int i=0;i<node.numOfChildren();i++) 
        //  log.debug("index:"+i+" "+ node.distToEachDirectChild[i]);
        //log.debug("disToPrint: "+distToPrint);  
        //log.debug("textWidth: "+node.getNormalizedLabelWHTextWidth());
        //log.debug(""); 

        StringBuffer sb = new StringBuffer();
        for(int c=0;c<distToPrint;c++){
          sb.append(" ");
        }
        print(sb.toString());
        //String printLabel = null; 
        //String printLabel = node.getNormalizedLabel()+"("+node.getHeadWord()+"-"+node.getHeadWordPOS()+")";
        String printLabel = node.getNormalizedLabelWithHead();
        print(printLabel);
        currentPos = currentPos + sb.toString().length() + node.getNormalizedLabelWHTextWidth();
        if(!node.isLeaf()){
          Tree[] children = node.getChildren();
          for(int i=0;i<children.length;i++){
            children[i].printIndent = node.printIndent + node.distToEachDirectChild[i];  
            nextLayer.add(children[i]);
          }
        }
      }
      print("\n");

      for(int i=0;i<V_SEPERATOR_HEIGHT;i++){
       int currPos = 0;
       for(Tree node : currentLayer){
        for(int childIndex=0;childIndex<node.numOfChildren();childIndex++){
          int p1 = node.printIndent;
          int p2 = node.printIndent+node.distToEachDirectChild[childIndex];
          int pos = (int)( node.printIndent + (p2-p1)/(V_SEPERATOR_HEIGHT)*(i+1));
          StringBuffer sbTemp = new StringBuffer();
          for(int j=0;j<pos-currPos;j++)
            sbTemp.append(" ");
          print(sbTemp.toString());  
          print(".");
          currPos = pos+1;
        }
       } 
       print("\n");
      }  
      currentLayer = nextLayer;
      nextLayer = null;
      nextLayer = new ArrayList<Tree>(100);
    }
  }

  /**
  * Mark the head nodes of a given tree
  * @param tree the tree to be marked
  *
  */
  public static void markHeadNode(Tree tree){
    if(tree.getLanguage() == Tree.CHINESE)
      markHeadNodeForChinese(tree);
    else
      markHeadNodeForEnglish(tree);
  }


  public static void markHeadNodeForEnglish (Tree tree){
    if(tree.getHeadNode() != null)
      return;
    int numOfChildren = tree.numOfChildren();
    if(tree.isPreterminal() || numOfChildren == 0)
      return;
    if(numOfChildren == 1){
      Tree child = tree.getChild(0);
      markHeadNodeForEnglish(child);
      tree.setHeadNode(child.getHeadNode());
      tree.setHeadNodeChildIndex(0);
    }else{
      String label = tree.getNormalizedLabel();
      String labelRoot = tree.getLabelRoot();
      if(label == null)
        System.err.println("Error in markHeadNodeForEnglish: tree normalized label null: "+tree.getLabel());
      int direction = 1;
      Integer dire = headRuleDirectionForE.get(labelRoot);
      if(dire == null){
        System.err.println("direction null for "+labelRoot);
        System.err.println("child: ");
        for(Tree child: tree.getChildren())
          System.err.println(child.getNormalizedLabel());
      }else{
        direction = dire.intValue();
      }
      
      String[] childrenLabels = tree.getChildrenNormalizedLabels();
      //int direction = headRuleDirectionForE.get(label);
      String[] priority = headRulePriorityListForE.get(labelRoot);
      int indexOfHeadNodeChild = -1;

      if(label.equals("NP")){
        if(childrenLabels[childrenLabels.length-1].equals("POS")){
          indexOfHeadNodeChild = childrenLabels.length-1;
        }  
        if(indexOfHeadNodeChild == -1){
          for(int i=childrenLabels.length-1;i>=0;i--){
            if(childrenLabels[i].matches("NN|NNP|NNPS|NNS|NX|POS|JJR")){
              indexOfHeadNodeChild = i;
              break;
            }
          }
        }
        if(indexOfHeadNodeChild == -1){
          for(int i=0;i<childrenLabels.length;i++){
            if(childrenLabels[i].equals("NP")){
              indexOfHeadNodeChild = i;
              break;
            }
          }
        }
        if(indexOfHeadNodeChild == -1){
          for(int i=childrenLabels.length-1;i>=0;i--){
            String clb = childrenLabels[i];
            if(clb.matches("ADJP|PRN|$")){
              indexOfHeadNodeChild = i;
              break;
            }
          }
        }
        if(indexOfHeadNodeChild == -1){
          for(int i=childrenLabels.length-1;i>=0;i--){
            if(childrenLabels[i].equals("CD")){
              indexOfHeadNodeChild = i;
              break;
            }
          }
        }
        if(indexOfHeadNodeChild == -1){
          for(int i=childrenLabels.length-1;i>=0;i--){
            if(childrenLabels[i].matches("JJ|JJS|RB|QP")){
              indexOfHeadNodeChild = i;
              break;
            }
          }
        }
     }else{
        //Right first
        if(priority ==null){
          System.err.println("Priority null for "+label);
        }
        if(direction == 1){
          for(String priorityItem: priority){
            for(int i=childrenLabels.length-1;i>=0;i--){
              if(childrenLabels[i].equals(priorityItem)){
                indexOfHeadNodeChild = i;
                break;
              }
            }
            if(indexOfHeadNodeChild != -1)
              break;
          }
        }else{//Left first
          for(String priorityItem: priority){
            for(int i=0;i<childrenLabels.length;i++){
              if(childrenLabels[i].equals(priorityItem)){
                indexOfHeadNodeChild = i;
                break;
              }
            }
            if(indexOfHeadNodeChild != -1)
              break;
          }
        }
      }
      //no headnode was assigned according to our rules
      //assign the default(rightmost) child as the headNode
      if(indexOfHeadNodeChild == -1){
        if(direction == 1)
          indexOfHeadNodeChild = tree.numOfChildren()-1;
        else
          indexOfHeadNodeChild = 0;
      }    

      Tree childNode = tree.getChild(indexOfHeadNodeChild);
      markHeadNodeForEnglish(childNode);
      tree.setHeadNodeChildIndex(indexOfHeadNodeChild);
      tree.setHeadNode(childNode.getHeadNode());

      //after marking the current node's head node, mark all its children's
      for(Tree child : tree.getChildren()){
        markHeadNodeForEnglish(child);
      }
    }
  }

  
  public static void markHeadNodeForChinese(Tree tree){
    if(tree.getHeadNode() != null)
      return;
    int numOfChildren = tree.numOfChildren();
    if(tree.isPreterminal() || numOfChildren == 0)
      return;
    if(numOfChildren == 1){
      Tree child = tree.getChild(0);
      markHeadNodeForChinese(child);
      tree.setHeadNode(child.getHeadNode());
      tree.setHeadNodeChildIndex(0);
    }else{
      String label = tree.getNormalizedLabel();
      String labelRoot = tree.getLabelRoot();
      if(label == null)
        System.err.println("tree normalized label null: "+tree.getLabel());
      Integer dire = headRuleDirectionForC.get(labelRoot);
      if(dire == null){
        System.err.println("direction null for "+labelRoot);
        System.err.println("child: ");
        for(Tree child: tree.getChildren())
          System.err.println(child.getNormalizedLabel());
      }  
        
      int direction = 1;
      if(dire != null)
      	direction = headRuleDirectionForC.get(labelRoot);
      String[] priority = headRulePriorityListForC.get(labelRoot);
      String[] childrenLabels = tree.getChildrenNormalizedLabelRoots();
      int indexOfHeadNodeChild = -1;
      if(labelRoot.equals("CP")){
        for(String priorityItem: new String[]{"DEC","SP"}){
          for(int i=childrenLabels.length-1;i>=0;i--){
            if(childrenLabels[i].equals(priorityItem)){
              indexOfHeadNodeChild = i;
              break;
            }
          }
          if(indexOfHeadNodeChild != -1)
            break;
        }
        if(indexOfHeadNodeChild == -1){
          for(String priorityItem: new String[]{"ADVP","CS"}){
            for(int i=0;i<childrenLabels.length;i++){
              if(childrenLabels[i].equals(priorityItem)){
                indexOfHeadNodeChild = i;
                break;
              }
            }
            if(indexOfHeadNodeChild != -1)
              break;
          }
        }
        if(indexOfHeadNodeChild == -1){
          for(String priorityItem: new String[]{"CP","IP"}){
            for(int i=childrenLabels.length-1;i>=0;i--){
              if(childrenLabels[i].equals(priorityItem)){
                indexOfHeadNodeChild = i;
                break;
              }
            }
            if(indexOfHeadNodeChild != -1)
              break;
          }
        }
     }else{
        //Right first
        if(priority ==null){
          System.err.println("Priority null for "+label);
	  indexOfHeadNodeChild = 0;
        }else{
        if(direction == 1){
          for(String priorityItem: priority){
            for(int i=childrenLabels.length-1;i>=0;i--){
              if(childrenLabels[i].equals(priorityItem)){
                indexOfHeadNodeChild = i;
                break;
              }
            }
            if(indexOfHeadNodeChild != -1)
              break;
          }
        }else{//Left first
          for(String priorityItem: priority){
            for(int i=0;i<childrenLabels.length;i++){
              if(childrenLabels[i].equals(priorityItem)){
                indexOfHeadNodeChild = i;
                break;
              }
            }
            if(indexOfHeadNodeChild != -1)
              break;
          }
        }
	}
      }
      //no headnode was assigned according to our rules
      //assign the default(rightmost) child as the headNode
      if(indexOfHeadNodeChild == -1){
        if(direction == 1)
          indexOfHeadNodeChild = tree.numOfChildren()-1;
        else
          indexOfHeadNodeChild = 0;
      }    

      Tree childNode = tree.getChild(indexOfHeadNodeChild);
      markHeadNodeForChinese(childNode);
      tree.setHeadNodeChildIndex(indexOfHeadNodeChild);
      tree.setHeadNode(childNode.getHeadNode());

      //after marking the current node's head node, mark all its children's
      for(Tree child : tree.getChildren()){
        markHeadNodeForChinese(child);
      }
    }
  }

  /**
  * Locate the parent of a child node, given the root of the tree, in a depth-first fashion
  * @param child 
  * @param root
  * @return Tree the parent of the given child. If the child node is the root, return null
  */
  public static Tree locateParent(Tree child, Tree root){
    if(child.isRoot() || child == root)
      return null;
    Tree result =  searchForParent(child, root); 
    if(result == null)
      System.err.println("Error in locateParent, failed to locate a child node");
    return result;
  }

  /**
  * Private method, search for the parent of a child node, in a depth-first fashion
  * @param target
  * @param current
  * @return Tree the parent of the given child. 
  */
  private static Tree searchForParent(Tree target, Tree current){
    for(Tree child: current.getChildren()){
      if(child == target)
        return current;
    }
    for(Tree child: current.getChildren()){
      Tree result = searchForParent(target, child);
      if(result != null)
        return result; 
    }
    return null;
  }

  /**
  * Remove all the empty nodes in the given tree
  * @param tree the tree to remove empty nodes from
  */
  public static boolean removeEmptyNode(Tree tree){
    if(tree.isLeaf())
      return false;
    boolean done = false;
    boolean removed = false;
    while(!done){
      done = true;
      for(int i=0;i<tree.numOfChildren();i++){
        if(removeEmptyNode(tree.getChild(i)) == true){
          tree.removeChild(i);
          done = false;
          removed = true;
          break;
        }  
      }
    }
    if(tree.numOfChildren() == 0)
      return true;
    String label = tree.getNormalizedLabel();
    if(label.equals("NONE")) // 
      return true;
    //collapsing nodes
    if(removed == true && (tree.numOfChildren() == 1)){
      if(label.equals(tree.getChild(0).getNormalizedLabel()))
        return true;
      if(label.startsWith("CP") || label.startsWith("IP")) 
        return true;
    }
    return false;
  }

  public static Tree findLeftDependent(Tree tree){
    if(tree.isPreterminal())
      return null;
    int index = tree.getHeadNodeChildIndex();  
    if(index == 0)
      return findLeftDependent(tree.getChild(index));
    else
      return tree.getChild(index-1).getHeadNode();
  }

  public static Tree findRightDependent(Tree tree){
    if(tree.isPreterminal())
      return null;
    int index = tree.getHeadNodeChildIndex();  
    if(index == tree.numOfChildren()-1)
      return findRightDependent(tree.getChild(index));
    else
      return tree.getChild(index+1).getHeadNode();
  }

  /**
  * Count the punctuation nodes in a given tree
  */
  public static int countPunctuation(Tree tree){
    punctuationCount = 0;
    recursivelyCountPunctuation(tree);
    return punctuationCount;
  }
  //diff punc count
  public static int countPunctuation(Tree tree, String direction){
    punctuationCount = 0;
    int headIndex = tree.getHeadNode().sequenceNo;
    recursivelyCountPunctuation(tree, headIndex, direction);
    return punctuationCount;
  }

  /**
  * Private method used by countPunctuation()
  */
  private static void recursivelyCountPunctuation(Tree tree){
    if(tree.isPreterminal()){
      if(tree.getNormalizedLabel().equals("PU"))
        punctuationCount++;
      return;
    }
    for(Tree child: tree.getChildren())
      recursivelyCountPunctuation(child);
  }

  //diff punc count
  private static void recursivelyCountPunctuation(Tree tree, int headIndex, String direction){
    if(tree.isPreterminal()){
      if(tree.getNormalizedLabel().equals("PU")){
        if( (direction.equals("L") && tree.sequenceNo < headIndex) ||
            (direction.equals("R") && tree.sequenceNo > headIndex)){
          punctuationCount++;
        }
      }  
      return;
    }
    for(Tree child: tree.getChildren())
      recursivelyCountPunctuation(child, headIndex, direction);
  }

  //comma count
  /*
  private static void recursivelyCountPunctuation(Tree tree, int headIndex, String direction){
    if(tree.isPreterminal()){
      if(tree.getNormalizedLabel().equals("PU")){
        if(tree.getChild(0).getNormalizedLabel().matches("，|：|、")){
          if( (direction.equals("L") && tree.sequenceNo < headIndex) ||
              (direction.equals("R") && tree.sequenceNo > headIndex)){
            punctuationCount++;
          }
        }
      }  
      return;
    }
    for(Tree child: tree.getChildren())
      recursivelyCountPunctuation(child, headIndex, direction);
  }
  */

  /**
  * Get the first preterminal of a tree
  */
  public static Tree getFirstPreterminal(Tree tree){
    if(tree.isPreterminal())
      return tree;
    return getFirstPreterminal(tree.getChild(0));  
  }


  /**
  * Get the last preterminal of a tree
  */
  public static Tree getLastPreterminal(Tree tree){
    if(tree.isPreterminal())
      return tree;
    return getLastPreterminal(tree.getChild(tree.numOfChildren()-1));  
  }
  
  /**
  * Count the dependents of the headnode of a given tree
  */
  public static int countDependents(Tree tree){
    int dependentCount = 0;
    Tree node = tree;
    while(!node.isPreterminal()){
      dependentCount += node.numOfChildren()-1;
      node = node.getChild(node.getHeadNodeChildIndex());
    }
    return dependentCount;
  }

  /**
  * Count the preterminals of a given tree
  */
  public static int countPreterminals(Tree tree){
    preterminalCount = 0;
    recursivelyCountPreterminal(tree);
    return preterminalCount;
  }

  /**
  * Private method used by countPreterminals()
  */
  private static void recursivelyCountPreterminal(Tree tree){
    if(tree.isPreterminal()){
      preterminalCount++;
      return;
    }
    for(Tree child: tree.getChildren())
      recursivelyCountPreterminal(child);
  }

  /**
  * Get the preterminals of a given tree, and return them in a Queue
  */
  public static MyQueue<Tree> getPreterminals(Tree tree){
    preterminalQueue = new MyQueue<Tree>();
    recursivelyGetPreterminal(tree);
    List<Tree> list = preterminalQueue.asList();
    //set the sequenceNo of these preterminal nodes
    for(int i=0;i<list.size();i++){
      list.get(i).sequenceNo = i;
    }
    return preterminalQueue;
  }

  /**
 * Get the leaves of a given tree, and return them as one String.
 * 
 * @param tree
 * @return the leaves of the given tree, concatenated together
 */
  public static String getLeaves(Tree tree) {
      StringBuilder res = new StringBuilder();
      List<Tree> preterms = getPreterminals(tree).asList();
      for (Tree preterm : preterms) {
          res.append(preterm.getChild(0).getLabel()+ " ");
      }
      return res.toString().trim();
  }

  public static void markSequenceNo(Tree tree){
    preterminalQueue = new MyQueue<Tree>();
    recursivelyGetPreterminal(tree);
    List<Tree> list = preterminalQueue.asList();
    //set the sequenceNo of these preterminal nodes
    for(int i=0;i<list.size();i++){
      list.get(i).sequenceNo = i;
    }
  }

  /**
  * Private method used by getPreterminals()
  */
  private static void recursivelyGetPreterminal(Tree tree){
    if(tree.isPreterminal()){
      preterminalQueue.push(tree);
      return;
    }
    for(Tree child: tree.getChildren())
      recursivelyGetPreterminal(child);
  }

  public static void getWordRange(Tree tree, String[][] map){
    if(tree.numOfChildren() == 0){
      System.err.println("Error in getWordRange: invoked on a tree that has 0 children");
      return;
    }
    if(tree.isPreterminal()){
      return;
    }
    for(Tree child: tree.getChildren())
      getWordRange(child, map);


    int leftIndex = -1;
    int rightIndex = -1;
    Tree leftMostChild = tree.getChild(0);
    Tree rightMostChild = tree.getChild(tree.numOfChildren()-1);
    if(leftMostChild.isPreterminal()){
      leftIndex = leftMostChild.sequenceNo;
    }else{
      leftIndex = leftMostChild.leftMostWordIndex; 
    }
    if(rightMostChild.isPreterminal()){
      rightIndex = rightMostChild.sequenceNo;
    }else{
      rightIndex = rightMostChild.rightMostWordIndex; 
    }
    tree.leftMostWordIndex = leftIndex;
    tree.rightMostWordIndex = rightIndex;
    if(map != null)
      map[leftIndex][rightIndex] = tree.getNormalizedLabel();
  }

  /**
  * This method builds up the dependency information of the preterminals of the given tree,
  * and store them in the int[] map
  * note that the root of a parse tree's dependency cannot be set by this method, but it always
  * defaults to -1.
  * @param tree
  * @param map
  */
  public static void getDependencyMap(Tree tree, int[] map){
    if(tree.isPreterminal())
      return;
    int headIndex = tree.getHeadNode().sequenceNo;
    int headChildIndex = tree.getHeadNodeChildIndex();
    for(int i=0; i<tree.numOfChildren();i++){
      if(i != headChildIndex){
        int index = tree.getChild(i).getHeadNode().sequenceNo;
        map[index] = headIndex;
      }
    }
    for(Tree child: tree.getChildren())
      getDependencyMap(child, map);
  }


  /**
   * Given a template tree with a marked node, extracts the node at the same
   * position in the other Tree, if the two trees are unifiable.  
   * 
   * For this method, two nonterminal nodes are unifiable if they have identical labels
   * and the children of the template tree node are a unifiable subset of those of its 
   * correspondent.  Preterminal nodes with a "xx"-labeled child are unifiable with any 
   * nonterminal node.    
   * 
   * Unification is done using a depth-first search of the template tree, looking for the 
   * marked node.  If at any point the two trees are not unifiable, <code>null/<code> is
   * returned.  When a marked node is found in the template tree, the corresponding node
   * in the first tree is returned if the two nodes are unifiable.  All other nodes in both 
   * trees are ignored.
   * 
   * @param tree the tree to extract a node from
   * @param template the tree with a marked node
   * @return the extracted node, or <code>null</code>
   */
  public static Tree extractNode(Tree tree, Tree template) {
      Tree result = null;
      if (tree == null || template == null) return null;
      if (tree.isLeaf() || template.isLeaf()) return null;
      // make sure the trees unify
      if (!unifyWithTemplate(tree,template)) return null;
      // look for the marked node
      Tree[] treeChildren = tree.getChildren();
      Tree[] templateChildren = template.getChildren();
      for (int i = 0; i < templateChildren.length; i++) {
          if (templateChildren[i].isMarked()) {
              return treeChildren[i];
          }
          result = extractNode(treeChildren[i],templateChildren[i]);
          if (result != null) return result;
      }
      return result;
  }
  
  private static boolean unifyWithTemplate(Tree tree, Tree template) {
      boolean res = true;
      if (tree == null || template == null) return false;
      if (template.isLeaf()) 
          return (tree.isLeaf() && template.getLabel().equals(tree.getLabel()));
      if (tree.isLeaf()) 
          return (template.isLeaf() && template.getLabel().equals(tree.getLabel()));
      if (!tree.getLabel().equals(template.getLabel())) return false;
      if (template.isPreterminal() && template.getChild(0).getLabel().equals("xx")) return true;
      Tree[] treeChildren = tree.getChildren();
      Tree[] templateChildren = template.getChildren();
      if (treeChildren.length < templateChildren.length) return false;
      for (int i = 0; i < templateChildren.length; i++) {
          if (!unifyWithTemplate(treeChildren[i],templateChildren[i])) return false;
      }
      return res;
  }
  
  /**
   * Returns the first node that has a child node with a label that
   * matches the given regular expression.  Depth-first search is used.
   * 
   * @param tree the tree to search in
   * @param childLabelRegex the regular expression of the child label
   * @return the node with the specified condition, or <code>null</code>
   */
  public static Tree findNodeWithChild(Tree tree, String childLabelRegex) {
      Tree res = null;
      if (tree == null || tree.isLeaf()) return null;
      if (tree.getChild(childLabelRegex) != null) return tree; 
      Tree[] children = tree.getChildren();
      for (Tree child : children) {
          res = findNodeWithChild(child,childLabelRegex);
          if (res != null) return res;
      }
      return res;
  }
  
  /**
   * Returns the first preterminal with a label that matches the given 
   * pattern and whose preceding preterminal matches the given pattern.
   * 
   * @param tree the tree to search in
   * @param labelRegex the regular expression of the preterminal
   * @param precedingLabelRegex the regular expression of the preceding preterminal
   * @return the node with the specified condition, or <code>null</code>
   */
  public static Tree findFirstPreterminalWithPrecedingPreterminal(Tree tree, String labelRegex, String precedingLabelRegex) {
      List<Tree> tags = TreeHelper.getPreterminals(tree).asList();
      Tree res = null;
      for (ListIterator<Tree> it = tags.listIterator(); it.hasNext();) {
          Tree tag = it.next();
          if (tag.getLabel().matches(precedingLabelRegex)) {
              if (it.hasNext() && it.next().getLabel().matches(labelRegex)) {
                  res = tags.get(it.previousIndex());
              }
              break;
          }
      }
      return res;
  }
  
  /**
   * Returns the last preterminal with a label that matches the given 
   * pattern and whose preceding preterminal matches the given pattern.
   * 
   * @param tree the tree to search in
   * @param labelRegex the regular expression of the preterminal labels to gather
   * @param precedingLabelRegex the regular expression of the preceding preterminal
   * @return the node with the specified condition, or <code>null</code>
   */
  public static List<Tree> getPreterminalsAfter(Tree tree, String labelRegex, String precedingLabelRegex) {
      List<Tree> tags = TreeHelper.getPreterminals(tree).asList();
      List<Tree> res = new ArrayList<Tree>();
      for (ListIterator<Tree> it = tags.listIterator(); it.hasNext();) {
          Tree tag = it.next();
          if (tag.getLabel().matches(precedingLabelRegex)) {
              for (ListIterator<Tree> it2 = tags.listIterator(it.nextIndex()); 
                  it2.hasNext() && it2.next().getLabel().matches(labelRegex);) {
                  res.add(tags.get(it2.previousIndex()));
              }
              break;
          }
      }
      if (res.size() == 0) return null;
      return res;
  }

}
