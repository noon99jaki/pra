/**
 * @author nlao
 * so-called ".fn" (FrameNet) format  is a file format used by Dan Gildea 
 * for his studies on FrameNet. ASSERT's author Sameer Pradhan followed 
 * the same file format to conduct his Sementic Role Labeling research
 *  on PropBank corpus. :
L1:DOMAIN/FRAME/open.v.ar:<S TPOS="00000000001000"> The/DT NYSE/NNP is/AUX prepared/JJ to/TO <C TARGET="y"> open/VB </C> tomorrow/NN on/IN generator/NN power/NN if/IN necessary/JJ the/DT statement/NN said/VBD </S>
L2:(S1 (S (S (NP (DT The) (NNP NYSE)) (VP (AUX is) (ADJP (JJ prepared) (S (VP (TO to) (VP (VB open) (NP (NN tomorrow)) (PP (IN on) (NP (NN generator) (NN power))) (SBAR (IN if) (FRAG (ADJP (JJ necessary)))))))))) (NP (DT the) (NN statement)) (VP (VBD said))))
L3:
 */
package edu.cmu.lti.nlp.parsing.srl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.structure.MyStack;
import edu.cmu.lti.nlp.parsing.FTree.FindNodeBySpan;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;

public class FrameNet extends SRLParser{
	//public ParseTree tree = new ParseTree(); 
	//<C TARGET="y"> open/VB </C> 

	public class Param{
		public String lang;
		public String s_dbg;
		public int dbg = 0;
		public final int cpb = 0;
		public final int fn = 1;
		public int fn_format = 1;
		
		public void parse(){
			lang = System.getProperty("lang");
			if (lang == null) lang = "en";
			s_dbg = System.getProperty("dbg");
			if (s_dbg != null) dbg = Integer.parseInt(s_dbg);	
		}
		public Param(){
			parse();
		}
	}
	public static Param p = null;
	public FrameNet(){
		if (p==null) 
			p = new Param();
	}	
	
	public ArrayList<Term> v_term = new ArrayList<Term>();
	public class Term {
		public String text = null;
		public String srl = null;

		public Term(String text, String srl) {
			this.text = text;
			this.srl = srl;
		}
	}
	
	final Pattern paFN2 = Pattern.compile("^(.*)=\"(.*)\">$");
	protected String readFNTag(String txt) {
		Matcher matcher = paFN2.matcher(txt);
		if (!matcher.matches()) {
			System.out.println("parse failed " + txt + "\n");
			throw new UnknownError();
			//return false;    	
			//System.exit(-1);			
		}
		return matcher.group(2);
	}

	protected boolean readFNTags(String line) {
		v_term.clear();
		final Pattern paFN1 = Pattern.compile(
				"DOMAIN/FRAME/(.*?).v.ar:<S TPOS=\"(.*?)\"> (.*)$");
		//"DOMAIN(.*)");
		//"DOMAIN/FRAME/(.*?).v.ar:<S TPOS=\"(.*?)\"> (.*) </S>$");
		Matcher matcher = paFN1.matcher(line);
		if (!matcher.matches()) {
			System.out.println("parse failed:\n" + line + "\n");
			return false;
			//System.exit(-1);			
		}
		verb = matcher.group(1);
		id_sent = matcher.group(2);
		String content = matcher.group(3);
		String[] spl = content.split(" ");
		int nTag=0;
		String srl = null;//"none";
		MyStack<String> vsrl = new MyStack<String>();
		int ib = 0, ie = 0;
		FindNodeBySpan.setTree(tree);
		for (int i = 0; i < spl.length; ++i) {
			String s = spl[i];
			//if (s.isEmpty()) continue;
			// TODO: fixed for jdk 5.0
			if (s==null || s.equals("")) continue;
			//final String s001 = new String("</C>");
			if (s.equals("</C>")) {
				ie = v_term.size();
				srl = vsrl.pop();
				int id= FindNodeBySpan.findNodeBySpanSoft(ib,ie);
				if (id == -1) {
					parseErr = 2;
					return false;
				}
				Node n = tree.getNode(id); 
				if (srl.equals(CTag.y)) 
					n.put(CTag.target, CTag.y);
				else
					n.put(CTag.srl, srl);
				continue;
			}
			//final String s002 = new String("<C");
			if (s.equals("<C")) {
				++i;
				//				if (!srl.equals("none"))System.out.println("nested srl tag");
				vsrl.push(readFNTag(spl[i]));
				ib = v_term.size();
				++ nTag;
				continue;
			}
			int id = v_term.size();
			//tree.v_term.get(id).srl = srl;
			s = s.split("/")[0];
			if (!s.equals(tree.vWord.get(id))) {
				System.out.println("unmatched term " + s + "<>" + tree.vWord.get(id));
				return false;
			}
			v_term.add(new Term(s, vsrl.peek()));
		}
		if (nTag<2) {
			System.out.println("nTag<2");
			parseErr = 1;
			return false;
		}
		return true;
	}

	/**
	 * read .fn format
	 * @param reader
	 * @return boolean
	 * @throws IOException
	 * @throws Exception
	 */
	public boolean readPBLine(BufferedReader reader) throws IOException ,Exception{
		tree = new TreeSyntax();
		//		t = new TreeSRL();

		//for(String nextLine;(nextLine=reader.readLine())!=null;writer.println(nextLine));
		parseErr = 0;
		line1 = reader.readLine();
		if (line1 == null) return false;

		line2 = reader.readLine();
		if (line2 == null) return false;

		reader.readLine();
		if (!tree.parseBracketString(line2)) 	parseErr = 2;

		if (!readFNTags(line1));// 	System.out.println("ReadFNTags failed");
		return true;
	}
		//static Logger logger = null;
	//private static final Logger log = Logger.getLogger(FrameNet.class);
	//private static final Logger log = Logger.getLogger(IXSubmission.class);



/*
	public void printErr() {//FrameNet fn){
		if (parseErr >1){
			System.out.println(line1);
			System.out.println(line2 );
			System.out.println("sent id="+id_sent);
			System.out.println(tree.toString());
			return;
		}
	}
*/

}
