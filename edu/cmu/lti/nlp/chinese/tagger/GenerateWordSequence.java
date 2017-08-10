package edu.cmu.lti.nlp.chinese.tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;

import edu.cmu.lti.algorithm.structure.MyQueue;
import edu.cmu.lti.nlp.chinese.util.Tree;
import edu.cmu.lti.nlp.chinese.util.TreeHelper;

/**
 * This class read in tree files and output new trees with head information marked
 * in a pretty printing format
 * During this process, FRAG rooted trees are eliminated from corpus
 */

public class GenerateWordSequence{
  //private static Logger log = Logger.getLogger( GenerateWordSequence.class );

  public static final String C_FILE_EXTENSION = ".fid";
  public static final String E_FILE_EXTENSION = ".MRG";

  private static BufferedWriter all = null;

  //default language Chinese
  private static int LANG = Tree.CHINESE;

  public static void main(String[] args){
    if(args.length != 3){
      System.err.println("Usage: java GenerateWordSequence 0|1 LANG(E|C) dir_name");
      System.exit(-1);
    }
    try{
      if(args[1].equals("E"))
        LANG = Tree.ENGLISH;
      else if(args[1].equals("C"))
        LANG = Tree.CHINESE;
      else{
        System.err.println("Usage: language options: E|C");
        System.exit(-1);
      }

      File dir = new File(args[2]);
      try{
        String fileName = dir.getPath()+"/all.wordSequence";
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
              all.write("<S ID="+id+">\n");
              Tree tree = Tree.newNode(id, strBuffer.toString(), LANG);
              if(tree == null){
                System.err.println("For file "+f.getName()+" Error in processFile method: Ill-bracketed tree string:"+strBuffer.toString());
                System.exit(-1);
              }  
              TreeHelper.removeEmptyNode(tree);
              
              MyQueue<Tree> preterminals = TreeHelper.getPreterminals(tree);
              List<Tree> list = preterminals.asList();
              for(Tree node : list){
                all.write(node.getHeadWord()+"\n");
              }
              all.write("</S>\n");
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
              all.write("<S>\n");
              Tree tree = Tree.newNode(strBuffer.toString(), LANG);
              if(tree == null){
                System.err.println("For file "+f.getName()+" Error in processFile method: Ill-bracketed tree string:"+strBuffer.toString());
                System.exit(-1);
              }  
              TreeHelper.removeEmptyNode(tree);
              
              MyQueue<Tree> preterminals = TreeHelper.getPreterminals(tree);
              List<Tree> list = preterminals.asList();
              for(Tree node : list){
                all.write(node.getHeadWord()+"\n");
              }
              all.write("</S>\n");
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

