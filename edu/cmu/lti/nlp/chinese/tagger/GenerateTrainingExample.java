package edu.cmu.lti.nlp.chinese.tagger;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.nlp.chinese.util.Tree;

/**
 * This class read in tree files and output new trees with head information marked
 * in a pretty printing format
 * During this process, FRAG rooted trees are eliminated from corpus
 */

public class GenerateTrainingExample{
  //private static Logger log = Logger.getLogger( GenerateTrainingExample.class );

  private static BufferedWriter all = null;
  private static BufferedReader pass1Out= null;
  private static int pass = -1;

  private static int lineC = 0;

  //default language Chinese
  private static int LANG = Tree.CHINESE;

  public static void main(String[] args){
    if(args.length != 4 && args.length != 5){
      System.err.println("Usage: java GenerateFirstPassTrainingSample 0|1 LANG(E|C) file_name 1|2(pass) pass1_output(for pass 2)");
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
      
      if(args[3].matches("[1|2]")){
        pass = Integer.parseInt(args[3]);
        if(pass == 2){
          pass1Out = new BufferedReader(new InputStreamReader(new FileInputStream(new File(args[4])))); 
        }
      }
      
      File inputFile = new File(args[2]);
      try{
        String fileName = inputFile.getParent()+"/all.trainingForPass"+pass;
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
      processFile(inputFile);

    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

  public static void pseudoParse(List<String[]> list, List<String> passOut){
    //switch correct POS with pass 1 output
    for(int i=0;i<list.size();i++){
      String temp = list.get(i)[0];
      list.get(i)[0] = passOut.get(i);
      passOut.set(i, temp);
    }
    for(int i=0;i<list.size();i++){
      //String word = list.get(i)[1];
      String pos = passOut.get(i);
      String feature = FeatureExtractor.extractFeature(list,i,pass, LANG);
      StringBuffer sb = new StringBuffer();
      sb.append(pos);
      sb.append(" ");
      sb.append(feature);
      sb.append("\n");
      try{
      all.write(sb.toString());
      }catch(Exception ex){
        ex.printStackTrace();
      }
    }
  } 

  public static void pseudoParse(List<String[]> list){
    for(int i=0;i<list.size();i++){
      //String word = list.get(i)[1];
      String pos = list.get(i)[0];
      String feature = FeatureExtractor.extractFeature(list,i,pass, LANG);
      StringBuffer sb = new StringBuffer();
      sb.append(pos);
      sb.append(" ");
      sb.append(feature);
      sb.append("\n");
      try{
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
        br = new BufferedReader(new InputStreamReader(new FileInputStream(f), "UTF8"));
        while( (line = br.readLine()) != null){
          if(line.startsWith("<S ID=")){
            String id = line.substring(line.indexOf('=')+1, line.lastIndexOf('>'));
            List<String[]> list = new ArrayList<String[]>();
            while(true){
              line = br.readLine();
              if(line == null){
                System.err.println("Error in processFile method: Ill-formated input tree file, <S> tag not closed by </S>");
                System.exit(-1);
              }
              if(line.equals("</S>")){
                break;
              }  
              String[] parts = line.split("@@@@");
              list.add(parts);
            }
            if(list.size() > 0){
              if(pass == 1)
                pseudoParse(list); 
              else{
                List<String> l = new ArrayList<String>();
                int i=0;
                while( i<list.size()){
                  line = pass1Out.readLine();
                  if(line == null){
                    break;
                  }  
                  lineC++;
                  l.add(line.trim());
                  i++;
                }
                //System.out.println("l size="+l.size()+" list.size()="+list.size());
                if(l.size() != list.size()){
                  System.err.println("read in total "+lineC+" lines");
                  System.err.println("Pass 1 out length not matching POS sequence input");
                  System.exit(-1);
                }
                pseudoParse(list, l);
              }
            }
          }
        }
      }  
      else{
        br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
        while( (line = br.readLine()) != null){
          if(line.startsWith("<S")){
            List<String[]> list = new ArrayList<String[]>();
            while(true){
              line = br.readLine();
              if(line == null){
                System.err.println("Error in processFile method: Ill-formated input tree file, <S> tag not closed by </S>");
                System.exit(-1);
              }
              if(line.equals("</S>")){
                break;
              }  
              String[] parts = line.split("@@@@");
              list.add(parts);
            }
            if(list.size() > 0){
              pseudoParse(list); 
            }
          }
        }
      }  
      all.flush();
    }catch(Exception ex){
      ex.printStackTrace();
    }
  }

}


