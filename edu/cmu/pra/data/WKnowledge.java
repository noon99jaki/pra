package edu.cmu.pra.data;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Pipe;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.algorithm.sequence.SeqS.PipeSUniq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FPattern;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.foil.EvaluatorFOIL;
import edu.cmu.pra.foil.EvaluatorPRA;
import edu.cmu.pra.foil.RelationLine;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.postprocess.Latex;

public class WKnowledge {

	public static String removePrefix(String name) {
		int i = name.lastIndexOf(':');
		if (i == -1) return name;
		return name.substring(i + 1);
	}
	EvaluatorPRA pra = null;
	EvaluatorFOIL foil = null;

	public static class KBLine implements IParseLine {
		public String Entity;
		public String Relation;
		public String Value;
		public String Iteration;
		public String Probability;
		public double prob;
		public String Source;

		public static String sanitize(String name) {
			int i = name.lastIndexOf(':');
			if (i > 0) name = name.substring(i + 1);
			name = name.replace(',', '_');
			return name;
		}

		public void sanitize() {
			Relation = sanitize(Relation);
			Entity = sanitize(Entity);
			Value = sanitize(Value);
		}

		public String toTextEdge() {
			return Relation + "\tc" + Graph.type_name_sep + Entity + "\tc"
					+ Graph.type_name_sep + Value;
		}

		public String toInversedTextEdge() {
			return "_" + Relation + "\tc" + Graph.type_name_sep + Value + "\tc"
					+ Graph.type_name_sep + Entity;
		}

		public String toAssertion() {
			return Relation + "(" + Entity + "," + Value + ")";
		}

		public String toDynamicAssertion() {//dynamic
			return "R(" + Entity + "," + Value + "," + Relation + ")";
		}

		public String toString() {
			return toAssertion();
		}

		public boolean parseLine(String line) {
			VectorS vs = FString.splitVS(line, "\t");
			int i = 0;
			Entity = vs.get(i);
			++i;
			Relation = vs.get(i);
			++i;
			Value = vs.get(i);
			++i;
			Iteration = vs.get(i);
			++i;
			Probability = vs.get(i);
			++i;
			if (Probability.startsWith("[")) Probability = Probability.substring(1,
					Probability.length() - 1);
			prob = Double.parseDouble(Probability);
			Source = vs.get(i);
			++i;
			//Candidate Source
			sanitize();
			return true;
		}

		public static Seq<KBLine> reader(String fn) {
			return reader(fn, false);
		}

		public static Seq<KBLine> reader(String fn, boolean bSkipTitle) {
			return new SeqTransform<KBLine>(KBLine.class, fn, bSkipTitle);
		}
	}

	public static void createNegative() {
		String fn = "NELL.08m.202.knownNegatives.csv";

		KBLine.reader(fn, true).select(new Pipe<KBLine, String>() {
			public String transform(KBLine e) {
				//{"john_mccain" "united_states_president"}
				VectorS vs = FPattern.matchParts(e.Value, "\"(.+)\" \"(.+)\"");//\\{\\}
				if (vs == null) return null;
				if (vs.size() == 0) return e.Entity + "(" + e.Value + ")";

				e.Relation = e.Entity;
				e.Entity = vs.get(0);
				e.Value = vs.get(1);
				return e.toDynamicAssertion();
			};
		}).select(new PipeSUniq()).save("202.negative");
	}

	public static void implied() {
		String fn = "NELL.08m.210.esv-implied.csv";
		//String fn="bkisiel_aaai10_08m.150.esv-implied.csv";

		//String pat="SpreadsheetEdits";
		String pat = "OntologyModifier";

		//FFile.enuLines(fn).select(new PipeSContains(pat)).save("210."+pat);
	}

	public static void printRelInfor() {
		System.out.println("\n loadRelInv()");
		MapSS mRelInf = new MapSS();
		for (RelationLine r : RelationLine.reader("relations", true)) {
			String rel = r.name.toLowerCase();
			//String inv =r.inverse.toLowerCase();
			mRelInf.put(rel, r.nrOfValues);
			mRelInf.put("_" + rel, r.nrOfInverseValues);
		}

		for (String rel : Param.ms.get("col1Values").split(","))
			System.out.println(rel + "\t" + mRelInf.get(rel));
		return;
	}

	public static void testIntersection() {
		SetS m1 = SetS.fromFile("AMT.pra/.missing");
		SetS m2 = SetS.fromFile("AMT.exp01/.missing");
		System.out.print("intersection=\n" + m1.and(m2).join("\n"));
	}

	public static void createPositive() {
		String fn = "210.SpreadsheetEdits";

		//KBLine.reader(fn, true).select(pipeDB).select(new PipeSUniq()).save("210.positive");
	}

	//"NELL.08m.165.cesv.csv";
	public static void createEdgeFile(String csv_file, String str_threshold,
			String format) {
		double thProb = Double.parseDouble(str_threshold);

		BufferedWriter file = FFile.newWriter(csv_file + ".p" + thProb + "."
				+ format);
		for (KBLine line : KBLine.reader(csv_file, true)) {
			if (line.prob < thProb) continue;

			if (format.equals("edges")) {
				FFile.writeln(file, line.toTextEdge());
				FFile.writeln(file, line.toInversedTextEdge());
			} else if (format.equals("db")) {
				FFile.writeln(file, line.toDynamicAssertion());
			} else {
				FSystem.die("unknown format=" + format);
			}
		}
	}

	

	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		//extractOntoloty("outputfile.csv");

		//implied();
		//createNegative();
		//createQueries();		
		//	printRelInfor();

		//Evaluator e=new EvaluatorPRA();		//EvaluatorPRA EvaluatorFOIL
		//e.createQueries();
		//e.predict();
		//removeLabeledResults();		
		//e.sortAndPublish();
		//e.evaluate();

		/*		FFile.mkdirs("AMT."+AMTurk.fdHit);
				FTable.mergeFilesUniq("queries.UL.PRA/.missing queries.UL.FOIL.MAX/.missing"		, "AMT."+ AMTurk.fdHit+".missing");
				testIntersection();
				Evaluator.generateAMT();
			*/
		//Evaluator.loadLabels2AMT();
		Param.overwriteFrom("conf");
		String task = args[0];
		if (task.equals("latexPathWK")) Latex.latexPathWK("Models");
		else if (task.equals("createEdgeFile")) createEdgeFile(args[1], args[2],
				args[3]);
	}
}
