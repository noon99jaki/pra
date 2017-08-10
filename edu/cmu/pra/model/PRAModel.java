package edu.cmu.pra.model;

import java.io.BufferedWriter;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.optimization.AModel;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.OptizationEval;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.fori.FeatureStat;
import edu.cmu.pra.fori.FeatureStat.PathStat;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.graph.IGraph;

/** stuff related to the parameter*/
public abstract class PRAModel extends AModel {

	public Param p;
	public IGraph graph_;
	public GraphWalker walker_;

	public VectorX<PathTree> trees_ = new VectorX<PathTree>(PathTree.class);

	public VectorS path_names_ = new VectorS();// selected paths
	
	public VectorD path_weights_ = new VectorD();// scores of documents are linear combination of features

	public VectorX<PathNode> path_nodes_ = new VectorX<PathNode>(PathNode.class);// selected nodes	

	public double bias_ = 0;



	public PRAModel(GraphWalker walker) { //IGraph graph) {
		
		this.graph_ = walker.graph_;
		this.walker_ = walker;//new GraphWalker(graph);
		
		p = new Param();
		p.exploration_code_ += walker_.p.code_;
		
		p.model_code_ = p.exploration_code_ + "_acc"
		+ p.getString("min_feature_accuracy", null);
		if (p.bias) p.model_code_ += String.format("_b%.0e", p.bias_value);
			
		if (p.feature_comments != null) feature_comments = MapSS
				.fromFile(p.feature_comments);

		for (int i = 0; i < p.num_fields; ++i) 
			trees_.add(new PathTree(this, i, p.max_steps.get(i)));
		
	}
	public abstract VectorD getGradient(OptizationEval eva);


	public abstract void SetParameters();

	public void loadOfflineFeatures() {

		//if (p.bEntityRank) entRank.loadEntityRank();
		return;
	}


	abstract public void initWeights();

	public void indexPathTree() {

		path_names_.clear(); //vsPath.addAll( vPathNode.getVS(CTag.nameS));		
		path_nodes_.clear();
		for (PathTree tree : trees_) {
			tree.indexPaths();

			for (String path : tree.path_names_)
				path_names_.add(tree.field_ + GraphWalker.field_separator + path);
			path_nodes_.addAll(tree.nodes_);
		}

		System.out.println(path_names_.size() + " paths in total");

		VectorI counts = new VectorI();
		for (PathNode n : path_nodes_)	counts.plusOnE(n.num_steps_, 1);
		System.out.println("path length distribution=" + counts.joinIndexed());

		SetParameters();
		initWeights();
		return;
	}
	
	
	// each path is a integer vector or a string?
	public void  explorePaths(Query query, PathStat path_stat) {
		Counter.count50.stepDot();
		for (PathTree tree : trees_) tree.explorePaths(query, path_stat);
	}
	
	PathNode addPath(VectorI path) {
		return trees_.get(path.get(0)).addPath(path.sub(1));
	}

	
	public void createPathTrees(PathStat path_stat, boolean add_all, String out_file ) {
		BufferedWriter writer=FFile.newWriter(out_file);

		for (Map.Entry<VectorI, FeatureStat> it : path_stat.entrySet()) {
			VectorI path = it.getKey();
			FeatureStat stat = it.getValue();
			
			if (!add_all)
			if (stat.hit_ < p.min_feature_hit) continue;
			
			FFile.writeln(writer,stat.print() 
					+ "\t"+ path.get(0) + "~" 
					+ walker_.getEdgeTypeNames(path.sub(1)).join(",") );

			if (!add_all)
			if (stat.eval_ < p.min_feature_accuracy) continue;
			
			PathNode node = this.addPath(path);
			node.is_target_ = true;
		}
		indexPathTree();
		FFile.close(writer);
		return;
	}
	
	
	PathNode addPath(String field_path) { //, int *field
		int p = field_path.indexOf(GraphWalker.field_separator);
		if (p == -1) FSystem.dieShouldNotHappen();

		int tree_id = Integer.parseInt(field_path.substring(0, p));
		PathNode node = trees_.get(tree_id).addPath(field_path.substring(p + 1));
		return node;
	}

