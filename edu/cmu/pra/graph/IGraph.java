package edu.cmu.pra.graph;

import java.util.Set;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.pra.graph.TimedGraph.TimedLinks;

public interface IGraph {

	// wrap an integer in an object 
	// so that it can be modified through function arguments
	class WrapInt{ 
		public WrapInt(){ value = 0;} 
		public int value;	
	}
	
	public VectorI getEdges(int node, int relation);
	// this interface is for timed graph
	public TimedLinks getTimedEdges(int iEnt, int iRel);//, int time);
	// this interface is for graph with weighted edges 
	public MapID getWeightedEdges(int node, int relation);

	// transform (typed) node names to their integer ids
	public int getNodeId(String typed_name);

	// transform node interger ids to their names
  public String getNodeName(int idx);
  
	// total number of nodes
  public int getNumNodes();
  
	// total number of edgeTypes
  public int getNumEdgeTypes();
 
  // transform edge type names to their integer ids
  public int getEdgeType(String type);
  // transform edge type ids to their names
  public String getEdgeTypeName(int idx);
  
  // transform node type names to their integer ids
  public int getNodeType(String type);
  // transform node type ids to their names
  public String getNodeTypeName(int idx);
  
  // Get the set of all edge types
  public String[] getOrderedEdgeLabels();
  // Get the set of edge types coming out of a node
  public Set<Integer> getNodeOutlinkTypes(int node);

}
