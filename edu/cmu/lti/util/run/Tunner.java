package edu.cmu.lti.util.run;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.regex.Matcher;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

/**
 * this class helps tuning parameters for programs
 * it does coordinate hill climbing
 * it caches system performances in memory within a run
 * and caches then on disk between runs
 * it helps organizing all the these information in nice folder structure
 * 
 * @author nlao
 *
 */
public abstract class Tunner { //extends ThreadPool{

	public class ThreadPool extends edu.cmu.lti.util.system.ThreadPool {

		@Override public edu.cmu.lti.util.system.ThreadPool.WorkThread newWorkThread(int i) {
			return new WorkThread(i);
		}

		public class WorkThread extends
				edu.cmu.lti.util.system.ThreadPool.WorkThread {
			public WorkThread(int id) {
				super(id);
			}

			@Override public void workOnJob(Job job) {
				System.out.println("@T" + id_ + "\t" + stop_watch_.currentHour() + "  "		+ job.key_);
				evaASetting(job.key_);
				//System.out.println("\t@T"+t.id+ "-->"+vd);		
			}
		}
		
	};
	public ThreadPool pool_ = new ThreadPool();

	public static enum TunnerMode {
		sweep, smart
	}

	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public int max_num_runs;

		public VectorS parameters;//= new VectorS();	
		public String sDParamCode = "";
		public int num_avarage = 2;
		public boolean split_summary = true;
		public int starting_id = 0;
		String cache;
		public boolean print_std_dev;
		public String fpTunnerGrid;
		//public int sweep;
		public TunnerMode tunner_mode;
		public int num_processes = 1;
		public int memory; //in Mb
		public String memory_code; //in Mb
		public String cmd; //in Mb
		public String task; //in Mb

		public Param(Class c) {
			super(c);//Tunner.class);
			parse();
		}

		String output_folder = null;
		String cout_folder = null;
		String score_folder = null;

		public String col1 = null;
		public String col2 = null;
		public int col1_ = -1;
		public int col2_ = -1;

		public String valueCol = null;
		public String fnBaseline = null;

