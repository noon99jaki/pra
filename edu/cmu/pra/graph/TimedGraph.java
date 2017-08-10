package edu.cmu.pra.graph;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapVecII;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.TMapIVecI;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.CTag;

public class TimedGraph extends AGraph{
//public SetI times_ = new SetI();
//public VectorI times_;

	
	public TimedGraph() {
	}
	
	public VectorX<TimedNode> nodes_ = new VectorX<TimedNode>(TimedNode.class);
	
	public int addNode() {
		nodes_.add(new TimedNode());
		return nodes_.size()-1;
	}
	protected void loadNodes(String graph_folder) {
		if (nodes_.size() == 0)
			nodes_ = (VectorX<TimedNode>) FFile.loadObject(graph_folder + node_file_);
	}
	public void saveNodes(String node_file) {
		FFile.saveObject(nodes_, node_file);
	}
  public Set<Integer> getNodeOutlinkTypes(int node) {
  	return this.nodes_.get(node).relation_outlinks_.keySet();
  }
	
	public static class TimedNode  implements Serializable
	, IGetStrByStr, IGetIntByStr {
		private static final long serialVersionUID = 2008042701L; 
		//public int id;	
		//public String name;
	
		public int time_=-1;
		//public int node_type;
		//public Graph g;
		
		////this version is used in query stage to speedup 
		public MapIX<TimedLinks> relation_outlinks_ = new MapIX<TimedLinks>(TimedLinks.class);

		
		public TimedNode(){//int id, Section sec, String name){
			//this.id=id;
			//this.name=name;
		}	

		
		@Override public Integer getInt(String tag){
			//if (tag.equals(CTag.id)) 				return id;			
			if (tag.equals(CTag.nOutLink))
				return relation_outlinks_.sum(CTag.size);
			return null;		
		}
		
	//	private MapID mdIdentityFeature=null;//reusable Instantiated feature
	//	public MapID getIdentityFeature(){
	//		return getIdentityFeature(1.0);
	//	}
	//		public MapID getIdentityFeature(double value){
	//		if (mdIdentityFeature==null){
	//			mdIdentityFeature= new MapID();
	//			mdIdentityFeature.put(id,value);
	//		}
	//		return this.mdIdentityFeature;		
	//	}
		
	
		
		@Override public String getString(String tag){
	//		if (tag.equals(CTag.name))	return this.name;		
	//		if (tag.equals(CTag.ghirlName))	return printGhirlName();		
	//		if (tag.equals(CTag.pctLinks))return printPCTLinks();
		/*	if(tag.equals(CTag.citeCount)){
				Outlinks ol=e.mOutlinks.get(r.id);
				int nCite=(ol!=null)?ol.viEnt.size():0;
				return e.name+"\t"+e.time+"\t"+nCite;
			}*/
			return null;
		}
	
	
		public int sampling(int iRel,  int timeStamp){
	//		Outlinks ols= this.mOutlinks.get(iRel);
	//		if (ols==null) return -1;
	//		int idx= ols.getIdx(timeStamp);
	//		if (idx==0) return -1;
	//		return ols.viEnt.sample(0,idx);
			return -1;
		}
		public void stepReversed(double p0,int iRel, int iRelR
				, MapID dist, int timeStamp	, Graph g){
			FSystem.dieNotImplemented();
	/*		MapVectorII mv= mmvRelTimeEnt.get(iRelR);
			if (mv==null) 	return ;
			mv.joinValuesWhereKeySmallerThan(timeStamp,viLink);
			if (viLink.size()==0)	return;
			
			for (int ie: viLink){
				Entity e=g.vEnt.get(ie);
				double p=e.mmvRelTimeEnt.get(iRel).cntSmallerThan(timeStamp);
				dist.plusOn(ie, p0/p);
			}
			return;*/
		}
		
	
	
	
		//private static VectorI viEnt= new VectorI();	//not thread safe
	
		public int samplingReversed(int iRel, int iRelR, int timeStamp, Graph g){
			FSystem.dieNotImplemented();
			return -1;
			/*
			MapII m=(MapII) mmvRelTimeEnt.get(iRelR);
			if (m==null) 
				return -1;
			//viLink.clear();
			m.idxSmallerThan(timeStamp,viLink);
			if (viLink.size()==0)
				return -1;
	
			viEnt.clear();
			for (int ie: viLink){
				double p=g.vEnt.get(ie).mmvRelTimeEnt.get(iRel).cntSmallerThan(timeStamp);
				if (FRand.drawBoolean(1/p))
					viEnt.add(ie);
			}
			if (viEnt.size()>0)
				return viEnt.sample();
			return -1;		*/
		}
	
	
	}



	
	public void addEdge(int rel, int e1, int e2) {
		FSystem.dieShouldNotHappen();
	//	nodes_.get(e1).getC(rel).add(e2);
	}
	
