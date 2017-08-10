package edu.cmu.pra;

import java.util.HashSet;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.model.ModelPathRank;
import edu.cmu.pra.model.PRAModel;
import edu.cmu.pra.model.Query;

public class Tests {


	public static void testPRA(String graph_folder, String model_file, String query_line) {
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		GraphWalker walker = new GraphWalker(graph);
		PRAModel model = new ModelPathRank(walker);
		model.loadModel(model_file);
		
		Query query = model.walker_.parseQuery(query_line, ",");
		MapID result = model.predict(query);
		System.out.println(model.walker_.printDistribution(result, 10, "\t", "\n"));
		
	}

	// Test intersectedRWR
	// seeds_str is a comma separated list of seed nodes
	public static void testRWR(String graph_folder, String seeds_str, String num_steps_str) {
		int num_steps = Integer.parseInt(num_steps_str);
		
		Graph graph = new Graph();
		graph.loadGraph(graph_folder);
		
		GraphWalker walker = new GraphWalker(graph);
		SetI seeds = walker.getNodeIds(seeds_str.split(" "));
		
		HashSet<Integer> constraint_set = new HashSet<Integer>();
		for (int i =0; i < graph.node_index_.size(); ++i)	constraint_set.add(i);
		
		for (int steps = 1; steps <= num_steps; ++ steps) {
			StopWatch watch = new StopWatch();
			MapID result = walker.walkRWR(null, seeds, constraint_set, steps );
			
			System.out.println("\nsteps=" + steps + " time used=" + watch.getSec() + " sec");
			System.out.println(walker.printDistribution(result, 10, "\t", "\n"));
		}
	}
	
	public static void testDualPRA(String graph_folder, String model_file, String query_line) {

		Graph graph = new Graph();
		graph.loadIndics(graph_folder);
		GraphWalker walker = new GraphWalker(graph);
		
		Param.overwrite("dual_mode=true");
		PRAModel model = new ModelPathRank(walker);
		model.loadModel(model_file);
		
		graph.loadGraph(graph_folder);
		Query query = model.walker_.parseQuery(query_line, ",");
		
		StringBuffer reasons = new StringBuffer();
		model.classify(query, reasons);
		System.out.println(reasons.toString());
		return;
	}
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		String task = args[0];
		if (task.equals("testPRA")) testPRA(args[1], args[2], args[3]);
		else if (task.equals("testRWR")) testRWR(args[1], args[2], args[3]);
		else if (task.equals("testDualPRA")) testDualPRA(args[1], args[2], args[3]);
		else FSystem.die("unknown task=" + task);
	}
}
