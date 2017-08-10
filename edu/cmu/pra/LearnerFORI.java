package edu.cmu.pra;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.MapSD;
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
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.OptizationEval;
import edu.cmu.lti.algorithm.structure.KeepTopK;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.run.DataSplit;
import edu.cmu.lti.util.run.Learner;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.CTag.LossMode;
import edu.cmu.pra.CTag.Prediction;
import edu.cmu.pra.CTag.ThreadTask;
import edu.cmu.pra.fori.Explore;
import edu.cmu.pra.fori.FORIModel;
import edu.cmu.pra.fori.FORIQuery;
import edu.cmu.pra.fori.FeatureSetValues;
import edu.cmu.pra.fori.Induce;
import edu.cmu.pra.fori.InductionStats;
import edu.cmu.pra.fori.Objective;
import edu.cmu.pra.fori.FORIModel.DoubleWrapper;
import edu.cmu.pra.fori.FeatureStat.PathStat;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.model.Query;

// first-order random walk model
// The power of duplication: how a cell splits and becomes two independent entities
public class LearnerFORI extends Learner {

	public FORIThreadPool pool_ = new FORIThreadPool();
	public FORIModel model_ = null;
	public OWLBFGS lbfgs_ = new OWLBFGS();
	public Graph graph_;// = new Graph();
	

	public VectorX<FORIQuery> queries_ = new VectorX<FORIQuery>(FORIQuery.class);
	// these random walk results are useful for feature induction
	public VectorX<VecMapID> reversed_random_walks_ = new VectorX<VecMapID>(
			VecMapID.class);

	//private VectorI selected_ids_ = null;//queries in the loss function
	public VectorX<FORIQuery> selected_queries_ = new VectorX<FORIQuery>(FORIQuery.class);

	public Param p = null;// new Param();

	public Explore explore_;
	public Induce induce_;
	public Objective objective_;
//public FORIQuery mean_query = new FORIQuery();
	public GraphWalker walker_;
	
	public LearnerFORI(String args) {
		this(null, args);
	}

	public LearnerFORI(GraphWalker walker, String args) {
		super(LearnerFORI.class, args);
		p = new Param();
		if (walker==null) {
			graph_ = new Graph();
			graph_.loadGraph(p.graph_folder);
			walker_ = new GraphWalker(graph_);
		}
		else {
			walker_ = walker;
			graph_ = (Graph) walker_.graph_;
			
		}
		init();
	}

	void init() {
		model_ = new FORIModel(walker_);
		explore_ = new Explore(this);
		induce_ = new Induce(this);
		objective_ = new Objective(this);


		if (p.target_relation != null) {
			super.param.train_samples = super.param.train_samples.replaceFirst(
					"<target_relation>", model_.walker_.p.target_relation);
			super.param.test_samples = super.param.test_samples.replaceFirst(
					"<target_relation>", model_.walker_.p.target_relation);
			super.param.model = super.param.model.replaceFirst("<target_relation>",
					model_.walker_.p.target_relation);
		}

		setOutputFolder();
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
		public boolean load_cache;
		public boolean inspect_data;
		public boolean inspect_query;
		public boolean print_node_pairs;
		
		public boolean multi_threaded;
		public int num_threads = 1;
//		public String explore_file;

		public String id;
		public String graph_folder;
		public String subgraph_store;
		public String target_relation;

		public String prediction_folder;
		public String model_folder;
		public boolean per_query_inspection=false;		
		public int max_epoch;
		public boolean reset_weights=true;
		public boolean print_predictions;

		public void parse() {
			print_predictions = getBoolean("print_predictions", false);
			
			print_node_pairs = getBoolean("print_node_pairs", false);
			per_query_inspection = getBoolean("per_query_inspection", false);
			inspect_data = getBoolean("inspect_data", false);
			inspect_query = getBoolean("inspect_query", false);

			load_cache = getBoolean("load_cache", true);


			num_threads = getInt("num_threads", 1);
			multi_threaded = num_threads > 1;

			id = getString("id", "a");
			target_relation = getString("target_relation", null);

			graph_folder = getFolder("graph_folder", null);
			prediction_folder = getFolder("prediction_folder", null);
			model_folder = getFolder("model_folder", null);

			subgraph_store = getString("subgraph_store", null);

			
			max_epoch = getInt("max_epoch", 10);
			reset_weights = getBoolean("reset_weights", true);

			code_ = "_e" + max_epoch;
			if(reset_weights) code_ += "r";

		}
		public String code_;

	}

