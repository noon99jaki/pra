package edu.cmu.lti.algorithm.learning.data;

import java.io.BufferedWriter;
import java.io.Serializable;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.minorthird.classify.Dataset;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.multi.MultiDataset;


//how to generalize from String->MapSD 
//to any data of learning problems (especially structural)?
//extends TVector<Instance> this cause bad serialization?
public class DataSet  implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	
	public VectorX<Instance> v=null;
	public DataSet(Class c){
		v =new VectorX<Instance>(c) ;
	}
	public DataSet add(Instance ins){
		v.add(ins);
		return this;
	}
	public String toString(){
		return v.join("\n")+"\n";
	}
	public Example[] toM3rdExamples(boolean bTraining){
		Example[] examples = new Example[v.size()];
		for (int i=0; i<v.size(); i++)
			examples[i] = v.get(i).toM3rdExample(bTraining);		
		return examples;
	}
	public Dataset toM3rdDataSet(boolean bTraining){
		Dataset ds = new MultiDataset();
		for (Instance ins: v)
			ds.add(ins.toM3rdExample(bTraining));
		return ds;
	}
	//protected Class cY, cX;//types of label and output
	// = Object.class;
/*	protected DataSet(Class cY, Class cX){
		this.cX = cX;
		this.cY = cY;
	}
	*/
	//dont be too general
	//let's refactor later
	//public DataSet(){	}

	
	public boolean load(String fn){
		for (String line: FFile.enuLines(fn)){
			if (line.startsWith("#")) continue;
			Instance ins =  v.newValue();
			if (!ins.parseLine(line)) continue;
			v.add(ins);
		} 
		return true;
	}
	public boolean save(String fn){
		//System.out.println("saving dataset to text file "+fn);
		BufferedWriter bw  = FFile.newWriter(fn);
		for (Instance ins: v){
			FFile.write(bw, printInstance(ins));
			FFile.write(bw, "\n");
		}
		FFile.flush(bw);		
		return true;
	}	
	//instance may need a index to print itself
	public String printInstance(Instance ins){
		return ins.toString();
	}
}
