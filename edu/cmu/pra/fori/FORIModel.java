package edu.cmu.pra.fori;

import java.io.BufferedWriter;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.MapIMapID;
import edu.cmu.lti.algorithm.container.MapIVecS;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.TMapDX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.optimization.AModel;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.OptizationEval;
import edu.cmu.lti.algorithm.structure.IndexX;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.fori.Feature.BaseFeature;
import edu.cmu.pra.fori.Feature.RandomWalk;
import edu.cmu.pra.fori.Feature.SourceType;
import edu.cmu.pra.fori.Feature.TargetType;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.graph.IGraph;
import edu.cmu.pra.model.PRAModel;

// to simply certain operations
// we are going to treat bias as a feature

// FORI features are supported by the following structures
// Path:		tree_.nodes_
// RandomWalks:	random_walk_index_
// BaseFeature: base_feature_index_
// Feature:		feature_index_
public class FORIModel extends AModel {

	public Param p;
	public IGraph graph_;
	public GraphWalker walker_;

    // [[The decision to have only one tree for all the fields]]
    // We might want different constant nodes to share the same path types.
	//
	// However, it prevent the possibility of having conjunctions as nodes
	// conjunctions are always tricky to index ...
	public FORITree tree_ = null;//new FORITree();
	
	public VectorX<BaseFeature> base_features_() {
		return base_feature_index_.list_;
	}
	public VectorX<Feature> features_() {
		return feature_index_.list_;
	}
	public VectorX<RandomWalk> random_walks_() {
		return random_walk_index_.list_;
	}
	
	// for induction
	private IndexX<RandomWalk> random_walk_index_ = new IndexX<RandomWalk>(RandomWalk.class);
	private IndexX<BaseFeature> base_feature_index_ = new IndexX<BaseFeature>(BaseFeature.class);
	private IndexX<Feature> feature_index_ = new IndexX<Feature>(Feature.class);

	public FORIModel(GraphWalker walker){//IGraph graph) {
		this.graph_ = walker.graph_;
		this.walker_ = walker;//new GraphWalker(graph);

		p = new Param();
			
		if (p.feature_comments != null) feature_comments = MapSS
				.fromFile(p.feature_comments);

		tree_ = new FORITree(this);
		
		addBiasFeature();
	}

	public void initWeights() {
		param_weights_.reset(param_names_.size(), 0.0);
	}


	public void setParameters(double[] x) {
		param_weights_.setAll(x);
	}

	public double[] getParameters() {
		return param_weights_.toDoubleArray();
	}

	public int addPath(String path) { 
		return tree_.addPath(path);
	}
	

	
	public int addBaseFeature(String name) {
		return addBaseFeature(new BaseFeature(name, this));
	}	
	public int addBaseFeature(BaseFeature bf) {
		return this.base_feature_index_.add(bf);
	}
	
	public int addFeature(String name) {
		return addFeature(new Feature(name, this));
	}
	public int addFeature(Feature f) { 
		return feature_index_.add(f);
	}
	
	// assuming each path has the format
	// FieldID:relation,relation,...
	public void loadModel(String model_file) {
		FSystem.checkTrue(FFile.exist(model_file), "cannot find " + model_file);
		
		for (VectorS vs : FFile.enuRows(model_file, "\t", true)) {
			double weight = Double.parseDouble(vs.get(0));
			String field_path = vs.get(1);
			if (field_path.equals("bias")) continue;
			addFeature(field_path);
		}
		indexFeatures();
		loadWeights(model_file);
		return;
	}
	public String printConjunctionDistribution(){
		MapII counts = new MapII();
		for (Feature f: this.features_())
			counts.plusOn(f.base_features_.size());
		return counts.join("=", " ");

	}
	public String toString(){
		return String.format("#path=%d #rw=%d #bf=%d #f=%d \npath_lengths:%s \nfeature_sizes:%s", 
				tree_.nodes_.size(),
				this.random_walks_().size(),
				this.base_features_().size(),
				this.features_().size(),
				tree_.printLengthDistribution(),
				this.printConjunctionDistribution());
	}

