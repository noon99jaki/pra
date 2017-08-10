package edu.cmu.lti.util.run;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.text.FString;

/**
 * Intended to be a generic experiment controller
 * @author nlao
 *
 */
public abstract class Learner {
	public String out_folder_;

	public String code_;

	public String TITLE, RESULT;

	
	public enum Task {
		train // train using trainFile
		, test //test using testFile
		, STT //train test by splitting trainFile
		, TT //train test using trainFile, testFile
		, CV //cross validation using trainFile
		, sCV //self cross validation using trainFile
		, LOO //leave-one-out cross validation using trainFile
		, sLOO //self leave-one-out cross validation using trainFile
		, predict // without knowing the ground truth
		, retrain // retrain an existing model
		, service, exp
		//experiment
	}

	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public String train_samples;
		public String test_samples;
		public String output_folder = null;

		public double train_rate; //for STT
		public int num_CV_folds; //for CV

		public Task task;
		public int dbg = 0;
		public String id;

		public boolean deploy = true;
		public String model;


		public Param(Class c) {
			super(c);
			parse();
		}
		public String cout_folder;
		public String err_folder;
		public String score_folder;

		public void parse() {
			dbg = getInt("dbg", 0);
			num_CV_folds = getInt("num_CV_folds", 3);
			id = getString("id", "x");

			train_rate = getDouble("train_rate", 0.7);
			task = Task.valueOf(getString("task", Task.service.name()));

			train_samples = getString("train_samples", null);
			test_samples = getString("test_samples", null);
			output_folder = getFolder("output_folder", null);
			deploy = getBoolean("deploy", false);
			model = getString("model", null);

			cout_folder = output_folder + task + ".cout/";
			err_folder = output_folder + task + ".err/";
			score_folder = output_folder + task + ".scores/";
		}

		public void generateCode() {
			train_data_code_ = FString.getLastSection(train_samples, '/');
			test_data_code_ = FString.getLastSection(test_samples, '/');

			task_code_ = task.name();
			switch (task) {
			case TT:
				task_code_ += test_data_code_;
				break;
			case STT:
				task_code_ += getString("train_rate", "");
				break;
			case CV:
			case sCV:
				task_code_ += getString("num_CV_folds", "");
				break;
			case LOO:
				break;
			}
		}
		public String train_data_code_;
		public String test_data_code_;
		public String task_code_;
	}

	public static void printf(String format, Object... args) {
		String s = String.format(format, args);
		System.out.print(s);
		//if (bwDbg!=null) 		FFile.write(bwDbg, s);
	}

	public static void println(String s) {
		System.out.println(s);
		// if (bwDbg!=null) 		FFile.writeln(bwDbg, s);
	}

	public static void errf(String format, Object... args) {
		String s = String.format("[Error] " + format, args);
		System.err.print(s);
		// if (bwDbg!=null) 		FFile.write(bwDbg, s);
	}

	public Param param = null;

	public void initParam(Class c) {
		param = new Param(c);
	}

	public Learner(Class c, String args) {
		this.args = args;
		if (args!=null)	Param.overwrite(args);
		//if (param == null) 
		initParam(c);
	}

	protected String STT(String example_file) {
		String train_file = example_file + ".p" + param.train_rate;
		String test_file = train_file + "_";
		if (!FFile.exist(train_file)) {
			FTable.splitByProb(example_file, param.train_rate, train_file, test_file);
		}
		train(train_file);
		test(test_file);
		return null;
	}

	protected String CV(String fn) {

		return null;
	}

	protected String LOO(String fn) {
		StopWatch sw = new StopWatch();
		//TODO: implement it
		TITLE += "\tTime";
		RESULT += "\t" + sw.stopSec3D();
		return null;
	}

	public String run() {
		//String rlt=null;
		System.out.println("task=" + param.task);
		System.out.println("outFolder=" + param.output_folder);
		StopWatch sw = new StopWatch();
		
		TITLE = "";
		RESULT = "";
		FFile.mkdirs(param.output_folder);

		switch (param.task) {
		case train:
			train(param.train_samples);
			break;
		case retrain:
			retrain(param.model, param.train_samples);
			break;

		case service:
			loadModel(param.model);
			break;
		case test:
			loadModel(param.model);
			test(param.test_samples);
			FFile.copyFile(param.model, out_folder_+  "model");
			break;
		case predict:
			loadModel(param.model);
			predict(param.test_samples);
			FFile.copyFile(param.model, out_folder_+  "model");
			break;
		case exp:
			exp();
			break;
		case TT:
			train(param.train_samples);
			test(param.test_samples);
			break;
		case STT:
			STT(param.train_samples);
			break;
		case CV:
			CV(param.train_samples);
			break;
		case LOO:
			LOO(param.train_samples);
			break;
		case sCV:
			selfCV(param.train_samples, param.num_CV_folds);
			break;
		case sLOO:
			selfCV(param.train_samples, -1);
			break;

		}
		TITLE += "\tTime"; 
		RESULT += "\t" + sw.stopSec3D();

		if (args != null) 
			FFile.appendToFile(param.output_folder + 
					FRun.getArgumentValues(args) + RESULT + "\n", "result.txt");//"\t"+
		else 
			FFile.appendToFile(param.output_folder + 
					code_ + RESULT + "\n", "result.txt");//"\t"+

		FFile.saveString(param.output_folder + param.task + ".title", TITLE);

		if (args != null) {
			FFile.mkdirs(param.score_folder);
			FFile.saveString(param.score_folder + args, 
					RESULT.trim() + "\n" + out_folder_);
		}

		this.printf(TITLE + "\n");
		this.printf(RESULT + "\n");

		dispose();
		return RESULT;
	}

	public String args = null;

	//public double exp(){return 0;};
	public void exp() {};

	public abstract boolean loadModel(String model_file);

	public abstract void train(String sample_file);
	public void retrain(String model_file, String sample_file){}

	public abstract void test(String sample_file);

	public void selfCV(String sample_file, int fold) {}

	public abstract void predict(String fn);

	public void dispose() {}
}
