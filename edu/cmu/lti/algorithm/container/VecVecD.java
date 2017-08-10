package edu.cmu.lti.algorithm.container;

import java.util.Collection;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

public class VecVecD extends VectorX<VectorD> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public VecVecD newInstance() {
		return new VecVecD();
	}

	//TVectorMapIX<Double> {
	public VecVecD() {
		super(VectorD.class);
		//super(Double.class);
	}

	public VecVecD(int n, VectorD v) {
		super(n, v);
		//super(Double.class);
	}

	public VecVecD add(double[] vd) {
		this.add(new VectorD(vd));
		return this;
	}

	public VectorD multiply(double[] vd) {
		VectorD vRlt = new VectorD();
		vRlt.ensureCapacity(size());
		for (VectorD v : this) {
			FSystem.checkVectorSizes(v.size(), vd.length);
			vRlt.add(v.inner(vd));
		}
		return vRlt;
	}

	public VecVecD multiplyOn(double d) {
		for (VectorD v : this)
			v.multiplyOn(d);
		return this;
	}

	public VecVecD plusOn(VecVecD vv) {
		FSystem.checkVectorSizes(size(), vv.size());
		for (int i = 0; i < vv.size(); ++i)
			get(i).plusOn(vv.get(i));

		return this;
	}

	public VecVecD plusOnE(VecVecD vv) {

		for (int i = 0; i < vv.size(); ++i)
			getE(i).plusOnE(vv.get(i));

		return this;
	}

	public void setSizeLowerTriangle(int n) {
		this.setSize(n);
		for (int i = 1; i <= n; ++i) {
			VectorD v = new VectorD();
			v.setSize(i);
			this.set(i - 1, v);
		}
	}

	public void set(int i, int j, Double x) {
		get(i).set(j, x);
	}

	public Double get(int i, int j) {
		return get(i).get(j);
	}

	public VectorD getCol(int iCol) {
		VectorD v = new VectorD();
		v.ensureCapacity(size());
		for (VectorD x : this)
			v.add(x.get(iCol));
		return v;
	}

	public static VecVecD fromFile(String fn, String sep, boolean bSkipTitle) {
		VecVecD.sep = sep;
		VecVecD vd = new VecVecD();
		vd.loadFile(fn, bSkipTitle);
		return vd;
	}

	public void loadFileTp(String fn, String sep, boolean bSkipTitle) {
		for (VectorS vs : FFile.enuRows(fn, sep, bSkipTitle)) {
			if (size() == 0) init(vs.size());
			VectorD vd = new VectorD();
			vd.load(vs);
			cat2(vd);
		}
	}

	//load transposed
	public static VecVecD fromFileTp(String fn, String sep, boolean bSkipTitle) {
		VecVecD vd = new VecVecD();
		vd.loadFileTp(fn, sep, bSkipTitle);
		return vd;
	}

	static String sep = null;

	public VectorD parseLine(String line) {
		VectorD vd = new VectorD();
		vd.load(line, sep);
		return vd;
	}

	//assuming the matrix consists of row vectors
	// the 1st direction is the vertical direction.
	public VecVecD cat1(VecVecD vv) {
		addAll(vv);
		return this;
	}

	//assuming the matrix consists of row vectors
	// the 2nd direction is the horizontal direction.
	public VecVecD cat2(VecVecD vv) {
		if (size() == 0) init(vv.size());
		FSystem.checkVectorSizes(size(), vv.size());

		for (int i = 0; i < size(); ++i)
			getC(i).addAll(vv.get(i));
		return this;
	}

	public VecVecD cat2(VectorD v) {
		if (size() == 0) init(v.size());
		FSystem.checkVectorSizes(size(), v.size());

		for (int i = 0; i < size(); ++i)
			getC(i).add(v.get(i));
		return this;
	}

	public VecVecD transpose() {
		VecVecD vv = new VecVecD();
		for (VectorD v : this)
			vv.cat2(v);
		return vv;
	}

	public VecVecD subRR(int nGroup, int iStart) {
		return (VecVecD) super.subRR(nGroup, iStart);
	}

	public VecVecD sub(Collection<Integer> vi) {
		return (VecVecD) super.sub(vi);
	}

}
