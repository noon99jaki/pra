package edu.cmu.pra.foil;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapSMapSI;
import edu.cmu.lti.algorithm.container.MapSSetS;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.structure.KeepTopK;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.HtmlPage;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.LearnerPRA;
import edu.cmu.pra.SmallJobs;
import edu.cmu.pra.postprocess.ReporterPRA;

public abstract class Evaluator {
	public Evaluator(String name) {
		this.name = name;
	}
	public String name;

	public static void _createQueries() {
//		LearnerPRA learner = SmallJobs.tryloadTask();
//		Graph graph = learner.graph_;
//
//		FFile.mkdirs("queries");
//
//		for (String rel : Param.ms.get("col1Values").split(",")) {
//			System.out.println("Exporting queries for relation=" + rel);
//			int r = graph.getEdgeType(rel);
//			if (r == -1) {
//				System.out.println("unknown relation=" + rel);
//				break;
//			}
//
//			BufferedWriter bw = FFile.newWriter("queries/" + rel);
//			for (TypedNode e : graph.nodes_) {
//				VectorI vi = e.relation_entities_.get(r);
//				if (vi == null) continue;
//				if (vi.size() == 0) continue;
//				FFile.writeln(bw, graph.getNodeName(e.id_) 
//						+ "," + graph.node_index_.list_.sub(vi).join(" "));
//			}
//			FFile.close(bw);
//		}
//
//		return;
	}


	//query-->answer-->label
	public MapSMapSI mmLabel = new MapSMapSI();

	public void loadLabels(String rel) {
		mmLabel.clear();

		// load Turker-generated labels
		for (String fn : fnsAMTLabel.split(" "))
			//		if (FFile.exist(fn))
			for (VectorS vs : FFile.enuRows(fn)) {
				if (vs.size() != 4) {
					System.err.println("expect 4 columns=" + vs.join(" "));//FFile.line_);
					continue;
				}
				if (!vs.get(0).equals(rel)) continue;
				mmLabel.getC(vs.get(1))
					.put(vs.get(2), Integer.parseInt(vs.get(3)));
			}

		// load PhD-generated labels
		if (!FFile.exist(fdLabel + rel)) {
			FFile.appendToFile("", fdLabel + rel);
			return;
		}
		for (VectorS vs : FFile.enuRows(fdLabel + rel)) {
			if (vs.size() != 3) {
				System.err.println("expect 3 columns=" + vs.join(" "));//FFile.line_);
				continue;
			}
			mmLabel.getC(vs.get(0)).put(vs.get(1), Integer.parseInt(vs.get(2)));
		}

	}

	public VectorS vsQ = new VectorS();
	public VectorS vsR = new VectorS();

	public void loadResults(String rel) {
		vsQ.clear();
		vsR.clear();
		for (String line : FFile.enuLines(predicate_folder_ + "/" + rel + code)) {
			String row[] = line.split("\t");
			//if (row.length<3) continue;
			String item1[] = row[2].split(",");
			String score = item1[0];
			vsQ.add(row[0]);
			vsR.add(item1[1]);
		}
	}

	//public static int nUnknown=0;
	public MapSSetS mUnknown = new MapSSetS();
	public MapSX<MapSSetS> mmUnknown = new MapSX<MapSSetS>(MapSSetS.class);

	protected double evaluate(int nTop, int nSamples) {
		//FFile.mkdirs(fdOut);
		int step = nTop / nSamples;
		double nCorrect = 0;
		for (int i = 0; i < nTop && i < vsQ.size(); i += step) {
			String Q = vsQ.get(i);
			String R = vsR.get(i);
			Integer j = mmLabel.get(Q, R);

			if (j != null) {
				if (j == 1) ++nCorrect;
				continue;
			}

			if (mUnknown.getC(Q).contains(R)) continue;

			mUnknown.getC(Q).add(R);
			//System.out.print(Q+"\t"+R+"\t\n");
			//++nUnknown;
		}

		if (step > vsQ.size()) {
			nCorrect *= vsQ.size();
			nCorrect /= step;

		}
		return nCorrect / nSamples;
	}

	public int sortForRelation(String rel) {
		//	System.out.println("\n sortPred()");
		KeepTopK keep = new KeepTopK(nTop);
		int n = 0;
		for (String line : FFile.enuLines(predicate_folder_ + "/" + rel)) {
			String row[] = line.split("\t");
			if (row.length < 3) continue;
			++n;
			String score = row[2].split(",")[0];
			keep.addAbsValue(Double.parseDouble(score), line);
		}

		keep.objects_.save(predicate_folder_ + "/" + rel + code);
		return n;
	}

	public void sortPredReasons(String rel) {
		String fn = predicate_folder_ + "/" + rel + ".reasons";
		SmallJobs.sortReasons(nTop, fn, fn + code);
	}

	public void sortAndPublish() {
		System.out.println("\npublish()");

		VectorS counts = new VectorS();
		Param.overwriteFrom("conf");
		for (String relation : Param.ms.get("col1Values").split(",")) {
			System.out.println(relation + "-->");

			int num_results = sortForRelation(relation);
			counts.add(relation + "\t" + num_results);
			sortPredReasons(relation);
			HtmlPage th = new HtmlPage("predict.PRA", fdAFS + fdRun + relation);//AMTurk.fdHit
			ReporterPRA.printPrediction(predicate_folder_ + "/" + relation + code, th);
			th.close();
		}

		System.out.println(counts.join("\n"));

	}

