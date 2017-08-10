package edu.cmu.pra;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.container.VecVecS;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.ir.eva.IREvaluation;
import edu.cmu.lti.algorithm.math.MomentumVec;
import edu.cmu.lti.algorithm.optimization.lbfgs.OWLBFGS;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.EvalLBFGS;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.IFunction;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.OptizationEval;
import edu.cmu.lti.algorithm.structure.KeepTopK;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.run.DataSplit;
import edu.cmu.lti.util.run.Learner;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.system.ThreadPool.WorkThread;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.CTag.LossMode;
import edu.cmu.pra.CTag.NegMode;
import edu.cmu.pra.CTag.Prediction;
import edu.cmu.pra.CTag.RankMode;
import edu.cmu.pra.CTag.ThreadTask;
import edu.cmu.pra.fori.FeatureStat.NormalizationMode;
import edu.cmu.pra.fori.FeatureStat.PathStat;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.graph.IGraph;
import edu.cmu.pra.model.ModelPathRank;
import edu.cmu.pra.model.ModelRelationRank;
import edu.cmu.pra.model.PRAModel;
import edu.cmu.pra.model.Query;

public class LearnerPRA extends Learner implements IFunction {

	PRAThreadPool pool_ = new PRAThreadPool();

	public OptizationEval opt_eval_ = new OptizationEval();
	public IREvaluation ir_eval_ = new IREvaluation();// = q.eval(rlt);

	public PRAModel model_ = null;

	public OWLBFGS lbfgs_ = new OWLBFGS();
	public IGraph graph_;// = new Graph();
	public GraphWalker walker_;
	public VectorX<Query> queries_ = new VectorX<Query>(Query.class);
	
	//queries in the loss function
//	public VectorX<Query> selected_queries_ = new VectorX<Query>(Query.class);
	public VectorI selected_queries_ = new VectorI();

	public Param p = null;// new Param();

	public LearnerPRA(String args) {
		this(null, args);
	}
	
	public LearnerPRA(GraphWalker walker, String args) {
		this(walker, args, null);
	}
	
	//Graph graph
	public LearnerPRA(GraphWalker walker, String args, String output_folder) {
		super(LearnerPRA.class, args);
		
		p = new Param();
		
		if (walker==null) {
			//graph_ = new Graph();
			//graph_.loadGraph(p.graph_folder);
			walker_ = new GraphWalker(p.graph_folder);
			this.graph_ = walker_.graph_;
		}
		else {
			walker_ = walker;
			graph_ = walker.graph_;
		}
		
		
		init();
		
		if (output_folder==null)	setOutputFolder();
		else this.out_folder_ = output_folder;
	}

