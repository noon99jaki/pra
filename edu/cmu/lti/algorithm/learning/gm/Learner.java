package edu.cmu.lti.algorithm.learning.gm;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.optimization.LBFGSWraper;

public class Learner extends LBFGSWraper{
	public static class Sample{
		public VectorD vZ;
		public VectorD vY;
	}
	public static class DataSet{
		//VectorVectorD vvX;
		//VectorVectorD vvY;
		VectorX<Sample> vSample;
	}		
	
	
	public static class Param	extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public static enum ERegularization{
			L1, L2;
		}
		public ERegularization loss;

		public Param(Class c) {
			super(c);
			parse();
		}		
		public void parse(){	
			//m = getInt("m",5);
			//eps = getDouble("eps",1e-5);
			//lang = getString("lang");
			//diagco=getBoolean("diagco", false);
			loss =ERegularization.valueOf(
					getString("loss",ERegularization.L1.name()) );
			
		}	
	}	
	public Param p;
	
	public Learner(){	
		super(Learner.class);		
		p=new Param(Learner.class);
	}
	
	/*
	public Learner(CRFBinomial crf){
		super(Learner.class);
		this.crf = crf;
	}
	*/
	
	double[] vG;
	double loss;
	protected void setx(double[] x){
		crf.x=x;
		loss=0;
		VectorD vG= new VectorD();
		for (Sample s: ds.vSample){
			crf.train(s.vY,s.vZ);
			vG.plusOn(crf.vG );
			loss+= crf.loss;
		}
		this.vG=vG.toDoubleArray();
	}
	protected double getValue(){return loss;	}		
	protected double[] getGrad(){return vG;}

	public CRFB crf;
	DataSet ds;
	public CRFB train(CRFB crf,  DataSet ds){
		this.ds= ds;
		this.crf= crf;
		//double[] x = new double[crf.viX.size()];
		this.optimize(crf.x);
		//crf.vW = x;
		return crf;			
	}
	
}