	public boolean indexFeatures() {
		if (param_names_.size()==features_().size()) return false;
		
		for (int i =param_names_.size(); i<features_().size(); ++i) {
			Feature f = features_().get(i);
			param_names_.add(f.getName(this));
			param_weights_.add(0.0);
			
			param_comments_.add("");//f.base_features_.size()  f.getName(this, true));
	//		System.out.println("new feature " + i +" --> " + f.getName(this, true));
		}
		System.out.println(toString());
		return true;
	}
	
	public int addRandomWalkAsFeature(int rw) {	//RandomWalk
		int bf_id = base_feature_index_.add(new BaseFeature(TargetType.Y, rw, -1));
		return this.feature_index_.add(new Feature(bf_id));
	}
	
	public void loadRandomWalksToModel(String exploration_file, 
			int min_support, int min_hit, double min_acc) {
		this.clear();
		System.out.println("\nloadExplorationFile " + exploration_file
				+ "\n min_suport=" + min_hit + "\n min_accuracy=" + min_acc);

		FeatureStat stat = new FeatureStat();
		for (VectorS vec : FFile.enuRows(exploration_file)) {
			stat.parseLine(vec);
			if (stat.support_  < min_support) continue;	// skip RWs with small support
			
			
			int rw_id = addRandomWalk(vec.get(3));
			RandomWalk rw = random_walks_().get(rw_id);
			rw.stat.copy(stat);
			
			if (stat.hit_ < min_hit) continue;	
			if (stat.eval_ < min_acc) continue;	
			if (!rw.source_type_.equals(SourceType.X)) continue;
			addRandomWalkAsFeature(rw_id);
		}
		indexFeatures();
		return;
	}
	
	public void saveRandomWalks(String model_file){		
		BufferedWriter bw=FFile.newWriter(model_file);
		for (RandomWalk rw: this.random_walks_())
			FFile.writeln(bw,rw.stat.print() + "\t"+ rw.getName(this) );
		FFile.close(bw);
	}
	
	public void saveBFRandomWalks(String model_file){		
		BufferedWriter writer=FFile.newWriter(model_file);
		for (BaseFeature bf: this.base_features_()) {
			RandomWalk rw = this.random_walks_().get(bf.random_walk_);
			FFile.writeln(writer,rw.stat.print() + "\t"+ rw.getName(this) );
		}		
		FFile.close(writer);
	}



	// incrementally adding new random walks and features to a query
	// features are stored in a sparse format
	public static final MapID empty_distribution = new MapID();
	public static TMapDX<MapID> singular_distributions = new TMapDX<MapID>(MapID.class);
	public static MapID getSingularDistribution(Double value) {
		if (value==null) return empty_distribution;
		if (value==0.0) return empty_distribution;
		
		MapID result = singular_distributions.get(value);
		if (result == null) {
			result = new MapID();
			result.put(-1, value);
			singular_distributions.put(value, result);
		}
		return result;
	}
	
