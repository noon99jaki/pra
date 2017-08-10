package edu.cmu.lti.algorithm.learning.data;

import java.io.BufferedWriter;
import java.io.Serializable;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.minorthird.classify.Dataset;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.RandomAccessDataset;


public class DataSetBinaryI  extends DataSet implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	/*
	public Instance parseLine(String line) {
		String vs[] = line.split("[ :]");
		MapID m = new MapID();
		for (int i = 1; i < vs.length; i += 2) {
			double value = Double.parseDouble(vs[i + 1]);
			if (idxFeature != null) 
				m.put(idxFeature.getID(vs[i]), value);
			else 
				m.put(Integer.parseInt(vs[i]), value);
		}
		return new InstanceBinaryI(idxLabel.getID(vs[0]), m);
	}*/
	public Index idxLabel = new Index();//= null;// 
	public Index idxFeature = new Index();//= null;// 

	public String info(){
		return String.format("#ins=%d #feature=%d #label=%d"
			,v.size(),idxFeature.v.size(),idxLabel.v.size());
	}
	/**	 * 
	 * @param n: minimun number of occurance
	 */
	public void shrinkInfrequentFeature(int n){
		//new IDs, with -1 indicating deleted feature
		if (n<=0) return ;
		VectorI viMap= idxFeature.shrinkInfrequentFeature(n);
		//v.updateIdx(viMap);
		for (Instance ins0 : v){
			InstanceBinaryI ins =((InstanceBinaryI) ins0);
			ins.m = viMap.subSet(ins.m);
			ins.m.remove(-1);
		}
	}
	public DataSetBinaryI addInstance(InstanceBinaryS ins){
		v.add(new InstanceBinaryI(
				idxLabel.index(ins.label)
				, idxFeature.index(ins.m)));
		return this;
	}
	public DataSetBinaryI addAll(DataSetBinaryS ds){
		for (Instance ins : ds.v)
			this.addInstance((InstanceBinaryS) ins);
		return this;
	}
	public InstanceBinaryI mapInstance(InstanceBinaryS ins){
		return new InstanceBinaryI(
				idxLabel.map(ins.label)
				, idxFeature.map(ins.m));
	}
	
	public DataSetBinaryI(){
		super(InstanceBinaryI.class);
	}
	public InstanceBinaryS toInstanceBinaryS(InstanceBinaryI ins){
		return new InstanceBinaryS(
				idxLabel.v.get(ins.label)
				,(SetS) idxFeature.v.subSet(ins.m));
	}
	public RandomAccessDataset toM3rdDataSet(boolean bTraining){
		System.out.println("transforming to M3rd data ..");
		RandomAccessDataset ds = new RandomAccessDataset();
		for (Instance ins: v){			
			InstanceBinaryS insS= toInstanceBinaryS((InstanceBinaryI)ins);
			Example e= insS.toM3rdExample(bTraining);
			ds.add(e);//, true);
		}
		return ds;
	}
	
	public String printInstance(Instance ins){
		InstanceBinaryS insS= (InstanceBinaryS) toInstanceBinaryS(
			(InstanceBinaryI)ins);
		return insS.toString();
	}	
}
