package edu.cmu.pra.graph;

import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapISetI;
import edu.cmu.lti.algorithm.container.MapIVecS;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.TMapIVecX;
import edu.cmu.lti.algorithm.container.VectorB;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.math.rand.FRand;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.data.PRA;
import edu.cmu.pra.graph.IGraph.WrapInt;
import edu.cmu.pra.graph.TimedGraph.TimedLinks;
import edu.cmu.pra.model.PathNode;
import edu.cmu.pra.model.Query;

// Augment graphs with extra functionalities
public class GraphWalker {
	public static final String relation_separator = ",";
	public static final String field_separator = "~";
	public static final String path_separator = ";";
	public static final String reserved_chars = 
	    field_separator + path_separator + relation_separator;
	
	public static boolean silent_=false; 
	
	public void checkReservedCharacters(){
		Pattern pattern = Pattern.compile(reserved_chars);
		
		for (int i = 0; i < graph_.getNumNodes(); ++i) {
			String name = graph_.getNodeName(i);
			if (pattern.matcher(name).find())
				FSystem.die("cannot contain reserved chars="+reserved_chars +" in node name=" + name);
		}
		for (int i = 0; i < graph_.getNumEdgeTypes(); ++i) {
			String name = graph_.getEdgeTypeName(i);
			if (pattern.matcher(name).find())
				FSystem.die("cannot contain reserved chars="+reserved_chars +" in edge type name=" + name);
		}
	}	
	
	public IGraph graph_;
	public Param p;

	// The model is trained to predict this relation
	public int target_relation_ = -1;
	public int target_relation_inverse_ = -1;
	public SetI inferred_relations_ = new SetI(); //null;// 
	public SetI inferred_relations_inverse_ = new SetI();//	null;// 
	
	
	private void loadInferedRelations(){
		for (VectorS row: FFile.enuRows(p.inferred_relation_file, " ")) {
			if (row.size() <=1) continue;
			int rel = graph_.getEdgeType(row.get(0));
			if (rel==-1) continue;
			if (rel==target_relation_) getEdgeTypeIds(row, inferred_relations_);
			if (rel==target_relation_inverse_)  getEdgeTypeIds(row, inferred_relations_inverse_);
		}
	}
	
	public SetI no_cost_relations_ = new SetI();
	public SetI no_repeat_relations_ = new SetI();
	public SetI forbidden_relations_ = new SetI();

	
	public GraphWalker(String graph_folder){
		p = new Param();
		if (p.timed_graph) {
			TimedGraph graph = new TimedGraph();
			graph.loadGraph(graph_folder);
			this.graph_ = graph;
		}
		else {
			Graph graph = new Graph();
			graph.loadGraph(graph_folder);
			this.graph_ = graph;
		}
		init();
	}
	public GraphWalker(IGraph graph) {
		p = new Param();
		graph_ = graph;
		init();
	}
	
	// normalized node name 
	public String getNodeNameN(int idx) {
		return PRA.normalizeName(graph_.getNodeName(idx));
	}
	
	void init(){
		checkReservedCharacters();
		setEdgeTypeInverse();
		detectFunctionalRelations();

		if (p.target_relation != null){
			target_relation_ = graph_.getEdgeType(p.target_relation);
			target_relation_inverse_ = this.getInversedRelation(target_relation_);
			if (p.inferred_relation_file !=null) 	loadInferedRelations();
			inferred_relations_.add(target_relation_);
			inferred_relations_inverse_.add(target_relation_inverse_);
		}
		if (p.forbidden_relations != null) {
			forbidden_relations_ = this.getEdgeTypeIds(p.forbidden_relations, path_separator);
		}

		parsePaths(p.rerank_and_paths, field_rerank_andPaths_);
		parsePaths(p.rerank_not_paths, field_rerank_notPaths_);

		parsePaths(p.and_paths, field_andPaths_);
		parsePaths(p.not_paths, field_notPaths_);
		//		FSystem.checkTrue(field_andPaths_.size()<=1, "assume only one field is related to field_andPaths_");
		//		FSystem.checkTrue(field_notPaths_.size()<=1, "assume only one field is related to field_notPaths_");
		
		if (p.no_cost_relations != null) 
			getEdgeTypeIds(p.no_cost_relations, no_cost_relations_, path_separator);
		
		if (p.no_repeat_relations != null) 
			getEdgeTypeIds(p.no_repeat_relations, no_repeat_relations_, path_separator);
		return;
	}
	
