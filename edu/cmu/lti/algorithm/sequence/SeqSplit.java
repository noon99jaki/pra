package edu.cmu.lti.algorithm.sequence;

import java.util.Iterator;

import edu.cmu.lti.algorithm.container.VectorX;

public class SeqSplit  <T> extends Seq<VectorX<T> >{
	
	T spliter;
	private Iterator<T> it=null;
	public SeqSplit(Class c,Iterable<T> v, T spliter) {
		super(c);
		it=v.iterator();
		//this.pipe=f;
		this.spliter=spliter;
	}

	public VectorX<T> newValue(){	
		return new VectorX<T>(c);
	}
	
	public boolean hasNext()  {
  	if (it==null) return false;
//		if (!it.hasNext()) return false;
		if (spliter==null)  return false;
		
	  x= newValue();

		while (it.hasNext()){
	  	T o=it.next();
	  	
  		if (o.equals(spliter))
	  			break;
	  	x.add(o);
	  }
		return x.size()>0;// true;
  }

}
