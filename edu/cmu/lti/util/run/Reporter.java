package edu.cmu.lti.util.run;

import java.io.BufferedWriter;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapIS;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.FHtml;
import edu.cmu.lti.util.html.HtmlPage;
import edu.cmu.lti.util.html.FHtml.CStyle;
import edu.cmu.lti.util.run.Learner.Task;
import edu.cmu.lti.util.text.FString;

//collect and organize scores into an html report
// from folder <task>.scores 
public abstract class Reporter {

	public String best_folder_ = null;	// absolute folder to the output
	public String[] best_scores_ = null;	// best score
	public String best_score_file_ = null;	// the score file

	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public String publish_folder;//="/afs/cs.cmu.edu/user/nlao/www/demo/wk/";
		public String result_filter;
		public String output_folder;
		public String prediction_folder;
		public String model_folder;// = p.output_folder + "best_models/";

		public Task task; //in Mb
		String score_folder = null;
		String eval_column=null;

		public String reporter_keys = null;

		public void parse() {
			task = Task.valueOf(getString("task", Task.sCV.name()));
			publish_folder = getFolder("publish_folder", null);
			output_folder = getFolder("output_folder", "./");
			
			prediction_folder = getFolder("prediction_folder", null);
			
			result_filter = getString("result_filter", null);

			eval_column = getString("eval_column", "mrr");
			reporter_keys = getString("reporter_keys", null);

			score_folder = output_folder + task + ".scores/";
			model_folder = output_folder + task + ".models/";
		}

		public Param(Class c) {
			super(c);//Tunner.class);
			parse();
		}
	}
	public Param p;

	public Reporter(Class c) {//,VectorS vParam,VectorI vGridSize,VectorS vDParam){
		p = new Param(c);
	}

	protected boolean findBest(String relation, int eval_column, String result_filter) {
		double best_eval = -1.0;
		
		best_folder_ = null;
		best_scores_ = null;
		best_score_file_ = null;

		for (String score_file : FFile.getFileNames(p.score_folder, ".*=" + relation
				+ ".*")) {
			
			if (result_filter != null)
				if (!score_file.contains(result_filter)) continue;

			String lines[] = FFile.loadString(p.score_folder + score_file).split("\n");
			String row0[] = FString.split(lines[0], '\t');

			if (eval_column !=-1) {
				double eval = Double.parseDouble(row0[eval_column]);
				if (eval <= best_eval) continue;			
				best_eval = eval;
			}
			
			best_folder_ = lines[1];
			best_scores_ = row0;
			best_score_file_ = score_file;
		}
		return best_folder_ !=null;
	}

	// specify how to format each column
	protected MapSS column_name_format_ = new MapSS();
	private void reformatScores(String[] scores) {
		for (Map.Entry<Integer, String> e : column_id_format_.entrySet()) {
			int column = e.getKey();
			scores[column] = String.format(e.getValue(), Double
					.parseDouble(scores[column]));
		}
	}
//	public static String model_folder_postfix_=".models/";
//	public static String prediction_folder_postfix_=".models/";
	
	MapIS column_id_format_;
	
	public void collectResults(String publish_folder, String filter) {
		
		FFile.mkdirs(p.model_folder);

		String title_line = FFile.loadString(p.output_folder + p.task + ".title").trim();
		VectorS titles = FString.splitVS(title_line, "\t");
		column_id_format_ = column_name_format_.replaceMatchIdx(titles);
		int eval_column = titles.idxLast(p.eval_column);

		HtmlPage main_page = new HtmlPage("index", publish_folder);
		main_page.repeat_title_=10;
		BufferedWriter eval_writer = main_page.extFile(".eval");

		main_page.newTable(p.result_filter, "\t" + title_line + "\t");
		FFile.writeln(eval_writer, "\t" + title_line);

		VectorD avg_scores = new VectorD();
		FFile.mkdirs(p.task + ".models/");
		//FFile.mkdirs(p.task + ".predictions/");
		int num_tasks=0;
		for (String key : p.reporter_keys.split(",")) {

			if (!findBest(key, eval_column, filter)) continue;

			FFile.writeln(eval_writer, best_score_file_ + "\t" + FString.join(best_scores_));
			
			int num_prediction = generateBestPage(key, publish_folder);		

			avg_scores.extend(best_scores_.length);
			avg_scores.plusOn(VectorD.from(best_scores_) );
			reformatScores(best_scores_);
			++num_tasks;

			VectorS row = new VectorS();
			String anchor = key;//createAnchor(best_file_name_) ;
			
			row.add(FHtml.addHref(anchor, key + "/index.html")	);
			row.addAll(best_scores_);
			row.add(FHtml.addHref(""+num_prediction, key + "/index.prediction.html"));
			main_page.writeRow(row, CStyle.alignRight);
		}
		
		if (num_tasks>0){	// add average scores
			VectorS row = new VectorS();
			row.add("avg");
	
			avg_scores.devideOn(num_tasks);
			String[] avg_scores_str = avg_scores.toVectorS().toArray();
			reformatScores(avg_scores_str);
			row.addAll(avg_scores_str);
			main_page.writeRow(row, CStyle.alignRight);
		}
		main_page.endTable();
		main_page.close();
		return;

	}
	
	
	static String createAnchor(String file_name) {
		VectorS parts= FString.splitVS(file_name, ",");
		for (int i=0; i< parts.size(); ++i) {
			int p= parts.get(i).indexOf('=');
			if (p !=-1) parts.set(i, parts.get(i).substring(p+1));
		}
//		anchor = anchor.replaceAll("target_relation=", "");
//		anchor = anchor.replaceAll(",id=00", "");
//		anchor = anchor.replaceAll(",", " &nbsp; &nbsp; ");
//		anchor = anchor.replaceAll(key, FHtml.bold(key));
		return parts.join(",");
	}
	
	
	abstract protected int generateBestPage(String rel, String fdOut);

	public void run(String args[]) {
		collectResults(p.publish_folder, p.result_filter);
	}
}
