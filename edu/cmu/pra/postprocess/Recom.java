package edu.cmu.pra.postprocess;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapISetI;
import edu.cmu.lti.algorithm.container.MapIVecD;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.ir.eva.IREvaluation;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.LearnerPRA;
import edu.cmu.pra.graph.IGraph;
import edu.cmu.pra.model.PRAModel;
import edu.cmu.pra.model.Query;

public class Recom {
	public static enum ERecomMode{
		H		//collaborative filtering
		,C	// content based
		,CH	//collaboration based on content model
	}
	
	public static class Param 	extends edu.cmu.lti.util.run.Param
	
	implements  Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	
		public String code;	
		public Param() {
			super (Recom.class);
			parse();
		}
		public ERecomMode recomMode=null;
		public double scSelf=10;
		
		public int collaborateK;		
		
		public void parse(){	

			collaborateK=getInt("collaborateK",	10);
			//id=getString("id", "x");
			//nTrainingThread=getInt("nTrainingThread",1);
			//bMultiThreaded=getBoolean("bMultiThreaded",false);
			scSelf=getDouble("scSelf",10.0);

			recomMode=ERecomMode.valueOf(getString("recomMode"
					, ERecomMode.H.name()));		

			code= recomMode.name();
			switch(recomMode){
			case C:// code+="C"+
				break;
			case CH: code+= collaborateK+"_sc"+scSelf;
				break;
			case H:   code+= ""+collaborateK;
				break;
			
			}
		}
		
	}	
	public static Param p=null;// new Param();

	//private static MAP map=new MAP();// = q.eval(rlt);

	private static MapIVecD mvIDF= new MapIVecD();
	//private static TMapVecIX<SetI> mvmFe= new TMapVecIX<SetI>(SetI.class);
	//private static TMapIX<VecSetI> mvmFe= new TMapIX<VecSetI>(VecSetI.class);
	private static MapIX<MapISetI> mmmFe= new MapIX<MapISetI>(MapISetI.class);
	
	private static VectorD getIDFs(int time){
		VectorD v= mvIDF.get(time);
//		if (v==null){
//			//System.out.print(""+time);
//			
//			int N=0;
//			v=new VectorD();
//			v.ensureCapacity(g.vEnt.size());
//			for (Entity e: g.vEnt){
//				int n=e.nLinks(time);
//				v.add(-Math.log(n));
//				N+=n;
//			}
//			v.plusOn(Math.log(N));
//			v.sqrOn();
//			mvIDF.put(time,v);
//			
//			//.VecSetI vm= new VecSetI();
//			//vm.ensureCapacity(secP.vEnt.size());
//			
//			MapSetII mm=new MapSetII();
//			for (Entity p: secP.vEnt)
//				mm.put(p.id_,p.getAllLinks(time));
//			mmmFe.put(time,mm);
//		}
		return v;
	}

	public static MapID content(Query q){		
		MapID mResult= new MapID();
//		VectorD vIDF=getIDFs(q.time_);
//		MapSetII mmFe= mmmFe.get(q.time_);
//		
//		SetI mUser= q.seeds_.get(1);
//		int idxUser=mUser.first();	//System.out.println("user="+g.getNodeName(idxUser));
//
//		SetI mRead=new SetI();
//		net.step(mUser, rRead, mRead, q.time_, -1);
//		if (mRead.size()==0) return mResult;
//
//		MapID mdRead= new MapID();
//		for (int iR: mRead){
//			SetI mOld=mmFe.get(iR);
//			mdRead.plusOn(mOld);
//		}
//		
//		for (int iP: q.and_){
//			double score=0;
//			SetI mNew=mmFe.get(iP);
//			if (mNew.size()==0) continue;
//			
//			score+= cosine(mNew, mdRead, vIDF);
//
//			mResult.put(iP, score);
//		}		
//		Counter.c50.step('.');
		
		return mResult;
	}

	public static double cosine(SetI s, MapID m, VectorD vW2){
		if (s.size()==0 || m.size()==0)
			return 0.0;
		double nAnd=0;
		for ( Integer x : m.keySet()) 
			if (s.contains(x))
				nAnd+=m.get(x)* vW2.get(x);
		
		double sum1=0;
		for ( Integer x : s) 
			sum1+=vW2.get(x);
		
/*		double sum2=0;
		for ( Map.Entry<Integer, Double> e : m.entrySet())
			sum2+=e.getValue()*e.getValue()*vW2.get(e.getKey());
*/
		return nAnd/Math.sqrt(sum1);//*sum2);
	}
	public static MapID collaborate(Query q){		
		MapID mResult= new MapID();
//		SetI mUser=(SetI) q.seeds_.get(1);
//		int idxUser=mUser.first();	//System.out.println("user="+g.getNodeName(idxUser));
//		
//		SetI mRead=new SetI();
//		net.step(mUser, rRead, mRead, q.time_, -1);
//		if (mRead.size()==0) return mResult;
//		
//		SetI mNei=new SetI();		
//		net.step(mRead, rRead_, mNei, q.time_, -1);
//		
//		//if (!p.recomMode.equals(ERecomMode.CH))
//				mNei.remove(idxUser);
//		if (mNei.size()==0) return mResult;
//		
//		KeepTopK keep = new KeepTopK(p.collaborateK);
//		for (int iNei: mNei){
//			SetI mReadNei= new SetI();
//			g.step(iNei,  rRead, mReadNei, q.time_);
//			double w=mRead.cosine(mReadNei);
//			keep.push(w,iNei);//mReadNei);// 
//		}
//		
//		double Z=0;
//		if (p.recomMode.equals(ERecomMode.CH))
//			if (p.scSelf!=0.0)
//				mResult.plusOn(content(q),p.scSelf);
//		
//		MapID mU=q.seeds_.get(1);
//		for (int i=0;i< keep.vObj.size();++i){
//			double w=keep.vVal.get(i);	//	Z+=w;
//			Integer iNei=(Integer) keep.vObj.get(i);
//			
//			if (p.recomMode.equals(ERecomMode.H)){
//				SetI mReadNei= new SetI();
//				g.step(iNei,  rRead, mReadNei, q.time_+1);			
//				mResult.plusOn(mReadNei, w);
//			}
//			else if (p.recomMode.equals(ERecomMode.CH)){
//				mU.clear();mU.put(iNei,1.0);				
//				MapID mR=content(q);
//				mResult.plusOn(mR, w);
//			}
//			else
//				FSystem.dieNotImplemented();
//		}
//		mU.clear();mU.put(idxUser,1.0);				
//
//		
		return mResult;
	}
	

	
	public static String test(VectorX<Query>  queries){		
		//System.out.println("\ntesting #Q="+vQ.size());		
		StopWatch sw= new StopWatch();
		IREvaluation ir_eval=new IREvaluation();
		IREvaluation eva = new IREvaluation();
		
		for (Query query: queries){			
			MapID result=null;
			switch(p.recomMode){
			case C:result=content(query);	break;
			case H:		case CH:
				result=collaborate(query);	break;			
			}
			
//			q.applyFilters(mResult);
			
			//System.out.println(""+mResult.size());
			eva.evaluate(result, query.good_);
			ir_eval.plusObjOn(eva);
		}
		
		ir_eval.meanOn();
		
		String rlt =p.code+"\t"+queries.size()
			+"\t"+ir_eval.print()+"\t"+sw.getSecI()+"\n";
		
		//System.out.println("\t#Q\t"+MAP.title());
		System.out.print("\n"+rlt);
		
		FFile.appendToFile(rlt	,"result.txt");//"\t"+

		return rlt;
	}
	
	public static IGraph g=null;
	public static int rRead=-1;
	public static int rRead_=-1;
	
	public static int secA=-1;
	public static int secP=-1;
	public static LearnerPRA l;
	public static PRAModel net=null;
	public static void prepare(){
		g=l.graph_;
		net=l.model_;
		rRead=g.getEdgeType("Read");
		rRead_=g.getEdgeType("_Read");
		secA=g.getNodeType("author");
		secP=g.getNodeType("paper");
		FSystem.dieNotImplemented();
		//l.loadQueries(Learner.p.train_samples);		
	//	net.createPathTrees(l.queries_, null);
	}
	public static void run() {
		Param.overwrite("maxStep=2");
		p=new Param();
		if (l==null)		prepare();
		test(l.queries_);
	}

	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
	}
}
