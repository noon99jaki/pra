package edu.cmu.lti.algorithm.sequence;

import java.util.Iterator;

import edu.cmu.lti.algorithm.sequence.Pipes.PipeSX;
import edu.cmu.lti.util.file.FFile;


/**
 *  * fun(this XX xx, YY yy) is helpful
 * it acts like a pipe, and glues two types of things together
 * 
//class and member variable pointers are needed 
// because the lack of yield return

 */
public class SeqTransform <T> extends Seq<T>{
	
	public Pipe pipe=null;
	private Iterator<T> it=null;
	public <T1> SeqTransform(Class c,Iterable v, Pipe<T1,T> f) {
		super(c);
		it=v.iterator();
		this.pipe=f;
		//_init();
		//c=getArgClass1();// getClass(getClass().getGenericSuperclass());

	}
	public <T1> SeqTransform(Class c, String fn, boolean bSkipTitle){
		this (c, FFile.enuLines(fn, bSkipTitle), new PipeSX<T>(c));
	}
	
	public <T1> SeqTransform(Class c, String fn){
		this (c, FFile.enuLines(fn), new PipeSX<T>(c));
	}
  public boolean hasNext()  {
  	if (it==null) return false;
  	
  	do{
			if (!it.hasNext()) return false;
			if (pipe==null)
				x=it.next();
			else
				x= (T) pipe.transform(it.next());
  	}while(x==null);
  	
		return true;
  }
}
