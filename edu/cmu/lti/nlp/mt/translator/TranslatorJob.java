package edu.cmu.lti.nlp.mt.translator;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.mt.MTResult;

public class TranslatorJob implements Runnable{
	
	private Translator translator;
	private MTResult translation;
	

	public TranslatorJob(
			Translator translator,MTResult translation	){
		this.translator=translator;
		this.translation=translation;
	}

	public void run(){
		StringBuilder message=new StringBuilder();
		String txt = translation.source;
		
		long time=System.currentTimeMillis();
		VectorS tgtTexts=null;
		
		try{
//					((translator instanceof WebTranslator)&&tgtTexts.size()<1)){
			tgtTexts= translator.translate(
				txt,translation.srcLang,translation.trgLang);
		}
		catch(Exception e){
			e.printStackTrace();
		}
		
		//TODO: merge the result here?
		for(String targetText:tgtTexts){
			translation.addTarget(targetText,translator);
		}
		time=System.currentTimeMillis()-time;
		message.append("\t\""+txt+"\" received "+tgtTexts.size()+" translation(s) from "+translator+" in "+((double)time/1000)+" sec.");
		System.out.println(message.toString());
		return;
	}

}
