/*
 * Frank Lin
 * 
 */

package edu.cmu.lti.nlp.mt.translator;

import edu.cmu.lti.nlp.CLang;

public class DictMTWikipedia extends DictMFTranslator{
	
	static final long serialVersionUID=1;
	public static class Param extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public boolean greedyMerge;
		public boolean tracing;
		public boolean lowerCase;
		public Param() {
			super(DictMTWikipedia.class);
			parse();
		}
		public void parse(){
			//super.parse();			
			greedyMerge=Boolean.parseBoolean(
					getString("greedyMerge","false"));
			lowerCase=Boolean.parseBoolean(
					getString("lowerCase","true"));
			tracing=Boolean.parseBoolean(
					getString("tracing","true"));
		}
	}	
	public Param p=new Param();	

	public DictMTWikipedia(){
		//super(new Param(getClass());
		//p=new Param();
/*		String[][] FILES={
				{"en_US","en_US","/wikipedia_redirect_en_filtered.txt"}
				,{"en","en","/wikipedia_redirect_ja.txt","ja"}
				,{"en","en","/wikipedia_redirect_zh.txt","zh"}
				,{"en","en","/wikipedia_link_en-ja.txt","en","ja"}
				,{"en","en","/wikipedia_link_en-zh.txt","en","zh"}
		};
*/
		mm_files.getC(CLang.en_US).getC(CLang.en_US).add("wikipedia_redirect_en_filtered.txt");
		mm_files.getC(CLang.ja_JP).getC(CLang.ja_JP).add("wikipedia_redirect_ja.txt");
		mm_files.getC(CLang.zh_CN).getC(CLang.zh_CN).add("wikipedia_redirect_zh.txt");
		mm_files.getC(CLang.en_US).getC(CLang.ja_JP).add("wikipedia_link_en-ja.txt");
		mm_files.getC(CLang.en_US).getC(CLang.zh_CN).add("wikipedia_link_en-zh.txt");
	}

	public boolean parseLine(String line){
		//int numEntries;
		String[] split=line.split("\\t");
		if(split.length <2) return false;
		split[0]=split[0].trim();
		split[1]=split[1].trim();
		
		if(p.lowerCase){
			split[0]=split[0].toLowerCase();
			split[1]=split[1].toLowerCase();
		}
		if(split[0].length()==0||split[1].length()==0)
			return false;
		dict.add(split[0], split[1]);
		return true;
	}

	public static void main(String[] args)throws Exception{
/*		MultiLexicon ml=new MultiLexicon(false,true,true);
		//ml.setGreedyMerge(false);
		//ml.setLowerCase(true);
		//ml.setTracing(true);
		ml.loadLexicon("resources/wikipedia_redirect_en_filtered.txt","en");
		ml.loadLexicon("resources/wikipedia_redirect_ja.txt","ja");
		ml.loadLexicon("resources/wikipedia_redirect_zh.txt","zh");
		ml.loadLexicon("resources/wikipedia_link_en-ja.txt","en","ja");
		ml.loadLexicon("resources/wikipedia_link_en-zh.txt","en","zh");
		BufferedReader reader=new BufferedReader(new InputStreamReader(System.in));
		System.out.print("Enter word: ");
		for(String nextLine;!(nextLine=reader.readLine()).equals("quit");System.out.print("Enter word: ")){
			//String[] words=nextLine.split("\\t+");
			//MLToolkit.println(new Boolean(ml.isAlternateForms(words[0],words[1],"en")));
			Set<String> trans1=ml.getTranslations(nextLine.trim(),"en","ja");
			System.out.println("Japanese:");
			for(String tran:trans1){
				System.out.println("\t"+tran);
			}
			Set<String> trans2=ml.getTranslations(nextLine.trim(),"en","zh");
			System.out.println("Chinese:");
			for(String tran:trans2){
				System.out.println("\t"+tran);
			}
		}*/
	}

}
