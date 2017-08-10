package edu.cmu.lti.algorithm.structure;

import edu.cmu.lti.algorithm.container.MapDI;
import edu.cmu.lti.algorithm.container.SetD;

public class KeepUniqTopK extends KeepTopK{
	public KeepUniqTopK(int K){
		super(K);
	}
	public SetD value_id_= new SetD();
//	public MapDI value_id_= new MapDI();
	public void clear(){
		value_id_.clear();
		super.clear();
	}

	public int addValue(double sort_value, double raw_value, Object obj){
		if (value_id_.contains(raw_value)) return -1;
		int id= super.addValue(sort_value, raw_value, obj);
		//value_id_.put(raw_value, id);
		value_id_.add(raw_value);
		return id;
	}
}
