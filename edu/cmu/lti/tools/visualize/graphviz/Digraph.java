package edu.cmu.lti.tools.visualize.graphviz;


/**
 * directed graph
 * @author nlao
 * 
 */
public class Digraph extends Graph{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
/*	public Digraph( String graphName){
		super("digraph", graphName);
	}
	public Digraph( ){
		this( "G");
	}*/
	//common properties
	public LabeledObj g = new LabeledObj();//graph
	public LabeledObj n = new LabeledObj();//node
	public LabeledObj e = new LabeledObj();//edge

	//TODO: why we need a counter? to distinguish ids of subgraphs
	public static int counter=0;//counter to generate GUID	
	public Digraph(){
		counter=0;
	}
	public String toString(){
		//update();
		StringBuffer bf = new StringBuffer();
		String sIndent="  ";
		bf.append("digraph G {\n");
		if (g.ms.size()>0)	bf.append(sIndent+"graph "+g.format()+";\n");
		if (e.ms.size()>0)	bf.append(sIndent+"edge "+e.format()+";\n");
		if (n.ms.size()>0)	bf.append(sIndent+"node "+n.format()+";\n");
		bf.append(super.toString("")+"}\n");
		return bf.toString();
	}	
	
	public static Digraph simpleGraph(){
  	Digraph g = new Digraph();
  	g.addNode("AA");
  	g.addNode("BB");
  	g.addLink(0,1); 
  	return g;
	}
  public static void main(String[] args) {

  	System.out.println(simpleGraph());
  	return;
  }
}
