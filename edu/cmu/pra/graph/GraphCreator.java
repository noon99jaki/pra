package edu.cmu.pra.graph;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapIMapIVecI;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.MapVecII;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Learner;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FClass;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.graph.AGraph.VirtualMacroRelation;
import edu.cmu.pra.graph.AGraph.VirtualMicroRelation;
import edu.cmu.pra.graph.TimedGraph.TimedLinks;
import edu.cmu.pra.graph.TimedGraph.TimedNode;

public class GraphCreator {

	
	public MapSX<Format> relation_format_ = new MapSX<Format>(Format.class);
	private AGraph agraph_;
	private Graph graph_;
	private TimedGraph tgraph_;

	private SetS skiped_relations_ = new SetS();

	public boolean add_twins_=true;
	

	public void indexTimedLinks(){
		for (int i=0; i<temp_nodes_.size(); ++i) {
			TempNode tn = temp_nodes_.get(i);
			TimedNode node = tgraph_.nodes_.get(i);
			node.time_ = tn.time_;
			
			for (Map.Entry<Integer, MapVecII> e: tn.relation_time_entities_.entrySet())
				node.relation_outlinks_.put(e.getKey(), new TimedLinks(e.getValue()));
		}
	}

	public VectorI times_;

	public int time_node_type_ = -1; // The type of entity that represent time 
	public int time_edge_type_ = -1;
	

	public GraphCreator() {//Graph graph) {
		//this.graph_ = graph;

	}


	//private static final Pattern pattern = Pattern.compile("(.*)\\((.*)\\).*");
	public void parseSchemaLine(String line) {
		VectorI node_type_ids = new VectorI();
		boolean is_arg_relation = false;

		// treating one argument as	relation (dynamic relation)
		String[] vsLine = line.split("\\|");

		final Pattern pattern = Pattern.compile("(.*)\\((.*)\\).*");
		Matcher matcher = pattern.matcher(vsLine[0]);
		if (!matcher.matches()) {// advisedBy(person, person)
			System.out.print("bad formated schema: " + line);
			return;
		}
		String predicate = matcher.group(1);// attribute
		String node_types = matcher.group(2);// entity types

		int edge_type = agraph_.edgeType_index_.add(predicate);
		int edge_twin_type = agraph_.edgeType_index_.add("_" + predicate);
		
		node_type_ids.clear();
		for (String node_type : node_types.split(", *")) {
			if (node_type.equals("*R")) {// R(animal,*A)
				node_type_ids.add(null);
				is_arg_relation = true;
			} else {
				int id = agraph_.nodeType_index_.add(node_type);
				node_type_ids.add(id);
			}
		}
		int nt_1 = node_type_ids.get(0);
		int nt_2 = node_type_ids.get(1);

		Format form = new Format(edge_type, edge_twin_type, nt_1, nt_2, line);

		VirtualMacroRelation macroRel = null;
		VirtualMicroRelation microRel = null;

		for (int i = 1; i < vsLine.length; ++i) {
			String tag = vsLine[i];
			//if (tag.equals("NoBack"))		form.bNoBack=true;

			//if (tag.equals("+1"))			
			if (tag.startsWith("+")) {
				form.timeShift = Integer.parseInt(tag.substring(1));
			} else if (tag.startsWith("-")) {
				form.timeShift = Integer.parseInt(tag);
			} else if (tag.equals("NoBF")) form.bNoBF = true;
			else if (tag.equals("NoFB")) form.bNoFB = true;
			//else if (vsLine[i].equals("NoRepeat"))form.bNoRepeat=true;
			else if (tag.equals("NoBB")) form.bNoBB = true;
			else if (tag.equals("NoFF")) form.bNoFF = true;
			else if (tag.equals("NoDir")) form.bNoDir = true;
			else if (tag.equals("NoTwin")) form.bNoTwin = true;
			else if (tag.equals("NoPath")) form.bNoPath = true;
			else if (tag.equals("TT0")) form.bTT0 = true;
			else if (tag.equals("TT1")) form.bTT1 = true;
			else if (tag.equals("SP1")) form.bSP1 = true;
			else if (tag.equals("SP2")) form.bSP2 = true;
			else if (tag.startsWith("MicroRel:")) {
				String name = tag.substring(9);
				microRel = (VirtualMicroRelation) FClass.newInstance(name);
			} else if (tag.startsWith("MacroRel:")) {
				String name = tag.substring(9);
				macroRel = (VirtualMacroRelation) FClass.newInstance(name);
			} else FSystem.die("unknown tag=" + tag);

		}

		relation_format_.put(predicate, form);
		return;
	}


