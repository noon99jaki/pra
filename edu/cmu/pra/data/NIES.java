package edu.cmu.pra.data;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.TMapSVecSa;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;


public class NIES {
	public static class RFLine implements IParseLine{
		public String query;
		public String queryparams	;
		public String rank	;
		public String document	;
		public String user ;
		public String judgement;
		public String timestamp	;



		public String toString(){
			return null;
		}
		public boolean parseLine(String line){
			VectorS vs= FString.splitVS(line,"\t");
			int i=0;
			query	=vs.get(i);++i;  
			queryparams	=vs.get(i);++i;  
			rank	=vs.get(i);++i;  
			document	=vs.get(i);++i;  
			user	=vs.get(i);++i;  
			judgement	=vs.get(i);++i;  
			timestamp	=vs.get(i);++i;  
			return true;
		}


		public static Seq<RFLine> reader(String fn){
			return reader(fn, false);
		}
		public static Seq<RFLine> reader(String fn,boolean bSkipTitle){
			return new SeqTransform<RFLine>(	RFLine.class, fn, bSkipTitle);
		}
	}
	
	
	public static void generateScenarios(String fnRelevance, String fnScen){
		TMapSVecSa mvP= new TMapSVecSa();
		TMapSVecSa mvN= new TMapSVecSa();
		
		for (RFLine rf: RFLine.reader(fnRelevance, true)){
			if (rf.judgement.equals("1"))
				mvP.getC(rf.query).add(rf.document);
			else if (rf.judgement.equals("0"))
				mvN.getC(rf.query).add(rf.document);
			else 
				FSystem.die("unknown judgement="+rf.judgement);
		}
		BufferedWriter bw=FFile.newWriter(fnScen);
		for (String q: mvP.keySet())
			FFile.writeln(bw, q+","+mvP.get(q)+","+mvN.getC(q));
		FFile.close(bw);
		return;
	}
	public static void main(String[] args){
	}

}
