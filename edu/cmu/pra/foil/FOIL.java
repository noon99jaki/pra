package edu.cmu.pra.foil;


import java.io.BufferedWriter;
import java.util.ArrayList;

import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.MapSMapSD;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.math.rand.FRand;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.data.PRA;
import edu.cmu.pra.data.WKnowledge;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.graph.IGraph;
import edu.cmu.pra.model.Query;

public class FOIL {
	public static abstract class RuleLine implements IParseLine {
		public String relation;
		//public String rule;
		public int iter;
		public String stats;
		public String txt;
		public int proved;

		//public String vsClause[];
		//public VectorVectorS vvsClause=new VectorVectorS();
		public ArrayList<String[]> vvsClause = new ArrayList<String[]>();

		public String toString() {
			return txt;
		}
	}

	public static class RuleLine1 extends RuleLine {
		public String comment;

		public RuleLine1() {}

		public RuleLine1(String line) {
			parseLine(line);
		}

		public boolean parseLine(String line) {
			VectorS vs = FString.splitVS(line, "\t");
			if (vs.size() < 4) return false;
			int i = 0;
			relation = vs.get(i);
			++i;
			comment = vs.get(i);
			++i;
			String rule = vs.get(i);
			++i;
			iter = Integer.parseInt(vs.get(i));
			++i;
			proved = Integer.parseInt(vs.get(i));
			++i;

			relation = WKnowledge.removePrefix(relation);

			if (!rule.startsWith("{") || !rule.endsWith("}")) FSystem.die("mal-formated rule=" + rule);

			int p = rule.lastIndexOf('}', rule.length() - 2);

			stats = rule.substring(p + 2, rule.length() - 1);
			txt = rule.substring(3, p - 1);

			this.vvsClause.clear();
			for (String clause : txt.split("\\} \\{"))
				vvsClause.add(clause.substring(1, clause.length() - 1).split("\" \""));
			return true;
		}

		public static Seq<RuleLine1> reader(String fn) {
			return reader(fn, false);
		}

		public static Seq<RuleLine1> reader(String fn, boolean bSkipTitle) {
			return new SeqTransform<RuleLine1>(RuleLine1.class, fn, bSkipTitle);
		}

	}

	public static class RuleLine2 extends RuleLine {
		//public String 	Judgement	;
		public int Candidates;
		public int NovelCandidates;
		public String ApprovalStats; //Stats at time of approval

		public RuleLine2() {}

		public RuleLine2(String line) {
			parseLine(line);
		}

		public boolean parseLine(String line) {
			VectorS vs = FString.splitVS(line, "\t");
			if (vs.size() < 4) return false;
			int i = 0;
			iter = Integer.parseInt(vs.get(i));
			++i;
			txt = vs.get(i);
			++i;
			String Judgement = vs.get(i);
			++i;
			Candidates = Integer.parseInt(vs.get(i));
			++i;
			NovelCandidates = Integer.parseInt(vs.get(i));
			++i;
			stats = vs.get(i);
			++i;
			stats = stats.substring(1, stats.length() - 1);
			ApprovalStats = vs.get(i);
			++i;

			if (Judgement.equals("+")) proved = 1;
			else if (Judgement.equals("-")) proved = 0;
			else System.err.println("unknown judgement=" + proved);

			this.vvsClause.clear();

			this.vvsClause.clear();
			for (String clause : txt.substring(2, txt.length() - 4).split("\\} , \\{"))
				vvsClause.add(clause.split(", "));
			relation = vvsClause.get(0)[0];
			return true;
		}

		public static Seq<RuleLine2> reader(String fn) {
			return reader(fn, false);
		}

		public static Seq<RuleLine2> reader(String fn, boolean bSkipTitle) {
			return new SeqTransform<RuleLine2>(RuleLine2.class, fn, bSkipTitle);
		}

	}

	IGraph g;

	public FOIL(IGraph g, ERankMod rankMod) {
		this.g = g;
	}

	//TMapVectorV
	VectorX<Rule> vRule = new VectorX<Rule>(Rule.class);

	public boolean loadModel(String fn, String rel) {

		for (RuleLine2 l : RuleLine2.reader(fn)) {
			if (!l.relation.equals(rel)) continue;
			//if (l.rule.indexOf("citycapitalofstate")>=0)continue;
			//if (l.rule.indexOf("stadiumhometeam")>=0)continue;

			Rule r = new Rule(l, g);
			vRule.add(r);
		}
		return true;
	}

