/*
 * Frank Lin
 * 
 */

package edu.cmu.lti.nlp.mt;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.mt.translator.Translator;
import edu.cmu.lti.util.run.IGetCode;

public class MTResult implements Serializable, IGetCode{
	public String getCode(){
		StringBuilder b=new StringBuilder();
		b.append(srcLang).append("-")
		.append(trgLang).append("-")
		.append(type).append("-")
		.append(source);
		return b.toString();
	}		
	static final long serialVersionUID=1;
	
	//Set+comparable or Map?
	public static class Target implements Serializable, IGetDblByStr{
		
		static final long serialVersionUID=1;
		public Double getDouble(String name){
			if (name.equals("score")) return score;
			return null;
		}
		public String text;
		//public TVector<Translator> translators = 			new TVector<Translator>(Translator.class);
		public VectorS translators = new VectorS();
		public double score;
		public Target(){}
		
		public String toString(){
			StringBuilder b=new StringBuilder();
			b.append(score).append(" ").append(text)
			.append(" ").append(translators);
			return b.toString();
		}
	}
	public String source;//VectorS
	
	public String srcLang;
	public String trgLang;
	public String type;//term, sentence, etc..
	public MTResult(String source, String type
			,String srcLang,String trgLang){
		this.source = source;
		this.type = type;
		this.srcLang = srcLang;
		this.trgLang = trgLang;
	}
		
	/**translation->Target */
	public MapSX<Target> targets=new MapSX<Target>(Target.class);
	
	/**translator-> translation
	the translation from each translator*/
	public MapSS m_tor_txt=new MapSS();
	public synchronized void addTarget(String txt,Translator tor){
		Target t =targets.getC(txt);
		t.translators.add(tor.toString());
		m_tor_txt.put(tor.toString(), txt);
	}


	@Override
	public String toString(){
		StringBuffer b=new StringBuffer();
		b.append("Source: ").append(source).append("\n")
		.append("Targets:\n")
		.append(targets.ValuesToVector().join( "\t")).append("\n");
		return b.toString();
	}
	
	public MapSD toMapSD(){
		//return targets.getMDouble("score");
		MapSD m = new MapSD();
		for ( Target t : targets.values() ) {
			m.put(t.text, t.score);
		}
		return m;
	}
}
