package edu.cmu.lti.algorithm.optimization.lbfgs;

import java.util.LinkedList;

import edu.cmu.lti.algorithm.FArrayD;
import edu.cmu.lti.algorithm.container.VecArrayD;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.EvalLBFGS;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.IFunction;
import edu.cmu.lti.util.html.EColor;
import edu.cmu.lti.util.html.EColorScheme;
import edu.cmu.lti.util.html.FHtml;
import edu.cmu.lti.util.html.HtmlPage;
import edu.cmu.lti.util.run.Learner;

/**
 * original @author Dan Klein
 * support incrementally adding new features
 * @author Ni Lao 
 */
public abstract class LBFGS {//implements IFunction {
	public double EPS = 1e-10;

	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public double eps_converge = 1e-5;
		//public  double iniStepAdapt = 0.01;
		public double step_adapt = 0.1;//was 0.5

		public int max_train_iteration = 50; //was 20
		public int min_train_iteration = 10;
		public int num_history = 5;
		public int num_stable = 3;

		public double maxLinfStep = 1.0;
		public double maxL1Step = 100.0;
		public double step0 = 1;

		public boolean bPrintLBFGSDir = false;
		public double epsLS = 0.01;

		public boolean LBFGS_silent = false;

		public Param(Class c) {
			super(c);//MaxEntParser.class);
			parse();
		}

		public String code;
		public boolean LBFGS_history;

		public void parse() {
			epsLS = getDouble("epsLS", 0.01);
			step0 = getDouble("step0", 1.0);

			LBFGS_history = getBoolean("LBFGS_history", false);
			LBFGS_silent = getBoolean("LBFGS_silent", false);

			eps_converge = getDouble("eps_converge", 0.0001);
			step_adapt = getDouble("step_adapt", 0.1);
			maxL1Step = getDouble("maxL1Step", 100.0);
			maxLinfStep = getDouble("maxLinfStep", 5.0);
			min_train_iteration = getInt("min_train_iteration", 5);
			max_train_iteration = getInt("max_train_iteration", 100);
			num_history = getInt("num_history", 10);
			num_stable = getInt("num_stable", 3);

			bPrintLBFGSDir = getBoolean("bPrintLBFGSDir", false);
			//code ="_it"+minItTrain+"-"+maxItTrain;
			code = "mIt=" + max_train_iteration
				+ String.format("_eps%.0e", eps_converge)
				+ String.format("_mL1=%.0f", maxL1Step)
				+ String.format("_mLi=%.0f", maxLinfStep);
			
			//code+=String.format("_cvg%.0e"	,epsCvg);	
		}
	}
