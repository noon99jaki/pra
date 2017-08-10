package edu.cmu.lti.algorithm.math.rand;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Random;

import edu.cmu.lti.algorithm.container.VectorI;

public class FRand {
	public static Random rand = new Random(2);

	public static void setSeed(String txt) {
		setSeed(txt.hashCode());
	}

	public static void setSeed(int seed) {
		FRand.rand = new Random(seed);
		//System.err.println("random seed="+seed);
		System.out.println("random seed=" + seed);
	}

	public static boolean drawBoolean(double p) {
		return rand.nextDouble() < p;
	}

	public static int drawBinary(double p) {
		return drawBoolean(p) ? 1 : 0;
	}

	// assume min=0
	public static double drawDouble() {
		return rand.nextDouble();
	}

	public static int drawInt(int max) {
		return (int) Math.floor(rand.nextDouble() * max);
	}

	public static int drawInt(int min, int max) {
		return drawInt(max - min) + min;
	}

	//	static VectorI vRlt= new VectorI();
	//
	//	public static VectorI lowVarSampleInt(int range, int nSample){
	//		VectorI vR= new VectorI();
	//		lowVarSampleInt(0,range,vR);
	//		return vR;
	//	}
	//	
	//	public static VectorI lowVarSampleIntStatic(int range, int nSample){
	//		lowVarSampleInt(0,range,vRlt);
	//		return vRlt;
	//	}
	//	
	//	public static void lowVarSampleInt(int range, int num_sample, VectorI vSamples){
	//		vSamples.clear();vSamples.ensureCapacity(num_sample);
	//		double stepSize= (double)range/ (double)num_sample;
	//		double p=FRand.drawDouble()*stepSize;
	//		for (int i=0;i<num_sample;++i){
	//			vSamples.add( (int) Math.floor(p) );
	//			p+=stepSize;
	//		}
	//	}
	//	

	public static class LowVarSampleSeq implements Iterable<Integer>,
			Iterator<Integer> {
		int range_ = -1;
		int num_sample_ = -1;
		double p_;
		double step_size_;

		public LowVarSampleSeq(int range, int num_sample) {
			num_sample_ = num_sample;
			range_ = range;
			
			if (num_sample < range) {
				step_size_ = (double) range / (double) num_sample;
				p_ = (FRand.drawDouble() - 1) * step_size_;
			} 
			else {			
				step_size_ = 1.0;			
				p_ = -1.0;		
			}
			return;
		}

		public Iterator<Integer> iterator() {
			return this;
		}

		public void remove() {}

		public Integer next() throws NoSuchElementException {
			return (int) Math.floor(p_);
			//if ( x==null )        throw new NoSuchElementException();
		}

		public boolean hasNext() {
			p_ += step_size_;
			return p_ < range_;
		}
	}

	public static LowVarSampleSeq lowVarSample(int range, int num_sample) {
		return new LowVarSampleSeq(range, num_sample);
	}

	public static void main(String[] args) {
		for (int i = 0; i < 2; ++i) {
			for (int j : lowVarSample(100, 7))
				System.out.print(" " + j);

			System.out.println("");
		}
	}

}
