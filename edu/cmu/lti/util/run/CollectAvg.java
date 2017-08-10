package edu.cmu.lti.util.run;

import java.io.BufferedWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.math.MomentumVec;
import edu.cmu.lti.algorithm.math.Momentum.VMomentum;
import edu.cmu.lti.util.file.FFile;

//useful for collecting scores and average them
public class CollectAvg {
	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public int minIt=5;
		public int colIt=-1;
		
		public String task;
		public Param(){//Class c) {
			super(CollectAvg.class);
			parse();
		}
		String fdCout=null;
		String fdErr=null;
		String fdScore=null;

		public String fdScores;
		public void parse() {
			task=getString("task", "TT");		
			minIt=getInt("minIt", 5);		
			colIt=getInt("colIt", -1);
			
			fdCout=task+".cout/";
			fdErr=task+".err/";
			fdScore=task+".scores/";

		}
	}
	public Param p = new Param();

	

	VectorI vX= new VectorI();

	public MapSX<MomentumVec> mvEst=new MapSX<MomentumVec>(MomentumVec.class);
	
	protected void addScore(String key, VectorD vd){//String rlt){//
		MomentumVec v = mvEst.getC(key);
		v.addInstance(vd);
	}
	
	
	
	MapSS mFnRlt= new MapSS();		
	private void collect(){
		Pattern paFile = Pattern.compile("(.*),id=\\d+");
		

		for (String fn: FFile.getFileNames(p.fdScore)){
			Matcher maFile = paFile.matcher(fn);
			if (!maFile.matches()) {
				System.out.println("bad file name: " + fn);
				continue;
			}
			
			String key = maFile.group(1);
			//String id = maFile.group(2);
			
			String rlt= FFile.loadFirstLine(p.fdScore+fn);
			rlt=rlt.replaceAll("/", "\t");
			addScore(key,VectorD.fromLine(rlt.trim(), "\t"));//
			mFnRlt.put(fn,rlt);//vd.join3f()
		}
	}
	public void run(){
		collect();
		mFnRlt.save("collected.results");
		
		
		BufferedWriter bwMean= FFile.newWriter("collected.mean");
		BufferedWriter bwSD= FFile.newWriter("collected.sd");
		for (Map.Entry<String, MomentumVec> e: mvEst.entrySet()){
			String key=e.getKey();
			MomentumVec MV=e.getValue();		
			MV.finish();
			
			String s= String.format("%s\t%s\t%d\n"
					, key, MV.means.join3f(),MV.num_samples);
			FFile.write(bwMean, s);
			System.out.print(s);

			FFile.write(bwSD,String.format("%s\t%s\t%d\n"
					, key, MV.std_devs.join3f(),MV.num_samples));

		}
		FFile.close(bwMean);
		FFile.close(bwSD);
	}
	
	public static void main(String[] args) {		
		try {
			//Param.overwriteFrom("conf");
			if (args.length>0)
				Param.overwrite(args[0]);
			(new CollectAvg()).run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
