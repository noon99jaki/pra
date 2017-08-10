/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.MapSMapSX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.mt.util.NetUtil;
import edu.cmu.lti.util.file.FFile;

public abstract class WebTranslator extends Translator{
	//public WebTranslator(){}

	//public WebTranslator(ParamMT p){	super(p);	}
	
	//public abstract String getURL(String source,String srcLang,String trgLang);
	//public abstract String getEncoding(String trgLang);

	//public String tgtEncoding ="UTF-8";
	//targetLang-->encoding
	MapSS ms_tgtEncoding = new MapSS();
	public int MAX_SOURCE_CHAR=10000;

	public boolean isCapable(	String srcLang,String trgLang){
		return mms.contains(srcLang,trgLang);
	}

	public MapSMapSX<String> mms
		= new MapSMapSX<String>(String.class);
	public String urlBase;
	public String urlFin;
	
	public WebTranslator(String urlBase, String urlFin,Pattern pTarget){
//			, String typeCap){
		super(_all);
		this.urlBase = urlBase;
		this.urlFin = urlFin;
		this.pTarget=	pTarget;	
	}	
	public String getURL(String txt,String srcLang,String trgLang){
		StringBuilder sb=new StringBuilder();
		sb.append(urlBase)
		.append(mms.get(srcLang).get(trgLang))
		.append(urlFin)		
		.append(txt);
		return sb.toString();
	}
	
	Pattern pTarget;
	public VectorS getTargets(String page,String srcLang,String trgLang){
    Matcher matcher=pTarget.matcher(page);
    VectorS targets=new VectorS();
    if(matcher.find()){
    	targets.add(matcher.group(1).trim());
    }
    else{
    	String cn = this.getClass().getSimpleName() ;
    	FFile.saveString(cn	+ ".html",page);
    	System.err.println(cn	
    			+ "parse page failed! with pattern "+ pTarget.toString());
    	System.err.println("page stored in " + cn	+ ".html");
    }
    return targets;
	}	
	
	public VectorS translate(String txt,String srcLang,String trgLang){
		try{
			txt=URLEncoder.encode(txt,"UTF-8");
		}
		catch(Exception e){
			e.printStackTrace();
		}
		if(txt.length()>MAX_SOURCE_CHAR)
			return new VectorS();
		
		String url=getURL(txt,srcLang,trgLang);
		System.out.println("\t"+url);
		String ec = ms_tgtEncoding.containsKey(trgLang)?
				ms_tgtEncoding.get(trgLang):"UTF-8";
				
		String page=NetUtil.getPage(url,ec);
		if(page==null){
			System.err.println("Error accessing "+url);
			return null;
		}
		return getTargets(page,srcLang,trgLang);
	}

}
