/**
 * 
 */
package edu.cmu.lti.nlp.parsing;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.cmu.lti.nlp.Interfaces.ISynxParseTaggedSent;
import edu.cmu.lti.nlp.parsing.SRParser.SRParser;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.util.net.RMIService;

/**
 * RMI service for ALL parsers derived from Parser?
 * worry about it later 
 * @author nlao
 *
 */
public class ParserService {
	//static RMIService service=null;
	static RMIService service= new RMIService(ParserService.class);
	public static ISynxParseTaggedSent getInstance( ){
		return getInstance(service.p.serverMode);
	}
	public static ISynxParseTaggedSent getInstance(boolean serverMode ){		
		if (serverMode)
			return Client.getInstance();
		return SRParser.getInstance();		
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
		SRParser ins = SRParser.getInstance();
		ins.loadModel();
		service.startService(port, ins);
	}
}