	public int addRandomWalk(String name) {
		return addRandomWalk(new RandomWalk(name, this));
	}
	public int addRandomWalk(RandomWalk rw) {
		 
		// TODO: do we need this?
		// it flushs out the support/acc statistics got during exploration 
		Integer rw_id= random_walk_index_.map_.get(rw);
		if (rw_id!=null) return rw_id;	// RW already exists
		
		FORINode parent = tree_.nodes_.get(rw.path_).parent_;
		if (parent !=null) {	// make sure its parent has a random walk
			RandomWalk p = new RandomWalk(rw.source_type_, rw.from_, parent.id_);
			rw.parent_ = addRandomWalk(p);
		}
		rw_id = random_walk_index_.add(rw);
		
		if (p.cache_RW ){
			boolean reversed = false;
			switch(rw.source_type_){
			case _z:	reversed = true;
			case z: {
				MapID dist = //this.getRandomWalk(null, rw_id, -1); 
				tree_.nodes_.get(rw.path_).walk(null, rw.getSingletonSeed(), reversed, true);
				this.zRW_cache.put(rw_id, dist);
			}break;
			}
		}
		return rw_id;
	}
	public MapID getRandomWalk(FORIQuery query, int rw_id, int Y){//RandomWalk rw,
		RandomWalk rw = random_walks_().get(rw_id);

		SetI seeds = null;
		boolean reversed=false;

		switch(rw.source_type_) {
		
		case _X: reversed = true;
		case X: {
			seeds = query.seeds_.get(rw.from_); 
		}break;
		
		case _Y:  reversed = true;
		case Y:{ 
			if (Y==-2)seeds = query.bad_;	//selected_ 
			else 	if (Y==-1)	seeds = query.good_;	
			else	seeds = new SetI(Y);
		}	break;
		
		case _z: reversed = true;
		case z: {
			if (p.cache_RW) return zRW_cache.get(rw_id);
			seeds = rw.getSingletonSeed(); 
		}break;
		default: FSystem.die("untreated type=" + rw.source_type_.name());
		}			
		return tree_.nodes_.get(rw.path_).walk(query, seeds, reversed, false);
	}
	public MapIX<MapID> zRW_cache= new MapIX<MapID>(MapID.class);
	
//	public MapXX<SetI,MapID> seed_cache_ = new MapXX<SetI,MapID>(SetI.class, MapID.class);

	public MapID getBaseFeature(FORIQuery query, BaseFeature bf){
		MapID random_walk = query.random_walks_.get(bf.random_walk_);
		MapID dist = null;
		switch(bf.target_type_) {
		case Y: dist = random_walk;	break;
		case X: dist = getSingularDistribution(random_walk.sum(query.seeds_.get(bf.to_))); break; 
		case z: dist = getSingularDistribution(random_walk.get(bf.to_)); break; 
		case n: dist = random_walk.size()==0? getSingularDistribution(1.0): empty_distribution; 	break; 
		case a: dist = getSingularDistribution(random_walk.sum()); break;	// to anything
		default: FSystem.die("untreated type=" + bf.target_type_.name());
		}		
		if (dist.containsKey(-1)) {
			FSystem.checkTrue(dist.size()==1, "either have constant or a distribution");
		}			
		return dist;
	}
	private void printRWDist(BufferedWriter inspect) {
//		FFile.writeln(inspect, String.format("rw%d\t%s%s\t%d\t\t%s",
//				rw_id, dist.containsAny(query.good_)?"*":"",
//				rw.getName(this, true),	dist.size(),	
//				walker_.printDistribution(dist, 10, "=", " ")));

	}
	
	private void indexRW(int rw_id, MapID dist, MapIMapID node_path_weight_){
		for (Map.Entry<Integer, Double> it: dist.entrySet())
			node_path_weight_.getC(it.getKey()).plusOn(rw_id, it.getValue());
		
	}
	
	
//switch(mode) {
//case g:{
//	if (query.good_.size()==1) {
//		query.target_Yrandom_walks_.getC(query.good_.first()).put(rw_id, dist);
//	}
//	else {
//		for (int target: query.good_)
//			query.target_Yrandom_walks_.getC(target)
//				.put(rw_id, getRandomWalk(query, rw, target));
//	}
//}break;
//}
	
