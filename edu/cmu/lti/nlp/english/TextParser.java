package edu.cmu.lti.nlp.english;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

/**
 * This class parse a piece of text into paragraphs, sentences, and words
 */
public class TextParser {
	private static final Logger log = Logger.getLogger(TextParser.class);
	/**
	 * Public menber variables to store the result of parsing
	 */
	public ArrayList<String> vs_words=new ArrayList<String>();
	public ArrayList<ArrayList<String> > vvs_objs 
		= new ArrayList<ArrayList<String> >();
	public ArrayList<Integer> vi_comma=new ArrayList<Integer>();
	public ArrayList<Integer> vi_sentence=new ArrayList<Integer>();
	ArrayList<Integer> vi_paragraph=new ArrayList<Integer>();
	public ArrayList<Integer> vi_char_b=new ArrayList<Integer>();
	public ArrayList<Integer> vi_char_e=new ArrayList<Integer>();
	
	
	/**
	 * Public menber variables 
	 */
	Set<String> ms_conj=new HashSet<String>();
	Set<String> ms_stop_word=new HashSet<String>();
	String allow_char= new String();
	boolean make_lower ;
	public void clear(){
		vs_words.clear();
		vi_comma.clear();
		vi_sentence.clear();
		vi_char_b.clear();
		vi_char_e.clear();
		vi_paragraph.clear();
	}
	public TextParser(){
		//String allow_char = new String(""))	{
		//this.allow_char  = allow_char;
		make_lower = false;
		ms_conj.add("and");
		ms_conj.add("&");
		ms_conj.add("amp");
		ms_conj.add("w");
		ms_conj.add("with");
		ms_conj.add("for");
		ms_stop_word.add("the");
	}
	
	/**
	 * check if a word has appeared in the parsed document
	 */
	int find_word(String word, List<Integer> vi_words){
		vi_words.clear();
		for (int i=0; i<vs_words.size();++i){
			if (vs_words.get(i) == word) vi_words.add(i);
		}
		return vi_words.size();
	}
	public boolean is_conj(String word){
		return ms_conj.contains(word);
	}
	public boolean is_stop_word(String  word){
		return ms_stop_word.contains(word);
	}
	public static boolean is_alnum(int c){
		return (c>='0' && c<='9')
			|| (c>='a' && c<='z')
			|| (c>='A' && c<='Z');
	}
	public static boolean is_capital(int c){
		return (c>='A' && c<='Z');
	}
	int last_false=-1;
	/**
	 * A word has been found at the current position
	 */
	public boolean add_word(int i,String txt){
		if (last_false >= i-1)return false;
		vi_char_b.add(last_false+1);
		vi_char_e.add(i-1);
		//s= txt.Mid(last_false+1, i-last_false-1);
		vs_words.add(txt.substring(last_false+1, i));
		return true;
	}
	/**
	 * Decide if the current position is the end of a paragraph
	 */
	public boolean is_paragraph(int i,String txt)	{
		int n_search=5;
		int k=i+1;
		for (;k<i+n_search && k<txt.length(); ++k )	{
			if (txt.charAt(k)=='\n') break;
		}
		return k<i+n_search;
	}
	/**
	 * Decide if the current position is a word character
	 */
	public boolean  lawful_char(int i,String  txt){
		char c=txt.charAt(i);
		if (is_alnum(c) ) 	return true;
		if (allow_char.contains(""+c)) 	return true;
		if (i>0 && i< txt.length()-1)		{
			char c1=txt.charAt(i-1);
			char c2=txt.charAt(i+1);
			switch(c){
			case ',':
			case '.':
				if (Character.isDigit(c1) && Character.isDigit(c2))	return true;
				break;
			case '-':
				if (is_alnum(c1) && is_alnum(c2)) return true;
				break;
			case '&':			
				return true;
			default:
				break;
			}
			//if (txt[i]=='#' && txt[i-1] = &) continue;
		}
		return false;
	}
	/**
	 * Decide if the current position is the end of a sentence
	 */
	void check_sentence(int i,String txt){
		String s;
		switch(txt.charAt(i)){
		case',':
			vi_comma.add(vs_words.size());
			break;
		case'.':
			//char c=vs_words.back()[0];
			if (vs_words.size()>0) {
				s=vs_words.get(vs_words.size()-1);
				if (s.charAt(0)>='A' && s.charAt(0)<='Z' 
					&& s.length()<=4)	break;
			}
			//vi_sentence.push_back(vs_words.size());
			//break;
		case'!':
		case'?':
			vi_sentence.add(vs_words.size());
			if (is_paragraph(i,txt))
				vi_paragraph.add(vi_sentence.size());
			break;
		default:
			break;
		}
	}
	/**
	 * finish the last word and sentence
	 */
	void finish_parse(){
		if (vi_sentence.size()>0){
			if (vi_sentence.get(vi_sentence.size()-1)!=vs_words.size())
				vi_sentence.add(vs_words.size());
		}
		else{
			vi_sentence.add(vs_words.size());
		}
		if (vi_paragraph.size()>0)	{
			if (vi_paragraph.get(vi_paragraph.size()-1)!=vi_sentence.size())
				vi_paragraph.add(vi_sentence.size());
		}
		else{
			vi_paragraph.add(vi_sentence.size());
		}
	}
	
	/**
	 * This is the main entrance to this class
	 * @param txt - text to be parsed
	 */
	public void parse( String txt){
		clear();
		last_false = -1;
		//if (make_lower) txt.makeLower();
		int i=0;
		for( ;i<txt.length(); ++i  ){
			if (lawful_char(i, txt)) continue;
			//unlawful chars
			add_word(i, txt);
			check_sentence(i, txt);
			last_false = i;
		}
		add_word(i, txt);
		finish_parse();
		
		log.debug("vi_sentence="+vi_sentence.size()
			+" vs_words="+ vs_words.size());
		//log.debug(vi_sentence);
		//log.debug(vs_words);
		return;
	}
}

