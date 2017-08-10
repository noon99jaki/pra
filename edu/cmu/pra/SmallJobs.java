package edu.cmu.pra;

import java.io.BufferedWriter;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.MapSMapSI;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.MapSSetS;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.structure.KeepTopK;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.foil.RelationLine;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphCreator;
import edu.cmu.pra.graph.GraphWalker;

public class SmallJobs {

	public static void translateWeightNames() {
		MapSS mPapaerJournal = new MapSS();
		//mPapaerJournal.load("../data/papers.csv",0,2, ",");
		mPapaerJournal.fromFile("../data/SGD.abstract.txt", 0, 2, "\t");

		//VectorS vs=;
		MapSMapSI mmJournalWeightCount = new MapSMapSI();

		Pattern pa = Pattern.compile("(\\d+)>.*");

		for (String s : FFile.enuLines("weights")) {
			String v[] = s.split("\t");
			Matcher ma = pa.matcher(s);
			if (!ma.find()) continue;
			String paper = ma.group(1);
			String journal = mPapaerJournal.get(paper);
			if (journal == null) {
				System.err.println("cannot find paper " + paper);
				continue;
			}
			mmJournalWeightCount.plusOn(journal, v[0], 1);
		}

		BufferedWriter bw = FFile.newWriter("weights.dedup");
		for (Map.Entry<String, MapSI> e : mmJournalWeightCount.entrySet()) {
			for (Map.Entry<String, Integer> ee : e.getValue().entrySet()) {
				FFile.write(bw, "%s\t%s\t%d\n", ee.getKey(), e.getKey(), ee.getValue());
			}
		}
		FFile.close(bw);
	}

	private static void printScatter(String fn, MapSD set1, MapSD set2) {
		BufferedWriter bw = FFile.newWriter(fn);
		FFile.write(bw, "entity\tscore1\tscore2\n");

		SetS set = new SetS();
		set.addAll(set1.keySet());
		set.addAll(set2.keySet());

		for (String e : set)
			FFile.write(bw, "%s\t%.2e\t%.2e\n", 
					e, set1.getD(e, 0.0), set2.getD(e,	0.0));
		FFile.close(bw);
	}

	public static void dump2ghirlFormat(SetS msTextRel) {
		l = tryloadTask();
	//	l.graph_.save2GhirlFormat(msTextRel, "ghirl.X");
	}

	public static LearnerPRA l = null;

	public static LearnerPRA tryloadTask() {
		if (l == null) l = loadTask();
		return l;
	}

	private static LearnerPRA loadTask() {
//		Learner.initParam(LearnerPRA.class);
		LearnerPRA l = new LearnerPRA(null);
		return l;
	}
	//	
	//	
	//	public static void dumpNodeLinkCountsByTime(){
	//		l= tryloadTask();
	//		l.graph_.dumpNodeCountsByTime(		"countNodeByTime."+l.p.codeTask+".csv");
	//		l.graph_.dumpLinkCountsByTime("countLinkByTime."+l.p.codeTask+".csv");
	//	}
	//	

	static int r;

	/**
	 * pmid year citeCount
	 */
	public static void dumpPaperCiteCount() {

	//		l= tryloadTask();
	//		 r= l.graph_.schema.getRelation("_Cites");
	//
	//		Pipe pipe= new Pipe<Entity, String>(){
	//			public String transform(Entity e){		
	//				TimedLinks ol=e.relation_outlinks_.get(r.id);
	//				int nCite=(ol!=null)?ol.viEnt.size():0;
	//				return e.name_+"\t"+e.time_+"\t"+nCite;
	//		};};
	//				
	//		l.graph_.getSection("paper")
	//		.vEnt.enu().select(pipe)
	//		.save("count.paper.cite."+l.p.codeTask);
	}

