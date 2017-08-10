package edu.cmu.lti.algorithm.math;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;

public class Histogram {
	public VectorI vCount= new VectorI();	
	public VectorD vTick=null;// new VectorD();
	double min, max;
	int nCell;
	public Histogram(double min, double max, int nCell){
		this.max=max;
		this.min=min;
		this.nCell=nCell;
		vCount.extend(nCell);
		step = (max-min)/nCell;
		vTick = VectorD.seq(min, max, nCell);
	}
	double step ;
	public void addInstance(double x){
		int id =(int) Math.floor((x-min)/step);
		vCount.plusOn(id,1);
	}
	public String print(){
		return vTick.join("\t")+"\n"
					+vCount.join("\t")+"\n";
	}
	
	public Histogram shrink(double rate){
		Histogram hist= new Histogram(min,max, (int) Math.floor(nCell/rate));
		for (int i=0; i<vCount.size(); ++i){
			hist.vCount.plusOn((int) Math.floor(i/rate),
					vCount.get(i));
		}
		return hist;
	}
	
	public void clear(){
		vCount.setAll(0);
	}
}
