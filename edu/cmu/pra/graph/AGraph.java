package edu.cmu.pra.graph;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Collection;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.structure.IndexS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.data.PRA;
import edu.cmu.pra.graph.GraphCreator.Format;
import edu.cmu.pra.graph.TimedGraph.TimedLinks;

//* an entity-relation graph
//* @author nlao
public abstract class AGraph implements IGraph, Serializable {
	private static final long serialVersionUID = 2008042701L;

	public IndexS nodeType_index_ = new IndexS();
	public IndexS edgeType_index_ = new IndexS();
	public IndexS node_index_ = new IndexS();

	public static final String type_name_sep = "$";
	public static final String edge_type_file_ = "edge_types";
	public static final String node_type_file_= "node_types";
	public static final String node_name_file_ = "node_names";
	public static final String node_file_ = "nodes.obj";	//


	
	//public VectorI getEdges(int node, int relation) {		return null;	}
	
	
	public MapID getWeightedEdges(int node, int relation) {
		return null;
	}	
	public TimedLinks getTimedEdges(int node, int relation) {
		return null;
	}
	
	public int getNumEdgeTypes(){
		return this.edgeType_index_.size();
	}
	
	public String getNodeName(int idx) {
		return node_index_.get(idx);
	}
	
  // transform node type names to their integer ids
  public int getNodeType(String type) {
  	return this.nodeType_index_.get(type);
  }
  
  // transform node type ids to their names
  public String getNodeTypeName(int idx) {
  	return this.nodeType_index_.get(idx);
  }
	

	public VectorS getNodeNames(Collection<Integer> vi) {
		return (VectorS) node_index_.list_.sub(vi);
	}
  public int getNumNodes() {
  	return this.node_index_.size();
  }
  
  
	public void loadIndics(String graph_folder) {
		if (edgeType_index_.size() ==0) 
			edgeType_index_.loadTextColumn(graph_folder + edge_type_file_, 0);
		
		if (nodeType_index_.size() == 0)
			nodeType_index_.loadTextColumn(graph_folder + node_type_file_, 0);
	}
	
	public void loadGraph(String graph_folder) {
		if (node_index_.size() != 0) return;
		
		System.out.println(" loadGraph from " + graph_folder);
		StopWatch sw = new StopWatch();
		
		loadIndics(graph_folder);
		if (node_index_.size() ==0)
			node_index_.loadTextColumn(graph_folder + node_name_file_, 0);
		loadNodes(graph_folder);
		
		System.out.println("done loading graph mem=" + FSystem.printMemoryUsageM() + " time=" + sw.getSecI());
		System.out.println(toString());
		
	}
	protected abstract void loadNodes(String graph_folder);
	
  
	public void saveGraph(String graph_folder) {
		System.out.print("saveGraph at " + graph_folder);

		
		FFile.mkdirs(graph_folder);
		edgeType_index_.list_.save(graph_folder + edge_type_file_);
		nodeType_index_.list_.save(graph_folder + node_type_file_);
		node_index_.list_.save(graph_folder + node_name_file_);
		saveNodes(graph_folder + node_file_);
		
		System.out.println("done");

	}
	public abstract void saveNodes(String node_file);


	
	@Override public String[] getOrderedEdgeLabels() {
		return this.edgeType_index_.list_.toArray();
	}

	@Override public int getEdgeType(String name) {
		int id = edgeType_index_.get(name);
		FSystem.checkTrue(id!=-1, "unknown edge type=" +name);
		return id;
	}

	@Override public String getEdgeTypeName(int idx) {
		return this.edgeType_index_.get(idx);
	}

	// TODO: should we use tryGet here?
	@Override public int getNodeId(String name) {
		int id = node_index_.tryGet(name);
		if (id == -1) System.err.println("Unknown node name=" + name);
		return id;
	}
	
	//	 * There are two types of virtual relations
	//	 * Macro: transfer a distribution on the graph to a distribution
	//	 * 				Can be loaded by the initialize() function
	//	 * Micro: transfer a node in the graph to a distribution
	//	 * 				Can be loaded by the initialize() function
	//	 * 				Can also be loaded from *.db files 
	//	 * 					assuming the format Rel(ent,distribution) 
	//	 * @author nlao	 *
	public static interface VirtualMacroRelation {
		abstract public MapID process(MapID dist);

		abstract public void initialize(Graph g, int r, String fn);
	}

	public interface VirtualMicroRelation {
		abstract public MapID getOutlinks(int iEnt);//Entity e);//

		abstract public void initialize(Graph g, int rel, String fn);

		abstract public boolean load(Format form, String e1, String dist);
	}

	public String toString() {
		return String.format("|NodeTypes|=%d |EdgeTypes|=%d |nodes|=%d ", 
				nodeType_index_.size(), edgeType_index_.size(), node_index_.size());
	}

	protected abstract int addNode();
	//* Modify the graph by adding one link to it
	//* possibly creating one or two entities if they are not in the graph yet  
	public int tryAddNode(int type, String name) {
		String typed_name = nodeType_index_.get(type) + type_name_sep + name;
		return tryAddNode(typed_name);
	}
	
	public VectorI node_types_= new VectorI();
	
	public int tryAddNode(String typed_name) {
		
		int id = node_index_.tryGet(typed_name);
		if (id !=-1) return id;
		
		id = node_index_.add(typed_name);
		
		int type = this.nodeType_index_.add(getNodeTypeName(typed_name));
		node_types_.add(type);
		FSystem.checkTrue(addNode()== id);
		return id;
	}	
	