	void init() {
		switch (p.rank_mode) {
		case Path:
			model_ = new ModelPathRank(walker_);//graph_);
			break;
		case Rel:
			model_ = new ModelRelationRank(walker_);//graph_);
			break;
		default:
			FSystem.die("unknown rankMode=" + p.rank_mode);
			break;
		}

		if (p.target_relation != null) {
			super.param.train_samples = super.param.train_samples.replaceFirst(
					"<target_relation>", model_.walker_.p.target_relation);
			super.param.test_samples = super.param.test_samples.replaceFirst(
					"<target_relation>", model_.walker_.p.target_relation);
			super.param.model = super.param.model.replaceFirst(
					"<target_relation>", model_.walker_.p.target_relation);
		}
		
		if (p.multi_threaded) pool_.startThreads(p.num_threads);		
	}
	// make it static for nested classes to use it
	// public static Param p=null;//dont instantiate with static
	public static class Param extends edu.cmu.lti.util.run.Param implements
			Serializable {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public Param() {
			super(LearnerPRA.class);
			parse();
		}

		/** loss */
		public LossMode loss_mode = null;
		public double L1;
		public double L2;
		public boolean pairwise_loss;
		public double negative_weight;
		public int K_value;
		public NegMode negative_mode;
		public double polynomial_value;
		public double exponential_value;
		public double log_base;

		public String obj_code_;
		public boolean output_training_data = false;
		public String output_training_data_folder;

		private void parseTrain() {
			loss_mode = LossMode.valueOf(getString("loss_mode", LossMode.log.name()));
			L1 = getDouble("L1", 1.0);
			L2 = getDouble("L2", 1.0);
			pairwise_loss = getBoolean("pairwise_loss", false);
			output_training_data = getBoolean("output_training_data", false);
			output_training_data_folder = getString("output_training_data_folder", "training_data");

			negative_mode = NegMode
					.valueOf(getString("negative_mode", NegMode.Sqr.name()));
			negative_weight = getDouble("negative_weight", 10.0);

			polynomial_value = getDouble("polynomial_value", 1.5);
			K_value = getInt("K_value", 10);
			exponential_value = getDouble("exponential_value", 1.5);
			log_base = Math.log(exponential_value);

			rank_mode = RankMode
					.valueOf(getString("rank_mode", RankMode.Path.name()));

			obj_code_ = rank_mode.name();

			if (loss_mode.equals(LossMode.none)) {
				if (this.rank_mode.equals(RankMode.Path)) FSystem
						.die("should use lossMode=none with rankMode=R");
				obj_code_ = "_untrained";
			} else {

				obj_code_ += String.format("_%s%s", 
						negative_mode.name(), getString("negative_weight", null));
				
				if (negative_mode.equals(NegMode.topK)) obj_code_ += String.format(
						"%d", K_value);
				if (negative_mode.equals(NegMode.poly)) obj_code_ += String.format(
						"%.1f", polynomial_value);
				if (negative_mode.equals(NegMode.expX)) obj_code_ += String.format(
						"%.1f", exponential_value);
				
				
				if (L1 != 0.0) obj_code_ += String.format("_L1=%.0e", L1);
				if (L2 != 0.0) obj_code_ += String.format("_L2=%.0e", L2);

				if (pairwise_loss) obj_code_ += "_paired";
			}
		}


		public double enOverflow = 20;
		public double exp_overflow;
		public double probability_overflow;
		//public boolean cache_data;
		
		public RankMode rank_mode = null;
		public boolean inspect_data;
		public boolean inspect_query;
		public boolean print_node_pairs;
		public boolean print_negative_nodes;
		
		public boolean multi_threaded;
		public int num_threads = 1;

		public String explore_file;


		public String id;
		public String graph_folder;
		public String subgraph_store;
		public String target_relation;
		
		public String prediction_folder;
		public String model_folder;
		public boolean collect_models=false;
		public boolean collect_predictions=false;
		public boolean print_predictions;

		public void parse() {
			print_node_pairs = getBoolean("print_node_pairs", false);
			print_negative_nodes = getBoolean("print_negative_nodes", false);
			
			collect_models = getBoolean("collect_models", false);
			collect_predictions = getBoolean("collect_predictions", false);

			explore_file = getString("explore_file", null);

			inspect_data = getBoolean("inspect_data", false);
			inspect_query = getBoolean("inspect_query", false);

			//cache_data = getBoolean("cache_data", true);
			print_predictions = getBoolean("print_predictions", false);

			exp_overflow = Math.exp(enOverflow);

			probability_overflow = exp_overflow / (1 + exp_overflow);

			num_threads = getInt("num_threads", 1);
			multi_threaded = num_threads > 1;

			id = getString("id", "a");
			target_relation = getString("target_relation", null);
			
			graph_folder = getFolder("graph_folder", null);
			
			prediction_folder = getFolder("prediction_folder", null);
			model_folder = getFolder("model_folder", null);
			
			if (prediction_folder==null)
				prediction_folder=getString("task", "XX") + ".predictions/";
			if (model_folder==null)
				model_folder=getString("task", "XX") + ".models/";
			
			if (print_predictions)
			if (this.collect_predictions) FFile.mkdirs(prediction_folder);
			if (this.collect_models)  FFile.mkdirs(model_folder);
			
			subgraph_store = getString("subgraph_store", null);

			parseTrain();
		}
	}
	
	

	class PRAThreadPool extends edu.cmu.lti.util.system.ThreadPool {

		// The task to be performed by the threads
		//protected boolean generate_data_ = false;
//		protected boolean induce_ = true;
//		protected boolean train_ = true;
//		protected boolean test_ = true;
		private ThreadTask thread_task_ = ThreadTask.none;
		
		protected BufferedWriter prediction_writer_ = null;
		protected BufferedWriter reason_writer_ = null;

		public void setTask(ThreadTask thread_task) {
			thread_task_ = thread_task;
		}
		
		public void runTask(ThreadTask thread_task, Collection<Query> queries) {
			Counter.count50.clear();
			thread_task_ = thread_task;
			for (Query query: queries) pool_.addJob(query);
			pool_.waitJobs();
		}
		
		
		public void addJob(Query query) {
			super.addJob(new Job(-1, null, query));
		}
		public MapIX<VecMapID> walk_cache_ = new MapIX<VecMapID>(VecMapID.class);


