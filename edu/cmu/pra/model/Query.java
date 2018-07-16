package edu.cmu.pra.model;

import java.io.Serializable;
import java.util.Collection;

import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.container.VecSetI;
import edu.cmu.pra.CTag;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphWalker;

public class Query implements IGetIntByStr, Serializable, IGetStrByStr {
	private static final long serialVersionUID = 2008042701L;
	public String name_ = null;
	public int time_ = -1;
	//public boolean reach_target_;

	/*
	When we try to predict the existence of an edge in graph, we need to first remove that edge from the graph.
	Otherwise it is cheating. 

	blocked_node_ helps to identify such edges and their reversed edges. For example, to block the following edges
		PersonGender(c$bob, c$male)
		GenderPerson(c$male, c$bob)
	we specify
		blocked_node_=c$bob which comes from the first field (blocked_field=0) in query
		target_relation=PersonGender
	*/
	public int blocked_node_=-1;	// the node that leasds to the answer
	public MapID result=null;
	
	//the list of seed nodes in a query
	public VecSetI seeds_ = new VecSetI();
	public SetI good_ = new SetI(); //the set of relevant entities
	public SetI bad_ = new SetI(); //the set of irrelevant entities
	public SetI selected_ = null; //the set of entities in objective function
	public SetI labeled_bad_ = null;//the set of labeled irrelevant entities
	
	public SetI hit_= null;//new SetI();

	public VecMapID features_ = new VecMapID();
	public VecMapID features_sampled_ = new VecMapID();

	
	
	// features are cleared before each training
	public void clearFeatures() {
		features_.clear();
		features_sampled_.clear();
		//clearStatistics();
	}
	public void clear(int num_fields) {
		// When clearing a query, we should be clearing
		// The seeds as well; however, this appears to increase the running time, and
		// test and train tasks are already handling the creations of queries correctly.
		// Predict appears to be resolved by just instantiating a new query for each line. 
		// leaving this line commented out for now.
		//seeds_.clear();
		seeds_.reset(num_fields);
		good_.clear();
		clearFeatures();
	}

	public Query() {
//		seeds_ = new VecSetI();
//		good_ = new SetI();
	}

	public Query(VecSetI seeds, SetI good) {
		this.seeds_ = seeds;
		this.good_ = good;
		//this.time_ = time;
	}

	@Override public String getString(String tag) {
		//if (tag.equals(CTag.details)) return printEvaDetail();
		//if (tag.equals(CTag.inspect)) return inspectData();

		return null;
	}


	@Override public Integer getInt(String tag) {
		if (tag.equals(CTag.nRel)) return good_.size();
		if (tag.equals(CTag.seed))seeds_.firstElement();
		return null;
	}

	public String print(GraphWalker walker) {
		StringBuffer sb = new StringBuffer();
//		sb.append("time=" + time_ + "\n");
		
		sb.append("\nseeds=");
		for (SetI seeds : seeds_)
			sb.append("[" + walker.getNodeNames(seeds, true).join(" ") + "]");
		sb.append(" good=[" + walker.getNodeNames(good_, true).join(" ") + "]");
		if (bad_ != null)		//sb.append(" |bad|=" + bad_.size());
			sb.append(" bad=[" + walker.getNodeNames(bad_, true).join(" ") + "]");
		//sb.append("\n");
		return sb.toString();
	}
	
	public static String printTitle() {
		return "name\ttime\t#active_features\t#good\t#bad";		//\t#and\t$not
	}
	
	public String toString() {
		return String.format("%s\t%d\t%d\t%d\t%d",		//\t%d\t%d
				name_, time_, 
				features_.getNumNoneEmptyElements() , 
				good_.size(),
				(bad_ != null) ?bad_.size() : -1
		);
	}
	
	//		sample
	//	#!query: {MSG$msg19508}
	//	#!filter: isa=$MSG
	//	#!graph: ceas_threads_all
	//	MSG$msg19729
	public String saveInGhirlFormat(String dbName, Graph g) {
		return null;
		//		BufferedWriter bw = FFile.newWriter(fn);
		//		//FFile.write(bw, "#!query: {"+dist0.toGhirlFormat()+"}\n");
		//		//FFile.write(bw, "#!filter: isa=$"+etTarget.nameS+"\n");
		//		FFile.write(bw, "#!graph: " + dbName + "\n");
		//		for (int id : good_)
		//			FFile.write(bw, g.vEnt.get(id).printGhirlName() + "\n");
		//
		//		FFile.close(bw);
		//		return true;
	}
	
	public static String printQueryStatistics(Collection<Query>queries ) {

		// Number of queries for which target can be reached
		double num_target_reached_ = 0;
		double num_hit_ = 0;

		// Number of bad pairs included into the objective
		double num_bad_ = 0;

		// Number of good pairs included into the objective
		double num_good_ = 0;
		for (Query query : queries) {
			if (query.hit_.size()!=0) ++num_target_reached_;
			num_bad_ += query.bad_.size();
			num_good_ += query.good_.size();
			num_hit_ += query.hit_.size();
		}

		num_target_reached_ /= queries.size();
		num_bad_ /= queries.size();
		num_good_ /= queries.size();
		num_hit_ /= queries.size();
		return String.format("%f\t%f\t%f\t%f", 
				num_target_reached_, num_hit_, num_good_,		num_bad_);
	}
	
	
	public static String getFeatureStatTitle(){
		return "#Q\ttRW\tmRW\tRcTg\t#hit\t#good\t#bad";
	}

//	public void parseQuery(String line) {
//		parseQuery(FString.splitVS(line, "\t"));
//	}
//
//	public boolean parseQuery(VectorS fields) {
//		clear(p.num_fields);
//		name_ = p.dual_mode? fields.firstElement() + "," + fields.lastElement() : 
//			fields.firstElement();
//
//		if (!p.dual_mode) {
//			FSystem.checkTrue(fields.size() == p.num_fields + 1, 
//					"expected number of query fields=" + (p.num_fields + 1));
//			
//			walker_.getNodeIds(	fields.get(p.num_fields).split(" "), query.good_);
//		}
//
//		for (int i = 0; i < p.num_fields; ++i) {
//			walker_.getNodeIds(fields.get(i).split(" "), query.seeds_.get(i));
//			if (query.seeds_.get(i).size()==0){
//				System.err.println("missing field " + i + " in query="+fields.join(", "));
//				return false;
//			}	
//		}
//
//		if (walker_.p.blocked_field !=-1) {
//			if (query.seeds_.get(walker_.p.blocked_field).size() !=1)
//				FSystem.die("expect exactly one seed for blocked field="
//						+ fields.get(walker_.p.blocked_field));
//			
//			if (walker_.p.target_relation !=null)
//				query.blocked_node_ = query.seeds_.get(walker_.p.blocked_field).first();
//		}
//		
//		if (p.timed_graph) query.time_ = Integer.parseInt(fields.get(p.time_field));
//		return true;
//	}
}
