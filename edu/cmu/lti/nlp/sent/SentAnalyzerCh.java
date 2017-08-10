/**
 * 
 */
package edu.cmu.lti.nlp.sent;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.CLang;
import edu.cmu.lti.nlp.NLP;
import edu.cmu.lti.nlp.parsing.tree.Token;


/**
 * @author nlao
 *
 */
public class SentAnalyzerCh extends SentAnalyzer{
	private static  SentAnalyzerCh instance =null;
	public static SentAnalyzerCh getInstance() {
		if (instance==null) 	 instance = new SentAnalyzerCh();			
		return instance;
	}
	
	public SentAnalyzerCh(){
		super(CLang.zh_CN);
		m_QWord.addAll("谁 何 什么 哪些");
		
		//m_questionDet.addAll(new String[]{"何","什么","哪些"});
		//m_questionPron.addAll(new String[]{"谁","什么","哪些"});
	}
	protected void tagSurfaceFeatures(){
	}	
	protected void tagShallowFeatures(){
		//VectorI vL = (VectorI) 
		VectorS vs = (VectorS) sa.v_token.getVS("text");
		VectorI vi = vs.getVI("length").cumu();
		for (int i=0;i<sa.v_token.size();++i){
			Token t = sa.v_token.get(i);
			if (!t.getPOS().equals("NR") )
				if (t.ms.get("NE")!=null)
					if (!t.ms.get("NE").equals("PERSON")) continue;
			
			SetS mF = new SetS();
			if (i<sa.v_token.size()-1){
				Token t1 = sa.v_token.get(i+1);
				//findPossesion
				if (t1.getText().equals("的"))
					mF.add(CTag.possession +":"+t.getText());							
				if (t1.getText().equals("是"))
					mF.add(CTag.be1 +":"+t.getText());							
			}
			if (i<sa.v_token.size()-2){
				Token t1 = sa.v_token.get(i+1);
				Token t2 = sa.v_token.get(i+2);
				//findPossesion
				if (t2.getText().equals("的"))
					mF.add(CTag.possession +":"+t.getText());							
				if (t1.getText().equals("，"))
					if (t2.getPOS().startsWith("N"))
						mF.add(CTag.apposition+":"+t.getText());							
			}
			if (i>0){
				Token t1 = sa.v_token.get(i-1);			
				if (t1.getText().equals("是"))
					mF.add(CTag.be2 +":"+t.getText());							
				//findApposition
				if (t1.getPOS().equals("NN"))
					mF.add(CTag.apposition+":"+t.getText());			
			}
			
			for (String f: mF)
				sa.tFeature.mi.put(f, vi.get(i));
			//sa.tFeature.m.add( vi.get(i));
		}
	}	
}
