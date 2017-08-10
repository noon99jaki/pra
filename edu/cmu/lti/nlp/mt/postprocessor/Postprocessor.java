package edu.cmu.lti.nlp.mt.postprocessor;

import java.util.Map;

import edu.cmu.lti.nlp.mt.MTResult;
import edu.cmu.lti.nlp.mt.MTResult.Target;

public abstract class Postprocessor extends MTProcessor{

	public String toString(){
		return this.getClass().getSimpleName();
	}
	
	public void process(MTResult tr){		
		if(!p.isCapable(tr.srcLang,tr.trgLang,tr.type))
			return;
		
		//for(Target t:t.){
		for ( Map.Entry<String,Target> e :tr.targets.entrySet() ) {
			String text = e.getKey();
			Target t = e.getValue();
			t.text= process(text,tr.srcLang,	tr.trgLang);
		}	
	}

	public abstract String process(String text,String srcLang,String trgLang);


}