	public void getEdgeTypeIds(String names, SetI ids, String separator) {
		getEdgeTypeIds(FString.splitVS(names, separator), ids);
	}
	public void getEdgeTypeIds(VectorS names, SetI ids) {
		for (String name : names) {
			int relation = graph_.getEdgeType(name);
			//FSystem.checkTrue(relation!=-1, "unknown edge type=" +name);
			if (relation !=-1)	ids.add(relation);
			//else System.err.println("unknown relation=" + name);
		}		
	}
	public VectorS getEdgeTypeNames(VectorI ids ) {
		VectorS names = new VectorS();
		for (int id: ids) names.add(this.graph_.getEdgeTypeName(id));		
		return names;
	}
	
	public SetI getEdgeTypeIds(String names, String separator) {
		SetI ids = new SetI();
		getEdgeTypeIds(names, ids, separator);
		return ids;
	}
	// for training purpose, remove the target relation of the query node
	// for multi-threading purpose this information is kept in Query until the last minutes
	//SetI blocked_nodes_ = null;
	


	
	
	public static enum RandomWalkMode {
		Truncate, //Truncation
		Beem, //Beam
		LVWalker, // Sampling (low variance)
		Walker, // Sampling
		Particle, //particle filtering
		LVParticle, //particle filtering (low variance)
		Exact, //none
	}


	public static class Param extends edu.cmu.lti.util.run.Param {
		public Param() {
			super(Graph.class);
			parse();
		}
		
		public MapIVecS blocked_paths = new MapIVecS();
		public MapIVecS and_paths = new MapIVecS();
		public MapIVecS not_paths = new MapIVecS();

		public MapIVecS rerank_and_paths = new MapIVecS();
		public MapIVecS rerank_not_paths = new MapIVecS();

		public static void parsePaths(String paths, MapIVecS mvsPath) {
			if (paths == null) return;
			if (paths.length() == 0) return;
			for (String path : paths.split(",")) {
				int p = path.indexOf(field_separator);
				if (p == -1) FSystem.die("sepPathField=" + field_separator + " not found");
				int ic = Integer.parseInt(path.substring(0, p));
				mvsPath.getC(ic).add(path.substring(p + 1));
			}
		}

		public static void parsePaths(String paths, VectorS vsPath) {
			if (paths == null) return;
			vsPath.addAll(paths.split(path_separator));
		}
		
		
		public double truncate = 0.001;
		public int dbgTree;

		public boolean RW_renormalize = false;
		public double damping_factor;

		public int num_walkers;
		public RandomWalkMode RW_mode;
		public double min_particle;

		public String forbidden_relations;	
		// accept a ;-separated list relations which do not participate in exploration 

		public int blocked_field = -1;
		public String target_relation;

		public String code_;

		public boolean has_filter;
		
		public String no_cost_relations;
		public String no_repeat_relations;

		public String inferred_relation_file;

		public boolean no_undirected_FF;
		public boolean no_functional_BF;

		public double dampening;

		public int time_field = 0;//column id in scenario file
		public boolean timed_graph;
		public boolean given_negative_samples=false;

