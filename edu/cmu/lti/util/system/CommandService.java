package edu.cmu.lti.util.system;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.cmu.lti.util.net.RMIService;

public class CommandService {
	//static RMIService service=null;
	static RMIService service= new RMIService(CommandService.class);
	public static IClient getInstance( ){
		return getInstance(service.p.serverMode);
	}
	public static IClient getInstance(boolean serverMode ){		
		if (serverMode)
			return Client.getInstance();
		return Command.getInstance();		
	}
	
	public static class Client implements IClient {
		private static Client instance = null;

		public static Client getInstance(){//Registry registry) {
			if (instance == null) instance = new Client();//registry);
			return instance;
		}

		public Client(){//Registry registry) {
			server = (IServer) service.lookup();
		}
		IServer server;
		public String execute(String command, String args, String[] text, int splitSize){
			try{
				return server.executeRMI(command, args, text,  splitSize);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}			
		}

		public String execute(String command, String args, String[] text){
			try{
				return server.executeRMI(command, args, text);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}			
		}

		public String execute(String[] cmdarray, String[] envp){
			try{
				return server.executeRMI(cmdarray, envp);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}		
		}

		public String execute(String command){
			try{
				return server.executeRMI(command);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}			
		}

		public String execute(String[] cmdarray){
			try{
				return server.executeRMI(cmdarray);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}			
		}

		public String execute(String[] command, String stdin){
			try{
				return server.executeRMI(command, stdin);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}
			
		}

		public String execute(String[] command, String stdin, String encoding){
			try{
				return server.executeRMI(command, stdin, encoding);
			} catch ( Exception e ) {
				e.printStackTrace();
				return null;
			}			
		}

	}

	public interface IServer extends Remote {
		public String executeRMI(String command, String args, String[] text, int splitSize)
				throws RemoteException;

		public String executeRMI(String command, String args, String[] text) throws RemoteException;

		public String executeRMI(String[] cmdarray, String[] envp) throws RemoteException;

		public String executeRMI(String command) throws RemoteException;

		public String executeRMI(String[] cmdarray) throws RemoteException;

		public String executeRMI(String[] command, String stdin) throws RemoteException;

		public String executeRMI(String[] command, String stdin, String encoding) throws RemoteException;
	}

	public interface IClient {
		public String execute(String command, String args, String[] text, int splitSize);

		public String execute(String command, String args, String[] text);

		public String execute(String[] cmdarray, String[] envp);

		public String execute(String command);

		public String execute(String[] cmdarray);

		public String execute(String[] command, String stdin);

		public String execute(String[] command, String stdin, String encoding);
	}
	public static void startService( int port ) {
		service.startService(port, Command.getInstance());
	}
}
