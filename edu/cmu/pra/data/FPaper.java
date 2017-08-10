package edu.cmu.pra.data;

import java.io.BufferedWriter;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.MapIMapII;
import edu.cmu.lti.algorithm.container.MapVecII;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.ir.eva.IREvaluation;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.nlp.StopWord;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;

public class FPaper {
	private static boolean bCaseSensitive=false;
	/**
	 * remove stopwords and puctuations
	 */
	public static String processTitle(String title) {//\\.
		title = title.replaceAll("[;:\\?\\!\"\\|]", " ");
		title = title.replaceAll("[,()]", "_");
		title= title.replaceAll(" +", " ");
		if (!bCaseSensitive)
			 title=title.toLowerCase();
		
		VectorS vs = new VectorS(title);
		if (bCaseSensitive)
			vs = (VectorS) vs.remove(StopWord.m429Cap);
		else			
			vs = (VectorS) vs.remove(StopWord.m429);
		title = vs.join(" ");
		return title;
	}
	public static void evalproduceRankingByCiteCount(
			String fnScen, String fnRanking){ // fn){//
		MapVecII mv= MapVecII.fromFile(fnRanking);
		//eva.evaluate(mSys, mRel);
		
		System.out.println("evaluated by "+fnScen);
		IREvaluation map0= new IREvaluation();
		for (VectorS vs: FFile.enuRows(fnScen,",")){
			int year= Integer.parseInt(vs.get(1));
			SetI mGold= SetI.fromString(vs.get(3), " ");
			VectorI vSys= mv.get(year);
			IREvaluation map= new IREvaluation();
			map.evaluate(vSys, mGold);
			map0.plusObjOn(map);
		}
		map0.meanOn();
		System.out.println(map0.title());
		System.out.println(map0.print());
		return;
	}

	public static void produceRankingByCiteCount(String fn){
		MapIMapII mm= new MapIMapII();//mmYearPaperCC
		for (VectorS vs: FFile.enuRows(fn)){
			int pmid= Integer.parseInt(vs.get(0));
			int year= Integer.parseInt(vs.get(1));
			int count= Integer.parseInt(vs.get(2));
			if (count==0) continue;
			mm.getC(year).put(pmid, count);
			
		}
		BufferedWriter bw= FFile.newWriter(fn+".RankByCite");
		for (Map.Entry<Integer, MapII> p: mm.entrySet()){
			MapII m=p.getValue();			
			VectorX<Integer> v=m.KeyToVecSortByValue(true);
			FFile.writeln(bw, p.getKey()
					+"\t"+ v.join(" "));
		}
		FFile.close( bw);
		return;
	}
	

	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		
		//produceRankingByCiteCount("count.paper.cite.sch.YA-Py.cite03");

		/*evalproduceRankingByCiteCount(//			""
				//"../data/read.nP10.1stA.midY"
				"../data/scenarios.Woolford_JL"
				,"count.paper.cite.sch.YA-Py.cite03"+".RankByCite");
				*/
	}
}
