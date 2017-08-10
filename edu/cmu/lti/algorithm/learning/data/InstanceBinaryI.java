package edu.cmu.lti.algorithm.learning.data;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.SetI;

public class InstanceBinaryI extends Instance  implements Serializable{
	private static final long serialVersionUID = 2008042701L; 
	//, IGetIntByString, IGetStringByString {
	// public MapSD msd= new MapSD();
	public int label;
	public SetI m = new SetI();

	public InstanceBinaryI() {}

	public InstanceBinaryI(int label, SetI m) {
		this.label = label;
		this.m = m;
	}
/*
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
	}*/
	


}
