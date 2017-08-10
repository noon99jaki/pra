package edu.cmu.lti.algorithm.math;

import java.math.BigDecimal;
import java.util.Map;

public class Util {
	
	/**
	 * Calculate F1 score given Precision and Recall
	 * @param p
	 * @param r
	 * @return f1
	 */
	public static double calcF( double p, double r ) {
		return calcF( p, r, 1 );
	}
	
	/**
	 * Calculate F score given Precision and Recall and beta param
	 * @param p
	 * @param r
	 * @param beta
	 * @return f1
	 */
	public static double calcF( double p, double r, double beta ) {
		if ( roundedMatch( p*r, 0, 6) ) {
			return 0;
		} else {
			double beta2 = beta*beta;
			return (1+beta2)*r*p/(r+beta2*p);
		}
	}

	public static void increment( Map<String, Integer> map, String key ) {
		int i = (map.get(key)==null)?0:map.get(key);
		map.put(key, i+1);
	}
	
	public static void increment( Map<String, Integer> map, String key, int diff ) {
		int i = (map.get(key)==null)?0:map.get(key);
		map.put(key, i+diff);
	}
	
	/**
	 * Calculate the MMS
	 * @param seq1
	 * @param seq2
	 * @return longest common consecutive sequence
	 */
	public static int[] getMMS( String[] seq1, String[] seq2 ) {
		int maxBegin = -1;
		int maxEnd   = -1;
		
		for ( int i=0; i<seq1.length; i++ ) {
			for ( int j=0; j<seq2.length; j++ ) {
				if ( seq1[i].equals(seq2[j]) ) {
					int p = 1;
					int begin = i;
					int end = i+1;
					while ( i+p<seq1.length && 
							j+p<seq2.length &&
							seq1[i+p].equals(seq2[j+p]) ) {
						p++;
						end = i+p;
					}
					if ( end-begin > maxEnd-maxBegin ) {
						maxBegin = begin;
						maxEnd = end;
					}
				}
			}
		}
		int[] result = {maxBegin, maxEnd};
		return result;
	}
	
	/**
	 * Compares double value at rounded value.  
	 * @param d1
	 * @param d2
	 * @param digit to round up
	 * @return matched
	 */
	public static boolean roundedMatch( double d1, double d2, int digit ) {
		try {
			BigDecimal bd1 = new BigDecimal( String.valueOf(d1) );
			String s1 = bd1.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue()+"";
			BigDecimal bd2 = new BigDecimal( String.valueOf(d2) );
			String s2 = bd2.setScale(digit, BigDecimal.ROUND_HALF_UP).doubleValue()+"";
			
			if ( s1.equals(s2) ) {
				return true;
			} else {
				return false;
			}
		} catch ( Exception e ) {
			System.err.println( "d1 = "+d1+", d2 = "+d2 );
			//e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Merge two String[] arrays
	 * @param a1
	 * @param a2
	 * @return merged array
	 */
	public static String[] mergeArrays( String[] a1, String[] a2 ) {
		String[] a12 = new String[a1.length+a2.length];
		int i = 0;
		for ( String s : a1 ) a12[i++] = s; 
		for ( String s : a2 ) a12[i++] = s;
		return a12;
	}
}