	public class FORIThreadPool extends edu.cmu.lti.util.system.ThreadPool {
		private ThreadTask thread_task_ = ThreadTask.none;
		protected BufferedWriter prediction_writer_ = null;
		protected BufferedWriter reason_writer_ = null;

		public void runTask(ThreadTask thread_task, Collection<FORIQuery> queries) {
			Counter.count50.clear();
			thread_task_ = thread_task;
			for (FORIQuery query : queries)		pool_.addJob(query);
			pool_.waitJobs();
		}

		public void setTask(ThreadTask thread_task) {
			thread_task_ = thread_task;
		}

		public void addJob(FORIQuery query) {
			super.addJob(new Job(-1, null, query));
		}
		public MapIX<VecMapID> walk_cache_ = new MapIX<VecMapID>(VecMapID.class);

		public class WorkThread extends
				edu.cmu.lti.util.system.ThreadPool.WorkThread {

			private FORIQuery query_ = new FORIQuery(); // TODO: remove this?

			public OptizationEval opt_eval_ = new OptizationEval(); // for optimization
			public IREvaluation ir_eval_ = new IREvaluation(); // for testing

			public VectorS reasons_ = new VectorS(); // for predictions
			public VectorS predictions_ = new VectorS(); // for predictions

			public PathStat path_stat_ = new PathStat(); // for path exploration

			public InductionStats gradients_ = new InductionStats(); // for induction
			public FeatureSetValues means_ = new FeatureSetValues(); // for induction

			public WorkThread(int id) {
				super(id);
			}

			@Override public void workOnJob(edu.cmu.lti.util.system.ThreadPool.Job job) {
				//System.out.println("iTh="+t.id+" iJob="+job.id);
				switch (thread_task_) {
				case RandomWalk:
					updateFeatures((FORIQuery) job.data_, null, means_);
					break;

				case EvaluateTrain:
					objective_.evaluateTrain((FORIQuery) job.data_, opt_eval_);
					break;
				case EvaluateTest:
					evaluateTest((FORIQuery) job.data_,ir_eval_);
					break;
				case CachedPredict:
					model_.walker_.parseQuery(job.key_, query_);
					predictQuery(query_, predictions_, reasons_);
					break;

				case Predict:
					model_.walker_.parseQuery(job.key_, query_);
					predictQuery(query_, prediction_writer_, reason_writer_);
					break;
				case Induction:
					induce_.induce((FORIQuery) job.data_, gradients_, null);
					break;
				case ExplorePaths:
					explore_.exploreAQuery((FORIQuery) job.data_, null, path_stat_);	
					break;
				default:
					FSystem.die("ThreadPool.thread_task_ is not set");
					break;
				}

			}
		}

		@Override public edu.cmu.lti.util.system.ThreadPool.WorkThread 
		newWorkThread(	int i) {
			return new WorkThread(i);
		}
	}
	
	public void updateFeatures(FORIQuery query, 
			BufferedWriter inspect, FeatureSetValues means) {
		
		model_.updateFeatures(query, inspect);
		objective_.updateNegativeSamples(query);
		
		if (inspect!=null) FFile.writeln(inspect, "\nbad=[" + query.bad_ + "]");

		return;
	}



