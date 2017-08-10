package edu.cmu.lti.util.run;

import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

/**
 * To achieve flexible #averaging, 
 * and reserve >2.5G memory for JVM 
 * instead of sending tune.pl jobs, send go.pl jobs
 *  
 * @author nlao
 *
 */
public class TunnerCondorJ  extends Tunner{
	public TunnerCondorJ(){//Class c){
		super(TunnerCondorJ.class);//c);
	}
	int id=0;
	public void tune(){
		for (id=p.starting_id; id<p.starting_id+ p.num_avarage; ++id)
			sweepRecur(new VectorI(param_list_.size()), 0);
		System.out.println("done");
		return;
	}
	
	/* sweep the ip-th parameter
	 */
	private void sweepRecur(VectorI v, int ip){
		
		if (ip== param_list_.size()){
			String ID= ",id="+id;
			String key=getPostLoadString(v)+ID;			
			String keyS= "jobs/"+getPostLoadString(v)+"/"+ID;	
				
			FFile.mkdirs(keyS);
			//FFile.saveString(VectorS.parse(key,",").join("\n"), keyS+"/tunner.grid");

			String job= FFile.loadString("../job.go"+p.memory_code);
			job= job.replace("condor.", keyS+"/condor.");
			
			FFile.saveString(keyS+"/job.go", job);
			FSystem.cmd("condor_submit "+keyS+"/job.go");
			//FSystem.cmd("condor_submit ../../job.tune",key);
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
			(new TunnerCondorJ()).tune();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}