	//public VectorI getFeatures(Query)

	public static enum ERankMod {
		MAX//
		, SUM
		//
	}

	public ERankMod rankMod = ERankMod.MAX;

	public MapID predict(Query q) {//, ERankMod mod){
		q.features_ = new VecMapID();
		for (Rule r : vRule) {
			int seed = q.seeds_.firstElement().first();
			q.features_.add(r.match(seed));
		}

		switch (rankMod) {//.weightedSum(vwFeature);
		case SUM:
			return q.features_.sum();
		case MAX:
			return q.features_.maxSum();
		default:
			FSystem.die("not implemented");
		}
		return null;
	}

	//TODO: merge this with the one in LearnerRank?
	public void predict(VectorX<Query> vQ, String fnOut) {
//
//		FFile.mkdirs("result/data");
//
//		BufferedWriter bw = FFile.newWriter(fnOut);
//		BufferedWriter bwR = FFile.newWriter(fnOut + ".details");//
//
//		int maxResult = 30;
//		//	Counter.c50.clear();
//		int nQ = 0;
//
//		for (int j = 0; j < vQ.size(); ++j) {//	for (Query q: vQ){
//			Query q = vQ.get(j);
//			MapID mResult = predict(q); //Counter.c50.stepDot();
//
//			if (q.features_.getVI(CTag.size).sum() == 0) continue;
//
//			++nQ;
//			//System.out.println("q="+q.name);
//
//			FFile.write(bw, "%s\t%d/%d", null, mResult.size(), q.good_.size());
//
//			if (bwR != null) {
//				FFile.write(bwR, "\n[" + q.name_ + "]\n");
//				for (int i = 0; i < q.features_.size(); ++i) {
//					if (q.features_.get(i).size() == 0) continue;
//					FFile.writeln(bwR, vRule.get(i).txt);
//					FFile.writeln(bwR, FString.join(g.getNodeNames(q.features_.get(i).keySet()), ", "));
//				}
//			}
//
//			int i = 0;
//			for (Integer id : mResult.KeyToVecSortByValue(true)) {
//				double score = mResult.get(id);
//				//if (score<=0) break;
//				if (i >= maxResult) break;
//				++i;
//
//				String name = g.getNodeName(id);
//				if (q.good_.contains(id)) name = name + "*";
//
//				FFile.write(bw, String.format("\t%.3e,%s", score, name));
//
//			}
//			FFile.write(bw, "\n");
//
//			if (bClearQ) {
//				q.features_.clear();
//				q.features_ = null;
//				vQ.set(j, null);
//			}
//		}
//		FFile.close(bw);
//		if (bwR != null) FFile.close(bwR);
//
//		System.out.println("\t" + nQ + "\t" + vQ.size());

	}

	public static void countRules() {

		int n = 0;
		int nGrounded = 0;
		VectorS vsRules = new VectorS();
		MapSI mRelCount = new MapSI();
		MapSI mRelGCount = new MapSI();
		MapII mLengthCount = new MapII();

		MapII mProveCount = new MapII();

		for (RuleLine2 l : RuleLine2.reader(fnRules)) {
			mRelCount.plusOn(l.relation);
			Rule r = new Rule(l);
			mLengthCount.plusOn(r.vNode.size() - 1);
			if (r.bGrounded) ++nGrounded;
			else mRelGCount.plusOn(l.relation);

			mProveCount.plusOn(l.proved);
			vsRules.add(r.txt);
			++n;
		}
		vsRules.save(fnRules + ".meat");

		System.out.println("nGrounded=" + nGrounded + "/" + n);
		System.out.println("mLengthCount=\n" + mLengthCount.join("\t", "\n"));
		System.out.println("#Proved=\n" + mProveCount.join("\t", "\n"));
		//System.out.println("mRelGCount=\n"+mRelGCount.join("\t","\n"));
		//System.out.println("mRelCount=\n"+mRelCount.join("\t","\n"));

		System.out.println("mRelCount=");
		for (String rel : mRelCount.keySet()) {
			int nNG = mRelGCount.getD(rel);
			int nAll = mRelCount.getD(rel);
			System.out.println(rel + "\t" + nNG + "(+" + (nAll - nNG) + ")");
		}

	}