		public class WorkThread extends edu.cmu.lti.util.system.ThreadPool.WorkThread {
//			public Query query_ = new Query();	// TODO: remove this?
			
			public OptizationEval opt_eval_ = new OptizationEval();	// for training
			public IREvaluation ir_eval_ = new IREvaluation();	// for validation
			
			public VectorS reasons_ = new VectorS();
			public VectorS predictions_ = new VectorS();
			public PathStat path_stat = new PathStat(); // for path exploration
			
			public WorkThread(int id) { super(id); }		
			
			@Override public void workOnJob(edu.cmu.lti.util.system.ThreadPool.Job job) {
				//System.out.println("iTh="+t.id+" iJob="+job.id);
				switch(thread_task_) {
				case RandomWalk: 				
					prepareFeatures((Query)job.data_, null);
					break;
					
				case EvaluateTrain: 
					evaluateTrain((Query)job.data_, opt_eval_);
					break;
				case EvaluateTest: 
					evaluateTest((Query)job.data_, ir_eval_);
					break;
					
				case CachedPredict: {
					Query query_ = new Query();	
					model_.walker_.parseQuery(job.key_, query_);
					predictQuery(query_, predictions_, reasons_);		
				}break;
				
				case Walk: 				
					VecMapID features = model_.trees_.get(0).walk(null, job.id_);
					Counter.count50.stepDot();
					walk_cache_.put(job.id_, features);
					break;
					
				case Predict: {
					Query query_ = new Query();	
					model_.walker_.parseQuery(job.key_, query_);
					model_.predictQuery(query_, prediction_writer_, reason_writer_);				
				}break;
				
				case ExplorePaths:
					model_.explorePaths((Query)job.data_, path_stat);
					break;
				default:
					FSystem.die("ThreadPool.thread_task_ is not set");
					break;
				}
			}
		}

		@Override public edu.cmu.lti.util.system.ThreadPool.WorkThread newWorkThread(int i) {
			return new WorkThread(i);
		}
	}


	public void  loadQueries(String sample_file) {
		
		System.out.println("loadQuery()" + sample_file);
		queries_.clear();
		if (!FFile.exist(sample_file)) FSystem.die("missing query file =" + sample_file);

		for (VectorS line : FFile.enuRows(sample_file, "\t")) {
			Query query = model_.walker_.parseQuery(line);
			if (query!=null)	queries_.add(query);
		}
		
		System.out.println("done loading |queries|=" + queries_.size() + "\n");
	}
	


	void explorePaths(VectorI ids, String code) {
		model_.clear();
		selected_queries_ = ids;

		System.out.println("\nexplorePaths() with #queries=" + ids.size());
		PathStat path_stat = new PathStat();
		
		VectorX<Query> queries= this.queries_.sub(ids);
		if (p.multi_threaded) {
			pool_.runTask(ThreadTask.ExplorePaths, queries);
			for (WorkThread thread:  pool_.threads_){
				PathStat stat =  ((PRAThreadPool.WorkThread)thread).path_stat;
				path_stat.plusOn( stat);
				stat.clear();
			}
		} else {
			//BufferedWriter inspect = (p.inspect_data)?FFile.newWriter(p.explore_file		+ ".inspection"):null;
			for (Query query : queries)		model_.explorePaths(query, path_stat);	
			
			//if (p.inspect_data) FFile.close(inspect);
		}
		
		path_stat.normalize(NormalizationMode.SUP, queries.size());
		
		System.out.println("#ExpPath=" + path_stat.size());
		model_.createPathTrees(path_stat, p.rank_mode.equals(RankMode.Rel),
				out_folder_ + code + ".explore");
		path_stat.clear();

		// Set the weights of paths as their probability of reaching correct nodes
	//	model_.saveModel(this.output_folder_ + code + ".initial_model", false);

//		model_.loadExplorationFile(this.output_folder_ + code + ".explore");
//		model_.saveModel(this.output_folder_ + code + ".model", false);
		return;

	}


	

