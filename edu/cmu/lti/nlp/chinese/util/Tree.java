package edu.cmu.lti.nlp.chinese.util;

import java.io.StringWriter;
import java.io.Writer;
import java.util.List;

/**
 * This class represents a parse tree or a subtree
 * A tree only knows its children, but not its parent, 
 * to prevent memoryleak.
 *  
 * To look up a tree node that is above the current node, 
 * a root node that contains this subtree must be provided.
 */

public class Tree{
  //private static Logger log = Logger.getLogger( Tree.class );
  
  public final static int INDENT_LENGTH = 1;
  /* horizontal seperator width used in pretty printing */
  public final static int H_SEPERATOR_WIDTH = 15;

  //Language Settings
  public static final int CHINESE = 0;
  public static final int ENGLISH = 1;

  //public static int LANG = CHINESE;
  private int lang = CHINESE;

  private String id;
  private Tree[] children;
  private String label;
  private String normalizedLabel;
  private String labelRoot;
  private String normalizedLabelWithHead;
  private int numOfChildren;
  //a preterminal node doesn't have a headNode (or we should say itself is a headnode)
  //a headNode is always a preterminal node
  private Tree headNode;
  private int headNodeChildIndex = -1;

  //knowledge about parent
  private int parentNumOfChildren = 0; //for root, this field is always 0
  private int indexAsChild = -1;

  //fields used solely for pretty printing purpose
  int distToLeftmostBottomChild = 0;
  int distToRightmostBottomChild = 0;
  int printIndent = 0;
  int[] distToEachDirectChild;
  int normalizedLabelWHTextWidth = -1;
  
  private static final String[] GRTags = {"SBJ","OBJ","PRD","IO","EXT","FOC","ADV","TPC"};
  private static final String[] AdvTags = {"BNF","CND","DIR","IJ","LGS","LOC","MNR","PRP","TMP","VOC"};


  //fields only used for ease of feature extraction
  public int sequenceNo = -1;
  //these two values will be -1 for preterminals and leaves
  int leftMostWordIndex = -1;
  int rightMostWordIndex = -1;

  //utility fields
  static Writer writer = null;
  
  //field used for node matching/extraction
  private boolean isMarked = false;
  
  /**
  * Private Constructor, used for Factory-style leaf creation
  */
  private Tree(String label, int lang){
    this.numOfChildren = 0;
    this.children = new Tree[0];
    this.label = label;
    this.lang = lang;
    //no children thus no need to update children's knowledge
  }

  /**
  * Private Constructor, used for Factory-style tree creation
  */
  private Tree(String label, List<Tree> subtrees){
    this.numOfChildren = subtrees.size();
    if(numOfChildren == 0)
      System.err.println("Illegal tree construction, try to construct a non-preterminal tree with no children");
    this.lang = subtrees.get(0).lang;  
    Tree[] children = new Tree[numOfChildren];
    this.children = subtrees.toArray(children);
    this.label = label;
    //update children's knowledge about parent
    updateChildrenKnowledge();
  }

  /**
  * Private Constructor, used for Factory-style tree creation
  */
  private Tree(String label, Tree[] subtrees){
    this.numOfChildren = subtrees.length;
    if(numOfChildren == 0)
      System.err.println("Illegal tree construction, try to construct a non-preterminal tree with no children");
    this.lang = subtrees[0].lang;  
    this.children = subtrees;
    this.label = label;
    //update children's knowledge about parent
    updateChildrenKnowledge();
  }


  /**
 * @return the isMarked
 */
public boolean isMarked() {
    return isMarked;
}

/**
 * @param isMarked the isMarked to set
 */
public void setMarked(boolean isMarked) {
    this.isMarked = isMarked;
}

/**
  * Convenience method for updating children's knowledge about parent
  * Setting each child's parentNumOfChildren field, and indexAsChild field
  */
  private void updateChildrenKnowledge(){
    for(int i=0;i<numOfChildren;i++){
      children[i].setParentNumOfChildren(this.numOfChildren);    
      children[i].setIndexAsChild(i);
    }
  }

  /**
  * Checks if the current tree node is a leaf node
  * @return boolean true if current node is a leaf node, false otherwise
  */
  public boolean isLeaf(){
    return (children.length == 0 && label != null);
  }

