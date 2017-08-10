package edu.cmu.lti.nlp.parsing.SRParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapSMapSD;
import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.ir.eva.ClassificationEval;
import edu.cmu.lti.algorithm.learning.data.DataSetBinaryI;
import edu.cmu.lti.algorithm.learning.data.DataSetBinaryS;
import edu.cmu.lti.algorithm.learning.data.InstanceBinaryS;
import edu.cmu.lti.algorithm.learning.tools.LeMaxEnt;
import edu.cmu.lti.nlp.chinese.CConsts;
import edu.cmu.lti.nlp.parsing.Parser;
import edu.cmu.lti.nlp.parsing.FTree.FTransformBinary;
import edu.cmu.lti.nlp.parsing.FTree.FindHead;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.FHtml;
import edu.cmu.lti.util.html.HtmlPage;
import edu.cmu.lti.util.run.Cacher;
import edu.cmu.lti.util.system.MyProcess;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Classifier;
import edu.cmu.minorthird.classify.Example;
import edu.cmu.minorthird.classify.RandomAccessDataset;

/**
 * a learning based shift reduce parser described in
		Mengqiu Wang, Kenji Sagae and Teruko Mitamura, 
		A Fast, Accurate Deterministic Parser for Chinese, 
		In Proceedings of COLING/ACL '06, 2006

 * This class read in test files and output new trees with head information
 * marked in a pretty printing format. Optionally, this class reads in a
 * gold-standard file(s), and compare them against the test files
 * 
 */
public class SRParser extends Parser  {
	private static SRParser instance = null;
	public static SRParser getInstance() {
		if (instance==null)
			instance = new SRParser();
		return instance;
		//return getInstance(Param.ms.get("lang"));//p.lang);
	}

	/*public static SRParser getInstance(){//String lang) {
		//if (lang.equals(CLang.zh_CN)) return SRParserCh.getInstance();
		//return null;
	}*/
	FeatureExtractor fex;
	public static enum ELearner{
		MaxEnt
		, NaiveBayes, DecisionTree
		, VitorBalancedWinnow
		, Maxent,Maxent20
		, SVMLinear,BBMira
		, MarginPerceptron, MarginPerceptronX
		,	VotedPerceptron, VotedPerceptronX
		
	}

	public enum Task {
		train, test, service, exp//experiment
	}
	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD
//		public String lang;
		public String maxent;
		public String model;
		public int UNARY_LIMIT = 4;
		public ELearner learner;
		public int minFeatureFreq=5;
		
		public Param(){//Class c) {
			super(SRParser.class);
			parse();
		}


