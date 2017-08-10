package edu.cmu.lti.nlp.parsing.srl;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Tag;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;

/**
 * a propbank parser
 * @author nlao
 *
 */
public class PropBank extends SRLParser{
	//6:0*7:0*21:1-ARG0
	//public static Pattern paTag = Pattern.compile("^(?:.**)$");
	
	public class Param extends edu.cmu.lti.util.run.Param{
		public String lang;
		public String PBFile;
		public String folderBracket1Line ="tmp";
		
		public void parse(){
			//lang = System.getProperty("lang");
			PBFile = getString("PBFile",null);
			folderBracket1Line = getString(
					"folderBracket1Line",null);			
		}
		public Param(Class c){
			super(c);//PropBank.class);//Static
			parse();
		}
	}
	public static  Param p =null;
	public PropBank(Class c){
		p = new Param(c);		
	}
	//public int i_target;//position of target on word sequence
	//public int in_target;//position of target on tree node


/*	public FeaturedGraph toFeaturedGraph(VectorS filter) {
		FeaturedGraph fg=super.toFeaturedGraph(filter);
		fg.
		return fg;
	}*/
	static class TagSrl extends Tag{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public TagSrl(){
			super(CTag.srl);
		}
		
		//propBank format
		public Tag parse(String s){
			type=CTag.srl;
			String[] vs = s.split ("\\*");//"\\p{Punct}");
			//24:0*25:0-ARG1,  0:1-ARGM-DIR
			final Pattern pa1 = Pattern.compile("^(\\d+)\\:(\\d+)-(.*)$");
			Matcher m1 = pa1.matcher(vs[vs.length-1]);
			if (!m1.matches()) {
				return null;
			}			
			i_node = Integer.parseInt(m1.group(1));
			depth =  Integer.parseInt(m1.group(2));
			txt = m1.group(3);
			
			/*if (txt.equals("rel")){
				txt="TARGET";
				return this;
			}*/
			final Pattern pa2 = Pattern.compile("^(ARG\\d)-(.*)$");
			Matcher m2 = pa2.matcher(txt);
			if (m2.matches()) {
				txt = m2.group(1);
			}			
			//	if (txt.startsWith(prefix))
			return this;
		}		
	}	
		
	
	protected boolean readCPBTags(PBMeta cpbline) {
		verb = cpbline.verb;
		id_sent = cpbline.file_name+"_"+  cpbline.i_sent;//String.valueOf(cpbline.i_sent);
		if (cpbline.v_tag.size()<2) {
			//System.out.println("nTag<2");
			parseErr = 1;
			return false;
		}
	
		if (!cpbline.verb.equals(tree.vWord.get(cpbline.i_pred))) {
			System.out.println("unmatched verb " +cpbline.verb
					+ "<>" + tree.vWord.get(cpbline.i_pred));
			parseErr = 2;
			return false;
		}		
		
		for (Tag tag: cpbline.v_tag){
			Node n= tree.setTagGentle(tag);
			
			if (n ==null){
				parseErr = 2;
				return true;
			}
			
			if (tag.txt.equals(CTag.rel)){
				//i_target = tag.i_node;
				if (n!=null)		
					n.put(CTag.target, CTag.y);
				
				//t.type = CTag._target;
				//t.txt =CTag._y;
			}
			

			//if (t.txt.equals(CTag._target)) 
		}
		//v_term.add(new Term(t.txt, t.txt));

		return true;
	}
	/**
	 * read Prop bank format
	 * @param br
	 * @return result
	 * @throws IOException
	 * @throws Exception
	 */

	public boolean readPBLine(BufferedReader br) {//throws IOException,Exception {
		tree = new TreeSyntax();
		//for(String nextLine;(nextLine=reader.readLine())!=null;writer.println(nextLine));
		parseErr = 0;
		line1 =FFile.readLine( br);
		if (line1 == null) return false;
		pbMeta.parse(line1);
		
		line2 = getTreeBankParseTree(pbMeta.file_name, pbMeta.i_sent);
		if (line2 == null) return false;	

		if (!tree.parseBracketString(line2)) {	
			parseErr = 2;return true;
		}
		if (!readCPBTags(pbMeta))return true; 
		//	System.out.println("ReadFNTags failed");
		return true;
	}	
	
	public void preprocess4NLSA(){
		//TODO: In out setting each sentence need only be parsed once
		//not like CAssert setting		

		 
	}
	
	private String file_ctb="";
	private VectorX<String> vs_ctb_cache=null;//cache sentences of a docuemnt 
	public String getTreeBankParseTree(
			String file_fid, int i_sent){//throws IOException {
		if (!file_ctb.equals(file_fid)){
			file_ctb=file_fid;
			vs_ctb_cache = 	FFile.loadLines(p.folderBracket1Line+"/"+file_ctb+".8");			
		}
		return vs_ctb_cache.get(i_sent);
	}	
}