	public int loadQueries(String sample_file) {
		//if (queries_.size() != 0) return queries_.size();

		System.out.println("loadQuery()" + sample_file);
		//queries_.clear();
		for (VectorS line : FFile.enuRows(sample_file, "\t")) {
			FORIQuery query = model_.parseQuery(line);
			if (query!=null)	queries_.add(query);
		}
		System.out.println("done loading |queries|=" + queries_.size() + "\n");
		
		TITLE += "\t#Q";//\ttExp\tmExp";
		RESULT += String.format("\t"+queries_.size());
		//"\t%d\t%f\t%d", queries_.size(), watch.getSec(),	FSystem.memoryUsed());


		return queries_.size();
	}

	// an average feature values for all queries (both train and test)
	FeatureSetValues means_ = new FeatureSetValues(); // average signals
	
	private void updateFeatures(boolean train, String id) {
		means_.clear();
		System.out.println("generateData(), #Q=" + queries_.size());


		if (p.multi_threaded) {
			pool_.runTask(ThreadTask.RandomWalk, queries_);
			
			for (edu.cmu.lti.util.system.ThreadPool.WorkThread i : pool_.threads_) {
				FORIThreadPool.WorkThread t = (FORIThreadPool.WorkThread) i;
				means_.plusOn(t.means_);
				t.means_.clear();
			}
		} else {
			BufferedWriter inspectQ = 	p.per_query_inspection?
					FFile.newWriter(out_folder_ + id	+ ".data.Q"):null;
					
			for (FORIQuery query : queries_) 	updateFeatures(query, inspectQ, means_);
			if (inspectQ != null)FFile.close(inspectQ);
		}
		
		this.induce_.updateMeanQuery();
		System.out.println(means_.toString());
		means_.multiplyOn(1.0 / queries_.size());
		
		if (p.inspect_data) {
			BufferedWriter inspect = FFile.newWriter(out_folder_ + id	+ ".data");
			means_.print(inspect, model_);
			FFile.close(inspect);
		}
		//if (p.print_node_pairs) printNodePairs(id+".pairs");

		System.out.println(FSystem.memoryUsage());
	}


	
	public void evaluateTest(FORIQuery query, IREvaluation ir_eval) {
		DoubleWrapper bias = new DoubleWrapper();
		query.result = model_.predict(query, bias);

		if (query.good_.size() == 0) return;
		IREvaluation e= new IREvaluation();
		e.evaluate(query.result, query.good_);
		ir_eval.plusObjOn(e);
		
		return;
	}


	
	protected void evaluateTest(VectorX<FORIQuery> queries, IREvaluation ir_eval_) {

		if (ir_eval_!=null) ir_eval_.clear();
		if (queries.size() == 0) return;

		if (p.multi_threaded) {
			for (edu.cmu.lti.util.system.ThreadPool.WorkThread i : pool_.threads_) {
				FORIThreadPool.WorkThread t = (FORIThreadPool.WorkThread) i;
				t.ir_eval_.clear();
			}
			pool_.runTask(ThreadTask.EvaluateTest, queries);

			for (edu.cmu.lti.util.system.ThreadPool.WorkThread i : pool_.threads_) {
				FORIThreadPool.WorkThread t = (FORIThreadPool.WorkThread) i;
				ir_eval_.plusObjOn(t.ir_eval_);
				t.ir_eval_.clear();
			}
		} else 
			for (FORIQuery query : queries) evaluateTest(query, ir_eval_);
		
		if (ir_eval_!=null) ir_eval_.multiplyOn(1.0 / queries.size());
		return;
	}

	public String test(VectorI ids, String code) {
		System.out.println("\ntesting #Q=" + ids.size());
		OptizationEval opt_eval_ = new OptizationEval();
		IREvaluation ir_eval_ = new IREvaluation();// = q.eval(rlt);

		objective_.evaluateTrain(this.queries_.sub(ids),  opt_eval_);
		evaluateTest(this.queries_.sub(ids), ir_eval_);

		String result = opt_eval_.print()		+ "\t" + ir_eval_.print();

		System.out.println(getTestTitle());
		System.out.println(result);

		if (p.print_predictions) {
			BufferedWriter prediction = FFile.newWriter(out_folder_ + code +".prediction");
			BufferedWriter reason = p.inspect_data?
					FFile.newWriter(out_folder_ + code +".prediction.reason") : null;
					
//			for (FORIQuery query: queries_.sub(ids)) 
//				model_.predict(query, prediction, reason);
			FFile.close(prediction);
			if (reason!=null) FFile.close(reason);
		}
		if (code != null) 
			FFile.appendToFile(
					code+ "\t" +  printQueryStatistics() + "\t" +  result + "\n", 
					out_folder_ + test_log_file);
		
		return result;
	}

