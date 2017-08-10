package edu.cmu.lti.algorithm.learning.data;

import java.io.Serializable;

import edu.cmu.minorthird.classify.Example;

public class Instance implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Example toM3rdExample(boolean bTraining){
		return null;
	}
	//public boolean load(String fn);
	public boolean parseLine(String line){
		return true;
	}
	//public IEva evaluate(IInstance ins);
}