		public double functional_threshold;
		public void parse() {
			functional_threshold = getDouble("functional_threshold", 1.5);
			given_negative_samples=getBoolean("given_negative_samples", false);
			//if (given_negative_samples) ++num_fields;

			
			time_field = getInt("time_field", 0);
			timed_graph = getBoolean("timed_graph", false);

			
			damping_factor = getDouble("damping_factor", 1.0);

			inferred_relation_file = getString("inferred_relation_file", null);
			
			no_undirected_FF = getBoolean("no_undirected_FF", false);
			no_functional_BF = getBoolean("no_functional_BF", true);

			String and_paths_str = getString("and_paths", null);
			String not_paths_str = getString("not_paths", null);
			dampening = getDouble("dampening", 1.0);

			parsePaths(getString("blocked_paths", null), blocked_paths);
			parsePaths(and_paths_str, and_paths);
			parsePaths(not_paths_str, not_paths);

			parsePaths(getString("rerank_and_paths", null), rerank_and_paths);
			parsePaths(getString("rerank_not_paths", null), rerank_not_paths);
			
			no_cost_relations = getString("no_cost_relations", null);
			no_repeat_relations = getString("no_repeat_relations", null);

			has_filter = rerank_and_paths.size() > 0 || rerank_not_paths.size() > 0
					|| and_paths.size() > 0 || not_paths.size() > 0;

					
			forbidden_relations = getString("forbidden_relations", null);
			dbgTree = getInt("dbgTree", 0);

			truncate = getDouble("truncate", 0.001);
			num_walkers = getInt("num_walkers", 10000);
			//min_particle = getDouble("min_particle", 0.001);
			min_particle = 1.0/ num_walkers;
			RW_renormalize = getBoolean("RW_renormalize", false);

			damping_factor = getDouble("damping_factor", 1.0);

			RW_mode = RandomWalkMode.valueOf(
					getString("RW_mode", RandomWalkMode.LVParticle.name()));

			blocked_field = getInt("blocked_field", -1);
			target_relation = getString("target_relation", null);
			if (target_relation==null) blocked_field=-1;
			
			code_ = "_" + RW_mode.name();

			switch (RW_mode) {
			case Truncate:
				code_ += String.format("%.0e", truncate);
				break;
			case Beem:
			case Particle:
			case LVParticle:
			case Walker:
			case LVWalker:
				code_ += String.format("%.0e", (double) num_walkers);
				break;
			case Exact:
				break;
			}
			if (no_undirected_FF) code_+="u";
			if (no_functional_BF) code_+="f";
			
			if (damping_factor != 1.0) code_ += String.format("_dp%.1f",
					damping_factor);
			if (RW_renormalize) code_ += "N";
			
			if (and_paths_str != null) code_ += "_and";// + and_paths_str;
			if (not_paths_str != null) code_ += "_not";// + not_paths_str;

			if (no_cost_relations != null) code_ += "_"	+ no_cost_relations;
			if (forbidden_relations != null) code_ += "_"	+ forbidden_relations;
			if (dampening!=1.0) code_ += "_damp"+getString("dampening", null);
			return;
		}
	}

	public void stepCoverage(Query query, int node, int relation, SetI result) {
		WrapInt idx = new WrapInt();
		VectorI targets = 	getEdges(query, node, relation, idx);
		if (targets == null) return;
		
		for (int i = 0; i < idx.value; ++i) result.add(targets.get(i));
	}

	public void plusOn(Query query, int relation, int target, double weight, MapID dist){
		if (query!=null)
		if (target== query.blocked_node_)	
			//if (relation==target_relation_inverse_ )
			if (this.inferred_relations_inverse_.contains(target))
				return;
		dist.plusOn(target, weight);
	}
	
	public void stepForward(Query query, int node, int relation, double p0, MapID dist) {
		WrapInt idx = new WrapInt();
		VectorI targets = 	getEdges(query, node, relation, idx);
		if (targets == null) return;

		double pp = p0 / idx.value;
		if (p.RW_mode.equals(RandomWalkMode.LVParticle)) {
			if (pp <= p.min_particle) {
				int n = (int) Math.ceil(p0 / p.min_particle);
				double w = p0 / n;
				for (int i : FRand.lowVarSample(idx.value, n)) 
					plusOn(query, relation, targets.get(i), w, dist);				
				return;
			}
		} else if (p.RW_mode.equals(RandomWalkMode.Particle)) {
			if (pp <= p.min_particle) {
				int n = (int) Math.ceil(p0 / p.min_particle);
				double w = p0 / n;
				for (int i = 0; i < n; ++i)	
					plusOn(query, relation, targets.sample(0, idx.value), w, dist);				
				return;
			}
		} else if (p.RW_mode.equals(RandomWalkMode.LVWalker)) {
			int n = (int) Math.ceil(p0 / p.min_particle);
			for (int i : FRand.lowVarSample(idx.value, n)) 
				plusOn(query, relation, targets.get(i), p.min_particle, dist);
			return;
		} else if (p.RW_mode.equals(RandomWalkMode.Walker)) {
			int n = (int) Math.ceil(p0 / p.min_particle);
			for (int i = 0; i < n; ++i) 
				plusOn(query, relation, targets.sample(0, idx.value), p.min_particle, dist);			
			return;
		} 
		//dist.plusOn(targets, 0, idx.value, pp);
		
		for (int i = 0; i < idx.value; ++i) 
			plusOn(query, relation, targets.get(i), pp, dist);				
		return;
	}


