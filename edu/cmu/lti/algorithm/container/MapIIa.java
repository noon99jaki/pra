package edu.cmu.lti.algorithm.container;

/**
 * @author nlao
 * another implementation of MapII;
 *
 */
public class MapIIa extends MapXI<Integer>{//TMap<Integer, Integer> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIIa newInstance(){
		return new MapIIa();
	}
	
	public Integer newValue(){//needed for primitive classes, silly java
		return 0;
	}	
	
	public MapIIa(){
		super(Integer.class);
		//super(Integer.class, Integer.class);
	}	


}

