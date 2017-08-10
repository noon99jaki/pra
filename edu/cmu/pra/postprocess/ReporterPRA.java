package edu.cmu.pra.postprocess;


import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.HtmlPage;
import edu.cmu.lti.util.run.Reporter;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.CTag;

public class ReporterPRA extends Reporter {

	protected String path2logic(String path, boolean bRelaceName) {
		if (path.startsWith("R")) return path;

		VectorS edges = FString.splitVS(path, ",");
		VectorS nodes = new VectorS();

		nodes.add("X");
		for (int i = 0; i < edges.size() - 1; ++i)	nodes.add("" + (char) ('A' + i));
		nodes.add("Y");

		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < edges.size(); ++i) {
			if (i > 0) sb.append("<br>");

			String rel = edges.get(i);
			if (rel.startsWith("_")) 
				sb.append(String.format("%s(%s,%s)", 
					rel.substring(1), nodes.get(i + 1), nodes.get(i)));
			else 
				sb.append(String.format("%s(%s,%s)", 
					rel, nodes.get(i), nodes.get(i + 1)));
		}
		return sb.toString();
	}

	protected void model2html(String model, HtmlPage th, boolean bLogicForm) {

		VectorD vd = new VectorD();
		VectorS vs = new VectorS();
		for (VectorS row : FFile.enuRows(model, "\t", true)) {
			double d = Double.parseDouble(row.get(0));
			if (d == 0) continue;
			vd.add(d);
			vs.add(row.get(1));
		}

		th.newTable("Rules", "ID\tWeight\tFeature\tComments");
		for (int i : vd.sortId().reverseOn()) {
			String comment = feature_comments_.get(vs.get(i));
			if (comment == null) comment = "";
			String path = (bLogicForm) ? path2logic(vs.get(i), true) : vs.get(i);
			th.addRow(String.format("<%d>\t%.1f\t%s\t%s", i, vd.get(i), path, comment));
		}

		th.endTable();
		th.close();
	}


	public static void printPrediction(String prediction_file, HtmlPage page) {

		int max_num_results = 5;
		page.newTable("predictions", 
						"id\tQuery\t#R\tHighest Score\tTop Results");

		VectorS row = new VectorS();
		int id = 0;
		for (VectorS line : FFile.enuRows(prediction_file)) {
			if (line.size() <= 2) continue;
			++id;
			row.clear();
			row.add(id + " ");
			row.add(line.get(0).replace('_', ' '));
			row.add(line.get(1));

			StringBuffer sb = new StringBuffer();
			int num = 0;
			for (int i = 2; i < line.size(); ++i) {
				String item = line.get(i);
				int idx = item.indexOf(',');
				if (i == 2) row.add(item.substring(0, idx));

				sb.append(item.substring(idx + 1)).append(", ");
				if (!item.endsWith("*")) ++num;
				if (num >= max_num_results) break;
			}
			row.add(sb.toString());
			page.addRow(row);//,CStyle.alignRight);

		}
		page.endTable();
		return;
	}

	protected int generateBestPage(String relation, String output_folder) {
		//System.out.print("\n");
		HtmlPage thC = new HtmlPage("index", output_folder + relation);
		thC.addPre(best_folder_);
		thC.copyFile(p.task + ".cout/"+best_score_file_, "cout");
		int num_prediction=0;
		
		switch(p.task) {
		case test:{
			FFile.copyFile(best_folder_ + "model", p.model_folder + relation);
		} break;
		case sCV:{
			FFile.copyFile(best_folder_ + "model.avg", p.model_folder + relation);
		} break;
		case TT :
		case train :{
			FFile.copyFile(best_folder_ + "train.model", p.model_folder + relation);
		}break;
		default: 
			FSystem.die("unknown task="+p.task);
		}
		
//		num_prediction =thC.extTablePage(".prediction",
//				p.prediction_folder + relation + ".reasons.sorted",	
//				"Score\tSource\tTarget\treasons");
//		thC.addPre(" ");
		
		thC.copyFile(p.model_folder + relation, "model");
		thC.copyFile(best_folder_ + CTag.top_predictions, CTag.top_predictions);
		model2html(p.model_folder + relation, thC, false);
		
		thC.close();
		return num_prediction;
	}

	boolean print_predictions = true;

	public MapSS feature_comments_ = null;

	public ReporterPRA() {
		super(Reporter.class);
		feature_comments_ = MapSS.fromFile("../data/feature_comments");
		column_name_format_.put("p@1", "%.3f");
		column_name_format_.put("p@10", "%.3f");
		column_name_format_.put("p@100", "%.3f");
		column_name_format_.put("p@1000", "%.3f");
		
		column_name_format_.put("#Q", "%.0f");
		column_name_format_.put("tRW", "%.1fs");
		column_name_format_.put("mRW", "%.0fmb");
		column_name_format_.put("tExp", "%.1fs");
		column_name_format_.put("mExp", "%.0fmb");

		column_name_format_.put("RcTg", "%.2f");
		column_name_format_.put("RchTg", "%.2f");
		column_name_format_.put("RchTgt", "%.2f");
		column_name_format_.put("#good", "%.1f");
		column_name_format_.put("#bad", "%.1f");
		column_name_format_.put("#hit", "%.1f");

		column_name_format_.put("Loss", "%.2f");

		
		column_name_format_.put("#aF", "%.1f");
		column_name_format_.put("#F", "%.0f");
		column_name_format_.put("L1", "%.1f");
		column_name_format_.put("IT", "%.0f");

		column_name_format_.put("tCV", "%.1fs");

		column_name_format_.put("mrr", "<b>%.3f<b>");
		column_name_format_.put("MAP", "<b>%.3f<b>");
		column_name_format_.put("p@K", "<b>%.3f<b>");

		column_name_format_.put("#results", "%.1f");
		column_name_format_.put("#R", "%.1f");
		column_name_format_.put("Time", "%.1f");
	}

	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		if (args.length > 0) Param.overwrite(args[0]);

		try {
			(new ReporterPRA()).run(args);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
