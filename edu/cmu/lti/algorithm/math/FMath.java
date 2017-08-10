package edu.cmu.lti.algorithm.math;

/**
 * This class realizes a set of mathmatical operations
 * which is more diverse and efficient than java.lang.Math.
 * Inspired by edu.stanford.nlp.math.SloppyMath
 * @author hideki
 *
 */

public class FMath {

	/**
	 * Find a 2x2 chi-square value.
	 * @param a
	 * @param b
	 * @param c
	 * @param d
	 * @return result
	 */
	public static double chiSquare2by2(int a, int b, int c, int d) {
		return (a+b+c+d)*(a*d-b*c)^2/(a+b)/(a+c)/(b+d)/(c+d);
	}
    
	// Not efficient
	public static double informationGain2by2(int a, int b, int c, int d) {
		int n = a+b+c+d;
		return Math.log(n^n*a^a*b^b*c^c*d^d/(a+b)^(a+b)/(a+c)^(a+c)/(b+d)^(b+d)/(c+d)^(c+d))/n;
	}
	
	public static double pmi2by2(int a, int b, int c, int d) {
		return Math.log((a+b+c+d)*a/(a+b)/(a+c));
	}
	
	public static double poisson2by2(int a, int b, int c, int d) {
		return Math.log(a*d/b/c);
	}
	
	// Pr( Y=1 | X=1 )
	public static double pr2by2(int a, int b, int c, int d) {
		return (double)a/(double)(a+b);
	}
	
	public static double intPow( double b, int e ) {
		return Math.pow( b, e );
	}
	
	// Not efficient
	public static float floatPow( float b, int e ) {
		return (float)Math.pow( b, e );
	}
	
	public static int doublePow( int b, int e ) {
		return b^e;
	}
	
	/**
	 * I don't know if this avoids underflow
	 * @param d1
	 * @param d2
	 * @return Returns the log of the sum of two numbers, which are themselves input in log form. 
	 */
	public static double logAdd( double d1, double d2 ) {
		double e = Math.pow( Math.E, (d1+d2) );
		return Math.log( e );
	}
	
	public static double log2( double num ){
		if(num==0){
			return Double.MIN_VALUE;
		}else{
			return (Math.log(num)/Math.log(2));
		}
	}
  // fractional error in math formula less than 1.2 * 10 ^ -7.
  // although subject to catastrophic cancellation when z in very close to 0
  public static double erf(double z) {
      double t = 1.0 / (1.0 + 0.5 * Math.abs(z));

      // use Horner's method
      double ans = 1 - t * Math.exp( -z*z   -   1.26551223 +
                                          t * ( 1.00002368 +
                                          t * ( 0.37409196 + 
                                          t * ( 0.09678418 + 
                                          t * (-0.18628806 + 
                                          t * ( 0.27886807 + 
                                          t * (-1.13520398 + 
                                          t * ( 1.48851587 + 
                                          t * (-0.82215223 + 
                                          t * ( 0.17087277))))))))));
      if (z >= 0) return  ans;
      else        return -ans;
  }

  // fractional error less than x.xx * 10 ^ -4.
  public static double erf2(double z) {
      double t = 1.0 / (1.0 + 0.47047 * Math.abs(z));
      double poly = t * (0.3480242 + t * (-0.0958798 + t * (0.7478556)));
      double ans = 1.0 - poly * Math.exp(-z*z);
      if (z >= 0) return  ans;
      else        return -ans;
  }

  public static double phi(double x) {
      return Math.exp(-0.5 * x * x) / Math.sqrt(2 * Math.PI);
  }

  public static double phi(double x, double mu, double sigma) {
      return phi((x - mu) / sigma) / sigma;
  }

  // accurate with absolute error less than 8 * 10^-16
  // Reference: http://www.jstatsoft.org/v11/i04/v11i04.pdf
  public static double Phi2(double z) {
      if (z >  8.0) return 1.0;    // needed for large values of z
      if (z < -8.0) return 0.0;    // probably not needed
      double sum = 0.0, term = z;
      for (int i = 3; sum + term != sum; i += 2) {
          sum  = sum + term;
          term = term * z * z / i;
      }
      return 0.5 + sum * phi(z);
  }

  // cumulative normal distribution
  public static double Phi(double z) {
      return 0.5 * (1.0 + erf(z / (Math.sqrt(2.0))));
  }

  // cumulative normal distribution with mean mu and std deviation sigma
  public static double Phi(double z, double mu, double sigma) {
      return Phi((z - mu) / sigma);
  }

  // random integer between 0 and N-1
  public static int random(int N) {
      return (int) (Math.random() * N);
  }

  // random number with standard Gaussian distribution
  public static double gaussian() {
      double U = Math.random();
      double V = Math.random();
      return Math.sin(2 * Math.PI * V) * Math.sqrt((-2 * Math.log(1 - U)));
  }

  // random number with Gaussian distribution of mean mu and stddev sigma
  public static double gaussian(double mu, double sigma) {
      return mu + sigma * gaussian();
  }


 /*************************************************************************
  *  Hyperbolic trig functions
  *************************************************************************/
  public static double cosh(double x) {
      return (Math.exp(x) + Math.exp(-x)) / 2.0;
  }

  public static double sinh(double x) {
      return (Math.exp(x) - Math.exp(-x)) / 2.0;
  }

  public static double tanh(double x) {
      return sinh(x) / cosh(x);
  } 

	
}