	public MapID getWeightedEdges(int node, int relation) {
		return null;
	}

	public TimedLinks getTimedEdges(int node, int relation) {//, int time) {
		return nodes_.get(node).relation_outlinks_.get(relation);
	}

	public VectorI getEdges(int node, int relation) {		
		return getTimedEdges(node, relation).nodes_;	
	}


	//public VectorX<Format> formats_ = new VectorX<Format>(Format.class);

	// for each relation print its edges
	//	private  boolean save2TimedGraphByRel(String folder){
	//		FFile.mkdirs(folder);
	//		for (Relation r:schema.vRel){
	//			//if (r.bIsAMirror)	continue;
	//			
	//			if (r.from_type_.id<0) continue;
	//			
	//			BufferedWriter bw= FFile.newWriter(folder+"/"+r.name_);
	//			
	//			Section sec= vSect.get(r.from_type_.id);
	//			
	//			for (Entity ent: sec.vEnt){
	//				TimedLinks ols=  ent.relation_outlinks_.get(r.id);
	//				if (ols==null) continue;
	//				
	//				VectorS vs= this.vEntName.sub(ols.viEnt);
	//				for (int i=1; i<ols.vIdx.size();++i){
	//					int b=ols.vIdx.get(i-1);
	//					int e=ols.vIdx.get(i);						
	//					FFile.write(bw, "%d\t%s(%s,%s)\n"
	//						, ols.vTime.get(i-1)
	//						, r.name_, ent.name_, vs.sub(b, e).join(" "));
	//				}
	//			}
	//			FFile.close(bw);
	//		}
	//		return true;
	//	}


	//	private  boolean save2TimedGraphByTime(String folder){
	//		FFile.mkdirs(folder);
	//		
	//		MapIBw mBW= new MapIBw(folder+"/t=");
	//
	//		for (Relation r:schema.vRel){
	//			
	//			if (r.from_type_.id<0) continue;
	//			
	//			Section sec= vSect.get(r.from_type_.id);
	//			
	//			for (Entity ent: sec.vEnt){
	//				TimedLinks ols=  ent.relation_outlinks_.get(r.id);
	//				if (ols==null) continue;
	//				
	//				VectorS vs= vEntName.sub(ols.viEnt);
	//				
	//				for (int i=1; i<ols.vIdx.size();++i){
	//					int b=ols.vIdx.get(i-1);
	//					int e=ols.vIdx.get(i);
	//					int time=ols.vTime.get(i-1);
	//					
	//					FFile.write(mBW.getBW(time)
	//							, "%s(%s,%s)\n"
	//						, r.name_, ent.name_, vs.sub(b, e).join(" "));
	//				}
	//			}
	//		}
	//		
	//		mBW.closeAll();
	//		return true;
	//	}
	//	


	public boolean save2GhirlFormat(SetS msTextRel, String folder) {
		//		//String folder= p.dbName;
		//		FFile.mkdirs(folder);
		//		
		//		for (Section sec: vSect){
		//			BufferedWriter bw= FFile.newWriter(
		//					folder+"/"+sec.et.name_);
		//			
		//			for (Entity e: sec.vEnt)
		//				FFile.write(bw, "edge isa %s %s\n", e.printGhirlName(), sec.et.name_);
		//
		//			for (Entity e: sec.vEnt){
		//				String gName= e.printGhirlName();
		//				for (Map.Entry<Integer, TimedLinks> e1: e.relation_outlinks_.entrySet()){
		//					int iRel= e1.getKey();
		//					Relation r= this.schema.vRel.get(iRel);
		//					if (r.bIsAMirror)	continue;					
		//					
		//					TimedLinks ols=  e1.getValue();
		//					VectorS vs= this.vEnt.getVS(CTag.name, ols.viEnt);
		//					
		//					if (msTextRel.contains(r.name_)){
		//						
		//						String tn= "TEXT$"+gName.replace("$", "-")+"-"+r.name_;
		//						FFile.write(bw, "edge %s %s %s\n"
		//								, r.name_, gName,tn);
		//						FFile.write(bw, "node %s %s\n"
		//								, tn,	vs.join(" "));
		//					}
		//					else{
		//							for (String name: vs)
		//							FFile.write(bw, "edge %s %s %s\n"
		//								, r.name_, gName, name);
		//					}
		//				}
		//			}
		//			FFile.close(bw);
		//		}
		return true;
	}

	
		public boolean dumpNodeCountsByTime(String fn){
			System.out.print("dumpNodeCountsByTime ...");
			
			BufferedWriter bw= FFile.newWriter(fn);
			FFile.writeln(bw, "Time\t"+this.nodeType_index_.list_.join("\t"));
	
			TMapIVecI time_type_count= new TMapIVecI();
			for (int i = 0; i < this.getNumNodes(); ++i)
				time_type_count.getC(this.nodes_.get(i).time_)
				.plusOnE(this.node_types_.get(i), 1);
			
			for (Map.Entry<Integer,VectorI> e: time_type_count.entrySet())
				FFile.writeln(bw, e.getKey()+"\t"+ e.getValue().join("\t"));
			
			FFile.close(bw);
			System.out.println("done");
			return true;
		}
		
