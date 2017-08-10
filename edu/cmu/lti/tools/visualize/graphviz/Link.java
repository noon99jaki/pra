package edu.cmu.lti.tools.visualize.graphviz;

import java.io.Serializable;

public  class Link extends LabeledObj implements Serializable{
private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public String ifrom, ito;
	public Link( String ifrom, String ito){
		this.ifrom = ifrom;
		this.ito = ito;
	}
	public String toString(){
		return String.format("%s -> %s %s;", ifrom, ito, format());
	}		

}	