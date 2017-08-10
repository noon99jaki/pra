package edu.cmu.lti.algorithm.ir;

import java.io.BufferedReader;
import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.TMapDVecI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VecVecI;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.text.FString;

public class SimpleIndex {
	//boolean bCase=false;
	public SimpleIndex(){
		
	}
	
	public static class CTag{
		public static String DF= "DF";
		public static String IDF= "IDF";
	}
	public static class Doc{
		public int id;
		public String txt;
		public String eid=null;//external id
		public double normIDF=0;
		public VectorI viTerm= new VectorI(); 
		
		public Doc(int id, String txt, String eid){
			this.id = id;
			this.txt=txt;
			this.eid=eid;
		}
		public String toString(){
			return id+"";
			//return String.format("%.2f",lenIDF);
			//return eid;
		}

	}
	public static class Lemma implements IGetIntByStr, IGetDblByStr{
		
		public int id=-1;
		public String txt;
		public int df=0;
		public double idf=0;
		public VectorI viDoc= new VectorI(); 
		//public int weight;
		public Lemma(int id, String txt){
			this.id=id;
			this.txt=txt;
		}
		@Override public Double getDouble(String name) {
			if (name.equals(CTag.IDF))
				return idf;
			return null;
		}
		
		@Override public Integer getInt(String name) {
			if (name.equals(CTag.DF))
				return df;
			return null;
		}
		public String toString(){
			return txt;
		}
	}
	
	public VectorX<Doc> vDoc= new VectorX<Doc> (Doc.class);
	//StringIndex si= new StringIndex();
	
	public MapSI mLemma = new MapSI();
	public VectorX<Lemma> vLemma = new VectorX<Lemma>(Lemma.class);
	
	public Lemma tryAddTerm(String term){
		Integer i=mLemma.get(term);
		if (i==null){
			i=vLemma.size();
			vLemma.add(new Lemma(i, term));
			mLemma.put(term,i);
		}
		Lemma l=vLemma.get(i);
		++l.df;
		return l;
	}
	


	public Doc addDoc(String txt, String  eid){
		txt=txt.toLowerCase();
		Doc d= new Doc(vDoc.size(), txt, eid);		
		vDoc.add(d);
		for (String term: FIR.tokenize(txt)){
			Lemma l=tryAddTerm(term);
			l.viDoc.add(d.id);	
			d.viTerm.add(l.id);
		}
		return d;
	}
	
	/**
	 * assuming each line is a id-->doc
	 * 		for (String line: FFile.enuLines(fn))			 
			addDoc(line);
	 * @param fn
	 * @return
	 */
	public boolean loadDocs(String fn){
		
		System.out.println("loading docs from "+fn);
		
		BufferedReader br = FFile.newReader(fn);	
		if (br==null) return false;
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			String vs[]= FString.split(line);
			if (vs[1].length()==0) continue;
			addDoc(vs[1], vs[0]);
		}
		FFile.close(br);
		System.out.println(vDoc.size()+" documents loaded");
		
		estimateIDF();
		return true;
	}
	public VectorI lookup(String query){
		VectorS vs=FIR.tokenize(query);
		//VectorI vi= mLexicon.subV(vs);
		//vi.remove(null);
		return mLemma.subVIgNull(vs);
	}
	int N=0;
	public void estimateIDF(){
		N= vDoc.size();
		for (Lemma l: vLemma){
			l.idf =  Math.log((N+0.5)/(l.df+0.5));//-l.df
			if (l.idf<=0)
				System.err.print("NaN");
		}
		for (Doc d: vDoc){
			d.normIDF=0;
			for (int iW: d.viTerm)
				d.normIDF+= vLemma.get(iW).idf;
			d.normIDF= Math.sqrt(d.normIDF);
		}
	}
	public VectorI rltDoc=new VectorI();
	public VectorD rltScore=new VectorD();
	
	//VectorVectorI vvInvertedIndex= new VectorVectorI();

	public MapID retrieve(String query, double th){
		VectorI viTerm = lookup(query);
		if (viTerm.size()==0)
			return null;
		
		VectorX<Lemma> vL= vLemma.sub(viTerm);
		VectorD vdWeight= vL.getVD(CTag.IDF);
		vdWeight.normalizeL2On();
		
		 ///////////ignore small words////////
		VectorI idx = vdWeight.idxLargerThan(th*0.1);
		vL= vL.sub(idx);
		vdWeight=vdWeight.sub(idx);
		viTerm=viTerm.sub(idx);
		
		
		/////////// feature matrix////////
		VecVecI vv= new VecVecI();
		vv.ensureCapacity(vL.size());
		for (Lemma l: vL)
			vv.add(l.viDoc);
		
		/////////// rank by IDF weighted cosine similarity////////
		MapID mRlt=new MapID();
		for (Map.Entry<Integer, Double>e: 
			vv.weightedSum(vdWeight).entrySet()){
			Doc d= vDoc.get(e.getKey());
			//System.out.println("[candi] "+d.txt);
			double s= e.getValue()/ d.normIDF;	//normalize

			if (s<th) continue;
			mRlt.put(d.id, s);
		}
		
		/////////// sort result////////
		TMapDVecI mv =mRlt.ValueKeyToMapVec();
		rltDoc= mv.toVectorV(); rltDoc.reverseOn();
		rltScore= mv.toVectorK(); rltScore.reverseOn();
		
		return mRlt;
	}
}
