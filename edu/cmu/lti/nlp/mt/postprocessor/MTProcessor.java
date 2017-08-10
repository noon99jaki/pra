package edu.cmu.lti.nlp.mt.postprocessor;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.nlp.mt.MTResult;
import edu.cmu.lti.util.text.FString;

public class MTProcessor {
	public class Param 	extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public SetS langCap;
		public SetS typeCap;
		private String dictFile;
		public String dictPath;
		public String dictDir;
		public String resourceDir;
		
		public Param(Class c) {
			super(c);
			parse();
		}
		public boolean isCapable(
				String srcLang,String trgLang,String type){
			return isCapable(srcLang,trgLang)&&typeCap.contains(type);
		}
		public boolean isCapable(	String srcLang,String trgLang){
			String langPair=srcLang+"-"+trgLang;
			return langCap.contains(langPair);
		}	
		//Param.proj_path
		//
		public void parse(){			
			langCap=(SetS) FString.tokenize(getString("langCap")).toSet();
			typeCap=(SetS) FString.tokenize(getString("typeCap")).toSet();
			
			dictFile=getString("dictFile","");
			dictPath= path_proj+"/data/tm/dictionaries/"	+ dictFile;

			dictDir=getString("dictDir","");
			resourceDir=getString("resourceDir","");
		}	
	}
	public Param p;
//	public void init(){	 p=new ParamMT(getClass());	}
//	public MTProcessor(Class c){	p=new ParamMT(c);	}
//	public MTProcessor(ParamMT p){	this.p=p;	}	
	public MTProcessor(){		
		p=new Param(this.getClass());	
	}	
	public boolean isCapable(MTResult t){			
		return p.isCapable(t.srcLang, t.trgLang, t.type);
	}
		
}
