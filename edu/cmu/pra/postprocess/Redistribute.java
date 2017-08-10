package edu.cmu.pra.postprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.html.EColorScheme;
import edu.cmu.lti.util.html.FHtml;
import edu.cmu.lti.util.html.HtmlPage;
import edu.cmu.pra.data.PM;
import edu.cmu.pra.data.PMAbsInfor;

public class Redistribute {
	

	public static void produceTxtResult(String fn){
		String fnRlt="../run/result/2011";
		SetS mPMID= new SetS(FFile.enuACol(fnRlt,1));
		//SetS.fromFile(), sep)
		
		MapSX<PMAbsInfor> mInf= new MapSX<PMAbsInfor>(PMAbsInfor.class);
		
		BufferedReader br = FFile.newReader("pmid.sgd.crawl.ex.f0");	
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			PMAbsInfor ab= new PMAbsInfor();
			ab.parseLine(line);
			mInf.put(ab.pmid, ab);			
		}		
		FFile.close(br);

		
		BufferedReader br1 = FFile.newReader(fnRlt);	
		BufferedWriter bw1 = FFile.newWriter(fnRlt+".out");	
		while ((line = FFile.readLine(br1)) != null) {
			String vs[]= line.split("\t");
			String pmid= vs[1];
			double d=Double.parseDouble(vs[0]);
			PMAbsInfor ab= mInf.get(pmid);
			FFile.write(bw1, String.format("%.0e\t%s\t%s\t%s\n"
					, d, pmid, ab.year,ab.title));
		}		
		FFile.close(br1);
		FFile.close(bw1);
		
