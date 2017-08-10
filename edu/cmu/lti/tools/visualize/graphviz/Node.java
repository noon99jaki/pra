package edu.cmu.lti.tools.visualize.graphviz;

import java.io.Serializable;

public class Node extends LabeledObj implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	//	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	//public MapSS ms = new MapSS();		
	public int id;
	public String sID;
	
	//public String label;
/*	public Node(int id, String label){
		this.id = Integer.toString(id);
		setLabel(label);
	}*/
	public Node(String sID, String label){
		this.sID = sID;
		setLabel(label);
	}
	public String toString(){
		//return String.format("node %s %d;"	, format(ms), id);
		return String.format("node %s %s;"	, format(ms), sID);
	}	

}	