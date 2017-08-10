package edu.cmu.lti.nlp.mt.translator;

import edu.cmu.lti.algorithm.container.MapSMapSX;
import edu.cmu.lti.algorithm.container.VectorS;

/**
 * multiple dict files
 * @author nlao
 *
 */
public abstract class DictMFTranslator  extends DictTranslator{
	//public DictMFTranslator(){}
	//public DictMFTranslator(ParamMT p){		super(p);	}
	public boolean isCapable(	String srcLang,String trgLang){
		return mm_files.contains(srcLang,trgLang);
	}
	
	public MapSMapSX<VectorS> mm_files
	= new MapSMapSX<VectorS>(VectorS.class);
	
	public void init() {
		VectorS files = mm_files.get(src).get(trg);
		for(String fn: files){
			init(dictDir+fn);//, "GBK");
		}
	}

}
