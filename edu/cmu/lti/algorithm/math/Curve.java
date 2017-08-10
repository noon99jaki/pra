package edu.cmu.lti.algorithm.math;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.VectorD;


/** curve = {{x0 y0}... {xn, yn}} 
 * */
public class Curve implements   Serializable,IPlusObjOn{//, IMultiplyOn{
	private static final long serialVersionUID = 2008042701L; 
	public VectorD vX,vY;
	public Curve(VectorD vX, 	VectorD vY){
		this.vX = vX;
		this.vY = vY;		
	}
	public Curve(){
		this.vX = new VectorD();
		this.vY = new VectorD();
	}
	public Curve addOn(double x, double y){
		vX.add(x);
		vY.add(y);
		return this;
	}
	public Curve plusObjOn(Object x){
		this.copy(this.plus((Curve) x));
		return this;
	}
	public Curve copy(Curve e){
		this.vX.copyShallow(e.vX);
		this.vY.copyShallow(e.vY);
		return this;
	}	
	public Curve plus(Curve c){
		Curve c1 = new Curve();
		c1.vX = (VectorD) vX.clone().catOn(c.vX).sortOn();
		c1.vY = this.getAt(c1.vX).plusOn(c.getAt(c1.vX));
		return c1;
	}
	public String toString(){
		return vX.toString()+"\n"+vY.toString()+"\n";
	}
	/**
	 * 
	 * @param i	expolate line between xi and x_i+1 
	 * @param x
	 * @return
	 */
	public double getAt(int i, double x){
		if (i>=vX.size()-1) return vY.lastElement();
		if (i<0) return vY.firstElement();
		double y1=vY.get(i);
		double y2=vY.get(i+1);
		double x1=vX.get(i);
		double x2=vX.get(i+1);
		double slope = (y2-y2)/(x2-x1);		
		return y1 + (x-x1)*slope;
	}
	/** interpolation, 
	 * assume y is contant befor x0 and beyond xn	 */
	public double getAt(double x){
		if (vX.size()==0) return 0.0;
		int i = vX.findSorted(x)-1;
		if (i<0) return vY.get(0);
		if (vX.get(i)== x) return vY.get(i);
		return getAt(i,x);
	}
	

	public VectorD getAt(VectorD vx){
		VectorD vy= new VectorD();
		vy.ensureCapacity(vx.size());
		for(double x: vx)
			vy.add(getAt(x));
		return vy;
	}

	//ugly implementation based on micro operations
	/*public Curve plus(Curve c1, Curve c2){
		Curve c = new Curve();
		//c.vX = c1.vX.clone().addOn(c2.vX).sort();
		int i1=0,i2=0;
		for(; i1<c1.vX.size() && i2<c1.vX.size();){
			double x1 = c1.vX.get(i1);
			double x2 = c2.vX.get(i2);
			double y1 = c1.vY.get(i1);
			double y2 = c2.vY.get(i2);
			if (x1==x2){
				c.addOn(x1, y1+y2);
				++i1;++i2;
			}
			if (x1<x2){
				c.addOn(x1, y1+ c2.getAt(i2-1, x1));
				++i1;
			}
			else{
				c.addOn(x2, c2.getAt(i2-1, x1)+y2);
				++i2;
			}			
		}
		return c;
	}*/
}
