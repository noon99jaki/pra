package edu.cmu.lti.nlp.chinese.consts;

import edu.cmu.lti.algorithm.container.SetS;

public class CSet  {
	public SetS m =new SetS();
//	public String[] vs=null;
/*	
	public SetS getInstance() {
		if (instance==null) 	 instance = new SetS();
		instance.from(vs);	
		return instance;
	}	*/
	protected CSet(String[] vs) {
		//this.vs = vs;
		m.load(vs);		
	}	
}
