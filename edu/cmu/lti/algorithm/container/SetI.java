/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.Iterator;
import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.util.file.FFile;

/**
 * @author nlao
 *This class is an extension to TSet&lt;Integer&gt;
 */
public class SetI extends SetX<Integer> implements ICloneable, IPlusObjOn{
//	public int compareTo(SetI c){
//		return ;
//	}
	public SetI plusObjOn(Object x){
		this.addAll((SetI)x);
		return this;
	}
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public SetI newInstance(){
		return new SetI();
	}	
	public SetI(int i){
		this();
		this.add(i);
	}
	public Integer newValue() {//weakness of Java template
		return 0;
	}
	public VectorX<Integer >newVector(){
		return new VectorI();		
	}
	public Integer newKey(){//needed for primitive classes, silly java
		return 0;
	}	
	public SetI(){super(Integer.class);}
	public SetI(Iterable<Integer> v){
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
	
	
	public SetI  andSet(Set<Integer> m){
		return (SetI) super.andSet(m);
	}
	public SetI  and(Set<Integer> m){
		return (SetI) super.andSet(m);
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
	

//
//	public int hashCode() {
//		int hash = 0;
//		for (Integer i: this) 	hash = Hash.incrementalHash(hash, i);
//		return hash;
//	}

//	//Used to compare arrays for content values.
//	public boolean equals(Object obj) {
//		// Always expect SetI
//		if (obj instanceof SetI == false) return false;
//		
//		SetI vec = (SetI) obj;
//		if (this.size() != vec.size()) return false;
//		return compareTo(vec) ==0;
//	}
	

	public int compareTo(SetI c) {
		int len = Math.min(size(), c.size());

		Iterator<Integer> it = this.iterator();
		Iterator<Integer> it_c = c.iterator();		
		for (int i = 0; i < len; ++i) {
			int cmp = it.next().compareTo(it_c.next());
			if (cmp != 0) return cmp;
		}
		if (size() < c.size()) return -1;
		else if (size() > c.size()) return 1;
		return 0;
	}
	public SetI clone() {
		return new SetI(this);
	}
	public static void main(String[] args) {
		// test hashing
		MapXI<SetI> map = new MapXI<SetI>(SetI.class);
		SetI vec = new SetI();
		
		for (int i = 4; i >=1; --i) {
			vec.add(i);
			map.plusOn(vec);//.clone());
		}
		
		for (int i = 1; i <= 2; ++i) {
			vec.remove(vec.first());
			map.plusOn(vec);
		}
		System.out.println(map.join("=", "\n"));
		return;
	}
}