		public void parse() {
			maxent = getString("maxent");
			UNARY_LIMIT = getInt("UNARY_LIMIT", 4);
			learner = ELearner.valueOf(getString(
				"learner",ELearner.Maxent.name()));
			model = path_data + "/"+learner.name();
			minFeatureFreq = getInt("minFeatureFreq", 5);
			}
	}

	public double exp(){
		//RandomAccessDataset ds=	 getDataM3rd(p.testFile);
		//RandomAccessDataset ds=	 getDataM3rd(p.testFile,0);
	
		//train();startModel();		
		loadModel();		
		testClassifier();			
		test();
		return 0.0;
	}

	
	public Param p;//= new Param();
	private int unaryCount = 0;
	/*
	private BufferedReader testFileReader;
	private PrintWriter socketOut;
	private BufferedReader socketIn;
	private boolean CH_initialized = false;
	*/
	
	//process for using stand alone learners online
	private MyProcess proc = new MyProcess();
	private Classifier classifier= null;
	private FindHead fh=null;
	private FTransformBinary tb=null;
	protected String codeData=null;
	//protected String localModel=null;

	protected SRParser(){//Class c) {
		super(SRParser.class);		
		p = new Param();//c);

		codeData= super.p.trainFile +".FF"+p.minFeatureFreq;//+".data");
		code = codeData+"."+p.learner.name();
		//localModel=code+"/model";

		fh=FindHead.getInstance();
		tb=FTransformBinary.getInstance();
	}

	public boolean loadModel() { 
		System.out.print("Starting model "+p.learner.name()+" ...");
		String fn = p.path_data+"/"+ p.learner.name();			
		switch(p.learner){
		case MaxEnt:
			proc.start(p.maxent, "-p", "-m", fn);
			break;
		default:
			classifier = (Classifier) FFile.loadObject(fn);
			if (classifier==null)
				return false;
			break;
		}
		System.out.println("done");
		return true;
	}

 
	
	//POS tagged words for parsing
	VectorX<Token> queue;
	
	//fully/partially generated tree
	TreeSyntax tree;
	
	//parsing stack with pointers to the tree.v_node
	VectorI stack;

	/*** 
	 * @param action
	 * @param pseudo
	 * @return the node created
	 */
	private Node doAction(String action, boolean pseudo) {
		fex.lastAction = action;
		if (action == null) 
			return null;
		
		//create new node 
		Node n=null; int ip=-1;
		
		if (action.equals("shift")) {
			Token t = queue.popFrontOn();			
			if (t == null)		return null;
			if (t.getText().matches(CConsts.paOpen)) 
				fex.bracketCount++;
			else if (t.getText().matches(CConsts.paClose)) 
				fex.bracketCount--;
			if (pseudo){
				n = tree.getNode(tree.viNode.getRight(queue.size()));				
				if (!n.t.getText().equals(t.getText())) 
					System.exit(-1);//validate text from tree and queue
			}
			else{
				n = tree.addTerminal(t);
			}
		}
		else{ //reduce
			
			String vs[] = action.split(":");
			String dir = vs[0];
			String pos = vs[1];
			//get children 
			VectorI vc=null;
			if (dir.equals("U")) {
				if (stack.size() == 0)		return null;
				vc = new VectorI(1,stack.pop());
			} 
			else if (dir.equals("B")) {
				if (stack.size() <= 1) 		return null;
				vc = (VectorI) stack.pop(2);
			} 
			else 	return null;	
			
			if (pseudo){
				ip = tree.getNode(vc.lastElement()).iparent;
				n =tree.getNode(ip);
				if (vc.size()!= n.vc.size())
					System.exit(-1);//validate 
				if (!n.t.getPOS().equals(pos))
					System.exit(-1);//validate pos from tree and queue
			}			
			else{	
				n = tree.addNonTerminal(vc, pos);		
			}

		}
		stack.add(n.id);			//update stack	
		//update features
		if (!pseudo)
			fh.setHead(tree, n.id);
			//n = tree.addNode(null, pos);	
		tree.addFeaturePU(n);
		return n;
	}

	
	/**
	 * eat POS tagged sentence, spit parse tree 2008.4.14 dare not to change Tree
	 * structure this week
	 */
	public TreeSyntax synxParseTaggedSent(VectorToken vt) {
		queue = vt.clone();//.reverse();
		tree = new TreeSyntax();
		stack = new VectorI();
		fex.reset();
		for (int i = 0; i < queue.size() || stack.size() > 1;) {
			String action = decideAction();//false);
			if (super.p.dbg>0)
				System.out.println(action);

			if (doAction(action, false)==null) break;
		}
		finishTree();		
		return tb.debinarizeOn((TreeSyntax)tree.clone());//tree remains not binarized
	}

	/**
	 * aftermass for parsing
	 */
	private void finishTree() {
		for (Token t : queue) {
			stack.add(tree.addTerminal(t).id);
		}
		if (stack.size() == 0) {
			System.err.println("Parse Tree is NULL");
			return;// null;
		}
		if (stack.size() > 1) {
			Node n = tree.addNonTerminal(stack, "IP");//or "S" for english
		}
		tree.root = tree.vNode.size()-1;
	}

	
	/** use psuedo parse to test classifier
	 * when testing, no feature should be filtered based on freq
	 * */
	public void testClassifier() {
		RandomAccessDataset ds=	 getDataM3rd(super.p.testFile,0);
		BufferedWriter bw = FFile.newWriter(	
			code+"/"+super.p.testFile + ".testC");
		
		VectorS vSys=new VectorS();
		VectorS vGold=new VectorS();
		vSys.ensureCapacity(ds.size());		
		vGold.ensureCapacity(ds.size());	
		//vEva.ensureCapacity(ds.size());	
		
		System.out.println("testing ...");
		for (int i=0; i<ds.size(); ++i){
			if (i % 1000==0)		System.out.print("k");

			Example e = ds.getExample(i);
			String gold = e.getLabel().bestClassName();
			vGold.add(gold);
			
			String sys = decideAction( SetS.fromM3dExample(e)).get(0);
			//String sys = classifier.classification(e).bestClassName();
			vSys.add(sys);
			
			FFile.write(bw,		gold+"-->"+sys+"\t"+e.toString()+"\n");
		}
		System.out.println("\ndone");
		
		ClassificationEval eva = ClassificationEval.evaluate(vSys, vGold);
		MapSMapSD mm = ClassificationEval.confusionMatrix(vSys, vGold);
		System.out.println(eva);

		HtmlPage th = new HtmlPage("test."+p.learner.name(),code);
		th.addPre(eva.toString());
		th.newTable("confusion matrix", mm);
		//th.newTableFixCol("model", LearnerFactory.);
		th.close();

		summary += FHtml.addHref(
					"testClassifier="+eva,code+"/testClassifier.html") +"\n";
		return;
	}	

	protected DataSetBinaryI getData(String treeFile, int freq) {
		String fn= treeFile +"."+freq;
		Cacher c = new Cacher(".");
		DataSetBinaryI ds =	(DataSetBinaryI)c.loadObj(fn);
		if (ds==null) {
			ds=createData(treeFile);
			System.out.println(ds.info());					
			ds.shrinkInfrequentFeature(freq);
			ds.save(fn+".txt");				
			c.saveObj(ds, fn);
		}
		System.out.println(ds.info());
		return ds;
	}
	protected RandomAccessDataset getDataM3rd(String treeFile, int freq) {
		String fn= treeFile +"."+freq+".M3rd";
		Cacher c = new Cacher(".");
		RandomAccessDataset ds =	(RandomAccessDataset)c.loadObj(fn);
		if (ds==null) {
			DataSetBinaryI ds1=getData(treeFile, freq);
	
			ds =ds1.toM3rdDataSet(true);
			c.saveObj(ds, fn);
		}
		return ds;
	}

	/**should use real parse instead of psuedo parse*/