	public static void cleanUpRules() {

		MapSI mMaxNP = new MapSI();
		MapSS mMaxLine = new MapSS();
		//TMapSX<Rule> mUniqRules= new TMapSX(Rule.class);
		for (String line : FFile.enuLines(fnRules)) {
			Rule r = new Rule(new RuleLine2(line));
			if (mMaxNP.containsKey(r.txt)) {
				System.out.println("repeated rule=" + mMaxNP.get(r.txt) + " " + mMaxLine.get(r.txt));
				if (mMaxNP.get(r.txt) > r.nP) continue;
				System.out.println("new nP=" + r.nP);

			}
			mMaxNP.put(r.txt, r.nP);
			mMaxLine.put(r.txt, line);
		}

		mMaxLine.ValuesToVector().save(fnRules + ".uniq");

	}

	public static void inspectRules() {

		int n = 0;
		int nGrounded = 0;
		VectorS vsRules = new VectorS();
		MapSMapSD mmRelModel = new MapSMapSD();
		MapSMapSD mmRelModel0 = new MapSMapSD();

		for (RuleLine2 l : RuleLine2.reader(fnRules)) {
			Rule r = new Rule(l);
			if (r.bGrounded) continue;
			vsRules.add(r.txt);

			String relation = r.relation;
			String path = r.getPath();

			MapSD mModel = mmRelModel.get(relation);
			MapSD mModel0 = mmRelModel0.get(relation);
			if (mModel == null) {
				mModel0 = MapSD.fromFile(EvaluatorPRA.fdModel + relation, 1, 0, true);
				mmRelModel0.put(relation, mModel0);

				mModel = mModel0.removeZeros();
				mmRelModel.put(relation, mModel);
			}
			String size = mModel.size() + "|" + mModel0.size();

			Double d = mModel.get(path);
			String rank = "none";
			if (d != null) {
				int iR = mModel.KeyToVecSortByValue(true).indexOf(path) + 1;
				rank = iR + "";
			}
			Double d0 = mModel.get(path);
			if (d0 != null) {
				int iR0 = mModel0.KeyToVecSortByValue(true).indexOf(path) + 1;
				//rank+= "|"+iR0;
			}
			System.out.println(RelationLine.getName(relation) + "\t" + size + "\t" + rank + "\t"
					+ l.stats.replaceAll(" ", "\t") + "\t" + path);
			//+"\t"+d
		}
		//		System.out.println("\n\n"+vsRules.join("\n"));
	}

	public static boolean bClearQ = true;
	public static String fnRules = "rules.all.150";

	//"rules.150.uniq";// .214
	public static void splitTrainTest(String predicate,  
			String pair_file,	String train_rate_str) {
		double train_rate = Double.parseDouble(train_rate_str);
		
		int a=predicate.indexOf("(");
		FSystem.checkTrue(a>0);
		String rel = predicate.substring(0,a);
		
		BufferedWriter writer = FFile.newWriter(pair_file+".tt"+train_rate_str);
		FFile.writeln(writer, predicate);
		
		
		VectorS good = new VectorS();
		VectorS bad = new VectorS(); 
		VectorS test = new VectorS();
		for (String line: FFile.enuLines(pair_file)) {
			if (FRand.drawBoolean(train_rate)) {
				int p=line.lastIndexOf(':');
				FSystem.checkTrue(p>0);
				String node = line.substring(0,p);
				
				if (line.endsWith("-")) bad.add(node);
				else if (line.endsWith("+")) good.add(node);
				else FSystem.dieShouldNotHappen();
			}
			else  {
				test.add(line);
			}
		}
		good.save(writer);
		FFile.writeln(writer, ";"	);
		bad.save(writer);
		FFile.writeln(writer, ".\n"	);
		
		FFile.writeln(writer, rel	);
		test.save(writer);
		FFile.writeln(writer, ".\n"	);
		FFile.close(writer);
	}
	