	public static void dump2PCTFormat(String graph_folder) {
		System.out.println("dump2PCTFormat()");
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		SetS msTextRel = new SetS("Title");// ??? not needed?
		graph.save2CompactGhirlFormat(msTextRel, "model_.p.dbName");
	}

	public static void dump2FOILFormat(String graph_folder) {
		System.out.println("dump2FOILFormat()");
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		graph.save2FOILFormat(graph_folder + "foil");
	//	graph.creatFOILQueries(graph_folder + "foil.queries/");
	}
	
	public static void dump2FOILFormatA(String graph_folder, String relations) {
		System.out.println("dump2FOILFormatA()");
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		graph.save2FOILFormatA(graph_folder + "foil.selected", relations);
	//	graph.creatFOILQueries(graph_folder + "foil.queries/");
	}
	

	public void createGhirlQueries() {

	//		this.loadQueries(null);//p1.dataFolder+p1.scenarioFile);
	//
	//		System.out.println("createGhirlQueries()...");
	//		String fd=LearnerGhirl.p1.taskFile+"/";
	//		//String dbName= "YPJ";
	//		FSystem.cmd("rm -rf "+fd);
	//		FFile.mkdirs(fd+"train");
	//		FFile.mkdirs(fd+"tune");
	//		FFile.mkdirs(fd+"test");
	//		for (Query q: queries_){
	//			if (q.good_.size()==0) continue;
	//			
	//			if (FRand.drawBoolean(0.5))	q.saveInGhirlFormat(fd+"test/"+q.name,p1.dbName);
	//			else if (FRand.drawBoolean(0.5))	q.saveInGhirlFormat(fd+"tune/"+q.name,p1.dbName);
	//			else 	q.saveInGhirlFormat(fd+"train/"+q.name,p1.dbName);
	//		}		
	//		
	//		graph_.saveInGhirlFormat(fd+"data", 2008);
	//		System.out.println("done");
	}
	

	public static void createTrainQueriesFromEdges(String edge_file, String output_folder) {
		if (!output_folder.endsWith("/")) output_folder += "/";

		FFile.mkdirs(output_folder);
		
		MapSI relation_counts = new MapSI();

		MapSX<MapSSetS> relation_key_values = new MapSX<MapSSetS>(MapSSetS.class);
		for (VectorS row : FFile.enuRows(edge_file)) {
			relation_key_values.getC(row.get(0)).getC(row.get(1)).add(row.get(2));
			relation_counts.plusOn(row.get(0));
		}
		
		for (Map.Entry<String, MapSSetS> it : relation_key_values.entrySet()) {
			it.getValue().save(output_folder + it.getKey(), "\t");
		}
		
		relation_counts.save(output_folder + "relation_counts");		
	}

	public static void sortPredictions(double threshold, String prediction_file, String output) {
		VectorS predictions = new VectorS();
		VectorD scores = new VectorD();
		
		for (VectorS line : FFile.enuRows(prediction_file)) {
			if (line.size() <= 2) continue;
			String key = line.get(0);
			for (int i = 2; i < line.size(); ++i) {
				String item = line.get(i);
				int idx = item.indexOf(',');
				
				FSystem.checkTrue(idx >0, "expect idx>0");
				
				String score_str = item.substring(0, idx);
				double score = Double.parseDouble(score_str);
				if (score < threshold) continue;

				String value = item.substring(idx + 1);
				if (value.startsWith("*")) continue;

				predictions.add(score_str + "\t" + key + "\t" + value);
				scores.add(score);
			}

		}			
		predictions.sub(scores.sortId(true)).save(output);
	}
	
	public static void sortPredictions(
			String prediction_folder, String score_threshold) {
		if (!prediction_folder.endsWith("/")) prediction_folder += "/";
		double threshold = Double.parseDouble(score_threshold);
		
		for (String file : FFile.getFileNames(prediction_folder)) {
			if (file.contains(".")) continue;
			sortPredictions(threshold, prediction_folder + file, prediction_folder + file + ".sorted");
		}
	}
	