	// assuming each path has the format
	// FieldID:relation,relation,...
	public void loadModel(String model_file) {
		FSystem.checkTrue(FFile.exist(model_file), "cannot find " + model_file);
		
		for (VectorS vs : FFile.enuRows(model_file, "\t", true)) {
			double weight = Double.parseDouble(vs.get(0));
			String field_path = vs.get(1);
			if (field_path.equals("bias")) continue;
			addPath(field_path).is_target_ = true;
		}

		indexPathTree();
		loadWeights(model_file);
		//this.saveModel(model_file +".loaded", false);
		return;
	}

	public void clear() {
		this.clearWeights();
		for (PathTree tree : this.trees_)
			tree.clear();
	}

	public void loadExplorationFile(String exploration_file) {
		this.clear();

		System.out.println("loadExplorationFile " + exploration_file
				+ "\n min_suport=" + p.min_feature_hit + "\n min_accuracy="
				+ p.min_feature_accuracy);

		for (VectorS vec : FFile.enuRows(exploration_file)) {
			String feature = vec.get(1);
			if (feature.equals("bias")) continue;

			double accuracy = Double.parseDouble(vec.get(0));
			double support = Double.parseDouble(vec.get(2));


			//if (suport ) < p.min_feature_suport) continue;
			if (accuracy < p.min_feature_accuracy) continue;
			addPath(vec.get(1)).is_target_ = true;

		}
		indexPathTree();
		//	  loadWeights(exploration_file);
		return;
	}

	public void getFeatures(Query query, BufferedWriter inspect) {
		Counter.count50.stepDot();
		//g.setTime(q.time);
		if (inspect != null) 
			FFile.writeln(inspect, query.print(walker_));
		
		query.features_.clear();

		//		if (p.timed_graph) ((Graph)graph_).setTime(query.time_);
		// eliminate certain link from the graph for training
		//assume there is one and only one entity in the distribution
		//		this.walker_.setBlockedEdges(query);

		for (int field = 0; field <  query.seeds_.size(); ++field) {
				SetI seed = query.seeds_.get(field);
				VecMapID data = trees_.get(field).walk(query, seed, inspect);
				query.features_.addAll(data);
		}
		if (p.dual_mode) {
			// a design is to use backward rw here
			SetI seed = query.good_;
			VecMapID data = trees_.get(p.num_fields-1).walk(query, seed, inspect);		//Backward
			query.features_.addAll(data);
		}
		return;
	}

	public MapID predict(Query query) {
		if (query.features_.size() == 0) getFeatures(query, null);
		MapID result = query.features_.weightedSum(path_weights_);
	  walker_.applyFilters(query, result);
	  return result;
	}
	
	public void predictQuery(Query query, 
			BufferedWriter result_writer,	BufferedWriter reason_writer) {
		StringBuffer result = new StringBuffer();
		StringBuffer reason = reason_writer!=null? new StringBuffer() : null;
		
		//MapID dist = predict(query);
		this.predictQuery(query, result, reason);
		//FString.split(fields, "\t")
		
    synchronized(result_writer) {
			FFile.writeln(result_writer, result.toString());
    }
		if (reason_writer!=null)
    synchronized(reason_writer) {
			FFile.writeln(reason_writer, reason.toString());		
    }
	}

	public void predictQuery(Query query,   
			StringBuffer predction, StringBuffer reasons) {
		
		MapID result = predict(query);
		//Query query = model_.parseQuery(fields);
		predction.append(String.format("%s\t%d", query.name_, result.size()));
		
		if (reasons!=null)	reasons.append(query.name_).append("\n");

		double bias = p.bias ? param_weights_.get(0) : 0;
		bias *= p.bias_value;

		VectorI sorted_result = result.KeyToVecSortByValue(true);

		for (int i = 0; i <  sorted_result.size(); ++i) {
			int id = sorted_result.get(i);
			
			double score = result.get(id) + bias;
			if (score < p.prediction_threshold) break;
			
			String name =  (query.good_.contains(id)) ?"*" : "";
			name += graph_.getNodeName(id);
			predction.append(String.format("\t%.3e,%s", score, name));


			if (reasons!=null) {
				MapID feature_score = query.features_.getRowMap(id)
				.multiply(this.path_weights_);//				param_weights_);
				reasons.append(String.format("%.3e\t%s\t", score, name));
				VectorI sorted_reason = feature_score.KeyToVecSortByAbsValue(true);
				reasons.append(feature_score.join(sorted_reason,"=", "\t", false));
				reasons.append("\n");
			}
		}
	}
	