	public void step(Query query, MapID src_dist, double p0, int relation, boolean reversed, MapID target_dist) {
		FSystem.checkTrue(relation >=0 , "relation id should >= 0");
		p0 *= p.damping_factor;
		//		if (r.relMacro != null) {
		//			target_dist.plusOn(r.relMacro.process(src_dist));
		//			return;
		//		}

		for (Map.Entry<Integer, Double> e : src_dist.entrySet()) {

			//			if (r.relMicro != null) { 
			//				target_dist.plusOn(r.relMicro.getOutlinks(e.getKey()), p0	* e.getValue());
			//			continue; }
			
      if (reversed)
      	stepBackward(query, e.getKey(), relation, p0 * e.getValue(), target_dist);
      else
      	stepForward(query, e.getKey(), relation, p0 * e.getValue(), target_dist);
		}
	}

	public VectorI getEdges(Query query, int node, int relation, 
			WrapInt idx){//, WrapInt excpet) {
		if (query!=null)
		if (node==query.blocked_node_)
			if (inferred_relations_.contains(relation))
			//if (relation == target_relation_ && )	
				return null ;		

		if (p.timed_graph) {
			TimedLinks ols = graph_.getTimedEdges(node, relation);
			if (ols == null) return null;
			idx.value = ols.getIdx(query.time_);			
			return ols.nodes_;
		} else {
			VectorI result = graph_.getEdges(node, relation);
			if (result != null)	idx.value = result.size();
			return result;
		}
		
	}

	public static MapISetI missing_edges = new MapISetI();
	public void stepBackward(Query query, int node, int relation, double p0, MapID dist) {
		
		WrapInt idx = new WrapInt();
		VectorI targets = 	getEdges(query, node, relation, idx);
		if (targets == null) return;

		int _relation = relation_inverse_.get(relation);

		WrapInt _idx = new WrapInt();
		for (int i : FRand.lowVarSample(idx.value, p.num_walkers)) {
			int target = targets.get(i);
			
			if (query != null)
			if (target== query.blocked_node_)
				//if (relation==this.target_relation_inverse_ )
				if (this.inferred_relations_inverse_.contains(relation))
					continue;
			
			VectorI _targets = 	getEdges(query, target, _relation, _idx);

			if (_idx.value >0) {
				dist.plusOn(target,  p0/ _idx.value);		
				continue;
			}
			
			// this should not happen
			synchronized(missing_edges) {
				if (missing_edges.getC(target).contains(node)) continue;
				missing_edges.get(target).add(node);
			}			
			String _relation_name = graph_.getEdgeTypeName(_relation);
			String name = this.graph_.getNodeName(node);
			String target_name = this.graph_.getNodeName(target);
			
			if (++num_missing_edge_message < 10)
				System.err.println("missing reversed edge " 
						+ _relation_name  + "(" + target_name + ", " + name + ")");
		}
		return;		
	}
	public static int num_missing_edge_message = 0;

	public VectorS getNodeNames(Collection<Integer> node_set) {
		return getNodeNames(node_set, false);
	}
	public VectorS getNodeNames(Collection<Integer> node_set, boolean short_name) {
		VectorS names = new VectorS();
		for (int node : node_set)
			names.add(getNodeName(node, short_name));
		return names;
	}
	SetS unknown_nodes_ = new SetS();
	public void getNodeIds(String[] typed_names, SetI ids) {
		for (String name : typed_names) {
			if (name.length()==0) continue;
			int id = graph_.getNodeId(name);
			if (id == -1) {
				if (!unknown_nodes_.contains(name)){
					System.out.println("skip unknown node=" + name);
					unknown_nodes_.add(name);
				}
				continue;
			}
			ids.add(id);
		}		
	}
	
