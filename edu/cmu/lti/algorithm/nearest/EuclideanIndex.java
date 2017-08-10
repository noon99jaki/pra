package edu.cmu.lti.algorithm.nearest;

import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapISetI;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.TMapDX;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.system.FSystem;


//always ready to get the largest element?

public class EuclideanIndex {
	public static class Instance{
		public int id;
		public MapID mFe;
		public MapID mScore=new MapID();//ins-->score; with early entities
		public Instance(int id, MapID mFe){
			this.id=id;
			this.mFe=mFe;
		}		
		public String toString(){
			return String.format(
				"F/S=%d/%d", mFe.size(), mScore.size());
		}
	}
	
	public static class Feature{
		public MapID mIns=new MapID();
		public String toString(){
			return mIns.toString();
		}
	}
	
	public VectorX<Instance> vInstance= new VectorX<Instance>(Instance.class);
	//public TVector<Feature> vFea= new TVector<Feature>(Feature.class);
	public MapIX<Feature> mFeature= new MapIX<Feature>(Feature.class);

	public EuclideanIndex(){// int nFeature){//int nIns,
		//vvFeature= new VectorVectorI(nFeature);
		//vmFeature= new VectorMapID(nFeature);
	}
	//public VectorMapID vIns= new VectorMapID();	//ins-->weighted feature list
	//public VectorMapID vmScores= new VectorMapID();//ins-->ins-->score
	//public VectorVectorI vvFeature;//= new VectorVectorI();//ins-->ins-->score
	//public VectorMapID vmFeature;//= new VectorVectorI();//ins-->ins-->score
	/*public int bestI=-1;
	public int bestJ=-1;	
	public int bestScore=-1;	
	*/

	
	public static class Candi{
		public int i;
		public int j;
		public Double score;
		public Candi(int i, int j, Double score){
			this.i=i;
			this.j=j;
			this.score=score;
			if (i>=j) FSystem.die("Let's assume that i<j");
			
		}
	}
	//public TMapVectorDX<Rank> mvRanks= new TMapVectorDX<Rank>(Rank.class);
	
	//score--> i-->j list; assuming i<j
	//public TMapDX<MapVectorII> mmvRanks= new TMapDX<MapVectorII>(MapVectorII.class);
	public TMapDX<MapISetI> mmvRanks= new TMapDX<MapISetI>(MapISetI.class);

	public VectorX<Candi> getTopKCandidate(int k) {
		int n=0;
		VectorX<Candi> vRanks = new VectorX<Candi>(Candi.class);
		for (Map.Entry<Double, MapISetI> e : mmvRanks.entrySet()) {
			Double score = -e.getKey();
			for (Map.Entry<Integer, SetI> e2 : e.getValue().entrySet()) {
				int i = e2.getKey();
				for (int j : e2.getValue()){
					vRanks.add(new Candi(i, j, score));
					++n;
				}
			}
			if (n>=k)
				break;
		}
		return vRanks;
	}
	public Candi getTopCandidate() {
		VectorX<Candi> vRanks =getTopKCandidate(1);
		if (vRanks.size()==0) return null;
		return vRanks.firstElement();
	}



	//public void checkBest(int i, int j, Double score){}
	// assume ins are added in increasing order?
	
	
	/**
	 * TODO: apply sampling here
	 */
	public void addInstance(MapID mdFe){

		Instance ins=new Instance(vInstance.size(),mdFe);
		vInstance.add(ins);
		
		for (Map.Entry<Integer,Double>e: mdFe.entrySet()){
			Feature fe=mFeature.getC(e.getKey());
			Double w=e.getValue();
			ins.mScore.plusOn(fe.mIns, w);// TODO: do sampling here
			fe.mIns.put(ins.id, w);
		}

		for (Map.Entry<Integer,Double>e: ins.mScore.entrySet()){
			int i=e.getKey();
			Double score=e.getValue();
			//checkBest(i, ins.id, score);
			//if (i>=j)		FSystem.die("Let's assume that i<j");
			
			vInstance.get(i).mScore.put(ins.id, score);		
			//if (score<=0) return;
			mmvRanks.getC(-score).getC(i).add(ins.id);

		}
	}
	

	public void removeInstance(int id){
		//System.out.println("removing ins #"+id);
		Instance ins=vInstance.get(id);			
		
		for (Map.Entry<Integer,Double>e: ins.mFe.entrySet())
			mFeature.get(e.getKey()).mIns.remove(id);
		
		for (Map.Entry<Integer,Double>e: ins.mScore.entrySet()){
			int k=e.getKey();			
			vInstance.get(k).mScore.remove(id);
			
			Double score=-e.getValue();
			MapISetI mm= mmvRanks.get(score);
			if (k<id)
				mm.chainRemove(k,id);
			else
				mm.remove(id);		
			if (mm.size()==0)
				mmvRanks.remove(score);
		}
		
		vInstance.set(id,null);
	}
	
	
	public String toString(){
		return "\n[EuclideanIndex]\nvInstance=\n"+vInstance.join("\n")
			+ "\n\nmFeature=\n"+mFeature.join("=", "\n")
			+ "\n\nmmvRanks=\n"+mmvRanks.join("=", "\n")
			+ "\n";
	}
	
	public void merge(Candi c){
		System.out.println("mering ins #"+c.i+" and #"+c.j);

		MapID m=vInstance.get(c.i).mFe.plus(vInstance.get(c.j).mFe);
		this.removeInstance(c.i);
		//System.out.println(this);
		
		this.removeInstance(c.j);
		//System.out.println(this);
		
		this.addInstance(m);
		System.out.println(this);
		return;
	}
	public void clear(){
		
	}
	
	public static void main(String[] args) {
		EuclideanIndex idx= new EuclideanIndex();
		idx.addInstance(MapID.fromLine("1=0.3 2=0.2 3=0.1"));
		idx.addInstance(MapID.fromLine("2=0.3 3=0.2 4=0.1"));
		idx.addInstance(MapID.fromLine("3=0.3 4=0.2 5=0.1"));
		idx.addInstance(MapID.fromLine("4=0.1 5=0.2 6=0.3"));
		System.out.println(idx);
		
		for (int i=0; i<2;++i){
			Candi c= idx.getTopCandidate();
			idx.merge(c);
//			System.out.println(idx);

		}
		
		return;
	}
}
