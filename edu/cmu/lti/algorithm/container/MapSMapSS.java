package edu.cmu.lti.algorithm.container;


public class MapSMapSS extends MapSX<MapSS>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapSMapSS newInstance(){
		return new MapSMapSS();
	}	
	public String newKey(){//needed for primitive classes, silly java
		return null;
	}	
	public MapSS newValue(){
		return new MapSS();
	}	
	
	public MapSMapSS(){
		super(MapSS.class);
	}	
	

}