	public SetI getNodeIds(String[] typed_names) {
		SetI ids = new SetI();
		getNodeIds(typed_names, ids);
		return ids;
	}

	
	public static String getNodeShortName(String name) {
		int p = name.lastIndexOf(':');
		if (p==-1) return name;
		return name.substring(p + 1);
	}
	public String printDistribution(MapID distribution, int max_num_item, String pair_sep, String item_sep) {
	  VectorI ids = (VectorI) distribution.KeyToVecSortByValue(true);

	  StringBuffer sb = new StringBuffer();
	  for (int i = 0;  i < ids.size(); ++i) {
	    int id = ids.get(i);
	    if (i > 0) sb.append(item_sep);
	    
	    String name = graph_.getNodeName(id);
	    name = getNodeShortName(name);
	    sb.append(String.format("%s%s%.1e",
	    		name, pair_sep, distribution.getD(id, 0.0)));
	    if (max_num_item>=0)
	    	if (i+1 >=max_num_item) break; 
	  }
	  return sb.toString();
	}
	
  // Print the names of a set of nodes
  public String printNodeNameSet(Collection<Integer> node_set) {
  	return "[" + getNodeNames(node_set).join(" ") + " ]";
  }
  
  // for Random Walk with Restart (RWR)
	public void stepRWR(Query query, MapID src_dist,  MapID target_dist) {

		for (Map.Entry<Integer, Double> e : src_dist.entrySet()) {
			Set<Integer> link_types = graph_.getNodeOutlinkTypes(e.getKey());
			double p0 = e.getValue() / link_types.size();
			for (int iRel : link_types)
				stepForward(query, e.getKey(), iRel, p0, target_dist);
		}
	}
	
  // Random Walk with Restart (RWR) from a set of seed nodes
	// with maximum step size = max_step 
	// and damping factor = p.damping_factor
	// Sparsity settings are the same as PRA which is set in the conf file
	public MapID walkRWR(Query query, SetI seeds, HashSet<Integer> constraint_set, int num_steps) {
		MapID result = new MapID();		
		MapID dist = new MapID();
		dist.plusOn(seeds, 1.0 / seeds.size());
		for (int i = 0; i < num_steps; ++i) {
			MapID new_dist = new MapID();
			stepRWR(query, dist, new_dist);
			for (Map.Entry<Integer, Double> it: new_dist.entrySet()) {
				if (constraint_set.contains(it.getKey())) 
					result.plusOn(it.getKey(), it.getValue());
			}
			dist = new_dist;
		}
		return result;
	}
	
  // Random Walk with Restart (RWR) from a set of seed nodes
	// with maximum step size = max_step 
	// and damping factor = p.damping_factor
	// Sparsity settings are the same as PRA which is set in the conf file
	public MapID walkRWR(Query query, SetI seeds, double rwr_restart, int num_steps) {
		MapID result = new MapID();		
		MapID dist = new MapID();
		dist.plusOn(seeds);//, 1.0 / seeds.size());
		double weight = 1.0;
		for (int i = 0; i < num_steps; ++i) {
			MapID new_dist = new MapID();
			stepRWR(query, dist, new_dist);
			result.plusOn(new_dist, weight);
			dist = new_dist;
			weight *= (1-rwr_restart);
		}
		return result;
	}

	public SetI getCoverage(Query query, VectorI node, SetI seed, boolean repeated) {
		SetI result = seed;
		for (int relation: node) {
			seed = result;
			result = new SetI();
			if (repeated)
				repeatStep(query, seed, relation, 20, result);
			else
				stepCoverage(query, seed, relation, result);
		}
		return result;
	}
		
	public void stepCoverage(Query query, SetI seed, int relation, SetI result) {
		for (int e : seed)
			stepCoverage(query, e, relation, result);		
	}
	
