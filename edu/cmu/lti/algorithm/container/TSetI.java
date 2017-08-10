package edu.cmu.lti.algorithm.container;

import java.util.Set;

import edu.cmu.lti.util.file.FFile;

public class TSetI extends TSetX<Integer>{
//public int compareTo(SetI c){
//	return ;
//}
	
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TSetI newInstance(){
		return new TSetI();
	}	
	public TSetI(int i){
		this();
		this.add(i);
	}
	public VectorX<Integer >newVector(){
		return new VectorI();		
	}
	public Integer newKey(){//needed for primitive classes, silly java
		return 0;
	}	
	public TSetI(){super(Integer.class);}
	public TSetI(Iterable<Integer> v){
		super(v, Integer.class);
	}
	
	public Integer parseLine(String k){		
		return Integer.parseInt(k);
	}
	public VectorI toVector(){
		return (VectorI) super.toVector();
	}
	
	public static SetI  fromFile(String fn){
		return new SetI(FFile.enuLines(fn).toSeqI());
	}
	public static SetI  fromString(String txt, String sep){
		SetI m= new SetI();
		m.loadLine(txt, sep);
		return m;
	}
	
	
	public TSetI  andSet(Set<Integer> m){
		return (TSetI) super.andSet(m);
	}
	public TSetI  and(Set<Integer> m){
		return (TSetI) super.andSet(m);
	}
	
	public double cosine(SetI m){
		if (size()==0 || m.size()==0)
			return 0.0;
		double nAnd=0;
		for ( Integer x : m) 
			if (contains(x))
				++nAnd;
		return nAnd/Math.sqrt(size()*m.size());
	}
	
	
	public double cosine(SetI m, VectorD vW2){
		if (size()==0 || m.size()==0)
			return 0.0;
		double nAnd=0;
		for ( Integer x : m) 
			if (contains(x))
				nAnd+= vW2.get(x);
		
		double sum1=0;
		for ( Integer x : this) 
			sum1+=vW2.get(x);
		
		double sum2=0;
		for ( Integer x : m) 
			sum2+=vW2.get(x);
		return nAnd/Math.sqrt(sum1*sum2);
	}
	
	}
