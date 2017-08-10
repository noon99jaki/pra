package edu.cmu.lti.nlp.mt;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.TreeMap;

import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.nlp.CLang;
import edu.cmu.lti.util.net.RMIService;

public class MTService {
	//static RMIService service=null;
	static RMIService service= new RMIService(MTService.class);
	public static IClient getInstance(String srcLang, String trgLang){
		return getInstance( srcLang,  trgLang, service.p.serverMode);
	}
	public static IClient getInstance(	String srcLang, String trgLang, boolean serverMode 	 ){
		if (serverMode)
			return Client.getInstance(srcLang,  trgLang);	
		return MTModule.getInstance(srcLang,  trgLang);	
	}	
	
	
	public static class Client implements IClient{		
		
		private static Map<String, Client> m_instance
			= new TreeMap<String, Client>();
		public static Client getInstance(String srcLang, String trgLang) {
			String bn= getCode(srcLang,trgLang);
			Client instance= m_instance.get(bn);
			if (instance==null) {
				instance = new Client(srcLang, trgLang);
				m_instance.put(bn, instance);
			}
			return instance;
		}

		public Client(String srcLang, String trgLang){
			server = (IServer) service.lookup(getCode(srcLang,trgLang) );
		}
		IServer server;
		
		public MTResult translate( String text, String type ){
			try{
				return server.translateRMI(text, type);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}
		}		
		public String translateSentence( String sent ){
			try{
				return server.translateSentenceRMI(sent);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}
		}	
		public MapSD translateKeyTerm( String term ){
			try{
				return server.translateKeyTermRMI(term);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}
		}
		
	
		public MapSD translateSentenceRaw( String sent ){
			try{
				return server.translateSentenceRawRMI(sent);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
	
	//these params are independent of MTModule?
	public static interface IServer extends Remote { //abstract
		public MTResult translateRMI( String text, String type ) throws RemoteException;
		public String translateSentenceRMI( String sent ) throws RemoteException;
		public MapSD translateKeyTermRMI( String term )throws RemoteException;
		public MapSD translateSentenceRawRMI( String sent )throws RemoteException;
	}
	public static interface IClient{
		public MTResult translate( String text, String type );
		public String translateSentence( String sent);
		public MapSD translateKeyTerm( String term );
		public MapSD translateSentenceRaw( String sent );
	}
	
	//why add server for each lang pair separately?
	//Oh, because they are separate objects in memory.

	public static void startService( int port ) {
		startService(port, CLang.en_US, CLang.ja_JP);
		startService(port, CLang.en_US, CLang.zh_CN); 
		startService(port, CLang.zh_CN, CLang.en_US); 
	}
	public static void startService(int port,String srcLang,String trgLang ) {
		service.startService(port
				, MTModule.getInstance(srcLang,trgLang)
				, getCode(srcLang,trgLang));
	}
	public static String getCode(String srcLang,String trgLang ){
		return "TM-"+srcLang+"-"+trgLang;
	}
}
