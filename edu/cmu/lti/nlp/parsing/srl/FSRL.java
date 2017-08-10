/**
 * 
 */
package edu.cmu.lti.nlp.parsing.srl;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.parsing.FTree.FindHead;
import edu.cmu.lti.nlp.parsing.FTree.Trim;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.FeaturedGraph;
import edu.cmu.lti.nlp.parsing.tree.TreeDep;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;
import edu.cmu.lti.util.file.FFile;

/**
 * a frame net tree
 * @author nlao
 *
 */
public class FSRL {
	public static  class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public int dbg = 0;
		public boolean bHeadFeatures = true;
		public boolean bDepTree = false;//1;
		public boolean bTrim2Phrase = true;
		public boolean bTrim2PredStack = true;
		public Param(){//Class c) {
			super(FSRL.class);
			parse();
		}
		public String code;
		public void parse() {
//			lang = getString("lang","zh_CN");
			//ctbTreeFile = getString("ctbTreeFile");
			bDepTree = getBoolean("bDepTree",false);
			bHeadFeatures = getBoolean("headFeatures",true);
			bTrim2Phrase = getBoolean("bTrim2Phrase",true);
			bTrim2PredStack = getBoolean("bTrim2PredStack",true);
			
			code = "";
			if (bDepTree)code+= ".Dep";
			else code+=".Synt";
			
			if (bHeadFeatures)code+= ".hf";
			if (bTrim2Phrase)code+= ".TrP";
			if (bTrim2PredStack)code+= ".TrS";				
				
			code +=FSRLFeatures.p.code;
		}
		/*		
		public String lang;
		public void parse(){
			lang = System.getProperty("lang");
			dbg = Integer.parseInt(System.getProperty("dbg"));	
		}
		public Param(){
			parse();
		}*/
	}
	//FrameNet parser = new FrameNet();
	public PropBank bank = null;//new PropBank();
	public FSRLFeatures srlf =null;
	public static Param p =null;// new Param();
	public FSRL(){
		srlf= new FSRLFeatures(FSRL.class);
		bank= new PropBank(FSRL.class);
		p =new Param();
	}
	//assume TreeSyntax 
	protected TreeParse transformTree(TreeSyntax tree){
		//SRLParser parser){	//TreeSRL tree){
	
		TreeParse t =  tree;
		
		if (p.dbg>0)System.out.print(t.toString());
		if (p.bHeadFeatures){					
			FindHead.getInstance().findHead(t);
			tree.addHeadFeatures();
		}

		if (p.bDepTree){			
			t= tree.toDepTree();
			//if (p.dbg>0)System.out.print(t.toString(1));			
			((TreeDep)t).reorder();
			if (p.dbg>0)System.out.print(t.toString());
		}
		else
			tree.assembleText();
		
		if (p.bTrim2Phrase){
			if (Trim.getInstance().trim2Phrase(t, false)==null)
				return null;
			if (p.dbg>0)System.out.print(t.toString());
		}
		
		if (p.bTrim2PredStack){
			if (Trim.getInstance().trim2PredcateStack(t)==null)
				return null;
			if (p.dbg>0) System.out.print(t.toString());
		}		
		return t;
	}
	
	static TreeParse tree;
	public FeaturedGraph parseTree2SrlFG(SRLParser parser) {
			//TreeSRL tree) {
		tree = transformTree((TreeSyntax) parser.tree);
		if (tree==null){
			System.out.println("addSRLFeature failed");
			return null;
		}
		srlf.addSrlFeatures(tree, parser.pbMeta);		
		/*String[] vs = {
				"atPred","level","nvi"
				//, "fs"
				,"pos","pos+hw","posx","text","verb"
				,"h.pos","h.posx","hw"
				,"p.nvi","p.nvi","p.nvi.s","p.pos"
				,"p.pos+hw","p.pos+hw.f","p.pos+hw.s","p.pos.f"
				,"p.pos.s","p.posx","p.posx.f","p.posx.s"
				//,"r.pos","r.hw"
				//,"l.pos","l.hw"
				//,"m.pos","m.hw"			
				,"target", "srl"
			};
		VectorS filter= new VectorS(vs);*/
	
		tree.findChildBound();
		//in_target =  t.v_inode.get(pbMeta.i_pred);
		//is inode still use able here?
		
		FeaturedGraph fg = tree.toFeaturedGraph(null);//filter);
		PBMeta meta= parser.pbMeta;
		int in_pred=tree.viNode.get(meta.i_pred);
		for (int i = 0; i < tree.vNode.size(); ++i) {
			Node n = tree.getNode(i);
			FeaturedGraph.Node gn = fg.v_node.get(i);
			gn.prefix= String.format("%s%d\t%d\t%d\t%d\t"
				, meta.file_name, meta.i_sent	, in_pred
				, n.icl, n.icr
				);
			//n.put(CTag.fs, String.format("%s.%d",meta.verb,meta.i_subcat));

		}
		FeaturedGraph.Node n= fg.v_node.get(in_pred);
		n.ms.remove(CTag.srl);
		//n.ms.put("pred", "true");
		//FeaturedGraph fg = toFeaturedGraph(t, parser.pbMeta, filter);
		fg.detachTag(CTag.srl);
		fg.normalizeText(CTag.text, 6);		
	
		return fg;
	}
