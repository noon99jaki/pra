package edu.cmu.lti.algorithm.sequence;

import java.util.Iterator;

import edu.cmu.lti.algorithm.container.VectorX;

public class SeqGroup  <T> extends Seq<VectorX<T> >{
	
	public Pipe pipe=null;
	private Iterator<T> it=null;
	public SeqGroup(Class c,Iterable<T> v, Pipe<T,Object> f) {
		super(c);
		it=v.iterator();
		this.pipe=f;
	}

	public boolean hasNext()  {
  	if (it==null) return false;
		//if (!it.hasNext()) return false;
		if (pipe==null)  return false;
		
	  x= new VectorX<T>(c);

		Object lastValue=null;
		while (it.hasNext()){
	  	T o=it.next();
	  	
	  	Object thisValue= pipe.transform(o);
	  	if (lastValue!=null)
	  		if (!thisValue.equals(lastValue))
	  			break;
	  	lastValue= thisValue;
	  	x.add(o);
	  }
		return x.size()>0;// true;
  }
 
}
