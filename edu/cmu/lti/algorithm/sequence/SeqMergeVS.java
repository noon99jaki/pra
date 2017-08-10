package edu.cmu.lti.algorithm.sequence;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.file.FFile;

public class SeqMergeVS extends Seq<VectorS>{
	
//private Iterator<T> it=null;
public SeqMergeVS(String files) {
	super(VectorS.class);
	VectorX<SeqVS> readers = new VectorX<SeqVS>(SeqVS.class);
	for (String file: FFile.getFiles(files))		
		readers.add(FFile.enuRows(file));
}
public boolean hasNext()  {
//	if (it==null) return false;
//	
//	do{
//		if (!it.hasNext()) return false;
//		if (pipe==null)
//			x=it.next();
//		else
//			x= (T) pipe.transform(it.next());
//	}while(x==null);
	
	return true;
}
}