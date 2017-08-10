package edu.cmu.lti.algorithm.learning.gm;

import edu.cmu.lti.algorithm.container.VecVecD;
import edu.cmu.lti.algorithm.container.VecVecI;
import edu.cmu.lti.algorithm.learning.gm.CRFB.EVType;
import edu.cmu.lti.algorithm.learning.gm.CRFB.Factor;
import edu.cmu.lti.algorithm.learning.gm.CRFB.Variable;
import edu.cmu.lti.algorithm.math.rand.FRand;

public class GeneratorCRFB {
	//int nVar=100;	int nNeighbors=4;
	public static CRFB generateCRF(int nVar,int nNeighbors, double w){
		double p= (double)nNeighbors/ (nVar);
		CRFB net = new CRFB();
		for (int i=0; i<nVar; ++i){
			net.addVar(EVType.OUT);
		}
		for (Variable var: net.vVar){
			if (FRand.drawBoolean(p))		proposeFactor(net, -1,var.id,w);
			for (int id1=var.id+1; id1<net.vVar.size();++id1)				 
				if (FRand.drawBoolean(p))
					proposeFactor(net, var.id, id1, w);			
		}		
		return net;
	}
	public static void proposeFactor(CRFB net, int iV1, int iV2, double w){
		Factor fa=net.addFactor(iV1,iV2);
		fa.w= FRand.rand.nextDouble()*2*w-w;		
		//fa.w= FRand.withProb(0.5)?w:-w;
	}
	public static void main(String[] args) {
		//int nVar=100;
		//int nNei=10;
		//int nSample=300;
		//
		double w=5;
		for (int nNei:new int[]{5}){//,20,30,40
			for (int nVar:new int[]{100,200,300,400,500}){//500,1000}){
				String code =String.format("V%dN%dW%.0f",nVar,nNei, w);
				CRFB net =generateCRF(nVar,nNei, w);
				net.save(code+".net");
				
				for (int nSample:new int[]{200}){
					VecVecI vv= net.sampleGibbs(nSample, 10000, 1000);
					//VectorVectorD vvd;
					vv.save(code+"."+nSample);	
					net.save(code+"."+nSample+".net");
				}			
			}
		}
		
		try {
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
