package edu.cmu.lti.util.crawl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;

/**
 * single thread extractor
 * INPUT FILES:
 * 	 a set of files matching filePattern
 *   each is a text file of P<=M entries of the following form:
 *    	url of the paper
 *      html of the url
 * 
 * What it does:
 * 			for each pair of (url,html) it calls
 * 				processADoc(url,	html)
 * OUTPUT FILES:
 *   XXX.ex: extracted information
 *    	format is determined by the derived class
 *      
 * Log messages go to stdout.
 * @author nlao
 *
 */
public abstract class  ASimpleExtractor {
	//URL=[http://www.tripadvisor.com]
	public int nEscape=0;
	public boolean bOutputDocs=false;
	public Param p = null;// new Param();
	public ASimpleExtractor(Class c) {
		p = new Param(c);
		FFile.mkdirs("failed");
	}
	
	Pattern paURL = Pattern.compile("URL=\\[(.+)\\]");
	private void processAnOutputFile(String fn){
		System.out.println("\nprocessAOutput="+fn);
		//bwOutput=FFile.bufferedWriter(fn+".ex");
		
		BufferedReader br= FFile.newReader(fn);
		StringBuffer sb= new StringBuffer();
		String line=null;		
		String url=null;
		String lastUrl=null;
		while ((line=FFile.readLine(br))!=null){
			if (line.length()<=1)
				continue;

			if (line.startsWith("URL=[")){
				
				for (int k=0;k<nEscape; ++k)
					FFile.readLine(br);
				
				lastUrl=url;
				url= line.substring(5,line.length()-1);
				if (lastUrl!=null)
					processADocWrapper(lastUrl, sb.toString());
				sb.setLength(0);
			}
			else
				sb.append(line).append("\n");
			
		}
		if (url!=null){
			processADocWrapper(url, sb.toString());
			sb.setLength(0);
		}
		FFile.close(br);
		//FFile.close(bwOutput);
		return;	
	}
	
	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public int nThread=5;
		public int nReportInterval=20;
		public int nGCInterval=100;
		public boolean bIncremental=false;

		public Param(Class c) {
			super(c);
			nThread = getInt("nThread", 10);
			nReportInterval = getInt("nReportInterval", 50);
			nGCInterval= getInt("nGCInterval", 200);
			bIncremental = getBoolean("bIncremental", false);
		}
	}

	
	protected static BufferedWriter bwOutput=null;
	public SetS msParsed=new SetS();
	public BufferedWriter bwParsed;
	
	public int nPaper=0;
	public int nParsed=0;

	
	public void processOutputs(String fn){
		processOutputs(fn,fn+".ex");
	}
	
	public String currentFile;
	public void processOutputs(String filePattern, String fnOut){
		nPaper=0;	 nParsed=0;
		
		bwOutput=	FFile.newWriter(fnOut,p.bIncremental);
		
		for (String fn:FFile.getFileNames(".",filePattern)){
			currentFile=fn;
			if (p.bIncremental){
				if (FFile.exist(fn+".exed"))
					msParsed.addIterable(FFile.enuLines(fn+".exed"));
				bwParsed= FFile.newWriterCA(fn+".exed");
			}
			processAnOutputFile(fn);
		}
		FFile.close(bwOutput);
		FFile.close(bwParsed);		
		System.out.println("\n"+nParsed+"/"+nPaper +" papers parsed " );//+nRef +"references");
		return;
	}

	private boolean processADocWrapper(String ID,	String txt){
		++nPaper;

		if (p.bIncremental){//cached
			if (msParsed.contains(ID)) 
				return true;
		}		

		if (bOutputDocs){
			FFile.mkdirs("docs");
			FFile.saveString("docs/"+ID, txt);
		}
		
		//html= FString.convertToUTF8(html);
		//html=html.replaceAll("[^\\p{ASCII}]", "");
		
		if (!processADoc(ID,txt)){	//failed
			System.out.print('e');
			FFile.saveString("failed/"+ID, txt);
			return false;
		}

		
		++nParsed;
		
		if (p.bIncremental){// successful
			msParsed.add(ID);
			FFile.writeln(bwParsed, ID);
			FFile.flush(bwParsed);
		}
		return true;
	}

	public void processOutputs(){
		processOutputs("output.\\d+");
	}
	public abstract boolean processADoc(String ID,	String txt);

}
