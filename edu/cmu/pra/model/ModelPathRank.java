package edu.cmu.pra.model;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.OptizationEval;
import edu.cmu.pra.graph.GraphWalker;

public class ModelPathRank extends PRAModel {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	
	public ModelPathRank(GraphWalker walker) {//IGraph graph) {
		super(walker);
	}
	
	@Override public void initWeights() {
		if (p.bias) bias_ = 0;
		path_weights_.reset(path_names_.size(), 0.0);
		getParameters();
		return;
	}

	public void SetParameters() {
		param_names_.clear();
		if (p.bias) param_names_.add("bias");
		param_names_.addAll(path_names_);
		return;
	}

	@Override public void setParameters(double[] x) {
		param_weights_.setAll(x);
		path_weights_.clear();

		int k = 0;
		if (p.bias) bias_ = x[k++];

		for (int i = 0; i < path_names_.size(); ++i)
			path_weights_.add(x[k++]);
	}

	@Override public double[] getParameters() {
		param_weights_.clear();
		if (p.bias) param_weights_.add(bias_);

		for (int i = 0; i < path_names_.size(); ++i)
			param_weights_.add(path_weights_.get(i));//++k;

		//return vwF;
		return param_weights_.toDoubleArray();
	}

	@Override public VectorD getGradient(OptizationEval eva) {
		VectorD param_gradients = new VectorD();

		if (p.bias) param_gradients.add(eva.bias_gradient_);//*p.scBias);
		param_gradients.addAll(eva.path_gradients_);
		return param_gradients;
	}
}
