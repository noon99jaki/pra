package edu.cmu.lti.algorithm.container;

/**
 * @author nlao
 *
 */
public class MapIMapII  extends MapIX<MapII>{//TMapMapIIX<Integer>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapIMapII newInstance(){		
		return new MapIMapII();
	}	
	public MapII newValue(){		
		return new MapII();
	}	

	//public MapII newValue(){	return new MapII();	}	
	
	public MapIMapII(){
		super(Integer.class);
	}	
	public Integer get(int i, int j) {
		MapIX<Integer> m = get(i);
		if (m==null)
			return null;
		return m.get(j);
	}
	public boolean containsKey(int i,int j){
		return get(i,j)!=null;		
	}
}