	public static String getNodeTypeName(String typed_name) {
		int p = typed_name.indexOf(AGraph.type_name_sep);
		if (p == -1) return "";
		return typed_name.substring(0, p);
	}
	public static String getRawNodeName(String typed_name) {
		int p = typed_name.indexOf(AGraph.type_name_sep);
		if (p == -1) return typed_name;
		return typed_name.substring(p+1);
	}
	public abstract void addEdge(int rel, int e1, int e2) ;
	
	
	public void addEdge(String rel, String node1, String node2) {
		rel = rel.replaceAll("[~,;]", "_");
		int r = this.edgeType_index_.add(rel);
		int n1 = this.tryAddNode(node1);
		int n2 = this.tryAddNode(node2);
		this.addEdge(r, n1, n2);
	}

	public boolean save2FOILFormatA(String file_name, String relations) {

		SetI Rs = this.edgeType_index_.getSet(relations.split(","));
		
		BufferedWriter writer = FFile.newWriter(file_name);
		SetS known_names= new SetS();
		FFile.write(writer, "c: ");	// concept
		for (String name: node_index_.list_) {
			String norm = PRA.normalizeName(name);
			if (known_names.contains(norm)) continue;
			known_names.add(norm);
			FFile.writeln(writer, norm+ ",");	// concept
		}
		FFile.writeln(writer,  "[].\n");	//the end
		
		//MapSVecS nodes = 
		
		VectorX<String> vL = edgeType_index_.list_;
		for (int r:Rs){//=0; r < this.edgeType_index_.size(); ++r) {
			FFile.writeln(writer, "*" + this.getEdgeTypeName(r)+"(c,c)");
			
			for (int n=0; n<this.getNumNodes(); ++n){
				VectorI outlinks= this.getEdges(n, r);
				if (outlinks==null) continue;
				
				String name0= PRA.normalizeName(getNodeName(n));
				for (int n1: outlinks)
					FFile.writeln(writer, name0+","+
						PRA.normalizeName(getNodeName(n1)));
			}
			FFile.writeln(writer, ".");
		}
		FFile.close(writer);	
		return true;
	}
	public boolean save2FOILFormat(String file_name) {
		return save2FOILFormat(file_name, null);
	}
	public boolean save2FOILFormat(String file_name, SetS skip_relations) {

		BufferedWriter writer = FFile.newWriter(file_name+".nodes");
		SetS known_names= new SetS();
		FFile.write(writer, "#c: ");	// concept
		for (String name: node_index_.list_) {
			String norm = PRA.normalizeName(name);
			if (known_names.contains(norm)) continue;
			known_names.add(norm);
			FFile.writeln(writer, norm+ ",");	// concept
		}
		FFile.writeln(writer,  "[].\n");	//the end
		FFile.close(writer);	
		
		writer = FFile.newWriter(file_name+".edges");
		
		VectorX<String> vL = edgeType_index_.list_;
		for (int r=0; r < this.edgeType_index_.size(); ++r) {
			String relation = getEdgeTypeName(r);
			if (skip_relations!=null)
				if (skip_relations.contains(relation)) 
					continue;
			
			FFile.writeln(writer, "*" + relation+"(c,c)");
			
			for (int n=0; n<this.getNumNodes(); ++n){
				VectorI outlinks= this.getEdges(n, r);
				if (outlinks==null) continue;
				
				String name0= PRA.normalizeName(getNodeName(n));
				for (int n1: outlinks)
					FFile.writeln(writer, name0+","+
						PRA.normalizeName(getNodeName(n1)));
			}
			FFile.writeln(writer, ".");
		}
		FFile.close(writer);	
		return true;
	}
	
	public boolean save2FOILFormatB(String file_name, String node_type) {

		BufferedWriter writer = FFile.newWriter(file_name);
		SetS known_names= new SetS();
		FFile.write(writer, "#c: ");	// concept
		for (String name: node_index_.list_) {
			String norm = PRA.normalizeName(name);
			if (known_names.contains(norm)) continue;
			known_names.add(norm);
			FFile.writeln(writer, norm+ ",");	// concept
		}
		FFile.writeln(writer,  "[].\n");	//the end
		
		//MapSVecS nodes = 
		
		VectorX<String> vL = edgeType_index_.list_;
		for (int r=0; r < this.edgeType_index_.size(); ++r) {
			FFile.writeln(writer, "*" + this.getEdgeTypeName(r)+"(c,c)");
			
			for (int n=0; n<this.getNumNodes(); ++n){
				VectorI outlinks= this.getEdges(n, r);
				if (outlinks==null) continue;
				
				String name0= PRA.normalizeName(getNodeName(n));
				for (int n1: outlinks)
					FFile.writeln(writer, name0+","+
						PRA.normalizeName(getNodeName(n1)));
			}
			FFile.writeln(writer, ".");
		}
		FFile.close(writer);	
		return true;
	}
	public void creatFOILQueries(String folder) {
		FFile.mkdirs(folder);
		
		for (int r=0; r < this.edgeType_index_.size(); ++r) {
			String R=getEdgeTypeName(r);
			BufferedWriter writer = FFile.newWriter(folder + R);
			FFile.writeln(writer, R);
			
			for (int n=0; n<this.getNumNodes(); ++n){
				VectorI outlinks= this.getEdges(n, r);
				if (outlinks==null) continue;
				
				String name0= this.getNodeName(n);
				for (int n1: outlinks)
					FFile.writeln(writer, name0+","+this.getNodeName(n1));
			}
			FFile.writeln(writer, ".");
		}
	}
}