	public static String getTestTitle() {
		return "\tLoss\t" + IREvaluation.title();
	}
	

	//useful for testing/training FOIL
	private void printNodePairs(VectorI query_ids, String name){
		if (query_ids==null) return;
		SetS pairs= new SetS();		
		
		if (p.target_relation!=null) {
			//for (Query query: queries_){
			for (int id : query_ids) {
				Query query= queries_.get(id);
				String N0=walker_.getNodeNameN(query.blocked_node_);
				for (int n : query.good_)	
					pairs.add(N0+","+walker_.getNodeNameN(n) +": +");
				for (int n : query.bad_)	
					pairs.add(N0+","+walker_.getNodeNameN(n) +": -");
			}		
		}
		else {
//			for (Query query: queries_){
			for (int id : query_ids) {
				Query query= queries_.get(id);
				for (int n : query.good_)		
					pairs.add(walker_.getNodeNameN(n) +": +");
				for (int n : query.bad_)		
					pairs.add(walker_.getNodeNameN(n) +": -");
			}		
		}
		pairs.save(out_folder_ + name);
	}

	
	public String train(VectorI train_ids, String code, VectorI test_ids) { //VectorX<Query> queries,
		Learner.println("\nTraining #Q=" + train_ids.size());

		if (objective_.p.loss_mode.equals(LossMode.none)) return "";
		selected_queries_ = queries_.sub(train_ids);
		for (FORIQuery query : this.queries_)		query.clearFeatures();
		System.gc();
		FSystem.printMemoryTime();
		
		double exp_time = explore_.exploreQueries(selected_queries_, code);//selected_queries_

		StopWatch watch = new StopWatch();
		int it = 0;
		lbfgs_.clearHistory();

		FFile.appendToFile("\n["+code +"]\n" , out_folder_ + model_log_file);
		FFile.appendToFile("\n["+code +"]\n" , out_folder_ + test_log_file);

		int epoch = 0;
		while (true) {
			String epoch_code = code + ".e" + epoch;
			updateFeatures(true, epoch_code);
			if (p.print_node_pairs) {
				printNodePairs(train_ids, epoch_code+".pairs.train");
				printNodePairs(test_ids, epoch_code+".pairs.test");
			}

			FFile.appendToFile("\n" +  epoch_code + "\n" + 
					model_.toString()+"\n", out_folder_ + model_log_file);
			
			it += lbfgs_.minimize(objective_, null, 
					p.reset_weights?null:model_.getParameters(), false);
			
			if (test_ids!=null)  test(test_ids, epoch_code);
			++epoch;
			if (epoch == p.max_epoch) break;
			if (!induce_.p.induction) break;
			if (!induce_.induce(selected_queries_, epoch_code)) break;
		}

		model_.saveModel(out_folder_ + code + ".model");
		watch.printTime("training");
		
		OptizationEval opt_eval_ = new OptizationEval();
		objective_.evaluateTrain(this.selected_queries_, opt_eval_);	// TODO: why recaculate?

		return exp_time+ "\t" + FSystem.memoryUsedM() 
			+ "\t" + printQueryStatistics() + "\t" + opt_eval_.print()
			+ "\t" + model_.getWeightCount() + "\t" + epoch + "\t" + it;
	}
	
	private String printQueryStatistics(){
		VectorX<Query> queries = new VectorX<Query>(Query.class);
		queries.addAll(queries_);
		return  Query.printQueryStatistics(queries);
	}
	
