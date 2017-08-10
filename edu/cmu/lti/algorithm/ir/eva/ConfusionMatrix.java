package edu.cmu.lti.algorithm.ir.eva;

import java.io.Serializable;

public class ConfusionMatrix  implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	
	public double tp,tn,fp,fn;
	public static String getTitle(){
		return "tp\ttn\tfp\tfn";
	}
	public String toString(){
		return String.format(
				"tp\t%.0f\ntn\t%.0f\nfp\t%.0f\nfn\t%.0f\n"
				, tp,tn,fp,fn);
	}
	public String print(){
		return String.format("%.0f\t%.0f\t%.0f\t%.0f"
					, tp,tn,fp,fn);
	}

	public void clear(){
		tp=0;tn=0;fp=0;fn=0;
	}
	public ConfusionMatrix(){
		clear();
	}

	double fpr, rec, prec, p, n, in, out;
	public void evaluate(){
		p = tp + fn;
		n = fp+ tn;
		in = tp+fp;
		out = tn + fn;
		if (n>0) fpr = fp/n; else fpr=0;
		if (p>0) rec = tp/p; else rec=1;
		if (in>0) prec = tp/in; else prec=1;
	}

}
