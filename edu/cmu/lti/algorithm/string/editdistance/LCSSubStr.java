package edu.cmu.lti.algorithm.string.editdistance;


import java.util.Arrays;


/**
 * The longest common substring problem is to find 
 * the longest string (or strings) that is a substring 
 * (or are substrings) of two or more strings. It should 
 * not be confused with the longest common subsequence 
 * problem. (For an explanation of the difference between 
 * a substring and a subsequence, see substring).
 * 
 * Source: http://en.wikipedia.org/wiki/Longest_common_substring_problem
 * 
 * @author hideki
 *
 */
public class LCSSubStr {
		
	private final static boolean debug = false;
	
	/**
	 * TODO: use hash instead of array
	 * TODO: output multiple answer
	 * @param x
	 * @param y
	 * @param delimiter
	 * @return LCSubstr
	 */
	public static String getLCSubstr( String[] x, String[] y, String delimiter ) {
		int[][] c = getLCSuff(x, y);
		
		int argmaxX = 0;
		int max = 0;
		for ( int i=1; i<=x.length; i++ ) {
			for ( int j=1; j<=y.length; j++ ) {
				if ( c[i][j] > max ) {
					argmaxX = i;
					max = c[i][j];
				}
			}
		}
		
		StringBuilder sb = new StringBuilder();
		for ( int i=argmaxX-max+1; i<argmaxX+1; i++ ) {
			sb.append( (i>argmaxX-max+1)?delimiter:"" );
			sb.append( x[i-1] );
		}
		
		return sb.toString();
	}
	
	private static int[][] getLCSuff( String[] x, String[] y ) {
		int m = x.length+1;
		int n = y.length+1;
		int[][] c = new int[m][n];
		
		for ( int i=1; i<m; i++ ) {
			for ( int j=1; j<n; j++ ) {
				if ( x[i-1].equals( y[j-1] ) ) {
					c[i][j] = c[i-1][j-1]+1;
				} 
			}
		}
		if (debug) {
			for ( int[] cs : c ) {
				System.out.println( Arrays.toString(cs) );
			}
		}
		return c;
	}
	
	public static boolean validate() {
		String[] x = {"The","great","composer","Mr.","Mozart","(1756-1791)","achieved","fame","fame"};
		String[] y = {"Mr.","Mozart","(1756-1791)","was","a","genius"};
		
		String result = getLCSubstr(x, y, "");
		System.out.println( result );
		return result.equals("Mr.Mozart(1756-1791)");
	}
	
	public static void main(String[] args) {
		System.out.print( validate() );
	}
	
}
