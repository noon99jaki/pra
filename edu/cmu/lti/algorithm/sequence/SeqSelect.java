package edu.cmu.lti.algorithm.sequence;

import java.util.Iterator;

import edu.cmu.lti.algorithm.sequence.Pipe;

public class SeqSelect <T> extends Seq<T>{
	
	public Pipe<T,Boolean> pipe=null;
	private Iterator<T> it=null;
	public SeqSelect(Class c,Iterable v, Pipe<T,Boolean> f) {
		super(c);
		it=v.iterator();
		this.pipe=f;
	}

  public boolean hasNext()  {
  	if (it==null) return false;
		if (!it.hasNext()) return false;
		
		while (it.hasNext()){
			x=it.next();
			if (pipe.transform(x)) return true;
		}
		x=null;
		return false;
  }

}
