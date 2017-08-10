package edu.cmu.lti.algorithm.sequence;

import java.util.Iterator;

public class SeqEnum<T> extends Seq<T>{
	private Iterator<T> it=null;

	public SeqEnum(Class c, Iterable<T> v ) {
		super(c);
		it=v.iterator();

	}
  public boolean hasNext()  {
  	if (it==null) return false;
		if (!it.hasNext()) return false;
		x=it.next();
		return true;
  }
}
