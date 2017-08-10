package edu.cmu.lti.nlp.mt.translator;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.util.text.FString;
/**
 * single dict file
 * @author nlao
 *
 */
public abstract class DictSFTranslator extends DictTranslator{
	
	public SetS langCap;
	public void setLangCap(String langCap){
		this.langCap=(SetS)  FString.tokenize(langCap).toSet();		
	}
	public boolean isCapable(	String srcLang,String trgLang){
		return langCap.contains(srcLang)&&langCap.contains(trgLang);
	}
		
	public String dictFile;
	public DictSFTranslator(String dictFile, String langCap){
		this.dictFile = dictFile;
		setLangCap(langCap);
	}
//	
	
	MapSS ms = new MapSS();
	public void addItem(){
		dict.add(ms.get(src),ms.get(trg));
	}
	public void init(){
		//init(super.p.dictPath);
		init(dictDir+dictFile);
	}
	
}
