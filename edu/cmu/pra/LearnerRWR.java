package edu.cmu.pra;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Collection;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.container.VecVecS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.ir.eva.IREvaluation;
import edu.cmu.lti.algorithm.structure.KeepTopK;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.run.Learner;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.CTag.Prediction;
import edu.cmu.pra.CTag.ThreadTask;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.model.Query;
import edu.cmu.pra.model.RWRModel;

public class LearnerRWR extends Learner{

	RWRThreadPool pool_ = new RWRThreadPool();

//	public OptizationEval opt_eval_ = new OptizationEval();
	public IREvaluation ir_eval_ = new IREvaluation();// = q.eval(rlt);
//	public PRAModel model_ = null;
//	public OWLBFGS lbfgs_ = new OWLBFGS();
	
	//public GraphWalker walker_;
	public RWRModel model_;
	public Graph graph_;// = new Graph();
	public GraphWalker walker_;
	public VectorX<Query> queries_ = new VectorX<Query>(Query.class);
	
	public VectorI selected_queries_ = new VectorI();

	public Param p = null;// new Param();

	public LearnerRWR(String args) {
		this(null, args);
	}
	
	public LearnerRWR(GraphWalker walker, String args) {
		this(walker, args, null);
	}
	
	public LearnerRWR(GraphWalker walker, String args, String output_folder) {
		super(LearnerPRA.class, args);
		
		p = new Param();
		
		if (walker==null) {
			graph_ = new Graph();
			graph_.loadGraph(p.graph_folder);
			walker_ = new GraphWalker(graph_);
		}
		else	{
			graph_ = (Graph)walker.graph_;
			walker_ = walker;
		}
		model_ = new RWRModel(walker_);
		
		init();
		
		if (output_folder==null)	setOutputFolder();
		else this.out_folder_ = output_folder;
		
	}

