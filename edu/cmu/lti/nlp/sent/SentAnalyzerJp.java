package edu.cmu.lti.nlp.sent;

import edu.cmu.lti.nlp.CLang;

public class SentAnalyzerJp extends SentAnalyzer{
	private static  SentAnalyzerJp instance =null;
	public static SentAnalyzerJp getInstance() {
		if (instance==null) 	 instance = new SentAnalyzerJp();			
		return instance;
	}
	
	public SentAnalyzerJp(){
		super(CLang.ja_JP);
		m_QWord.addAll(new String[]{"谁","何","什么","哪些"});
		//m_questionDet.addAll(new String[]{"何","什么","哪些"});
		//m_questionPron.addAll(new String[]{"谁","什么","哪些"});
		
	}	
}
