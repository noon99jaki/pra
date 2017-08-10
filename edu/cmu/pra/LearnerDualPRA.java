package edu.cmu.pra;

import java.util.Map;

import edu.cmu.lti.algorithm.container.MapISetI;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.model.PathTree;
import edu.cmu.pra.model.Query;

// use the same train/test example files as regular PRA model
// assume that the first field is the reversed target field
// and the last field becomes the reversed source field
public class LearnerDualPRA extends LearnerPRA {
	LearnerPRA learner;	// a regular LearnerPRA for forward path exploration
	LearnerPRA dual_learner; // a regular LearnerPRA for backward path exploration
	public LearnerDualPRA(String args) {	
		this(null, args);
	}
	
	public LearnerDualPRA(GraphWalker walker, String args) {
		super(walker, args);
		GraphWalker.silent_=true;	
		
	//	Param.overwrite("num_threads=1");	// TODO: fix this
		Param.overwrite("dual_mode=false");
		
		VectorI max_steps = model_.p.max_steps.left(model_.p.max_steps.size()-1);
		Param.overwrite("max_steps=" + max_steps.join("-"));
		learner= new LearnerPRA(walker_, args, this.out_folder_);
		
		
		Param.overwrite("max_steps=" + model_.p.max_steps.lastElement() + "-0");
		Param.overwrite("blocked_field=1");
		dual_learner= new LearnerPRA(walker_, args, this.out_folder_);
		return;
	}
	@Override public void dispose() {		
		if (p.multi_threaded){
			super.dispose();
			learner.dispose();
			dual_learner.dispose();
		}
	}

	// create queries for path exploration
	public VectorX<Query> makeDualQueries(VectorI ids) {
		
		MapISetI target_sources = new MapISetI();
		
		for (Query query: this.queries_.sub(ids)) 
			for (int target : query.seeds_.lastElement())		
				target_sources.getC(target).addAll(query.seeds_.firstElement());
			
		VectorX<Query> queries=new VectorX<Query>(Query.class);
		for (Map.Entry<Integer, SetI> i: target_sources.entrySet()) {
			Query query = new Query();
			query.good_.addAll(i.getValue());	//source
			query.seeds_.add(new SetI(i.getKey()));	// target
			query.seeds_.add(new SetI(i.getValue()));	//source
			queries.add(query);
		}
		return queries;
	}

	// prepare model
	void explorePaths(VectorI ids, String code) {
		//learner.queries_=this.queries_;
		model_.trees_.clear();
		learner.explorePaths(ids, code +".origial");
		for (PathTree tree: learner.model_.trees_)		model_.trees_.add(tree);
		
		PathTree dual_tree = dual_learner.model_.trees_.firstElement();		
		dual_tree.field_ = 0;
		dual_learner.queries_ = makeDualQueries(ids);
		VectorI dual_ids = VectorI.seq(dual_learner.queries_.size());
		dual_learner.explorePaths(dual_ids, code +".dual");
		
		dual_tree.field_ = model_.trees_.size();
		model_.trees_.add(dual_tree);
		
		model_.indexPathTree();
		model_.saveModel(this.out_folder_ + code + ".initial_model", false);
		return;
	}
	
	public void loadQueries(String sample_file) {
		super.loadQueries(sample_file);
		this.learner.loadQueries(sample_file);
		//this.dual_learner.loadQueries(sample_file +".dual");
	}
	
	public static void main(String[] args) {
		try {
			Param.overwriteFrom("conf");
			Param.overwrite("dual_mode=true");
			if (args.length > 0) 			Param.overwrite(args[0]);
			
			(new LearnerDualPRA(args.length > 0? args[0]:null)).run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
