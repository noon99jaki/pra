package edu.cmu.lti.nlp.english;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.cmu.lti.nlp.Interfaces.ISynxParseSent;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.util.net.RMIService;

public class CharniakParserService {

	//static RMIService service=null;
	static RMIService service= new RMIService(CharniakParserService.class);
	public static ISynxParseSent getInstance( ){
		return getInstance(service.p.serverMode);
	}
	public static ISynxParseSent getInstance(boolean serverMode ){		
		if (serverMode)
			return Client.getInstance();
		return CharniakParser.getInstance();		
	}


	public interface IServer extends Remote {				
		public TreeSyntax synxParseSentRMI(String sentence) throws RemoteException ;
	}
	public static class Client	implements ISynxParseSent{		
		private static  Client instance =null;
		public static Client getInstance() {
			if (instance==null) 	 instance = new Client();			
			return instance;
		}
		public Client(){
			server = (IServer) service.lookup();
		}
		IServer server;
		
		public TreeSyntax synxParseSent(String sentence) {
			try{
				return server.synxParseSentRMI(sentence);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}
		}		
	}
	public static void startService( int port ) {
		CharniakParser ins = CharniakParser.getInstance();
		ins.startModel();
		service.startService(port, ins);
	}	
}