  /**
  * Checks if the current tree node is a preterminal node
  * @return boolean true if current node is a preterminal node, false otherwise
  */
  public boolean isPreterminal(){
    if (children.length == 1 && children[0].isLeaf())
      return true;
    return false;
  }

  /**
  * @return int the language of this tree
  */
  public int getLanguage(){
    return this.lang;
  }

  /**
  * Checks if the current tree node is root
  * @return boolean true if current node is root node, false otherwise
  */
  public boolean isRoot(){
    return parentNumOfChildren == 0;
  }

  
  /**
  * set the index of the current node as a child of its parent
  * @param index int the index
  */
  public void setIndexAsChild( int index ){
    this.indexAsChild = index;
  }

  /**
  * set the number of children of the current node's parent node
  * @param num int parent's number of children
  */
  public void setParentNumOfChildren( int num){
    this.parentNumOfChildren = num;
  }

  /**
  * get the index of the current node as a child of its parent
  * @return int the index
  */
  public int getIndexAsChild(){
    return this.indexAsChild;
  }

  /**
  * get the number of children of the current node's parent node
  * @return int parent's number of children
  */
  public int getParentNumOfChildren(){
    return this.parentNumOfChildren;
  }

  /**
  * Marks the index of child which will leads to 
  * the head node of the current tree node
  * the head node child index of a preterminal node is set to -1 by default
  * 
  */
  public void setHeadNodeChildIndex(int headNodeChildIndex){
    if(this.isPreterminal()){
      System.err.println("Setting the head child index of a preterminal node: "+this);
      System.exit(1);
    }  
    this.headNodeChildIndex = headNodeChildIndex;
  }


  /**
  * Returns the head node child index
  * the head node child index of a preterminal node is set to -1 by default
  * @return int the head node child index 
  */
  public int getHeadNodeChildIndex(){
    return this.headNodeChildIndex;
  }

  /**
  * Marks the head node of the current tree node
  * 
  */
  public void setHeadNode(Tree headNode){
    if(!headNode.isPreterminal()){
      System.err.println("Setting a non-preterminal node "+headNode+" to be the headNode of "+this);
      System.exit(1);
    }  
    this.headNode = headNode;
  }

  /**
  * Returns the head node of the current tree node
  * 
  * @return Tree the head node of the current tree node
  *         if current node is a preterminal node, return itself
  */
  public Tree getHeadNode(){
    if(isPreterminal())
      return this;
    return this.headNode;
  }


  /**
  * Get the the headword of the current tree node
  * If the current tree node has a headword, recursively invoke this method call on the headword
  * else if the current tree node is a preterminal node, output the normalized label of its child (the headword)
  * Otherwise, print error and terminate
  */
  public String getHeadWord(){
    if(headNode != null)
      return headNode.getHeadWord();
    else if(isPreterminal())
      return children[0].getNormalizedLabel();
    else{
      System.err.println("in getHeadWord, current node "+ getNormalizedLabel()+" is not preterminal, but doesn't have a valid headNode");
      //try{
      //throw new RuntimeException();
      //}catch(Exception ex){
      //  ex.printStackTrace();
      //}
      System.exit(1);
    }  
    return null;
  }

  /**
  * Get the POS of the headword of the current tree node
  * If the current tree node has a headword, recursively invoke this method call on the headword
  * else if the current tree node is a preterminal node, output the normalized label (POS of headword)
  * Otherwise, print error and terminate
  */
  public String getHeadWordPOS(){
    if(headNode != null)
      return headNode.getHeadWordPOS();
    else if(isPreterminal())
      return getNormalizedLabel();
    else{
      System.err.println("in getHeadWordPOS, current node is not preterminal, but doesn't have a valid headNode");
      System.exit(1);
    }  
    return null;
  }

  
  /**
  * Returns the child treenode at specified index
  * null is returns if index is less than 0 or 
  * greater equal to the number of children of the current tree node
  * @param index int index The index of the desired children node
  * @return Tree the child at specified index
  */
  public Tree getChild(int index){
    if( index < 0 || numOfChildren <= index)
      return null;
    else
      return children[index];
  }
  