	public static void createTestData(String predicate, String positive, 
			String candidates, String output) {
		SetS good = SetS.fromFile(positive);
		
		FSystem.checkTrue(good.size()>0);
		
		BufferedWriter writer = FFile.newWriter(output);
		FFile.writeln(writer, predicate);
		
		
		for (String name: good)
			FFile.writeln(writer, name + ": +");
		
		for (String node: FFile.enuLines(candidates)){
			String name = PRA.normalizeName(node);
			if (name.length()<3) continue;			
			if (good.contains(name)) continue;
			FFile.writeln(writer, name + ": -");
		}
		
		FFile.writeln(writer, ".");
		FFile.close(writer);
	}
	
	
	public static void createTrainTestData(String str_train_rate) {
		double train_rate = Double.parseDouble(str_train_rate);
		String folder = "q" + str_train_rate + "/";//ueries-tr
		FFile.mkdirs(folder);
		
		SetS predicates =new SetS(Param.ms.get("reporter_keys").split(","));
		
		String train_samples =Param.ms.get("train_samples");
		
		String graph_folder =Param.ms.get("graph_folder");
		
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		GraphWalker walker = new GraphWalker(graph);
		
		for (String predicate: predicates) {
			System.out.println("\npredicate="+ predicate);
			SetS skip= new SetS(predicate);
			skip.add("_"+predicate);
			
			graph.save2FOILFormat(folder + predicate, skip);

			BufferedWriter train_file = FFile.newWriter(folder + predicate + ".train");
			BufferedWriter test_file = FFile.newWriter(folder + predicate + ".test");
			BufferedWriter bg_file = FFile.newWriter(folder + predicate + ".background");

			FFile.writeln(train_file, predicate + "(c,c)");
			FFile.writeln(test_file, predicate);
			
			SetS range = null;

			String query_file = train_samples.replaceFirst("<target_relation>", predicate);
			//System.out.println("query_file="+ query_file);
			
			for (VectorS row: FFile.enuRows(query_file)) {
				if (range==null) {
					range = new SetS();
					SetI set = walker.repeatedExpand(row.get(1), "_generalizations");
					for (int id: set)
						range.add( walker.getNodeNameN(id));					
				}
				
				String seed =PRA.normalizeName(row.get(0));
				
				SetS good = new SetS ();
				for (String str: row.get(2).split(" "))
					good.add(PRA.normalizeName(str));
				
				if (FRand.drawBoolean(train_rate)){ 
					for (String tgt: good)
						FFile.writeln(train_file,  seed + "," + tgt);
				}
				else {
					for (String tgt: good)
						FFile.writeln(test_file,  seed + "," + tgt + ": +");
					
					for (String name: range){
						if (!good.contains(name)) 
						FFile.writeln(bg_file,  seed + "," + name + ": -");
					}
				}
			}	
			FFile.writeln(train_file, ".\n");
			FFile.writeln(bg_file, ".\n");

			FFile.close(train_file);
			FFile.close(test_file);
			FFile.close(bg_file);
			
			FFile.enuLines(		folder + predicate + ".nodes"
					+ "|" + folder + predicate + ".edges"
					+ "|" + folder + predicate + ".train"
					+ "|" + folder + predicate + ".test"
					+ "|" + folder + predicate + ".background"
					).save(folder + predicate);
		}
	}
	
	public static void createTrainTestDataA(String str_train_rate) {
		double train_rate = Double.parseDouble(str_train_rate);
		String folder = "q" + str_train_rate + "/";//ueries-tr
		FFile.mkdirs(folder);
		
		SetS predicates =new SetS(Param.ms.get("reporter_keys").split(","));
		
		FSystem.silent_cmd = false;
		
		for (String predicate: predicates) {
			System.out.println("\npredicate="+ predicate);
/*			FSystem.cmd("cat " + folder + predicate + ".nodes"
					+ " " + folder + predicate + ".edges"
					+ " " + folder + predicate + ".train"
					+ " " + folder + predicate + ".test"
					+ " " + folder + predicate + ".background"
					+ " > " + folder + predicate);
					*/
			FFile.enuLines(		folder + predicate + ".nodes"
					+ "|" + folder + predicate + ".edges"
					+ "|" + folder + predicate + ".train"
					+ "|" + folder + predicate + ".test"
					+ "|" + folder + predicate + ".background"
					).save(folder + predicate);
		}
	}
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		String task = args[0];
		if (task.equals("inspectRules")) inspectRules();
		else if (task.equals("splitTrainTest")) 
			splitTrainTest(args[1],args[2],args[3]);
		
		else if (task.equals("createTestData")) 
			createTestData(args[1],args[2],args[3],args[4]);
		
		else if (task.equals("createTrainTestData")) createTrainTestData(args[1]);
		else if (task.equals("createTrainTestDataA")) createTrainTestDataA(args[1]);


		else FSystem.die("unknown task=" + task);

		//cleanUpRules();
		//countRules();

	}

}