	protected void selectNegativeSamples(MapID sys, SetI bad) {
		bad.clear();
		
		//SetI bad = new SetI();
//		VectorI bad = new VectorI();
		VectorI vi = (VectorI) sys.KeyToVecSortByValue(true);
		if (p.negative_mode.equals(NegMode.all)) {
			bad.addAll(vi);
		}
		else if (p.negative_mode.equals(NegMode.topK)) {
			bad.addAll(vi.left(p.K_value));
		}
		else {
			for (int i = 0; true; ++i) {
				int n = 0;
				switch (p.negative_mode) {
				case expX:
					n = (int) Math.floor(Math.exp(i * p.log_base));
					break;
				case poly:
					n = (int) Math.floor(Math.pow(i, p.polynomial_value));
					break;
				case exp:
					n = (int) Math.pow(2, i) - 1;
					break;
				case sqr:
					n = i * i;
					break;
				case Sqr:
					n = i * (i + 1) / 2;
					break;
				case tri:
					n = i * i * i;
					break;
				case Tri:
					n = i * (i + 1) * (i + 2) / 6;
					break;
				default:
					FSystem.die(p.negative_mode + " not implemented");
					break;
				}
				if (n >= vi.size()) break;
				bad.add(vi.get(n));
			}
		}
		return;
	}

	public void prepareFeatures(Query query, BufferedWriter inspect) {
		model_.getFeatures(query, inspect);
		
		MapID result = query.features_.sum();
		model_.walker_.applyFilters(query, result);
		query.hit_ = new SetI(result.sub(query.good_).keySet());
		
		result.removeAll(query.good_);
		
		selectNegativeSamples(result, query.bad_);

		if (walker_.p.given_negative_samples) {
			query.bad_.addAll(query.labeled_bad_);
		}
		
		// this is the set that we care about
		SetI selected = new SetI();
		selected.addAll(query.good_);
		selected.addAll(query.bad_);

		query.features_sampled_.clear();
		for (MapID feature : query.features_)
			query.features_sampled_.add(feature.subSet(selected));

	}

	protected String prepareFeatures(String code) {

		StopWatch watch = new StopWatch();

		if (p.multi_threaded) {
			pool_.runTask(ThreadTask.RandomWalk,queries_);
		} 
		else {
			BufferedWriter inspect = p.inspect_data ? 
					FFile.newWriter(this.out_folder_ + code + ".data.inspect") : null;					
			for (Query query : queries_) prepareFeatures(query, inspect);			
			if (p.inspect_data) FFile.close(inspect);
			
			// TODO: refactorize to remove inspect from all functions
			// Prints data when output_training_data=true 
		}
		if (p.output_training_data) outputTrainingData();
		if (p.print_node_pairs) printNodePairs();
		if (p.print_negative_nodes) printNegativeNodes();
		
		return String.format("%d\t%f\t%d\t%s", 
			queries_.size(), watch.getSec(), 
			FSystem.memoryUsedM(), Query.printQueryStatistics(queries_));
	}
	private void outputTrainingData(){
		if (p.target_relation == null) FSystem.die("Need to specify target_relation");
		FFile.mkdirs(p.output_training_data_folder);
		BufferedWriter writer = FFile.newWriter(
				p.output_training_data_folder + "/" + p.target_relation + ".data");					
		for (Query query : queries_) {
			if (query.seeds_.size() != 1) FSystem.die("Expects only one seed group.");
			if (query.seeds_.get(0).size() != 1) FSystem.die("Expects only one seed.");
			int seed = query.seeds_.get(0).first();
			for (int target : query.good_) OutputNodePair(query, seed, target, true, writer);		
			for (int target : query.bad_) OutputNodePair(query, seed, target, false, writer);		
		}
		FFile.close(writer);
		model_.saveModel(p.output_training_data_folder + "/" + p.target_relation + ".model", false);
	}
	//For our experiments, ideally we would like to have tuples of the form:
	// entity pair, relation, relation paths with weights, label
	// relation, label, entity pair, relation paths with weights

	private void OutputNodePair(Query query, int seed, int target, boolean good, BufferedWriter writer) {
		FFile.write(writer, String.format(
				"%s,%s,%s,%s",
				p.target_relation,
				good ? "+" : "-",
				graph_.getNodeName(seed),
				graph_.getNodeName(target)));

		for (int feature = 0; feature < query.features_sampled_.size(); ++feature) {
			Double value = query.features_sampled_.get(feature).get(target);
			if (value != null) FFile.write(writer, String.format(",%d:%f", feature, value));
		}
		FFile.write(writer, "\n");
	}
	//useful for testing/training external classifier
	private void printNegativeNodes(){
		VectorS pairs= new VectorS();
		for (Query query: queries_){
			String N0=walker_.getNodeNameN(query.blocked_node_);
			String good= walker_.getNodeNames(query.good_).join(" ");
			String bad= walker_.getNodeNames(query.bad_).join(" ");
			pairs.add(N0+"\t"+good+"\t"+bad);
		}		
		pairs.save(param.train_samples +  ".neg");
	}
	
	
	//useful for testing/training FOIL
	private void printNodePairs(){
		SetS pairs= new SetS();
		for (Query query: queries_){
			
			String N0=walker_.getNodeNameN(query.blocked_node_);
			for (int n : query.good_)
				pairs.add(N0+","+walker_.getNodeNameN(n) +": +");
			for (int n : query.bad_)
				pairs.add(N0+","+walker_.getNodeNameN(n) +": -");
		}		
		pairs.save(param.train_samples + ".pairs");
	}
	