	// TODO: seems to be wrong
	public static void sortReasons(int nTop, String reason_file, String output_file) {
		if (!FFile.exist(reason_file)) return;
		KeepTopK keep = new KeepTopK(nTop);
		for (VectorX<String> vs: FFile.enuSections(reason_file)) {
			if (vs.size() < 2) continue;

			String line = vs.get(0) + "\t" + vs.get(1);
			String row2[] = vs.get(1).split("\t");
			keep.addAbsValue(Double.parseDouble(row2[0]), line);
		}
		keep.objects_.save(output_file);
	}
	
// example	
//	c$concept:athlete:torii_hunter
//	4.197e+00	*c$concept:sportsleague:mlb	1=8.476e+00	50=7.065e-03	2=3.589e-02	71=1.997e-02	66=-2.875e-01	47=1.120e-03	44=1.168e-01	74=-7.412e-02	60=1.746e-01
	public static void sortReasons(double  threshold, String reason_file, String output_file) {
		if (!FFile.exist(reason_file)) return;
		VectorS predictions = new VectorS();
		VectorD scores = new VectorD();

		for (VectorX<String> vs: FFile.enuSections(reason_file)) {
			if (vs.size() < 2) continue;
			
			String key = vs.get(0)  ;
			for (int i=1;i<vs.size();++i) {
				String row[] = vs.get(i).split("\t");
				double score = Double.parseDouble(row[0]);
				if (score< threshold) continue;
				
				String value = row[1];
				if (value.startsWith("*")) continue;
				scores.add(score);
				predictions.add(row[0] +"\t" + key+ "\t" + value 
						+ "\t" + FString.join(row, " ", 2, Math.min(2+10, row.length)));
			}
		}
		predictions.sub(scores.sortId(true)).save(output_file);
	}
	
	public static void sortReasons(
			String prediction_folder, String score_threshold) {
		if (!prediction_folder.endsWith("/")) prediction_folder += "/";
		double threshold = Double.parseDouble(score_threshold);
		
		for (String file : FFile.getFileNames(prediction_folder, ".*\\.reasons")) {
			System.out.println(file);
			sortReasons(threshold, prediction_folder + file, prediction_folder + file + ".sorted");
		}
	}

	
	// Transform model from old format to new format
	public static void transformModel(String model_file, String edge_type_file) {
		
		MapSS old_new = new MapSS();
		for (String relation : FFile.enuLines(edge_type_file)) {
			String old_name = relation.substring(0, Math.min(4, relation.length()));
			String new_name = relation.startsWith("_")?
					relation.substring(1) + "Inverse" : relation;
			old_new.put(old_name, new_name);			
		}
		
		SetS target_relations = new SetS();
		target_relations.loadFile(
				"/usr0/nlao/work/ghirl/yeast-aug11-mips_compact/graphLink.pct");
		
		BufferedWriter writer = FFile.newWriter(model_file + ".new");
		for (VectorS line: FFile.enuRows(model_file, true)) {
			if (Double.parseDouble(line.get(0)) == 0.0) continue;
			
			String path = line.get(1);
			if (path.equals("bias")) continue;
			
			String vs[]= path.split("[\\(\\)]");
			VectorS relations = new VectorS();
			boolean succ = true;
			for (int i = 1; i < vs.length; i += 2) {
				String new_name = old_new.get(vs[i]);
				FSystem.checkTrue(new_name != null, "new_name != null");
				if (!target_relations.contains(new_name)) {
						System.err.println("target_relations.contains("+ new_name + ")");
						succ = false;
						break;
				}
				relations.add(new_name);
			}
			if (!succ) continue;
			
			String new_path = relations.join(",");
			if (new_path.startsWith("Y")) 
				new_path = "1:"+ new_path;
			else
				new_path = "2:"+ new_path;
			
			line.set(1, new_path);
			FFile.writeln(writer, line.join("\t"));				
		}
		FFile.close(writer);
	}

	
	public static void createQueries(String graph_folder, 
			String output_folder, String train_str, String with_range_str) {
		boolean train = Boolean.parseBoolean(train_str); 
		boolean with_range = Boolean.parseBoolean(with_range_str);
		
		createQueries(graph_folder,  output_folder, train, with_range,
				 RelationLine.getDomains(), RelationLine.getRanges());
	}

