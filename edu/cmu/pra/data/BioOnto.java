package edu.cmu.pra.data;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Pipe;
import edu.cmu.lti.util.file.FFile;

public class BioOnto {

	public static void extractOntoloty(String fn) {
		FFile.enuRows(fn, ",", true).select(new Pipe<VectorS, String>() {
			public String transform(VectorS v) {
				return "IsA(" + v.get(0) + "," + v.get(3) + ")";
			};

			public String transform(String[] v) {
				return "IsA(" + v[0] + "," + v[3] + ")";
			};
		}).save(fn + ".db");
	}
}
