package edu.cmu.lti.util.system;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

import edu.cmu.lti.util.system.CommandService.IClient;
import edu.cmu.lti.util.system.CommandService.IServer;

public class Command implements IServer , IClient{
	
	private static String enc;
	
	private static  Command instance =null;
	public static Command getInstance() {
		enc = "utf-8";
		if (instance==null) 	 instance = new Command();
		return instance;
	}
	
	public static Command getInstance( String enc ) {
		Command.enc = enc;
		if (instance==null) instance = new Command();
		return instance;
	}	
	
	
	public String executeRMI( String[] cmdarray, String stdin ) 
	throws RemoteException{
		return execute( cmdarray, stdin );
	}
	public String execute( String[] cmdarray, String stdin ){
		return execute( cmdarray, stdin, enc );
	}	

	
	
	public String executeRMI( String[] cmdarray, String stdin, String enc ) 
	throws RemoteException{
		return execute(  cmdarray,  stdin,  enc );
	}
	public String execute( String[] cmdarray, String stdin, String enc ) {
		Process process=null;
	    PrintWriter writer=null;
	    BufferedReader reader=null;
	    StringBuffer processed=new StringBuffer();

	    try{
			process=Runtime.getRuntime().exec(cmdarray,null,new File("./"));
			
			writer=new PrintWriter(new OutputStreamWriter(process.getOutputStream(),enc));
			writer.println(stdin);
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
			return stdin;
		}else{
			return processed.toString();
	    }
	}
	
	public String executeRMI( String command, String args, String[] text, int splitSize ) throws RemoteException{
		return execute( command,  args, text,splitSize);		
	}
	public String execute( String command, String args, String[] text, int splitSize ) {
		StringBuilder sb = new StringBuilder();
		
		List<String> q = new ArrayList<String>();
		List<String[]> queue = new ArrayList<String[]>(); 
		for ( int i=0; i<text.length; i++ ) {
			q.add( text[i] );
			if ( i%splitSize==(splitSize-1) ) {
				queue.add( (String[]) q.toArray(new String[q.size()]) );
				q = new ArrayList<String>();
			}
		}
		if ( q.size()>0 ) {
			queue.add( (String[]) q.toArray(new String[q.size()]) );
		}
		
		for ( String[] miniText : queue ) {
			sb.append( execute( command, args, miniText ) );
		}
		return sb.toString();
	}
	
	
	public String executeRMI(String command, String args, String[] text)throws RemoteException{
		return execute( command,  args, text);
	}	
	public String execute(String command, String args, String[] text) {
        Process process=null;
	    PrintWriter writer=null;
	    BufferedReader stdOutReader=null;
	    BufferedReader stdErrReader=null;
	    StringBuffer processed=new StringBuffer();
	
	    try{                                                   
	        process=Runtime.getRuntime().exec(command+" "+args,null,new File("./"));
	
	        writer=new PrintWriter(new OutputStreamWriter(process.getOutputStream(),enc));
	        stdOutReader = new BufferedReader(new InputStreamReader(process.getInputStream(),enc));
	        stdErrReader = new BufferedReader(new InputStreamReader(process.getErrorStream(),enc));
	
	        String line="";
            StringBuilder s = new StringBuilder();
            for ( String in : text ) {
            	s.append(in+"\n");
            }
            writer.println( s.toString()+"\n" );
            writer.flush();
            process.waitFor();
            while ((line = stdErrReader.readLine())!=null){
            }
            while ((line = stdOutReader.readLine())!=null){
            	processed.append(line+"\n");
            }
            writer.close();
            stdOutReader.close();
            stdErrReader.close();
            process.destroy();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        	try {
        		writer.close();
                stdOutReader.close();
                stdErrReader.close();
                process.destroy();
        	} catch (Exception e) {
        		e.printStackTrace();
        	}
        }

        if(processed==null){
                System.err.println("WARNING: Cannot get the result.");
                return "";
        }else{
                return processed.toString();
        }
	}
	
	public String executeRMI(String[] cmdarray, String[] envp) throws RemoteException{
		return execute( cmdarray, envp);
	}	
	/**
	 * This is the proper way of running a command. 
	 * 
	 * @param cmdarray array containing the command to call and its arguments.
	 * @param env parray of strings, each element of which has environment variable settings in format <i>name=value</i>. 
	 * @return execution result
	 * @throws Exception
	 * @throws RemoteException
	 */
	public String execute(String[] cmdarray, String[] envp) {
		Runtime runtime = null;
		Process process = null;
		InputStream is = null;
	    BufferedReader br = null;
		StringBuffer output = new StringBuffer();
		try {
			runtime = Runtime.getRuntime();
            process = runtime.exec( cmdarray, envp );
			is = process.getInputStream();
			br = new BufferedReader(new InputStreamReader(is));  
			String line = null;
			while ((line = br.readLine()) != null) {
				output.append(line+"\n");
			}
			br.close();
			is.close();
			process.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return output.toString();
	}


	public String executeRMI( String command ) throws RemoteException{
		return execute(command );
	}
	/**
	 * Runs arbitrary command.
	 * 
	 * @param command the command to call and its arguments.
	 * @return execution result
	 * @throws Exception
	 * @throws RemoteException
	 * @deprecated depending on command arguments, this method doesn't work. 
	 */
	public String execute( String command ) {
		return execute( command.split(" ") );
	}


	
	public String executeRMI( String[] cmdarray ) throws RemoteException{
		return execute(cmdarray);
	}
	/**
	 * This is the proper way of running a command. 
	 * 
	 * @param cmdarray array containing the command to call and its arguments.
	 * @return execution result
	 * @throws Exception
	 * @throws RemoteException
	 */
	public String execute( String[] cmdarray ){
		String[] s = null;
		return execute( cmdarray, s );
	}

}
