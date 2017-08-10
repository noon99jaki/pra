package edu.cmu.lti.util.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.VectorS;


public class FPattern {

	final static String illegalChars = "[�B。0-9a-zA-Z,_?!\\-\\(\\)\\.\\[\\]\\!\\?]+";
	
	public static String getSafePattern(String s) {
		return s.replaceAll(illegalChars, "");
	}
	
	
	public static VectorS matchAll(String txt, String regx){
		VectorS vs= new VectorS();
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(txt);
		while (m.find()) 
			vs.add(m.group(1));		
		return vs;
	}
	public static String match(String txt, String regx){
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(txt);
		if (m.find()) 
			return m.group(1);
		
		return null;
	}
	
	public static VectorS matchAll(String txt, String regx, int n){
		VectorS vs= new VectorS();
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(txt);
		while (m.find()) {
			for (int i=1; i<=m.groupCount(); ++i)
				vs.add(m.group(i));
		}
		return vs;
	}
	
	public static VectorS matchParts(String txt, String regx){
		VectorS vs= new VectorS();
		Pattern p = Pattern.compile(regx);
		Matcher m = p.matcher(txt);
		if(m.find()) 
			for (int i=1; i<=m.groupCount(); ++i)
				vs.add(m.group(i));		
		return vs;
	}
}
