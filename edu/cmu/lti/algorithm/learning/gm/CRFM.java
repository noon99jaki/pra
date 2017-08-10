package edu.cmu.lti.algorithm.learning.gm;

import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.learning.data.Index;

/**
 * this is the first draft, now it is bloated to a package
 * @author nlao
 *
 */
public class CRFM  {
	

	public static class Factor{
		public int ifr,ifr1, ito;//id of states
		public Factor(int ifr, int ifr1, int ito){
			this.ifr= ifr;
			this.ifr1= ifr1;
			this.ito= ito;
		}
	}
	public static class Variable{
		public Index indexValue= new Index();
		public int ibState=-1;
		public int ieState=-1;
		
	}
	public static class State{
		public SetI mi=new SetI();// axion side factors
	}
	
	public VectorX<State> vSta= new VectorX<State>(State.class);
	public VectorX<Variable> vVar= new VectorX<Variable>(Variable.class);
	public VectorX<Factor> vFactor= new VectorX<Factor>(Factor.class);
	//VectorD vE;	
	//double[] x ;
	
	// expectations//in log domain?	
	public VectorD vEVar, vEFa;		
	public VectorI viZ,viY,viH,viX;
	
	public void selectSubNet(VectorI viY, VectorI viH	, VectorI viZ){
		this.viH = viH;
		this.viY = viY;
		this.viX = viH; viX.addAll(viY);
		this.viZ = viZ;
	}

	/**
	 * x={y,h}, 
	 * estimate p(x|z)
	 * @param viZ: ids of z variables
	 * @param viX: ids of x variables
	 */
	public void doMeanField(VectorI viX, VectorI viZ){
		for (int iScan=0; iScan<5; ++iScan){
			for (int ix : viX){
				expectVar(ix);				
			}
		}
	}
	
	private void expectVar(int id){
		Variable var= vVar.get(id);
		for (int iS=var.ibState; iS<var.ieState; ++iS){
			State st= vSta.get(iS);
			double e=0;
			for (int iFa :  st.mi)
				e+= expectFactor(iFa, iS);
			vEVar.set(iS,Math.exp(e));
			//x[iS]=Math.exp(e);
		}
		vEVar.normalizeOn(var.ibState,var.ieState);
		return;
	}
	private double expectFactor(int iFa, int iS){
		Factor fa= vFactor.get(iFa);
		double e=vEVar.get(fa.ifr);
		if (fa.ifr1>=0) e*=vEVar.get(fa.ifr1);
		if (fa.ito>=0) e*=(1-vEVar.get(fa.ito));
		vEFa.set(iFa, e);
		return e;		
	}	
	/** 
	 * @param vY
	 * @return loss=-log(p(y|z))
	 */
	protected double getValue( VectorD vY){
		double loss=0;
		//for (int iy: viY)
		for (int i=0; i<vY.size(); ++i){
			if (vY.get(i)==1.0)
				loss += -Math.log(vEVar.get(viY.get(i)));
			else
				loss += -Math.log(1-vEVar.get(viY.get(i)));					
		}
		return 0;
	}
	//public void setData(  VectorD vZ){	vEVar.set(viZ, vZ);	}
	
	public VectorD vX;
	public VectorD vX_y;
	public double loss=0;
	public VectorD vG;
	
	public void test(VectorD vZ){//Sample s){
		vEVar.set(viZ, vZ);		
		//test();
		doMeanField(viX, viZ);
		vX= (VectorD) vEVar.clone();//vEVar.sub(viX);
	}
	
	//public void test(){}
	//public void train(){}
	public void train(VectorD vY,  VectorD vZ){//Sample s){//
		test(vZ);
		
		loss = getValue(vY);			
		vEVar.set(viY, vY);
		doMeanField(viH, viY);
		vX_y= (VectorD) vEVar.clone();//vEVar.sub(viX);			
		vG=vX_y.minus(vX);
	}
	/**
	 * vector of all variables
	 * @param vA
	 */
	public void trainA(VectorD vA){
		train(vA.sub(viY), vA.sub(viZ));
	}
	public void testA(VectorD vA){
		test(vA.sub(viZ));
	}

	public void run(){
		
	}
	public static void main(String[] args) {
		//TODO: test it with data from GM hw
		try {
			(new CRFM()).run();
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
