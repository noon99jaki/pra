/**
 * 
 */
package edu.cmu.lti.util.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import edu.cmu.lti.algorithm.container.VectorS;

/**
 * @author nlao
 *
 */
public class MyProcess {
	public ProcessBuilder pb;

	public Process proc;
	public BufferedWriter out;
	public BufferedReader in;
	public BufferedReader err;
	public String cmd;
	
	public void printErr(){
		String s ;
		System.out.println("remote error message from: "
				+cmd);//				+pb.toString());
	  try{
	  	while ((s = err.readLine()) != null) {
	      System.out.println(s);
	    }
	  }catch(Exception ex){
	    ex.printStackTrace();
	  }			
	}
	public boolean write(String str){
    try{
	    out.write(str,0,str.length());
	    out.flush();
    }catch(Exception ex){
    	printErr();
      ex.printStackTrace();
      return false;
    }		
    return true;
	}
	public String readLine(){	
    try{
    	return in.readLine().trim();
	  }catch(Exception ex){
    	printErr();
	    ex.printStackTrace();
	    return null;
	  }			
	}
	public String dir=null;
	public void start(String ... args){
		cmd = new VectorS(args).join(" ") ;
		try {
			pb = new ProcessBuilder(args);	
			if (dir != null)
				pb.directory(new File(dir));
			proc = pb.start();
			out = new BufferedWriter(new OutputStreamWriter(
					proc.getOutputStream(), "UTF8"));
			in = new BufferedReader(new InputStreamReader(
					proc.getInputStream()));
			err = new BufferedReader(new InputStreamReader(
					proc.getErrorStream()));
			//proc.getErrorStream().close();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}		
	}	
  public void destroy(){
  	try{
	    out.close();
	    in.close();
	    err.close();
	    proc.destroy();
		} 
		catch (Exception ex) {
			ex.printStackTrace();
		}		    
  }
  public String pushPop(String str){
    write(str);
    return readLine();
  }
}
