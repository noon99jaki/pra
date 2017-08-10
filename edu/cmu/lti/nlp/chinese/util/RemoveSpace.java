package edu.cmu.lti.nlp.chinese.util;

import java.util.regex.Pattern;

public class RemoveSpace {

	static public Pattern spacePattern = Pattern.compile(" ");
	static public Pattern spaceHolderPattern = Pattern.compile("([a-zA-Z]) ([a-zA-Z])");
	static public Pattern recoverSpacePattern = Pattern.compile("SPACEHOLDER");
	
	static public String removeSpace(String line){
		line = line.replaceAll("/"," ");
		line = spaceHolderPattern.matcher(line).replaceAll("$1SPACEHOLDER$2");
		line = spacePattern.matcher(line).replaceAll(""); 
		line = recoverSpacePattern.matcher(line).replaceAll(" "); 
		return line;
	}
}
