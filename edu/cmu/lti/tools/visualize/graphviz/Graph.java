/**
 * 
 */
package edu.cmu.lti.tools.visualize.graphviz;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.VectorX;

/** * 
 * Data structure to help you describe graph structurally
 * instead of procedurally. Instead of learn GraphViz language
 * you just need to call functions here. 
 * @author nlao
 *
 */

public abstract class Graph implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD


	public VectorX<Node> vNode = new VectorX<Node>(Node.class);
	public VectorX<Link> vLink = new VectorX<Link>(Link.class);
	public VectorX<SubGraph> vSubGraph = new VectorX<SubGraph>(SubGraph.class);
/*	//public String attributes="";  
	public String graphType=null;  
	public String graphName=null;  
	//public String graphName=null;  
	protected Graph(String graphType, String graphName){
		this.graphType=graphType;
		this.graphName=graphName;
	}*/
	/*public Graph(String attributes){
		this.attributes = attributes;
	}*/
	public String toString(String sIndent){
		//update();
		sIndent = sIndent+"  ";
		StringBuffer bf = new StringBuffer();
		//bf.append(graphType+" "+graphName+" {\n");//digraph G
		//if (vSubGraph.size()>0) bf.append(vSubGraph.join("\n")+"\n");
		for (SubGraph sb: vSubGraph)
			bf.append(sb.toString(sIndent));		
		if (vNode.size()>0)	bf.append(vNode.joinIndented(sIndent));//join("\n")+"\n");
		if (vLink.size()>0) bf.append(vLink.joinIndented(sIndent));//.join("\n")+"\n");
		//bf.append("\n}\n");
		return bf.toString();
	}	
	public String toString(){
		return toString("");
	}
	public Node addNode(String label){
		return addNode(vNode.size(), label);
	}
	public String sIDPrefix="";
	//public int nIndent=0;
	public Node addNode(int id, String label){
		Node n= new Node(sIDPrefix+"_"+id, label);
		n.id = id;//vNode.size();
		vNode.add(n);
		return vNode.lastElement();
	}
	public Link addLink(int ifrom, int ito){
		vLink.add(new Link(sIDPrefix+"_"+ifrom, sIDPrefix+"_"+ito)); 
		return vLink.lastElement();
	}	
	void update(){
	/*	for (int i=0; i<vNode.size();++i)
			vNode.get(i).id = i;
		for (Link l :vLink){
			//l.from = 
		}*/
	}

  
}	
