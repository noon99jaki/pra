package edu.cmu.lti.util.text;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.VectorX;

public class VectorPattern  extends VectorX<Pattern>{
	public VectorPattern(String[] vsPattern){
		super(Pattern.class);
		for (String s: vsPattern)
			add( Pattern.compile(s));

	}
	public Matcher match(String s){		
		for (Pattern p: this){
			Matcher ma = p.matcher(s);
      if(ma.matches())
      	return ma;
		}
		return null;
	}
}
