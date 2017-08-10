/*****************************************************************************
 * Richard's Named Entity Translator (RANET)
 * Author: Richard C. Wang
 * E-mail: rcwang#cs,cmu,edu
 * Website: http://www.rcwang.com
 *****************************************************************************/

package edu.cmu.lti.nlp.mt.ranet;

public class Tester {
	private double tf, maxTF, df, maxDF, ctf, maxCTF, cdf, maxCDF, wd, maxWD, numWords, maxWords, avgWords;
	private int trialID;
	private double[] tfScore, dfScore, ctfScore, cdfScore, wdScore, nwScore;

	public Tester () {
		this(0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
	}

	public Tester (double tf, double maxTF, double df, double maxDF, double ctf, double maxCTF, double cdf, double maxCDF, double wd, double maxWD, double numWords, double maxWords, double avgWords, int trialID) {
		this.tf = tf;
		this.maxTF = maxTF;
		this.df = df;
		this.maxDF = maxDF;
		this.ctf = ctf;
		this.maxCTF = maxCTF;
		this.cdf = cdf;
		this.maxCDF = maxCDF;
		this.wd = wd;
		this.maxWD = maxWD;
		this.numWords = numWords;
		this.maxWords = maxWords;
		this.avgWords = avgWords;
		this.trialID = trialID;
		defineScores();
	}

	private void defineScores () {
		double[] tfScore = {
				tf / maxTF,
				Math.pow(tf / maxTF, 2),
		};
		double[] dfScore = {
				df / maxDF,
				Math.pow(df / maxDF, 2),
		};
		double[] ctfScore = {
				ctf / maxCTF,
				Math.pow(ctf / maxCTF, 2),
		};
		double[] cdfScore = {
				cdf / maxCDF,
				Math.pow(cdf / maxCDF, 2),
		};
		double[] wdScore = {
//				1 - wd / maxWD,
//				1 - Math.pow(wd / maxWD, 2),
//				Math.log((maxWD + 0.5) / wd) / Math.log(maxWD + 1)
				wd / maxWD,
				Math.pow(wd / maxWD, 2),
				Math.log(maxWD + 1) / Math.log((maxWD + 0.5) / wd)
		};
		double[] nwScore = {
				numWords / maxWords,
				Math.log(numWords + 0.5) / Math.log(maxWords + 1),
				Math.exp(Math.pow(numWords-avgWords,2)/(-2*Math.pow(avgWords/2,2)))/(avgWords/2*Math.sqrt(2*Math.PI)),
				1-Math.pow(numWords-(avgWords+1),2)/Math.pow(avgWords+1,2),
		};
		this.tfScore = tfScore;
		this.dfScore = dfScore;
		this.ctfScore = ctfScore;
		this.cdfScore = cdfScore;
		this.wdScore = wdScore;
		this.nwScore = nwScore;
	}

	public double getScore () {
		int counter = 0;
		for (int i=0; i<tfScore.length; i++)
			for (int j=0; j<dfScore.length; j++)
				for (int k=0; k<ctfScore.length; k++)
					for (int l=0; l<cdfScore.length; l++)
						for (int m=0; m<wdScore.length; m++)
							for (int n=0; n<nwScore.length; n++) {
								if (counter == trialID)
									return tfScore[i] * dfScore[j] * ctfScore[k] * cdfScore[l] * wdScore[m] * nwScore[n];
								counter++;
							}
		return 0;
	}

	public int getNumTests () {
		return tfScore.length * dfScore.length * ctfScore.length * cdfScore.length * wdScore.length * nwScore.length;
	}
}