	// these features does not include the bias term?
	public void updateFeatures(FORIQuery query, BufferedWriter inspect){
		Counter.count50.stepDot();
		if (inspect != null) FFile.writeln(inspect, query.print(walker_));
		
		// update random walks
		query.random_walks_.ensureCapacity(this.random_walks_().size());
		
		for (int rw_id = query.random_walks_.size(); rw_id < random_walks_().size(); ++ rw_id){ 
			RandomWalk rw = random_walks_().get(rw_id);
			MapID dist = getRandomWalk(query, rw_id, -1);
			query.random_walks_.add(dist);

			//// index the random walks 
			switch(rw.source_type_){
			case _Y:	indexRW(rw_id, dist, query.node_RrwY_); break;
			case Y:		indexRW(rw_id, dist, query.node_rwY_); break;
			case _X:	indexRW(rw_id, dist, query.node_RrwX_); break;
			case X:	indexRW(rw_id,dist, query.node_rwX_); break;
			default: break;
			}
	    if (inspect != null  && dist.size() > 0) //printRWDist(inspect);
	  		FFile.writeln(inspect, String.format("rw%d\t%s%s\t[%d] %s",
	  				rw_id, 
	  				dist.containsAny(query.good_)?"*":"",
	  				rw.getName(this, true),	dist.size(),	
	  				walker_.printDistribution(dist, -1, "=", " ")));
						//walker_.printDistribution(dist, 10, "=", " ")));
		}
		if (inspect!=null) {
			FFile.writeln(inspect, "\n[indexed__Y_rw_]");
			FFile.writeln(inspect, query.node_RrwY_.join(" ", ", "));
			FFile.writeln(inspect, "\n[indexed_X_rw_]");
			FFile.writeln(inspect, query.node_rwX_.join(" ", ", "));
		}
		
		// update base features
		query.base_features_.ensureCapacity(base_features_().size());
		for (int i = query.base_features_.size(); i < base_features_().size(); ++ i){
			query.base_features_.add(getBaseFeature(query, base_features_().get(i)));
		}

		// update features
		query.features_.ensureCapacity(features_().size());
		for (int f_id = query.features_.size(); f_id < features_().size(); ++ f_id){
			Feature f = features_().get(f_id);
			
			MapID dist = (f_id==0 && p.bias) ? 
					getSingularDistribution(p.bias_value):
					conjunction(query,  f.base_features_);
			query.features_.add(dist);
		}
		return;
	}
	
// if (inspect != null  && dist.size() > 0) 
//FFile.writeln(inspect, String.format("f%d\t%s%s\t%s",
//		f_id, dist.containsAny(query.good_)?"*":"",
//		f.getName(this, true),	
//		dist.joinF("=", " ", "%.1e")
//		//walker_.printDistribution(dist, 5, "=", " ")
//		));
	
	public MapID conjunction(FORIQuery query, SetI base_features) {

		MapID dist = null;
		for (int bf :	base_features) {
			MapID dist1 = query.base_features_.get(bf);
			if (dist==null)  dist = dist1;
			else dist = conjunction(dist, dist1);
		}
		return dist;
	}
	
	// multiplication of two feature values
	private MapID conjunction(MapID dist1, MapID dist2) {
		Double bias1 = dist1.get(-1);
		Double bias2 = dist2.get(-1);
		
		MapID result = dist1.multiply(dist2);
		
		if (bias1 != null)	result.plusOn(dist2, bias1);
		if (bias2 != null)	result.plusOn(dist1, bias2);
		if (bias1 != null && bias2 != null) result.plusOn(-1, -2* bias1 * bias2);
		Double d = result.get(-1);
		if (d != null) if (d > 1.0) FSystem.dieShouldNotHappen(); 
		return result;
	}
	
//	if (bias1 == null) 		 {
//		if (bias2 == null) return dist1.multiply(dist2);
//		else return dist1.multiply(bias2);
//		
//	}
//	else {
//		if (bias2 != null) return this.getSingularDistribution(bias1* bias2);
//		else return dist2.multiply(bias1);
//	}
//
	public static class DoubleWrapper{
		public double value=0.0;
	}
	
	public MapID predict(FORIQuery query, DoubleWrapper bias) {
		if (query.features_.size() < this.features_().size()) 
			updateFeatures(query, null);
		
		MapID result = query.features_.weightedSum(this.param_weights_);
		
		if (result.containsKey(-1)) {
			if (bias !=null) bias.value = result.get(-1);
			result.remove(-1);
		}
		walker_.applyFilters(query, result);
		return result;
	}
	
