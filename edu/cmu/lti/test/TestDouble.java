package edu.cmu.lti.test;

import edu.cmu.lti.algorithm.container.VectorD;

public class TestDouble {
	
	public static void main(String[] args) {
		VectorD vd1= new VectorD();
		vd1.add(0.1);vd1.add(0.2);
		VectorD vd2= new VectorD();
		vd2.add(vd1.get(0));
		

	}
	
}
