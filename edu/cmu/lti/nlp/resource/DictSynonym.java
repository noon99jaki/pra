package edu.cmu.lti.nlp.resource;

import edu.cmu.lti.algorithm.container.MapSVecI;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VecVecS;

public class DictSynonym {
	VecVecS vvs_dict = new VecVecS();
	//MapSI msi_index = new MapSI();
	
	MapSVecI index= new MapSVecI();
	public int addSynSet(VectorS vs){
		int id = vvs_dict.size();
		vvs_dict.add(vs);
		for (String w: vs)
			index.getC(w).add(id);
		return id;
	}
	public VectorI getSynSet(String word){
		return (VectorI) index.get(word);
	}	
}