		public void parse() {
			task = getString("task", null);

			starting_id = getInt("starting_id", 0);
			max_num_runs = getInt("max_num_runs", 20);
			print_std_dev = getBoolean("print_std_dev", false);
			num_avarage = getInt("num_avarage", 1);
			num_processes = getInt("num_processes", 1);
			split_summary = getBoolean("bSplitSummary", true);
			tunner_mode = TunnerMode.valueOf(getString("tunner_mode",
					TunnerMode.sweep.name()));

			sDParamCode = getBaseCode();//"vDParam");
			fpTunnerGrid = getString("fpTunnerGrid", "grid");
			//code+=".avg"+nAvarage;
			cache = sDParamCode + "/c" + parameters;
			memory = getInt("memory", 1000);
			cmd = getString("cmd", null);
			memory_code = getString("memory_code", "");

			col1 = getString("col1", null);
			col2 = getString("col2", null);
			valueCol = getString("valueCol", null);
			fnBaseline = getString("fnBaseline", null);

			output_folder = getFolder("output_folder", "./");

			System.out.println("task="+task);
			cout_folder =  output_folder + task + ".cout/";
			score_folder = output_folder + task + ".scores/";

		}

	}

	Parameter param2 = null;
	Parameter param1 = null;

	public void parseCols() {
		for (int i = 0; i < param_list_.size(); ++i) {
			String name = param_list_.get(i).name;
			if (name.equals(p.col1)) p.col1_ = i;
			if (name.equals(p.col2)) p.col2_ = i;

		}
		if (p.col1_ != -1) param1 = param_list_.get(p.col1_);
		if (p.col2_ != -1) param2 = param_list_.get(p.col2_);;
	}

	public static enum EPType {
		linear, log, reversed
	}

	/**actual run is project specific*/
	//abstract protected double evaASetting(String postload);
	protected void evaASetting(String args) {
		if (FFile.exist(p.cout_folder + args)) 	FFile.delete(p.cout_folder + args);
		
		String rlt= FSystem.cmd(p.cmd + " " + args + " > " + p.cout_folder + args);
		//System.out.print(rlt);
	}
	public String path_ = null;

	public String collectAScore(String code) {
		String line = FFile.loadString(p.score_folder + code);
		if (line == null) return null;
		String values[] = line.split("\n");
		path_ = values[1];
		return values[0];
	}

	public static class Parameter implements IGetStrByStr {
		public int id;
		public String name;
		public int num_grids;
		public VectorS grids = new VectorS();

		public int max;
		public int up, high, down;
		public EPType type;

		public String toString() {
			return name + "(" + num_grids + ")";
		}

		public String getString(String name) {
			return null;
		}

		public Parameter(int id, String name, int nGrid) {//EPType type,
			this.id = id;
			//this.type=type;
			this.name = name;
			this.num_grids = nGrid;
			high = nGrid / 2;
			int delta = (int) Math.floor(nGrid * 0.1 + 1);
			up = high + delta;
			down = high - delta;
			normalized();
		}

		public String getPostLoadString(int id) {
			return name + "=" + grids.get(id);
		}

		public String getPostLoadStringS(int id) {
			return name.charAt(0) + "-" + grids.get(id);
		}

		public String getValue(int id) {
			return grids.get(id);
		}

		public void normalized() {
			down = Math.max(0, down);
			up = Math.min(num_grids - 1, up);

		}

		public void shiftDown() {
			int delta = (int) Math.floor((high - down) * 1.4) + 1;
			up = high;
			high = down;
			down -= delta;
			normalized();
			//down=Math.max(0,down- delta);			
		}

		public void enlargeGaps() {
			if (high - down <= this.num_grids * 0.1 + 1) {
				int dD = (int) Math.floor((high - down) * 0.4) + 1;
				down -= dD;
			}
			if (up - high <= this.num_grids * 0.1 + 1) {
				int dU = (int) Math.floor((up - high) * 0.4) + 1;
				up += dU;
			}
			normalized();
		}

		public void shiftUp() {
			int delta = (int) Math.floor((up - high) * 1.4) + 1;
			down = high;
			high = up;
			up += delta;
			normalized();
			//up=Math.min(nGrid-1,up+ delta);			
		}

		public void replaceHigh(int ix) {
			if (ix > high) down = high;
			else up = high;
			high = ix;
		}
	}

	protected String getPostLoadString(VectorI v) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.size(); ++i) {
			Parameter p = param_list_.get(i);
			if (i > 0) sb.append(",");
			sb.append(p.getPostLoadString(v.get(i)));
		}
		return sb.toString();
	}

	protected String getPostLoadStringSkipCol12(VectorI v) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.size(); ++i) {
			if (i == p.col1_ || i == p.col2_) continue;
			Parameter p = param_list_.get(i);
			if (sb.length() > 0) sb.append(",");
			sb.append(p.getPostLoadString(v.get(i)));
		}
		return sb.toString();
	}

	protected String getPostLoadStringSkipCol1(VectorI v) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.size(); ++i) {
			if (i == p.col1_) continue;
			Parameter p = param_list_.get(i);
			if (sb.length() > 0) sb.append(",");
			sb.append(p.getPostLoadString(v.get(i)));
		}
		return sb.toString();
	}

	protected String getPostLoadStringS(VectorI v) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.size(); ++i) {
			Parameter p = param_list_.get(i);
			if (i > 0) sb.append("_");
			//sb.append(p.getPostLoadStringS( v.get(i)));
			sb.append(p.getValue(v.get(i)));
		}
		return sb.toString();
	}

	StopWatch stop_watch_ = new StopWatch();

	NumberFormat formatter = new DecimalFormat("00");

	protected void averageASetting(String key) {
		for (int i = p.starting_id; i < p.starting_id + p.num_avarage; ++i) {
			pool_.waitWorker();
			synchronized (pool_.queue_) {
				pool_.queue_.add(pool_.new Job(-1, key + ",id=" + formatter.format(i)));
				pool_.queue_.notify();
			}
		}
		return;
	}

	public Param p;

	public Tunner(Class c) {//,VectorS vParam,VectorI vGridSize,VectorS vDParam){
		p = new Param(c);

		FFile.mkdirs(p.cout_folder);
		FFile.mkdirs(p.score_folder);
		param_list_ = loadGrid(p.fpTunnerGrid);

	}

	//protected boolean loadGrid(){return loadGrid(p.fpTunnerGrid);}

	public VectorX<Parameter> param_list_ = null;// new TVector<Parameter>(Parameter.class);

	public VectorX<Parameter> loadGrid(String fn) {
		VectorX<Parameter> vp = new VectorX<Parameter>(Parameter.class);
		//System.out.println("loading dataset from text file"+fn);
		for (String line : FFile.enuLines(fn)) {
			if (line.startsWith("#")) break;

			Matcher ma = Param.pParam.matcher(line);
			if (!ma.matches()) {
				System.err.print("parse paramater failed " + line + "\n");
				continue;
			}
			String name = ma.group(1);
			String v[] = ma.group(2).split(",");
			Parameter p = new Parameter(vp.size(), name, v.length);
			p.grids.addAll(v);
			vp.add(p);
		}
		return vp;

	}

}
