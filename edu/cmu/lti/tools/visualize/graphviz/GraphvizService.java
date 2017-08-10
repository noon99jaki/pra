/**
 * 
 */
package edu.cmu.lti.tools.visualize.graphviz;

import java.rmi.Remote;
import java.rmi.RemoteException;

import edu.cmu.lti.tools.visualize.graphviz.Tags.EOutFormat;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.net.RMIService;

/**
 * Serverize graphviz
 * @author nlao
 *
 */
public class GraphvizService {
	static RMIService service= new RMIService(GraphvizService.class);
	public static IClient getInstance( ){
		return getInstance(service.p.serverMode);
	}
	public static IClient getInstance(boolean serverMode ){		
		if (serverMode)
			return Client.getInstance();
		return Graphviz.getInstance();		
	}

	public interface IClient {
		public void dot(String graph,boolean bStraightLine, EOutFormat outFormat, String outFile);
		public void neato(String graph,boolean bStraightLine, EOutFormat outFormat, String outFile);
	}
	public interface IServer extends Remote {				
		public byte[] dotRMI(String graph,boolean bStraightLine, EOutFormat outFormat)throws RemoteException;
		public byte[] neatoRMI(String graph,boolean bStraightLine, EOutFormat outFormat)throws RemoteException;
	}
	
	public static class Client implements 	IClient{		
		private static  Client instance =null;
		public static Client getInstance() {
			if (instance==null) 	 instance = new Client();			
			return instance;
		}
		public Client(){
			server = (IServer) service.lookup();		
		}
		IServer server;
		
		public void dot(String graph,boolean bStraightLine, EOutFormat outFormat, String outFile){
			try{
				byte[] vb=server.dotRMI(graph,bStraightLine, outFormat);
				FFile.saveByte(vb, outFile);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}
		public void neato(String graph,boolean bStraightLine, EOutFormat outFormat, String outFile){
			try{
				byte[] vb=server.neatoRMI(graph,bStraightLine, outFormat);
				FFile.saveByte(vb, outFile);
			} catch (Exception e) {
				e.printStackTrace();
			}	
		}		
	}
	
	public static void startService( int port ) {
		service.startService(port, Graphviz.getInstance());
	}	
	
  public static void main(String[] args) {
  	IClient viz = GraphvizService.getInstance();
  	//Graph g =Digraph.simpleGraph() ;  	
  	//viz.dot(g.toString(),true, EOutFormat.gif, "test.gif");//"~/test.gif");
  	viz.dot(FFile.loadString( "subgraph.txt")
  			, true, EOutFormat.gif, "test.gif");//"~/test.gif");
  	return;
  }
}
