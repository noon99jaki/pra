package edu.cmu.lti.algorithm.sequence;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Pipes.PipeSVS;


/**
 * this is a sequence of strings
 * which is special because we know how to 
 * convert it from/to other types
 * @author nlao
 *
 */
public class SeqS extends SeqTransform<String>{ 	

	public static class PipeSI  implements Pipe<String,Integer>{
		public Integer transform(String s){
			return Integer.parseInt(s);
		}
	}
	public static class PipeSD  implements Pipe<String,Double>{
		public Double transform(String s){
			return Double.parseDouble(s);
		}
	}

	// convert anything that implements IGetStringByString to string
	public static class PipeXS<T1>  implements Pipe<T1, String>{
		public String name_;	
		public PipeXS(String name){
			this.name_=name;
		}
		public String transform(T1 x){
			return  ((IGetStrByStr)x).getString(name_);
		}
	}

	public static class PipeSMatch  implements Pipe<String,String>{
		public String pattern;	
		public PipeSMatch(String pattern){
			this.pattern=pattern;
		}
		public String transform(String s){
			if (s.matches(pattern)) return s;
			return null;
		}
	}
	public static class PipeSContains  implements Pipe<String,String>{
		public String pattern;	
		public PipeSContains(String pattern){
			this.pattern=pattern;
		}
		public String transform(String s){
			if (s.indexOf(pattern)>=0) return s;
			return null;
		}
	}
	
	public static class PipeSUniq  implements Pipe<String,String>{
		public SetS m=new SetS();
		
		public String transform(String s){
			if (s==null) return null;
			if (m.contains(s)) return null;
			m.add(s);
			return s;
		}
	}


	public SeqS(Iterable<String> v ) {
		super(String.class,v,null);
	}
	
	public <T1> SeqS(Iterable v, Pipe<T1,String> f) {
		super(String.class,v,f);
	}


	public static PipeSI pipeSI= new PipeSI();
	public static PipeSD pipeSD= new PipeSD();
	
	public Seq<Integer> toSeqI(){
		return new SeqTransform<Integer>(Integer.class,this, pipeSI);
	}
	public Seq<Double> toSeqD(){
		return new SeqTransform<Double>(Double.class,this, pipeSD);
	}

	
  public SeqTransform<VectorS> toRows(String sep) {
  	return new SeqTransform<VectorS>(
  			String.class, this, new PipeSVS(sep));
  }
}