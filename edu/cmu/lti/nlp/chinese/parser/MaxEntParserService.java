/**
 * 
 */
package edu.cmu.lti.nlp.chinese.parser;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.nlp.Interfaces.ISynxParseTaggedSent;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.util.net.RMIService;

/**
 * @author nlao
 *
 */
public class MaxEntParserService {
	//static RMIService service=null;
	static RMIService service= new RMIService(MaxEntParserService.class);
	public static ISynxParseTaggedSent getInstance( ){
		return getInstance(service.p.serverMode);
	}
	public static ISynxParseTaggedSent getInstance(boolean serverMode ){		
		if (serverMode)
			return Client.getInstance();
		return MaxEntParser.getInstance();		
	}


	public interface IServer extends Remote {				
		public TreeSyntax synxParseTaggedSentRMI(VectorToken vt) throws RemoteException;		
	}
	public static class Client// extends RMIService
		implements ISynxParseTaggedSent{		
		private static  Client instance =null;
		public static Client getInstance() {
			if (instance==null) 	 instance = new Client();			
			return instance;
		}
		public Client(){
			server = (IServer) service.lookup();
		}
		IServer server;
		
		public TreeSyntax synxParseTaggedSent(VectorToken vt){
			try{
				return server.synxParseTaggedSentRMI(vt);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}
		}		
	}
	public static void startService( int port ) {
		service.startService(port, MaxEntParser.getInstance());
	}
}
