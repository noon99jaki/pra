package edu.cmu.lti.algorithm.learning.tools;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.system.MyProcess;

public class LeMaxEnt  {
	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String binFile;
		public Param(){//Class c) {
			super(LeMaxEnt.class);
			parse();
		}
		
		public void parse() {
			binFile = getString("maxent"
				,"/home/javelin/extern/maxent-20041229/bin/maxent");
		}
	}
	public static Param p = new Param();

	private MyProcess proc = new MyProcess();
	public LeMaxEnt(){
		
	}
	public void loadModel(String modelFile) {
		proc.start(p.binFile, "-p", "-m", modelFile);
	}
	
	public VectorS  classifySorted(SetS fe) {
		//VectorS vAction = new VectorS();
		String line = proc.pushPop("? " + fe.join(" ") + "\n");
		String vs[] = line.split("[ \t\n\r\f]");
		//vAction.addAll(vs);
		return new VectorS(vs);
	}
	
	/**	
	 */
	public static void train(String dataFile, String modelFile){
		FSystem.cmd(String.format("%s -m%s  -b -g2 -i200 %s"
				, p.binFile, modelFile, dataFile));
	}		
}

/**
 Usage: maxent [OPTIONS]... [FILES]...
   -h         --help            Print help and exit
   -V         --version         Print version and exit
   -v         --verbose         verbose mode (default=off)
   -mSTRING   --model=STRING    set model filename
   -b         --binary          save model in binary format (default=off)
   -oSTRING   --output=STRING   prediction output filename
              --detail          output full distribution in prediction mode (default=off)
   -iINT      --iter=INT        iterations for training algorithm (default='30')
   -gFLOAT    --gaussian=FLOAT  set Gaussian prior, disable if 0 (default='0.0')
   -cINT      --cutoff=INT      set event cutoff (default='1')
              --heldout=STRING  specify heldout data for training
   -r         --random          randomizing data in cross validation (default=off)
              --nommap          do not use mmap() to read data (slow) (default=off)

   Group: MODE
   -p         --predict         prediction mode, default is training mode
   -nINT      --cv=INT          N-fold cross-validation mode (default='0')

   Group: Parameter Estimate Method
              --lbfgs           use L-BFGS parameter estimation (default)
              --gis             use GIS parameter estimation

*/