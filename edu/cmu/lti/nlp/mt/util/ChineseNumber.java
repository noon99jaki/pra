/*Frank Lin
 *
 *Generates Chinese numbers
 */

package edu.cmu.lti.nlp.mt.util;

import java.io.*;

public class ChineseNumber{

  public static final int[] SIMPLIFIED_CHARS={0x96F6,0x4E00,0x4E8C,0x4E09,0x56DB,0x4E94,0x516D,0x4E03,0x516B,0x4E5D,
                                              0x5341,0x767E,0x5343,0x4E07,0x4EBF,0x5146,0x8D1F};
  
  public static final int[] TRADITIONAL_CHARS={0x96F6,0x4E00,0x4E8C,0x4E09,0x56DB,0x4E94,0x516D,0x4E03,0x516B,0x4E5D,
                                               0x5341,0x767E,0x5343,0x842C,0x5104,0x5146,0x8CA0};
  
  public static final int SIMPLIFIED=0;
  public static final int TRADITIONAL=1;
  
  public static String generate(int number,int type){
    
    int[] chars;
    StringBuffer chinese_number=new StringBuffer();
    
    if(type==SIMPLIFIED){
	    chars=SIMPLIFIED_CHARS;
    }
    else{
	    chars=TRADITIONAL_CHARS;
    }
    
    if(number>0){
	    doNumber(number,chars,chinese_number);
	    postProcess(chinese_number,chars);
    }
    else if(number<0){
	    doNumber(Math.abs(number),chars,chinese_number);
	    postProcess(chinese_number,chars);
	    chinese_number.insert(0,(char)chars[16]);
    }
    else{
	    chinese_number.append((char)chars[0]);
    }
    
    return chinese_number.toString(); 
  }

  public static String generate(String number,int type){
    return generate(Integer.parseInt(number.trim().replaceAll(",","")),type);
  }
  
  private static void postProcess(StringBuffer buffer,int[] chars){
    if(buffer.length()>=2&&buffer.charAt(0)==(char)chars[1]&&buffer.charAt(1)==(char)chars[10]){
	    buffer.deleteCharAt(0);
    }
  }
  
  private static void doNumber(int number,int[] chars,StringBuffer buffer){
    
    if(number<10000){
	    doBlock(number,chars,buffer);
    }
    else if(number<100000000){
	    doBlock(number/10000,chars,buffer);
	    buffer.append((char)chars[13]);
	    if(number%10000<1000&&number%10000>0){
        buffer.append((char)chars[0]);
	    }
	    doNumber(number%10000,chars,buffer);
    }
    else if(number<2147483647){
	    doBlock(number/100000000,chars,buffer);
	    buffer.append((char)chars[14]);
	    if(number%100000000<10000000&&number%100000000>0){
        buffer.append((char)chars[0]);
	    }
	    doNumber(number%100000000,chars,buffer);
    }
    else{
	    System.err.println("Cannot generate Chinese number for: "+number);
    }
  }
  
  private static void doBlock(int block,int[] chars,StringBuffer buffer){
    
    if(block<10){
	    if(block!=0){
        buffer.append((char)chars[block]);
	    }
    }
    else if(block<100){
      buffer.append((char)chars[block/10]);
      buffer.append((char)chars[10]);
	    doBlock(block%10,chars,buffer);
    }
    else if(block<1000){
	    buffer.append((char)chars[block/100]);
	    buffer.append((char)chars[11]);
	    if(block%100<10&&block%100>0){
        buffer.append((char)chars[0]);
	    }
	    doBlock(block%100,chars,buffer);
    }
    else if(block<10000){
	    buffer.append((char)chars[block/1000]);
	    buffer.append((char)chars[12]);
	    if(block%1000<100&&block%100>0){
        buffer.append((char)chars[0]);
	    }
	    doBlock(block%1000,chars,buffer);
    }
    else{
	    System.err.println("ERROR!");
	    System.exit(1);
    }
  }
  
  public static void main(String[] args)throws Exception{
    
    PrintWriter writer=new PrintWriter(new OutputStreamWriter(System.out,"UTF8"),true);
    BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
    
    for(String next_line;!(next_line=reader.readLine()).equalsIgnoreCase("quit");){
	    try{
        writer.println(generate(next_line,TRADITIONAL));
	    }catch(Exception e){
        writer.println(e);
	    }
    }
  }
  
}
