package edu.cmu.lti.util.run;

import java.util.Map;

import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

//collect and organize scores into a table from folder <task>.scores
//in order according to the grid file
//output for all measures
public class CollectTable1D extends Tunner {
	public CollectTable1D(){
		super(CollectTable1D.class);
		parseCols();
		if (p.col1_==-1) FSystem.die("col1 not found in grid="+p.col1);

	}
	
	//TMapSX< VectorVectorS> mvv= new TMapSX< VectorVectorS>(VectorVectorS.class);
	// code-->col1-->metrics
	MapSX< VectorS> mvRlt= new MapSX< VectorS>(VectorS.class);
	//MapSS mRlt= new MapSS();
	MapSX< VectorD> mvScore= new MapSX< VectorD>(VectorD.class);

	private void sweepRecur(VectorI v, int ip){
		if (ip== param_list_.size()){
			String key=getPostLoadString(v);
			String score=  super.collectAScore(key+",id=0");
			if (score==null) return ;
			
			String code= getPostLoadStringSkipCol1(v);
			int iValue1= v.get(p.col1_);
			mvRlt.getC(code).add( param1.getValue(v.get(p.col1_))+"\t"+ score);
			mvScore.getC(code).plusOnE(VectorD.fromLine(score, "\t"));
			return;
		}		
		Parameter pa = param_list_.get(ip);
		for (int i=0; i<pa.num_grids;++i){
			v.set(ip,i);
			sweepRecur(v,ip+1);
		}	
	}
	
	String fdOut="collectedTables1D/";
	public void collectAll(){
		FFile.mkdirs(fdOut);
		
		String title= "\t"+	FFile.loadString(p.task+".title").trim();

		sweepRecur(new VectorI(param_list_.size()), 0);
		
		System.out.println("setting"+title);
		VectorS vs= new VectorS();
		vs.add("setting"+title);
		
		for ( Map.Entry<String,VectorS> e : mvRlt.entrySet() ) {
			int n=e.getValue().size();
			VectorD vd= mvScore.get(e.getKey()).devideOn(n);
			String txt=e.getKey()	+"\t"+vd.join3f()+"\t"+n;
			vs.add(txt);
			System.out.println(txt);
			
			e.getValue().saveWithTitle(fdOut+e.getKey(),param1.name+title);
		}
		vs.save(fdOut+"sum");
		


		return;	
	}
	public static void main(String[] args) {		
		try {
			Param.overwriteFrom("conf");
			if (args.length>0)
				Param.overwrite(args[0]);
			
			(new CollectTable1D()).collectAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

}