	public static String getTrainTitle() {
		//if (p.loss_mode.equals(LossMode.none)) return "";
		return "\tTime\tMem" + getQStatTitle() + "\tLoss\t#aF\t#F\tL1\tEpoch\tIT";
	}
	public static String getQStatTitle() {
		return "\tRcTg\t#hit\t#good\t#bad";
	}


	@Override public void dispose() {
		if (p.multi_threaded) pool_.killThreads();
	}

	void predictQuery(FORIQuery query, BufferedWriter result_writer,
			BufferedWriter reason_writer) {
		StringBuffer result = new StringBuffer();
		StringBuffer reason = new StringBuffer();
		model_.predict(query, result, reason);

		synchronized (result_writer) {
			FFile.writeln(result_writer, result.toString());
			FFile.writeln(reason_writer, reason.toString());
		}
	}

	void predictQuery(FORIQuery query, VectorS predictions, VectorS reasons) {
		StringBuffer result = new StringBuffer();
		StringBuffer reason = new StringBuffer();
		model_.predict(query, result, reason);//FString.split(fields, "\t")

		predictions.add(result.toString());
		reasons.add(reason.toString());
	}

	@Override public void predict(String query_file) {

		System.out.println("predict() " + query_file);

		String output = out_folder_ + "predictions";
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
			} else {
				pool_.setTask(ThreadTask.Predict);
				pool_.reason_writer_ = reason_writer;
				pool_.prediction_writer_ = prediction_writer;
			}

			for (String line : FFile.enuLines(query_file))
				pool_.addJob(line);
			pool_.waitJobs();

