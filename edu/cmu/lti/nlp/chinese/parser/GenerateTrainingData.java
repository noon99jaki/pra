package edu.cmu.lti.nlp.chinese.parser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import edu.cmu.lti.algorithm.structure.MyQueue;
import edu.cmu.lti.algorithm.structure.MyStack;
import edu.cmu.lti.nlp.chinese.util.Tree;
import edu.cmu.lti.nlp.chinese.util.TreeHelper;

/**
 * This class read in tree files and output new trees with head information marked
 * in a pretty printing format
 * During this process, FRAG rooted trees are eliminated from corpus
 */

public class GenerateTrainingData{
  //private static Logger log = Logger.getLogger( GenerateTrainingData.class );

  public static final String C_FILE_EXTENSION = ".fid";
  public static final String E_FILE_EXTENSION = ".MRG";
  public static final String HM_PP_TRANS_FILE_EXTENSION = ".training";

  //default Language Chinese
  private static int LANG = Tree.CHINESE;

  private static BufferedWriter all = null;

  public static void main(String[] args){
    if(args.length != 2){
      System.err.println("Usage: java GenerateTrainingData LANG(E|C) dir_name");
      System.exit(-1);
    }
    try{
      if(args[0].equals("E"))
        LANG = Tree.ENGLISH;
      else if(args[0].equals("C"))
        LANG = Tree.CHINESE;
      else{
        System.err.println("Usage: language options: E|C");
        System.exit(-1);
      }

      File dir = new File(args[1]);
      try{
        String fileName = dir.getPath()+"/all.trainingForZhangMaxentReducedBranching";
        if(LANG == Tree.CHINESE){
          fileName += ".C";
          all = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF8"));
        }else{  
          fileName += ".E";
          all = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName)));
        }  
      }catch(Exception ex){
        ex.printStackTrace();
      }
      visitAllFiles(dir);

    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

  public static void visitAllFiles(File f) {
    if (f.isDirectory()) {
      String[] children = f.list();
      for (int i=0; i<children.length; i++) {
          visitAllFiles(new File(f, children[i]));
      }
    } else {
      if(checkFileExtension(f.getName()) == true)
        processFile(f);
    }
  }

  private static boolean checkFileExtension(String fileName){
    if(LANG == Tree.CHINESE){
      if(fileName.endsWith(C_FILE_EXTENSION))
        return true;
      else
        return false;
    }else{
      if(fileName.endsWith(E_FILE_EXTENSION))
        return true;
      else
        return false;

    }
  }

  public static void pseudoParse(Tree tree){
    Tree root = tree;
    MyQueue<Tree> queue = TreeHelper.getPreterminals(tree);
    MyStack<Tree> stack = new MyStack<Tree>();
    while(queue.peek() != null || stack.size() > 1){
      List<String> featureList = FeatureExtractor.extractFeatureList(stack,queue);
      String action = null;
      if(stack.isEmpty()){
        action = "S";
        Tree queueItem = queue.poll();
        if(queueItem.getHeadWord().matches("[‘“〔〈《「『（［｛〖【]")){
          FeatureExtractor.bracketCount++;
        }else if(queueItem.getHeadWord().matches("[’”〕〉》」』）］｝〗】]")){
          FeatureExtractor.bracketCount--;
        }
        stack.push(queueItem);
      }else{
        Tree top = stack.peek();
        int indexAsChild = top.getIndexAsChild();
        if(indexAsChild == 1){//Binary reduction
          Tree parent = TreeHelper.locateParent(top, root);
          action = "R:"+parent.getNormalizedLabel();
          stack.pop();
          stack.pop();
          stack.push(parent);
        }else if(top.getParentNumOfChildren() == 1){//Unary reduction
          Tree parent = TreeHelper.locateParent(top, root);
          action = "U:"+parent.getNormalizedLabel();
          stack.pop();
          stack.push(parent);
        }else{//Shift
          action = "S";
          Tree queueItem = queue.poll();
          if(queueItem.getHeadWord().matches("[‘“〔〈《「『（［｛〖【]")){
            FeatureExtractor.bracketCount++;
          }else if(queueItem.getHeadWord().matches("[’”〕〉》」』）］｝〗】]")){
            FeatureExtractor.bracketCount--;
          }
          stack.push(queueItem);
        }
      }
      FeatureExtractor.lastAction = action;
      try{
        StringBuffer sb = new StringBuffer();
        sb.append(action+" ");
        for(int i=0;i<featureList.size();i++){
          //sb.append((i+1)+"-");
          sb.append((i+1)+"-");
          sb.append(featureList.get(i));
          sb.append(" ");
        }
        sb.append("\n");
        all.write(sb.toString());
      }catch(Exception ex){
        ex.printStackTrace();
      }
    }
  } 

  private static void processFile(File f){
    try{
      BufferedReader br = null;
      String line = null;

      if(LANG  == Tree.CHINESE){
        br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "GB18030"));
        while( (line = br.readLine()) != null){
          if(line.startsWith("<S ID=")){
            String id = line.substring(line.indexOf('=')+1, line.lastIndexOf('>'));
            StringBuffer strBuffer = new StringBuffer();
            while(true){
              line = br.readLine();
              if(line == null){
                System.err.println("Error in processFile method: Ill-formated input tree file, <S> tag not closed by </S>");
                System.exit(-1);
              }
              if(line.equals("</S>")){
                break;
              }  
              strBuffer.append(line.trim());
            }
            String treeStr = strBuffer.toString();
            if(treeStr.trim().length() > 0){
              Tree tree = Tree.newNode(id, strBuffer.toString(), LANG);
              if(tree == null){
                System.err.println("For file "+f.getName()+" Error in processFile method: Ill-bracketed tree string:"+strBuffer.toString());
                System.exit(-1);
              }  
              TreeHelper.removeEmptyNode(tree);
              TreeHelper.markHeadNode(tree);
              TreeHelper.transform(tree);
              
              pseudoParse(tree); 
            }
          }
        }
      }  
      else{
        br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        line = br.readLine();
        while( line != null){
          if(line.startsWith("(")){
            StringBuffer strBuffer = new StringBuffer();
            strBuffer.append(line.trim());
            while(true){
              line = br.readLine();
              if(line == null || line.startsWith("(")){
                break;
              }
              strBuffer.append(line.trim());
            }
            String treeStr = strBuffer.toString();
            if(treeStr.trim().length() > 0){
              Tree tree = Tree.newNode(strBuffer.toString(), LANG);
              if(tree == null){
                System.err.println("For file "+f.getName()+" Error in processFile method: Ill-bracketed tree string:"+strBuffer.toString());
                System.exit(-1);
              }  
              TreeHelper.removeEmptyNode(tree);
              TreeHelper.markHeadNode(tree);
              TreeHelper.transform(tree);
              
              pseudoParse(tree); 
            }
          }else
            line = br.readLine();
        }
      }  
      all.flush();
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

}


