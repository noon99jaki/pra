package edu.cmu.lti.algorithm;

import java.util.Arrays;
import java.util.Map;

import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.optimization.lbfgs.FMath;

/**
 */
public class FArrayD {
	public static double[] clone(double[] x) {
		double[] y = new double[x.length];
		assign(y, x);
		return y;
	}
	public static double[] extend(double[] x, int dim) {
		if (x.length >dim) 
			throw new RuntimeException(
				"cannot retract array size");		
		if (x.length ==dim)
			return x;
		double[] y = new double[dim];
		System.arraycopy(x, 0, y, 0, x.length);
		return y;
	}

	public static double getD(double[] x, int i, double dft){
		if (x.length>i) return x[i];
		return dft;
	}
	
	public static void clear(double[] x) {
		setAll(x,0);
	}
	public static void setAll(double[] x, double d) {
		for(int i=0;i<x.length; ++i)
			x[i]=d;
	}
	public static void plusOn(double[] x, Map<Integer, Double>m, double scale) {
		for (Map.Entry<Integer, Double> e: m.entrySet())
			x[e.getKey()]+=e.getValue()*scale;
	}
	
	public static void plusOnTranslated(
			double[] x, Map<Integer, Double>m, double scale, VectorI vi) {
		for (Map.Entry<Integer, Double> e: m.entrySet()){
			int idx=vi.get(e.getKey()); 
			x[idx]+=e.getValue()*scale;
		}
	}
	
	public static void assign(double[] y, double[] x) {
		if (x.length != y.length) throwDifLen(x.length, y.length);
		System.arraycopy(x, 0, y, 0, x.length);
	}

	public static double inner(double[] x, double[] y) {
		//if (x.length < y.length) 
			//throw new RuntimeException(
				//"diff lengths: " + x.length + " "		+ y.length);
		double result = 0.0;
		int len= Math.min(x.length,y.length);
		for (int i = 0; i < len; i++) 
			result += x[i] * y[i];
		
		return result;
	}

	public static double[] addMultiples(double[] x, double xMultiplier, double[] y, double yMuliplier) {
		//if (x.length > y.length) throwDifLen(x.length, y.length);
		double[] z = new double[Math.max(x.length, y.length)];
		
		if (x.length< y.length){
			for (int i = 0; i < x.length; i++) 
				z[i] = x[i] * xMultiplier + y[i] * yMuliplier;
			for (int i = x.length; i < y.length; i++) 
				z[i] = y[i] * yMuliplier;
		}
		else{
			for (int i = 0; i < y.length; i++) 
				z[i] = x[i] * xMultiplier + y[i] * yMuliplier;
			for (int i = y.length; i < x.length; i++) 
				z[i] = x[i];
			
		}
		return z;
	}

	public static double[] repeat(double c, int length) {
		double[] x = new double[length];
		Arrays.fill(x, c);
		return x;
	}

	public static double[] multiply(double[] x, double[] y) {
		if (x.length > y.length) throwDifLen(x.length, y.length);
		double[] z = new double[y.length];
		for (int i = 0; i < x.length; i++) 
			z[i] = x[i] * y[i];
		for (int i = x.length; i < y.length; i++) 
			z[i]=0;
		return z;
	}

	public static String toString(double[] x) {
		return toString(x, x.length);
	}

	public static String format="%.2f";// nDigit=2;
	public static String toString(double[] x, int length) {
		StringBuffer sb = new StringBuffer();
		sb.append("[");
		for (int i = 0; i < FMath.min(x.length, length); i++) {
			sb.append(String.format(format,x[i]));
			if (i + 1 < FMath.min(x.length, length)) sb.append(", ");
		}
		if (length<x.length)sb.append("...");
		sb.append("]");
		return sb.toString();
	}

	public static void scale(double[] x, double d) {
		if (d == 1.0) return;
		for (int i = 0; i < x.length; i++) 
			x[i] *= d;		
	}
	
	public static void removeSmallElements(double[] x, double d) {
		if (d == 0.0) return;
		for (int i = 0; i < x.length; i++)
			if (Math.abs(x[i])<d)
				x[i] =0;		
	}
	//remove anything larger than d or smaller than -d
	public static void decapitate(double[] x, double d) {
		if (d == 0.0) return;
		for (int i = 0; i < x.length; i++)
			x[i]= decapitate(x[i],d);
	}
	public static double decapitate(double x, double d) {
		if (x>d)
			return d;		
		else if (x<-d)
			return -d;		
		else
			return x;	
	}
	public static void truncate(double[] x, double d) {
		if (d == 0.0) return;
		for (int i = 0; i < x.length; i++)
			x[i]= truncate(x[i],d);
		/*	if (x[i]>d)
				x[i] -= d;		
			else if (x[i]<-d)
				x[i] += d;		
			else
				x[i] =0;		*/
	}
	public static double truncate(double x, double d) {
		if (x>d)
			return x - d;		
		else if (x<-d)
			return x + d;		
		else
			return 0;	
	}
	public static double[] multiply(double[] x, double s) {
		double[] result = new double[x.length];
		if (s == 1.0) {
			System.arraycopy(x, 0, result, 0, x.length);
			return result;
		}
		for (int i = 0; i < x.length; i++) {
			result[i] = x[i] * s;
		}
		return result;
	}