	public void repeatStep(Query query, SetI seeds, int relation,  int step_limit, SetI result) {
		SetI dist = seeds;
		int i=0;
		for (i = 0; i < step_limit; ++i) {
			SetI new_dist = new SetI();
			stepCoverage(query, dist, relation, new_dist);
			if (new_dist.size() ==0) break;
			result.addAll(new_dist);
			dist = new_dist;
		}
		if (i == step_limit) 
			System.err.println("repeatStep cannot terminate after step_limit=" + step_limit);
		
	}
	
	MapISetI repeatStep_cache = new MapISetI();
	
	// follow a relation one or many times
	// find where the walker goes
	public SetI repeatStepCached(Query query, SetI seeds, int relation,  int step_limit) {
		SetI result = null;
		if (seeds.size() ==1) {
			result = repeatStep_cache.get(seeds.first());
			if (result != null) return result;
		}
		
		result = new SetI();
		this.repeatStep(query, seeds, relation, step_limit, result);
		
		if (seeds.size() ==1) repeatStep_cache.put(seeds.first(), result);		
		return result;
	}
	
	public SetI repeatStep(Query query, int seed, int relation,  int step_limit) {
		return repeatStepCached(query, new SetI(seed), relation, step_limit);
	}
	// find nodes having certain relation with the src_node
	public SetI repeatedExpand(String seed, String relation) {
		int id = graph_.getNodeId(seed);
		int rel = graph_.getEdgeType(relation);
		if (id == -1 || rel == -1) return new SetI();
		return repeatStep(null, id , rel, 50);		
	}
	
	public static char name_segment_char = ':';
	public String getNodeName(int id, boolean short_name) {
		String name = this.graph_.getNodeName(id);
		if (short_name) {
			int p = name.lastIndexOf(name_segment_char);
			if (p>=0) name = name.substring(p +1);
		}
		return name;
	}
	

	MapISetI not_cache = new MapISetI();

	public SetI generateNotMask(Query query) {//, MapVecIX<PathNode> field_notPaths) {
		if (field_notPaths_.size() == 0) return null;

		SetI seeds = query.seeds_.get(field_andPaths_.firstKey());
		FSystem
				.checkTrue(seeds.size() == 1, "assume only one seed for the filters");

		int key = seeds.first();
		if (not_cache.containsKey(key)) return not_cache.get(key);

		SetI not = new SetI();
		for (int field : field_notPaths_.keySet()) {
			for (VectorI node : field_notPaths_.get(field)) {

				SetI seed = new SetI(query.seeds_.get(field));
				if (seed.size() == 0) FSystem.die("empty seed set?");

				SetI set = getCoverage(query, node, seed, false);

				not.addAll(set);
			}
		}

		if (not.containsAny(query.good_)) {
			FSystem.die("mNot should not eliminate relevant entities");
		}
		not_cache.put(key, not);
		return not;
	}

	public void applyFilters(Query query, MapID distribution) {
		distribution.removeExcept(generateAndMask(query));
		distribution.removeAll(generateNotMask(query));
	}

	MapISetI and_cache = new MapISetI();

	public SetI generateAndMask(Query query) { //, MapVecIX<PathNode> field_andPaths) {
		if (field_andPaths_.size() == 0) return null;

		FSystem.checkTrue(field_andPaths_.size() <= 1,
				"assume only one field is related to field_andPaths_");

		SetI seeds = query.seeds_.get(field_andPaths_.firstKey());
		int key = seeds.size() == 1? seeds.first() : -1;
		
		if (key != -1)	if (and_cache.containsKey(key)) return and_cache.get(key);

		SetI and = new SetI();
		int i = 0;

		for (int field : field_andPaths_.keySet()) {
			SetI seed = query.seeds_.get(field);
			if (seed.size() == 0) FSystem.die("empty seed set?");

			for (VectorI node : field_andPaths_.get(field)) {
				SetI set = getCoverage(query, node, seed, true);

				if (set.size() == 0) FSystem.die("mMask.size()==0");

				if (i == 0) and.addAll(set);
				else and = and.andSet(set);
				++i;
			}
		}

		for (int good: query.good_)
		if (!and.contains(good)) 
			System.err.println("and paths=" + p.getString("and_paths", null)
					+ " should not eliminate relevant entity="
					+ this.getNodeName(good, false) + " in query=" + query.name_);
		
		
		if (key != -1)	and_cache.put(key, and);
		return and;
	}

