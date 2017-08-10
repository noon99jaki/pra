/**
 * 
 */
package edu.cmu.lti.nlp.english;

import java.io.BufferedReader;
import java.rmi.RemoteException;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.Interfaces.ISynxParseSent;
import edu.cmu.lti.nlp.english.CharniakParserService.IServer;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.MyProcess;

/**
 * @author nlao
 *
 */
public class CharniakParser implements ISynxParseSent, IServer {
	private static CharniakParser instance = null;

	public static CharniakParser getInstance() {
		if (instance == null) instance = new CharniakParser();
		return instance;
	}

	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String charniakParser;
		public String charniakDir;
		
		public Param(){//Class c) {
			super(CharniakParser.class);
			parse();
		}
		public enum Task{
			train, test, service
		}
		public void parse() {
			//task =Task.valueOf(getString("task", Task.service.name()));
			charniakParser = getString("charniakParser");
			charniakDir = getString("charniakDir");
			//UNARY_LIMIT = getInt("UNARY_LIMIT",4);
		}
	}
	public Param p = new Param();
	
	private static boolean EN_initialized = false;

	private MyProcess proc= new MyProcess();
	
	protected CharniakParser(){//Class c) {
		//p = new Param(c);
		startModel();
	}
	
	public void startModel(){
		proc.dir = p.charniakDir;
		proc.start(p.charniakParser);//, "-p", "-m", p.model);
		System.out.println("charniak parser started");
		System.out.println("initializing ...");
	}



	/**
	 * For English, use Charniak parser to get the POS and parse tree of the
	 * input sentence
	 */
	public String[] parse(VectorS v_word){
		if (v_word.size() > 400) {
			System.err.println(
				"input sentence has more than 400 words, which is the MAXIMUM length supported for English parser");
			return null;
		}		
		return parse(v_word.join(" "));
	}
	public String[] parse(String sent){
		String parseStr = null;
		String posStr = null;
		synchronized (CharniakParser.class) {
			proc.write( sent+ "\n");
			parseStr = proc.readLine();
			posStr = proc.readLine();
		}		
		if (parseStr == null || posStr == null) {
			System.err.println("Charniak parser outputed null");
			return null;
		}
		//throw new Exception("Failed to use Charniak Parser process", e);
		//remove the extra root symbol S1 as in (S1 ...)
		return new String[] { parseStr, posStr };
	}

	/**
	 * @param terms in a sentence that needs to be POS tagged and generate parse
	 * tree The terms will be assinged POS tags NOTE: non-alphabetic symbols
	 * should always be given in the input as a separate term
	 * @return the syntactic parse tree produced by Charniak parser, in case of
	 * error, null is returned
	 */
	public TreeSyntax synxParseSent(String sentence) {
		String vs[] =parse(sentence);
		TreeSyntax pt = new TreeSyntax();
		pt.parseBracketString(vs[0]);		
		System.out.println("CharniakParser:" + vs[0] + "\n");
/*
		String[] POSs = posStr.split(" ");
		if (POSs.length != terms.size()) {
			log.error("number of POSs returned by Charniak parser doesn't match number of given terms");
			return null;
		}
		for (int i = 0; i < terms.size(); i++)
			terms.get(i).setPOS(POSs[i]);*/
		return pt;
	}
	public TreeSyntax synxParseSentRMI(String sentence) throws RemoteException {
		return synxParseSent(sentence);
	}

	public void process(String fn){
		BufferedReader in = FFile.newReader(fn);
		String line;
		while ((line =FFile.readLine(in)) != null) {
			String[] output = parse(line);
			System.out.println(output[0]);
		}
		FFile.close(in);		
	}
	/**
	 * Parses sentences in a file that has one sentence per line.
	 * Parse trees are printed to System.out.
	 * 
	 * @param args contains name of the file
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		CharniakParser cp = CharniakParser.getInstance();
		cp.process(args[0]);
	}
}