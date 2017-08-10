/*Frank Lin
 *
 *For changing single-width characters to double-width characters and vice versa for [A-Z],[a-z],[0-9], and common symbols and punctuation marks
 */

package edu.cmu.lti.nlp.mt.util;

import java.io.*;

public class DoubleWidthChar{

	public static String toDoubleWidth(String s){

		char[] chars=s.toCharArray();
		
		for(int i=0;i<chars.length;i++){
	    chars[i]=toDoubleWidth(chars[i]);
		}
		
		return (new String(chars));
	}
	
	public static char toDoubleWidth(char c){
		return (char)toDoubleWidth((int)c);
	}
	
	public static int toDoubleWidth(int c){
		if(c<=0x007e&&c>=0x0021){
	    return c+0xfee0;
		}
		else{
	    return c;
		}
	}

	public static String toSingleWidth(String s){
		
		char[] chars=s.toCharArray();
		
		for(int i=0;i<chars.length;i++){
	    chars[i]=toSingleWidth(chars[i]);
		}
		
		return (new String(chars));
	}
	
	public static char toSingleWidth(char c){
		return (char)toSingleWidth((int)c);
	}
	
	public static int toSingleWidth(int c){
		if(c<=0xff5e&&c>=0xff01){
	    return c-0xfee0;
		}
		else{
	    return c;
		}
	}	

	public static void main(String[] args)throws Exception{
		
		PrintWriter writer=new PrintWriter(new OutputStreamWriter(System.out,"UTF8"),true);
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		
		for(String next_line;!(next_line=reader.readLine()).equalsIgnoreCase("quit");){
	    try{
				writer.println(toDoubleWidth(next_line));
	    }catch(Exception e){
				writer.println(e);
	    }
		}
	}
}