//		codeData= super.p.trainFile +".FF"+p.minFeatureFreq;//+".data");
//	localModel=code+"/model";

	public void trainRaw() {		
		switch(p.learner){
		case MaxEnt:
			if (!FFile.exist(codeData+".txt"))
				getData(super.p.trainFile,p.minFeatureFreq);
			//DataSetBinaryI ds=getData(p.trainFile,p.minFeatureFreq);
			//FFile.saveString(ds, filePath);
			//"/all.trainingForZhangMaxentReducedBranching"

			LeMaxEnt.train( codeData+".txt", code+"/model");
			break;
		default://M3rd learners
	/*		Dataset ds=getDataM3rd(super.p.trainFile,p.minFeatureFreq);
			System.out.println("Start training ..");
			ClassifierLearner learner=null;
			switch(p.learner){
			case MarginPerceptronX:
			case VotedPerceptronX:
				learner= LearnerFactory.getMCLearner(
						MCLearner.valueOf(FString.trimRight(p.learner.name(), 1)));
				classifier =LearnerFactory.train(learner, ds, 10);
				break;
			default:
				learner= LearnerFactory.getMCLearner(
						MCLearner.valueOf(p.learner.name()));
				classifier= new DatasetClassifierTeacher( ds ).train( learner );
				break;
			}*/
			FFile.saveObject(classifier,	code+"/model");
			break;
		}
		return;
	}


	

	/**
	 * the tree is already there
	 * just pretending to be operating on the stack and queue
	 */
	protected DataSetBinaryS pseudoParse(TreeSyntax tree) {
		this.tree = tree;
		//fh.findHead(tree);
		DataSetBinaryS ds = new DataSetBinaryS();
		queue = tree.getTerminals();
		stack = new VectorI();
		fex.reset();
		for (int i = 0; i < queue.size() || stack.size() > 1;) {
			String action = decideActionPseudo();
			if (doAction(action, true)==null)			break;
			if (f!=null){//feature is used for decision
				InstanceBinaryS ins =new InstanceBinaryS(action, f);
				//System.out.println(ins.toString());
				ds.add(ins);
			}
		}
		return ds;
	}
	
	
	protected DataSetBinaryI createData(String treeFile) {
		DataSetBinaryI ds = new DataSetBinaryI();
		BufferedReader br = FFile.newReader(treeFile);
		//BufferedWriter bw = FFile.bufferedWriter(treeFile + ".data");
		String line = null;int nSent=0;
		while ((line = FFile.readLine(br)) != null) {
			TreeSyntax tree0=TreeSyntax.parseNew(line);
			if (tree0==null) continue;
			//if (p.dbg>0)		System.out.println(tree0);			
			
			tree = tb.binarizeOn(tree0);
			//if (p.dbg>0)		System.out.println(tree);

			
			DataSetBinaryS ds1 = pseudoParse(tree);
			//if (p.dbg>0)		System.out.println(ds1);			
			
			ds.addAll(ds1);
			++nSent;	if (nSent % 10==0)		System.out.print("t");
			//FFile.write(bw,  ds1.toString());
		}
		//need to index features
		//FFile.flush(bw);
		return ds;
	}

	/*
	 (VP (VV 有待)
	 (NP-OBJ (-NONE- *pro*))
	 (IP-OBJ 
	 (NP-SBJ (-NONE- *PRO*)) 
	 (VP (PP-LOC (P 在) (LCP (NP (NN 实践))(LC 中))) 
	 (ADVP (AD 逐步)) 
	 (VP (VV 完善))
	 )
	 )
	 )))))) 

	 */

	private String decideActionPseudo() {
		f=null;
		if (stack.isEmpty()) return "shift";
		f = fex.extractFeature(queue, stack, tree);
		
		int i0 = stack.lastElement();
		Node n0 = tree.getNode(i0);
		Node np = tree.getNode(n0.iparent);
		if (np.vc.size() == 1) //Unary reduction
			return "U:" + np.t.getPOS();		
		else{//Binary reduction
			if (np.vc.lastElement()==i0)
				return "B:" + np.t.getPOS();
			else
				return "shift";
		}
	}
	private VectorS decideAction(SetS f){
		VectorS vAction = new VectorS();
		
		switch(p.learner){
		case MaxEnt:
			String line = proc.pushPop("? " + f.join(" ") + "\n");
			String vs[] = line.split("[ \t\n\r\f]");
			vAction.addAll(vs);
			break;
		default:
			Example e =null;//new Example( f.toM3rdInstance(), new ClassLabel());
			ClassLabel label = classifier.classification(e);
			MapSD m =MapSD.fromM3rdLabel(label) ;
			vAction.addAll(m.KeyToVecSortByValue(true) );
			break;
		}
		if (super.p.dbg>0)
			System.out.println(vAction +" "+f);
		return vAction;
	}
	SetS f=null;
	/**
	 * action = 
	 * 	shift
	 *	,U(niary reduce)-X-dir
	 *  ,B(inary reduce)-X-dir
	 */
	private String decideAction(){//boolean pseudo) {
		f=null;
		if (stack.isEmpty()) return "shift";
		f = fex.extractFeature(queue, stack, tree);
		//String action=null;
		VectorS vAction =  decideAction(f);
		for (String action:vAction){
			if (!action.startsWith("U:")) {
				unaryCount = 0;
				return action;
			}
			Node s0=tree.getNode(stack.lastElement());
			if (action.endsWith(s0.t.getPOS()))
				continue;
			if (unaryCount >= p.UNARY_LIMIT) 
				continue;
			++unaryCount;
				return action;
		}
		return null;		
	}


	public static void main(String[] args) {
		try {
			Param.overwrite(args);
			SRParser.getInstance().run();
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
	public TreeSyntax synxParseSent(VectorS vWord) {
		return null;
	}	
}
