package edu.cmu.pra.data;

import edu.cmu.lti.util.run.Param;
import edu.cmu.pra.postprocess.Latex;

public class Woolford {
	
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
			Latex.latexPathYeast("Models");
	
/*		Param.overwrite("collaborateK=1");		Recom.run();
			Param.overwrite("collaborateK=3");		Recom.run();
			Param.overwrite("collaborateK=10");		Recom.run();
			Param.overwrite("collaborateK=30");		Recom.run();
			Param.overwrite("collaborateK=100");		Recom.run();
	*/		
			/*				Param.overwrite("collaborateK=300");		
			Param.overwrite("recomMode=CH,scSelf=0");		Recom.run();
			Param.overwrite("recomMode=CH,scSelf=1");		Recom.run();
					
		*/
			//Param.overwrite("recomMode=C");		Recom.run();

			return;
		}
}
