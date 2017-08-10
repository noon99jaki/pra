/**
 * 
 */
package edu.cmu.lti.nlp.chinese.tagger;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.nlp.Interfaces.ITagPOS;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.util.net.RMIService;

/**
 * @author nlao
 *
 */
public class MaxEntTaggerService  {
	//static RMIService service=null;
	static RMIService service= new RMIService(MaxEntTaggerService.class);
	public static ITagPOS getInstance( ){
		return getInstance(service.p.serverMode);
	}
	public static ITagPOS getInstance(boolean serverMode ){		
		if (serverMode)
			return Client.getInstance();
		return MaxEntTagger.getInstance();		
	}


	public interface IServer extends Remote {				
	  public VectorToken  tagPOSRMI(VectorToken vt )throws RemoteException;		
	}
	public static class Client implements 	ITagPOS{		
		private static  Client instance =null;
		public static Client getInstance() {
			if (instance==null) 	 instance = new Client();			
			return instance;
		}
		public Client(){
			server = (IServer) service.lookup();		
		}
		IServer server;
		
	  public VectorToken  tagPOS(VectorToken vt ){
	  	try{
				//return server.tagPOSRMI(vt);
	  		//to keep it an in-place operating function
	  		VectorX<Token> vt1 = server.tagPOSRMI(vt);
	  		vt.clear();
	  		vt.addAll(vt1);
	  		return vt;
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}
		}		
	}
	
	public static void startService( int port ) {
		service.startService(port, MaxEntTagger.getInstance());
	}
}
