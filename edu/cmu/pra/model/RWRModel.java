
package edu.cmu.pra.model;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.optimization.AModel;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.OptizationEval;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.graph.IGraph;

/** stuff related to the parameter*/
public class RWRModel extends AModel {

	public Param p;
	public IGraph graph_;
	public GraphWalker walker_;

	public RWRModel(GraphWalker walker) { //IGraph graph) {
		
		this.graph_ = walker.graph_;
		this.walker_ = walker;//new GraphWalker(graph);
		
		p = new Param();
	}
	public VectorD getGradient(OptizationEval eva){
		return null;
	}
	public void setParameters(double x[]){
		
	}
	public double[] getParameters(){
		return null;
	}
	public void initWeights() {
		
	}


	// assuming each path has the format
	// FieldID:relation,relation,...
	public void loadModel(String model_file) {
		FSystem.checkTrue(FFile.exist(model_file), "cannot find " + model_file);
//		
//		for (VectorS vs : FFile.enuRows(model_file, "\t", true)) {
//			double weight = Double.parseDouble(vs.get(0));
//			String field_path = vs.get(1);
//			if (field_path.equals("bias")) continue;
//			addPath(field_path).is_target_ = true;
//		}
//
//		loadWeights(model_file);
//		//this.saveModel(model_file +".loaded", false);
//		return;
	}

	public void clear() {
		this.clearWeights();
	}



	public void getFeatures(Query query, BufferedWriter inspect) {
		Counter.count50.stepDot();
		//g.setTime(q.time);
		if (inspect != null) 
			FFile.writeln(inspect, query.print(walker_));
		
		query.features_.clear();


		for (int field = 0; field < query.seeds_.size(); ++field) {
			SetI seed = query.seeds_.get(field);
			MapID dist = walker_.walkRWR(query, seed, 
					p.rwr_restart, p.max_steps.get(field));
			query.features_.add(dist);
		}
		return;
	}

	public MapID predict(Query query) {
		if (query.features_.size() == 0) getFeatures(query, null);
		MapID result = query.features_.sum();
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


		VectorI sorted_result = result.KeyToVecSortByValue(true);

		for (int i = 0; i <  sorted_result.size(); ++i) {
			int id = sorted_result.get(i);
			
			double score = result.get(id);
			if (score < p.prediction_threshold) break;
			
			String name =  (query.good_.contains(id)) ?"*" : "";
			name += graph_.getNodeName(id);
			predction.append(String.format("\t%.3e,%s", score, name));

//
//			if (reasons!=null) {
//				MapID feature_score = query.features_.getRowMap(id)
//				.multiply(this.feature_weights_);//				param_weights_);
//				reasons.append(String.format("%.3e\t%s\t", score, name));
//				VectorI sorted_reason = feature_score.KeyToVecSortByAbsValue(true);
//				reasons.append(feature_score.join(sorted_reason,"=", "\t", false));
//				reasons.append("\n");
//			}
		}
	}
	

	public static class Param extends edu.cmu.lti.util.run.Param {
		public Param() {
			super(PRAModel.class);
			parse();
		}

		public VectorI max_steps = new VectorI();
		public String max_steps_str;
		// Assume that the last column contains the targets
		public int num_fields = -1;
		public int num_columns = -1;


		public boolean cache_RW;
		public double prediction_threshold;
		
		public double  rwr_epsilon;
		public double  rwr_restart;
		

		
		//public double min_exploration_particle;
		public void parse() {
			rwr_epsilon = getDouble("rwr_epsilon", 1e-5);
			rwr_restart = getDouble("rwr_restart", 0.2);

			max_steps_str = getString("max_steps", "3");
			max_steps = VectorI.from(FString.splitVS(max_steps_str, "-"));
			num_fields = max_steps.size();
			
			num_columns=num_fields+1;
			
			prediction_threshold = getDouble("prediction_threshold", 0.0);

			cache_RW = getBoolean("cache_RW", false);
			model_code_ = "M" + max_steps_str + String.format("_R%.0e", rwr_restart);

		}
		public String model_code_;
	}


}