		//PMAbsInfor
	}
	
	static VectorI viF=new VectorI();
	/**
	 * print top K features
	 * @param th
	 * @param K
	 */
	public static void printFeatures(HtmlPage th, int K ){
		MapSS mFeatureComments= MapSS.fromFile("mFeatureComments.txt");
		VectorD vW=VectorD.fromFile(modelFile,0, "\t",true);
		VectorD vWAbs=vW.abs().findKLargest(K);
		double thAbs= vWAbs.lastElement();//.findTheKthLargest(20);
		double maxAbs=vWAbs.firstElement();
		
		VectorS vsF=FTable.loadAColumn(modelFile,1);//,"\t", true);
		viF.clear();
		
		EColorScheme cs= EColorScheme.RdBu11;
		cs.setRange(-maxAbs, maxAbs);
		
		th.newTable("Major Features","id\tweight\tpath\tcomments");		
		for (int i=0; i<vW.size(); ++i){
			double w=vW.get(i);
			if (Math.abs(w)<=thAbs) continue;
			viF.add(i);
			String name= vsF.get(i);
			th.addRow(String.format("%d\t%.1f\t%s\t%s"
					,i,w,name, mFeatureComments.getD(name))
					, cs.getBg(w));
		}
		th.endTable();
	}
	
	/**
	 * print subset of features
	 * @param th
	 * @param miF
	 */
	public static void printFeatures(HtmlPage th , SetI miF){
		MapSS mFeatureComments= MapSS.fromFile("../mFeatureComments.txt");
		VectorD vW=VectorD.fromFile(modelFile,0, "\t",true);
		
		VectorD vWAbs=vW.abs();
		double maxAbs=vWAbs.max();
		
		VectorS vsF=FTable.loadAColumn(modelFile,1);//,"\t", true);
		viF.clear();//	viF.addAll(miF);
		
		EColorScheme cs= EColorScheme.RdBu11;
		cs.setRange(-maxAbs, maxAbs);
		
		th.newTable("Major Features","id\tweight\tpath\tcomments");		
		for (int i: miF){
			double w=vW.get(i);
			viF.add(i);
			String name= vsF.get(i);
			th.addRow(String.format("%d\t%.1f\t%s\t%s"
				,i,w,name, mFeatureComments.getD(name)),cs.getBg(w));
		}
		th.endTable();
	}
	

	
	/**
	 * <a onclick="doFeedback(
	 * '/nies-2.0/RelevanceFeedback.action?
	 * query=10.1.1.102.1095
	 * &amp;depth=2
	 * &amp;document=10.1.1.102.9515
	 * &amp;rank=1
	 * &amp;url=
	 * &type=promote','2.1');">
	 * 
	 * <a onclick="doFeedback(
	 * '/nies-2.0/RelevanceFeedback.action?
	 * query=2011
	 * &amp;document=20008552
	 * &amp;rank=0
	 * &amp;depth=
	 * &amp;url=
	 * &type=demote','2011.0');">
	 * 
	 * <a onclick="doFeedback(
	 * '/nies-2.0/RelevanceFeedback.action?
	 * queryTerms=gene
	 * &amp;queryParams=depth%3D4
	 * &amp;queryId=0000000005
	 * &amp;document=3165494
	 * &amp;rank=6
	 * &amp;url=http%3A%2F%2Fwww.yeastgenome.org%2Fcgi-bin%2Freference%2Freference.pl%3Fpmid%3D3165494
	 * &type=demote', '3165494');">
	 * <img id="demote off 3165494" src="images/demote-empty.png" alt="Mark nonrelevant" /></a>
	 * 
	 * @param query
	 * @param doc
	 * @param rank
	 * @return
	 */
	private static String printFeedback(String query, String doc, String rank){//, String user
		String url="/nies-2.0/RelevanceFeedback.action";
		String params="depth%3D3";
		
		String info= String.format(
			"'%s?queryTerms=%s&amp;document=%s&amp;rank=%s&amp;url=&amp;queryParams=%s"
			,url,query, doc,rank,params);
		String pos= String.format("%s.%s",query,rank);
		
		return formatBBD(info,pos);
	}
	private static String formatKatie(String info, String pos){
		return String.format(	
			"\n\n<a onclick=\"doFeedback(%s&type=demote', '%s');\"><img id=\"demote off %s\" src=\"images/demote-empty.png\" alt=\"Mark nonrelevant\" /></a>"
			+"\n<a onclick=\"doFeedback(%s&type=unmark', '%s');\"><img id=\"demote on %s\" src=\"images/demote-full.png\" alt=\"Unmark nonrelevant\" style=\"display:none\"/></a>"
			+"\n<a onclick=\"doFeedback(%s&type=promote','%s');\"><img id=\"promote off %s\" src=\"images/promote-empty.png\" alt=\"Mark relevant\" style=\"display:none\"/></a>"
			+"\n<a onclick=\"doFeedback(%s&type=unmark', '%s');\"><img id=\"promote on %s\" src=\"images/promote-full.png\" alt=\"Unmark relevant\" /></a>"
			+"\n<span id=\"indicator %s\" style=\"display:none;\"><img src=\"images/indicator.gif\" /></span>"
			,info,pos,pos
			,info,pos,pos
			,info,pos,pos
			,info,pos,pos
			,pos);
	}
	private static String formatBBD(String info, String pos){
	 	return String.format(	
			"\n\n\n<a onclick=\"doFeedback(%s&type=demote','%s');\">"
			+"\n<img id=\"demote off %s\"   src=\"images/demote-empty.png\" alt=\"Mark nonrelevant\"/>"
			+"\n<img id=\"demote on %s\"   src=\"images/demote-full.png\"   alt=\"Unmark nonrelevant\" style=\"display:none\"/></a>"
	    
			+"\n<a onclick=\"doFeedback(%s&type=promote','%s');\">"
			+"\n<img id=\"promote off %s\"  src=\"images/promote-empty.png\" alt=\"Mark relevant\"  />"
			+"\n<img id=\"promote on %s\"  src=\"images/promote-full.png\"  alt=\"Unmark relevant\"   style=\"display:none\"/></a>"
			+"\n\n<span id=\"indicator %s\" style=\"display:none;\"><img src=\"images/indicator.gif\" /></span>"
			,info,pos,pos,pos
			,info,pos,pos,pos,pos);
	}
	 
	
	static double thImportant=0.1;
	static SetI miMajorF= new SetI();	

	public static String printReasons(MapID mScores,EColorScheme cs){
		StringBuffer sb= new StringBuffer();
		for (Map.Entry<Integer, Double> e: mScores.entrySet()){
			int i=e.getKey();
			double d=e.getValue();
			if (Math.abs(d)<thImportant) continue;
			
			miMajorF.add(i);
			
			String txt=String.format("<b>%d</b>(%.1f)",i,d);
			sb.append( FHtml.addBG(txt, cs.getColor(d))+" ");
		}
		return sb.toString();
	}
	
	static EColorScheme csReason= EColorScheme.RdGy9.setRange(-1,1);//RdBu9
	public static String printReasons(MapID mScores, double score){//,EColorScheme cs){
		if (score<=0) score=1.0;
		mScores=mScores.devide(Math.abs(score));
		return printReasons(mScores,csReason);
	}
	
	static EColorScheme csScore=EColorScheme.RdBu11;

	public static void printSuggestions(String fnRlt,HtmlPage page ){
		MapSX<PMAbsInfor> mInf=PMAbsInfor.loadAbstracts(fnRlt+".abs" );
		miMajorF.clear();
		page.newTable("Suggested Readings"
				, "id\tScore\tReasons\tTitle\tPMID\tJudge"
				);//, FHtml.backGround(EColor.cornsilk2));
		

		double maxAbs=VectorD.fromFile(fnRlt, 0).absOn().max();
		maxAbs=Math.sqrt(maxAbs);
		csScore.setRange(-maxAbs, maxAbs);
		
		int id=0;
		for (String line: FFile.enuLines(fnRlt)) {
			String vs[]= line.split("\t");
			double score=Double.parseDouble(vs[0]);
			double scaled= score/Math.sqrt(Math.abs(score));
			String pmid= vs[1];
			MapID mScores= MapID.fromLine(vs[2]);
			PMAbsInfor ab= mInf.get(pmid);
			
			VectorS vTxt= new VectorS();
			vTxt.addOn(id+"")
				.addOn(String.format("%.0e",score))
				.addOn(printReasons(mScores, score))
				.addOn("("+ab.year+") "+ab.title)
				.addOn(FHtml.addHref(pmid,PM.getAbsURL(pmid)))
				.addOn(printFeedback(fnRlt,pmid,id+""));
			
			VectorS styles= new VectorS();
			styles.addOn(null).addOn(csScore.getBg(scaled))
				.addOn(null).addOn(null).addOn(null).addOn(null);
			
			page.writeRow(vTxt,styles);
			++id;
			if (id==50) break;
			//if (id%10==0) th.writeTitle();
		}	
				
	}
	public static String modelFile="weights";

	
	public static String script="script.bbd";
	public static void produceHtmlResult( String fnRlt){	//String fnRlt="../run/result/2011";
		
		
		SetS mPMID= new SetS( FFile.enuACol(fnRlt,1));
		//SetS mPMID= SetS.fromFile(fnRlt,1);
		
		if (!FFile.exist(fnRlt+".abs"))
			FTable.filterByColumn(fnAbs, fnRlt+".abs", 0, mPMID);
		
		HtmlPage._script=FFile.loadString(script);
		HtmlPage th = new HtmlPage(fnRlt);//"html";
		HtmlPage._script="";
			
		th.addTxt("You can find explanations to the Reasons at here");
		
		HtmlPage thF=th.extPage(".features","Major Features");
		printSuggestions(fnRlt,th);
		printFeatures(thF, miMajorF);		
		th.close();
	}
	public static void debug(){
		//SetS m334= SetS.newFrom(e)("debug/334");
	}
	public static String fnAbs="../preprocess/pmid.sgd.crawl.ex";
	//public static String fnRlt="2011";
	public static void main(String args[]) {
		//Param.overwriteFrom("../run/conf");

		produceHtmlResult("2010");
		produceHtmlResult("2011");
		//debug();
	}	
	
	
}
