package edu.cmu.pra.data;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;
import edu.cmu.pra.LearnerPRA;
import edu.cmu.pra.SmallJobs;


public class PRA {

	public static String badChars="[();:\\.,\\?\\! ]";
	public static String normalizeName(String name){
		return name.replaceAll(badChars, "_");
	}
	
	public static void normalizeV(VectorS vs){
		for (int i=0;i<vs.size(); ++i)
			vs.set(i,normalizeName(vs.get(i)));
	}
	
	public static void generateTextEdges(String types){
//		LearnerPRA l= SmallJobs.tryloadTask();
//		
//		BufferedWriter bw = FFile.newWriter("../data/TextEdges.db");//fdData
//		
//		for (String type:	types.split(" ")){
//			String rel="w"+type;
//			System.out.print("\n"+ rel+"("+type+",word)");
//			int type_id = l.graph_.nodeType_index_.get(type);
//			
//			for (int e=0; e < l.graph_.node_index_.size(); ++e){
//				if (l.graph_.nodes_.get(e).type_ != type_id) continue;
//				
//				String name = l.graph_.getNodeName(e);
//				String text=name.toLowerCase();
//				text=text.replaceAll("[();:\\.,\\?\\!_\\-]", " ");
//				text=text.replaceAll(" +", " ");
//				text=text.trim();
//				FFile.writeln(bw,rel+"("+name+","+text+")");
//			}			
//		}
//		FFile.close(bw);
		
	}
	public static void main(String args[]) {
		Param.overwriteFrom("conf");

	}
}
