/**
 * 
 */
package edu.cmu.lti.nlp.chinese;

import edu.cmu.lti.util.net.SocketClient;

/**
 * @author nlao
 * edu.cmu.lti.nlp.chinese.MSRSegClient
 */
public class MSRSegClient extends SocketClient{
	//socket connection to MSRSegServer (only one object instance, can't run IX and QA in the same JVM)
	//public static final MSRSegClient instance = new MSRSegClient();	
	public static  MSRSegClient instance =null;
	public static MSRSegClient getInstance() {
		if (instance==null) 	 instance = new MSRSegClient();		
		return instance;
	}	
	
	public MSRSegClient(){
		super(MSRSegClient.class);
		//if (p.port==null) p.port = 9005;
		//if (p.server==null) p.server ="kariya.lti.cs.cmu.edu";
		connect();
	}		

	public String process(String txt){
		txt = txt.trim()+"\n";
		send(txt);		
		String rlt =recieve(); 
		//the next line ensures \n will be a token too, but maybe unnecessary
		//rlt = rlt.replace("/\n","");
		return rlt;
	} 	

}