	protected double sigmoid(double score) {
		if (score > p.enOverflow) score = p.enOverflow;
		else if (score < -p.enOverflow) score = -p.enOverflow;
		double exp = Math.exp(score);
		return exp / (1 + exp);
	}

	protected void evaluateNode(boolean good, int node, double score, double weight,
			final Query query, OptizationEval eva) {
		if (model_.p.bias) score += model_.bias_ * model_.p.bias_value;

		double p = sigmoid(score);
		eva.loss_ += good ? -Math.log(p) * weight : -Math.log(1 - p) * weight;

		double error = good ? (1 - p) * weight : -p * weight;
		for (int feature = 0; feature < query.features_sampled_.size(); ++feature) {
			Double value = query.features_sampled_.get(feature).get(node);
			if (value != null) eva.path_gradients_.minusOn(feature, error * value);
		}
		if (model_.p.bias) eva.bias_gradient_ -= error * model_.p.bias_value;
	}

	protected void evaluatePair(int good, double good_score, int bad,
			double bad_score, double weight,		final Query query, OptizationEval eva) {
		double margin = good_score - bad_score;
		double p = sigmoid(margin);
		eva.loss_ += -Math.log(p) * weight;

		double error = (1 - p) * weight;
		for (int feature = 0; feature < query.features_sampled_.size(); ++feature) {
			MapID dist = query.features_sampled_.get(feature);
			Double value = dist.getD(good) - dist.getD(bad);
			eva.path_gradients_.minusOn(feature, error * value);
		}
	}

	public void evaluateTrain(Query query,	OptizationEval eval){//, BufferedWriter inspect) {

		//MapID result = model_.predict(query);//
		//change it to features_sampled_.weightedSum? no the test eval will be weird
		MapID result =query.features_sampled_.weightedSum(model_.path_weights_);
	  model_.walker_.applyFilters(query, result);
		
		
		if (p.pairwise_loss) {
			if (query.good_.size() != 0 && query.bad_.size() != 0) {
				double w = 1.0 / query.bad_.size() / query.good_.size();
				for (int good : query.good_)
					for (int bad : query.bad_)
						evaluatePair(good, result.get(good), bad, result.get(bad), w, query,
								eval);
			}
		} else {
			if (query.good_.size() >0) {
				double weight = 1.0 / query.good_.size();
				for (int good : query.good_)
					evaluateNode(true, good, result.getD(good, 0.0), weight, query, eval);
			}
			if (query.bad_.size() >0) {
				double weight = p.negative_weight / query.bad_.size();
				for (int bad : query.bad_)
					evaluateNode(false, bad, result.getD(bad, 0.0), weight, query, eval);
			}
		}
		
	}
	
	public void evaluateTest(Query query, IREvaluation ir_eval){
		query.result = query.features_.weightedSum(model_.path_weights_);
	  model_.walker_.applyFilters(query, query.result);

		if (query.good_.size() == 0) return;
			
		IREvaluation eval = new IREvaluation();
		eval.evaluate(query.result, query.good_);
		ir_eval.plusObjOn(eval);
	}

	public EvalLBFGS evaluate(double[] x, boolean bInduce) {
		
		if (x.length == 0) model_.initWeights();
		else model_.setParameters(x);
		
		evaluateTrain(selected_queries_);
		EvalLBFGS eval = new EvalLBFGS(model_.param_weights_, opt_eval_.loss_,
				model_.getGradient(opt_eval_), p.L1, p.L2);
//		eval.scaleDown(1.0 / pool_.queries.size());
		
		System.out.printf(" %s %s\n", FSystem.printMemoryUsageM(), eval.toStringE(5));
		return eval;
	}


