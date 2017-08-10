package edu.cmu.pra.model;

import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.OptizationEval;
import edu.cmu.pra.graph.GraphWalker;

/**
 * same as PathRank except that the model is parameterized by Relation 
 * @author nlao
 *
 */
public class ModelRelationRank extends PRAModel {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public VectorD vwRel = null; //weights
	public VectorD Zs = new VectorD(); //weights

	public ModelRelationRank(GraphWalker walker) { //IGraph graph) {
		super(walker);
	}

	@Override public String getWeightCount() {
		return vwRel.countNonZero() + "\t" + vwRel.size() + "\t" + vwRel.norm1();
	}

	private double[] _wRel2wPath(double[] wRel) {
		double[] wPath = new double[path_names_.size()];
		for (int i = 0; i < path_nodes_.size(); ++i) {
			PathNode n = path_nodes_.get(i);
			double d = 1;
			for (Map.Entry<Integer, Integer> e : n.relation_count_.entrySet()) {
				int iRel = e.getKey();
				int c = e.getValue();
				for (int j = 0; j < c; ++j)
					d *= wRel[iRel];
			}
			wPath[i] = d;
		}
		return wPath;
	}

	protected double[] normalizedWRel(double[] wRel) {
		double[] wRel_ = new double[wRel.length];//normalized
		//		for (EntType et : schema.vEntType) {
		//			et.Z = 0;
		//			for (Relation r : et.vRelationTo)
		//				et.Z += wRel[r.id];
		//		}
		//		for (Relation r : schema.vRel)
		//			wRel_[r.id] = wRel[r.id] / r.from_type_.Z;
		return wRel_;
	}

	protected VectorD normalizedWRel(VectorD wRel) {
		VectorD wRel_ = new VectorD(wRel.size());//normalized
		//		for (EntType et : schema.vEntType) {
		//			et.Z = 0;
		//			for (Relation r : et.vRelationTo)
		//				et.Z += wRel.get(r.id);
		//		}
		//		for (Relation r : schema.vRel)
		//			wRel_.set(r.id, wRel.get(r.id) / r.from_type_.Z);
		return wRel_;
	}

	private double[] wRel2wPath(double[] wRel) {
		double[] wPath = new double[path_names_.size()];
		double[] wRel_ = null;
		//if (super.p.bNormalizeWeights)
		//wRel_=normalizedWRel(wRel);		else 
		wRel_ = wRel;

		for (int i = 0; i < path_nodes_.size(); ++i) {
			PathNode n = path_nodes_.get(i);
			double d = 1;
			for (Map.Entry<Integer, Integer> e : n.relation_count_.entrySet()) {
				int iRel = e.getKey();
				int c = e.getValue();
				for (int j = 0; j < c; ++j)
					d *= wRel_[iRel];
			}
			wPath[i] = d;
		}
		return wPath;
	}

	private VectorD wRel2wPath(VectorD wRel) {
		VectorD wPath = new VectorD(path_names_.size());
		VectorD wRel_ = null;
		//if (p.bNormalizeWeights)	wRel_=normalizedWRel(wRel);	else 
		wRel_ = wRel;

		for (int i = 0; i < path_nodes_.size(); ++i) {
			PathNode n = path_nodes_.get(i);
			double d = 1;
			for (Map.Entry<Integer, Integer> e : n.relation_count_.entrySet()) {
				int iRel = e.getKey();
				int c = e.getValue();
				for (int j = 0; j < c; ++j)
					d *= wRel_.get(iRel);
			}
			wPath.set(i, d);
		}
		return wPath;
	}

	private double[] gPath2gRel(double[] gPath, double[] wRel, double[] wPath) {
		double[] gRel = new double[vwRel.size()];
		MapID m = B.weightedSum(gPath);
		//MapID m=B.weightedSum( FArrayD.multiply(gPath,wPath));
		for (Map.Entry<Integer, Double> e : m.entrySet()) {
			int iRel = e.getKey();
			Double c = e.getValue();
			if (wRel[iRel] != 0.0) gRel[iRel] = c / wRel[iRel];
			else gRel[iRel] = c; // a terrible approximation, hope it works

			if (Double.isNaN(gRel[iRel])) System.err.println("NaN");
		}
		return gRel;
	}

	private VectorD gPath2gRel(VectorD gPath, VectorD wRel, VectorD wPath) {
		VectorD gRel = new VectorD(vwRel.size());
		MapID m = B.weightedSum(gPath);
		//MapID m=B.weightedSum( FArrayD.multiply(gPath,wPath));
		for (Map.Entry<Integer, Double> e : m.entrySet()) {
			int iRel = e.getKey();
			Double c = e.getValue();
			if (wRel.get(iRel) != 0.0) gRel.set(iRel, c / wRel.get(iRel));
			else gRel.set(iRel, c); // a terrible approximation, hope it works

			//if (Double.isNaN(gRel.get(iRel)))			System.err.println("NaN");
		}
		return gRel;
	}

	VecMapID B = null;// new VectorMapID();

	@Override public void SetParameters() {
		param_names_.clear();
		if (p.bias) param_names_.add("bias");
		param_names_.addAll(this.graph_.getOrderedEdgeLabels());

	}

	@Override public void initWeights() {
		vwRel = new VectorD(graph_.getOrderedEdgeLabels().length, 1.0);//p.dampening);
		path_weights_ = wRel2wPath(vwRel);


		B = getPowerMatrix();
		double x[] = getParameters();
		setParameters(x);
		return;
	}

	public VecMapID getPowerMatrix() {
		VecMapID B = new VecMapID();
		for (PathNode n : path_nodes_)
			B.add(n.relation_count_.toDouble());
		return B;
	}

	public void setParameters(double[] x) {
		param_weights_.setAll(x);
		path_weights_.clear();

		int k = 0;
		if (p.bias) bias_ = x[k++];

		for (int i = 0; i < vwRel.size(); ++i)
			vwRel.set(i, x[k++]);

		path_weights_ = wRel2wPath(vwRel);

	}

	@Override public double[] getParameters() {
		int k = 0;
		if (p.bias) {
			param_weights_.setE(0, bias_);
			++k;
		}
		for (int i = 0; i < vwRel.size(); ++i) {
			param_weights_.setE(k, vwRel.get(i));
			++k;
		}

		return param_weights_.toDoubleArray();
	}

	@Override public VectorD getGradient(OptizationEval eva) {

		VectorD param_gradients = new VectorD();
		if (p.bias) param_gradients.add(eva.bias_gradient_);//*p.scBias
		param_gradients
				.addAll(gPath2gRel(eva.path_gradients_, vwRel, path_weights_));
		return param_gradients;
	}

}
