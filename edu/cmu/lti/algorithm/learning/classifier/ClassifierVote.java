package edu.cmu.lti.algorithm.learning.classifier;

import edu.cmu.lti.algorithm.container.MapIMapIX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VecVecI;
import edu.cmu.lti.algorithm.learning.data.DataSet;
import edu.cmu.lti.algorithm.learning.data.DataSetBinaryI;
import edu.cmu.lti.algorithm.learning.data.Instance;

/**
 * do multi-class classification based on voting
 * @author nlao
 *
 */
public abstract class ClassifierVote extends Classifier {


	protected MapIMapIX<C2Classifier> mmModel;

	
	Class cC;//type of 2 class classifier
	public ClassifierVote(Class cC){
		this.cC = cC;
		mmModel= new MapIMapIX<C2Classifier>(cC); 
	}

	public void train(DataSetBinaryI ds){
		VectorI vL = ds.v.getVI("label");
		int nC = ds.idxLabel.m.size();
		VecVecI vvi= new VecVecI();
		for (int i=0; i<nC;++i)
			vvi.add(vL.idxEqualToInt(i));
		
		
		for (int i=0; i<nC-1;++i)
			for (int j=i+1; j<nC;++j)
				mmModel.getC(i).put(j, 
					trainBinarized(	ds, vvi.get(i),vvi.get(j)) );			
	}
	
	public abstract C2Classifier trainBinarized(
			DataSet dsTrain, VectorI vp, VectorI vn);
		
	
	public Instance test(Instance ins){
		Instance ins1 = new Instance();
		
		return ins1;
	}

}
