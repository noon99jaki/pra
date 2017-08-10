package edu.cmu.lti.test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.net.Socket;

public class SocketClient {
	public Socket server = null;
	public PrintWriter socketOut = null;
	public BufferedReader socketIn = null;
	
	
	public static class Param extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public Integer port ;
		public String server;
		public Param(Class c) {//throws IOException{
			super(c);
			port = getInt("port",9005);
			server=getString("server","kariya.lti.cs.cmu.edu");
		}
	}
	
	public Param p=null;//new Param();
	public SocketClient(Class c){
		p=new Param(c);
	}
	public void connect(){
		try {
			//InetAddress ad = InetAddress.getByName("128.2.178.240");
			//server = new Socket(ad, p.port);
			System.out.print("connecting to "+p.server+":"+ p.port +"...");
			server = new Socket(p.server, p.port);
			socketOut = new PrintWriter(new PrintStream(server
					.getOutputStream(), true, "UTF8"), true);
			socketIn = new BufferedReader(new InputStreamReader(server
					.getInputStream(), "UTF8"));
		} catch (Exception ex) {
			System.out.println("failed");
			ex.printStackTrace();
			return;
		}		
		System.out.println("succeded");
	}
	public void send(String txt){
		socketOut.print(txt);
		socketOut.flush();
		socketOut.print("EOF\n");
		socketOut.flush();
	}
	public String recieve(){
		String s = null;
		StringBuffer sb = new StringBuffer();
		try{
			while ((s = socketIn.readLine())!= null && !s.equals("EOF")) {
				sb.append(s);
				sb.append("\n");
			}
		}catch(Exception e){
			e.printStackTrace();
		}

		return sb.toString();		
	}	
}