	public static class RelationAttributes {
		public boolean bNoFB = false;//block the path walking forward backward
		public boolean bNoFF = false;
		public int timeShift = 0;

		public boolean bTT0 = false;
		public boolean bTT1 = false;
		public boolean bNoPath = false;

		public boolean bSP1 = false;
		public boolean bSP2 = false;
	}

	public static class Format extends RelationAttributes {
		public int type1_, type2_;
		public String txt_;

		public boolean bNoDir = false;
		public boolean bNoFB = false;
		public boolean bNoBF = false;
		public boolean bNoFF = false;
		public boolean bNoBB = false;
		public boolean bNoTwin = false;
		public int timeShift = 0;
		public int relation_ = -1;
		public int relation_twin_ = -1;

		public Format(int relation, int twin_relation, 
				int type1, int type2, String txt) {//
			this.relation_ = relation;
			this.relation_twin_ = twin_relation;
			this.type1_ = type1;
			this.type2_ = type2;
			this.txt_ = txt;
		}
	}
	
	public static class TempNode {
		////this version is only used in preparation stage
		public MapIMapIVecI relation_time_entities_ 		= new MapIMapIVecI();
		public int time_ = -1;
		
	}
	
	public VectorX<TempNode> temp_nodes_ 	= new VectorX<TempNode>(TempNode.class);

	private void copyTime(TempNode from, TempNode to) {
		if (from.time_ != -1) 
			if (to.time_ == -1 || from.time_ < to.time_) 
				to.time_ = from.time_;
	}

	public void addEdge(Format form, int i1, int i2) {	//int rel
		
		if (!timed_graph) {
			agraph_.addEdge(form.relation_, i1, i2);
			if (add_twins_) agraph_.addEdge(form.relation_twin_, i2, i1);
		} 
		else {
	//		while (temp_nodes_.size() < tgraph_.getNumNodes())
	//			temp_nodes_.add(new TempNode());
			
			//Format form = formats_.get(rel);
			TempNode e1 = temp_nodes_.get(i1);
			TempNode e2 = temp_nodes_.get(i2);
			
	  	if (form.bTT0)copyTime(e2,e1);
	  	if (form.bTT1)copyTime(e1,e2);
	  	
	
	  	int time =Math.max(e1.time_, e2.time_);
	  	if (form.timeShift!=0) 		time +=form.timeShift;
	  	
	  	if (form.bSP2)		time=e2.time_+1;
	  	if (form.bSP1) 		time=e2.time_;
	
			e1.relation_time_entities_.getC(form.relation_, time).add(i2);
			if (add_twins_)
			if (form.relation_twin_ != -1)
			  e2.relation_time_entities_.getC(form.relation_twin_, time).add(i1);
		}		
	}
	
	public int addNode(int type, String name){
		int id = agraph_.tryAddNode(type, name);
		
		if (this.timed_graph) {
			if (temp_nodes_.size() < tgraph_.getNumNodes()) {
				TempNode n = new TempNode();
				temp_nodes_.add(n);
				if (type == this.time_node_type_) {
					//String raw_name = AGraph.getRawNodeName(name);
					try{
						n.time_ = Integer.parseInt(name);
					}
					catch(Exception e) {
						
					}
				}
			}
		}
		return id;
	}
	public boolean parseDBLine(String line, boolean add_node) {//, TRMRF net){

		int iLeftBra = line.indexOf('(');
		int iComma = line.indexOf(',');
		if (iLeftBra == -1 || iComma == -1) {
			System.out.println("bad formated line: " + line);
			return false;
		}
		String relation = line.substring(0, iLeftBra);
		String slots = line.substring(iLeftBra + 1, line.length() - 1);

		Format form = relation_format_.get(relation);
		if (form == null) {
			if (!skiped_relations_.contains(relation)) {
				System.out.println("skip relation not in the schema: " + relation);
				skiped_relations_.add(relation);
			}
			return false;
		}

		String[] vsSlot = slots.split(", *");
		if (vsSlot.length < 2) {
			System.err.println("bad db line: " + line);
			return false;
		}

		if (vsSlot[0].length() == 0) return false;

		int e1 = addNode(form.type1_, vsSlot[0]);

		for (String sE2 : vsSlot[1].split(" ")) {
			if (sE2.length() == 0) continue;
			int e2 = addNode(form.type2_, sE2);
			addEdge(form, e1, e2);
			
		}
		return true;
	}


