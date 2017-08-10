package edu.cmu.lti.algorithm.ir.eva;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.math.FMath;

/**
 * http://en.wikipedia.org/wiki/T-test
 * @author nlao
 *
 */
public class PairedTTest {
	public static double getP(VectorD v1, VectorD v2){
		return FMath.Phi(-getZ(v1,v2));
	}
	public static double getZ(VectorD v1, VectorD v2){
		return getZ(v1.minus(v2));
	}
	public static double getZ(VectorD v){
		v=v.subNonZero();
		double d=v.mean();
		v.minusOn(d);
		double sd=v.sd();
		return Math.abs(d)/sd;
	}
	public static void main(String[] args) {
		System.out.print(getP(
			 VectorD.fromFile("1e-4.scores.Q3e-4",1)
			,VectorD.fromFile("1e-4.scores.Q0",1) ));
		// VectorD.fromFile("3e-4.scores.Q3e-4",1)
			//,VectorD.fromFile("3e-4.scores.Q0",1) ));

			//1e-4: 1.9735038632728852E-5
		//
		return;
	}
}