	public void predict(FORIQuery query, StringBuffer predction, StringBuffer reasons) {
		//Query query = model_.parseQuery(fields);
		DoubleWrapper bias = new DoubleWrapper();
		MapID result = predict(query, bias);
		
		predction.append(String.format("%s\t%d", query.name_, result.size()));
		
		reasons.append(query.name_).append("\n");

		VectorI sorted_result = result.KeyToVecSortByValue(true);

		// get the base value for each feature
		MapID base_values = query.features_.getRowMap(-1);
		
		for (int i = 0;  i < sorted_result.size();) {
			int id = sorted_result.get(i);
			String name = "";
			if (query.good_.contains(id)) name += "*";
			name += graph_.getNodeName(id);

			double score = result.get(id);	// + bias.value;
			if (score < 0) break;
			
			predction.append(String.format("\t%.3e,%s", score, name));

			MapID features = query.features_.getRowMap(id).plusOn(base_values);
			MapID feature_score = features.multiply(param_weights_);

			reasons.append(String.format("%.3e\t%s\t", score, name));
			reasons.append(feature_score.joinF("=", "\t", "%.3e"));
			reasons.append("\n");
			++i;
			if (p.top_prediction_results > 0)
				if (i >= p.top_prediction_results) break;			
		}
	}
	
//	public double classify(Query query, VectorD features) {
//		features.clear();
//		FSystem.checkTrue(p.num_fields == query.seeds_.size(), 
//				"trees_.size()  != query.seeds_.size()");
//
//		int node1 = query.seeds_.firstElement().first();
//		int node2 = query.seeds_.lastElement().first();
//		
//		for (int i = 0; i < p.num_fields; ++i) {
//			VecMapID data = tree_.walk(query, query.seeds_.get(i), null);
//			
//			if (i + 1< p.num_fields)
//				features.addAll(data.getRowV(node2));
//			else
//				features.addAll(data.getRowV(node1));
//		}
//		return features.inner(this.param_weights_);
//	}
//
//	public double classify(Query query,  StringBuffer reasons) {
//		VectorD features = new VectorD();
//		double score = classify(query, features);
//
//		reasons.append(String.format("%.3e\t%s", score, 	query.name_));
//		VectorD feature_score = features.multiplyOn(this.param_weights_);
//		for (int i = 0; i < features.size(); ++i) {
//			if (feature_score.get(i) == 0.0) continue;
//
//			reasons.append(String.format("\t%d=%.3e", i, feature_score.get(i)));
//		}
//		reasons.append("\n");
//		return score;
//	}

	public static class Param extends edu.cmu.lti.util.run.Param {
		public Param() {
			super(PRAModel.class);
			parse();
		}
		public boolean bias; // a single bias as the last parameter
		public double bias_value;

		// Assume that the last column contains the targets
		public int num_fields = -1;
		public boolean cache_RW;
		public String feature_comments;//"/mFeatureComments.txt"
//		public boolean timed_graph;
		public boolean dual_mode;

		public int top_prediction_results;
		public VectorI max_steps = new VectorI();

		public void parse() {
			top_prediction_results = getInt("top_prediction_results", 5);
			dual_mode = getBoolean("dual_mode", false);

			feature_comments = getString("feature_comments", null);

//			timed_graph = getBoolean("timed_graph", false);

			bias_value = getDouble("bias_value", 1.0);
			bias = getBoolean("bias", true);
			max_steps = VectorI.from(FString.splitVS(
					getString("max_steps", "3"), "-"));
			
			num_fields = max_steps.size() - 1;
			//num_fields =getString("max_steps",null).split("-").length;

			cache_RW = getBoolean("cache_RW", true);
		}

