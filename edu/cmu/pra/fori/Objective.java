package edu.cmu.pra.fori;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.EvalLBFGS;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.IFunction;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.OptizationEval;
import edu.cmu.lti.util.run.Learner;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.LearnerFORI;
import edu.cmu.pra.LearnerPRA;
import edu.cmu.pra.CTag.LossMode;
import edu.cmu.pra.CTag.NegMode;
import edu.cmu.pra.CTag.RankMode;
import edu.cmu.pra.CTag.ThreadTask;
import edu.cmu.pra.LearnerFORI.FORIThreadPool;
import edu.cmu.pra.fori.FORIModel.DoubleWrapper;
import edu.cmu.pra.graph.Graph;

public class Objective  implements IFunction{
	
	LearnerFORI l;
	FORIModel model_;
	Graph graph_;
	public Objective(LearnerFORI learner){
		l=learner;
		model_ = learner.model_;
		graph_ =  learner.graph_;
		p= new Param();
	}
	
	public Param p;
	public static class Param extends edu.cmu.lti.util.run.Param implements
	Serializable {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		
		public Param() {
			super(LearnerPRA.class);
			parse();
		}
		public double enOverflow = 20;
		public double exp_overflow;
		public double probability_overflow;
		
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
		public RankMode rank_mode = null;

		
		public String code_;
		public void parse() {
			exp_overflow = Math.exp(enOverflow);
			probability_overflow = exp_overflow / (1 + exp_overflow);

			negative_mode = NegMode.valueOf(getString("negative_mode", NegMode.Sqr
					.name()));
			negative_weight = getDouble("negative_weight", 10.0);

			polynomial_value = getDouble("polynomial_value", 1.5);
			K_value = getInt("K_value", 10);
			exponential_value = getDouble("exponential_value", 1.5);
			log_base = Math.log(exponential_value);

			rank_mode = RankMode
					.valueOf(getString("rank_mode", RankMode.Path.name()));
			loss_mode = LossMode.valueOf(getString("loss_mode", LossMode.log.name()));

			L1 = getDouble("L1", 1.0);
			L2 = getDouble("L2", 1.0);
			pairwise_loss = getBoolean("pairwise_loss", false);

			code_ = rank_mode.name();

			if (loss_mode.equals(LossMode.none)) {
				if (this.rank_mode.equals(RankMode.Path)) FSystem
						.die("should use lossMode=none with rankMode=R");
				code_ = "_untrained";
			} else {

				code_ += String.format("_%s%s", negative_mode.name(), getString(
						"negative_weight", null));

				if (negative_mode.equals(NegMode.topK)) code_ += String.format(
						"%d", K_value);
				if (negative_mode.equals(NegMode.poly)) code_ += String.format(
						"%.1f", polynomial_value);
				if (negative_mode.equals(NegMode.expX)) code_ += String.format(
						"%.1f", exponential_value);

				if (L1 != 0.0) code_ += "_L1=" + getString("L1", "");
				if (L2 != 0.0) code_ += "_L2=" + getString("L2", "");

				if (pairwise_loss) code_ += "_paired";
			}

		}
	}
	

	protected VectorI selectNegativeSamples(MapID sys) {
		//SetI bad = new SetI();
		VectorI bad = new VectorI();
		VectorI vi = (VectorI) sys.KeyToVecSortByValue(true);
		if (p.negative_mode.equals(NegMode.all)) return vi;

		if (p.negative_mode.equals(NegMode.topK)) return vi.left(p.K_value);

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
		return bad;
	}

	protected double sigmoid(double score) {
		if (score > p.enOverflow) score = p.enOverflow;
		else if (score < -p.enOverflow) score = -p.enOverflow;
		double exp = Math.exp(score);
		return exp / (1 + exp);
	}

	protected void evaluateNode(boolean good, int target, double score,
			double weight, FORIQuery query, OptizationEval eva) {
		//		if (model_.p.bias) score += model_.param_weights_.get(0) * model_.p.bias_value;

		double p = sigmoid(score);
		eva.loss_ += good ? -Math.log(p) * weight : -Math.log(1 - p) * weight;

		double gradiant = good ? (1 - p) : -p;
		gradiant *= weight;
		query.target_gradients.put(target, gradiant);

		for (int feature = 0; feature < query.features_sampled_.size(); ++feature) {
			double d = query.features_sampled_.get(feature).getBiasedValue(target);
			if (d != 0.0) eva.path_gradients_.minusOn(feature, gradiant * d);
		}
		//		if (model_.p.bias) eva.bias_gradient_ -= error * model_.p.bias_value;
		return;
	}

