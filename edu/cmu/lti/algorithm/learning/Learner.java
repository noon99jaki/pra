package edu.cmu.lti.algorithm.learning;

import java.io.BufferedReader;
import java.io.Serializable;

import edu.cmu.lti.algorithm.learning.Interfaces.IEva;
import edu.cmu.lti.algorithm.learning.data.DataSet;
import edu.cmu.lti.algorithm.learning.data.Instance;
import edu.cmu.lti.util.file.FFile;


/**
 * use case: 
 * 1) cross validation
 * 2) train/test save load model
 * 3) aggregate evaluation, produce report
 * @author nlao
 *
 */
public abstract class Learner implements Serializable{
	private static final long serialVersionUID = 20061012L; // YYYYMMDD
/*	
	protected Class cY, cX;//types of label and output 
	// = Object.class;
	protected Learner(Class cY, Class cX){
		this.cX = cX;
		this.cY = cY;
	}*/

	//public abstract IDataSet load(String fn);
	
	public abstract Instance newInstance();//{	return null;//new DataSet(cX, cY);}
	public String learnerName=this.getClass().getSimpleName();// "learner";

	public abstract void train(DataSet dsTrain);
	public abstract Instance test(Instance ins);
	
	public DataSet test(DataSet ds){
		DataSet ds1 = newDataSet();
		for (Instance ins: ds.v)
			ds1.add(ins);
		return ds1;
	}
	public abstract IEva evaluate(Instance ins1, Instance ins2);

	public DataSet newDataSet(){
		DataSet ds =  new DataSet(newInstance().getClass());
		return ds;
	}

	public abstract Instance parseLine(String line);

	public DataSet loadData(String fn){
		DataSet ds = newDataSet();
		BufferedReader br = FFile.newReader(fn);		
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			if (line.startsWith("#")) continue;
			Instance ins = parseLine(line);// newInstance();
			if (ins==null) continue;
			ds.add(ins);
		} 
		//MapSD
		return ds;
	}
	
	public void trainTest(String fnTrain, String fnTest){
		train(fnTrain);
		test(fnTest);
	}
	public void train(String fn){
		DataSet ds = loadData(fn);		
		train(ds);
		FFile.saveObject(this, fn+"."+learnerName);
	}
	//public abstract boolean load(String fnModel);
	
	public void test(String fn){
		DataSet ds = loadData(fn);		
		DataSet ds1 = test(ds);
		FFile.saveObject(this, fn+"."+learnerName);
	}
}