//	public LineSearcherBT searcher = null;

	VecArrayD X_history_ = new VecArrayD();
	VecArrayD G0_history_ = new VecArrayD();
	VectorD step_history_ = new VectorD();

	public void printHistory(String folder, VectorS vsName) {//, double x[]){
		printHistory(folder, "lbfgs.history", vsName);
	}

	public void printHistory(String folder, String fn, VectorS vsName) {//, double x[]){
		HtmlPage th = new HtmlPage(fn, folder);
		printHistory(th, vsName);
		th.close();
	}

	double epsPrintG = 1e-3;

	public void addStepSizeRow(HtmlPage page) {
		String pink = FHtml.backGround(EColor.lightpink);
		page.clearStyledRow();
		page.addStyledCell(null, null);
		page.addStyledCell("stepSize", null);
		for (int i = 0; i < step_history_.size(); ++i) {
			double step = step_history_.get(i);
			page.addStyledCell(String.format("%.2f", step), (step == 1.0) ? null
							: pink);
		}
		page.writeStyledRow();
		//th.addRow("\tStep\t"+this.vStepHist.join("\t"),FHtml.backGround(EColor.cadetblue));
	}

	public void printHistory(HtmlPage th, VectorS vsName) {//, double x[]){
		int n = G0_history_.lastElement().length;
		String blue = FHtml.backGround(EColor.lightsteelblue);
		String title = "ID\tName\t" + VectorI.seq(1, X_history_.size()).join("\t")
				+ "\tName";
		th.newTable("lbfgs history", title);

		addStepSizeRow(th);

		EColorScheme cs = EColorScheme.RdBu7;
		cs.setLogRange(-6, -3, 3);
		//cs.setRange(-epsPrintG,epsPrintG);

		for (int i = 0; i < n; ++i) {
			VectorD vG0 = G0_history_.getRow(i);
			VectorD vX = X_history_.getRow(i);

			//th.addRow(vsRow)
			th.clearStyledRow();
			th.addCell("" + (i + 1));
			th.addCell(vsName.get(i));

			for (int j = 0; j < X_history_.size(); ++j) {
				Double x = vX.getD(j, null);
				String txt = (x != null) ? String.format("%.1f", x) : "";
				th.addCell(txt);
			}
			th.addCell(vsName.get(i));
			th.addStyledRow(blue);

			th.clearStyledRow();
			th.addStyledCell(null, null);
			th.addStyledCell(null, null);

			for (int j = 0; j < G0_history_.size(); ++j) {
				Double g = vG0.getD(j, null);
				String txt = "";
				String style = null;
				if (g != null) {
					if (g != 0) txt = String.format("%1.0e", -g);
					style = cs.getBg(-g);
				}
				th.addStyledCell(txt, style);
			}
			th.writeStyledRow();
		}

		addStepSizeRow(th);
		th.addTitleRow();
		th.endTable();
	}


	public Param p = null;

	public LBFGS() {
		p = new Param(LBFGS.class);
		//this.funIterCallback = fun;
//		searcher = new LineSearcherBT();// false);
//		searcher.epsLS = p.epsLS;
	}


	LinkedList<double[]> vdX = new LinkedList<double[]>();
	LinkedList<double[]> vdG = new LinkedList<double[]>();

	//IterCallbackFun funIterCallback = null;

	public void clearHistory() {
		vdX.clear();
		vdG.clear();
		this.G0_history_.clear();
		this.step_history_.clear();
		this.X_history_.clear();
	}

	// double[] iniX  
	public int minimize(IFunction fun, EvalLBFGS e, double[] x, boolean induction) {//, double tolerance) {
		nStable = 0;
		if (e == null) {
			if (x == null) x = new double[0];
			e = fun.evaluate(x, false);
		}

		int it = 0;
		for (; it < p.max_train_iteration; it++) {
			double[] dir = getDir(e.dim, e.g);
			//      System.out.println(" Derivative is: "+DoubleArrays.toString(derivative, 100));
			//      DoubleArrays.assign(direction, derivative);
			FArrayD.scale(dir, -1.0);
			//      System.out.println(" Looking in direction: "+DoubleArrays.toString(direction, 100));

			EvalLBFGS e1 = search(fun, e, dir, false, induction);

			Learner.printf("\tIt= %d y=%.6f", it, e1.y);

			if (it >= p.min_train_iteration && converged(e.y, e1.y, p.eps_converge)) return it;

			updateHistories(e, e1);
			e.copy(e1);
			//if (funIterCallback != null)     funIterCallback.onIter(e.x,it);

		}
		//System.err.println("LBFGSMinimizer.minimize: Exceeded maxIterations without converging.");
		return it;
	}

	protected boolean converged(double d, double d1, double tol) {
		double diff = FMath.abs(d - d1);
		double avg = FMath.abs(d + d1 + EPS) / 2.0;
		return (diff / avg < tol);
	}//epsCvg

	int nStable = 0;

	protected boolean converged(EvalLBFGS e, EvalLBFGS e1, double tol) {
		//if (e.y ==e1.y)    return true;
		if (converged(e.y, e1.y, tol)
		//&& converged(e.xL1,e1.xL1,tol)
		//&& converged(e.nonZeroDim,e1.nonZeroDim,tol)
		) ++nStable;
		else nStable = 0;
		return nStable >= p.num_stable;
	}

	protected void updateHistories(EvalLBFGS e, EvalLBFGS eNew) {
		push(FArrayD.minus(eNew.x, e.x), vdX);
		push(FArrayD.minus(eNew.g0, e.g0), vdG);
	}

	protected void push(double[] v, LinkedList<double[]> list) {
		list.addFirst(v);
		if (list.size() > p.num_history) list.removeLast();
	}

	protected int historySize() {
		return vdX.size();
	}

	protected double[] implicitMultiply(double[] IHD, double[] derivative) {
		double[] rho = new double[historySize()];
		double[] alpha = new double[historySize()];
		double[] right = FArrayD.clone(derivative);

		// loop last backward
		for (int i = historySize() - 1; i >= 0; i--) {
			double[] dX = vdX.get(i);//getXDiff(i);
			double[] dG = vdG.get(i);//getGDiff(i);
			rho[i] = FArrayD.inner(dX, dG);
			if (rho[i] == 0.0) {
				//throw new RuntimeException("LBFGS.implicitMultiply: Curvature problem.");
				System.err.println("LBFGS.implicitMultiply: Curvature problem.");
				return null;
			}

			alpha[i] = FArrayD.inner(dX, right) / rho[i];
			right = FArrayD.addMultiples(dG, -alpha[i], right, 1.0);
		}

		double[] left = FArrayD.multiply(IHD, right);
		for (int i = 0; i < historySize(); i++) {
			double[] xDiff = vdX.get(i);// getXDiff(i);
			double[] gDiff = vdG.get(i);// getGDiff(i);
			double beta = FArrayD.inner(gDiff, left) / rho[i];
			left = FArrayD.addMultiples(xDiff, alpha[i] - beta, left, 1.0);
		}
		return left;
	}

	protected double[] getDir(int dim, double[] g) {
		double[] IHD = getIniIHD(dim);
		double[] dir = implicitMultiply(IHD, g);
		return dir;
	}

	//getInitialInverseHessianDiagonal(int)
	protected double[] getIniIHD(int dimension) {
		double scale = 1.0;
		if (vdG.size() >= 1) {
			double[] dG1 = vdG.getFirst();//getLastGDiff();
			double[] dX1 = vdX.getFirst();//getLastXDiff();
			double num = FArrayD.inner(dG1, dX1);
			double den = FArrayD.inner(dG1, dG1);
			scale = num / den;
		}
		return FArrayD.repeat(scale, dimension);
	}

	//public double epsLS = 1e-2;
  public double stepAdapt = 0.3;//was 0.1;
  public double epsDecrease = 1e-3;//0.9;
  public double step0 =1.0;// 0.5;//was 1.0
  public double step = 1.0;//was 1.0
  
  public EvalLBFGS search(IFunction fun,EvalLBFGS eIni 
  		,double[] dir, boolean bProj, boolean bInduce) {
  	//double[] iniX, 
    double initG = FArrayD.inner(eIni.g, dir);
    double[] x = null;
   
    double Linf= FArrayD.normInf(dir);
    if (Linf> p.maxLinfStep){
    	Learner.printf("\tdir/=(Li=%.1f)\n", Linf);
    	FArrayD.scale(dir,p.maxLinfStep/Linf);
    	//FArrayD.decapitate(dir,LBFGS.p.maxLinfStep);
    }
  	//FArrayD.removeSmallElements(dir,0.1);	    	
    
    
    double L1= FArrayD.norm1(dir);
    if (L1> p.maxL1Step){
    	Learner.printf("\tdir/=(L1=%.1f)\n", L1);
    	FArrayD.scale(dir,p.maxL1Step/L1);
    }
    
    EvalLBFGS e=null;
    //stepSize*=1.4;
    step=step0;
    for (int i=0;true; ++i) {
    	if (!p.LBFGS_silent) 		Learner.printf(" %.0e", step);

      x = FArrayD.addMultiples(eIni.x, 1.0, dir, step);
      if (bProj) FArrayD.project2(x, eIni.x); //keep the guess within the same orthant
      e = fun.evaluate(x,false);
      double delta=epsDecrease * initG * step;
      if (e.y <= eIni.y - delta  ){//+0.1
      	//System.out.printf("line search %s\n",e.toString());
      	if ( bInduce)
          e = fun.evaluate(x,true);
      	return e;
      }
      if (step<0) break;
      if (step <= p.epsLS)      	
      	step=-step;
      else
      	step *= stepAdapt;
      
    }
	  //throw new RuntimeException("");
    Learner.println(	"LineSearcherBT.minimize: stepSize underflow.");	  
	  return e;//eIni;
  }
}
