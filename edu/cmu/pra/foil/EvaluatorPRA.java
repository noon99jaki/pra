package edu.cmu.pra.foil;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.pra.LearnerPRA;

public class EvaluatorPRA extends Evaluator{
	
	public static String fdModel="../run12/collectedModels/";

	public EvaluatorPRA(){
		super("PRA");
		predicate_folder_=Evaluator.query_folder_+".PRA";
	
	//	fdPred="predictions.PRA";
	}
	
	// predict with PRA model
	// we now have a parallel version of this
	public void predictForTask(LearnerPRA l,String rel){
		
		if (!FFile.exist(fdModel+rel)) {
			System.err.print("no model found for relation="+rel);
			return;
		}
		
		l.model_.loadModel(fdModel+rel);
		
		//l.predict(l.queries_,fdPred+rel);
		
		return;
	}
}
