package edu.cmu.lti.nlp.mt.translator;

import java.io.BufferedReader;

import edu.cmu.lti.algorithm.container.MapSSetS;
import edu.cmu.lti.algorithm.container.MapSMapSX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;

public abstract class DictTranslator extends Translator{
	public String dictDir= Param.path_proj+ "/data/tm/dictionaries/"	;

	public DictTranslator(){
		super(_all_term);
	}
	//public DictTranslator(ParamMT p){	super(p);	}
	
	//public TMapMapSSX<MapMapSSD> mm_dict	= new TMapMapSSX<MapMapSSD>(MapSetSS.class);
	//public abstract boolean isCapable(	String srcLang,String trgLang);
	
	public MapSMapSX<MapSSetS> mm_dict
	= new MapSMapSX<MapSSetS>(MapSSetS.class);

	/*	public VectorS translate(String source,String srcLang,String trgLang){
		String src = srcLang.toString();
		String trg = trgLang.toString();
		MapMapSSD  dict= mm_dict.getC(src).getC(trg);
		source=source.toLowerCase();
		return dict.get(source).toVector();//.clone();
	}	
		*/
	public VectorS translate(String source,String src,String trg){
		this.src = src;
		this.trg = trg;
		dict= mm_dict.getC(src).getC(trg);	
		return dict.get(source.toLowerCase()).toVector();//.clone();
	}	
	
	public abstract boolean parseLine(String line );//{}
	protected MapSSetS dict;	
	protected String src;	
	protected String trg;
	public void init(String src,String trg) {
		this.src = src;
		this.trg = trg;
		dict= mm_dict.getC(src).getC(trg);	
			init();
	}	
	public void init(String fn){
		try{
			BufferedReader reader=FFile.newReader(fn);
			
			String line;
			for(int i=0;(line=reader.readLine())!=null;++i){
				if(line.startsWith("#"))		continue;
	
				if (!parseLine(line)){
					System.err.println("Bad data in dictionary: "	+fn);
					System.err.println("line"+i+"="+line);
					System.err.println("Continuing...");
				}
			}
			reader.close();		
		}
		catch(Exception e){
			e.printStackTrace();
		}

	}	
	public void init(){}
	
}


