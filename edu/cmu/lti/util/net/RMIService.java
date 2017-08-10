package edu.cmu.lti.util.net;

import java.rmi.Remote;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class  RMIService {
	
	//these params are independent of MTModule?
	//public static abstract  void startService( int port ) ;

	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String host ;//adb1.lti.cs.cmu.edu";
		public int port ;
		public boolean serverMode;
		
		public Param(Class c) {
			super(c);
			parse();
		}
		public void parse(){	
			serverMode =  getBoolean("serverMode", true );
			//host = getString("host", "mu.lti.cs.cmu.edu");
			//host = getString("host", "128.2.204.124");//hawthorne
			host = getString("host", "128.2.204.113");	// banarus
			//host = getString("host", "128.2.205.96");	// artigas
			
			
			port = getInt("port", 2001);
		}
	}
	public Registry registry;
	public Param p;
	//public Class cServer;
	public Class cService;
	public RMIService(Class c){
		cService = c;
		p = new Param(c);
		if (p.serverMode){
			try {
				registry = LocateRegistry.getRegistry(p.host, p.port);
			} catch ( Exception e ) {
				e.printStackTrace();
			}
		}		
	}
	public Remote lookup(){
		return lookup(cService.getSimpleName());
//		return lookup(cService.getCanonicalName());
	}
	public Remote lookup(String bindName){
		//super(Client.class);
		try {
			return (Remote) registry.lookup(bindName );
		} catch ( Exception e ) {
			e.printStackTrace();
			return null;
		}			
	}
	public void startService(int port, Remote obj) {
		//startService(port, obj,cService.getCanonicalName());
		startService(port, obj,cService.getSimpleName());
	}
	public void startService(int port
			, Remote obj, String bindName) {
		try {
			if (System.getSecurityManager() == null) 
				System.setSecurityManager(new SecurityManager());
			Registry registry = LocateRegistry.getRegistry( port );
			registry.bind(bindName, 
					UnicastRemoteObject.exportObject(obj, 0));
			
			System.err.println(
					"Server ready: bind "+  obj.getClass().getSimpleName()
					+" to "+bindName);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