		public static void parsePaths(String paths, MapIVecS mvsPath) {
			if (paths == null) return;
			if (paths.length() == 0) return;
			for (String path : paths.split(GraphWalker.relation_separator)) {
				int p = path.indexOf(GraphWalker.field_separator);
				if (p == -1) FSystem.die("Field separator=" + GraphWalker.field_separator + " not found");
				int ic = Integer.parseInt(path.substring(0, p));
				mvsPath.getC(ic).add(path.substring(p + 1));
			}
		}

//		public static void parsePaths(String paths, VectorS vsPath) {
//			if (paths == null) return;
//			vsPath.addAll(paths.split(path_separator));
//		}
	}


	public VectorD getGradient(OptizationEval eva) {
		VectorD param_gradients = new VectorD();

//		if (p.bias) param_gradients.add(eva.bias_gradient_);//*p.scBias);
		param_gradients.addAll(eva.path_gradients_);
		return param_gradients;
	}
	

	
	public void addBiasFeature() {			

		feature_index_.add(new Feature());
		param_names_.add("bias");
		param_comments_.add("bias");
		param_weights_.add(0.0);
	}
	
	public void clear() {
		this.tree_.clear();
//		this.paths_.clear();
		this.random_walk_index_.clear();
		this.base_feature_index_.clear();
		this.feature_index_.clear();
		
		this.param_names_.clear();
		this.param_weights_.clear();
		param_comments_.clear();
		if (p.bias)  addBiasFeature();
		return;
	}
	
	public FORINode getNode(int id) {
		return tree_.nodes_.get(id);
	}
	
	public void saveModel(String model_file){	
		super.saveModel(model_file, true);
		super.saveModel(model_file + ".full", false);
		
		this.tree_.nodes_.save(model_file + ".paths");
//		this.random_walk_().save(model_file + ".random_walks");
//		this.base_features_().save(model_file + ".base_features");
		
		VectorS vs = new VectorS();
		for (RandomWalk rw: random_walks_())
			vs.add(rw + "\t" + rw.getName(this, true));
		vs.save(model_file + ".random_walks");
		
		vs.clear();
		for (BaseFeature bf: base_features_())
			vs.add(bf + "\t" + bf.getName(this, true));
		vs.save(model_file + ".base_features");	
		
	}
	
	public int concatenatePath(RandomWalk rwX, RandomWalk rwY) {
		return addReversedPath(rwX.source_type_, rwX.from_, rwX.path_, rwY.path_);
	}
	
	public int addReversedPath(SourceType type, int from, int reversed_path) {
		return addReversedPath(type, from, 0, reversed_path);
	}
	
	public int addReversedPath(SourceType type, int from, int base_path, int reversed_path) {
		RandomWalk rw = new RandomWalk(type, from, tree_.root_.id_);
		int rw_id = this.addRandomWalk(rw);	
		FORINode node = tree_.nodes_.get(base_path);
		
		for(FORINode p =  tree_.nodes_.get(reversed_path); p != tree_.root_; p = p.parent_) {
			int relation = walker_.getInversedRelation(p.relation_);
			node = tree_.addNode(node, relation);
			rw.path_ = node.id_;
			rw_id = this.addRandomWalk(rw);			
		}		
		return rw_id;
	}
	



//	public int sourceType2id(SourceType type){
//		
//	}
	public RandomWalk id2rw(int source, int path){
		SourceType type = SourceType.unknown;
		int from = -1;
		if (source >=0) {
			if (source == p.num_fields) type = SourceType.Y;
			else {
				type = SourceType.X;
				from = source;
			}
		}
		else {
			source = -source -1;
			if (source == p.num_fields) type = SourceType._Y;
			else {
				type = SourceType._X;
				from = source;
			}
		}
		return new RandomWalk(type, from, path);
	}
	public int addRandomWalk(int source, int path){
		return addRandomWalk(id2rw(source, path));
	}
	
	public FORIQuery parseQuery(String line) {
		return parseQuery(line, "\t");
	}
	public FORIQuery parseQuery(String line, String separator) {
		return parseQuery(FString.splitVS(line, separator));
	}
	public FORIQuery parseQuery(VectorS fields) {
		FORIQuery query = new FORIQuery();
		if (walker_.parseQuery(fields, query))	return query;
		return null;
	}
}