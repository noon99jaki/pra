/*
 * Frank Lin
 * 
 */

package edu.cmu.lti.nlp.mt.cacher;

import edu.cmu.lti.util.run.Cacher;

public class WebStatCacher extends Cacher{

	public WebStatCacher(String cacheDir){
		super(cacheDir);
	}

	public void saveCache(
			String statSource,String statType,String lang
			,String query,String stat) throws Exception{
		saveObj(stat, verification(statSource,statType,lang,query));
	}

	public String loadCache(
			String statSource,String statType,String lang
			,String query)throws Exception{
		return (String) loadObj(verification(statSource,statType,lang,query));
	}
	
	protected String verification(String statSource,String statType,String lang,String query){
		return statSource+"_"+statType+"_"+lang+"_"+query;
	}
}
