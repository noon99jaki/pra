package edu.cmu.lti.algorithm.optimization;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

public abstract class  AModel {
	public VectorD param_weights_=new VectorD();
	public VectorS param_names_=new VectorS(); //=null;// feature names
	public VectorS param_comments_=new VectorS(); //=null;// feature names
	
	public MapSS feature_comments;

	public AModel(){
	}
	
	public abstract void setParameters(double[] x);
	public abstract double[] getParameters();

	
	public String getWeightCount(){
		return param_weights_.countNonZero()
			+"\t"+param_weights_.size()
			+"\t"+param_weights_.norm1();		
	}
	
	public void clearWeights(){
		param_names_.clear();
		param_weights_.clear();
		param_comments_.clear();
	}
	
	// load weights to an existing model
	public void loadWeights(String model_file){
		//initWeights();
		MapSI mParamID= param_names_.toMapValueId();
		
		for (VectorS vs: FFile.enuRows(model_file, "\t",true)){
			double weight=Double.parseDouble(vs.get(0));
			String feature=vs.get(1);
			
			if (!mParamID.containsKey(feature)) continue;
				//FSystem.die("unmatched feature name="+feature);
			
			param_weights_.set(mParamID.get(feature),weight);
		}
		setParameters(param_weights_.toDoubleArray());	//irony
	}
	
	
	public void saveModel(String model_file, boolean shrink){		
		BufferedWriter bw=FFile.newWriter(model_file);
		
		//FFile.write(bw,"weight\tfeature\tcomments\n" );
		for (int i=0; i<param_names_.size();++i){
			if (shrink) 
				if (param_weights_.get(i) == 0.0) 
					continue;
			
			FFile.write(bw,"%.5f\t%s\t%s\n",	
				param_weights_.get(i),
				param_names_.get(i),
				param_comments_.getD(i, ""));
		}
		FFile.close(bw);
	}


}
