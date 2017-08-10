package edu.cmu.lti.algorithm.container;

import java.util.Map;

public class MapIMapID extends MapIX<MapID>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIMapID newInstance(){
		return new MapIMapID();
	}	
	public Integer newKey(){//needed for primitive classes, silly java
		return 0;
	}	
	public MapID newValue(){
		return new MapID();
	}	
	
	public MapIMapID(){
		super(MapID.class);
	}	
	
	public MapIMapID transpose(){
		MapIMapID mm=newInstance();
		for ( Map.Entry<Integer, MapID> e1 : entrySet() ) {
			Integer k1 = e1.getKey();
			MapID  m = e1.getValue();
			
			for ( Map.Entry<Integer, Double> e2 : m.entrySet() ) {
				Integer k2 = e2.getKey();
				Double x = e2.getValue();
				mm.getC(k2).put(k1, x);
			}
		}
		return mm;
	}
	public MapIMapID plusOn(MapIMapID mm, double scale) {
		for (Map.Entry<Integer, MapID> it : mm.entrySet())
			this.getC(it.getKey()).plusOn(it.getValue(), scale);
		return this;
	}
}