	//* given a set of path weights
	//* calculate the loss and derivatives and store then in eva
	protected void evaluateTrain(VectorI ids) {
		VectorX<Query> queries = 	this.queries_.sub(ids);
		int num_features = model_.path_weights_.size();
		opt_eval_.clear(num_features);
		if (queries.size() ==0) return;

		if (p.multi_threaded) {
			for (edu.cmu.lti.util.system.ThreadPool.WorkThread t : pool_.threads_) 
				((PRAThreadPool.WorkThread) t).opt_eval_.clear(num_features);

			pool_.runTask(ThreadTask.EvaluateTrain, queries);

			for (edu.cmu.lti.util.system.ThreadPool.WorkThread t : pool_.threads_) {
				opt_eval_.plusOn(((PRAThreadPool.WorkThread) t).opt_eval_);
			}

		} else {
			for (Query query : queries) evaluateTrain(query, opt_eval_);
		}
    opt_eval_.multiplyOn(1.0 / queries.size());
		return;
	}
	
	protected void evaluateTest(VectorI ids) {
		VectorX<Query> queries = 	this.queries_.sub(ids);
		ir_eval_.clear();
		if (queries.size() ==0) return;
		if (p.multi_threaded) {
			pool_.runTask(ThreadTask.EvaluateTest, queries);

			for (edu.cmu.lti.util.system.ThreadPool.WorkThread t : pool_.threads_) {
				ir_eval_.plusObjOn(((PRAThreadPool.WorkThread) t).ir_eval_);
				((PRAThreadPool.WorkThread) t).ir_eval_.clear();
			}

		} else {
			for (Query query : queries) 	evaluateTest(query, ir_eval_);
		}
    ir_eval_.multiplyOn(1.0 / queries.size());		
		return;
	}

	public String test(VectorI ids, String code) {
		System.out.println("\ntesting #Q=" + ids.size());
		evaluateTest(ids);
		evaluateTrain(ids);
		
		if (p.print_predictions) {
			BufferedWriter prediction = FFile.newWriter(out_folder_ + code +".prediction");
			BufferedWriter reason = p.inspect_data?
					FFile.newWriter(out_folder_ + code +".prediction.reason") : null;
					
			for (Query query: queries_.sub(ids)) 
				model_.predictQuery(query, prediction, reason);
			FFile.close(prediction);
			if (reason!=null) FFile.close(reason);
		}
		
		String rlt = "\t" + opt_eval_.loss_ + "\t" + ir_eval_.print();
		System.out.println(getTestTitle());
		System.out.println(rlt);
		return rlt;
	}

	public static String getTestTitle() {
		return "\tLoss\t" + IREvaluation.title();
	}

	public void retrain(String model_file, String sample_file) { 
		loadQueries(sample_file);
		String code = "retrain";
		
		selected_queries_ = VectorI.seq(this.queries_.size());
		model_.loadModel(model_file);
		
		RESULT += "\t" + trainCore(selected_queries_, code);
		TITLE += getTrainTitle();		
		
		model_.saveModel(out_folder_ + code 
				+ FString.getLastSection(model_file, '/'), false);
		model_.saveModel(model_file + ".retrain", false);
	}
	
	public String trainCore(VectorI ids, String code) { //VectorX<Query> queries, 
		String prep = prepareFeatures(code);
		model_.initWeights();
		if (p.loss_mode.equals(LossMode.none)) return prep;
		
		Learner.println("\nTraining #Q=" + selected_queries_.size());

		int it = 0;
		
		if (ids.size() >= 0 && model_.path_weights_.size() !=0) 
				it += lbfgs_.minimize(this, null, null, false);

		return prep 	+ "\t"  + opt_eval_.loss_ + "\t" + model_.getWeightCount() + "\t" + it;

	}
	
	public String train(VectorI ids, String code) { //VectorX<Query> queries, 
		selected_queries_ = ids;
		explorePaths(ids, code);
		String rlt= trainCore(ids, code);
		
		model_.saveModel(out_folder_ + code + ".model", true);
		model_.saveModel(out_folder_ + code + ".model.full", false);
		return rlt;
	}
	
	
	public String getTrainTitle() {
		if (p.loss_mode.equals(LossMode.none))//p.rank_mode.equals(RankMode.Path))
			return "\t"+ Query.getFeatureStatTitle() ;
		else 
			return "\t"+ Query.getFeatureStatTitle() + "\tLoss\t#aF\t#F\tL1\tIT";
	}
	
	@Override public void dispose() {
		if (p.multi_threaded) pool_.killThreads();
	}


	
	//String fields,
	void predictQuery(Query query, VectorS predictions,	VectorS reasons) {
		StringBuffer result = new StringBuffer();
		StringBuffer reason = new StringBuffer();
		//MapID dist = model_.predict(query);
		model_.predictQuery(query, result, reason);
		
		predictions.add(result.toString());
		reasons.add(reason.toString());		
	}
	
