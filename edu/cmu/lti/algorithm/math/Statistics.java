package edu.cmu.lti.algorithm.math;

import java.util.Arrays;

public class Statistics {

	public static double calcAvg( int[] array ) {
		return calcSum(array)/(double)array.length;
	}

	public static double calcAvg( double[] array ) {
		return calcSum(array)/(double)array.length;
	}

	public static double calcSum( int[] array ) {
		int result = 0;
		for ( double d : array ) {
			result+=d;
		}
		return result;
	}
	
	public static double calcSum( double[] array ) {
		double result = 0.0D;
		for ( double d : array ) {
			result+=d;
		}
		return result;
	}
	
	public static double calcVar( double[] array ) {
		double avg = calcAvg(array);
		double result = 0;
		for ( double d : array ) {
			result += ( d - avg )*( d - avg );
		}
		return result / (double)array.length;
	}
	
	public static double calcSD( double[] array ) {
		return Math.sqrt( calcVar( array ) );
	}
	
	public static double[] getMeanRank( double[] values ) {
		
		int N = values.length;
		
		double[] originalValues = values.clone();
		Arrays.sort(values);
		
		double[] ranks = new double[N];
				
		boolean isTie = false;
		boolean wasTie = false;
		double prevValue = -1;
		int consecutiveTie = 0;
		boolean isLastTie = false;
		for ( int i=0; i<=N; i++ ) {
			if (i!=N) isTie = Util.roundedMatch(prevValue, values[i], 5);
			isLastTie = ( isTie && i==N );
			if ( isLastTie && wasTie ) isTie = false;
			if ( i==N && !wasTie ) break;
			
			if ( (wasTie && !isTie) || isLastTie ) {
				consecutiveTie++;
				if ( ! isLastTie ) ranks[i] = i+1;
				double meanRank = 0;
				for ( int j=0; j<consecutiveTie; j++ ) {
					meanRank += i-j; 
				}
				meanRank /= (double)consecutiveTie;
				for ( int j=0; j<consecutiveTie; j++ ) {
					ranks[i-j-1] = meanRank; 
				}
				
				if ( i==N ) break;
				consecutiveTie = 0;
			} else if ( !wasTie && !isTie ) {
				// usual scenario
				ranks[i] = i+1;
			} else if ( wasTie && isTie  ) {
				consecutiveTie++;
			} else if ( !wasTie &&  isTie ) {
				consecutiveTie++;
			}
			wasTie = isTie;
			prevValue = values[i];
		}

		double[] finalRanks = new double[ranks.length];
		for ( int i=0; i<N; i++ ) {
			for ( int j=0; j<N; j++ ) {
				if ( originalValues[i]==values[j] ) {
					finalRanks[i] = ranks[j];
				}
			}
		}
			
		return finalRanks;
	}
	
	/**
	 * Calculate the Pearson product-moment correlation coefficient
	 * @param x
	 * @param y
	 * @return Pearson product-moment correlation coefficient
	 */
	public static double calcPearson( double[] x, double[] y ) {
		double numerator = 0;
		double denominator = 0;
		
		double avgY = calcAvg(y);
		double avgX = calcAvg(x);
		
		if ( x.length != y.length ) {
			return 0;
		}
		for ( int i=0; i<x.length; i++ ) {
			numerator += (x[i]-avgX)*(y[i]-avgY);
		}
		double denomX = 0;
		double denomY = 0;
		for ( int i=0; i<x.length; i++ ) {
			denomX += (x[i]-avgX)*(x[i]-avgX);
			denomY += (y[i]-avgY)*(y[i]-avgY);
		}
		denominator = Math.sqrt(denomX*denomY);
		
		return numerator/denominator;
	}
	