		public boolean dumpLinkCountsByTime(String fn){
			System.out.print("dumpLinkCountsByTime ...");
			
	
			TMapIVecI time_type_count= new TMapIVecI();	
			
			//for (int i = 0; i < this.getNumNodes(); ++i) {
			for (TimedNode node : nodes_) {
				//Counter.count100.step();
				
				for (Map.Entry<Integer,TimedLinks> p: node.relation_outlinks_.entrySet()){
					int relation= p.getKey();
					
					
					TimedLinks links =p.getValue();
					
					for (int t=0; t< links.times_.size(); ++t){
						VectorI v = time_type_count.getC(links.times_.get(t));
						
						if (v.size() == 0) v.extend(this.edgeType_index_.size());
						
						v.plusOn(relation, links.indices_.get(t+1)-links.indices_.get(t));
					}
				}
			}
			System.out.println(" ready");
			
			time_type_count.save(fn, "\t", "Time\t"+ this.edgeType_index_.list_.join("\t"));
//			BufferedWriter writer= FFile.newWriter(fn);
//			FFile.writeln(writer,"Time\t"+ this.edgeType_index_.list_.join("\t"));
//			
//			for (Map.Entry<Integer,VectorI> i: time_type_count.entrySet())
//				FFile.writeln(writer, i.getKey()+"\t"+ i.getValue().join("\t"));
//			
//			FFile.close(writer);
			
			System.out.println(" done");

			return true;
		}
	

	public static class TimedLinks implements Serializable, IGetIntByStr {
		private static final long serialVersionUID = 2008042701L;

		public VectorI nodes_ = new VectorI(); //entities sorted by time
		public VectorI times_ = new VectorI(); // sorted time
		public VectorI indices_ = new VectorI(); //sorted idx

		public int getIdx(int timeStamp) {
			int i = times_.findSorted(timeStamp);
			return indices_.get(i);
		}

		public TimedLinks(MapVecII time_links) {
			indices_.add(0);
			for (Map.Entry<Integer, VectorI> e : time_links.entrySet()) {
				nodes_.addAll(e.getValue());
				indices_.add(nodes_.size());
				times_.add(e.getKey());
			}
			return;
		}

		public Integer getInt(String tag) {
			if (tag.equals(CTag.size)) return nodes_.size();
			return null;
		}

		public VectorI getTimeVector() {
			VectorI vi = new VectorI();
			int idx = 0;
			for (int i = 1; i < indices_.size(); ++i)
				for (; idx < indices_.get(i); ++idx)
					vi.add(times_.get(i - 1));
			return vi;
		}
		public void addToSet(int time, SetI set) {
			int idx= getIdx(time);
			if (idx==0) return;
			
			for (int i=0; i< idx; ++i)
				set.add(nodes_.get(i));
		}
	}

	
//	
//	//public static boolean bKeepMMV = false;
//	public void indexTimedLinks() {
//		for (Map.Entry<Integer, MapVecII> e1 : relation_time_entities_.entrySet())
//			relation_outlinks_.put(e1.getKey(), new TimedLinks(e1.getValue()));
//
//		//if (!bKeepMMV) relation_time_entities_ = null;
//		return;
//	}		
//
	
	
	
//	//print compact graph from timed graph (ignore time information)
//	public String printPCTLinks(Graph graph, VectorI vNodeReIdx) {
//		if (relation_outlinks_.size() == 0) return null;
//
//		StringBuffer sb = new StringBuffer();
//
//		for (Map.Entry<Integer, TimedLinks> e1 : relation_outlinks_.entrySet()) {
//			int relation = e1.getKey();
//			TimedLinks ols = e1.getValue();
//			if (ols.nodes_.size() == 0) continue;
//
//			int ID = vNodeReIdx == null ? id_ : vNodeReIdx.get(id_);
//			sb.append((ID + 1) + " " + (relation + 1) + " " + ols.nodes_.size());
//
//			for (int id1 : ols.nodes_) {
//				int ID1 = vNodeReIdx == null ? id1 : vNodeReIdx.get(id1);
//				sb.append(" " + (ID1 + 1) + ":1");
//			}
//			sb.append("\n");
//		}
//		return sb.toString();
//	}



}
