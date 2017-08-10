package edu.cmu.lti.nlp.parsing;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;


public class FTools {
	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD
		
		public String evalb;//location of the program evalb
		public Param(){//Class c) {
			super(FTools.class);
			parse();
		}
		
		public void parse() {
			evalb=getString("evalb","/usr2/nlao/tools/nlp/parsing/eva/EVALB");
		}
	}
	public static Param p = new Param();
	//evalb -p COLLINS.prm 25.gold.standard.txt 25.output.txt >25.eva.txt
	//https://ufal.mff.cuni.cz/user:zeman:parsing-evaluation
	//http://nlp.cs.nyu.edu/evalb/
	public static String evalb(String fGold, String fSys){
		return evalb(fGold, fSys, true);
	}	
	public static String evalb(String fGold, String fSys, boolean bLabeled){
		//String prm="COLLINS.prm";
		String prm=null;
		if (bLabeled)
			prm="new.prm";//"labeled.prm";//
		else
			prm="new.U.prm";//"unlabeled.prm";
		return FSystem.cmd(String.format("%s/evalb -p %s/%s %s %s"
			, p.evalb, p.evalb,prm, fGold, fSys));
	}
	
	
	public static class EvalbPR{
		public double p, r, f1;
		public double p_40, r_40, f1_40;
		public EvalbPR(double p,double r,double f1
				,double p_40,double r_40,double f1_40){
			this.p=p;
			this.r=r;
			this.f1=f1;
			this.p_40=p_40;
			this.r_40=r_40;
			this.f1_40=f1_40;
		}
		public EvalbPR(){
			
		}
		public static final int iNum=28;
		public void parse(String eva){
			VectorS vs = FString.splitVS(eva, "\n|\r\n").subMatch("Bracketing.*");
			//int i1 = eva.indexOf(str)
			if (vs.size()==0){
				System.err.println("evalbGetPR failed");
				return;
			}
			p = FString.parseDouble(vs.get(1).substring(iNum));
			r = FString.parseDouble(vs.get(0).substring(iNum));
			f1= FString.parseDouble(vs.get(2).substring(iNum));
					
			p_40 = FString.parseDouble(vs.get(4).substring(iNum));
			r_40 = FString.parseDouble(vs.get(3).substring(iNum));
			f1_40= FString.parseDouble(vs.get(5).substring(iNum));
		}	
		public static EvalbPR newEva(String txt){
			EvalbPR eva = new EvalbPR();
			eva.parse(txt);
			return eva;
		}
		public String toString(){
			return String.format("P=%.2f\tR=%.2f\tF1=%.2f"
					, p, r, f1);
			/*return String.format(
				"[All]\tPrec=%.2f\tRecall=%.2f\tF1=%.2f\n"
				+"[<=40]\tPrec=%.2f\tRecall=%.2f\tF1=%.2f\n"
				, p, r, f1,	 p_40, r_40, f1_40);*/
		}
	}
}
