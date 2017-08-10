/**
 * 
 */
package edu.cmu.lti.algorithm.math;

import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.VectorI;

/**
 * @author nlao
 *
 */
public class Distribution{// extends MapID{
	public MapID mCount = new MapID();
	public MapID mDist = new MapID();// a normalized version of mCount
	
	public double sum, mean, var, median;
	
	//public Distribution(){}
	public Distribution(VectorI viSample){
		if (viSample==null) 
			return;
		mCount.from(viSample);
		update();
	}
	public Distribution(MapID mSample){
		if (mSample==null) 
			return;
		mCount.load(mSample);
		update();
	}	
	
	
	public String toStringCount(double baseline){			
		return mCount.subNEqualTo(1.0).join(")"," ");
	}
/*	public String toStringCount(double min){		
		return m_count.truncate(min).join(")"," ");
	}	*/
	public void addSample(int i){
		mCount.plusOn(i);
		update();		
	}
	public void addSamples(VectorI viSample){
		mCount.plusOn(viSample);
		update();		
	}
	
	public void update(){
		mDist= (MapID) mCount.normalize();
		sum = mCount.sum();
		mean = sum/ mCount.size();
		//var = this.norm2();	
		median=percentile(0.5);
	}		
	public int percentile(double p){
		double u=p*sum;
		for ( Map.Entry<Integer,Double> e : mCount.entrySet() ) {
			Integer k = e.getKey();
			Double v = e.getValue();
			u -= v;
			if (u<=0.0) return k;
		}
		System.err.println("this should not happen");
		return -1;
	}
}