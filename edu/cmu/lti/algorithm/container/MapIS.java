package edu.cmu.lti.algorithm.container;

public class MapIS  extends MapIX<String>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIS newInstance(){
		return new MapIS();
	}	
	public String newValue(){
		return new String();//null;
	}			
	public MapIS(){
		super(String.class);
	}
	public String parseValue(String v){		
		return v;
	}
	public static MapIS  fromFile(String fn){
		MapIS  vs = new MapIS();
		vs.loadFile(fn);
		return vs;
	}
}