package edu.cmu.lti.algorithm.sequence;

import java.util.Iterator;
import java.util.NoSuchElementException;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Pipes.PipeSVS;
import edu.cmu.lti.algorithm.sequence.Pipes.PipeVSS;
import edu.cmu.lti.algorithm.sequence.SeqS.PipeXS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FClass;


/**
 * this is a sequence of objects 
 * which can be deserialized from a text file
 * with one object per line
 * 
 * @author nlao
 *
 * @param <T>
 * http://www.artima.com/weblogs/viewpost.jsp?thread=208860
 */

public abstract class Seq<T> implements Iterable <T>, Iterator<T>{
	
	//public static interface IWhere {boolean extract(T o);}
	
	//public TSequence<T> Where(IWhere f){	}
	
	public Class c=null;//stupid java	
	public T newValue(){	
		return (T) FClass.newValue(c);
	}
	
	
	/*
	private Class getArgClass1(){
		try{
			Type t=getClass().getGenericSuperclass();
			return (Class)((ParameterizedType)t)
				.getActualTypeArguments()[0];
		}
		catch(Exception e){
			
		}
		return null;
	}
	private Class getClass(Type type){
		//Type type=getClass();//.getGenericSuperclass();
    if (type instanceof Class) {
      return (Class) type;
    }
    else if (type instanceof ParameterizedType) {
      return getClass(((ParameterizedType) type).getRawType());
    }
    else if (type instanceof GenericArrayType) {
      Type componentType = ((GenericArrayType) type).getGenericComponentType();
      Class<?> componentClass = getClass(componentType);
      if (componentClass != null ) {
        return Array.newInstance(componentClass, 0).getClass();
      }
      else {
        return null;
      }
    }
    else {
      return null;
    }
	}
	private void _init(){
		//FSystem.

	}*/

	public Seq(Class c) {
		this.c=c;
	}

	
	public void save(String fn){
		FFile.save(this,fn,"\n",null);
	}
	public void save(String fn, String sep){
		FFile.save(this,fn,sep,null);
	}


	public <T2> SeqGroup groupBy(Pipe<T, T2> p){
		return new SeqGroup(c, this, p);
	}
	
	public SeqSplit splitBy(T spliter){
		return new SeqSplit(c, this, spliter);
	}

	public SeqSelect where(Pipe<T, Boolean> p){
		return new SeqSelect(c, this, p);
	}
	
	public <T1> SeqTransform<T1>  select (Pipe<T, T1> p){
		return new SeqTransform(c, this, p);
	}
	
	public SeqS selectS(String name){
		return new SeqS(this, new PipeXS<T>(name));
	}
	
  public Iterator<T> iterator(){
  	return this;
  }
  

  public void remove()  {
  }
  //int nSucc=0;  int nTot=0;  int nFailure=0;
  
  T x=null;
  
  public T next() throws NoSuchElementException {
     //if ( x==null )        throw new NoSuchElementException();
     return x;
  }
  

  public static void main ( String[] args )   {
 /*  for ( PMAbsInfor abs: 
  	 new TSequence<PMAbsInfor>("pmid.sgd.crawl.ex") )
     System.out.println( abs );*/
     
  }
}
