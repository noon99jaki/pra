package edu.cmu.lti.algorithm.structure;

import edu.cmu.lti.algorithm.container.TMapDD;

public class KeepUniqTopKwCost  extends KeepTopK{
	public KeepUniqTopKwCost(int K){
		super(K);
	}
	//public VectorD costs_ = new VectorD();
	//public VectorI costs_ = new VectorI();
	//public MapDI value_costs_ = new MapDI();
	public TMapDD value_costs_ = new TMapDD();
	
	public void clear(){
		super.clear();
		value_costs_.clear();
	}

	public int addValueCost(double raw_value, double cost, Object obj){
		Double existing_cost = value_costs_.get(raw_value);
		
		if (existing_cost ==null) {	// a new entry
			int id =  super.addValue(-raw_value, raw_value, obj);
			if (id!=-1) {
				value_costs_.put(raw_value, cost);
				if (value_costs_.size() > this.size())
					value_costs_.remove(value_costs_.firstKey());
			}
			return id;
		}
		
		if (cost >=  existing_cost)		return -1;
		
		value_costs_.put(raw_value, cost);
		
		// replace an existing entry
		int i = this.raw_values_.findExact(raw_value);
		this.objects_.set(i, obj);
		//this.raw_values_.set(i, raw_value);
		//this.values_.set(i, value);
		return i;
	}
	
//	public int addValueCost(double raw_value, double cost, Object obj){
//		Integer i = value_id_.get(raw_value);
//		if (i ==null) {	// a new entry
//			i =  super.addValue(-raw_value, raw_value, obj);
//			//if (i==-1) return -1;
//			costs_.insertTruncate(cost, i, K_);
//			return i;
//		}
//		
//		double existing_cost = costs_.get(i);
//		if (cost >=  existing_cost)		return -1;
//		
//		// replace an existing entry
//		this.raw_values_.set(i, raw_value);
//		this.sort_values_.set(i, -raw_value);
//		this.costs_.set(i, cost);
//		this.objects_.set(i, obj);
//		return i;
//	}
}