	public static int findMax(double[] v) {
		int maxI = -1;
		double maxV = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < v.length; i++) {
			if (v[i] > maxV) {
				maxV = v[i];
				maxI = i;
			}
		}
		return maxI;
	}

	public static double max(double[] v) {
		double maxV = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < v.length; i++) {
			if (v[i] > maxV) {
				maxV = v[i];
			}
		}
		return maxV;
	}

	public static short max(short[] v) {
		short maxV = Short.MIN_VALUE;
		for (int i = 0; i < v.length; i++) {
			if (v[i] > maxV) {
				maxV = v[i];
			}
		}
		return maxV;
	}

	public static double sum(double[] a) {
		double result = 0;
		for (int i = 0; i < a.length; i++) 
			result += a[i];		
		return result;
	}

	public static double[] add(double[] a, double b) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			double v = a[i];
			result[i] = v + b;
		}
		return result;
	}

	public static double norm2(double[] x) {
		return Math.sqrt(inner(x, x));
	}
	

	public static double norm1(double[] a) {
		double result = 0;
		for (int i = 0; i < a.length; i++) 
			result += Math.abs(a[i]);		
		return result;
	}
	public static double normInf(double[] a) {
		double result = 0;
		for (int i = 0; i < a.length; i++) {
			double d=Math.abs(a[i]);
			if (d>result)
				result = d;		
		}
		return result;
	}
	
	public static double[] add(double[] x, double[] y) {
		if (x.length != y.length) throwDifLen(x.length, y.length);
		
		double[] result = new double[x.length];
		for (int i = 0; i < x.length; i++) {
			result[i] = x[i] + y[i];
		}
		return result;
	}

	public static void throwDifLen(int x, int y){
		throw new RuntimeException(	"diff lengths: "+x+" "+y);

	}
	public static double[] minus(double[] x, double[] y) {
		if (x.length < y.length) 
			throwDifLen(x.length, y.length);
		
		double[] result = new double[x.length];
		for (int i = 0; i < y.length; i++) 
			result[i] = x[i] - y[i];
		for (int i = y.length; i < x.length; i++) 
			result[i] = x[i];
		
		return result;
	}

	public static double[] exp(double[] pUnexponentiated) {
		double[] exponentiated = new double[pUnexponentiated.length];
		for (int index = 0; index < pUnexponentiated.length; index++) {
			exponentiated[index] = FMath.exp(pUnexponentiated[index]);
		}
		return exponentiated;
	}

	public static double[] log(double[] pUnexponentiated) {
		double[] exponentiated = new double[pUnexponentiated.length];
		for (int index = 0; index < pUnexponentiated.length; index++) {
			exponentiated[index] = Math.log(pUnexponentiated[index]);
		}
		return exponentiated;
	}

	public static void initialize(double[] x, double d) {
		Arrays.fill(x, d);
	}

	public static void initialize(Object[] x, double d) {
		for (int i = 0; i < x.length; i++) {
			Object o = x[i];
			if (o instanceof double[]) initialize((double[]) o, d);
			else initialize((Object[]) o, d);
		}
	}

	public static void subtract(double[] x, double[] mean, double[] retVal) {
		assert mean != null;
		for (int i = 0; i < x.length; ++i) {
			retVal[i] = x[i] - mean[i];
		}
	}

	public static void add(double[] x, double[] mean, double[] retVal) {
		assert mean != null;
		for (int i = 0; i < x.length; ++i) {
			retVal[i] = x[i] + mean[i];
		}
	}

	public static void multiply(double[] x, double c, double[] retVal) {
		assert x != null;
		for (int i = 0; i < x.length; ++i) {
			retVal[i] = x[i] * c;
		}
	}

	/*
	 * project x onto a orthant defined by y
	 */
	public static void project(double[] dir, double[] g) {
		for (int i = 0; i < dir.length; i++) 
			if (dir[i] * g[i] <= 0) dir[i] = 0;		
	}

	/*
	 * project x onto a orthant defined by y
	 */
	public static void project2(double[] x, double[] xOld) {
		for (int i = 0; i < xOld.length; i++) 
			if (x[i] * xOld[i] < 0) x[i] = 0;
	}
	
	public static int countNonZero(double[] vd){
		int n=0;
		for (double d:vd)
			if (d!=0.0) ++n;
		return n;
	}
	
	public static String join(double[]v, String format, String c, int ib, int ie) {
		ie= Math.min(ie, v.length);
		StringBuffer sb = new StringBuffer();
		for (int i = ib; i < ie; i++) {
			if (i > ib)	sb.append(c);
			sb.append(String.format(format,v[i]));
		}
		return (sb.toString());
	}
	
	public static String join(double[]v,String format, String c, int ib) {
		return join(v, format, c, ib,v.length);
	}
	
	public static String join(double[]v,String format,  String c) {
		return join(v,format, c, 0);
	}
	public static String join(double[] v) {
		return join(v, "%.3f", " ");
	}
	public static double sum(double[] v, int[] ids) {
		double score =0;
		for (int id: ids) score += v[id];
		return score;
	}
	
	public static void plusOn(double[] v, int[] ids, double x) {
		for (int id: ids) v[id] += x;
	}
}