	void init() {

		if (p.target_relation != null) {
			super.param.train_samples = super.param.train_samples.replaceFirst(
					"<target_relation>", p.target_relation);
			super.param.test_samples = super.param.test_samples.replaceFirst(
					"<target_relation>", p.target_relation);
			super.param.model = super.param.model.replaceFirst(
					"<target_relation>", p.target_relation);
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


		public String id;
		public String graph_folder;
		public String subgraph_store;
		public String target_relation;
		public boolean multi_threaded;
		public int num_threads = 1;
		public boolean inspect_data;
		public boolean print_predictions;
		public double  enOverflow;
		
		public boolean collect_predictions=false; 
		public String prediction_folder;

		public void parse() {
			id = getString("id", "a");
			target_relation = getString("target_relation", null);
			
			graph_folder = getFolder("graph_folder", null);
			subgraph_store = getString("subgraph_store", null);
			
			num_threads = getInt("num_threads", 1);
			multi_threaded = num_threads > 1;
			inspect_data = getBoolean("inspect_data", false);
			print_predictions = getBoolean("print_predictions", false);
			collect_predictions = getBoolean("collect_predictions", false);
			prediction_folder= getString("prediction_folder", null);
			enOverflow = 10.0;
			
			if (print_predictions)
				if (this.collect_predictions) FFile.mkdirs(prediction_folder);
		//		if (this.collect_models)  FFile.mkdirs(model_folder);

		}
	}	

	class RWRThreadPool extends edu.cmu.lti.util.system.ThreadPool {

		// The task to be performed by the threads
		private ThreadTask thread_task_ = ThreadTask.none;
		
		
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
			
//			public OptizationEval opt_eval_ = new OptizationEval();	// for training
			public IREvaluation ir_eval_ = new IREvaluation();	// for validation
			
			public WorkThread(int id) { super(id); }		
			
			@Override public void workOnJob(edu.cmu.lti.util.system.ThreadPool.Job job) {
				//System.out.println("iTh="+t.id+" iJob="+job.id);
				switch(thread_task_) {
				case RandomWalk: 				
					prepareFeatures((Query)job.data_, null);
					break;
					
				case EvaluateTrain: 
//					evaluateTrain((Query)job.data_, opt_eval_);
					break;
				case EvaluateTest: 
					evaluateTest((Query)job.data_, ir_eval_);
					break;
					
				case Predict: {
//					Query query_ = new Query();	
//					parseQuery(job.key_, query_);
//					predictQuery(query_, prediction_writer_, reason_writer_);				
				}break;
				
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
		
		for (VectorS line : FFile.enuRows(sample_file, "\t")) {
			Query query = model_.walker_.parseQuery(line);
			if (query!=null)	queries_.add(query);
		}
		
		System.out.println("done loading |queries|=" + queries_.size() + "\n");
	}
	


	public void prepareFeatures(Query query, BufferedWriter inspect) {
		model_.getFeatures(query, inspect);
		
		MapID result = query.features_.sum();
		model_.walker_.applyFilters(query, result);
		query.hit_ = new SetI(result.sub(query.good_).keySet());
		
//		result.removeAll(query.good_);		
//		if (!model_.p.given_negative_samples)
//			selectNegativeSamples(result, query.bad_);
//
//
//		// this is the set that we care about
//		SetI selected = new SetI();
//		selected.addAll(query.good_);
//		selected.addAll(query.bad_);
//
//		query.features_sampled_.clear();
//		for (MapID feature : query.features_)
//			query.features_sampled_.add(feature.subSet(selected));

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
		}
		return String.format("%d\t%f\t%d\t%s", 
			queries_.size(), watch.getSec(), 
			FSystem.memoryUsedM(), Query.printQueryStatistics(queries_));
	}
	

	protected static String getFeatureStatTitle(){
		return "#Q\ttRW\tmRW\tRcTg\t#hit\t#good\t#bad";
	}

	protected double sigmoid(double score) {
		if (score > p.enOverflow) score = p.enOverflow;
		else if (score < -p.enOverflow) score = -p.enOverflow;
		double exp = Math.exp(score);
		return exp / (1 + exp);
	}

	
	public void evaluateTest(Query query, IREvaluation ir_eval){
		query.result = query.features_.sum();//weightedSum(model_.feature_weights_);
	  model_.walker_.applyFilters(query, query.result);

		if (query.good_.size() == 0) return;
			
		IREvaluation eval = new IREvaluation();
		eval.evaluate(query.result, query.good_);
		ir_eval.plusObjOn(eval);
	}

	protected void evaluateTest(VectorI ids) {
		VectorX<Query> queries = 	this.queries_.sub(ids);
		ir_eval_.clear();
		if (queries.size() ==0) return;
		if (p.multi_threaded) {
			pool_.runTask(ThreadTask.EvaluateTest, queries);

			for (edu.cmu.lti.util.system.ThreadPool.WorkThread t : pool_.threads_) {
				ir_eval_.plusObjOn(((RWRThreadPool.WorkThread) t).ir_eval_);
				((RWRThreadPool.WorkThread) t).ir_eval_.clear();
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
//		evaluateTrain(ids);
		
		if (p.print_predictions) {
			BufferedWriter prediction =p.print_predictions?
					FFile.newWriter(out_folder_ + code +".prediction") : null;
			BufferedWriter reason = p.print_predictions?
					FFile.newWriter(out_folder_ + code +".prediction.reason") : null;
					
			for (Query query: queries_.sub(ids)) 
				model_.predictQuery(query, prediction, reason);
			
			if (prediction!=null) FFile.close(prediction);
			if (reason!=null) FFile.close(reason);
		}
		
		String rlt = //"\t" + opt_eval_.loss_ +
			 "\t" + ir_eval_.print();
		System.out.println(getTestTitle());
		System.out.println(rlt);
		return rlt;
	}

	public static String getTestTitle() {
		return //"\tLoss\t" 
			"\t"+ IREvaluation.title();
	}

	public void retrain(String model_file, String sample_file) { 
		FSystem.dieNotImplemented();
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

//		System.out.println("predict() " + query_file);
//	
//		String output =	out_folder_ + "predictions";
//		if (p.target_relation != null) {
//			FFile.mkdirs(p.prediction_folder);
//			output = p.prediction_folder + p.target_relation;
//		}
//
//		BufferedWriter prediction_writer = FFile.newWriter(output);
//		BufferedWriter reason_writer = FFile.newWriter(output + ".reasons");
//		
//		boolean cached = false;
//		if (p.multi_threaded) {
//			
//			if (cached) {
//				pool_.setTask(ThreadTask.CachedPredict);
//			}
//			else {
//				pool_.setTask(ThreadTask.Predict);
//				pool_.reason_writer_ = reason_writer;
//				pool_.prediction_writer_ = prediction_writer;
//			}
//			
//			for (String line : FFile.enuLines(query_file))	pool_.addJob(line);
//			pool_.waitJobs();
//			
//			if (cached) {
//				for (edu.cmu.lti.util.system.ThreadPool.WorkThread t : pool_.threads_) {
//					PRAThreadPool.WorkThread thread = ((PRAThreadPool.WorkThread) t);
//					for (String prediction: thread.predictions_)
//						FFile.writeln(prediction_writer, prediction);
//					for (String reason: thread.reasons_)
//						FFile.writeln(reason_writer, reason);
//				}
//			}
//		}
//		else {
//			Query query = new Query();
//			for (String line : FFile.enuLines(query_file)) {
//				model_.walker_.parseQuery(line, query);
//				model_.predictQuery(query, prediction_writer, reason_writer);
//			}
//		}
//		System.out.println(FFile.num_lines_ + " predicted");
//		FFile.close(prediction_writer);
//		FFile.close(reason_writer);
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
//		model_.loadModel(model);
//		model_.loadOfflineFeatures();
		return true;
	}


	@Override public void train(String sample_file) {
//		loadQueries(sample_file);
//		String result = train(VectorI.seq(this.queries_.size()), "train");
//
//		if (p.collect_models) 
//			FFile.copyFile(out_folder_ + "train.model", 
//				p.model_folder + p.target_relation);
//		
//		RESULT += "\t" + result;
//		TITLE += getTrainTitle();
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
//		loadQueries(sample_file);
//		
//		DataSplit split = new DataSplit(num_folds, this.queries_.size());
//		
//		VectorS results = new VectorS();
//		MomentumVec momentum = new MomentumVec();
//		VectorD model_avg = new VectorD();
//
//		for (int i = 0; i < num_folds; ++i) {
//			String code = "cv" + i + "-" + num_folds;
//			Learner.println("\n\n" + code);
//			
//			String result = train(split.fold_train_ids.get(i), code);
//			model_avg.plusOnE(model_.param_weights_);
//			
//			result += test(split.fold_test_ids.get(i), code);
//			results.add("F" + i + "\t" + result);
//			momentum.addInstance(VectorD.fromLine(result));
//		}
//		
//		collectCVPredictions(num_folds);
//		if (p.collect_predictions) 
//			FFile.copyFile(out_folder_ + "prediction", 	p.prediction_folder + p.target_relation);
//
//		double scale = 1.0 / num_folds;
//		model_avg.multiplyOn(scale);
//
//		model_.setParameters(model_avg.toDoubleArray());
//		model_.saveModel(this.out_folder_ + "model.avg.full", false);
//		model_.saveModel(this.out_folder_ + "model.avg", true);
//		if (p.collect_models) 
//			FFile.copyFile(out_folder_ + "model.avg", 
//				p.model_folder + p.target_relation);
//
//		results.save(out_folder_ + "result.txt");
//		momentum.finish();
//		
//		TITLE += getTrainTitle() + getTestTitle();
//		RESULT += "\t" + momentum.means.join3f(); 
//		
//		if (p.target_relation!=null){
//			TITLE += getMacroEvalTitle();
//			RESULT += getMacroEval();
//		}

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

	

	String setOutputFolder() {
		String feature_code = "";

		String graph_code_ = FString.getSecondLastSection(p.graph_folder, "/");
		if (p.subgraph_store != null) {
			graph_code_ += "_" + FString.getSecondLastSection(p.subgraph_store, "/");
		}

		String model_code = null;
//		if (super.param.task.equals(Task.test) || super.param.task.equals(Task.predict)) 
//			model_code = FString	.getLastSection(super.param.model, '/');
//		else 
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
			code_ += super.param.train_data_code_ + "/" + process_code + "/";//+ p.obj_code_ 
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
			(new LearnerRWR(args.length > 0? args[0]:null)).run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}	
}
