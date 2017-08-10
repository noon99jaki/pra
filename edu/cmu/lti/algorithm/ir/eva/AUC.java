package edu.cmu.lti.algorithm.ir.eva;

import java.io.BufferedWriter;
import java.io.Serializable;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.tools.visualize.r.RWrapper;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.HtmlPage;

public class AUC  implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public double auc;
	public VectorD vFPR= new VectorD();
	public VectorD vRec= new VectorD();
	public VectorD vPrec= new VectorD();

	void clear(){
		vFPR.clear(); vPrec.clear();vRec.clear();
		auc=0;
	}
	
	//public void evaluate(VectorI vSystem, SetI mGold){
	public void evaluate(VectorD vScore, VectorI vRel){
		clear();
		if (vScore.size() != vRel.size())
			System.err.print("unmatched score and rel vector");
		
		int n_all=vScore.size();
		int n_posi=vRel.sum();
		int n_nega = n_all-n_posi;

		ConfusionMatrix cf= new ConfusionMatrix();
		// start by assuming nothing is positive
		//cf.fp = n_nega;		cf.tp = n_posi;
		cf.tn = n_nega;		cf.fn = n_posi;

		VectorI viOrder = (VectorI) vScore.sortId().reverseOn();
		//double lastPrec=1;
		double lastRec=0;

		for (int i: viOrder)	{
			double pr = vScore.get(i);
			int c = vRel.get(i);
			if (c==1)	{
				++cf.tp; --cf.fn; 
			}
			else{ 
				++cf.fp; --cf.tn;
			}
			cf.evaluate();

			vFPR.add(cf.fpr);
			vPrec.add(cf.prec);
			vRec.add(cf.rec);

			if (c==1){
				auc += (cf.rec-lastRec)*cf.prec;
				//lastPrec = cf.fpr;	
				lastRec=cf.rec;
			}
		}
		return;
	}

	public static String getTitle(){
		return "auc";
	}
	public String toString(){
		return String.format(	"auc=%.3f", auc	);
	}
	public String print(){
		return String.format("%.3f"	, auc);
	}

	public boolean save(String fn)	{
		BufferedWriter bw= FFile.newWriter(fn);
		boolean b= save(bw);
		FFile.close(bw);
		return b;
	}
	public boolean save(BufferedWriter bw)	{
		//tr.os.WriteString(Format(0));
		FFile.write(bw,"Rec\tprec\tFPR\n");
		for (int j=0; j< vFPR.size(); ++j)	
			FFile.write(bw,String.format("%.4f\t%.4f\t%.4f\n"
					,vRec.get(j), vPrec.get(j),	vFPR.get(j)));
		return true;
	}
	public void drawCurve(String fn){
		//VectorS vLegend=VectorI.seq(1, vFolder.size()).toVectorS();
		RWrapper wrapper = new RWrapper();
		wrapper.openImage(fn);		
		wrapper.drawXYPlot("Prec/Rec curve"
				, "Prec", "Recall", this.vPrec, vRec);
		//wrapper.addLegend( vLegend );
		wrapper.closeImage();		
	}
}
