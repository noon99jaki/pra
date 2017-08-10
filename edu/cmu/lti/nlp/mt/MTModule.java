package edu.cmu.lti.nlp.mt;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.rmi.RemoteException;
import java.util.Map;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.CLang;
import edu.cmu.lti.nlp.mt.MTResult.Target;
import edu.cmu.lti.nlp.mt.MTService.IClient;
import edu.cmu.lti.nlp.mt.MTService.IServer;
import edu.cmu.lti.nlp.mt.postprocessor.Postprocessor;
import edu.cmu.lti.nlp.mt.scorer.Scorer;
import edu.cmu.lti.nlp.mt.translator.Translator;
import edu.cmu.lti.nlp.mt.translator.TranslatorJob;
import edu.cmu.lti.util.run.Cacher;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FClass;


public class MTModule implements IServer,  IClient{//
	public static MTModule getInstance(){
		return getInstance(p.src_lang, p.lang);
	}
	public static MapSX<MTModule> m_instance
		=new MapSX<MTModule>(MTModule.class);
	
	public static MTModule getInstance( String lang1, String lang2 ){
		String code= lang1+"-"+lang2;
		if ( m_instance.containsKey(code)) 
			return m_instance.get(code);
		
		try{
			MTModule x=new MTModule(lang1,lang2);
			m_instance.put(code, x);
			return x;
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return null;		
	}	

	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String lang;
		public String src_lang;
		public boolean caching;
		//public String transCacheDir;
		public String translators;
		public String scorer;
		public String postprocessors;
		
		public Param() {
			super(MTModule.class);
			parse();
		}
		@Override
		public void parse(){	
			lang=getString("lang","zh_CN");
			src_lang=getString("src_lang","en_US");		
			caching=Boolean.parseBoolean(getString("caching", "true"));
			//transCacheDir= getString("transCacheDir");			
			
			postprocessors=getString("postprocessors");
			translators=getString("translators");
			scorer=getString("scorer");
		}
		
		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append( "src_lang = "+src_lang+"\n" );
			sb.append( "lang = "+lang+"\n" );
			sb.append( "caching = "+caching+"\n" );
			sb.append( "postprocessors = "+postprocessors+"\n" );
			sb.append( "translators = "+translators+"\n" );
			sb.append( "scorer = "+scorer+"\n" );
			return sb.toString();
		}
	}	
	//public Param p=new Param();
	public static Param p;//=new Param();
	
	public static Cacher cacher;
	public static VectorX<Translator> translators
		= new VectorX<Translator>(Translator.class);
	public static VectorX<Postprocessor> postprocessors
		= new VectorX<Postprocessor>(Postprocessor.class);
	public static Scorer scorer;

	static{
		p=new Param();
		if (p.caching) 
			cacher = new Cacher(p.path_cache);//p.transCacheDir);
		
		try{
			translators.initClasses(Translator.class, p.translators);
			postprocessors.initClasses(Postprocessor.class, p.postprocessors);
			if (p.scorer != null) 
				scorer = (Scorer) FClass.newInstance(Scorer.class, p.scorer);
		}
		catch (Exception e){
			e.printStackTrace();
		}
	}
	
	String srcLang;
	String trgLang;
	/*instead of using function paramter, 
	make this preference tranparent to the users*/
	String[] prefered_tor=null;
	MapSD mTorWeights = new MapSD();
	protected MTModule(String srcLang, String trgLang)throws Exception{
		this.srcLang = srcLang;
		this.trgLang = trgLang;
		//TODO: move then into conf files
		if (srcLang.equals(CLang.en_US)){
			
			if (trgLang.equals(CLang.ja_JP)){
				prefered_tor=new String[]{"WebMTAmikai","WebMTGoogle"};
			}
			
			if (trgLang.equals(CLang.zh_CN)){
				mTorWeights.put("WebMTGoogle", 0.5);
				mTorWeights.put("WebMTWorldLingo", 0.3);
				mTorWeights.put("WebMTAmikai", 0.1);
			}
		}
		init();
	}
	
	//translators that are capable for this language pair
	public VectorX<Translator> v_tor
	= new VectorX<Translator>(Translator.class);

	public void init() {
		for (Translator t:translators){
			System.out.print("Init "+t.getClass().getSimpleName()+" ... "); 
			System.out.flush();
			
			if (t.isCapable(srcLang, trgLang)){
				t.init(srcLang, trgLang);
				v_tor.add(t);
			}
			System.out.println("done.");
		}
		//for (Postprocessor t:postprocessors)	t.init();
		return;
	}
	public MapSD translateKeyTermRMI( String term )throws RemoteException{
		return translateKeyTerm(term);
	}
	
	public MapSD translateKeyTerm( String term ){
		MTResult t = translate(term, CTxtType.TERM);
		return t.toMapSD();
	}
	public String translateSentenceRMI( String sent )throws RemoteException{
		return translateSentence(sent);
	}
	public String translateSentence( String sent ){
		MTResult t = translate(sent, CTxtType.SENTENCE);
		if (prefered_tor !=null){
			for (String tor: prefered_tor){
				String txt = t.m_tor_txt.get(tor);
				if (txt==null) continue;
				if (txt.length()==0) continue;
				return txt;
			}
		}
		return t.toMapSD().idxMax();
	}	
	public MapSD translateSentenceRawRMI( String sent )throws RemoteException{
		return translateSentenceRaw(sent);
	}
	public MapSD translateSentenceRaw( String sent ){
		MTResult t = translate(sent, CTxtType.SENTENCE);
		return t.toMapSD();
	}

	public MTResult translateRMI( String text, String type )throws RemoteException{
		return translate(text,type);
	}
	public MTResult translateRaw( String text, String type ){
		MTResult t = new MTResult(text,type, srcLang,trgLang);
		if (p.caching) {
			Object o=  cacher.loadObj(t);
			if (o!=null) return (MTResult) o;
		}

		ThreadGroup jobs=new ThreadGroup("TranslatorJobs");
		
		StopWatch sw = new StopWatch();
		for(Translator tor:v_tor){
			new Thread(jobs,new TranslatorJob(tor,t)).start();
			
		}
		while(jobs.activeCount()>0){
			try{
				Thread.sleep(100);
			}
			catch(Exception e){
				e.printStackTrace();
			}
		}
		
		System.out.println(
				"Received "+t.targets.size()+" translation(s) for "
				+text+" from all translators in "
				+sw.getSecI()+" sec.");
		
		
		System.out.println("Postprocessing translations...");
		for(Postprocessor postprocessor:postprocessors){
			postprocessor.process(t);
		}
		if(p.caching) cacher.saveObj(t);
		return t;
	}
	
	//failed translation has English char
	protected static final Pattern paWord 
		//= Pattern.compile("\\w");
		= Pattern.compile("[a-zA-Z]");


	/** to falicitate the tuning of Tor weights
	 * pull the scoring part from cache 
	 */
	public MTResult translate( String text, String type ){
		//if(srcLang.equals(FLocale.en_US)){	source=source.toLowerCase();	}
		MTResult t =translateRaw(text, type);
		//if(scorer!=null){
		System.out.println("Scoring translations...");
		//scorer.score(t);
		for (Map.Entry<String, Target> e: t.targets.entrySet()){
			String txt = e.getKey();
			Target x = e.getValue();
			x.score=x.translators.size() 
				+ mTorWeights.sub(x.translators.getVS()).sum();
			if (!trgLang.equals(CLang.en_US))
				if (paWord.matcher(txt).find())
//				if (txt.matches("\\w"))
					x.score -=10.0;//punish failed translations
		}
		//}
		System.out.println("Translation:");
		System.out.println(t);
		return t;
	}

	public VectorX<MTResult> translate (	VectorX<String> texts, String type){
		VectorX<MTResult> vt=new VectorX<MTResult>(MTResult.class);
		for (String txt:texts) 
			vt.add(translate(txt,type));
		return vt;
	}
	
	public static void printUsage(){
		System.out.println("Usage:");
		System.out.println(" java TranslationModule TYPE_NUM SRC_LANG TRG_LANG");
		System.out.println("TYPE_NUM:");
		System.out.println(" 1 - Translate a single term");
		System.out.println(" 2 - Translate a set of related terms, separated by \"|\"");
		System.out.println("SRC_LANG, TRG_LANG:");
		System.out.println(" en_US, zh_CN, zh_TW, etc.");
	}
	public static void main(String[] args)throws Exception{

		if(args.length!=3){
			printUsage();
			return;
		}

		int typeNum=Integer.parseInt(args[0]);

		if(typeNum<1||typeNum>2){
			printUsage();
			return;
		}

		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in,"UTF-8"));
		PrintWriter writer=new PrintWriter(new OutputStreamWriter(System.out,"UTF-8"),true);

		writer.println("Initializing Translation Module...");
		MTModule tm= MTModule.getInstance(args[1],args[2]);
		writer.println("Translation Module initialization complete.");
		writer.println();

		writer.print("Enter source text: ");
		writer.flush();

		for(String nextLine;(nextLine=reader.readLine())!=null;){

			writer.println();

			if(typeNum==1){
				MTResult translation
					=tm.translate(nextLine.trim(),CTxtType.UNKNOWN);
				writer.println(translation);
			}
			else if(typeNum==2){
				VectorS sources	=new VectorS(nextLine.split("\\|"));

				VectorX<MTResult> translations
					=tm.translate(sources, CTxtType.UNKNOWN);
				
				for(MTResult translation:translations){
					writer.println(translation);
				}
			}

			writer.print("Enter source text: ");
			writer.flush();

		}

	}



}
