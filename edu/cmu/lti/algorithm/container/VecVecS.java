/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.io.BufferedReader;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 *
 */
public class VecVecS  extends VectorX<VectorS>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VecVecS newInstance(){
		return new VecVecS();
	}	

	public VecVecS(){
		super(VectorS.class);
		//super(Double.class);
	}	

//	public static VecVecS load(String fn,  String cRow){// throws IOException {
//		return FString.parseTable(FFile.loadString(fn), "\n", cRow);
//	}

}
