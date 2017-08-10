package edu.cmu.lti.algorithm.container;

public class TMapIX <V>  extends TMapXX<Integer, V> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapIX<V> newInstance(){
		return new TMapIX<V>(cv);
	}	
	public SetI newSetKey(){
		return new SetI();
	}
	public TMapIX(Class c){
		super(Integer.class,c);
	}

	public Integer parseKey(String k){		
		return Integer.parseInt(k);
	}
	
}