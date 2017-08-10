package edu.cmu.lti.test;

import java.util.regex.Pattern;

public class TestPattern {
	protected static final Pattern pNE 
	= Pattern.compile("\\w");
	//= Pattern.compile("[a-zA-Z]");

		public static void main(String[] args) {
			String txt = "  as";
			//if (txt..matches("[a-zA-Z]+"))
			//	if (txt.matches("[a-zA-Z]"))
			if (pNE.matcher(txt).find())
				txt = "";
			return;
		}
}
