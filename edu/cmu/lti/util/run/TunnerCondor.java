package edu.cmu.lti.util.run;

import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

public class TunnerCondor extends Tunner{
	public TunnerCondor(){//Class c){
		super(TunnerCondor.class);//c);
	}
	public void tune(){
		sweepRecur(new VectorI(param_list_.size()), 0);
		System.out.println("done");
		return;
	}
	
	/* sweep the ip-th parameter
	 */
	private void sweepRecur(VectorI v, int ip){
		
		if (ip== param_list_.size()){
			String key=getPostLoadString(v);			
			//String key1= key.replaceAll("=", "_").replaceAll(",", "_");
			String keyS= "jobs/"+getPostLoadStringS(v);	
				//+vp.getVS(CTag.value,v).join("_");
				
			FFile.mkdirs(keyS);
			FString.toVS(key,",").save(keyS+"/grid");

			String job= FFile.loadString("../job.tune"+p.memory_code);
			job= job.replace("condor.", keyS+"/condor.");
			job= job.replace("tune.pl"
					, "tune.pl fpTunnerGrid="+keyS+"/grid"
					 +",memoryCode="+p.memory_code);
			
			
			FFile.saveString(job, keyS+"/job.tune");
			FSystem.cmd("condor_submit "+keyS+"/job.tune");
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
			(new TunnerCondor()).tune();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
