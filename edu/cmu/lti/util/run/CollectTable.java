package edu.cmu.lti.util.run;

import edu.cmu.lti.algorithm.Interfaces.CTag;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.util.file.FFile;

//collect and organize scores into a table from folder <task>.scores
//in order according to the grid file
public class CollectTable extends Tunner {
//public TVector<Parameter> vp=null;// new TVector<Parameter>(Parameter.class);
	
	//public Param p;
	public CollectTable(){
		super(CollectTable.class);
		//p.parseCols(vp);
	}
	
	
	private void sweepRecur(VectorI v, int ip){
		if (ip== param_list_.size()){
			String key=getPostLoadString(v);
			String score=  super.collectAScore(key+",id=0");
			mRlt.put(key,score);
			return;
		}		
		Parameter pa = param_list_.get(ip);
		for (int i=0; i<pa.num_grids;++i){
			v.set(ip,i);
			sweepRecur(v,ip+1);
		}	
	}
	//TMapSX< VectorVectorS> mvv= new TMapSX< VectorVectorS>(VectorVectorS.class);
	MapSS mRlt= new MapSS();
	String fdOut="collectedTables/";

	public void collectAll(){
		FFile.mkdirs(fdOut);
		String title= FFile.loadString(p.task+".title").trim();
	
		sweepRecur(new VectorI(param_list_.size()), 0);
		String code=param_list_.getVS(CTag.toString).join(",");
		mRlt.save(code, "\t", title);			


		return;	
	}
	public static void main(String[] args) {		
		try {
			Param.overwriteFrom("conf");
			if (args.length>0)
				Param.overwrite(args[0]);
			
			(new CollectTable()).collectAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
