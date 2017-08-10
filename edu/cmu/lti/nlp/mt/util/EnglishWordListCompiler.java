package edu.cmu.lti.nlp.mt.util;

import java.util.*;
import java.util.regex.*;
import java.io.*;

public class EnglishWordListCompiler{

	public static void main(String[] args)throws Exception{

		String outputFileName=args[0];

		SortedSet<String> list=new TreeSet<String>();

		list.addAll(parse12Dicts("data/12dicts-4.0/2of4brif.txt"));
		list.addAll(parse12Dicts("data/12dicts-4.0/2of12inf.txt"));
		list.addAll(parse12Dicts("data/12dicts-4.0/6of12.txt"));
		//list.addAll(parse12Dicts("data/tm/dictionaries/scowl-6-common.utf8"));

		list.addAll(parseCeDict("data/tm/dictionaries/cedict_ts.u8"));

		list.addAll(parseLdcCeLex("data/tm/dictionaries/ldc_cedict.simp.v3"));

		list.addAll(parseEijiroEnglishWordList("data/tm/dictionaries/EijiroEnglishWordList.txt"));

		PrintWriter writer=new PrintWriter(new OutputStreamWriter(new FileOutputStream(outputFileName),"utf8"),true);
		for(String word:list){
			writer.println(word);
		}
		writer.close();
		System.out.println("Saved "+list.size()+" words to "+outputFileName+".");

	}

	public static Set<String> parse12Dicts(String filename)throws Exception{
		Set<String> list=new HashSet<String>();
		Pattern suffixGarbagePattern=Pattern.compile("[^\\p{Alpha}]+$");
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"utf8"));
		for(String nextLine;(nextLine=reader.readLine())!=null;){
			String word=suffixGarbagePattern.matcher(nextLine.trim()).replaceAll("").toLowerCase();
			list.add(word);
		}
		reader.close();
		System.out.println("Loaded "+list.size()+" words from "+filename+".");
		return list;
	}

	public static Set<String> parseEijiroEnglishWordList(String filename)throws Exception{
		Set<String> list=new HashSet<String>();
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"utf8"));
		for(String nextLine;(nextLine=reader.readLine())!=null;){
			String word=nextLine.trim().toLowerCase();
			list.add(word);
			if(word.indexOf("-")>-1){
				list.add(word.replace('-',' '));
			}
		}
		reader.close();
		System.out.println("Loaded "+list.size()+" words from "+filename+".");
		return list;
	}

	private final static Pattern LINE_PAT=Pattern.compile("(.+?) (.+?) \\[(.+?)\\] /(.*)/");
	private final static Pattern PAREN_PAT=Pattern.compile("[\\{\\(\\[].*?[\\}\\)\\]]");
	private final static Pattern TO_PAT=Pattern.compile("^to");
	private final static Pattern DET_PAT=Pattern.compile("^((a)|(an)|(the)) ");
	private final static Pattern OMIT_PAT=Pattern.compile("\\.\\.\\.");
	private final static Pattern QUOTE_PAT=Pattern.compile("\"");
	private final static Pattern SPACES_PAT=Pattern.compile("\\s\\s+");

	public static Set<String> parseCeDict(String filename)throws Exception{
		Set<String> list=new HashSet<String>();
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"utf8"));
		for(String nextLine;(nextLine=reader.readLine())!=null;){
			if(!nextLine.startsWith("#")){
				Matcher matcher=LINE_PAT.matcher(nextLine);
				//Find line
				if(matcher.find()){
					//Split the translations
					String[] senses=matcher.group(4).split("/");
					//Do some processing with each translation
					for(int i=0;i<senses.length;i++){
						senses[i]=PAREN_PAT.matcher(senses[i]).replaceAll("").trim();
						String[] trans=senses[i].split("[,;]");
						for(int j=0;j<trans.length;j++){
							String word=trans[j].trim();
							word=TO_PAT.matcher(word).replaceAll("").trim();
							word=DET_PAT.matcher(word).replaceAll("").trim();
							word=OMIT_PAT.matcher(word).replaceAll("").trim();
							word=QUOTE_PAT.matcher(word).replaceAll("").trim();
							word=SPACES_PAT.matcher(word).replaceAll("").trim();
							word=word.toLowerCase();
							if(word.length()>0){
								list.add(word);
							}
						}
					}
				}
				else{
					System.err.println("Bad data in dictionary: "+filename+": "+nextLine+", continuing...");
				}
			}
		}
		reader.close();
		System.out.println("Loaded "+list.size()+" words from "+filename+".");
		return list;
	}

	public static Set<String> parseLdcCeLex(String filename)throws Exception{
		Set<String> list=new HashSet<String>();
		Pattern pattern=Pattern.compile("(.+?)\\t/(.+)/");
		BufferedReader reader=new BufferedReader(new InputStreamReader(new FileInputStream(filename),"utf8"));
		for(String nextLine;(nextLine=reader.readLine())!=null;){
			if(!nextLine.startsWith("#")){
				Matcher m=pattern.matcher(nextLine);
				if(m.find()){
					String[] english=m.group(2).split("/");
					//add data to hashtables
					for(int i=0;i<english.length;i++){
						String word=english[i].trim().toLowerCase();
						word=PAREN_PAT.matcher(word).replaceAll("");
						word=DET_PAT.matcher(word).replaceAll("");
						if(word.length()>0){
							list.add(word);
						}
					}
				}
				else{
					System.err.println("Bad data: "+nextLine+", continuing...");
				}
			}
		}
		reader.close();
		System.out.println("Loaded "+list.size()+" words from "+filename+".");
		return list;
	}

}
