package edu.cmu.lti.util.run;

import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.util.system.FSystem;


/**
 * Brutal force method to try all combination of parameters
 * want averageASetting() to be multithreaded?
 * @author nlao
 *
 */public class TunnerSweep extends Tunner{
	public TunnerSweep(){//Class c){
		super(TunnerSweep.class);//c);
	}
	public void tune(){
		//if (p.nWorkingThread>1)
		StopWatch sw= new StopWatch();
		FSystem.printMemoryTime();
		
		pool_.startThreads(p.num_processes);

		sweepRecur(new VectorI(param_list_.size()), 0);
		
		pool_.waitJobs();
		pool_.killThreads();
		//FFile.appendToFile(getStatus(), p.cache+".SweepScores");
		System.out.println("done");
		
		FSystem.printMemoryTime();
		sw.printTime("tune()");
		return;
	}
	/*
	 * sweep the sweep-th parameter
	 */
	private void sweepRecur(VectorI v, int ip){
		if (ip== param_list_.size()){
			String key=getPostLoadString(v);			
			averageASetting(key);			
	    return;
		}		
		
		Parameter pa = param_list_.get(ip);
		for (int i=0; i<pa.num_grids;++i){
			v.set(ip,i);
			sweepRecur(v,ip+1);
		}	
	}
	

	public static void main(String[] args) {		
		try {
			Param.overwriteFrom("conf");
			if (args.length>0)
				Param.overwrite(args[0]);
			(new TunnerSweep()).tune();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
