package edu.cmu.lti.algorithm.ir.eva;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapSMapSD;
import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;

public class ClassificationEval implements Serializable, IPlusObjOn,
		IMultiplyOn, ICloneable, Cloneable {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public ClassificationEval clone() {
		try {
			ClassificationEval e = (ClassificationEval) super.clone();
			e.accuracy = accuracy;
			e.sacc = sacc;
			//return (new EvaClassify()).copy(this);
			return e;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public ClassificationEval() {}

	public ClassificationEval(boolean same) {
		if (same) accuracy = 1.0;
		else accuracy = 0.0;
	}

	public ClassificationEval plusObjOn(Object x) {
		if (x == null) return this;
		ClassificationEval e = (ClassificationEval) x;
		accuracy += e.accuracy;
		sacc += e.sacc;
		return this;
	}

	public ClassificationEval multiplyOn(Double x) {
		accuracy *= x;
		sacc *= x;
		return this;
	}
	public double accuracy; //hard evaluation
	public double sacc; //soft evaluatio,p(target)

	public String toString() {
		return String.format("%.2f\t%.2f", accuracy, sacc);
	}

	public String title() {
		return "Acc\tSAcc";
	}

	public String print() {
		return String.format("Acc=%.2f\tSAcc=%.2f", accuracy, sacc);
	}

	//assume mSystem is normalized to be |mSystem|=1 ?
	public void evaluate(MapSD mSystem, String gold) {
		String choice = mSystem.idxMax();
		if (choice.equals(gold)) accuracy = 1.0;
		else accuracy = 0.0;
		sacc = mSystem.normalizeOn().get(choice);
		return;// eva;	
	}

	public void evaluate(MapID mSystem, int gold) {
		int choice = mSystem.idxMax();
		if (choice == gold) accuracy = 1.0;
		else accuracy = 0.0;
		sacc = mSystem.normalizeOn().get(choice);
		return;// eva;	
	}

	public static MapSMapSD confusionMatrix(VectorS vSys, VectorS vGold) {
		MapSMapSD mm = new MapSMapSD();
		//VectorMapSD vm = new VectorMapSD();
		for (int i = 0; i < vGold.size(); ++i) {
			mm.plusOn(vGold.get(i), vSys.get(i), 1.0);
		}
		return mm;
	}

	public static ClassificationEval evaluate(VectorS vSys, VectorS vGold) {
		VectorX<ClassificationEval> vEva = new VectorX<ClassificationEval>(
				ClassificationEval.class);
		for (int i = 0; i < vGold.size(); ++i)
			vEva.add(new ClassificationEval(vSys.get(i).equals(vGold.get(i))));
		return vEva.mean();
	}
}
