package edu.cmu.pra.data;

import org.w3c.dom.Element;

import edu.cmu.lti.util.crawl.ASimpleExtractor;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.xml.FDom;

public class PMExtractor  extends ASimpleExtractor{
	static PMAbsXmlExtractor ae= new PMAbsXmlExtractor();
	public PMExtractor(){
		super(PMExtractor.class);
		nEscape=2;
	}

	public boolean processADoc(String pmid,	String txt)	{


		//html=html.replaceAll("&lt;","<");
		//html=html.replaceAll("&gt;",">");
		
		Element e= FDom.loadTxt(txt);
		//FXml.print(e);
		//Element e= FXml.loadFile("samples/10922406.xml");
		if (e==null){ 
			++nError;
			return false;		
		}
		ae.extract(e);		
		FFile.write(bwOutput	,ae.abs.print()+"\n");
		return true;
	}
	int nError=0;
	
	/** 
	 * INPUT FILES:
	 *   pmids.pm.crawl: text file of P<=M entries of the following form:
	 *    	url of the paper
	 *      html of the url
	 * 
	 * OUTPUT FILES:
	 *   pmids.pm.crawl.ex: text file of paper abstract	each line is 
			0	pmid
			1	year
			2	journal
			3	email
			4	affiliation
			5	Authors
			6	Chemicals
			7	MHDescriptorM
			8	MHQualifierM
			9	MHDescriptor
			10	MHQualifier
			11	title
			12	abstract
	 * Log messages go to stdout.
	 * 
	 * 
	 */
	public static void main(String args[]) {
		if (args.length!=1)
			FSystem.die("need crawl file");
		PMExtractor extractor = new PMExtractor();
		extractor.processOutputs(args[0]);//"pmids.pmc.crawl");
	}
}