	/**
	 * Calculate the Kendall tau rank correlation coefficient
	 * Formula from http://aoki2.si.gunma-u.ac.jp/lecture/Soukan/kendall.html 
	 * @param rankX
	 * @param rankY
	 * @return Kendall tau rank correlation coefficient 
	 */
	public static double calcKendall( double[] rankX, double[] rankY ) {
		
		int Ta = 0;
		int Tb = 0;
		int P = 0;
		int Q = 0;
		int N = rankX.length;
		for ( int i=0; i<N ;i++ ) {
			for ( int j=0; j<rankX.length; j++ ) {
				if ( (rankX[i] < rankX[j] && rankY[i] < rankY[j]) 
						|| (rankX[i] > rankX[j] && rankY[i] > rankY[j]) ) {
					P++;
				} else if ((rankX[i] < rankX[j] && rankY[i] > rankY[j]) 
					|| (rankX[i] > rankX[j] && rankY[i] < rankY[j])) {
					Q++;
				} else if ( rankX[i] == rankX[j] && rankY[i] != rankY[j] ) {
					Ta++;
				} else if ( rankX[i] != rankX[j] && rankY[i] == rankY[j] ) {
					Tb++;
				}
			}
		}
		
		double numerator   = P - Q;
		double f1 = P + Q + Ta;
		double f2 = P + Q + Tb;
		double denominator = Math.sqrt( f1 * f2 );

		return ((double)numerator / (double)denominator);
	}
	
	/**
	 * Calculate the Kendall tau rank correlation coefficient from
	 * arrays of values (not ranks!)
	 * @param valueX
	 * @param valueY
	 * @return Kendall tau rank correlation coefficient 
	 */
	public static double calcKendallFromValue( double[] valueX, double[] valueY ) {
		try {
			double[] rankX = getMeanRank(valueX);
			double[] rankY = getMeanRank(valueY);
			return calcKendall(rankX, rankY);
		} catch ( NumberFormatException nfe ) {
			System.err.println( "ERROR calculating calcKendallFromValue." );
			System.err.println( "valueX = "+Arrays.toString(valueX) );
			System.err.println( "valueY = "+Arrays.toString(valueY) );
			return Double.NaN;
		}
	}
	
	/**
	 * Calculate the Kendall tau rank correlation coefficient from
	 * arrays of values (not ranks!)
	 * @param valueX
	 * @param letters
	 * @return Kendall tau rank correlation coefficient 
	 */
	public static double calcKendallFromValue( double[] valueX, String[] letters ) {
		double[] valueY = new double[letters.length];
		for ( int i=0; i<letters.length; i++ ) {
			if ( letters[i].equals("A") ) {
				valueY[i] = 1;
			} else if ( letters[i].equals("B") ) {
				valueY[i] = 0.6;
			} else if ( letters[i].equals("C") ) {
				valueY[i] = 0.3;
			} else if ( letters[i].equals("D") ) {
				valueY[i] = 0;
			}
		}
		
		double[] rankX = getMeanRank(valueX);
		double[] rankY = getMeanRank(valueY);
		return calcKendall(rankX, rankY);
	}
		
	public static boolean validate() {
		boolean passed = true;
		{
			// pearson's correl
			double[] x = {0,0,1,1,1,1,1};
			double[] y = {0,0,0,0,1,1,1};
			passed &= Util.roundedMatch( calcPearson(x, y), 0.5477D, 3 );
		}
		{
			// kendall's rank correl
			//http://aoki2.si.gunma-u.ac.jp/lecture/Soukan/kendall.html
			double[] x = {1,2,3,4,5,6,7,8};
			double[] y = {3,4,1,2,5,7,8,6};
			passed &= Util.roundedMatch( calcKendall(x, y), 0.57, 2 );
		}
		{
			// TODO:write test case: http://aoki2.si.gunma-u.ac.jp/lecture/Soukan/kendall.html
			// mean rank
			double[] x = {-1.4,1.2,3.4,1.2,3.4,3.4};
			String result = Arrays.toString( getMeanRank(x) );
			passed &= result.equals("[1.0, 2.5, 5.0, 2.5, 5.0, 5.0]");
		}
		{
			double[] x = {2.8,3.4,3.6,5.8,7.0,9.5,10.2,12.3,13.2,13.4};
			double[] y = {0.6,3.0,0.4,1.5,15.0,13.4,7.6,19.8,18.3,18.9};
			passed &= Util.roundedMatch(calcKendallFromValue(x, y), 0.64444, 5);
		}
		
		return passed;
	}
	
}
