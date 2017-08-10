package edu.cmu.lti.algorithm.learning;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;

public class Interfaces {
/*	public static interface IDataSet{
		public boolean load(String fn);
	}*/
	
	
	/**
	 * 
	 * @author nlao
	 *
	 */
/*	public static interface IInstance extends Serializable{
		public Example toM3rdExample(boolean bTraining);
		//public boolean load(String fn);
		public boolean parseLine(String line);
		//public IEva evaluate(IInstance ins);
	}*/
	public static interface IEva extends Serializable,IPlusObjOn, IMultiplyOn
		, Cloneable,ICloneable{//, ICopyable{//
		
	}

	/*
	public static interface IC2Classifier{
		public abstract VectorD testBinarized(
			DataSet dsTest);
		public abstract Object trainBinarized(
				DataSet dsTrain, VectorI vp, VectorI vn);
				
	}*/
}
