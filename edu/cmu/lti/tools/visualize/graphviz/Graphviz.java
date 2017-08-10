/**
 * 
 */
package edu.cmu.lti.tools.visualize.graphviz;

import java.rmi.RemoteException;

import edu.cmu.lti.tools.visualize.graphviz.GraphvizService.IClient;
import edu.cmu.lti.tools.visualize.graphviz.GraphvizService.IServer;
import edu.cmu.lti.tools.visualize.graphviz.Tags.EOutFormat;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

/**
 * @author nlao
 * http://www.graphviz.org/Documentation.php
 * dotty is a graph editor for the X Window System
 * lefty is a two-view graphics editor for technical pictures.
 * NEATO is a program that makes layouts of undirected graphs 
 * 		following the filter model of DOT.
 * dot draws directed graphs as hierarchies.
 * 
 */
public class Graphviz implements IServer , IClient{
	private static  Graphviz instance =null;
	public static Graphviz getInstance() {
		if (instance==null) 	 instance = new Graphviz();			
		return instance;
	}
	public Graphviz(){		
	}
	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String graphviz_path;
		public String dot;
		public String neato;
		//public String graphviz_path;
		
		public Param(){//Class c) {
			super(Graphviz.class);
			parse();
		}

		public void parse() {
			graphviz_path=getString("graphviz_path");
				//,	"/usr0/javelin/src/graphviz-2.18");
			dot =getString("dot",null);
			if (dot==null)
				dot= graphviz_path+"/cmd/dot/dot";
			//neato = graphviz_path+"/cmd/neato/neato";
			neato=getString("neato");
			//="/usr2/local/graphviz/bin/neato";
		}
	}
	Param p = new Param();
	
	
	public byte[] dotRMI(String graph,boolean bStraightLine
			, EOutFormat outFormat)throws RemoteException{
		String fn = "dotRMI.out";
		dot(graph,bStraightLine,outFormat, fn);
		return FFile.loadByte(fn);
	}	
	public byte[] neatoRMI(String graph,boolean bStraightLine
			, EOutFormat outFormat)throws RemoteException{
		String fn = "neatoRMI.out";
		neato(graph,bStraightLine,outFormat, fn);
		return FFile.loadByte(fn);
	}		
	/**
	 * 
	 * @param graph
	 * @param bStraightLine
	 *   Draw edges using straight lines. Graphviz uses bezier curves to
	 *   draw straight edges. Use this option to force the use of line to
	 *   operations instead of curves.
	 * @param outFormat
	 * @param outFile
	 */
	public void dot(String graph,boolean bStraightLine
			, EOutFormat outFormat, String outFile){
		String fn = outFile+".doc";//"tmp.doc";
		FFile.saveString(fn, graph);
		//String cmd;FSystem.cmd(cmd);
		if (bStraightLine){
			FSystem.cmd(String.format("%s %s -o %s"	, p.dot, fn, "tmp"));
			FSystem.cmd(String.format("%s -n -T%s %s -o %s"	
					, p.neato, outFormat.name(), "tmp", outFile));
			//dot g.dot | neato -n -Tpng > g.png
			//cmd=String.format("%s %s | %s -n -T%s -o %s"
			//, p.dot,fn,  p.neato, outFormat.name(), outFile);
			//FSystem.cmd(cmd);
		}
		else{
			FSystem.cmd(String.format("%s -T%s %s -o %s"
					, p.dot,outFormat.name(), fn, outFile));
				
			//dot/dot -Tgif /usr1/nlao/try.dot -o /usr1/nlao/try.gif
		}
	}
	public void neato(String graph,boolean bStraightLine
			, EOutFormat outFormat, String outFile){
		String fn = outFile+".doc";//"tmp.doc";
		FFile.saveString(fn, graph);
		//String cmd;FSystem.cmd(cmd);
		FSystem.cmd(String.format("%s -T%s %s -o %s"	
				, p.neato, outFormat.name(), fn, outFile));
		//neato -Tpng graph.dot > graph.dot.png
		
		if (bStraightLine){
		}
		else{
		}
	}
  public static void main(String[] args) {
  	Graphviz viz = Graphviz.getInstance();
  	Graph g =Digraph.simpleGraph() ;  	
  	viz.dot(g.toString(), true, EOutFormat.gif, "test.gif");//"~/test.gif");
  	
  	return;
  }
}
