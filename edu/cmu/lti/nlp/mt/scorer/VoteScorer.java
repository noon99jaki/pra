package edu.cmu.lti.nlp.mt.scorer;

import java.util.Map;

import edu.cmu.lti.nlp.mt.MTResult;
import edu.cmu.lti.nlp.mt.MTResult.Target;

public class VoteScorer extends Scorer{

	public void score(MTResult tr){
		int max=Integer.MIN_VALUE;
		//for(Target target:tr.targets){
		for (Map.Entry<String, Target> e: tr.targets.entrySet()){
			String k = e.getKey();
			Target x = e.getValue();
			if(x.translators.size()>max){
				max=x.translators.size();
			}
		}
		for (Map.Entry<String, Target> e: tr.targets.entrySet()){
			String k = e.getKey();
			Target x = e.getValue();
			x.score=((double)x.translators.size())/max;
		}
	}

}
