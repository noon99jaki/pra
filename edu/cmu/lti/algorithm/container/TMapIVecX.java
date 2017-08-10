package edu.cmu.lti.algorithm.container;

public class TMapIVecX<V> extends TMapXVecX<Integer, V>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapIVecX(Class cv){
		super(Integer.class, (new VectorX<V>(cv)).getClass());
	}
	public MapII newMapKeyI(){
		return  new MapII();
	}
	public VectorI newVectorKey(){
		return new VectorI();
	}

	public VectorX<V> getC(Integer k){
		if (get(k)!=null) 
			return get(k);
		VectorX<V> x=newValue();
		put(k, x);
		x.c= cv;
		return x;
	}	
	public VectorI toVectorK(){
		return (VectorI) super.toVectorK();
	}
	public Integer parseKey(String k){		
		return Integer.parseInt(k);
	}
}

