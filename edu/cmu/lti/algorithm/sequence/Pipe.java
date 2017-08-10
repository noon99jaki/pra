package edu.cmu.lti.algorithm.sequence;

public interface Pipe <T1,T2>{
		// implements Iterable <T2>, Iterator<T2>{
		public T2 transform(T1 x);
	
}
