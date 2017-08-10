package edu.cmu.lti.algorithm.container;

public class MapSMapSI extends MapSX< MapSI>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSMapSI newInstance(){
		return new MapSMapSI();
	}	

	public MapSI newValue(){
		return new MapSI();
	}	
	public MapSMapSI(){
		super(MapSI.class);
	}	
	public MapSMapSI plusOn(String k1, String k2, int x){
		getC(k1).plusOn(k2, x);
		return this;
	}
	public Integer get(String k1, String k2){
		MapSI m= get(k1);
		if (m==null) return null;
		return m.get(k2);
	}
}
