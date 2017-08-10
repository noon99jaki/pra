package edu.cmu.pra.foil;

import edu.cmu.pra.LearnerPRA;
import edu.cmu.pra.foil.FOIL.ERankMod;

public class EvaluatorFOIL extends Evaluator{
	
	
	public ERankMod rankMod=ERankMod.MAX;//.MAX; SUM
	public EvaluatorFOIL(){
		super("FOIL");
		predicate_folder_=Evaluator.query_folder_+".FOIL."+rankMod;//+"/";
		//"predictions.FOIL."
		return;
	}
//	public static String fdQueries="queries.UL/";	
	protected void predictForTask(LearnerPRA l,String rel){
		FOIL model= new FOIL(l.graph_, rankMod);
		model.loadModel(FOIL.fnRules, rel);
		model.predict(l.queries_,predicate_folder_+"/"+rel);

	}
	

}