	public static int createQueries_max_relation_count = 10000;
	public static int createQueries_min_relation_count = 10;
	
	// find nodes having certain relation with the src_node
	//public SetI repeatedExpand(String seed, String relation) {

	public static void createQueries(String graph_folder, 
			String output_folder, boolean train, boolean with_range, 
			MapSS relation_domain, MapSS relation_range) {
		
		if (!graph_folder.endsWith("/")) graph_folder += "/";
		if (!output_folder.endsWith("/")) output_folder += "/";
		FFile.mkdirs(output_folder);
		
		VectorS filter_errors = new VectorS();
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		
		VectorI relation_counts = graph.getEdgeCounts();
		
		GraphWalker walker = new GraphWalker(graph);
		
		for (int relation_id = 0; relation_id < graph.edgeType_index_.size(); ++relation_id) {
			String relation = graph.getEdgeTypeName(relation_id);
			
			int count = relation_counts.get(relation_id);
			if (createQueries_max_relation_count != -1)
				if (count > createQueries_max_relation_count) {
					System.out.println( "skip relation " + relation + " which has frequency > " + createQueries_max_relation_count );
					continue;
				}
			if (createQueries_min_relation_count != -1)
				if (count < createQueries_min_relation_count) {
					System.out.println( "skip relation " + relation + " which has frequency < " + createQueries_min_relation_count );
					continue;
				}
			
			String domain = relation_domain.get(relation);			
			if (domain == null) {
				System.out.println("domain not found for relation=" + relation);
				continue;
			}
			String range = relation_range.get(relation);
			if (range == null) {
				System.out.println("range not found for relation=" + relation);
				continue;
			}
			domain = "c" + Graph.type_name_sep + domain;
			range = "c" + Graph.type_name_sep + range;
			
			SetI domain_filter = walker.repeatedExpand(domain, "_generalizations");
			SetI range_filter = walker.repeatedExpand(range, "_generalizations");
			
			
			SetI errors = new SetI();
			VectorS queries = new VectorS();
			SetI answers = new SetI();
			
			if (train) {
				for (int id = 0; id < graph.nodes_.size(); ++id) {
					VectorI values = graph.nodes_.get(id).get(relation_id);
					if (values == null) continue;
					String key = graph.getNodeName(id);
					
					answers.addAll(values);
					for (int target : values) if (!range_filter.contains(target)) errors.add(target);
					
					if (with_range) 
						queries.add(key + "\t" + range + "\t" + walker.getNodeNames(values).join(" "));
					else 
						queries.add(key + "\t" + walker.getNodeNames(values).join(" "));
				}
				
				filter_errors.add(relation +"\t" + queries.size()
						+ "\t" + range + "\t" + range_filter.size() 
						+ "\t" + answers.size() + "\t" + errors.size());
				if (errors.size() > 0) 
					walker.getNodeNames(errors).save(output_folder + relation + ".filter_error");
			} else {
				for (int id : domain_filter) {
					VectorI vi = graph.nodes_.get(id).get(relation_id);
					if (vi == null) continue;
					if (with_range)
						queries.add( graph.getNodeName(id) + "\t" + range
								+ "\t" + graph.getNodeNames(vi).join(" "));
					else
						queries.add( graph.getNodeName(id) 
								+ "\t" + graph.getNodeNames(vi).join(" "));
				}				
			}
			queries.save(output_folder + relation);
		}
		
		if (train) {
			filter_errors.saveWithTitle(
				output_folder + "createQueries.filter_errors",
				"relation\t#queries\trange\t#filter_size\t#good\t#filter_error");
		}
	}
	public static void dumpGraph(String graph_folder) {
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		graph.saveEdges(graph_folder +"edge_dump");
	}
	
