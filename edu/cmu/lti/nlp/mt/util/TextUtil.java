/**
 * TextUtil.java
 * 
 * @author frank
 */

package edu.cmu.lti.nlp.mt.util;

import java.util.*;
import java.util.regex.*;

/**
 * 
 */

public class TextUtil{

	private static Pattern XML_ESCAPE_PATTERN=Pattern.compile("&(.+?);");
	private static Map<String,String> XML_ESCAPE_MAP=null;
	static{
		Map<String,String> m=new HashMap<String,String>();
		for(int i=0;i<255;i++){
			m.put("#"+i,""+(char)i);
		}
		m.put("euro","€");
		m.put("quot",""+(char)34);
		m.put("amp",""+(char)38);
		m.put("lt",""+(char)60);
		m.put("gt",""+(char)62);
		m.put("nbsp",""+(char)160);
		m.put("iexcl",""+(char)161);
		m.put("cent",""+(char)162);
		m.put("pound",""+(char)163);
		m.put("curren",""+(char)164);
		m.put("yen",""+(char)165);
		m.put("brvbar",""+(char)166);
		m.put("sect",""+(char)167);
		m.put("uml",""+(char)168);
		m.put("copy",""+(char)169);
		m.put("ordf",""+(char)170);
		m.put("laquo",""+(char)171);
		m.put("not",""+(char)172);
		m.put("shy",""+(char)173);
		m.put("reg",""+(char)174);
		m.put("macr",""+(char)175);
		m.put("deg",""+(char)176);
		m.put("plusmn",""+(char)177);
		m.put("sup2",""+(char)178);
		m.put("sup3",""+(char)179);
		m.put("acute",""+(char)180);
		m.put("micro",""+(char)181);
		m.put("para",""+(char)182);
		m.put("middot",""+(char)183);
		m.put("cedil",""+(char)184);
		m.put("sup1",""+(char)185);
		m.put("ordm",""+(char)186);
		m.put("raquo",""+(char)187);
		m.put("frac14",""+(char)188);
		m.put("frac12",""+(char)189);
		m.put("frac34",""+(char)190);
		m.put("iquest",""+(char)191);
		m.put("Agrave",""+(char)192);
		m.put("Aacute",""+(char)193);
		m.put("Acirc",""+(char)195);
		m.put("Atilde",""+(char)195);
		m.put("Auml",""+(char)196);
		m.put("Aring",""+(char)197);
		m.put("AElig",""+(char)198);
		m.put("Ccedil",""+(char)199);
		m.put("Egrave",""+(char)200);
		m.put("Eacute",""+(char)201);
		m.put("Ecric",""+(char)202);
		m.put("Euml",""+(char)203);
		m.put("Igrave",""+(char)204);
		m.put("Iacute",""+(char)205);
		m.put("Icirc",""+(char)206);
		m.put("Iuml",""+(char)207);
		m.put("ETH",""+(char)208);
		m.put("Ntilde",""+(char)209);
		m.put("Ograve",""+(char)210);
		m.put("Oacute",""+(char)211);
		m.put("Ocirc",""+(char)212);
		m.put("Otilde",""+(char)213);
		m.put("Ouml",""+(char)214);
		m.put("times",""+(char)215);
		m.put("Oslash",""+(char)216);
		m.put("Ugrave",""+(char)217);
		m.put("Uacute",""+(char)218);
		m.put("Ucirc",""+(char)219);
		m.put("Uuml",""+(char)220);
		m.put("Yacute",""+(char)221);
		m.put("THORN",""+(char)222);
		m.put("szlig",""+(char)223);
		m.put("agrave",""+(char)224);
		m.put("aacute",""+(char)225);
		m.put("acirc",""+(char)226);
		m.put("atilde",""+(char)227);
		m.put("auml",""+(char)228);
		m.put("aring",""+(char)229);
		m.put("aelig",""+(char)230);
		m.put("ccedil",""+(char)231);
		m.put("egrave",""+(char)232);
		m.put("eacute",""+(char)233);
		m.put("ecirc",""+(char)234);
		m.put("euml",""+(char)235);
		m.put("igrave",""+(char)236);
		m.put("iacute",""+(char)237);
		m.put("icirc",""+(char)238);
		m.put("iuml",""+(char)239);
		m.put("eth",""+(char)240);
		m.put("ntilde",""+(char)241);
		m.put("ograve",""+(char)242);
		m.put("oacute",""+(char)243);
		m.put("ocirc",""+(char)244);
		m.put("otilde",""+(char)245);
		m.put("ouml",""+(char)246);
		m.put("divide",""+(char)247);
		m.put("oslash",""+(char)248);
		m.put("ugrave",""+(char)249);
		m.put("uacute",""+(char)250);
		m.put("ucirc",""+(char)251);
		m.put("uuml",""+(char)252);
		m.put("yacute",""+(char)253);
		m.put("thorn",""+(char)254);
		XML_ESCAPE_MAP=m;
	}

