package edu.cmu.lti.algorithm.ir.eva;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.IMultiplyOn;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.system.FSystem;

public class IREvaluation implements Serializable, IPlusObjOn, IMultiplyOn {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public double map_ = 0;
	public double precision_at_K_ = 0; //precision at K=
	public double num_results_ = 0;
	public double num_relevant_ = 0;
	public double mrr_ = 0;
	public double num_ = 0;

	public void clear() {
		map_ = 0;
		precision_at_K_ = 0;
		//prec=0;		recall=0;
		num_results_ = 0;
		num_relevant_ = 0;
		mrr_ = 0;
		num_ = 0;
	}

	// an ordered list of ids

	private static VectorI results_ = new VectorI();

	//TODO: not thread safe
	public void evaluateStatic(MapID results, SetI good) {
		//VectorI vi=(VectorI) mSys.toVectorKeySortedByValueDesc();
		results.KeysToVecSortByValueDesc(results_);
		evaluate(results_, good);
	}

	public void evaluate(MapID results, SetI good) {
		VectorI vi = (VectorI) results.KeyToVecSortByValue(true);
		evaluate(vi, good);
	}
	public void evaluate(VectorD scores, SetI good) {
		VectorI sorted= scores.sortId(true);
		evaluate(scores.sortId(true), good);
	}
	public void evaluate(VectorI results, SetI good) {
		clear();
		double precision = 0;
		double recall = 0;

		FSystem.checkTrue(good.size() > 0, "mGold.size()==0");

		num_ = 1;
		for (int id : results) {
			++num_results_;
			if (good.contains(id)) {
				if (num_relevant_ == 0.0) mrr_ = 1.0 / num_results_;

				++num_relevant_;
				precision = num_relevant_ / num_results_;
				map_ += precision;
			} else precision = num_relevant_ / num_results_;

			if (num_results_ == good.size()) precision_at_K_ = precision;

		}
		map_ /= good.size();

		recall = num_relevant_ / good.size();

		if (results.size() < good.size()) precision_at_K_ = num_relevant_
				/ good.size();
		return;
	}

	public static String title() {
		return "#R\tmrr\tMAP\tp@K";//\t#Rel\t#Ret";
	}

	public String print() {
		return String.format("%.1f\t%.3f\t%.3f\t%.3f",
				num_results_, mrr_, map_, precision_at_K_//,nRel, nRet
				);
	}

	public String toString() {
		return print();
	}

	public IREvaluation plusObjOn(Object x) {
		if (x == null) return this;
		return plusOn((IREvaluation) x);
	}

	public IREvaluation plusOn(IREvaluation x) {
		map_ += x.map_;
		precision_at_K_ += x.precision_at_K_;
		//prec += e.prec;		recall += e.recall;
		num_results_ += x.num_results_;
		num_relevant_ += x.num_relevant_;
		mrr_ += x.mrr_;
		num_ += x.num_;
		return this;
	}

	public IREvaluation multiplyOn(Double x) {
		map_ *= x;
		precision_at_K_ *= x;
		//prec*=x;		recall*=x;
		num_results_ *= x;
		num_relevant_ *= x;
		mrr_ *= x;
		num_ *= x;
		return this;
	}

	public IREvaluation meanOn() {
		if (num_ != 0.0) multiplyOn(1.0 / num_);
		return this;
	}
	
	
	public static void evalScoredItems(String result_file) {
		VectorD scores = new VectorD();
		SetI good = new SetI();
		scores.ensureCapacity(1000);
		for (VectorS row: FFile.enuRows(result_file)) {
			double score = Double.parseDouble(row.get(0));
			if (score==0.0) {	// skip results with zero scores
				if (row.get(1).equals("+")) good.add(-1-good.size()); 
			}
			else {
				if (row.get(1).equals("+")) good.add(scores.size()); 
				scores.add(score);
			}
		}
		IREvaluation eval = new IREvaluation();
		eval.evaluate(scores, good);
		System.out.println(IREvaluation.title());
		System.out.println(eval.print());
		FFile.appendToFile(result_file + "\t" + eval.print() +"\n", "IREvaluation");
	}
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		String task = args[0];
		if (task.equals("evalScoredItems")) evalScoredItems(args[1]);

		else FSystem.die("unknown task=" + task);
	}
}