	public VectorI parsePath(String path) {
		VectorI v = new VectorI();
		for (String name : path.split(",")) {
			int relation = graph_.getEdgeType(name);
			FSystem.checkTrue(relation !=-1, "unknown relation="+ name);
			v.add(relation);
		}
		return v;
	}
	
	public void parsePaths(MapIVecS field_paths_str,
			TMapIVecX<VectorI> field_paths) {
		for (int tree_id : field_paths_str.keySet()) 
			for (String path : field_paths_str.get(tree_id)) 
				field_paths.getC(tree_id).add(parsePath(path));
		return;
	}

	public TMapIVecX<VectorI> field_andPaths_ = new TMapIVecX<VectorI>(
			PathNode.class);
	public TMapIVecX<VectorI> field_notPaths_ = new TMapIVecX<VectorI>(
			PathNode.class);

	public TMapIVecX<VectorI> field_rerank_andPaths_ = new TMapIVecX<VectorI>(
			PathNode.class);
	public TMapIVecX<VectorI> field_rerank_notPaths_ = new TMapIVecX<VectorI>(
			PathNode.class);


	private VectorB is_relation_functional = new VectorB();
	private void detectFunctionalRelations(){
		System.out.println("\n[detectFunctionalRelations]");

		int k = graph_.getNumEdgeTypes();
		is_relation_functional.reset(k, false);
		
		VectorI support = new VectorI(k);
		VectorI fan_out = new VectorI(k);
		int N= graph_.getNumNodes();
		for (int node=0; node< N; ++node) {
			for (int relation: graph_.getNodeOutlinkTypes(node)) {
				support.plusOn(relation);
				fan_out.plusOn(relation, graph_.getEdges(node, relation).size());
			}
		}
		
		int M = graph_.getNumEdgeTypes();
		for (int r=0; r<M; ++r) {
			if (support.get(r)==0) continue;
			double branching = ((double)fan_out.get(r))/support.get(r) ;
	

			if (branching < p.functional_threshold) 
				is_relation_functional.set(r, true);

			if (!silent_) {
				System.out.printf("%s has branching=%.2f", 
						graph_.getEdgeTypeName(r), branching);
				
				if (branching<p.functional_threshold) 	
					System.out.println(" treat as functional");					
				else 	
					System.out.println();
			}
		}
		return;
	}
	public boolean isRelationFunctional(int relation) {
		return is_relation_functional.get(relation);
	}
	
	public Integer getInversedRelation(int relation) {
		if (relation==-1) {
			System.err.println("unknown inversed relation for " 
					+ graph_.getEdgeTypeName(relation));
			return -1;
		}
		return relation_inverse_.get(relation);
	}
	

	// keep track of the inverse of each relations
	private VectorI relation_inverse_ = new VectorI();

	private void setEdgeTypeInverse() {
		System.out.println("\n[setEdgeTypeInverse]");
		String relations[] = graph_.getOrderedEdgeLabels();
		relation_inverse_.reset(relations.length, -1);
		SetS set = new SetS();
		set.addAll(relations);

		for (int i = 0; i < relations.length; ++i) {
			String name = relations[i];
			if (!name.startsWith("_")) continue;

			String reversed_name = name.substring(1);
			if (!set.contains(reversed_name)) continue;

			int reverse = graph_.getEdgeType(reversed_name);

			relation_inverse_.set(i, reverse);
			relation_inverse_.set(reverse, i);
			
			if (!silent_) System.out.println(name + " has inverse " + reversed_name);
		}
		
		for (int i = 0; i < relations.length; ++i) {
			if (relation_inverse_.get(i) != -1) continue;
			
			String name = graph_.getEdgeTypeName(i);
			if (!silent_)	System.out.println("treat " + name + " as undirected edge type");
			relation_inverse_.set(i, i);
			//System.err.println(	"Missing inversed relation for " + name);
		}
		return;
	}
	public boolean isUndirected(int relation) {
		return relation_inverse_.get(relation)==relation;
	}
	public boolean canFollow(int rel0, int rel1) {
		return canFollow(rel0,rel1,false);
	}
	public boolean canFollow(int rel0, int rel1, boolean reversed) {
		if (this.forbidden_relations_.contains(rel1)) return false;
		
		if (rel0 == -1 || rel1 == -1) return true;
		
		if (p.no_undirected_FF)
			if (rel0 == rel1)
				if (this.isUndirected(rel0)) return false;
				
		// disallow functional relations to go backward-forward
		if (p.no_functional_BF)
		if (relation_inverse_.get(rel0) == rel1) 
				if (isRelationFunctional(rel1))		return false;
		

		// disallow no-cost relations to be repeated
		if (no_cost_relations_.contains(rel0)) 
			if (no_cost_relations_.contains(rel1)) 
				return false;
		
		if (no_repeat_relations_.contains(rel0)) 
			if (rel0 == rel1) return false;
		
		return true;
	}
	