  /**
   * Returns the first child treenode with a label that
   * matches the specified regular expression
   * null is returned if one does not exist
   * @param regex The regular expression match children labels against
   * @return Tree the child with the specified label
   */
  public Tree getChild(String regex){
      for (Tree child : children) {
          if (child.getLabel().matches(regex)) {
              return child;
          }
      }
      return null;
  }

  public void setID(String id){
    this.id = id;
  }

  public String getID(){
    return this.id;
  }  

  /**
  * Returns the number of children of the current treenode
  *
  * @return int the number of children
  */
  public int numOfChildren(){
    return this.numOfChildren;
  }

  /**
  * Returns the children of the current treenode
  * 
  * @return Tree the child at specified index
  */
  public Tree[] getChildren(){
    return children;
  }

  /**
  * Set the children of the current treenode
  * Should be only used for transformation and detransformation
  * Should not be used for creating new tree
  * This function doesn't calculate the new head node or head node index 
  *
  * @param children Tree[] the new children of the current tree node
  */
  void setChildren(Tree[] children){
    this.children = children;
    this.numOfChildren = children.length;
    updateChildrenKnowledge();
  }

  /**
  * Set the children of the current treenode
  * Should be only used for transformation and detransformation
  * Should not be used for creating new tree
  * This function doesn't calculate the new head node or head node index 
  * 
  * @param children List of Tree: the new children of the current tree node
  */
  void setChildren(List<Tree> children){
    Tree[] c = new Tree[0];
    this.children = children.toArray(c);
    this.numOfChildren = children.size();
    updateChildrenKnowledge();
  }

  /*
  * Remove the child node at the given index
  * and checks if the node is preterminal or doesn't have children
  * if eithe is true, then don't try to add the node's children as its new children
  * otherwise, insert the node's children as its new children, in the position of the node
  * @param int the index of the child node to remove
  * @return boolean false if the index is valid, true if the remove operation succeeded
  */
  //public boolean removeChild(int index){
  //  if(index >= numOfChildren || index < 0){
  //    System.err.println("Error in removeChildren: invalid index of child to remove");
  //    return false;
  //  }  
  //  Tree[] newChildren = new Tree[numOfChildren-1];
  //  int counter = 0;
  //  for(int i=0;i<numOfChildren;i++){
  //    if(i != index)
  //      newChildren[counter++] = children[i];
  //  }
  //  numOfChildren--;
  //  children = newChildren;
  //  updateChildrenKnowledge();
  //  //if the child to remove contains the headword, re-find the headword
  //  if(headNodeChildIndex == index){
  //    this.headNode = null;
  //    this.headNodeChildIndex = -1;
  //    TreeHelper.markHeadNode(this); 
  //  }
  //  return true;
  //}
  public boolean removeChild(int index){
    if(index >= numOfChildren || index < 0){
      System.err.println("Error in removeChildren: invalid index of child to remove");
      return false;
    }  
    Tree[] newChildren = null;
    if(children[index].numOfChildren() == 0 || children[index].isPreterminal()){
      newChildren = new Tree[numOfChildren-1];
      int counter = 0;
      for(int i=0;i<numOfChildren;i++){
        if(i != index)
          newChildren[counter++] = children[i];
      }
    }else{
      newChildren = new Tree[numOfChildren-1+children[index].numOfChildren()];
      int counter = 0;
      for(int i=0;i<numOfChildren;i++){
        if(i != index)
          newChildren[counter++] = children[i];
        else{
          for(Tree child: children[index].getChildren())
            newChildren[counter++] = child;
        }
      }
    }
    children = newChildren;
    numOfChildren = children.length;
    updateChildrenKnowledge();
    //if the child to remove contains the headword, re-find the headword
    if(headNodeChildIndex == index){
      this.headNode = null;
      this.headNodeChildIndex = -1;
      TreeHelper.markHeadNode(this); 
    }
    return true;
  }


  public static Tree newLeaf(String label, int lang){
    return new Tree(label, lang);
  }

  public static Tree newPreterminal(String label, String leafLabel, int lang){
    Tree leaf = newLeaf(leafLabel, lang);
    return new Tree(label, new Tree[]{leaf});
  }

  public static Tree newNode(String label, List<Tree> subtrees){
    return new Tree(label, subtrees);
  }

