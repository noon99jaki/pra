package edu.cmu.lti.algorithm.learning.classifier;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.learning.Learner;
import edu.cmu.lti.algorithm.learning.Interfaces.IEva;
import edu.cmu.lti.algorithm.learning.data.Instance;

/**
 * for multi-class classification
 * @author nlao
 *
 */
public abstract class Classifier extends Learner {
	
	public static class Eva implements IEva {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public Eva clone() {
			// EvaClassify e = (EvaClassify)super.clone();
			Eva e =new Eva();
			e.acc = acc;
			e.sacc = sacc;
			return e;
			//return (new Eva()).copy(this);
		}

/*		public Eva copy(Eva e) {
			this.acc = e.acc;
			this.sacc = e.sacc;
			return this;
		}*/

		public Eva() {}

		public Eva plusObjOn(Object x) {
			if (x == null) return this;
			Eva e = (Eva) x;
			acc += e.acc;
			sacc += e.sacc;
			return this;
		}

		public Eva multiplyOn(Double x) {
			acc *= x;
			sacc *= x;
			return this;
		}
		public double acc; // hard evaluation
		public double sacc; // soft evaluatio,p(target)

		public String toString() {
			return String.format("%.2f\t%.2f", acc, sacc);
		}

		public String title() {
			return "Acc\tSAcc";
		}

		public String print() {
			return String.format("Acc=%.2f\tSAcc=%.2f", acc, sacc);
		}

		// assume mSystem is normalized to be |mSystem|=1 ?
		public Eva evaluate(MapID mSystem, int gold) {
			int system = mSystem.idxMax();
			if (system == gold) acc = 1.0;
			else acc = 0.0;
			sacc = mSystem.normalizeOn().get(system);
			return this;// eva;
		}

		public Eva evaluate(int system, int gold) {
			if (system == gold) acc = 1.0;
			else acc = 0.0;
			sacc = acc;
			return this;
		}
	}


	public Instance newInstance(){
		return new Instance();
		//Instance ins = new Instance();
	}


	public Eva evaluate(Instance ins1, Instance ins2) {
		return null;
		//(new Eva()).evaluate(	((Instance) ins1).label, ((Instance) ins2).label);
	}
	/*
	 * //protected boolean public DataSet(boolean bIndexLabel, boolean
	 * bIndexFeature){ if (bIndexLabel) idxLabel = new Index(); if (bIndexFeature)
	 * idxFeature = new Index();
	 *  } public DataSet(Index idxLabel, Index idxFeature){ this.idxLabel =
	 * idxLabel; this.idxFeature = idxFeature; }
	 */
	
	
	 
}
