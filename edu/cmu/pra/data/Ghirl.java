package edu.cmu.pra.data;



import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.util.file.FFile;

public class Ghirl {
	public static void findLinkTypes(){
		SetS m= new SetS();
		for (String line: FFile.enuLines("../data/yeast2.cite.TEXTedges.db"))
			m.add(line.split("\\(")[0]);
		System.out.print(m.join("\n"));
	}
	//findLinkTypes();

}