	// index *.db files in the folder data_folder into a graph
	// assume the existence of a schema file in this folder
	public void indexDBFiles(String graph_folder) {
		System.out.println("index graph at " + graph_folder);
		if (!graph_folder.endsWith("/")) graph_folder += "/";
		
		FFile.checkExist(graph_folder + "schema");
			
		for (String line : FFile.enuLines(graph_folder + "schema")) {
			if (line.startsWith("#")) break;
			parseSchemaLine(line);
		}

		if (this.timed_graph) {
			String time_type = Param.ms.getD("time_type", null);
			if ( time_type !=null) {
				time_node_type_ = tgraph_.nodeType_index_.add(time_type);
				System.out.println("set time_type=" + time_type);
			}
			
			String time_relation = Param.ms.getD("time_relation",  null);
			if ( time_relation !=null) {
				time_edge_type_ = tgraph_.edgeType_index_.add(time_relation);
				System.out.println("set time_relation=" + time_relation);
			}
			
		}
		for (String line : FFile.enuLines(graph_folder + ".*\\.db"))
			parseDBLine(line, true);
		
//		if (graph_.timed_graph_) for (TypedNode e : graph_.nodes_)
//			e.indexTimedLinks();

	}

	
	public void indexEdgeFiles(String graph_folder) {
		System.out.println("index graph at " + graph_folder);
		if (!graph_folder.endsWith("/")) graph_folder += "/";
		
		for (VectorS line : FFile.enuRows(graph_folder + ".*edges")) {//".*\\.edges"
			if (line.size() != 3) {
				System.err.println("bad line=" + line.join(" "));//FFile.line_);
				continue;
			}
			agraph_.addEdge(line.get(0), line.get(1), line.get(2));
		}
	}
	
	public void indexGhirlFiles(String graph_folder) {
		System.out.println("index graph at " + graph_folder);
		
		for (VectorS line : FFile.enuRows(graph_folder + ".*\\.graph", " ")) {
			if (line.size() != 4) {
				System.err.println("bad line=" + line.join(" "));//FFile.line_);
				continue;
			}
			if (!line.get(0).equals("edge")) continue;
			
			agraph_.addEdge(line.get(1), line.get(2), line.get(3));
		}
	}
	
	
	public void indexGraph(String graph_folder, String format){
		if (!graph_folder.endsWith("/")) graph_folder += "/";
		
		if (timed_graph) {
			tgraph_ = new TimedGraph();
			agraph_ = tgraph_;
		}
		else {
			graph_ = new Graph();
			agraph_ = graph_;
		}
		
		StopWatch stop_watch = new StopWatch();
		
		if (format.equals("db")) indexDBFiles(graph_folder);
		else if (format.equals("edges")) indexEdgeFiles(graph_folder);
		else if (format.equals("ghirl")) indexGhirlFiles(graph_folder);
		else FSystem.die("data format can only be ghirl, edges or db");

		if (timed_graph) {
			this.indexTimedLinks();
			tgraph_.dumpNodeCountsByTime(	graph_folder +	"countNodeByTime");
			tgraph_.dumpLinkCountsByTime(graph_folder + "countLinkByTime");
		} else {
			graph_.edgeDedupe();
		}
		
		agraph_.saveGraph( graph_folder);
		Learner.println(agraph_.toString());
		Learner.println(FSystem.memoryUsage());
		
		stop_watch.printTime("indexGraph");
	}

	public static boolean timed_graph=false;
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		
		String task = args[0];
		if (task.equals("indexGraph")) timed_graph=false;
		else if (task.equals("indexTimedGraph")) timed_graph=true;
		else FSystem.die("unknown task=" + task);

		GraphCreator graph_creator = new GraphCreator();
		graph_creator.indexGraph(args[1], args[2]);

	}
}