	private void push(MapID pr, MapID r, int key, VectorI edges, double rwr_restart ) {
		double weight = r.get(key);
		pr.plusOn(key, weight * rwr_restart);
		
		r.remove(key);
		
		if (edges==null) return;
		r.plusOn(edges, (1-rwr_restart) * weight /edges.size());
	}
	
	public MapID walkSnowBall(int seed, double rwr_restart, double rwr_epsilon) {
		MapID pr = new MapID();	// the personalized page rank vector
		MapID r = new MapID();	// the residue vector
		r.put(seed, 1.0);
		
		while(true) {
			boolean pushed=false;
			for (Map.Entry<Integer, Double> i : r.entrySet()) {
				double value = i.getValue();
				int node = i.getKey();
				if (value < rwr_epsilon) continue;
				
				Set<Integer> relations = graph_.getNodeOutlinkTypes(node);
				for (int relation: relations){
					VectorI edges = graph_.getEdges(node, relation);
	//				if (edges !=null)
					double push_value = value/relations.size()/ edges.size();
					if ( push_value< rwr_epsilon) continue;
					pushed = true;
					push(pr,r,node, edges, rwr_epsilon);
				}
			}
			if (!pushed) break;
		}		
		return pr;
	}
	



	public void parseQuery(String line, Query query) {
		parseQuery(FString.splitVS(line, "\t"), query);
	}

	public boolean parseQuery(VectorS fields, Query query) {
		int num_fields = fields.size()-1;
		if (p.given_negative_samples) 
			--num_fields;	// the last column contains negative samples
		
		query.clear(num_fields);
		query.name_ = //p.dual_mode? fields.firstElement() + "," + fields.lastElement() : 
			fields.firstElement();

		getNodeIds(	fields.get(num_fields).split(" "), query.good_);

		for (int i = 0; i < num_fields; ++i) {
			getNodeIds(fields.get(i).split(" "), query.seeds_.get(i));
			
			if (query.seeds_.get(i).size()==0){
				System.err.println("missing field " + i + " in query="+fields.join(", "));
				return false;
			}	
		}

		if (p.blocked_field !=-1) {
			if (query.seeds_.get(p.blocked_field).size() !=1)
				FSystem.die("expect exactly one seed for blocked field="
						+ fields.get(p.blocked_field));
			
			if (p.target_relation !=null)
				query.blocked_node_ = query.seeds_.get(p.blocked_field).first();
		}
		
		if (p.timed_graph) 
			query.time_ = Integer.parseInt(
					AGraph.getRawNodeName(	fields.get(p.time_field)));
		
		if (p.given_negative_samples) {
			query.labeled_bad_ = 	getNodeIds(	fields.get(num_fields+1).split(" "));
		}
		return true;
	}


	public Query parseQuery(String line) {
		return parseQuery(line, "\t");
	}
	public Query parseQuery(String line, String separator) {
		return parseQuery(FString.splitVS(line, separator));
	}
	public Query parseQuery(VectorS fields) {
		Query query = new Query();
		if (parseQuery(fields, query))	return query;
		return null;
	}
}