	public static String decodeCommonXmlEscapes(String s){
		StringBuffer b=new StringBuffer();
		Matcher m=XML_ESCAPE_PATTERN.matcher(s);
		while(m.find()){
			String decoded=XML_ESCAPE_MAP.get(m.group(1));
			if(decoded!=null){
				m.appendReplacement(b,decoded);
			}
			else{
				m.appendReplacement(b,m.group(0));
			}
		}
		m.appendTail(b);
		return b.toString();
	}

	private static Pattern UNICODE_ESCAPE_PATTERN_DEC=Pattern.compile("&\\#(\\d+?);");
	private static Pattern UNICODE_ESCAPE_PATTERN_HEX=Pattern.compile("&\\#x([a-fA-F\\d]+?);");

	public static String decodeUnicodeEscapes(String s,Pattern pat){
		Matcher m=pat.matcher(s);
		StringBuffer decoded=new StringBuffer();
		while(m.find()){
			if(pat.equals(UNICODE_ESCAPE_PATTERN_HEX)){
				m.appendReplacement(decoded,new Character((char)Integer.parseInt(m.group(1),16)).toString());
			}
			else if(pat.equals(UNICODE_ESCAPE_PATTERN_DEC)){
				m.appendReplacement(decoded,new Character((char)Integer.parseInt(m.group(1))).toString());
			}
			else{
				m.appendReplacement(decoded,m.group(1));
			}
		}
		m.appendTail(decoded);
		return decoded.toString();
	}

	public static String decodeUnicodeEscapes(String s){
		String decoded=null;
		decoded=decodeUnicodeEscapes(s,UNICODE_ESCAPE_PATTERN_DEC);
		decoded=decodeUnicodeEscapes(decoded,UNICODE_ESCAPE_PATTERN_HEX);
		return decoded;
	}

	private static final Pattern OPEN_SEARCH_TERM_TAG=Pattern.compile("<(([Bb])|([Ff][Oo][Nn][Tt] .+?))>");
	private static final Pattern CLOSE_SEARCH_TERM_TAG=Pattern.compile("</(([Bb])|([Ff][Oo][Nn][Tt]))>");

	public static String replaceSearchTermTags(String s){
		Matcher m=OPEN_SEARCH_TERM_TAG.matcher(s);
		s=m.replaceAll("[[[");
		m=CLOSE_SEARCH_TERM_TAG.matcher(s);
		s=m.replaceAll("]]]");
		return s;
	}

	public static String stripXmlTags(String s){
		return s.replaceAll("<.+?>","");
	}

