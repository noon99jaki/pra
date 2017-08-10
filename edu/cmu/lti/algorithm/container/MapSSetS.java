package edu.cmu.lti.algorithm.container;


public class MapSSetS extends MapSX<SetS>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSSetS(){
		super(SetS.class);
	}

	public void add(String k,String v){
		getC(k).add(v);
	}

	public SetS get(String k){
		return getC(k);
	}

}
