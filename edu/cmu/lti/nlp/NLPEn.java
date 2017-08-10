/**
 * 
 */
package edu.cmu.lti.nlp;

import edu.cmu.lti.nlp.Interfaces.ISynxParseSent;
import edu.cmu.lti.nlp.english.CharniakParserService;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;

/**
 * @author nlao
 *
 */
public class NLPEn extends NLP{
	private static  NLPEn instance =null;
	public static NLPEn getInstance() {
		if (instance==null) 	 instance = new NLPEn();			
		return instance;
	}
	public static class Param extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public Param() {
			super(NLPEn.class);
			parse();
		}
		public void parse(){			
		}
	}	
	public Param p=new Param();
	public NLPEn(){
		regexBreakSent="[.?!]";
	}
	
	public VectorToken  segWord(String sent){	
		//VectorToken(vs)
		VectorToken vt = new VectorToken();
		String vs[]= sent.split("[ '\\!\\?]");
		for (String s: vs){
			vt.add(new Token(s));
		}
		return vt;
	}
	ISynxParseSent parser = CharniakParserService.getInstance();
	public TreeSyntax synxParseSent(String sent) { 
		TreeSyntax pt= parser.synxParseSent(sent);
		this.vt = pt.getTerminals();
		return pt;
	}
}