	public static List<String> tokenizeSimple(String s){
		List<String> tokens=new ArrayList<String>();
		if(s.length()>0){
			//create token holder
			StringBuilder token=new StringBuilder();
			//process the first character
			int prev=s.codePointAt(0);
			int prevType=Character.getType(prev);
			Character.UnicodeBlock prevBlock=Character.UnicodeBlock.of(prev);
			token.appendCodePoint(prev);
			for(int i=1;i<s.length();i++){
				int curr=s.codePointAt(i);
				int currType=Character.getType(curr);
				Character.UnicodeBlock currBlock=Character.UnicodeBlock.of(curr);
				boolean delim;
				if(currType!=prevType||currBlock!=prevBlock){
					delim=true;
				}
				else{
					delim=false;
				}
				if(delim){
					String cand=token.toString().trim();
					if(cand.length()>0){
						tokens.add(cand);
					}
					token=new StringBuilder();
					token.appendCodePoint(curr);
				}
				else{
					token.appendCodePoint(curr);
				}
				prev=curr;
				prevType=currType;
				prevBlock=currBlock;
			}
			//process the characters in token holder
			tokens.add(token.toString());
		}
		return tokens;
	}

	private static final Pattern BASIC_TOKEN_FILTER=Pattern.compile("["+
			"\\p{Lu}"+
			"\\p{Mn}"+
			"\\p{Mc}"+
			"\\p{Me}"+
			"\\p{Pc}"+
			"\\p{Pd}"+
			"\\p{Ps}"+
			"\\p{Pe}"+
//			"\\p{Pi}"+
			"“<«"+
//			"\\p{Pf}"+
			"”>»"+
			"\\p{Po}"+
			"\\p{Zs}"+
			"\\p{Zl}"+
			"\\p{Zp}"+
			"\\p{Cc}"+
			"\\p{Cf}"+
			"\\p{Cs}"+
			"‘|~"+
	"]+");

	public static boolean filterBasic(String s){
		return BASIC_TOKEN_FILTER.matcher(s).matches();
	}

	public static List<String> filterTokensBasic(List<String> tokens){
		List<String> filtered=new ArrayList<String>();
		for(String token:tokens){
			if(!filterBasic(token)){
				filtered.add(token);
			}
		}
		return filtered;
	}

	public static List<String> filterWhiteSpace(List<String> tokens){
		List<String> filtered=new ArrayList<String>();
		for(String token:tokens){
			if(token.trim().length()>0){
				filtered.add(token);
			}
		}
		return filtered;
	}

	public static List<String> filterTokens(List<String> tokens,Set<String> filter){
		List<String> filtered=new ArrayList<String>();
		for(String token:tokens){
			if(!filter.contains(token)){
				filtered.add(token);
			}
		}
		return filtered;
	}

	public static List<String> filterTokens(List<String> tokens,List<String> filter){
		return filterTokens(tokens,new HashSet<String>(filter));
	}

	public static List<String> filterTokens(List<String> tokens,String[] filter){
		return filterTokens(tokens,Arrays.asList(filter));
	}

	//Levenshtein Distance algorithm : modified version of Java version from http://en.wikipedia.org/wiki/Levenshtein_distance

	private static int minimum(int a,int b,int c){
		return Math.min(Math.min(a,b),c);
	}

	public static int getLevenshteinDistance(char[] str1,char[] str2){
		int[][] distance=new int[str1.length+1][];
		for(int i=0;i<=str1.length;i++){
			distance[i]=new int[str2.length+1];
			distance[i][0]=i;
		}
		for(int j=0;j<str2.length+1;j++){
			distance[0][j]=j;
		}
		for(int i=1;i<=str1.length;i++){
			for(int j=1;j<=str2.length;j++){
				distance[i][j]=minimum(distance[i-1][j]+1,distance[i][j-1]+1,distance[i-1][j-1]+((str1[i-1]==str2[j-1])?0:1));
			}
		}
		return distance[str1.length][str2.length];
	}

	public static int getLevenshteinDistance(String s1,String s2){
		return getLevenshteinDistance(s1.toCharArray(),s2.toCharArray());
	}

	public static List<String> chunk(String s){
		List<String> chunks=new ArrayList<String>();
		String[] split=s.split("\\.+");
		for(String chunk:split){
			chunks.add(chunk.trim());
		}
		return chunks;
	}

}
