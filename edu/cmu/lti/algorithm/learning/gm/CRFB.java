package edu.cmu.lti.algorithm.learning.gm;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VecVecI;
import edu.cmu.lti.algorithm.math.rand.FRand;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.EColor;
import edu.cmu.lti.util.system.FSystem;

/**
 * this is the first draft, now it is bloated to a package
 * @author nlao
 *
 */
public class CRFB  {
	public static class Param	extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public Param(Class c) {
			super(c);
			parse();
		}		
		public void parse(){	
			//m = getInt("m",5);
			//eps = getDouble("eps",1e-5);
			//lang = getString("lang");
			//diagco=getBoolean("diagco", false);
			
		}	
	}	
	
	public static enum EVType {		
		IN(0,EColor.azure)/*conditioned on*/ 
		, OUT(1,EColor.lightskyblue)/*to be evaluated*/
		, MID(2,EColor.rosybrown2);//hidden nodes
		
		//really a nice place to put data!
		public EColor color;
		public int id;
		EVType(int id, EColor color){
			this.id = id;
			this.color = color;
		}
	}

	public static class Factor{
		public int id;
		public double w=0;
		
		public int iV1,iV2;
		//assume iV1<iV2,	if iV1==-1 then it is a bias feature
		public Factor(int id, int iV1, int iV2){
			this.iV2= iV2;
			this.iV1= iV1;
		}
		public String toString(){
			return String.format("(%d,%d)%.1f",iV1,iV2,w);
		}
		public String print(){
			return String.format("%d\t%d\t%.3f",iV1,iV2,w);
		}
	}
	public static class Variable{
		//public SetI mi= new SetI();
		public VectorX<Factor> vf= new VectorX<Factor>(Factor.class);
		//use reference to update it outside the model
		//public Variable(SetI mia,SetI mid){	this.mia= mi;	}
		EVType type;
		int id;
		public Variable(int id, EVType type){
			this.id = id;
			//mia= new SetI();
			//mid= new SetI();
			this.type=type;
		}
	}
	
	// variables
	public VectorX<Variable> vVar= new VectorX<Variable>(Variable.class);
	// factors
	public VectorX<Factor> vFactor= new VectorX<Factor>(Factor.class);
	// weights
	//VectorD vW;	
	double[] x ;
	
	// expectations//in log domain?	
	public VectorD vEVar=new VectorD();
	public VectorD vEFa;		
	public VectorI viZ=new VectorI();
	public VectorI viY=new VectorI();
	public VectorI viH=new VectorI();
	public VectorI viX=new VectorI();
	public VectorI viA=new VectorI();
	
	/*public void selectSubNet(VectorI viY, VectorI viH	, VectorI viZ){
		this.viH = viH;
		this.viY = viY;
		this.viX = viH; viX.addAll(viY);
		this.viZ = viZ;
	}*/

	/**
	 * x={y,h}, 
	 * estimate p(x|z)
	 * @param viZ: ids of z variables
	 * @param viX: ids of x variables
	 */
	public void meanField(VectorI vi){
		vEVar.extend(viA.size());
		for (int iScan=0; iScan<5; ++iScan)
			for (int ix : vi)
				expectVar(ix);	
	}
	
	private double expectVar(int id){		
		double e=0;
		
		for (Factor fa:  vVar.get(id).vf)
			e+= expectFactor(id,fa);
		
		//exp(e1)/(exp(e0)+exp(e1))=1/(1+exp(e0-e1))
		double p=1/(1+Math.exp(-e));
		vEVar.set(id, p);
		return p;
	}
	
	private double expectFactor(int iVar, Factor fa){//int iF){
		//= vFactor.get(iF);	
		
		if (fa.w==0)		return 0.0;

		double e=1;
		if (fa.iV2!=iVar) e*=vEVar.get(fa.iV2);
		if (fa.iV1>=0) if (fa.iV1!=iVar) 	e*=vEVar.get(fa.iV1);
		return e*fa.w;		
	}	
	private double expectFactor(int id){
		Factor fa= vFactor.get(id);
		double e=vEVar.get(fa.iV2);
		if (fa.iV1>=0) e*=vEVar.get(fa.iV1);
		vEFa.set(id, e);
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
	
	public Variable  addVar(EVType type){
		int id = vVar.size();
		Variable var=new Variable(id,type);
		vVar.add(var);
		viA.add(id);
		switch(type){
		case OUT:	viY.add(id);viX.add(id);break;
		case MID:	viH.add(id);viX.add(id);break;
		case IN:	viZ.add(id);	break;
		}	
		return var;
	}
	
	public String toString(){
		return String.format("|vVar|=%d, |vFa|=%d"
			, vVar.size(), vFactor.size());
	}
	
	public Factor addFactor(int V1, int V2){
		if (V1==V2) 
			FSystem.dieShouldNotHappen();

		if (V1>V2){	int a=V2;	V2=V1;V1=a;	}
		
		Factor f=new Factor(vFactor.size(), V1,V2);
		vFactor.add(f);
		if (V1!=-1)		vVar.get(V1).vf.add(f);
		if (V2!=-1)		vVar.get(V2).vf.add(f);
		return f;
	}
	
	public void test(VectorD vZ){//Sample s){
		vEVar.set(viZ, vZ);		
		//test();
		meanField(viX);
		vX= (VectorD) vEVar.clone();//vEVar.sub(viX);
	}
	
	//public void test(){}
	//public void train(){}
	public void train(VectorD vY,  VectorD vZ){//Sample s){//
		test(vZ);
		
		loss = getValue(vY);			
		vEVar.set(viY, vY);
		meanField(viH);
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
	public VecVecI sampleGibbs(int n, int nBurnIn,int nThining){
		vEVar.extend(viA.size());
		vEVar.setAll(0.5);

		VectorI vi= this.viX;
		//if (withHidden)		vi.addAll(b.viA);
		//else		vi.addOn(b.viZ).addOn(b.viY).sortOn();			
		
		VecVecI vv=new VecVecI ();
		if (nBurnIn>0)
			for (int i=0;i< nBurnIn; ++i)	scanGibbs(vi);
		
		for (int i=0;i<n; ++i){
			if (nBurnIn<=0)
				for (int j=0;j<vEVar.size(); ++j)
					vEVar.set(j,(double)FRand.drawBinary(0.5));
			
			
			for (int j=0;j<nThining; ++j)	scanGibbs(vi);
			vv.add(vEVar.sub(vi).toVectorI());
			System.out.print(".");
		}		
		System.out.println();
		return vv;
		//return null;
	}
	protected void scanGibbs(VectorI vi){

		
		for (int i : vi){
			expectVar(i);
			if (FRand.drawBoolean( vEVar.get(i)))
				vEVar.set(i,1.0);
			else
				vEVar.set(i,0.0);
		}
		return;
	}
	public boolean save(String fn){
		BufferedWriter bw  = FFile.newWriter(fn);
		FFile.write(bw,vVar.size()+"\n");
		for ( Factor f :vFactor ) 
			FFile.write(bw, f.print()+"\n");		
		FFile.flush(bw);		
		return true;
	}	
	/**
	 * estimate p(y|x)
	 * @param yb: starting id of y variables
	 * @param ye: ending id of y variables
	 */
	//public void variational(int yb, int ye){	}	
	
	public static void main(String[] args) {
		//TODO: test it with data from GM hw
		try {
		} catch ( Exception e ) {
			e.printStackTrace();
		}
	}
}
