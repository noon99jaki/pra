package edu.cmu.lti.nlp.mt.scorer;

import edu.cmu.lti.nlp.mt.MTResult;

public abstract class Scorer{
	
	public String toString(){
		return this.getClass().getSimpleName();
	}
	
	public abstract void score(MTResult translation);

}
