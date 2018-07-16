package edu.cmu.lti.nlp.parsing;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.rmi.RemoteException;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.Interfaces.ISynxParseTaggedSent;
import edu.cmu.lti.nlp.parsing.FTools.EvalbPR;
import edu.cmu.lti.nlp.parsing.ParserService.IServer;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.html.FHtml;
import edu.cmu.lti.util.html.HtmlPage;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

// missing dependency
//import edu.cmu.nlsa.nlsa.AParsing;

public abstract class Parser implements ISynxParseTaggedSent, IServer{
	public enum Task {
		train, test, service, exp, exp1//experiment
	}
	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD
		//public String lang;
		public String ctbTreeFile;
		public String trainFile;
		public String testFile;
		public Task task;
		public int dbg=0;
		public int maxSent=-1;
		public boolean deploy=true;
		public String modelFile;
		public String codeTmp;
		public boolean bPrintChartTrain;	
		public boolean bPrintChartTest;	
		public boolean bPrintTestTrees;
		
		public Param(Class c) {
			super(c);//MaxEntParser.class);
			parse();
		}
		public String sDParamCode;
		public void parse() {
			sDParamCode=getBaseCode();//"vDParam");

			
			task = Task.valueOf(getString("task", Task.service.name()));
//			lang = getString("lang","zh_CN");
			//ctbTreeFile = getString("ctbTreeFile");
			trainFile = getString("trainFile",null);
			testFile = getString("testFile",null);
			deploy = getBoolean("deploy", false);
			modelFile = path_data+"/net";
			codeTmp = getString("codeTmp",null);
			bPrintChartTrain=getBoolean("bPrintChartTrain",false);
			bPrintChartTest=getBoolean("bPrintChartTest",false);
			bPrintTestTrees=getBoolean("bPrintTestTrees",false);
		}
	}
	
	public Param p=null;
	public Parser(Class c){
		p = new Param(c);
	}
	
	public abstract void trainRaw();
	public double secTrain=0 ;
	public void train(){
		StopWatch log = new StopWatch();//		FSystem.currentTime();
		
		trainRaw();
		
		secTrain = log.getSec();
		summary += String.format(
				"Train %s\t[time]\t%s\t[memory]\t%s\n"
				,FHtml.addHref("details",code+"/train.html")
				,FSystem.formatTime((long)secTrain)
				,FSystem.memoryUsage());

		if (p.deploy){			
			FFile.mkdirs(p.path_data);
			FFile.copyFile(code+"/model", p.path_data+"/model");
		}			
		return;
	}
	
	//public void test(){	}
	
	public abstract boolean loadModel();
	public double exp(){return 0;};
	public double exp1(){return 0;};
	
	public void testRaw(){//int maxSent) {		
		//FFile.mkdirs(code+"/err");
		int nSent=0;
		for (String line: FFile.enuLines(p.testFile)){
			++nSent;	if (nSent % 10==0)	
				System.out.print("t");//System.exit(-1);//
			
			if (p.maxSent>=0) 
				if (nSent==p.maxSent)
					break;
			
			TreeSyntax tree0=TreeSyntax.parseNew(line);
			if (tree0==null) 
				continue;
			//if (p.dbg>0)	System.out.println(tree0);				
			//TreeSyntax tree1= synxParseTaggedSent(tree0.getTerminals());
			
			if (thChartTest!=null)
				thChartTest.addPre("sent"+nSent+"="+tree0.vWord.join(" "));
			TreeSyntax tree1= synxParseSent(tree0.vWord);
			
			String str0 = tree0.printBracketString();
			String str1 = tree1.printBracketString();
			if (p.bPrintTestTrees)
			if (!str0.equals(str1)){//print inperfectly parsed sentences to file
				System.out.print("*");
				FFile.write(bwTreeGold,"[Gold "+nSent+"]\n"+tree0.toString(1)+"\n\n");
				FFile.write(bwTreeSys,"[System "+nSent+"]\n"+tree1.toString(1)+"\n\n");
			}			
			FFile.write(bwOutSys, str1+"\n");
		}
	}
	protected void printTrees(int id, TreeSyntax treeGold, TreeSyntax treeSys){
		
	}
	protected BufferedWriter bwOutSys=null, bwOutGold=null;
	protected BufferedWriter bwTreeSys=null, bwTreeGold=null;
	public HtmlPage thChartTest =null;
	public HtmlPage thNEI =null;
	protected BufferedWriter bwDbg=null;
	public double test() {
		HtmlPage th = new HtmlPage("test."+p.testFile // missing dependency --> +AParsing.p.codeTest
				,code);//p.learner.name(),""); "test"
		
		//th.newTable("confusion matrix", mm);
		if (p.bPrintChartTest)	
			thChartTest= th.extPageF("charts");
			//thChartNEI= new TableHtml("neighbors", code, code);
		bwOutSys = th.extFileF("sys");
		bwOutGold = th.extFileF( "gold");
		if (p.bPrintTestTrees){
			bwTreeSys =th.extFileF("tree.sys");
			bwTreeGold = th.extFileF("tree.gold");
		}
		bwDbg = th.extFileF( "sbDbg");
		
		
		testRaw();
		th.closeChildren();
		
		th.addPre("\n================labled=================\n");		
		String txtEva = FTools.evalb(				
				th.getFilePath()+ "/gold"			
			, th.getFilePath()+ "/sys");
		th.addPre(txtEva);
		
		th.addPre("\n================unlabled=================\n");
		String txtEvaU = FTools.evalb(
				th.getFilePath()+ "/gold"			
			, th.getFilePath()+ "/sys",false);
		th.addPre(txtEvaU);		
		th.close();
		
		EvalbPR eva = EvalbPR.newEva(txtEva);
		EvalbPR evaU = EvalbPR.newEva(txtEvaU);
		
		System.out.println(eva	);
		System.out.println(evaU	);
		summary += "test"	+FHtml.addHref(	"details",th.getURL())+"\t"
			+"L"+eva+"\tU"+evaU+"\n";

		FFile.appendToFile(String.format(
				"%s\t%s\t%.2f\t%.2f\t%d\t%s\n"
				,p.sDParamCode,args, eva.f1, evaU.f1
				, secTrain 	,sTrainRlt)		,"result.txt");
		
		return eva.f1;
	}
	public String sTrainRlt=null;

	
	public String code="";//null;
	protected String summary=null;
	public double run() {
		FFile.mkdirs("score");
		double score=0;
		System.out.println("task="+p.task);
		summary = "\ntask="+p.task+"\t"+FSystem.printTime()+"\n"
							+"code="+code.replace("/","\t")+"\n";
		
		FFile.mkdirs(code);

		switch (p.task) {
		case train:		train();		break;
		case test:		loadModel();score=test();		break;
		case service:		loadModel();		break;
		case exp:		score=exp();		break;
		case exp1:		score=exp1();		break;
		}
		
		String fn = "summary.html";
		if (!FFile.exist(fn))
			FFile.copyFile("summary0.html",fn);
			//FSystem.cmd("cp summary0.html "+fn);
		
		String html = FFile.loadString(fn);
		
		summary=summary.replace("\n", "<br>\n");
		html = html.replaceFirst("<body>"
				,"<body>\n"+summary +"<br>\n");
		FFile.saveString(fn,html);
		//FFile.saveString(""+score, code+"/score");
		if (args!=null){
			FFile.mkdirs(p.sDParamCode);
			FFile.saveString(p.sDParamCode+"/"+args, ""+score);
		}
		return score;	
	}
	public static String args=null;

	public TreeSyntax synxParseTaggedSentRMI(VectorToken vt) throws RemoteException {
		return synxParseTaggedSent(vt);
	}
	public abstract TreeSyntax synxParseTaggedSent(VectorToken vt) ;
	public abstract TreeSyntax synxParseSent(VectorS vWord) ;

}