			if (cached) {
				for (edu.cmu.lti.util.system.ThreadPool.WorkThread t : pool_.threads_) {
					FORIThreadPool.WorkThread thread = ((FORIThreadPool.WorkThread) t);
					for (String prediction : thread.predictions_)
						FFile.writeln(prediction_writer, prediction);
					for (String reason : thread.reasons_)
						FFile.writeln(reason_writer, reason);
				}
			}
		} else {
			FORIQuery query = new FORIQuery();
			for (String line : FFile.enuLines(query_file)) {
				model_.walker_.parseQuery(line, query);
				predictQuery(query, prediction_writer, reason_writer);
			}
		}
		System.out.println(FFile.num_lines_ + " predicted");
		FFile.close(prediction_writer);
		FFile.close(reason_writer);
	}

	public static final String test_log_file = "tests.log";
	public static final String model_log_file = "model.log";
	
	@Override public void test(String sample_file) {
		FFile.appendToFile("\ntest\n"+getTestTitle()+"\n", out_folder_ + test_log_file);

		this.loadQueries(sample_file);
		
		RESULT += "\t" + test(VectorI.seq(queries_.size()), "");
		TITLE += getTestTitle() ;
		
		if (p.target_relation!=null){
			TITLE += getMacroEvalTitle();
			RESULT += getMacroEval();
		}

	}

	@Override public boolean loadModel(String model) {
		model_.loadModel(model);
		return true;
	}

	@Override public void train(String sample_file) {
		int num_samples = loadQueries(sample_file);
		String result = train(VectorI.seq(num_samples), "train", null);

		RESULT += "\t" + result;
		TITLE += getTrainTitle();
//		copyModel(output_folder_	+ "train.model");
	}

	void collectCVPredictions(int num_folds){		
		//VectorX<BufferedReader> readers = new VectorX<BufferedReader>(BufferedReader.class);
		VecVecS fold_results = new VecVecS();
		for (int i = 0; i < num_folds; ++i) {
			String code = "cv" + i + "-" + num_folds;
			//readers.add(FFile.newReader());
			fold_results.add(VectorS.fromFile(out_folder_ + code + "prediction"));
		}		
		VectorS results = new VectorS();
		for (int i=0; i<this.queries_.size(); ++i) {
			int fold = i % num_folds;
			int id = i/num_folds;
			results.add(fold_results.get(fold).get(id));
		}
		results.save("prediction");
		//for (BufferedReader reader: readers) FFile.close(reader);
		return;
	}
	//Leave one out CV inside application to save overhead cost
	@Override public void selfCV(String sample_file, int num_folds) {

		FFile.appendToFile("\nselfCV"+ getQStatTitle() + getTestTitle() + "\n", 
				out_folder_ + test_log_file);

		int num_samples = loadQueries(sample_file);

		DataSplit split = new DataSplit(num_folds, num_samples);
		VectorS results = new VectorS();
		MomentumVec momentum = new MomentumVec();
		MapSD model_avg = new MapSD();

		
		for (int i = 0; i < num_folds; ++i) {
			Learner.println("\n\nFold=" + i);
			String code = "cv" + i + "-" + num_folds;

			String result = train(split.fold_train_ids.get(i), code, split.fold_test_ids.get(i));

			model_avg.plusOn(model_.param_names_, model_.param_weights_);

			result += "\t" + test(split.fold_test_ids.get(i), code);
			results.add(code + "\t" + result);
			momentum.addInstance(VectorD.fromLine(result));
		}
//		collectCVPredictions(num_folds);
		
		model_avg.multiplyOn(1.0 / num_folds);
		model_avg.save(out_folder_ + "model.avg", "\t", null, true);

		results.save(out_folder_ + "result.txt");
		momentum.finish();

		RESULT += "\t" + momentum.means.join3f();
		TITLE += getTrainTitle() + getTestTitle();
		if (p.target_relation!=null){
			TITLE += getMacroEvalTitle();
			RESULT += getMacroEval();
		}

//		copyModel(output_folder_ + "model.avg");
	}
	
	public void copyModel(String raw) {
		FFile.mkdirs(p.model_folder);
		FFile.copyFile(raw,	p.model_folder + p.target_relation);
	}
	
	@Override public void exp() {

	};

	void setOutputFolder() {

		String graph_code_ = FString.getSecondLastSection(p.graph_folder, "/");
		if (p.subgraph_store != null) {
			graph_code_ += "_" + FString.getSecondLastSection(p.subgraph_store, "/");
		}

		String model_code = null;
		if (super.param.task.equals(Task.test)
				|| super.param.task.equals(Task.predict)) model_code = FString
				.getLastSection(super.param.model, '/');
		else model_code = explore_.p.code_	 + p.code_	+ "/" + induce_.p.code_;
		//model_.p.model_code_

		// A string describing how data is processed
		String process_code = model_code + model_.walker_.p.code_ ;

		super.param.generateCode();

		code_ = graph_code_ + "/";
		switch (super.param.task) {
		case test:
		case predict:
			code_ += super.param.test_data_code_ + "/" + process_code + "/";
			break;
		default:
			code_ += super.param.train_data_code_ + "/" + process_code + "/"
					+ objective_.p.code_ + "/";
			break;
		}
		code_ += super.param.task_code_ +"_T"+p.num_threads + "_"+p.id + "/";
		out_folder_ = super.param.output_folder + code_; 
				
		System.out.println("output_folder_=" + out_folder_);

		FFile.mkdirs(out_folder_);

		return;
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
		predictions.save(this.out_folder_ + CTag.top_predictions);
		return "\t" + p10+ "\t" + p100 + "\t" + p1000;
	}
	
	public static String getMacroEvalTitle(){
		return "\tp@10\tp@100\tp@1000";//\tp@1
	}
	
	public void retrain(String model_file, String sample_file) { 
		loadQueries(sample_file);
		String code = "retrain";
//		
//		selected_queries_ = VectorI.seq(this.queries_.size());
//		model_.loadModel(model_file);
//		
//		RESULT += "\t" + trainCore(selected_queries_, code);
//		TITLE += getTrainTitle();		
	}
	public static void main(String[] args) {
		try {
			Param.overwriteFrom("conf");
			if (args.length > 0) Param.overwrite(args[0]);

			(new LearnerFORI(args.length > 0 ? args[0] : null)).run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}