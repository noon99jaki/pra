package edu.cmu.lti.algorithm.math;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObj;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.Interfaces.ISetDblByStr;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;

public class Momentum implements 
	Serializable,  IGetDblByStr, ISetDblByStr
	,IMultiplyOn, IPlusObjOn, IPlusObj{//, IGetObjByString{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	
	public static class VMomentum extends VectorX<Momentum>{
		public VMomentum(){super(Momentum.class);}
		public void meanOn(){
			for (Momentum m: this)
				m.meanOn();
		}
		public void plusOn(VectorD vd){
		for (int i=0; i<vd.size(); ++i)
			getC(i).addInstance(vd.get(i));		
		}
	}
	
	
	public double V=0;	//variance
	public double m=0;	// mean
	public int n=0; // number of samples
	public Momentum(){
		
	}
	public Momentum(Momentum exp){
		this.copy(exp);
	}
	
	public Momentum clone(){
		Momentum s= new Momentum();
		s.plusOn(this);
		return s;
	}
	public void copy(Momentum x){
		n=x.n;	V=x.V;m=x.m;
	}	
	public void clear(){
		n=0; V=0;m=0;
	}	

	public Double getDouble(String name) {
		//if (name.equals(CTag.g)) return g;//getG();
		System.err.println("unknown variable "+name);
		return null;
	}
	
	public void setDouble(String name,Double d){
		//if (name.equals(CTag.we)) we =d;		
		System.err.println("unknown variable "+name);
		return;
	}
	public void addInstance(double x){
		n++;
		m+=x;
		V+=x*x;
	}
	public void meanOn(){
		this.multiplyOn(1.0/n);
	}
	public Momentum plusOn(Momentum x){
		m+=x.m;	V+=x.V; n+=x.n;
		return this;
	}
	
	public Momentum plusObjOn(Object x){
		return plusOn( (Momentum) x);
	}
	public Momentum plusObj(Object x){
		return (new Momentum(this)).plusOn( (Momentum) x);
	}
	
	public Momentum multiplyOn(Double x){
		//n*=x;
		V*=x;m*=x;
		return this;			
	}
	public Momentum multiply(Double x){
		return (new Momentum(this)).multiplyOn(x);
	}
	
	public static String getTitle() {
		return "n\tMean\tVariance\tSD"; //\te\teY
	}
	public static double getSD(double V, int n){
		if (n<=1) return 0.0;
		return Math.sqrt(V *n/(n-1));
	}
	public double getSD(){
		return getSD(V,n);
	}
	public String print() {
		return String.format("%d\t%.2f\t%.2f\t%.2f"//\t%.2f\t%.2f
				,n,m,V,getSD()	);
	}
	public String toString() {
		return String.format("%d M)%.2f V)%.2f"
				,n,m,V);
	}
}
