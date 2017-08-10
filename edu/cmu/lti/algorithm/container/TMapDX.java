package edu.cmu.lti.algorithm.container;


public class TMapDX<V>   extends TMapXX<Double, V>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TMapDX<V> newInstance(){
		return new TMapDX<V>(cv);
	}
	public Double newKey(){//weakness of Java template
		return null;
	}
	public VectorD newVectorKey(){
		return new VectorD();
	}	
	public TMapDX(Class c){
		super(Double.class,c);
	}

	public SetD newSetKey(){
		return new SetD();
	}	
	//public MapDD newMapXD(){
		//return  new MapDD();}
	public MapDI newMapKeyI(){		
		return  new MapDI();	
	}
	public Double parseKey(String v){		
		return Double.parseDouble(v);
	}

}
