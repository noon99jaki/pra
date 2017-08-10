package edu.cmu.lti.algorithm.ir;

import java.math.BigDecimal;
import java.util.Map;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.StopWord;
/**
 * functions used in IR
 * @author nlao
 *
 */
public class FIR {
	
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
	 * @return f
	 */
	public static double calcF( double p, double r, double beta ) {
		if ( roundedMatch( p*r, 0, 6) ) {
			return 0;
		} else {
			double beta2 = beta*beta;
			return (1+beta2)*r*p/(r+beta2*p);
		}
	}


	/**
	 * Calculate the MMS
	 * @param seq1
	 * @param seq2
	 * @return longest common consecutive sequence
	 */
	public static int[] getMMS(VectorS seq1, VectorS seq2 ) {
		int maxBegin = -1;
		int maxEnd   = -1;
		
		for ( int i=0; i<seq1.size(); i++ ) {
			for ( int j=0; j<seq2.size(); j++ ) {
				if ( seq1.get(i).equals(seq2.get(j)) ) {
					int p = 1;
					int begin = i;
					int end = i+1;
					while ( i+p<seq1.size() && 
							j+p<seq2.size() &&
							seq1.get(i+p).equals(seq2.get(j+p)) ) {
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


	
	public static  VectorD  getMeanRank(double[] x) {
		return getMeanRank(new VectorD(x));
	}	
	public static VectorD getMeanRank( VectorD values ) {
		
		int N = values.size();		
		VectorD originalValues = (VectorD) values.clone();		
		values.sortOn();
		
		VectorD ranks = new VectorD(N);
				
		boolean isTie = false;
		boolean wasTie = false;
		double prevValue = -1;
		int consecutiveTie = 0;
		boolean isLastTie = false;
		for ( int i=0; i<=N; i++ ) {
			if (i!=N) isTie = roundedMatch(prevValue, values.get(i), 5);
			isLastTie = ( isTie && i==N );
			if ( isLastTie && wasTie ) isTie = false;
			if ( i==N && !wasTie ) break;
			
			if ( (wasTie && !isTie) || isLastTie ) {
				consecutiveTie++;
				if ( ! isLastTie ) ranks.set(i, i+1.0);
				double meanRank = 0;
				for ( int j=0; j<consecutiveTie; j++ ) {
					meanRank += i-j; 
				}
				meanRank /= (double)consecutiveTie;
				for ( int j=0; j<consecutiveTie; j++ ) {
					ranks.set(i-j-1, meanRank); 
				}
				
				if ( i==N ) break;
				consecutiveTie = 0;
			} else if ( !wasTie && !isTie ) {
				// usual scenario
				ranks.set(i, i+1.0);
			} else if ( wasTie && isTie  ) {
				consecutiveTie++;
			} else if ( !wasTie &&  isTie ) {
				consecutiveTie++;
			}
			wasTie = isTie;
			prevValue = values.get(i);
		}

		VectorD finalRanks = new VectorD(ranks.size());
		for ( int i=0; i<N; i++ ) {
			for ( int j=0; j<N; j++ ) {
				if ( originalValues.get(i)==values.get(j) ) {
					finalRanks.set(i,ranks.get(j));
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
	public static double calcPearson(double[] x, double[] y) {
		return calcPearson(new VectorD(x), new VectorD(y));
	}
	public static double calcPearson(VectorD x, VectorD y) {
		double numerator = 0;
		double denominator = 0;
		
		double avgY = y.mean();
		double avgX = x.mean();
		
		if ( x.size() != y.size() ) {
			return 0;
		}
		for ( int i=0; i<x.size(); i++ ) {
			numerator += (x.get(i)-avgX)*(y.get(i)-avgY);
		}
		double denomX = 0;
		double denomY = 0;
		for ( int i=0; i<x.size(); i++ ) {
			denomX += (x.get(i)-avgX)*(x.get(i)-avgX);
			denomY += (y.get(i)-avgY)*(y.get(i)-avgY);
		}
		denominator = Math.sqrt(denomX*denomY);
		
		return numerator/denominator;
	}
	
	/**
	 * Calculate the Kendall tau rank correlation coefficient
	 * Formula from http://aoki2.si.gunma-u.ac.jp/lecture/Soukan/kendall.html 
	 * @param x
	 * @param y
	 * @return Kendall tau rank correlation coefficient 
	 */
	public static double calcKendall(double[] x, double[] y) {
		return calcKendall(new VectorD(x), new VectorD(y));
	}
	public static double calcKendall( VectorD rankX, VectorD rankY ) {
		
		int Ta = 0;
		int Tb = 0;
		int P = 0;
		int Q = 0;
		int N = rankX.size();
		for ( int i=0; i<N ;i++ ) {
			for ( int j=0; j<rankX.size(); j++ ) {
				if ( (rankX.get(i) < rankX.get(j) && rankY.get(i) < rankY.get(j)) 
						|| (rankX.get(i) > rankX.get(j) && rankY.get(i) > rankY.get(j)) ) {
					P++;
				} else if ((rankX.get(i) < rankX.get(j) && rankY.get(i) > rankY.get(j)) 
					|| (rankX.get(i) > rankX.get(j) && rankY.get(i) < rankY.get(j))) {
					Q++;
				} else if ( rankX.get(i) == rankX.get(j) && rankY.get(i) != rankY.get(j) ) {
					Ta++;
				} else if ( rankX.get(i) != rankX.get(j) && rankY.get(i) == rankY.get(j) ) {
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
	 * @param x
	 * @param y
	 * @return Kendall tau rank correlation coefficient 
	 */
	public static double calcKendallFromValue(double[] x, double[] y) {
		return calcKendallFromValue(new VectorD(x), new VectorD(y));
	}
	public static double calcKendallFromValue( VectorD valueX, VectorD valueY ) {
		try {
			VectorD rankX = getMeanRank(valueX);
			VectorD rankY = getMeanRank(valueY);
			return calcKendall(rankX, rankY);
		} catch ( NumberFormatException nfe ) {
			System.err.println( "ERROR calculating calcKendallFromValue." );
			System.err.println( "valueX = "+valueX.toString() );
			System.err.println( "valueY = "+valueY.toString() );
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
	public static double calcKendallFromValue( VectorD valueX, VectorS letters ) {
		VectorD valueY = new VectorD(letters.size());
		for ( int i=0; i<letters.size(); i++ ) {
			if ( letters.get(i).equals("A") ) {
				valueY.set(i,1.0);
			} else if ( letters.get(i).equals("B") ) {
				valueY.set(i,0.6);
			} else if ( letters.get(i).equals("C") ) {
				valueY.set(i,0.3);
			} else if ( letters.get(i).equals("D") ) {
				valueY.set(i,0.0);
			}
		}
		
		VectorD rankX = getMeanRank(valueX);
		VectorD rankY = getMeanRank(valueY);
		return calcKendall(rankX, rankY);
	}
	public static VectorS tokenize(String title) {
		title = title.replaceAll("[\\[\\]();:\\.,\\?\\!]", "");
		return new VectorS(title);
	}

	
	public static VectorS tokenize(String title, boolean bCase) {
		if (!bCase)
			 title=title.toLowerCase();		
		VectorS vs = tokenize(title);
		if (bCase)
			vs = (VectorS) vs.remove(StopWord.m429Cap);
		else			
			vs = (VectorS) vs.remove(StopWord.m429);
		return vs;
	}
}