	@Override public void predict(String query_file) {

		System.out.println("predict() " + query_file);
	
		String output =	out_folder_ + "predictions";
		if (p.target_relation != null) {
			FFile.mkdirs(p.prediction_folder);
			output = p.prediction_folder + p.target_relation;
		}

		BufferedWriter prediction_writer = FFile.newWriter(output);
		BufferedWriter reason_writer = FFile.newWriter(output + ".reasons");
		
		boolean cached = false;
		if (p.multi_threaded) {
			
			if (cached) {
				pool_.setTask(ThreadTask.CachedPredict);
			}
			else {
				pool_.setTask(ThreadTask.Predict);
				pool_.reason_writer_ = reason_writer;
				pool_.prediction_writer_ = prediction_writer;
			}
			
			for (String line : FFile.enuLines(query_file))	pool_.addJob(line);
			pool_.waitJobs();
			
			if (cached) {
				for (edu.cmu.lti.util.system.ThreadPool.WorkThread t : pool_.threads_) {
					PRAThreadPool.WorkThread thread = ((PRAThreadPool.WorkThread) t);
					for (String prediction: thread.predictions_)
						FFile.writeln(prediction_writer, prediction);
					for (String reason: thread.reasons_)
						FFile.writeln(reason_writer, reason);
				}
			}
		}
		else {
			Query query = new Query();
			for (String line : FFile.enuLines(query_file)) {
				model_.walker_.parseQuery(line, query);
				model_.predictQuery(query, prediction_writer, reason_writer);
			}
		}
		System.out.println(FFile.num_lines_ + " predicted");
		FFile.close(prediction_writer);
		FFile.close(reason_writer);
	}

	@Override public void test(String sample_file) {
		this.loadQueries(sample_file);
		String prep = prepareFeatures(".test");
		
		RESULT += "\t"	+  prep + test(VectorI.seq(queries_.size()), "test") ;
		TITLE += "\t"	+  Query.getFeatureStatTitle() + getTestTitle();
		
		if (p.print_predictions)
		if (p.collect_predictions) 
			FFile.copyFile(out_folder_ + "test.prediction", 
				p.prediction_folder + p.target_relation);

		if (p.target_relation!=null){
			TITLE += getMacroEvalTitle();
			RESULT += getMacroEval();
		}

	}

	@Override public boolean loadModel(String model) {
		model_.loadModel(model);
		model_.loadOfflineFeatures();
		return true;
	}


	@Override public void train(String sample_file) {
		loadQueries(sample_file);
		String result = train(VectorI.seq(this.queries_.size()), "train");

		if (p.collect_models) 
			FFile.copyFile(out_folder_ + "train.model", 
							p.model_folder + p.target_relation);
		
		RESULT += "\t" + result;
		TITLE += getTrainTitle();
//		copyModel(output_folder_ + "model.train");
	}

	void collectCVPredictions(int num_folds){		
		//VectorX<BufferedReader> readers = new VectorX<BufferedReader>(BufferedReader.class);
		VecVecS fold_results = new VecVecS();
		for (int i = 0; i < num_folds; ++i) {
			String code = "cv" + i + "-" + num_folds;
			//readers.add(FFile.newReader());
			fold_results.add(VectorS.fromFile(out_folder_ + code + ".prediction"));
		}		
		VectorS results = new VectorS();
		for (int i=0; i<this.queries_.size(); ++i) {
			int fold = i % num_folds;
			int id = i/num_folds;
			results.add(fold_results.get(fold).get(id));
		}
		results.save(this.out_folder_ + "prediction");
		//for (BufferedReader reader: readers) FFile.close(reader);
		return;
	}

