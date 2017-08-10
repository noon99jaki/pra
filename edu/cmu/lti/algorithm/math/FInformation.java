package edu.cmu.lti.algorithm.math;

public class FInformation {
	// assume everything is smoothed
	public static double enthropy(double p) {
		//if (p == 1 || p == 0) return 0;
		return -p * Math.log(p) - (1 - p) * Math.log(1 - p);
	}
//
//	public static double mutualInfo(double yx, double x, double y, double total,
//			double smoothing) {
//		return mutualInfo(total, y, x, total - x, y - yx, yx, smoothing);
//	}

	public static double mutualInfo(double x, double _x,
			double y_x, double yx) {
		return mutualInfo(x, _x, y_x, yx, 1.0);
	}
	public static double mutualInfo(double x, double _x,
			double y_x, double yx, double smoothing) {
		if (smoothing > 0) {
			yx += smoothing;
			y_x += smoothing;
			x += smoothing * 2;
			_x += smoothing * 2;
		}
		double total = x +_x;
		double y = yx + y_x;
		double Hgb =  enthropy(yx / x);
		double Hgb_ =  enthropy(y_x / _x) ;
		double Hcb =  (Hgb * x + Hgb_ * _x) / total ;
		double Haa_ =  enthropy(y / total) ;
		return Haa_ - Hcb;
	}

//	public static double mutualInfo(int yx, int _yx, int y_x, int _y_x, double smoothing) {
//		int x = yx + _yx;
//		int _x = y_x + _y_x;
//		return mutualInfo(x, _x, y_x, yx, smoothing);
//	}
	
	public static void main(String[] args) {
		System.out.println("100,1000, 10,0 -->"+mutualInfo(100,1000, 10,0));
		System.out.println("100,1000, 10,1 -->"+mutualInfo(100,1000, 10,1));
		
		System.out.println("100,1000, 100,1 -->"+mutualInfo(100,1000, 100,1));
		System.out.println("100,1000, 100,10 -->"+mutualInfo(100,1000, 100,10));
	}

}
