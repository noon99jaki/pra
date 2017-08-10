package edu.cmu.lti.nlp.parsing.srl;

import java.io.BufferedReader;
import java.io.IOException;

import edu.cmu.lti.nlp.parsing.tree.TreeParse;


public abstract class SRLParser {
	public String id_sent = null;
	public String verb = null;

	public static int parseErr = 0;
	public static String line1, line2;

	//public TreeSRL t;// = new TreeSRL();
	public TreeParse tree;// = new TreeSRL();
	public PBMeta pbMeta=new PBMeta();


	public abstract boolean readPBLine(BufferedReader reader) throws IOException,Exception ;
/*			if (parseErr > 0) {
				printErr();
				continue;
			}*/
	public void printErr(){
		System.out.println(SRLParser.line1);
		System.out.println(SRLParser.line2);
		//if (parseErr<2)	System.err.println(tree.toString(1));
	}
		
}
