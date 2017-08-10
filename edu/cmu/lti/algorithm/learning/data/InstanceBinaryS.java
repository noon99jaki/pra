package edu.cmu.lti.algorithm.learning.data;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Example;

public class InstanceBinaryS extends Instance{
	//implements IInstance, IGetIntByString, IGetStringByString {

	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	// public MapSD msd= new MapSD();
	//public int iLabel;
	public String label;
	public SetS m = new SetS();

	public InstanceBinaryS() {}

	public InstanceBinaryS(String label, SetS m) {
		this.label = label;
		this.m = m;
	}

	public String getString(String name) {
		if (name.equals("label")) return label;
		return null;
	}

	public Integer getInt(String name) {
		// if (name.equals("iLabel")) return iLabel;
		return null;
	}
	public String toString(){
		return label + " " + m.join(" ");
	}

	public ClassLabel getLabel(boolean bTraining){
		if ( bTraining ) 
			return ClassLabel.multiLabel(label, 1.0);			
		else 
			return new ClassLabel();		
	}
	public Example toM3rdExample(boolean bTraining){
		return null;//new Example( m.toM3rdInstance(), getLabel(bTraining) );
	}
}