	MapIX<VecMapID> cache_node_rw_ = new MapIX<VecMapID>(VecMapID.class);
	MapIX<VecMapID> cach_dual_rw_ = new MapIX<VecMapID>(VecMapID.class);

	// assume there is only one source and target
	public VectorD generateDualFeatures(Query query, int source, int target) {
		VectorD features = new VectorD();
		VecMapID node_data =  cache_node_rw_.get(source);
		if (node_data==null) {
			node_data= trees_.get(0).walk(query, source);
			cache_node_rw_.put(source, node_data);
		}
		
		VecMapID dual_data =  cach_dual_rw_.get(target);
		if (dual_data==null) {
			dual_data= trees_.lastElement().walk(query,target);
			cach_dual_rw_.put(target, dual_data);
		}
		
		features.addAll(node_data.getRowV(target));
		features.addAll(dual_data.getRowV(source));
		return features;
	}
	
	public double classify(Query query,  StringBuffer reasons) {
		//features.clear();
		FSystem.checkTrue(trees_.size() == query.seeds_.size(), 
				"trees_.size()  != query.seeds_.size()");

		VectorD features = generateDualFeatures(query, 	
				query.seeds_.firstElement().first(),
				query.seeds_.lastElement().first());
		
		double score = features.inner(this.path_weights_);
		double bias = p.bias ? param_weights_.get(0) : 0;
		score += bias * p.bias_value;
		
		if (reasons!=null) {
			reasons.append(String.format("%.3e\t%s", score, 	query.name_));
			
			VectorD feature_score = features.multiplyOn(this.path_weights_);
			for (int i = 0; i < features.size(); ++i) 
				if (feature_score.get(i) != 0.0)
					reasons.append(String.format("\t%d=%.3e", i, feature_score.get(i)));
			reasons.append("\n");
		}
		return score;
	}

	public static class Param extends edu.cmu.lti.util.run.Param {
		public Param() {
			super(PRAModel.class);
			parse();
		}

		public boolean bias; // a single bias as the last parameter
		public double bias_value;

		public int dual_steps;
		public VectorI max_steps = new VectorI();
		public String max_steps_str;
		// Assume that the last column contains the targets
		public int num_fields = -1;
		//public int num_columns = -1;


		public boolean cache_RW;
		public double min_feature_accuracy;// the probability of reaching any correct answer
		public int min_feature_hit;	// # queries for which a path leads to any correct answer

		public String feature_comments;//"/mFeatureComments.txt"


		public boolean dual_mode;

		public double prediction_threshold;

		public int max_num_exploration_queries;
		public int max_num_exploration_particles;
		
		//public double min_exploration_particle;
		public void parse() {
			max_steps_str = getString("max_steps", "3");
			max_steps = VectorI.from(FString.splitVS(max_steps_str, "-"));
			num_fields = max_steps.size();
			
			//num_columns=this.dual_mode? num_fields: num_fields+1;
			

			max_num_exploration_queries = getInt("max_num_exploration_queries", -1);
			max_num_exploration_particles = getInt("max_num_exploration_particles", -1);
			
			
			prediction_threshold = getDouble("prediction_threshold", 0.0);
			dual_mode = getBoolean("dual_mode", false);

			feature_comments = getString("feature_comments", null);

			dual_steps = getInt("dual_steps", 3);

			bias_value = getDouble("bias_value", 1.0);
			bias = getBoolean("bias", true);
			//num_fields = getInt("num_fields", -1);


			min_feature_hit = getInt("min_feature_hit", 2);
			min_feature_accuracy = getDouble("min_feature_accuracy", 0.01);

			cache_RW = getBoolean("cache_RW", false);
			exploration_code_ = "M" + max_steps_str;

			if (max_num_exploration_queries != -1) 
				exploration_code_ += "_q" + max_num_exploration_queries;
			
			if (max_num_exploration_particles != -1) 
				exploration_code_ += String.format("_p%.0e", (double)max_num_exploration_particles);
			
			exploration_code_ += "_h" + min_feature_hit;


		}
		public String exploration_code_;
		public String model_code_;


	}


}




