package edu.cmu.lti.algorithm.sequence;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Pipes.PipeSVS;
import edu.cmu.lti.util.file.FFile;


public class SeqVS extends SeqTransform<VectorS>{
	
	public SeqVS(String fn, String sep ){
		super(null, FFile.enuLines(fn), new PipeSVS(sep));
	}
	
	public SeqVS(Iterable<String> v, Pipe<String,VectorS> f) {
		super(VectorS.class, v, f);
	}
}