	//Leave one out CV inside application to save overhead cost
	@Override public void selfCV(String sample_file, int num_folds) {
		loadQueries(sample_file);
		
		DataSplit split = new DataSplit(num_folds, this.queries_.size());
		
		VectorS results = new VectorS();
		MomentumVec momentum = new MomentumVec();
		VectorD model_avg = new VectorD();

		for (int i = 0; i < num_folds; ++i) {
			String code = "cv" + i + "-" + num_folds;
			Learner.println("\n\n" + code);
			
			String result = train(split.fold_train_ids.get(i), code);
			model_avg.plusOnE(model_.param_weights_);
			
			result += test(split.fold_test_ids.get(i), code);
			results.add("F" + i + "\t" + result);
			momentum.addInstance(VectorD.fromLine(result));
		}
		
		if (p.print_predictions)
			collectCVPredictions(num_folds);
		if (p.print_predictions)
		if (p.collect_predictions) 
			FFile.copyFile(out_folder_ + "prediction", 	p.prediction_folder + p.target_relation);

		double scale = 1.0 / num_folds;
		model_avg.multiplyOn(scale);

		model_.setParameters(model_avg.toDoubleArray());
		model_.saveModel(this.out_folder_ + "model.avg.full", false);
		model_.saveModel(this.out_folder_ + "model.avg", true);
		if (p.collect_models) 
			FFile.copyFile(out_folder_ + "model.avg", 
				p.model_folder + p.target_relation);

		results.save(out_folder_ + "result.txt");
		momentum.finish();
		
		TITLE += getTrainTitle() + getTestTitle();
		RESULT += "\t" + momentum.means.join3f(); 
		
		if (p.target_relation!=null){
			TITLE += getMacroEvalTitle();
			RESULT += getMacroEval();
		}


//		copyModel(output_folder_ + "model.avg");
	}
	

	
	public String getMacroEval(){
		KeepTopK top= new KeepTopK(1000);
		for (Query query: this.queries_) {
			for (Map.Entry<Integer, Double> i: query.result.entrySet()) {
				int id = top.addValue(i.getValue());
				if (id>=0) 
					top.objects_.set(id, new Prediction(
							i.getValue(), 
							query.good_.contains(i.getKey()),
							query.blocked_node_,
							i.getKey()));
			}
		}
		VectorS predictions=new VectorS();
		double p1=0;
		double p10=0;
		double p100=0;
		double p1000=0;
		double num_correct=0;
		for (int i=0; i<1000; ++i) {
			if (i<top.size()) {
				Prediction pred = (Prediction) top.objects_.get(i);
				if (pred.label) ++num_correct;
				predictions.add(pred.print(this.graph_));
			}
			
			if (i==0) p1= num_correct;
			else if (i==9) p10= num_correct/10;
			else if (i==99) p100= num_correct/100;
			else if (i==999) p1000= num_correct/1000;
		}
		predictions.save(this.out_folder_ +CTag.top_predictions);
		return "\t" + p10+ "\t" + p100 + "\t" + p1000;
	}
	
	
	public static String getMacroEvalTitle(){
		return "\tp@10\tp@100\tp@1000";//\tp@1
	}
	
	
	@Override public void exp() {

	};

	// do random walks for a set of nodes
	public void walkSet(SetI set, MapIX<VecMapID> output) {
		if (p.multi_threaded) {
			pool_.setTask(ThreadTask.Walk);
			for (int src: set) pool_.addJob(src);
			pool_.waitJobs();
			output.putAll(pool_.walk_cache_);
		}
		else {
			for (int src: set) {
				Counter.count50.stepDot();
				output.put(src, model_.trees_.get(0).walk(null, src));
			}
		}
	}

	String setOutputFolder() {
		String feature_code = "";
		if (model_.p.bias) feature_code += "_b";

		String graph_code_ = FString.getSecondLastSection(p.graph_folder, "/");
		if (p.subgraph_store != null) {
			graph_code_ += "_" + FString.getSecondLastSection(p.subgraph_store, "/");
		}

		String model_code = null;
		if (super.param.task.equals(Task.test) || super.param.task.equals(Task.predict)) 
			model_code = FString	.getLastSection(super.param.model, '/');
		else 
			model_code = model_.p.model_code_;

		// A string describing how data is processed
		String process_code = model_code + "_" + feature_code + "/";

		super.param.generateCode();
		
		code_ = graph_code_ + "/";
		switch (super.param.task) {
		case test:
		case predict:
			code_ += super.param.test_data_code_ + "/" + process_code;
			break;
		default:
			code_ += super.param.train_data_code_ + "/" + process_code + p.obj_code_ + "/";
			break;
		}
		code_+= super.param.task_code_ +"_T"+p.num_threads + "_"+p.id + "/";
		out_folder_ = super.param.output_folder + code_ ;
		
		System.out.println("output_folder_=" + out_folder_);

		FFile.mkdirs(out_folder_);
		return code_;
	}
	public static void main(String[] args) {
		try {
			Param.overwriteFrom("conf");
			if (args.length > 0) {
				Param.overwrite(args[0]);
			}
			(new LearnerPRA(args.length > 0? args[0]:null)).run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