  public static Tree newNode(String label, Tree[] subtrees){
    return new Tree(label, subtrees);
  }

  public static Tree newNode(String id, String bracketedTreeAsString, int lang){
    Tree newTreeNode = TreeHelper.buildTree(bracketedTreeAsString, lang);
    if(newTreeNode == null){
      System.err.println("new tree node is null");
    }
    newTreeNode.setID(id);
    return newTreeNode;
  }

  public static Tree newNode(String bracketedTreeAsString, int lang){
    Tree newTreeNode = TreeHelper.buildTree(bracketedTreeAsString, lang);
    if(newTreeNode == null){
      System.err.println("new tree node is null");
    }
    return newTreeNode;
  }

  /**
  * return the label of the current tree node as it was in the original Chinese TreeBank,
  * which contains functional and other optional labels
  * @return String the label as in Chinese Treebank
  */
  public String getLabel(){
    return label;
  }

  /**
  * sets the label of the current tree node,
  * @param label the new label of the current tree
  */
  public void setLabel(String label){
    this.label = label;
    this.normalizedLabel = null;
    this.labelRoot = null;
    this.normalizedLabelWithHead = null;
  }

  /**
  * A convenience function.Returns all the normalized labels of the children nodes of
  * the current tree node as a String array.If this is invoked on a leaf node, a String array
  * containing 0 elements is returned
  * 
  * @return String[] the array containing the normalized labels of all the children nodes
  */
  public String[] getChildrenNormalizedLabels(){
    String[] childrenLabels = new String[numOfChildren];
    for(int i=0;i<numOfChildren;i++){
      childrenLabels[i] = children[i].getNormalizedLabel();
    }
    return childrenLabels;
  }

  /**
  * A convenience function.Returns all the labelRoots of the children nodes of
  * the current tree node as a String array.If this is invoked on a leaf node, a String array
  * containing 0 elements is returned
  * 
  * @return String[] the array containing the normalized labels of all the children nodes
  */
  public String[] getChildrenNormalizedLabelRoots(){
    String[] childrenLabelRoots = new String[numOfChildren];
    for(int i=0;i<numOfChildren;i++){
      childrenLabelRoots[i] = children[i].getLabelRoot();
    }
    return childrenLabelRoots;
  }

  public String getLabelRoot(){
    if(labelRoot == null)
      getNormalizedLabel();
    return labelRoot;  
  }

  public void setNormalizedLabel(String norLabel){
    this.normalizedLabel = norLabel;
  }
  
  /**
  * normalize the label of the current tree node and return it
  * normalization is by removing any functional-marker (e.g -PN, =2) from the label; 
  * @return String the normalized label
  */
  public String getNormalizedLabel(){
    //checks if a normalized label already exists
    if(normalizedLabel != null)
      return normalizedLabel;
    //first step
    if(label.contains("NONE")){
      normalizedLabel = "NONE";
    }else if(label.contains("-") || label.contains("=")){
      //do not reserve tags in label
      int indexOfDash = label.indexOf('-');
      int indexOfEqual = label.indexOf("=");
      indexOfDash = indexOfDash != -1 ? indexOfDash : Integer.MAX_VALUE;
      indexOfEqual = indexOfEqual != -1 ? indexOfEqual: Integer.MAX_VALUE;
      int minIndex = indexOfDash < indexOfEqual ? indexOfDash : indexOfEqual;

      normalizedLabel = label.substring(0, minIndex).trim();
      if(normalizedLabel.length() == 0){
        normalizedLabel = "NONE";
      }  

    }else{
      normalizedLabel = label;
    }  
    if(normalizedLabel.contains(" "))  
      normalizedLabel = normalizedLabel.replace(" ","");  
    if(normalizedLabel.endsWith("*"))
      this.labelRoot = normalizedLabel.substring(0,normalizedLabel.length()-1);
    else
      this.labelRoot = normalizedLabel;
    return normalizedLabel;
  }


  /**
  * Return the normalize label of the current tree node with head information
  * If the current tree node is a preterminal node, do not include head information
  * because a preterminal node's head is itself
  * Mainly used for printing
  * 
  * @return String the normalized label with head information
  */
  public String getNormalizedLabelWithHead(){
    if(normalizedLabelWithHead != null)
      return normalizedLabelWithHead;
    if(isPreterminal() || isLeaf())
      normalizedLabelWithHead = getNormalizedLabel();
    else{  
      String tempLabel = getNormalizedLabel();
      normalizedLabelWithHead= "["+getHeadWordPOS()+"["+getHeadWord()+"]]" + tempLabel; 
    }
    return normalizedLabelWithHead;
  }

