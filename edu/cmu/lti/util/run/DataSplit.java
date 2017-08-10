package edu.cmu.lti.util.run;

import edu.cmu.lti.algorithm.container.VecVecI;
import edu.cmu.lti.algorithm.container.VectorI;

//K fold cross validation
//K<=1 means Leave one out cross validation 
public class DataSplit {

	public VecVecI fold_train_ids = new VecVecI();
	public VecVecI fold_test_ids = new VecVecI();

	public DataSplit(int num_fold, int num_sample) {
		splitDeterministic(num_fold, num_sample);
	}

	public void splitDeterministic(int fold, int num_samples) {
		splitDeterministic(fold, VectorI.seq(num_samples));
	}

	public void splitDeterministic(int num_fold, VectorI ids) {
		if (num_fold <= 1) num_fold = ids.size();

		fold_train_ids.clear();
		fold_train_ids.extend(num_fold);

		fold_test_ids.clear();
		fold_test_ids.extend(num_fold);

		for (int i = 0; i < ids.size(); ++i) {
			int fold = i % num_fold;

			for (int j = 0; j < num_fold; ++j)
				if (j == fold) fold_test_ids.get(j).add(i);
				else fold_train_ids.get(j).add(i);
		}
		return;
	}

	public static void main(String[] args) {
		DataSplit cv3 = new DataSplit(3, 20);
		DataSplit loo = new DataSplit(-1, 20);
		return;
	}
}