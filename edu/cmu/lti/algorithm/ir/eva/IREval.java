/**
 * 
 */
package edu.cmu.lti.algorithm.ir.eva;

import java.io.Serializable;

import org.apache.log4j.Logger;

import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.math.Curve;

/**
 * @author nlao
 * this class has most of the standard IR evaluation metrics
 */
public class IREval  implements   Serializable,IPlusObjOn, IMultiplyOn{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	private static final Logger log = Logger.getLogger(IREval.class);
	
	public IREval plusObjOn(Object x){
		if (x == null) return this;
		IREval e = (IREval) x;
		this.pr.plusObjOn(e.pr);
		this.mrr+=e.mrr;
		this.P1+=e.P1;
		this.P5+=e.P5;
		this.P10+=e.P10;
		this.P20+=e.P20;
		this.P50+=e.P50;
		this.P100+=e.P100;
		
		Curve cR = (new Curve(vN, vR)).plus((new Curve(e.vN, e.vR)));
		this.vR = cR.vY;
		
		Curve cMAP = (new Curve(vN, vMAP)).plus((new Curve(e.vN, e.vMAP)));
		this.vMAP = cMAP.vY;
		
		this.vN= cMAP.vX;
		
		return this;
	}
	
	public IREval multiplyOn(Double x){
		this.pr.multiplyOn(x);
		this.mrr*= x;
		this.P1*= x;
		this.P5*= x;
		this.P10*= x;
		this.P20*= x;
		this.P50*= x;
		this.P100*= x;
		
		this.vMAP.multiplyOn(x);
		this.vR.multiplyOn(x);

		return this;			
	}
	
	public PrecRecall pr = new PrecRecall();	
	public double mrr;
	public double P1,P5,P10,P20,P50,P100;
	
	public VectorD vN = new VectorD();//1,0.0);//#document returned
	public VectorD vR = new VectorD();//1,0.0);//recall	
	public VectorD vMAP = new VectorD();//1,0.0);//MAP

	
	public IREval copy(){
		//EvaClassify e  = (EvaClassify)super.clone();
		return (new IREval()).copy(this);
	}
	public IREval copy(IREval e){
		this.mrr = e.mrr;
		this.P1 = e.P1;
		this.P5 = e.P5;
		this.P10 = e.P10;
		this.P20 = e.P20;
		this.P50 = e.P50;
		this.P100 = e.P100;
		this.pr.copy(e.pr);
		
		this.vN.copyShallow(e.vN);
		this.vR.copyShallow(e.vR);
		this.vMAP.copyShallow(e.vMAP);

		return this;
	}
	
	private void evaluateIdx(VectorI idxRel, double nRel, double nRet){		
		this.P1=  getPrecision(idxRel,1);
		this.P5=  getPrecision(idxRel,5);
		this.P10= getPrecision(idxRel,10);
		this.P20= getPrecision(idxRel,20);
		this.P50= getPrecision(idxRel,50);
		this.P100= getPrecision(idxRel,100);

		idxRel.plusAllOn(1);
		if (idxRel.size()==0)
			mrr=0;
		else
			mrr=1.0/ (idxRel.get(0)+1);
		
		vN=idxRel.toVectorD();
		//dont over use container
		//vR = VectorI.seq(1, vN.size()).toVectorD().devide(vN);
		
		vR.clear();vMAP.clear();
		VectorD vP = new VectorD();
		double map=0;double recall=0;
		for (int i=1; i<=vN.size();++i){
			double prec= i/vN.get(i-1);
			recall = i/nRel;
			vR.add(recall);
			vP.add(prec);
			map += prec/nRel;
			vMAP.add(map);
		}
		vR.add(recall);
		vMAP.add(map);
		vN.add(nRet);
		Curve c= new Curve(vN, vP);
		return;
	}
	
	/** 
	 * @param idxRel	0,6, 8
	 * @param rank	1,2,3...
	 * @return precision at rank, based on positions of relevant docs
	 */
	public static double getPrecision(VectorI idxRel, int rank){
		return  (idxRel.findSorted(rank-1)+1)/ (double) rank;
	}
/*	
	private void evaluate(VectorI vRel){		
		if (pr.recall==0)
			mrr=0;
		else
			mrr=1.0/ (vRel.idxFirst(1)+1);
		
			
		//SetS m= new SetS(m_gold);
		//SetS m= new SetS();
		
		double nRel=0.0;
		double precision=0.0;
		for (int i=1;i<=100;++i){
			if (i<=vRel.size()){
				if (vRel.get(i-1)==1)++nRel;
				precision = nRel/i;
			}
			if (i==1)	this.P1 = precision;
			else if (i==5)	this.P5 = precision;
			else if (i==10)	this.P10 = precision;
			else if (i==20)	this.P20 = precision;
			else if (i==50)	this.P50 = precision;
			else if (i==100)	this.P100 = precision;
		}
	}*/
	public void evaluate(VectorS v_system, SetS m_gold){
		try {
			pr = new PrecRecall((SetS) v_system.toSet(), m_gold);
			evaluateIdx( v_system.idxIn(m_gold), m_gold.size(), v_system.size());
		//	evaluate( v_system.maskIn(m_gold));
		} catch ( Exception e ) {
			log.error(e.getMessage());
		}
	}
	
	public void evaluate(VectorI v_system, SetI m_gold){
		pr = new PrecRecall((SetI) v_system.toSet(), m_gold);
		evaluateIdx( v_system.idxIn(m_gold), m_gold.size(), v_system.size());
//		evaluate( v_system.maskIn(m_gold));
	}

	public static String title(){
		//return pr.title()+"\tMRR";
		//return pr.title()+"\tMRR\tP1\tP5\tP10\tP20\tP50\tP100";
		return PrecRecall.title()+"\tMRR\tP1\tP5\tP20\tP100";
	}
	
	public String toString() {
//		return String.format("%.2f\t%s"		, mrr,pr.toString());		
//		return String.format("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f"
//			,pr.toString(), mrr,P1,P5,P10,P20,P50,P100);
		
//		return String.format("%s\t%.2f\t%.2f\t%.2f\t%.2f\t%.2f"
//				,pr.toString(), mrr,P1,P5,P20,P100);		
		return String.format("%s\t%.1f\t%.1f\t%.1f\t%.1f\t%.1f"
				,pr.toString(), mrr*100,P1*100,P5*100,P20*100,P100*100);				
	}	
}
