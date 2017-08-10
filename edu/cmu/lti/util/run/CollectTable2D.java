package edu.cmu.lti.util.run;

import java.util.Map;

import edu.cmu.lti.algorithm.container.MapDD;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VecVecS;
import edu.cmu.lti.algorithm.math.rand.MultinomD;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

// collect and organize scores into a table from folder <task>.scores
// in order according to the grid file\
// output for a single measure
public class CollectTable2D extends Tunner {
	public CollectTable2D(){
		super(CollectTable2D.class);
		parseCols();
		if (p.col1_==-1) FSystem.die("col1 not found in grid="+p.col1);
		if (p.col2_==-1) FSystem.die("col2 not found in grid="+p.col2);

	}
	

	// rest of the confs-->col1-->col2
	MapSX< VecVecS> mvv= new MapSX< VecVecS>(VecVecS.class);

	
	public int iValueCol=-1;
	public String collectAScoreCol(String code){
		String row= super.collectAScore(code+",id=0");
		if (row==null) return null;
		return row.split("\t")[iValueCol];
	}
	
	private void sweepRecur(VectorI v, int ip){
		if (ip== param_list_.size()){
			String key=getPostLoadString(v);
			String score= collectAScoreCol(key);
			
			String name= getPostLoadStringSkipCol12(v);
			int iValue1= v.get(p.col1_);
			int iValue2= v.get(p.col2_);
			mvv.getC(name).getE(iValue1).setE(iValue2, score);
			
	    return;
		}		
		

		Parameter pa = param_list_.get(ip);
		for (int i=0; i<pa.num_grids;++i){
			v.set(ip,i);
			sweepRecur(v,ip+1);
		}	
	}
	
	public VectorD vdBaseline=null;// new VectorD();
	
	public void collect(String valueCol){
		String title= FFile.loadString(p.task+".title").trim();
		VectorS vTitle= FString.splitVS(title,"\t");	
		iValueCol= vTitle.idxLast(valueCol);//MAP mrr
		if (iValueCol==-1){
			FSystem.die("cannot find col="+valueCol+" in\n"+vTitle);
		}
		
		if (p.fnBaseline!=null)
			vdBaseline= FTable.loadAColumn(
				p.fnBaseline, iValueCol+1, true).toVD();//skip the first col

		sweepRecur(new VectorI(param_list_.size()), 0);
		
		VectorD vdAvg=new VectorD();
		
		for (Map.Entry<String,VecVecS> e: mvv.entrySet()){// for each setting
			System.out.println(valueCol +"-->"+e.getKey());
			
			VectorS vsRlt= new VectorS();
			vsRlt.add(e.getKey() + "\t"+ param2.grids.join("\t"));
			

			
			vdAvg.clear();
			VecVecS vvs=e.getValue();
			if (vvs==null){
				System.err.print("cannot find results for "+e.getKey());
				continue;
			}
				
			VectorX<MapDD> vmdd=p.fnBaseline==null?
					null:new VectorX<MapDD>(MapDD.class);
			for (int i=0;i< vvs.size();++i){ // for each col1 value
				VectorS vs=vvs.get(i);
				vsRlt.add( param1.getValue(i)+"\t"+vs.join("\t"));
				VectorD vd=vs.toVD();
				vdAvg.plusOnE(vd);//average the results
				
				if (p.fnBaseline!=null)
					for (int j=0;j<vd.size();++j)//for each col2 value
						vmdd.getE(j).plusOn(vd.get(j)-vdBaseline.get(i));
			}
			vdAvg.devideOn(vvs.size());

					
			String avg=vdAvg.join3f();
			vsRlt.add( "AVG\t"+avg);
			vsRlt.save(fdOut+valueCol+","+e.getKey());

			FFile.appendToFile(
					valueCol +"-->"+e.getKey()+"\t"+avg +"\n"
					,"CollectTable");

			if (p.fnBaseline!=null){
				VectorD vdUp= new VectorD();
				VectorD vdDown= new VectorD();
				for (MapDD m: vmdd){
					MultinomD multi= new MultinomD(m);
					vdUp.add(multi.percentileD(0.9));
					vdDown.add(multi.percentileD(0.1));
				}
				FFile.appendToFile(
						"\t"+vdUp.join3f() +"\n"
						+"\t"+vdDown.join3f() +"\n"
						,"CollectTable");
				
			}
			
		}
		return;
	}
	String fdOut="collectedTables2D/";
	public void collectAll(){
		FFile.mkdirs(fdOut);
		for (String valueCol: p.valueCol.split(","))
			collect(valueCol);
	}
	public static void main(String[] args) {		
		try {
			Param.overwriteFrom("conf");
			if (args.length>0)
				Param.overwrite(args[0]);
			
			(new CollectTable2D()).collectAll();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	
}
