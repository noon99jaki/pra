package edu.cmu.lti.algorithm.structure;

import java.util.Map;

import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;

public class IndexS extends IndexX<String>{

	public IndexS() {
		super(String.class);
		list_ = new VectorS();
	}

	// Set the index to a set of numbers
	public void setSeq(int n) {
		list_ = VectorI.seq(n).toVectorS();
		map_ = list_.toMapValueId();
	}
	
	// Sort the keys to string order
	public void sortKeys() {
		int i = 0; 
		for (Map.Entry<String, Integer> e: map_.entrySet()) {
			e.setValue(i);
			list_.set(i, e.getKey());
			++i;
		}
	}
		
	public void loadText(String file_pattern) {
	  clear();	  
	  addAll(FFile.enuLines(file_pattern));
	}
	
	public void loadTextColumn(String file_pattern, int column) {
	  clear();	  
	  addAll(FFile.enuACol(file_pattern, column));
	}
}
