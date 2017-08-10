package edu.cmu.pra.model;

import edu.cmu.pra.graph.Graph;

/**
 * As a generalization of PageRank
 * this class calculates offline sets of query independent path features 
 * for all types of entities  
 * 
 * @author nlao
 */
public class EntityRank{// extends ERGraph{
	public Graph g = null;	//  too much stuff involved, don't want to use the IGraph interface 
	public PRAModel net = null;
	//public Schema schema = null;
	
	public String baseFolder=null;
	public EntityRank(PRAModel net){
		this.g=(Graph)net.graph_;
		this.net = net;
		//baseFolder=	net.p.dataFolder	+net.p.taskFile;

	}
	//public ETGraphPathRank net=null;
//
//	private String getBaseFolder( boolean gap){
//		if (!gap)return baseFolder;
//		else return baseFolder+"_gap"+net.p.e0Gap;
//
//	}
//	/*private String getEntRankFolder(String sTagEnt, int nStep, boolean gap){//, String path){
//		return String.format("%s/%s/step%d"//%s/"
//			,getBaseFolder(gap)	,sTagEnt, nStep);// , path);	
//	}*/
//	private String getEntRankFolder(String sTagEnt,  boolean gap){//, String path){
//		return getBaseFolder(gap)+"/"+sTagEnt;	
//	}
//	public PathTree tree=null;
//
//	public void createPathTree(int etTgt){
//		createPathTree(net.p.maxE0Step, etTgt);
//	}
//	public void createPathTree(int maxStep,int etTgt){
//		tree= new PathTree(net, net.schema.T0,etTgt
//				, maxStep,net.p.minE0Step);
//		tree.createPathTree(null);
//		tree.indexPaths();
//	}
//
//	private void walkE0PathTreeRecur(int time
//			,PathNode n,MapID secM){//,int nStep
//
//		//System.out.println(n.name);
//
//		if (n.num_steps_>=2){
//			String folder=getEntRankFolder(n.type_.name_, false)		+"/"+ n.short_name_;
//			FFile.mkdirs(folder);
//			secM.save3(folder+"/"+time);
//		}
//		
//		//net.g.setTime(time);
//		for (Map.Entry<Integer, PathNode>e: n.relation_children_.entrySet()){
//			int iRel= e.getKey();
//			PathNode nC =e.getValue();
//			MapID sM= new MapID();
//
//			if (n.num_steps_==0){
//				Section secC=g.vSect.get(nC.type_.id);
//				
//				int nEnt=0;
//				for (Entity ent: secC.vEnt)
//					if (ent.time_<time)
//						++nEnt;
//				double d = 1.0/nEnt;
//				for (Entity ent: secC.vEnt)
//					if (ent.time_<time)
//						sM.put(ent.id_, d);
//			}
//			else
//				net.step(secM,1.0, iRel, sM, time);			
//			
//			walkE0PathTreeRecur( time,nC, sM);
//		}
//		return;
//	}
//	
//	///private ERGraph graph=null;
//	public void createEntityRank(boolean bRW){
//		System.out.println("createEntityRank()");
//
//		createPathTree(null);
//		//mmTgtStepPathNames=new TMapMapIIX<VectorS>(VectorS.class);
//		System.out.println(tree.vsPath.join("\n"));
//		
//		if (bRW){
//			for (int time: g.mTime.toVector().reverseOn())	{
//				System.out.print("\n walkE0PathTreeRecur(), time="+time);
//				walkE0PathTreeRecur(time,tree.root_, null);	
//			}
//		}
//		
//		mergeEntityRank();	
//		
//		System.out.println("done");
//		return;
//	}
//
//	private void mergeEntityRank(){//int gap){//String folder){net.p.e0Gap
//		System.out.println("merging txt files to obj files");
//		
//		SetI mT= new SetI();
//		for (int time:g.mTime)
//			if (time % net.p.e0Gap==0)
//				mT.add(time);
//		
//		String fd=getBaseFolder(true);
//		FFile.mkdirs(fd);
//		mT.save(fd+"/time");
//		
//		for (EntType etTrg: net.schema.vEntType){
//			System.out.print("\n"+etTrg.name_);
//			for (int nStep=2; nStep<=net.p.maxE0Step; ++nStep){
//				
//				mergeEntityRank( nStep,  etTrg	, mT);			
//				vsAllPath.clear();
//				mTimeEntityRank.clear();
//			}
//		}
//	}
//	
//	private void mergeEntityRank(int nStep, EntType etTrg	,SetI mT){
//		System.out.print("\n"+nStep);
//		String folder = getEntRankFolder(etTrg.name_, false);
//		String fdGap = getEntRankFolder(etTrg.name_, true)
//			+ "/step"+ nStep;
//
//		createPathTree(nStep, etTrg);
//		
//		
//		FFile.mkdirsTrimFile(fdGap);
//		tree.vsPath.save(fdGap+".paths");
//		
//		for (Integer time: mT){
//			VecMapID vm= mTimeEntityRank.getC(time);
//			vm.clear();
//			for (String path:tree.vsPath)
//				vm.add(MapID.fromFile(folder+"/"+path+"/"+time));
//			if (time.equals(mT.last()))
//				vm.getVI(CTag.size).save(fdGap+".num.signal."+time);
//		}
//		FFile.saveObject(mTimeEntityRank, fdGap+".obj");
//		System.gc();
//		System.out.print(FSystem.memoryUsage());
//	}
//	public VecMapID getFeatures(int time){
//		return mTimeEntityRank.get(time);
//	}
//	
//	public TMapIX<VecMapID> mTimeEntityRank=
//		new TMapIX<VecMapID>(VecMapID.class);
//	
//	public VectorS vsAllPath=new VectorS();
//
//	//learner.etTarget.name
//	public void loadEntityRank(){
//		loadEntityRank(getEntRankFolder());
//	}
//	public String getEntRankFolder(){//add step information
//		return getEntRankFolder(net.target_type_.name_	, true)
//			+ "/step"+ net.p.maxE0Step;
//	}
//	
//	public void loadEntityRank(String folder){
//
//		System.out.println("loadEntityRank()"+folder);
//		
//		vsAllPath=FFile.loadLines(folder+".paths");
//		mTimeEntityRank=(TMapIX<VecMapID>) 
//			FFile.loadObject(folder+".obj");
//		
//		System.out.println("done for time="+mTimeEntityRank.keySet());
//		System.out.println(FSystem.memoryUsage()+"\n");
//		
//		g=(Graph) net.g;
//		//now fill in Entity Rank of the missing years
//		VecMapID vm=new VecMapID();//null;
//		for (int time: g.mTime){
//			if (mTimeEntityRank.containsKey(time))
//				vm =mTimeEntityRank.get(time);			
//			else
//				mTimeEntityRank.put(time,vm);			
//		}
//		return;
//	}
//	
//	
//	public static void run(boolean bRW){
//		Param.overwrite("bEntityRank=true");	//
//		//Param.overwrite("sMode=N,maxE0Step=3");	//
//		
//		Dataset l= SmallJobs.tryloadTask();
//
//		l.model_.entRank=new EntityRank(l.model_);
//		l.model_.entRank.createEntityRank(bRW);
//		
//
//	}
//	public static void main(String[] args) {
//		Param.overwriteFrom("conf");
//		if (args.length>0){
//			Param.overwrite(args[0]);
//			Learner.args = args[0];				
//		}
//		Param.overwrite("e0Gap=1");
//		Param.overwrite("maxE0Step=4,sMode=P,minParticle=0.0001");
//		run(true);
//	}
}
