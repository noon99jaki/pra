package edu.cmu.lti.algorithm.ir.eva;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.math.FMath;

/**
 * http://en.wikipedia.org/wiki/Sign_test
 * @author nlao
 *
 */
public class PairedSignTest {
	public static double getP(VectorD v1, VectorD v2){
		return FMath.Phi(-getZ(v1,v2));
	}
	public static double getZ(VectorD v1, VectorD v2){
		int w=0;
		int n=0;
		for (int i=0; i<v1.size(); ++i){
			if (v1.get(i)==v2.get(i)) continue;
			++n;
			if (v1.get(i)> v2.get(i))
				++w;
		}
		return getZ(w,n);
	}
	public static double getZ(int w, int n){
		double sigma0=0.5*Math.sqrt(n);
		double d=w-n/2.0;
		return Math.abs(d)/sigma0;
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
