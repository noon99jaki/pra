package edu.cmu.lti.nlp;

import edu.cmu.lti.algorithm.container.VectorS;

public class Tokenizer {
	public static VectorS tokenize(String txt){
		VectorS vs= new VectorS();
		for (String word:txt.split(	"[\\p{Space}]+")){
			//word.replaceAll("\\p{Punct}+$", "");
			word=word.replaceAll("[.,!?;:]+$", "");
			if (word.length()<=1)continue;
			word= word.toLowerCase();
			if (StopWord.m429.contains(word))continue;
			vs.add(word);
		}
		return vs;
	}
}
