package edu.cmu.lti.nlp.sent;

import edu.cmu.lti.nlp.CLang;
import edu.cmu.lti.nlp.NLP;

public class SentAnalyzerEn extends SentAnalyzer{
	private static  SentAnalyzerEn instance =null;
	public static SentAnalyzerEn getInstance() {
		if (instance==null) 	 instance = new SentAnalyzerEn();			
		return instance;
	}
	public SentAnalyzerEn(){
		super(CLang.en_US);
		m_QWord.addAll(new String[]{"谁","何","什么","哪些"});
		//m_questionDet.addAll(new String[]{"何","什么","哪些"});
		//m_questionPron.addAll(new String[]{"谁","什么","哪些"});
		
	}	
}
