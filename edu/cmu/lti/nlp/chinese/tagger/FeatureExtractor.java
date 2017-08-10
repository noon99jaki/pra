package edu.cmu.lti.nlp.chinese.tagger;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.nlp.chinese.util.Tree;



/**
*  This class contains a collection of utility methods
*/

public class FeatureExtractor{
  //private static Logger log = Logger.getLogger( FeatureExtractor.class );

  
  public static String lastAction = "NONE";
  public static int bracketCount = 0;

  /**
  * Extract features from the current Stack and Queue for Chinese Parser
  * @param list
  * @param position
  * @param pass
  * @return String the feature set
  */
  public static String extractFeatureForChinese(List<String[]> list, int position, int pass){
    List<String> features = extractFeatureListForChinese(list, position, pass);
    StringBuffer featureStr = new StringBuffer(); 
    for(int i=0;i<features.size();i++){
      featureStr.append(features.get(i));
      featureStr.append(" ");
    }
    return featureStr.toString();
  }
  
  public static List<String> extractFeatureListForChinese(List<String[]> list, int position, int pass){
    //index 0 is POS, index 1 is word
    List<String> feature = new ArrayList<String>(55); 
    //current word
    String word =list.get(position)[1]; 
    feature.add("1-"+word);
    //previous word and POS
    if(position > 0){
      feature.add("2-"+list.get(position-1)[1]);
      feature.add("3-"+list.get(position-1)[0]);
    }else{
      feature.add("2-NON");
      feature.add("3-NON");
    }
    //second previous POS
    if(position > 1){
      feature.add("4-"+list.get(position-2)[0]);
    }else{
      feature.add("4-NON");
    }
    //NEW feature prev 2 POS chain
    //if(position > 1){
    //  feature.add("24-"+list.get(position-2)[0]+"-"+list.get(position-1)[0]);
    //}else{
    //  if(position > 0){
    //    feature.add("24-NON-"+list.get(position-1)[0]);
    //  }else
    //    feature.add("24-NON-NON");
    //}

    //if word is punctuation
    if(word.matches("[—…。，、；：？！‘’“”∶〔〕〈〉《》「」『』．（）［］｛｝]")){
      feature.add("5-PU");
    }else{
      //feature.add(1+"");
    }
    int len = word.codePointCount(0,word.length());
    if(word.startsWith("百分之")){
      feature.add("10-PER");
    }else{
      if(word.matches("[·余．多点一二三四五六七八九〇千万亿十百兆厘壹贰叁肆伍陆柒捌玖零仟拾佰４０１３５２６７８９]+")){
        feature.add("8-NUM");
      }else{
        if(word.matches("第[一二三四五六七八九〇千万亿十百兆厘壹贰叁肆伍陆柒捌玖零仟拾佰４０１３５２６７８９]+.*")){
          feature.add("19-DI");
        }else
        if(word.matches("[一二三四五六七八九〇千万亿十百兆厘壹贰叁肆伍陆柒捌玖零仟拾佰４０１３５２６７８９]+[日月年号]+")){
          feature.add("9-DATE");
        }else
        if(word.contains("·")){
          feature.add("6-FORE");
        }else
        if(word.matches(".*[ＡＢＣＤＥＦＧＨＩＪＫＬＭＮＯＰＱＲＳＴＵＶＷＸＹＺ]+.*")){
          //if word is a foreign name
          feature.add("11-LETR");
        }else
        if((len == 2 || len == 3)&& 
          word.matches("^[弗厦罗丁叶邹胡崔柯朱刘彭唐张萨陈黄邱吴许谭蒋徐梁冯吕薛李苏郭王孙卢浙郑戴沈杜郝赵蔡邓廖宋毕卞曹程池范方樊费符傅葛龚韩洪侯胡姜孔林骆孟潘邱魏邬邢俞袁詹张朱庄].*")
          ){
          feature.add("20-CHNM");
        }
        //else if(word.codePointCount(0,word.length()) > 5){
        //  //if word has 5 or more characters
        //  feature.add("7-5MORE");
        //}else{
        //  //feature.add(1+"");
        //}
      }  
    }
    //word length feature
    if(word.codePointCount(0,word.length()) > 4){
      feature.add("7-5");
    }else
      feature.add("7-"+word.codePointCount(0,word.length()));


    //additional features for second pass
    if(pass == 2){
      //first future POS and word
      if(position < list.size()-1 ){
        feature.add("13-"+list.get(position+1)[0]);
        feature.add("12-"+list.get(position+1)[1]);
      }else{
        feature.add("13-NON");
        feature.add("12-NON");
      }
      //second future POS
      if(position < list.size()-2){
        feature.add("14-"+list.get(position+2)[0]);
      }else{
        feature.add("14-NON");
      }
    }

    if(pass == 2){
      //feature.add("16-"+list.get(position)[0]);
      if(position > 0){
        feature.add("15-"+list.get(position-1)[0]+"-"+list.get(position)[0]);
      }
      if(position < list.size()-1){
        feature.add("17-"+list.get(position)[0]+"-"+list.get(position+1)[0]);
      }
      //if(position > 0 && position < list.size()-1){
      //  feature.add("18-"+list.get(position-1)[0]+"-"+list.get(position)[0]+"-"+list.get(position+1)[0]);
      //}
    }

    if(pass == 2){
      if(list.get(position)[0].matches("NN|NT|NR|VV|VE|VA|VC")){
        if(position > 0 && list.get(position-1)[0].matches("NN|NT|NR|VV|VE|VA|VC")){
          String w = list.get(position-1)[1];
          int len2 = w.codePointCount(0,w.length());
          feature.add("21-"+len2+"-"+len); 
        }
        if(position < list.size()-1 && list.get(position+1)[0].matches("NN|NT|NR|VV|VE|VA|VC")){
          String w = list.get(position+1)[1];
          int len2 = w.codePointCount(0,w.length());
          feature.add("22-"+len+"-"+len2); 
        }
      }
    }

    //previous 1 POS concatenated with future 1 POS
    if(pass == 2){
      if(position > 0){
        if(position < list.size()-1)
          feature.add("23-"+list.get(position-1)[0]+"-"+list.get(position+1)[0]);
        else  
          feature.add("23-"+list.get(position-1)[0]+"-NON");
      }else{
        if(position < list.size()-1)
          feature.add("23-NON-"+list.get(position+1)[0]);
        else  
          feature.add("23-NON-NON");
      }
    }

    return feature;
  }

  public static List<String> extractFeatureList(List<String[]> list, int position, int pass, int lang){
    if(lang == Tree.CHINESE)
      return extractFeatureListForChinese(list, position, pass);
    else if(lang == Tree.ENGLISH){
      System.err.println("extractFeatureList for English not implemented");
    }else
      System.err.println("Error in FeatureExtractor: LANG not defined");
    return null;
  }  

  public static String extractFeature(List<String[]> list, int position, int pass, int lang){
    if(lang == Tree.CHINESE)
      return extractFeatureForChinese(list, position, pass);
    else if(lang == Tree.ENGLISH)
      System.err.println("extractFeature for English not implemented");
    else{
      System.err.println("Error in FeatureExtractor: LANG not defined");
    }  
    return null;
  }  
  
}  
