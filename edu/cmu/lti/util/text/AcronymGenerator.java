package edu.cmu.lti.util.text;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;

/**
 * generate (approximately) three letter acronyms for a string of words
 * @author nlao
 *
 */
public class AcronymGenerator {
	public MapSS mTopicCode= new MapSS();
	public MapSS mCodeTopic= new MapSS();
	public SetS mCode= new SetS();
	public VectorS getCode(String vsTopic[]){
		VectorS vsCode= new VectorS();
		vsCode.ensureCapacity(vsTopic.length);
		for (String topic: vsTopic)
			vsCode.add(getCode(topic));		
		return vsCode;
	}
	public String getCode(String topic){
		String code=mTopicCode.get(topic);
		if (code!=null) 
			return code;
		
		code= FString.getLeadingLetters(topic,0);
		if (code.length()<3)
			code= FString.getLeadingLetters(topic, 1);
		if (code.length()<3)
			code= FString.getLeadingLetters(topic, 2);
		
		if (mCode.contains(code))
			System.err.print("code conflict");
		mCode.add(code);
		mTopicCode.put(topic,code);
		mCodeTopic.put(code,topic);
		return code;
	}
}
