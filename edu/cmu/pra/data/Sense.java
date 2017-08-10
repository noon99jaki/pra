package edu.cmu.pra.data;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapSMapSS;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.model.ModelPathRank;
import edu.cmu.pra.model.Query;

public class Sense {
	
	// each line looks like this
	//be-v	13	-2.452e-01,*02655135-v	-2.452e-01,02268246-v
	public static class PredictionLine implements IParseLine{
		public String name=null;
		public int num_results=-1;
		public VectorS results= new VectorS();
		public VectorD scores = new VectorD();
		public int correct_answer=-1;
		
		public PredictionLine() {
		}
		
		public String toString(){
			return null;
		}
		public boolean parseLine(String line){
			VectorS row= FString.splitVS(line,"\t");
			if (row.size() < 2) {
				System.err.println("empty prediction="+line);
				//FFile.line_);
				return false;
			}			
			
			name = row.get(0);
			num_results = Integer.parseInt(row.get(1));
			for (int i=2; i<row.size(); ++i) {
				String[] vs= row.get(i).split(",");
				FSystem.checkTrue(vs.length ==2, "weird formated item=" + row.get(i));
				scores.add(Double.parseDouble(vs[0]));
				if (vs[1].startsWith("*")){
					correct_answer = i-2;
					results.add(vs[1].substring(1));
				}
				else
					results.add(vs[1]);
			}
			return true;
		}
		
		public static Seq<PredictionLine> reader(String fn){
			return reader(fn, false);
		}
		
		public static Seq<PredictionLine> reader(String fn,boolean bSkipTitle){
			return new SeqTransform<PredictionLine>(	PredictionLine.class, fn, bSkipTitle);
		}
		
	};
	
	public static class QueryLine implements IParseLine, IGetStrByStr{
		String token;
		String lemma_pos;
		String context;
		String good;
		String bad;
		
		public String getString(String name){
			if (name.equals("T")) return getQueryLine(true);
			else if (name.equals("F")) return getQueryLine(false);
			return null;
		}
		
		public String getQueryLine(boolean give_negative_sample) {
			if (give_negative_sample)
				return lemma_pos + "\t" + context  + "\t" + good + "\t" + bad;
			else
				return lemma_pos + "\t" + context  + "\t" + good;
		}
		public QueryLine() {
			
		}
		
		public String toString(){
			return token;
		}
		public boolean parseLine(String line){
			VectorS vs= FString.splitVS(line,"\t");
			if (vs.size() < 4) {
				System.err.println("bad query line=" + line);//FFile.line_);
				return false;
			}			
			if (vs.size()<5)	vs.add("");
			
			int i=0;
			token = vs.get(i++);
			lemma_pos = vs.get(i++);
			context = vs.get(i++);
			good = vs.get(i++).replaceAll("\\$", "");
			bad = vs.get(i++).replaceAll("\\$", "");
			return true;
		}
		
		public static Seq<QueryLine> reader(String fn){
			return reader(fn, false);
		}
		
		public static Seq<QueryLine> reader(String fn,boolean bSkipTitle){
			return new SeqTransform<QueryLine>(	QueryLine.class, fn, bSkipTitle);
		}
		
	};

	
	public static void createQueries(String raw_file, boolean give_negative_sample) {
		String code = give_negative_sample?"T":"F";
		QueryLine.reader(raw_file)
			.selectS(code).save(raw_file + ".query" + code);
	}
	
	public static void predict(String model_file, String raw_file) {
		Graph graph = new Graph();
		//graph.loadIndics(Param.ms.get("graph_folder"));
		graph.loadGraph(Param.ms.get("graph_folder"));
		GraphWalker walker = new GraphWalker(graph);
		ModelPathRank model = new ModelPathRank(walker);
		model.loadModel(model_file);//Param.ms.get("model"));
		
		VectorS results = new VectorS();
		
		for (QueryLine line : QueryLine.reader(raw_file)) {
			if (line == null) continue;
			
			String task = line.token.substring(0, line.token.indexOf('.'));
			String result_line = line.lemma_pos + " " + line.token + " ";
			
			if (line.bad.length() > 0) {
				Query query = model.walker_.parseQuery(line.getQueryLine(true));
				MapID result  = model.predict(query);
				if (result.size() >0 ) {
					int top = result.idxMax();
					result_line += graph.getNodeName(top);
				} else {
					System.out.println(" no results for " + line.token + "\t" + line.lemma_pos);
					result_line += line.good + " " + line.bad;//line.graph.getNodeName(top);
				}
			}
			else {
				//continue;
				result_line += line.good;
			}
			results.add(result_line);
		}
		results.save(raw_file + ".result");
		return;
	}
	
