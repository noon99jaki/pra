package edu.cmu.lti.test;

import edu.cmu.lti.algorithm.math.rand.FRand;

public class TestRand {
	public static void main(String[] args) {
		for (int i=0; i<10;++i){
			System.out.println(FRand.rand.nextDouble());
		}
		return;
	}	
}
/*
0.7308781907032909
0.41008081149220166
0.20771484130971707
0.3327170559595112
0.9677559094241207
0.006117182265761301
0.9637047970232077
0.9398653887819098
0.9471949176631939
0.9370821488959696
*/