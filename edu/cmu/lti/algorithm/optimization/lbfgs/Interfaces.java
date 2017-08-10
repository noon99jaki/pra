package edu.cmu.lti.algorithm.optimization.lbfgs;

import java.io.Serializable;

import edu.cmu.lti.algorithm.FArrayD;
import edu.cmu.lti.algorithm.container.VectorD;

public class Interfaces {
	public static interface IterCallbackFun {
		public void onIter(double[] curX, int iIter);
	}

	public static class OptizationEval implements Serializable {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public double loss_ = 0;
		public VectorD path_gradients_ = new VectorD();
		public double bias_gradient_;

		public static String getTitle(){
			return "\tLoss";
		}
		
		public String print(){
			return String.format("%.3f", loss_);
		}
		public void clear(int num_features) {
			bias_gradient_ = 0;
			loss_ = 0;
			//path_gradients_.clear();//
			path_gradients_.reset(num_features, 0.0);
		}

		public OptizationEval plusOn(OptizationEval eva) {
			loss_ += eva.loss_;
			bias_gradient_ += eva.bias_gradient_;
			//if (path_gradients_.size() == 0) 
			//path_gradients_.reset(eva.path_gradients_.size(), 0.0);
			path_gradients_.plusOn(eva.path_gradients_);
			return this;
		}

		public OptizationEval multiplyOn(Double x) {
			loss_ *= x;
			bias_gradient_ *= x;
			path_gradients_.multiplyOn(x);
			return this;
		}
	}

	public static class EvalLBFGS {
		public int dim;
		public int nonZeroDim;
		public double y;
		public double[] x;
		public double xL1; // help determine convergence
		public double xL2; // help determine convergence
		public double g0L1;
		public double gL1;
		public double[] g;
		public double[] g0;
		public double[] dir;

		public void copy(EvalLBFGS e) {
			this.dim = e.dim;
			this.nonZeroDim = e.nonZeroDim;
			this.y = e.y;
			this.x = e.x;
			this.g = e.g;
			this.g0 = e.g0;
			this.xL1 = e.xL1;
			this.xL2 = e.xL2;
		}

		public EvalLBFGS(double[] x, double y0, double[] g0, double L1, double L2) {
			this.dim = x.length;//dim; int dim,
			this.y = y0;
			this.x = x;
			this.g0 = g0;
			addRegularization(L1, L2);
			this.nonZeroDim = FArrayD.countNonZero(this.x);
		}

		public EvalLBFGS(VectorD x, double y0, VectorD g0, double L1, double L2) {
			this.dim = x.size();//dim; int dim,
			this.y = y0;
			this.x = x.toDoubleArray();
			this.g0 = g0.toDoubleArray();
			addRegularization(L1, L2);
			this.nonZeroDim = FArrayD.countNonZero(this.x);
		}

		public String toString() {
			return String.format(" y=%.6f |x|=%.1f |g|=%.3f d=%d/%d", y, xL1, gL1,
					nonZeroDim, dim);
		}

		public String toStringE(int n) { //extended version
			return toString() + " x=" + FArrayD.toString(x, n) + " g="
					+ FArrayD.toString(g, n);
		}

		public void print() {
			System.out.println();
			System.out.println("x=" + FArrayD.join(x, "%.3f", " "));
			System.out.println("g0=" + FArrayD.join(g0, "%.3f", " "));
			System.out.println(toString());
		}

		// for lbfgs to find step size quickly
		// the magnitude of g should be similar to x
		// therefore we scale down the loss function 
		// (instead of stretching out on x directions)
		public void scaleDown(double sc) {
			y *= sc;
			//FArrayMath.multiplyInPlace(x, sc);
			FArrayMath.multiplyInPlace(g, sc);
			FArrayMath.multiplyInPlace(g0, sc);
			this.g0L1 *= sc;
			this.gL1 *= sc;
		}

		public void addRegularization(double L1, double L2) {
			g = new double[x.length];
			xL1 = FArrayD.norm1(x);
			xL2 = FArrayD.inner(x, x) / 2;
			g0L1 = FArrayD.norm1(g0);
			for (int i = 0; i < x.length; ++i) {
				g0[i] += x[i] * L2;
				g[i] = regularize(g0[i], L1, x[i]);
			}
			gL1 = FArrayD.norm1(g);
			y += xL1 * L1 + xL2 * L2;
		}

		/**
		 * return regularized gradient
		 */
		public static double regularize(double g0, double L1, double w) {
			if (w == 0.0) {
				if (Math.abs(g0) <= L1) return 0.0;

				if (g0 > 0.0) return (g0 - L1);
				else return (g0 + L1);
			} else {
				if (w < 0.0) return (g0 - L1);
				else return (g0 + L1);
			}
			//return getG0()+getG1();
		}
	}

	public static interface IFunction {
		public EvalLBFGS evaluate(double[] x, boolean bInduce);
	}

//	public static interface ILineSearcher {
//		public EvalLBFGS search(IFunction fun, EvalLBFGS iniE, double[] dir,
//				boolean bProj, boolean bInduce);
//	}

}
