package edu.cmu.lti.algorithm.container;

public class MapSetSI extends MapSX<SetI>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSetSI(){
		super(SetI.class);
	}

	public void add(String k,Integer v){
		getC(k).add(v);
	}

	public SetI get(String k){
		return getC(k);
	}

}