package edu.cmu.lti.algorithm.string.editdistance;

import java.util.Arrays;
import java.util.regex.Pattern;

/**
 * The longest common subsequence problem (LCS) is finding the 
 * longest subsequence common to all sequences in a set of 
 * sequences (often just two). It is a classic computer science 
 * problem, the basis of diff, and has applications in bioinformatics.
 * It should not be confused with the longest common substring problem 
 * (substrings are necessarily contiguous).
 * 
 * Source: http://en.wikipedia.org/wiki/Longest_common_subsequence_problem
 * 
 * @author hideki
 *
 */
public class LCS {
		
	private final static boolean debug = false;
	
	public static int getLCSLength( String[] x, String[] y ) {
		int[][] c = getLCSTable( x, y );
		return c[x.length][y.length];
	}
	
	public static int[][] getLCSTable( String[] x, String[] y ) {
		int m = x.length+1;
		int n = y.length+1;
		int[][] c = new int[m][n];
		
		for ( int i=1; i<m; i++ ) {
			for ( int j=1; j<n; j++ ) {
				if ( x[i-1].equals( y[j-1] ) ) {
					c[i][j] = c[i-1][j-1]+1;
				} else {
					c[i][j] = Math.max(c[i][j-1], c[i-1][j]);
				}
			}
		}
		if (debug) {
			System.out.print( "      " );
			for ( String s : x ) {
				System.out.print( s+"  " );
			}
			System.out.println();

			for ( int i=0; i<c.length; i++ ) {
				int[] cs = c[i];
				System.out.print( (i>0)?(x[i-1]+" "):"  " );
				System.out.println( Arrays.toString(cs) );
			}
		}
		return c;
	}
	
	public static String backTrack( int[][] c, String[] x, String[] y, int i, int j, String delimiter ) {
		if ( i==0 || j==0 ) {
			return "";
		} else if ( x[i-1].equals(y[j-1]) ) {
			return backTrack(c, x, y, i-1, j-1, delimiter) + delimiter+ x[i-1] + delimiter ;
		} else {
			if ( c[i][j-1] > c[i-1][j] ) {
				return backTrack( c, x, y, i, j-1, delimiter );
			} else {
				return backTrack( c, x, y, i-1, j, delimiter );
			}
		}
	}
	
	public static String getLCS( String[] x, String[] y, String delimiter ) {
		int[][] c = getLCSTable( x, y );
		String lcs = backTrack(c, x, y, x.length, y.length, delimiter);
		String d = Pattern.quote(delimiter);
		lcs = lcs.replaceAll("("+d+")+|(^"+d+")|("+d+"$)", delimiter);
		lcs = lcs.replaceAll("(^"+d+")|("+d+"$)", "");
		return lcs;
	}
	
	/************************************************************************
	 * TODO: REMOVE (****) arg0 wa * arg1 (***)
	 ***********************************************************************/
	public static String getPrototypeFromLCS( String[] lcs, String[] tokens1, String[] tokens2, int argSize ) {
		int maxInterval = 3;
		
		String[] newTokens1 = maskWithLCS( lcs, tokens1 );
		String[] newTokens2 = maskWithLCS( lcs, tokens2 );
		
		StringBuilder sb = new StringBuilder();
		int i=0;
		int j=0;
		int count = 0;
		int argCount = 0;
		
//		System.err.println( "----" );
//		System.err.println( Arrays.toString( newTokens1 ) );
//		System.err.println( Arrays.toString( newTokens2 ) );
		
		// number of * allowed  
		
		while ( i<newTokens1.length && j<newTokens2.length ) {
			boolean starFlag = false;
			boolean longStarFlag = false;
						
			int iCount = 0;
			while ( i<newTokens1.length-1 && newTokens1[i].equals("*") ) { 
				i++; iCount++;
				starFlag = true;
				if ( iCount > maxInterval-1 ) longStarFlag = true;
			}
			int jCount = 0;
			while ( j<newTokens2.length-1 && newTokens2[j].equals("*") ) { 
				j++; jCount++;
				starFlag = true; 
				if ( jCount > maxInterval-1 ) longStarFlag = true;
			}
			
			boolean beginBoundary = false;
			boolean endBoundary = false;
			if (newTokens1[i].matches("<NP.+?>")) {
				argCount++;
				beginBoundary = (argCount == 1);
				endBoundary = (argCount == argSize);
			}
			
			if ( longStarFlag ) {
				if ( argCount==0 || beginBoundary ) {
					sb = new StringBuilder();
					sb.append( newTokens1[i] );
				}
				
				if ( !endBoundary && argCount==argSize ) {
					break;
				} else if ( ((!beginBoundary && argCount>0 && !endBoundary) || endBoundary) ) {
					return null;
				}
			} else {
				sb.append( (count!=0 && starFlag)?" *":"" );
				sb.append( (count!=0)?" ":"" );
				sb.append( newTokens1[i] );
			}
			
			i++;
			j++;
			count++;
		}
		//System.err.println( "----"+Arrays.toString( sb.toString().split(" ") ) );
		
		return sb.toString();
	}
	