	protected void evaluatePair(int iP, double dP, int iN, double dN, double w,
			FORIQuery q, OptizationEval eva) {
		double margin = dP - dN;
		double p = sigmoid(margin);
		eva.loss_ += -Math.log(p) * w;
		//if (!bGradient) return;
		double error = (1 - p) * w;
		for (int iF = 0; iF < q.features_sampled_.size(); ++iF) {
			MapID m = q.features_sampled_.get(iF);
			Double d = m.getD(iP) - m.getD(iN);
			eva.path_gradients_.minusOn(iF, error * d);
		}
	}
	
	public void evaluateTrain(FORIQuery query, OptizationEval eval) {
		//this.bGradient= bTrain;
		DoubleWrapper bias = new DoubleWrapper();
		MapID result = model_.predict(query, bias);
		//query.features_sampled_.weightedSum(model_.param_weights_);
		//model_.walker_.applyFilters(query, result);
		if (result.containsKey(-1)) result.remove(-1);
		
		if (eval == null) return;
//			if (p.pairwise_loss) {
//				if (query.good_.size() != 0 && query.bad_.size() != 0) {
//					double w = 1.0 / query.bad_.size() / query.good_.size();
//					for (int good : query.good_)
//						for (int bad : query.bad_)
//							evaluatePair(good, result.get(good), bad, result.get(bad), w,
//									query, eval);
//				}
//			} else 

		double weight = 1.0;
		
		if (query.hit_.size()!=0) {
			weight = 1.0 / query.hit_.size();
			query.target_gradients.clear();

			for (int good : query.hit_)
				evaluateNode(true, good, result.getD(good, 0.0) + bias.value, weight, query, eval);
		}
		weight = p.negative_weight / query.bad_.size();
		for (int bad : query.bad_)
			evaluateNode(false, bad, result.getD(bad, 0.0) + bias.value, 
					weight, query,	eval);
		
	}

	//private VectorI selected_ids_ = null;//queries in the loss function
	//public VectorX<FORIQuery> selected_queries_ = new VectorX<FORIQuery>(FORIQuery.class);

	public EvalLBFGS evaluate(double[] x, boolean bInduce) {

		if (x.length == 0) model_.initWeights();
		else model_.setParameters(x);

		OptizationEval opt_eval_ = new OptizationEval();
		evaluateTrain(l.selected_queries_, opt_eval_);
		EvalLBFGS eval = new EvalLBFGS(model_.param_weights_, opt_eval_.loss_,
				model_.getGradient(opt_eval_), p.L1, p.L2);
		//		eval.scaleDown(1.0 / pool_.queries.size());

		if (!l.lbfgs_.p.LBFGS_silent) Learner.printf("\t%s\n", eval.toStringE(5));
		return eval;
	}

	//* given a set of path weights		VectorI ids
	//* calculate the loss and derivatives and store then in eva
	public void evaluateTrain(VectorX<FORIQuery> queries, 
			 OptizationEval opt_eval_ ) {

		int num_features = model_.param_weights_.size();

		if (opt_eval_!=null) opt_eval_.clear(num_features);
		if (queries.size() == 0) return;

		if (l.p.multi_threaded) {
			for (edu.cmu.lti.util.system.ThreadPool.WorkThread i : l.pool_.threads_) {
				FORIThreadPool.WorkThread t = (FORIThreadPool.WorkThread) i;
				t.opt_eval_.clear(num_features);
			}
			l.pool_.runTask(ThreadTask.EvaluateTrain, queries);

			for (edu.cmu.lti.util.system.ThreadPool.WorkThread i : l.pool_.threads_) {
				FORIThreadPool.WorkThread t = (FORIThreadPool.WorkThread) i;
				opt_eval_.plusOn(t.opt_eval_);
				t.opt_eval_.clear(num_features);

			}
		} else {
			for (FORIQuery query : queries) evaluateTrain(query, opt_eval_);
		}
		if (opt_eval_!=null) opt_eval_.multiplyOn(1.0 / queries.size());
		return;
	}
	
	public void updateNegativeSamples(FORIQuery query) {

		MapID result = query.features_.sum();
		if (result.containsKey(-1)) result.remove(-1);
		model_.walker_.applyFilters(query, result);
		query.hit_ = new SetI(result.sub(query.good_).keySet());
		result.removeAll(query.good_);
		
		query.bad_ = selectNegativeSamples(result).toSet();
		
		if (model_.walker_.p.given_negative_samples) {
			query.bad_.addAll(query.labeled_bad_);
		}
		
		
		query.selected_ = new SetI();

		query.selected_.addAll(query.good_);
		query.selected_.addAll(query.bad_);
		query.selected_.add(-1);

		query.features_sampled_.clear();
		for (MapID feature : query.features_)
			query.features_sampled_.add(feature.subSet(query.selected_));
		query.selected_.remove(-1);
	}
	
}