  public int getNormalizedLabelWHTextWidth(){
    if(normalizedLabelWHTextWidth != -1){
      return normalizedLabelWHTextWidth;
    }else if(isPreterminal()){
      normalizedLabelWHTextWidth = getNormalizedLabel().length();
    }else if(isLeaf()){
      if(lang == Tree.CHINESE)
        normalizedLabelWHTextWidth = label.codePointCount(0,label.length())*2;
      else
        normalizedLabelWHTextWidth = label.length();
    }else{  
      String headWord = getHeadWord();
      if(lang == Tree.CHINESE)
        normalizedLabelWHTextWidth = getNormalizedLabel().length()+4+getHeadWordPOS().length()+headWord.codePointCount(0,headWord.length())*2;
      else
        normalizedLabelWHTextWidth = getNormalizedLabel().length()+4+getHeadWordPOS().length()+headWord.length();
    }
    return normalizedLabelWHTextWidth;
  }

  /**
  * An internal method used for convenience of switching between different printing outputs
  */
  private void print(String str){
    if(writer == null)
      System.out.print(str); 
    else{
      try{
        writer.write(str);
      }catch(Exception ex){
        ex.printStackTrace();
      }
    }  
  }


  /**
  * Print this tree(subtree) to a specified Writer,
  * normalized label is used in printing
  */
  public void printInBracketedFormat(Writer w){
    writer = w;
    printInBracketedFormat();
    writer = null;
  }
  
  public String getBracketedFormat(){
	  StringWriter w = new StringWriter();
//  printInBracketedFormat(w);
    writer = w;
//    printInBracketedFormat();
    printWithIndent(0);
    writer = null;
    String s = w.toString();
    //s.replaceAll("\\n", "");
    //s.replaceAll(" +", " ");
    //s.replaceAll("\\n *", " ");
//    s.replaceAll("[\\n ]+", " ");
//    s.replaceAll("(\\p{Space})+", " ");
    s=  s.replaceAll("(\\p{Space}){2,}", " ");
	  return s;
  }
   

  /**
  * Print this tree(subtree) to stdout, 
  * normalized label with head is used in printing
  */
  public void printInBracketedFormat(){
    print("(");
    for(int i=0;i<INDENT_LENGTH;i++)
      print(" ");
    printWithIndent(1+INDENT_LENGTH);
    for(int i=0;i<INDENT_LENGTH;i++)
      print(" ");
    print(")\n");
  }

  private void printWithIndent(int indent){
    int childIndent = 1+indent+label.length()+INDENT_LENGTH;
    if(numOfChildren > 0)
      print("(");
    print(getNormalizedLabel());
    if(numOfChildren > 0){
      for(int i=0;i<INDENT_LENGTH;i++)
        print(" ");
    }  
    if(numOfChildren > 0){  
      //first child, don't print EOL, don't print ending bracket
      children[0].printWithIndent(childIndent);
      int i=1;
      //for all the children in the middle, print a EOL after each child
      for(;i<numOfChildren;i++){
        print("\n");
        for(int j=0;j<childIndent;j++)
          print(" ");
        children[i].printWithIndent(childIndent);
      }
      print(")");
    }
  }

  /**
  * Print this tree(subtree) to a specified Writer,
  * normalized label with head is used in printing
  */
  public void printInBracketedFormatWithHead(Writer w){
    writer = w;
    printInBracketedFormatWithHead();
    writer = null;
  }


  /**
  * Print this tree(subtree) to stdout, 
  * normalized label without head is used in printing
  */
  public void printInBracketedFormatWithHead(){
    print("(");
    for(int i=0;i<INDENT_LENGTH;i++)
      print(" ");
    printWithIndentWithHead(1+INDENT_LENGTH);
    for(int i=0;i<INDENT_LENGTH;i++)
      print(" ");
    print(")\n");
  }

