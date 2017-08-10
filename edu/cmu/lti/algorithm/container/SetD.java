package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;

import edu.cmu.lti.util.file.FFile;

public class SetD  extends SetX<Double>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public SetD newInstance(){
		return new SetD();
	}	
	public VectorX<Double >newVector(){
		return new VectorD();		
	}
	public Double newKey(){//needed for primitive classes, silly java
		return 0.0;
	}	
	public SetD(){
		super(Double.class); 
	}	

	public boolean loadFile(String fn){
		//System.out.println("loading dataset from text file"+fn);
		BufferedReader br = FFile.newReader(fn);	
		if (br==null) return false;
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			//if (line.startsWith("#")) continue;
			this.add(Double.parseDouble(line));
		} 
		return true;
	}
	public VectorD toVector(){
		return (VectorD) super.toVector();
	}
	
	public void sortedInsertDesc(double d, int maxLen){
		if (size()<maxLen){
			this.add(d);
			return;
		}
		if (d<=this.first())
			return;
		this.add(d);
		this.remove(this.first());
		return;
	}
}