	public static void getPrediction(String raw_file, String prediction_file) {
		VectorX<QueryLine> lines = new VectorX<QueryLine>(
				 QueryLine.reader(raw_file), QueryLine.class);
		
		int id =0;
		VectorS results = new VectorS();
		for (PredictionLine pred: PredictionLine.reader(prediction_file)) {
			
			while(!pred.name.equals(lines.get(id).lemma_pos)) ++id;
			
			QueryLine line = lines.get(id);
			++id;
			
			String result_line = line.lemma_pos + " " + line.token + " ";
			
			if (line.bad.length() > 0) {
				if (pred.results.size() >0 ) {
					result_line += pred.results.firstElement();
				} else {
					System.out.println(" no results for " + line.token + "\t" + line.lemma_pos);
					result_line += line.good + " " + line.bad;//line.graph.getNodeName(top);
				}
			}
			else {
				result_line += line.good;
			}
			results.add(result_line);
		}
		results.save(prediction_file + ".result");
		
		System.out.println("#results=" + results.size());
		return;
	}
	
	public static void transform171(String mapping, String result_file) {
		//MapSS map = MapSS.fromFile(mapping, " ");
		MapSMapSS sense_lemma_map = new MapSMapSS();
		for(VectorS line: FFile.enuRows(mapping, " ")) {
			String sense = line.get(0);
			String map = line.get(1);
			String lemma = map.substring(0, map.indexOf('%'));
			sense_lemma_map.getC(sense).put(lemma, map);
		}
		
		
		VectorS results = new VectorS();
		for(VectorS line: FFile.enuRows(result_file, " ")) {
			String lemma = line.get(0);
			lemma = lemma.substring(0, lemma.lastIndexOf('-'));
			
			String token = line.get(1);
			String task = token.substring(0, token.indexOf('.'));
			line.set(0, task);

			for (int i=2; i< line.size(); ++i) {
				String sense =line.get(i);
				MapSS map = sense_lemma_map.get(sense);
				if (map == null) {
					System.err.println("missing map for sense=" + sense);
					continue;
				}
				String wn17 = map.get(lemma);
				if (wn17 == null) {
					System.err.println("missing map for lemma=" + lemma);
					continue;
				}
				
				line.set(i, wn17);
			}
			results.add(line.join(" "));
		}
		results.save(result_file + ".171");
		return;
	}

	
	public static void transform30(String result_file) {
		
		VectorS results = new VectorS();
		for(VectorS line: FFile.enuRows(result_file, " ")) {
			String lemma = line.get(0);
			lemma = lemma.substring(0, lemma.lastIndexOf('-'));
			
			String token = line.get(1);
			String task = token.substring(0, token.indexOf('.'));
			line.set(0, task);
			double weight = 1.0 / (line.size() -2);
			for (int i=2; i< line.size(); ++i) {
				line.set(i, line.get(i) +   "/" + weight);
				
			}
			line.add("!!");
			line.add(lemma);
			
			results.add(line.join(" "));
		}
		results.save(result_file + ".30");
		return;
	}
	
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		String task = args[0];
		if (task.equals("createQueriesF")) createQueries(args[1], false);
		else if (task.equals("createQueriesT")) createQueries(args[1], true);
		else if (task.equals("predict")) predict(args[1], args[2]);
		else if (task.equals("getPrediction")) getPrediction(args[1], args[2]);
		else if (task.equals("transform30")) transform30(args[1]);
		else if (task.equals("transform171")) transform171(args[1], args[2]);
												 //transfrom171
		else FSystem.die("unknown task=" + task);
	}

}