	public void removeLabeledResults(String rel) {
		//	System.out.println("\nremoveLabeledResults()");
		BufferedWriter bw = FFile.newWriter(predicate_folder_ + ".RL/" + rel);

		for (String line : FFile.enuLines(predicate_folder_ + "/" + rel)) {
			String row[] = line.split("\t");
			VectorS vs = new VectorS();
			for (String s : row)
				if (s.indexOf('*') < 0) vs.add(s);
			if (row.length < 3) continue;
			FFile.writeln(bw, vs.join("\t"));
		}
		FFile.close(bw);

	}

	public void removeLabeledResults() {
		System.out.println("\ngenerateAMT()");

		Param.overwriteFrom("conf");
		FFile.mkdirs(predicate_folder_ + ".RL");
		for (String rel : Param.ms.get("col1Values").split(",")) {
			System.out.println(rel + "-->");
			removeLabeledResults(rel);
		}
	}

	public void generateMissing() {
		System.out.println("generateMissing()");
		BufferedWriter bw = FFile.newWriter(predicate_folder_ + "/.missing");

		for (String rel : mmUnknown.keySet()) {
			mUnknown = mmUnknown.get(rel);
			if (mUnknown.size() == 0) continue;
			for (String a : mUnknown.keySet())
				for (String b : mUnknown.get(a))
					FFile.write(bw, "%s\t%s\t%s\n", rel, a, b);
		}
		FFile.close(bw);
	}

	public String google = "http://www.google.com/search?q=";

	public static void generateAMT() {
		System.out.println("\ngenerateAMT()");


		VectorS vItems = new VectorS();
		VectorS vLabels = new VectorS();

		for (VectorS vs : FFile.enuRows("AMT." + AMTurk.fdHit + ".missing")) {

			String desc = RelationLine.loadRelationInfor().get(vs.get(0)).humanFormat;
			int a = 1;
			int b = 1;
			if (desc.startsWith("arg1")) {
				a = 1;
				b = 2;
			} else if (desc.startsWith("arg2")) {
				b = 2;
				a = 1;
			} else FSystem.die("desc should start with arg1 or arg2=" + desc);

			String item = String.format("\t%s\t%s\t%s\t%s\t%s\t%s", 
					desc.replaceAll("arg1", "").replaceAll("arg2", ""), 
					vs.get(a).toUpperCase(), 
					vs.get(b).toUpperCase(), 
					vs.get(a).replaceAll("_", "+"), 
					vs.get(b).replaceAll("_", "+"), 
					"search");
			vItems.add(item);
			if (vs.size() >= 4) vLabels.add(vs.get(3));
			//Integer.parseInt(vs[3]));
		}

		AMTurk.generateInputFile("\tr%d\ta%d\tb%d\taa%d\tbb%d\trr%d", vItems, vLabels);//"AMT."+AMTurk.fdHit+"/.AMT.input"

	}

	public static void loadLabels2AMT() {
		VectorS vsMissing = new VectorS();

		for (String rel : FFile.getFileNames(fdLabel))
			if (!rel.startsWith(".")) for (String line : FFile.enuLines(fdLabel + rel))
				vsMissing.add(rel + "\t" + line);

		vsMissing.save(fdLabel + ".missing");

		predicate_folder_ = fdLabel;
		generateAMT();
	}

	public void evaluate() {

		System.out.println("\nevaluate()");
		//Param.overwriteFrom("conf");
		VectorS vsRlt = new VectorS();

		for (String rel : Param.ms.get("col1Values").split(",")) {
			System.out.println(rel + "-->\t" + RelationLine.getDescription(rel));

			loadLabels(rel);

			loadResults(rel);
			mUnknown = mmUnknown.getC(rel);
			double p10 = evaluate(10, 10);
			double p100 = evaluate(100, 50);
			double p1000 = evaluate(1000, 50);

			vsRlt.add(String.format("%s\t%d\t%.3f\t%.3f\t%.3f\t%d", RelationLine.getName(rel),
					vsQ.size()//, evaluate(1,1)
					, p10, p100, p1000, mUnknown.size()));
		}
		System.out.println("task\t#R\tp@10\tp@100\tp@1000\t#UL");
		System.out.println(vsRlt.join("\n"));
		//System.out.println("#unknown label="+mUnknown.size());
		generateMissing();
		//	generateAMT();
	}

	public static String fdAFS = "/afs/cs.cmu.edu/user/nlao/www/demo/wk/";
	public static String fdRun = "run06.PRA/";

	public static boolean unlabeled_query = true;
	public static String query_folder_ = "queries." + (unlabeled_query ? "UL" : "all");

	//public static String fdPred="predictions.FOIL.MAX/";//MAX  SUM
	//	public static String fdPred="queries.all.PRA.RL";
	public static String predicate_folder_ = "queries.FOIL01.MAX";

	public static int nTop = 1000;
	public static String code = ".top" + nTop;
	//public static String fdOut= fdPred+".top"+nTop;

	public static String fdLabel = "label/";
	public static String fnsAMTLabel = "AMT.pra/.AMT AMT.exp01/.AMT";

	//TVector<Query> vQ
	abstract void predictForTask(LearnerPRA l, String rel);

	public void predict() {
		System.out.println("predict()");

		Param.overwriteFrom("conf");
		LearnerPRA l = SmallJobs.tryloadTask();

		FFile.mkdirs(predicate_folder_);

		for (String rel : Param.ms.get("col1Values").split(",")) {
			System.out.print(rel);
			FSystem.dieNotImplemented();
			//l.loadQueries(query_folder_ + "/" + rel);
			predictForTask(l, rel);
		}
		return;
	}

}
