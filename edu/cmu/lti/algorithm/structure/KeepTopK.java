package edu.cmu.lti.algorithm.structure;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;

//sort in descending order
public class KeepTopK {

	public VectorX<Object> objects_= new VectorX<Object>(Object.class);
	public VectorD sort_values_= new VectorD();
	public VectorD raw_values_= new VectorD();
	
	public int addValueCost(double raw_value, double cost, Object obj){
		return addValue(-raw_value, raw_value, obj);
	}
	
	public int K_;
	public KeepTopK(int K){
		this.K_=K;
	}
	public void clear(){
		objects_.clear();	
		sort_values_.clear();	
		raw_values_.clear();	
	}
	public int size() {
		return objects_.size();
	}
	public double getThreshold(){
		return - sort_values_.lastElement();
	}


	
	//sort in ascending order of sort_value
	public int  addValue(double sort_value, double raw_value, Object obj){
		int i = sort_values_.findSorted(sort_value);
		
		if (i>=K_) return -1;
		
		sort_values_.insertTruncate(sort_value, i, K_);
		objects_.insertTruncate(obj, i, K_);
		raw_values_.insertTruncate(raw_value, i, K_);
		return i;
	}
	
	//sort in ascending order of |val| 
	public int addAbsValue( double val, Object obj){
		return addValue(-Math.abs(val), val, obj);
	}
	
	//sort in ascending order of |val| 
	public int addAbsValue( double val){
		return addValue(-Math.abs(val), val, null);
	}
	
	public int  addValue(double raw_value){
		return addValue(-raw_value, raw_value, null);
	}
	
}
