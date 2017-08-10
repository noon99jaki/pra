package edu.cmu.lti.util.run;

import edu.cmu.lti.algorithm.container.VectorS;

public class FRun {
	public static String getArgumentValues(String args) {
		VectorS values = new VectorS();
		for (String arg: args.split(",")) {
			int p = arg.indexOf('=');
			if (p==-1) {
				System.err.println("bad formated arg: " + arg);
				continue;				
			}
			values.add(arg.substring(p+1));
		}
		return values.join(",");
	}
}
