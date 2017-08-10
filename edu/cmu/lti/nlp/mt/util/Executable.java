package edu.cmu.lti.nlp.mt.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class Executable {

	public static String execute(String command, String text, String enc){
		Process process=null;
	    PrintWriter writer=null;
	    BufferedReader reader=null;
	    StringBuffer processed=new StringBuffer();

	    try{
			process=Runtime.getRuntime().exec(command,null,new File("./"));

			writer=new PrintWriter(new OutputStreamWriter(process.getOutputStream(),enc));
			writer.println(text);
			writer.close();

			reader=new BufferedReader(new InputStreamReader(process.getInputStream(),enc));
			
			String line="";
			while((line=reader.readLine())!=null){
				processed.append(line+"\n");
			}
			reader.close();
			process.destroy();
		}catch(Exception e){
			e.printStackTrace();
		}
	    
		if(processed==null){
			System.err.println("WARNING: Cannot get the result.");
			return text;
		}else{
			return processed.toString();
	    }
	}

}