	public static void graphStatistics(String graph_folder) {
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		VectorI counts = graph.getEdgeCounts();
		VectorS out = new VectorS();
		for (int i=0; i < counts.size(); ++ i) {
			out.add(graph.getEdgeTypeName(i) + "\t" + counts.get(i));
		}
		out.save(graph_folder + "edge_type_counts");
	}
	
	public static void predict() {
		GraphWalker.silent_ =true;
		Graph graph = new Graph();
		graph.loadGraph(Param.ms.get("graph_folder"));
		GraphWalker walker = new GraphWalker(graph);
		for (String relation: Param.ms.get("reporter_keys").split(",")){
			Param.overwrite("task=predict,target_relation="+relation);
			LearnerPRA learner = new LearnerPRA(walker, "task=predict,target_relation="+relation);
			learner.run();			
		}
	}

	
	public static void indexGraph(String graph_folder, String format) {
		GraphCreator graph_creator = new GraphCreator();
		graph_creator.indexGraph(graph_folder, format);
		
	}
	
	// transform an old format scenario file to new format scenario file
	// with node types and tab separations
	public static boolean addNodeTypes(String fn, String prefixes, String output_file) {
		VectorS prefix = new VectorS(prefixes.split("-"));

		BufferedWriter bw = FFile.newWriter(output_file);
		if (bw == null) return false;		
		
		for (VectorS row : FFile.enuRows(fn, ",")) {
			
			for (int i=0; i< prefix.size(); ++i) {
				String str = row.get(i);
				if (str.length()==0) continue;
				VectorS items = VectorS.fromLine(str, " ");
				row.set(i, items.joinPrefixed(" ", prefix.get(i) +  "$"));
			}
			FFile.writeln(bw, row.join());
		}
		
		FFile.close(bw);
		return true;
	}
	
	public static boolean batchLearnerPRA(String class_folder, String target_relation_file) {		
		for (String relation : FFile.enuLines(target_relation_file)) {
			FSystem.cmd("java -cp " + class_folder + " edu.cmu.pra.LearnerPRA target_relation=" + relation);
		}
		return true;
	}
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		String task = args[0];
		if (task.equals("translateWeightNames")) translateWeightNames();
		else if (task.equals("dump2PCTFormat")) dump2PCTFormat(args[1]);
		else if (task.equals("dump2ghirlFormat")) dump2ghirlFormat(null);
		else if (task.equals("dumpPaperCiteCount")) dumpPaperCiteCount();
		
		else if (task.equals("indexGraph")) indexGraph(args[1], args[2]);
		else if (task.equals("transformModel")) transformModel(args[1], args[2]);
		
		else if (task.equals("createQueries")) createQueries(args[1], args[2], args[3], args[4]);

		else if (task.equals("dumpGraph")) dumpGraph(args[1]);
		else if (task.equals("graphStatistics")) graphStatistics(args[1]);
		else if (task.equals("predict")) predict();
		else if (task.equals("sortPredictions")) sortPredictions(args[1], args[2]);
		else if (task.equals("sortReasons")) sortReasons(args[1], args[2]);
		
		else if (task.equals("dump2FOILFormat")) dump2FOILFormat(args[1]);
		else if (task.equals("dump2FOILFormatA")) dump2FOILFormatA(args[1],args[2]);

		else if (task.equals("addNodeTypes")) addNodeTypes(args[1],args[2],args[3]);
		else if (task.equals("batchLearnerPRA")) batchLearnerPRA(args[1], args[2]);
		
		else FSystem.die("unknown task=" + task);
	}
}
