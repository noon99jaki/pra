package edu.cmu.pra.data;

import java.util.Map;

import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.MapISetI;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.sequence.Pipe;
import edu.cmu.lti.algorithm.sequence.Pipes;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;


public class Cite2Read {
//	public static boolean bRequireUnderScore=true;
//	
//	private static MapSetII getReadings(Entity a, int minPub){
//		if (bRequireUnderScore) FSystem.dieNotImplemented();
//			//if (a.name_.indexOf('_')==-1)				return null;
//		
//		TimedLinks o= a.relation_outlinks_.get(rAuthor.id);
//		if (o==null) return null;		//no publication					
//		if (o.viEnt.size()<minPub) return null;	//no publication
//		
//		Relation rA=l.model_.schema.getRelation("_Author");
//		SetI mOwnPapers= a.relation_outlinks_.get(rA.id).viEnt.toSet();
//		int firstYear=o.vTime.get(0);
//			
//		
//		MapSetII mv= new MapSetII(); //year-->citation
//			
//		for(Entity p:l.graph_.vEnt.sub(o.viEnt)){	//published papers
//			TimedLinks oC=p.relation_outlinks_.get(rCites.id);
//			if (oC==null) continue; 	//cites nothing
//
//			for(Entity c:l.graph_.vEnt.sub(oC.viEnt)){//cited papers
//				if (mOwnPapers.contains(c.id_))	//skip his own papers
//					continue;
//				if (c.time_<firstYear-5) continue;
//				if (c.time_<=0) continue;
//
//				mv.getC(c.time_).add(c.id_);
//				
//				if (p.time_<= c.time_){
//				//	System.out.printf("%s cites %s(%d) in %d\n"
//				//			,a.name,c.name,c.time, p.time);
//				}
//			}
//		}
//		return mv;
//	}
//	
//	public static Pipe<Entity, String> pipeRead = new  Pipe<Entity, String>(){
//		public String transform(Entity a){
//			MapSetII mv= getReadings(a,0);			
//			if (mv==null) return null;
//
//			if (mv.size()==0) return null;
//			String oldReadings=l.graph_.vEntName.sub(mv.joinValues()).join(" ");
//
//			return "Read("+a.name_+","+oldReadings+")\n";
//		}
//	};
//	
//
//	//print readings for each author
//	public static Pipe<Entity, String> pipeScene = new  Pipe<Entity, String>(){
//		public String transform(Entity a){
//			MapSetII mv= getReadings(a,nPub);			
//			if (mv==null) return null;
//			
//			StringBuffer sb= new StringBuffer();			
//			for (Map.Entry<Integer, SetI> e: mv.entrySet()){
//				int year = e.getKey();
//				String newReadings=l.graph_.vEntName.sub(e.getValue()).join(" ");
//				sb.append((year+1)+Pipes.sep+year+Pipes.sep+a.name_
//						+Pipes.sep+newReadings+"\n");
//			}
//			return sb.toString();
//		}
//	};
//	
//	//I want function pointers, 	// pushing stuffing into Entity is not wise
//	//
//	private static Dataset l=null;
//	private static Relation rAuthor=null;
//	private static Relation rYear=null;
//	private static Relation rCites=null;
//	//private static PRAModel n=null;
//	
//	private static int nPub=0;
//	//private static boolean bFirstAuthor=false;
//	private static String fnData="../data/";
//
//	public static void generateReadings(boolean bUnderScore, boolean bFirstA, Integer[] vi){//int nP){
//		bRequireUnderScore=bUnderScore;
//		bFirstAuthor=bFirstA;
//		tag=bFirstAuthor?".1A":"";
//		
//		
//		l= SmallJobs.tryloadTask();
//		
//		rAuthor= bFirstAuthor?
//				l.model_.schema.getRelation("_FAuthor"):
//				l.model_.schema.getRelation("_Author");
//				
//		rYear= l.model_.schema.getRelation("Year");
//		rCites= l.model_.schema.getRelation("Cites");
//
//		for (int nP: vi){
//			System.out.println("generateReadings() with nP="+nP);
//			nPub=nP;
//			l.graph_.getSection("author")
//				.vEnt.enuString(pipeScene)//CTag.readings)//(
//				.save(fnData+"cite.nP"+nPub+tag,null);			
//		}
//
//		l.graph_.getSection("author")
//		.vEnt.enuString(pipeRead)
//			.save(fnData+"Read"+tag+".db",null);
//		
//		statAuthors();
//	}
//	private static boolean bFirstAuthor=false;
//	private static String tag=bFirstAuthor?".1A":"";
//	
//	static void statAuthors(){
///*		l= SmallJobs.loadTask();
//		rAuthor= bFirstAuthor?
//				l.net.schema.getRelation("_FAuthor"):
//				l.net.schema.getRelation("_Author");*/
//
//		MapSI mAuthorCount=new MapSI();
//
//		for (Entity a: l.graph_.getSection("author").vEnt){
//			TimedLinks o= a.relation_outlinks_.get(rAuthor.id);
//			if (o==null) continue;		//no publication					
//			mAuthorCount.put(a.name_, o.viEnt.size());
//		}
//		mAuthorCount.saveSorted("mAuthorCount"+tag);
//		
//		MapII mCountHist= new MapII();
//		for (Integer c: mAuthorCount.values())
//			mCountHist.plusOn(c);
//		String txt=mCountHist.join("\t","\n");
//		//System.out.print(txt);
//		FFile.saveString("mAuthorCounthist"+tag, txt);
//	}
//	
//	public static void main(String args[]) {
//		Param.overwriteFrom("conf");
//	}
}
