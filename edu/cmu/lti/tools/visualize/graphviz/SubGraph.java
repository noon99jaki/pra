package edu.cmu.lti.tools.visualize.graphviz;


public class SubGraph  extends Graph{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	
	public String graphName=null; //identifier 
	public String graphLabel=null;  //showed on the picture
	
	
	public SubGraph(String graphName, String graphLabel, String sIDPrefix){
		this.graphName = graphName;
		this.graphLabel = graphLabel;
		this.sIDPrefix = sIDPrefix;		
	}
	public SubGraph(String graphName, String graphLabel ){
		this(graphName,graphLabel,graphName);
	}
	public SubGraph(String graphName ){
		this(graphName,graphName);
	}
	
	public String toString(){
		return toString("");
	}
	public String toString(String sIndent){
		//update();
		StringBuffer bf = new StringBuffer();
		bf.append(sIndent	+"subgraph cluster_"+graphName+" {\n");
		bf.append(sIndent+"  label=\""+ graphLabel+"\";\n");
		bf.append(super.toString(sIndent));
		bf.append(sIndent+"}\n");
		return bf.toString();
	}	
}
