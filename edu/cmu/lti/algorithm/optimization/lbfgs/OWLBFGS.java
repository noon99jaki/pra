/**
 * 
 */
package edu.cmu.lti.algorithm.optimization.lbfgs;

import edu.cmu.lti.algorithm.FArrayD;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.EvalLBFGS;
import edu.cmu.lti.algorithm.optimization.lbfgs.Interfaces.IFunction;
import edu.cmu.lti.util.run.Learner;

/**
 * @author petrov
 * Orthant-Wise L-BFGS * 
 */
public class OWLBFGS extends LBFGS  {

	public OWLBFGS(){	}
	
	public int minimize(IFunction fun, EvalLBFGS e, double[] x
			,boolean bInduce){//, double tolerance) {
		this.nStable=0;
		clearHistory();
		
		if (e==null){
			if (x==null)
				x=new double[0];
			e=fun.evaluate(x,false);
		}

    epsDecrease = 0;
    int it = 0;
    int maxIt= p.max_train_iteration;
    if (bInduce) maxIt/=2;
    for (; it < maxIt; it++) {
    	if (!p.LBFGS_silent)
    		Learner.printf("It=%d\n", it);

      if (it>=1) step0=p.step0;
    	
      double[] dir = getDir(e.dim, e.g);    
      if (dir==null) {
      	//this.clearHistory();  	continue;
      	return -1;//e;
      }
      e.dir=dir;
      //double[] orthant = getOrthant(e.x, e.g);
      FArrayD.project(dir, e.g);//orthant);//
      //p^k: project search direction onto orthant defined by gradient
      FArrayD.scale(dir, -1.0);

      stepAdapt = p.step_adapt;
      EvalLBFGS eNew = search(fun, e, dir,true,bInduce);//, true);
      
  		if (p.LBFGS_history){
	      X_history_.add(e.x);
	      G0_history_.add(e.g0);//vIT.add(it);
	      step_history_.add(step);
  		}  
  		
      if (eNew==null) {
      	//this.clearHistory();  	continue;
      	return -1;//e;
      }
      
      //System.out.printf("It=%d %s\n", it, eNew);
      if (eNew==e)
      	continue;//  	this.clearHistory();
      
     	if (it >= p.min_train_iteration && converged(e, eNew, p.eps_converge)){
     		
     		if (!p.LBFGS_silent)
     			Learner.printf(" owLBFGS converged at it=%d with eps<%.0e\n"
        		, it, p.eps_converge );
     		return it;//eNew;      
     	}
      // update with unregularized derivatives!
      updateHistories(e, eNew);
      e.copy(eNew);
      //if (funIterCallback != null)      funIterCallback.onIter(e.x,it);
      
    }
    Learner.printf( "LBFGS.minimize: Exceeded maxIt="+p.max_train_iteration+"\n");
    return it;//e;
  }

	private double[] getOrthant(double[] iniX, double[] g) {
		double[] o=new double[g.length];
		for (int i=0; i<iniX.length; i++) 
			if (iniX[i]!=0) 
				o[i] = Math.signum(iniX[i]);
			else 
				o[i] = Math.signum(-g[i]);		
		
		for (int i=iniX.length; i<g.length; i++) 
				o[i] = Math.signum(-g[i]);
		
		return o;
	}

}
