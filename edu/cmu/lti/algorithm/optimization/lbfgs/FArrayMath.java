package edu.cmu.lti.algorithm.optimization.lbfgs;


import java.text.NumberFormat;
import java.util.Random;
import java.io.IOException;


/**
 * Class ArrayMath
 * 
 * @author Teg Grenager
 */
public class FArrayMath {

	// CASTS

	public static float[] toFloat(double a[]) {
		float[] result = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = (float) a[i];
		}
		return result;
	}

	public static double[] toDouble(float a[]) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = (double) a[i];
		}
		return result;
	}

	public static double[][] toDouble(float a[][]) {
		double[][] result = new double[a.length][];
		for (int i = 0; i < a.length; i++) {
			result[i] = new double[a[i].length];
			for (int j = 0; j < a[i].length; j++) {
				result[i][j] = (double) a[i][j];
			}
		}
		return result;
	}

	public static float[][] toFloat(double a[][]) {
		float[][] result = new float[a.length][];
		for (int i = 0; i < a.length; i++) {
			result[i] = new float[a[i].length];
			for (int j = 0; j < a[i].length; j++) {
				result[i][j] = (float) a[i][j];
			}
		}
		return result;
	}

	// ARITHMETIC FUNCTIONS

	public static double[] add(double[] a, double c) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] + c;
		}
		return result;
	}

	public static void addOn(int[][] a, int[][] b) {
		if (a == null || b == null)
			return;
		if (a.length != b.length)
			return;
		for (int i = 0; i < a.length; ++i) {
			addOn(a[i], b[i]);
		}
	}

	public static void addOn(int[] a, int b) {
		for (int i = 0; i < a.length; i++) {
			a[i] += b;
		}
	}

	public static void addOn(int[] a, int[] b) {
		for (int i = 0; i < a.length; i++) {
			a[i] += b[i];
		}
	}

	public static void addOn(double[] a, double b) {
		for (int i = 0; i < a.length; i++) {
			a[i] += b;
		}
	}

	public static void addOn(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			a[i] += b[i];
		}
	}

	public static void subtractInPlace(double[] a, double[] b) {
		for (int i = 0; i < a.length; i++) {
			a[i] -= b[i];
		}
	}

	public static double[] add(double[] a, double[] b) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] + b[i];
		}
		return result;
	}

	public static float[] add(float[] a, float[] b) {
		float[] result = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] + b[i];
		}
		return result;
	}

	public static double[] subtract(double[] a, double[] b) {
		double[] c = new double[a.length];

		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}

	public static float[] subtract(float[] a, float[] b) {
		float[] c = new float[a.length];

		for (int i = 0; i < a.length; i++) {
			c[i] = a[i] - b[i];
		}
		return c;
	}

	/**
	 * Scales the values in this array by c.
	 */
	public static double[] multiply(double[] a, double c) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] * c;
		}
		return result;
	}

	/**
	 * Scales the values in this array by c.
	 */
	public static int[] multiply(int[] a, int c) {
		int[] result = new int[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] * c;
		}
		return result;
	}

	/**
	 * Scales in place the values in this array by c.
	 */
	public static void multiplyInPlace(double[] a, double c) {
		for (int i = 0; i < a.length; i++) 
			a[i] = a[i] * c;		
	}

	/**
	 * Scales the values in this array by c.
	 */
	public static float[] multiply(float[] a, float c) {
		float[] result = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = a[i] * c;
		}
		return result;
	}

	public static double average(double[] a) {
		double total = FArrayMath.sum(a);
		return total / (double) a.length;
	}

	/**
	 * Scales the values in this array by c.
	 */
	public static float[] pow(float[] a, float c) {
		float[] result = new float[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = (float) Math.pow(a[i], c);
		}
		return result;
	}

	/**
	 * Scales the values in this array by c.
	 */
	public static void powInPlace(float[] a, float c) {
		for (int i = 0; i < a.length; i++) {
			a[i] = (float) Math.pow(a[i], c);
		}
	}

	/**
	 * Scales the values in this array by c.
	 */
	public static void powInPlace(double[] a, double c) {
		for (int i = 0; i < a.length; i++) {
			a[i] = Math.pow(a[i], c);
		}
	}

	/**
	 * Scales the values in this array by c.
	 */
	public static double[] pow(double[] a, double c) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = Math.pow(a[i], c);
		}
		return result;
	}

	public static boolean hasNaN(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (Double.isNaN(a[i]))
				return true;
		}
		return false;
	}

	public static boolean hasZero(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (a[i] == 0.0)
				return true;
		}
		return false;
	}

	public static boolean hasInfinite(double[] a) {
		for (int i = 0; i < a.length; i++) {
			if (Double.isInfinite(a[i]))
				return true;
		}
		return false;
	}

	/**
	 * Computes inf-norm of vector
	 * 
	 * @param a
	 * @return inf-norm of a
	 */
	public static double norm_inf(double[] a) {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < a.length; i++) {
			if (Math.abs(a[i]) > max) {
				max = Math.abs(a[i]);
			}
		}
		return max;
	}

	/**
	 * Computes inf-norm of vector
	 * 
	 * @param a
	 * @return inf-norm of a
	 */
	public static double norm_inf(float[] a) {
		double max = Double.NEGATIVE_INFINITY;
		for (int i = 0; i < a.length; i++) {
			if (Math.abs(a[i]) > max) {
				max = Math.abs(a[i]);
			}
		}
		return max;
	}

	/**
	 * Computes 1-norm of vector
	 * 
	 * @param a
	 * @return 1-norm of a
	 */
	public static double norm_1(double[] a) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += (a[i] < 0 ? -a[i] : a[i]);
		}
		return sum;
	}

	/**
	 * Computes 1-norm of vector
	 * 
	 * @param a
	 * @return 1-norm of a
	 */
	public static double norm_1(float[] a) {
		double sum = 0;
		for (int i = 0; i < a.length; i++) {
			sum += (a[i] < 0 ? -a[i] : a[i]);
		}
		return sum;
	}

	/**
	 * Computes 2-norm of vector
	 * 
	 * @param a
	 * @return Euclidean norm of a
	 */
	public static double norm(double[] a) {
		double squaredSum = 0;
		for (int i = 0; i < a.length; i++) {
			squaredSum += a[i] * a[i];
		}
		return Math.sqrt(squaredSum);
	}

	/**
	 * Computes 2-norm of vector
	 * 
	 * @param a
	 * @return Euclidean norm of a
	 */
	public static double norm(float[] a) {
		double squaredSum = 0;
		for (int i = 0; i < a.length; i++) {
			squaredSum += a[i] * a[i];
		}
		return Math.sqrt(squaredSum);
	}

	/**
	 * Scales the values in this array by b. Does it in place.
	 */
	public static void scale(double[] a, double b) {
		for (int i = 0; i < a.length; i++) {
			a[i] = a[i] * b;
		}
	}

	/**
	 * Scales the values in this array by b. Does it in place.
	 */
	public static void scale(float[] a, double b) {
		for (int i = 0; i < a.length; i++) {
			a[i] = (float) (a[i] * b);
		}
	}

	/**
	 * Shifts the values in this array by b. Does it in place.
	 */
	public static void shift(double[] a, double b) {
		for (int i = 0; i < a.length; i++) {
			a[i] = a[i] + b;
		}
	}

	/**
	 * Assumes that both arrays have same length.
	 */
	public static double[] pairwiseMultiply(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new RuntimeException();
		}
		double[] result = new double[a.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = a[i] * b[i];
		}
		return result;
	}

	/**
	 * Assumes that both arrays have same length.
	 */
	public static float[] pairwiseMultiply(float[] a, float[] b) {
		if (a.length != b.length) {
			throw new RuntimeException();
		}
		float[] result = new float[a.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = a[i] * b[i];
		}
		return result;
	}

	/**
	 * Puts the result in the result array. Assumes that all arrays have same
	 * length.
	 */
	public static void pairwiseMultiply(double[] a, double[] b, double[] result) {
		if (a.length != b.length) {
			throw new RuntimeException();
		}
		for (int i = 0; i < result.length; i++) {
			result[i] = a[i] * b[i];
		}
	}

	/**
	 * Puts the result in the result array. Assumes that all arrays have same
	 * length.
	 */
	public static void pairwiseMultiply(float[] a, float[] b, float[] result) {
		if (a.length != b.length) {
			throw new RuntimeException();
		}
		for (int i = 0; i < result.length; i++) {
			result[i] = a[i] * b[i];
		}
	}

	public static double[] pairwiseAdd(double[] a, double[] b) {
		if (a.length != b.length) {
			throw new RuntimeException();
		}
		double[] result = new double[a.length];
		for (int i = 0; i < result.length; i++) {
			result[i] = a[i] + b[i];
		}
		return result;
	}

	public static double sum(double[] a) {
		double result = 0.0;
		for (int i = 0; i < a.length; i++) {
			result += a[i];
		}
		return result;
	}

	public static int sum(int[] a) {
		int result = 0;
		for (int i = 0; i < a.length; i++) {
			result += a[i];
		}
		return result;
	}

	public static float sum(float[] a) {
		float result = 0.0F;
		for (int i = 0; i < a.length; i++) {
			result += a[i];
		}
		return result;
	}

	/**
	 * @return the index of the max value; if max is a tie, returns the first one.
	 */
	public static int argmax(double[] a) {
		double max = Double.NEGATIVE_INFINITY;
		int argmax = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
				argmax = i;
			}
		}
		return argmax;
	}

	public static double max(double[] a) {
		return a[argmax(a)];
	}

	public static int max(int[] a) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}

	public static int max(short[] a) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}

	public static int max(Integer[] a) {
		int max = Integer.MIN_VALUE;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
			}
		}
		return max;
	}

	/**
	 * @return the index of the max value; if max is a tie, returns the first one.
	 */
	public static int argmax(float[] a) {
		float max = Float.NEGATIVE_INFINITY;
		int argmax = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] > max) {
				max = a[i];
				argmax = i;
			}
		}
		return argmax;
	}

	public static float max(float[] a) {
		return a[argmax(a)];
	}

	/**
	 * @return the index of the max value; if max is a tie, returns the first one.
	 */
	public static int argmin(double[] a) {
		double min = Double.POSITIVE_INFINITY;
		int argmin = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] < min) {
				min = a[i];
				argmin = i;
			}
		}
		return argmin;
	}

	public static double min(double[] a) {
		return a[argmin(a)];
	}

	/**
	 * @return the index of the max value; if max is a tie, returns the first one.
	 */
	public static int argmin(float[] a) {
		float min = Float.POSITIVE_INFINITY;
		int argmin = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] < min) {
				min = a[i];
				argmin = i;
			}
		}
		return argmin;
	}

	public static float min(float[] a) {
		return a[argmin(a)];
	}

	/**
	 * @return the index of the max value; if max is a tie, returns the first one.
	 */
	public static int argmin(int[] a) {
		int min = Integer.MAX_VALUE;
		int argmin = 0;
		for (int i = 0; i < a.length; i++) {
			if (a[i] < min) {
				min = a[i];
				argmin = i;
			}
		}
		return argmin;
	}

	public static int min(int[] a) {
		return a[argmin(a)];
	}

	public static double[] exp(double[] a) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = Math.exp(a[i]);
		}
		return result;
	}

	public static double[] log(double[] a) {
		double[] result = new double[a.length];
		for (int i = 0; i < a.length; i++) {
			result[i] = Math.log(a[i]);
		}
		return result;
	}

	public static void expInPlace(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = Math.exp(a[i]);
		}
	}

	public static void logInPlace(double[] a) {
		for (int i = 0; i < a.length; i++) {
			a[i] = Math.log(a[i]);
		}
	}

	// LINEAR ALGEBRAIC FUNCTIONS

	public static double innerProduct(double[] a, double[] b) {
		double result = 0.0;
		for (int i = 0; i < a.length; i++) {
			result += a[i] * b[i];
		}
		return result;
	}

	public static double innerProduct(float[] a, float[] b) {
		double result = 0.0;
		for (int i = 0; i < a.length; i++) {
			result += a[i] * b[i];
		}
		return result;
	}

	// UTILITIES

	public static int[] subArray(int[] a, int from, int to) {
		int[] result = new int[to - from];
		System.arraycopy(a, from, result, 0, to - from);
		return result;
	}

	public static String toString(double[] a) {
		return toString(a, null);
	}

	public static String toString(double[] a, NumberFormat nf) {
		if (a == null)
			return null;
		if (a.length == 0)
			return "[]";
		StringBuffer b = new StringBuffer();
		b.append("[");
		for (int i = 0; i < a.length - 1; i++) {
			String s;
			if (nf == null) {
				s = String.valueOf(a[i]);
			} else {
				s = nf.format(a[i]);
			}
			b.append(s);
			b.append(", ");
		}
		String s;
		if (nf == null) {
			s = String.valueOf(a[a.length - 1]);
		} else {
			s = nf.format(a[a.length - 1]);
		}
		b.append(s);
		b.append(']');
		return b.toString();
	}

	public static String toString(float[] a) {
		return toString(a, null);
	}

	public static String toString(float[] a, NumberFormat nf) {
		if (a == null)
			return null;
		if (a.length == 0)
			return "[]";
		StringBuffer b = new StringBuffer();
		b.append("[");
		for (int i = 0; i < a.length - 1; i++) {
			String s;
			if (nf == null) {
				s = String.valueOf(a[i]);
			} else {
				s = nf.format(a[i]);
			}
			b.append(s);
			b.append(", ");
		}
		String s;
		if (nf == null) {
			s = String.valueOf(a[a.length - 1]);
		} else {
			s = nf.format(a[a.length - 1]);
		}
		b.append(s);
		b.append(']');
		return b.toString();
	}

	public static String toString(int[] a) {
		return toString(a, null);
	}

	public static String toString(int[] a, NumberFormat nf) {
		if (a == null)
			return null;
		if (a.length == 0)
			return "[]";
		StringBuffer b = new StringBuffer();
		b.append("[");
		for (int i = 0; i < a.length - 1; i++) {
			String s;
			if (nf == null) {
				s = String.valueOf(a[i]);
			} else {
				s = nf.format(a[i]);
			}
			b.append(s);
			b.append(", ");
		}
		String s;
		if (nf == null) {
			s = String.valueOf(a[a.length - 1]);
		} else {
			s = nf.format(a[a.length - 1]);
		}
		b.append(s);
		b.append(']');
		return b.toString();
	}

	public static String toString(byte[] a) {
		return toString(a, null);
	}

	public static String toString(byte[] a, NumberFormat nf) {
		if (a == null)
			return null;
		if (a.length == 0)
			return "[]";
		StringBuffer b = new StringBuffer();
		b.append("[");
		for (int i = 0; i < a.length - 1; i++) {
			String s;
			if (nf == null) {
				s = String.valueOf(a[i]);
			} else {
				s = nf.format(a[i]);
			}
			b.append(s);
			b.append(", ");
		}
		String s;
		if (nf == null) {
			s = String.valueOf(a[a.length - 1]);
		} else {
			s = nf.format(a[a.length - 1]);
		}
		b.append(s);
		b.append(']');
		return b.toString();
	}

	// PROBABILITY FUNCTIONS

	/**
	 * Makes the values in this array sum to 1.0. Does it in place. If the total
	 * is 0.0, sets a to the uniform distribution.
	 */
	public static void normalize(double[] a) {
		double total = sum(a);
		if (total == 0.0) {
			throw new RuntimeException("Can't normalize an array with sum 0.0");
		}
		scale(a, 1.0 / total); // divide each value by total
	}

	/**
	 * Makes the values in this array sum to 1.0. Does it in place. If the total
	 * is 0.0, sets a to the uniform distribution.
	 */
	public static void normalize(float[] a) {
		float total = sum(a);
		if (total == 0.0) {
			throw new RuntimeException("Can't normalize an array with sum 0.0");
		}
		scale(a, 1.0F / total); // divide each value by total
	}

	/**
	 * Makes the values in this array sum to 1.0. Does it in place. If the total
	 * is 0.0, sets a to the uniform distribution.
	 */
	public static void logNormalize(double[] a) {
		double logTotal = logSum(a);
		if (logTotal == Double.NEGATIVE_INFINITY) {
			// to avoid NaN values
			double v = -Math.log((double) a.length);
			for (int i = 0; i < a.length; i++) {
				a[i] = v;
			}
			return;
		}
		shift(a, -logTotal); // subtract log total from each value
	}

	private static Random rand = new Random();

	/**
	 * Samples from the distribution over values 0 through d.length given by d.
	 * Assumes that the distribution sums to 1.0.
	 * 
	 * @param d
	 *          the distribution to sample from
	 * @return a value from 0 to d.length
	 */
	public static int sampleFromDistribution(double[] d) {
		return sampleFromDistribution(d, rand);
	}

	/**
	 * Samples from the distribution over values 0 through d.length given by d.
	 * Assumes that the distribution sums to 1.0.
	 * 
	 * @param d
	 *          the distribution to sample from
	 * @return a value from 0 to d.length
	 */
	public static int sampleFromDistribution(double[] d, Random random) {
		// sample from the uniform [0,1]
		double r = random.nextDouble();
		// now compare its value to cumulative values to find what interval it falls
		// in
		double total = 0;
		for (int i = 0; i < d.length - 1; i++) {
			if (Double.isNaN(d[i])) {
				throw new RuntimeException("Can't sample from NaN");
			}
			total += d[i];
			if (r < total) {
				return i;
			}
		}
		return d.length - 1; // in case the "double-math" didn't total to exactly
													// 1.0
	}

	/**
	 * Samples from the distribution over values 0 through d.length given by d.
	 * Assumes that the distribution sums to 1.0.
	 * 
	 * @param d
	 *          the distribution to sample from
	 * @return a value from 0 to d.length
	 */
	public static int sampleFromDistribution(float[] d, Random random) {
		// sample from the uniform [0,1]
		double r = random.nextDouble();
		// now compare its value to cumulative values to find what interval it falls
		// in
		double total = 0;
		for (int i = 0; i < d.length - 1; i++) {
			if (Float.isNaN(d[i])) {
				throw new RuntimeException("Can't sample from NaN");
			}
			total += d[i];
			if (r < total) {
				return i;
			}
		}
		return d.length - 1; // in case the "double-math" didn't total to exactly
													// 1.0
	}

	public static double klDivergence(double[] from, double[] to) {
		double kl = 0.0;
		double tot = sum(from);
		double tot2 = sum(to);
		// System.out.println("tot is " + tot + " tot2 is " + tot2);
		for (int i = 0; i < from.length; i++) {
			if (from[i] == 0.0) {
				continue;
			}
			double num = from[i] / tot;
			double num2 = to[i] / tot2;
			// System.out.println("num is " + num + " num2 is " + num2);
			kl += num * (Math.log(num / num2) / Math.log(2.0));
		}
		return kl;

	}

	public static void setToLogDeterministic(float[] a, int i) {
		for (int j = 0; j < a.length; j++) {
			if (j == i) {
				a[j] = 0.0F;
			} else {
				a[j] = Float.NEGATIVE_INFINITY;
			}
		}
	}

	public static void setToLogDeterministic(double[] a, int i) {
		for (int j = 0; j < a.length; j++) {
			if (j == i) {
				a[j] = 0.0;
			} else {
				a[j] = Double.NEGATIVE_INFINITY;
			}
		}
	}

	// SAMPLE ANALYSIS

	public static double mean(double[] a) {
		return sum(a) / (double) a.length;
	}

	public static double sumSquaredError(double[] a) {
		double mean = mean(a);
		double result = 0.0;
		for (int i = 0; i < a.length; i++) {
			double diff = a[i] - mean;
			result += (diff * diff);
		}
		return result;
	}

	public static double variance(double[] a) {
		return sumSquaredError(a) / (double) (a.length - 1);
	}

	public static double stdev(double[] a) {
		return Math.sqrt(variance(a));
	}

	public static double standardErrorOfMean(double[] a) {
		return stdev(a) / Math.sqrt((double) a.length);
	}

	// PRINTING FUNCTIONS

	public static String toString(int[][] counts) {
		return toString(counts, 10, null, null, NumberFormat.getInstance(), false);
	}

	public static String toString(int[][] counts, int cellSize,
			Object[] rowLabels, Object[] colLabels, NumberFormat nf,
			boolean printTotals) {
		// first compute row totals and column totals
		int[] rowTotals = new int[counts.length];
		int[] colTotals = new int[counts[0].length]; // assume it's square
		int total = 0;
		for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts[i].length; j++) {
				rowTotals[i] += counts[i][j];
				colTotals[j] += counts[i][j];
				total += counts[i][j];
			}
		}
		StringBuffer result = new StringBuffer();
		// column labels
		if (colLabels != null) {
			result.append(FString.padLeft("", cellSize));
			for (int j = 0; j < counts[0].length; j++) {
				String s = colLabels[j].toString();
				if (s.length() > cellSize - 1) {
					s = s.substring(0, cellSize - 1);
				}
				s = FString.padLeft(s, cellSize);
				result.append(s);
			}
			if (printTotals) {
				result.append(FString.padLeft("Total", cellSize));
			}
			result.append("\n\n");
		}
		for (int i = 0; i < counts.length; i++) {
			// row label
			if (rowLabels != null) {
				String s = rowLabels[i].toString();
				s = FString.padOrTrim(s, cellSize); // left align this guy only
				result.append(s);
			}
			// value
			for (int j = 0; j < counts[i].length; j++) {
				result.append(FString.padLeft(nf.format(counts[i][j]), cellSize));
			}
			// the row total
			if (printTotals) {
				result.append(FString.padLeft(nf.format(rowTotals[i]), cellSize));
			}
			result.append("\n");
		}
		result.append("\n");
		// the col totals
		if (printTotals) {
			result.append(FString.pad("Total", cellSize));
			for (int j = 0; j < colTotals.length; j++) {
				result.append(FString.padLeft(nf.format(colTotals[j]), cellSize));
			}
			result.append(FString.padLeft(nf.format(total), cellSize));
		}
		result.append("\n");
		return result.toString();
	}

	public static String toString(double[][] counts) {
		return toString(counts, 10, null, null, NumberFormat.getInstance(), false);
	}

	public static String toString(double[][] counts, int cellSize,
			Object[] rowLabels, Object[] colLabels, NumberFormat nf,
			boolean printTotals) {
		if (counts == null)
			return null;
		// first compute row totals and column totals
		double[] rowTotals = new double[counts.length];
		double[] colTotals = new double[counts[0].length]; // assume it's square
		double total = 0.0;
		for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts[i].length; j++) {
				rowTotals[i] += counts[i][j];
				colTotals[j] += counts[i][j];
				total += counts[i][j];
			}
		}
		StringBuffer result = new StringBuffer();
		// column labels
		if (colLabels != null) {
			result.append(FString.padLeft("", cellSize));
			for (int j = 0; j < counts[0].length; j++) {
				String s = colLabels[j].toString();
				if (s.length() > cellSize - 1) {
					s = s.substring(0, cellSize - 1);
				}
				s = FString.padLeft(s, cellSize);
				result.append(s);
			}
			if (printTotals) {
				result.append(FString.padLeft("Total", cellSize));
			}
			result.append("\n\n");
		}
		for (int i = 0; i < counts.length; i++) {
			// row label
			if (rowLabels != null) {
				String s = rowLabels[i].toString();
				s = FString.pad(s, cellSize); // left align this guy only
				result.append(s);
			}
			// value
			for (int j = 0; j < counts[i].length; j++) {
				result.append(FString.padLeft(nf.format(counts[i][j]), cellSize));
			}
			// the row total
			if (printTotals) {
				result.append(FString.padLeft(nf.format(rowTotals[i]), cellSize));
			}
			result.append("\n");
		}
		result.append("\n");
		// the col totals
		if (printTotals) {
			result.append(FString.pad("Total", cellSize));
			for (int j = 0; j < colTotals.length; j++) {
				result.append(FString.padLeft(nf.format(colTotals[j]), cellSize));
			}
			result.append(FString.padLeft(nf.format(total), cellSize));
		}
		result.append("\n");
		return result.toString();
	}

	public static String toString(float[][] counts) {
		return toString(counts, 10, null, null, NumberFormat.getIntegerInstance(),
				false);
	}

	public static String toString(float[][] counts, int cellSize,
			Object[] rowLabels, Object[] colLabels, NumberFormat nf,
			boolean printTotals) {
		// first compute row totals and column totals
		double[] rowTotals = new double[counts.length];
		double[] colTotals = new double[counts[0].length]; // assume it's square
		double total = 0.0;
		for (int i = 0; i < counts.length; i++) {
			for (int j = 0; j < counts[i].length; j++) {
				rowTotals[i] += counts[i][j];
				colTotals[j] += counts[i][j];
				total += counts[i][j];
			}
		}
		StringBuffer result = new StringBuffer();
		// column labels
		if (colLabels != null) {
			result.append(FString.padLeft("", cellSize));
			for (int j = 0; j < counts[0].length; j++) {
				String s = colLabels[j].toString();
				if (s.length() > cellSize - 1) {
					s = s.substring(0, cellSize - 1);
				}
				s = FString.padLeft(s, cellSize);
				result.append(s);
			}
			if (printTotals) {
				result.append(FString.padLeft("Total", cellSize));
			}
			result.append("\n\n");
		}
		for (int i = 0; i < counts.length; i++) {
			// row label
			if (rowLabels != null) {
				String s = rowLabels[i].toString();
				s = FString.pad(s, cellSize); // left align this guy only
				result.append(s);
			}
			// value
			for (int j = 0; j < counts[i].length; j++) {
				result.append(FString.padLeft(nf.format(counts[i][j]), cellSize));
			}
			// the row total
			if (printTotals) {
				result.append(FString.padLeft(nf.format(rowTotals[i]), cellSize));
			}
			result.append("\n");
		}
		result.append("\n");
		// the col totals
		if (printTotals) {
			result.append(FString.pad("Total", cellSize));
			for (int j = 0; j < colTotals.length; j++) {
				result.append(FString.padLeft(nf.format(colTotals[j]), cellSize));
			}
			result.append(FString.padLeft(nf.format(total), cellSize));
		}
		result.append("\n");
		return result.toString();
	}

	/**
	 * Returns the log of the sum of an array of numbers, which are themselves
	 * input in log form. This is all natural logarithms. Reasonable care is taken
	 * to do this as efficiently as possible (under the assumption that the
	 * numbers might differ greatly in magnitude), with high accuracy, and without
	 * numerical overflow.
	 * 
	 * @param logInputs
	 *          An array of numbers [log(x1), ..., log(xn)]
	 * @return log(x1 + ... + xn)
	 */
	public static double logSum(double[] logInputs) {
		int leng = logInputs.length;
		if (leng == 0) {
			throw new IllegalArgumentException();
		}
		int maxIdx = 0;
		double max = logInputs[0];
		for (int i = 1; i < leng; i++) {
			if (logInputs[i] > max) {
				maxIdx = i;
				max = logInputs[i];
			}
		}
		boolean haveTerms = false;
		double intermediate = 0.0;
		double cutoff = max - FMath.LOGTOLERANCE;
		// we avoid rearranging the array and so test indices each time!
		for (int i = 0; i < leng; i++) {
			if (i != maxIdx && logInputs[i] > cutoff) {
				haveTerms = true;
				intermediate += Math.exp(logInputs[i] - max);
			}
		}
		if (haveTerms) {
			return max + Math.log(1.0 + intermediate);
		} else {
			return max;
		}
	}

	/**
	 * Returns the log of the sum of an array of numbers, which are themselves
	 * input in log form. This is all natural logarithms. Reasonable care is taken
	 * to do this as efficiently as possible (under the assumption that the
	 * numbers might differ greatly in magnitude), with high accuracy, and without
	 * numerical overflow.
	 * 
	 * @param logInputs
	 *          An array of numbers [log(x1), ..., log(xn)]
	 * @return log(x1 + ... + xn)
	 */
	public static float logSum(float[] logInputs) {
		int leng = logInputs.length;
		if (leng == 0) {
			throw new IllegalArgumentException();
		}
		int maxIdx = 0;
		float max = logInputs[0];
		for (int i = 1; i < leng; i++) {
			if (logInputs[i] > max) {
				maxIdx = i;
				max = logInputs[i];
			}
		}
		boolean haveTerms = false;
		double intermediate = 0.0f;
		float cutoff = max - FMath.LOGTOLERANCE_F;
		// we avoid rearranging the array and so test indices each time!
		for (int i = 0; i < leng; i++) {
			if (i != maxIdx && logInputs[i] > cutoff) {
				haveTerms = true;
				intermediate += Math.exp(logInputs[i] - max);
			}
		}
		if (haveTerms) {
			return max + (float) Math.log(1.0 + intermediate);
		} else {
			return max;
		}
	}

	public static double[][] load2DMatrixFromFile(String filename)
			throws IOException {
		String s = FString.slurpFile(filename);
		String[] rows = s.split("[\r\n]+");
		double[][] result = new double[rows.length][];
		for (int i = 0; i < result.length; i++) {
			String[] columns = rows[i].split("\\s+");
			result[i] = new double[columns.length];
			for (int j = 0; j < result[i].length; j++) {
				result[i][j] = Double.parseDouble(columns[j]);
			}
		}
		return result;
	}

	public static void squareMatrixMultiply(double[][] A, double[][] B,
			double[][] result, double[] Bcolj) {

		for (int j = 0; j < B.length; j++) {
			for (int k = 0; k < B.length; k++) {
				Bcolj[k] = B[k][j];
			}
			for (int i = 0; i < B.length; i++) {
				double[] Arowi = A[i];
				double s = 0;
				for (int k = 0; k < B.length; k++) {
					s += Arowi[k] * Bcolj[k];
				}
				result[i][j] = s;
			}
		}

	}
}
