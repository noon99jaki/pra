package edu.cmu.lti.algorithm.ir.eva;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.util.system.FSystem;

public class PrecRecall  implements   Serializable,IPlusObjOn, IMultiplyOn{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public PrecRecall plusObjOn(Object x){
		if (x == null) return this;
		PrecRecall e = (PrecRecall) x;
		this.prec+=e.prec;
		this.recall+=e.recall;
		this.F1+=e.F1;
		this.nRet+=e.nRet;
		this.nRel+=e.nRel;
		this.nTP+=e.nTP;
		return this;
	}
	public PrecRecall multiplyOn(Double x){
		this.prec*=x;
		this.recall*=x;
		this.F1*=x;
		this.nRet*=x;
		this.nRel*=x;
		this.nTP*=x;
		return this;			
	}
	//public double returned;
	//public double positive;
	//public double tp;
	public double prec=0.0;
	public double recall=0.0;
	public double F1=0.0;
	public double nRet=0.0;	//# item from system
	public double nRel=0.0;	//# relevant item from gold
	public double nTP=0.0;	//true positive
	
	public PrecRecall copy(){
		//EvaClassify e  = (EvaClassify)super.clone();
		return (new PrecRecall()).copy(this);
	}
	public PrecRecall copy(PrecRecall e){
		this.prec = e.prec;
		this.recall = e.recall;
		this.F1 = e.F1;
		this.nRet = e.nRet;
		this.nRel = e.nRel;
		this.nTP = e.nTP;
		return this;
	}	
	public PrecRecall(){
		
	}

	public double F(double b){
		return F(prec,recall,b);
	}
	public static double F(double P, double R, double b){
		double bb= b*b;
		double lower = (P*bb+R);
		if (lower==0.0) return 0.0;
		return (1+bb)*P*R/lower;
	}
	public static VectorD F(VectorD vP, VectorD vR, double b){
		FSystem.checkVectorSizes(vP.size(), vR.size());
		VectorD vF = new VectorD();
		vF.ensureCapacity(vP.size());
		for (int i=0; i< vP.size(); ++i)
			vF.add(F(vP.get(i),vR.get(i), b));
		return vF;
	}
	public String print() {
		return String.format("nRel=%.0f, nRet=%.0f, P=%.2f,R=%.2f,F1=%.2f"
				, nRel, nRet,	prec, recall, F1);		
	}
	public String toString() {
//		return String.format("%.0f\t%.0f\t%.0f\t%.2f\t%.2f\t%.2f"
//				, nRel, nRet,nTP, prec, recall, F1);		
		return String.format("%.0f\t%.0f\t%.0f\t%.1f\t%.1f\t%.1f"
				, nRel, nRet,nTP, prec*100, recall*100, F1*100);		
	}
	public static String title(){
		return "nRel\tnRet\tnTP\tP\tR\tF1";
	}	

	/**
	 * 
	 * @param nRet	
	 * @param nRel	
	 * @param nTP	true positive
	 */
	public PrecRecall(double nRet, double nRel, double nTP){
		this.nTP= nTP;
		this.nRet= nRet;
		this.nRel= nRel;
		if (nTP==0.0) return;
		//this.returned=returned;
		//this.positive = positive;
		//this.tp = tp;
		prec = nTP/nRet;
		recall = nTP/nRel;
		F1 = 2*prec*recall/(prec+recall);
	}
	
	//soft system output and gold standard qrel
	public PrecRecall(MapSD mSystem, MapSD mGold){
		this(mSystem.sum(), mGold.sum(), mSystem.min(mGold).sum());
	}	
		
	//soft system output and gold standard qrel
	public PrecRecall(MapID mSystem, MapID mGold){
		this(mSystem.sum(), mGold.sum(), mSystem.min(mGold).sum());
	}	
	public PrecRecall(SetS mSystem, SetS mGold){
		this(mSystem.size(), mGold.size(), mSystem.and(mGold).size());
	}	
	public PrecRecall(SetI mSystem, SetI mGold){
		this(mSystem.size(), mGold.size(), mSystem.and(mGold).size());
	}		

}
