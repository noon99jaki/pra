package edu.cmu.lti.algorithm.math.rand;

import edu.cmu.lti.algorithm.container.VectorD;

public class Interfaces {
	public static interface IDrawInt {
		public int draw();
	}
	public static interface IDrawDouble {
		public double draw();
	}
	
	/**
	 * draw multidimensional random variable
	 * @author nlao
	 */
	public static interface IDrawVecD {
		public VectorD draw();

	}
}