	public static String getLCSubseq( String[] tokens1, String[] tokens2, String delimiter, int argSize ) {
		String resultLCS = LCS.getLCS( tokens1, tokens2, delimiter );
		return LCS.getPrototypeFromLCS( resultLCS.split(delimiter), tokens1, tokens2, argSize );
	}
	
	private static String[] maskWithLCS( String[] lcs, String[] tokens ) {
		StringBuilder sb = new StringBuilder();
		int i=0;
		for ( int j=0; j < tokens.length; j++ ) {
			if ( i < lcs.length && lcs[i].equals( tokens[j] ) ) {
				sb.append( lcs[i]+" " );
				i++;
			} else {
				sb.append( "* " );
			}
		}
		
		return sb.toString().split(" ");
	}
	
	public static void test() {
		String[] x = {"北朝鮮","が","ミサイル","を","イラン","や","シリア","に","輸出","した"};
		String[] y = {"北朝鮮","が","ミサイル","本体","を","イラン","など","に","輸出","した"};
		
		int[][] c = getLCSTable( x, y );
		System.out.println( backTrack(c, x, y, x.length, y.length, " ") );
		
		String seed1 = "北朝鮮";
		String seed2 = "ミサイル";
		String sentence1 = "北朝鮮 は ミサイル 本体 を イラン に 昨年 輸出 し て い た";
		String sentence2 = "昨年 北朝鮮 は ミサイル 技術 を 中国 に 輸出 し た の です";
		System.out.println("seed 1\t\t= "+seed1);
		System.out.println("seed 2\t\t= "+seed2);
		System.out.println("sentence 1\t= "+sentence1);
		System.out.println("sentence 2\t= "+sentence2);
		
		sentence1 = sentence1.replaceAll(seed1, "<NP0>").replaceAll(seed2, "<NP1>");
		sentence2 = sentence2.replaceAll(seed1, "<NP0>").replaceAll(seed2, "<NP1>");
		System.out.println("sentence 1'\t= "+sentence1);
		System.out.println("sentence 2'\t= "+sentence2);
		
		String[] tokens1 = sentence1.split(" ");
		String[] tokens2 = sentence2.split(" ");
		String resultLCS = LCS.getLCS( tokens1, tokens2, " " );
		System.out.println( "LCS\t\t= "+resultLCS );
		System.out.println( "LCS merged\t= "+LCS.getPrototypeFromLCS( resultLCS.split(" "), tokens1, tokens2, 2 ) );
	}
	
//	public static void test2() {
//		String[] x = {"<NP0>", "<NP1>", "社長", "*", "*", "*", "*", "*", "*", "*", の, *, *};
//		String[] y = {"<NP0>", "<NP1>", "社長", "の"};
//		String resultLCS = LCS.getLCS( x, y, " " );
//		System.out.println( "LCS\t\t= "+resultLCS );
//		System.out.println( "LCS merged\t= "+LCS.getPrototypeFromLCS( resultLCS.split(" "), x, y, 2 ) );
//
//	}
	
	public static boolean validate() {
		String[] x = {"X","M","J","Y","A","U","Z"};
		String[] y = {"M","Z","J","A","W","X","U"};
		
		int result = getLCSLength(x, y);
		System.out.println(result);
		return result==4;
	}
	
	public static void main(String[] args) {
		//System.out.print( test() );
		test();
	}
	
}
