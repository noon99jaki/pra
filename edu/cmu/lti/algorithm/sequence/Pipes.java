package edu.cmu.lti.algorithm.sequence;

import java.util.ArrayList;

import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.system.FClass;
import edu.cmu.lti.util.text.FString;

public class Pipes {

/*
	public static interface Pipe<T1,T2>{
		// implements Iterable <T2>, Iterator<T2>{
		public T2 transform(T1 x);
	}*/
	
	public static interface PipeSS {
		String transform(String s);
	}
 
	// convert string to anything that implements IParseLine
	public static class PipeSX<T2>  implements Pipe<String,T2>{
		public Class c=null;//Object.class;	

		public PipeSX(Class c){
			this.c=c;
			//c=(Class)	 ((ParameterizedType)this.getClass().getGenericSuperclass())
			 //.getActualTypeArguments()[0];
			
			//c=(Class) this.getClass()	.getGenericInterfaces()[0];
		}
		public T2 transform(String s){
			if (s==null) 
				return null;
			T2 x=(T2)FClass.newValue(c);
			if (! ((IParseLine)x).parseLine(s))
				x=null;			
			return  x;
		}
	}
	
	public static class PipeVSS  implements Pipe<VectorS,String>{
		int iCol;
		public PipeVSS(int iCol ){
			this.iCol=iCol;
		}
		public String transform(VectorS v){
			return v.get(iCol);
		}
	}	
//	public static class PipeVSS  implements Pipe<String[],String>{
//		int iCol;
//		public PipeVSS(int iCol ){
//			this.iCol=iCol;
//		}
//		public String transform(String[] v){
//			return v[iCol];
//		}
//	}
	
	public static class PipeSVS  implements Pipe<String, VectorS>{
		String sep= "\t";
		public PipeSVS(){	
		}
		public PipeSVS(String sep){
			this.sep=sep;
		}
		public VectorS transform(String s){
			return FString.splitVS(s, sep);
		}
	}

	/*Pipe pipePickLast= new Pipe<ArrayList<String[]>, String[]>()
	{String[] transform(){
		}
	};*/
	
	public static String sep=",";
	//static char sep=',';

	
	public static Pipe pipePickLast= new Pipe<ArrayList<String[]>, String[]>(){
		public String[] transform(ArrayList<String[]> v){
			return v.get(v.size()-1);};};
	
	public static Pipe pipePickMid= new Pipe<ArrayList<String[]>, String[]>(){
		public String[] transform(ArrayList<String[]> v){
			return v.get(v.size()/2);};};

					
			
	public static Pipe pipeLaterHalf= new Pipe<ArrayList<String[]>, String>(){
		public String transform(ArrayList<String[]> v){
			StringBuffer sb= new StringBuffer();
			for (int i=v.size()/2; i<v.size();++i)
				sb.append(FString.join( v.get(i),",")).append("\n");
			return sb.toString();};};

					
	public static Pipe pipeJoinVS= new Pipe<String[], String>(){
		public String transform(String[] v){
			return FString.join(v,sep);};};
				
}
