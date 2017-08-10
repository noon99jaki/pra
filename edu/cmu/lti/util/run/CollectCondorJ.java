package edu.cmu.lti.util.run;

import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

public class CollectCondorJ extends Tunner{
	public CollectCondorJ(){//Class c){
		super(CollectCondorJ.class);//c);
	}
	int id=0;
	public void collect(){
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
				
			String rlt= FFile.loadLastLine(keyS+"/condor.out");
			FFile.saveString(p.score_folder+key, rlt);
			
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