//	public static FeaturedGraph toFeaturedGraph(
//			TreeParse t, PBMeta pbMeta,  VectorS filter) {return fg;}
	
	public void run_fn2graph(String file){// throws IOException ,Exception{
		BufferedReader reader = FFile.newReader(file);
		String folder = file +p.code+"/";//"."+
		FFile.mkdirs(folder);
		BufferedWriter bwAst = FFile.newWriter(folder + "u.ast");
		BufferedWriter bwNd = FFile.newWriter(folder + "u.nd");
		BufferedWriter bwLnk = FFile.newWriter(folder + "u.lk");
		BufferedWriter bwTree = FFile.newWriter(folder + "u.tree");


		int n=0;
		int nvalid=0;
		int ntot=0;
		int nnodes=0;
		for (; bank.readPBLine(reader); ++ntot, ++n) {
			//TreeParse tree = bank.tree;
			if (bank.parseErr>0){
				//System.out.println("parse PropBank failed");
				//parser.printErr();
				continue;
			}

			String pos=bank.tree.getNode(0).getString(CTag.pos);
			if (!pos.equals(CTag.IP)) {//TODO generalize this hack
				continue;
			}
			
			FeaturedGraph fg=null;// =  ParseTree2SrlFG.convert(tree);
			try{
				fg =  parseTree2SrlFG(bank);
			}
			catch(Exception e){
				bank.parseErr=2;
				bank.printErr();
				e.printStackTrace();
				continue;
			}
			
			
			if (fg ==null)
				continue;

			FFile.write(bwTree,tree.toString()); 			
			fg.writeGRMM(bwNd,bwLnk);
			fg.writeAssert(bwAst);			
			
			if (p.dbg > 0) {
				FFile.flush(bwLnk);	
				FFile.flush(bwLnk);
				FFile.flush(bwTree);
			}
			//if (n==12) break;
			++nvalid;
			nnodes+= fg.v_node.size();
			if (n==100){
				n=0; 
				System.out.print(".");
			}
			continue;
		}
		System.out.println("#total sent" + ntot);
		System.out.println("#valid sent" + nvalid);		
		System.out.println("#nodes " + nnodes);		
		FFile.close(reader);
		FFile.close(bwAst);
		FFile.close(bwNd);
		FFile.close(bwLnk);
		FFile.close(bwTree);
	}
	public void run(){
		run_fn2graph(bank.p.PBFile);//args[0]);
	}
	public static void main(String[] args) throws IOException,Exception {
		/*if (args.length < 1) {
			System.out.println("Usage: java FSRL file_in");
			System.exit(-1);
		}*/
		
		try{
			(new FSRL()).run();
		}
		catch (Exception e) {
			// logger.warn("Problem in execution", e);
			System.out.println(e.getClass().getName());
			// e.getCause();
			e.printStackTrace();
			return;
		}
	}
}