  private void printWithIndentWithHead(int indent){
    int childIndent = 1+indent+label.length()+INDENT_LENGTH;
    if(numOfChildren > 0)
      print("(");
    print(getNormalizedLabelWithHead());
    if(numOfChildren > 0){
      for(int i=0;i<INDENT_LENGTH;i++)
        print(" ");
    }  
    if(numOfChildren > 0){  
      //first child, don't print EOL, don't print ending bracket
      children[0].printWithIndent(childIndent);
      int i=1;
      //for all the children in the middle, print a EOL after each child
      for(;i<numOfChildren;i++){
        print("\n");
        for(int j=0;j<childIndent;j++)
          print(" ");
        children[i].printWithIndent(childIndent);
      }
      print(")");
    }
  }

  public void printBracket(Writer w){
    writer = w;
    printBracket();
    writer = null;
  }

  public void printBracket(){
    printBracketRecursively();
    print("\n");
  }

  public void printBracketRecursively(){
    if(numOfChildren > 0)
      print("(");
    print(getNormalizedLabel());
    if(numOfChildren > 0){  
      //for all the children in the middle, print a EOL after each child
      for(int i=0;i<numOfChildren;i++){
        print(" ");
        children[i].printBracketRecursively();
      }
      print(")");
    }
  }

  /**
  * Print this tree(subtree) to stderr, 
  * original label without head is used in printing
  */
  public void printForDebugging(){
    StringBuffer debugInfo = new StringBuffer();
    debugInfo.append("(");
    for(int i=0;i<INDENT_LENGTH;i++)
      debugInfo.append(" ");
    printWithIndentForDebugging(1+INDENT_LENGTH, debugInfo);
    for(int i=0;i<INDENT_LENGTH;i++)
      debugInfo.append(" ");
    debugInfo.append(")\n");
    //log.debug(debugInfo.toString());
  }

  private void printWithIndentForDebugging(int indent, StringBuffer debugInfo){
    int childIndent = 1+indent+label.length()+INDENT_LENGTH;
    if(numOfChildren > 0)
      debugInfo.append("(");
    debugInfo.append(getLabel());
    if(numOfChildren > 0){
      for(int i=0;i<INDENT_LENGTH;i++)
        debugInfo.append(" ");
    }  
    if(numOfChildren > 0){  
      //first child, don't print EOL, don't print ending bracket
      children[0].printWithIndentForDebugging(childIndent, debugInfo);
      int i=1;
      //for all the children in the middle, print a EOL after each child
      for(;i<numOfChildren;i++){
        debugInfo.append("\n");
        for(int j=0;j<childIndent;j++)
          debugInfo.append(" ");
        children[i].printWithIndentForDebugging(childIndent, debugInfo);
      }
      debugInfo.append(")");
    }
  }

  /**
  * Method used only in pretty printing
  */
  public void calcDist(){
    if(isLeaf())
      return;
    for(Tree child: children){
      child.calcDist();
    }
    distToEachDirectChild = new int[numOfChildren];
    int[] eachDirectChildPos = new int[numOfChildren];
    Tree currentChild = null;
    Tree prevChild = null;
    for(int i=0;i<numOfChildren;i++){
      currentChild = children[i];
      if(i == 0){
        eachDirectChildPos[i] = 0;
      }else{
        eachDirectChildPos[i] = eachDirectChildPos[i-1] + prevChild.distToRightmostBottomChild + 
                                  H_SEPERATOR_WIDTH + Math.abs(currentChild.distToLeftmostBottomChild);
      }
      prevChild = currentChild;
    }
    int totalSpan = (eachDirectChildPos[0]+eachDirectChildPos[numOfChildren-1]);
    int parentPos = totalSpan/2 + totalSpan%2;
    for(int i=0;i<numOfChildren;i++){
      distToEachDirectChild[i] = eachDirectChildPos[i] - parentPos;
    }
    this.distToLeftmostBottomChild = children[0].distToLeftmostBottomChild + Math.abs(distToEachDirectChild[0]);
    this.distToRightmostBottomChild = children[numOfChildren-1].distToRightmostBottomChild + distToEachDirectChild[numOfChildren-1];
  }

  public String toString(){
    if(isPreterminal()){
      return getNormalizedLabel()+":"+getHeadWord();
    }
    return "";
  }

}
