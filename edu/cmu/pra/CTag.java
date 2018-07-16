package edu.cmu.pra;

import edu.cmu.pra.graph.IGraph;

public class CTag {
	
	public static final String name = "name";
	public static final String nameS = "nameS";
	public static final String ghirlName = "ghirlName";
	public static final String nOutLink = "nOutLink";
	public static final String size = "size";
	public static final String id = "id";
	public static final String pctLinks = "pctLinks";
	public static final String readings = "readings";
	public static final String nRel = "nRel";
	public static final String rel = "rel";
	public static final String PRA = "PRA";
	public static final String DAssertion = "DAssertion";
	public static final String citeCount = "citeCount";
	public static final String details = "details";
	public static final String inspect = "inspect";
	
	public static final String seed = "seed";
	public static final String top_predictions="top_predictions";

	public static enum RankMode {
		Path, //PathRank
		Rel
		// RelationRank
	}

	public static enum NegMode {
		all // use all negative samples
		, topK // take top K*rNega samples 
		, exp // take 2^x: 1,2,4,8,16,32,...th negative samples
		, expX // take a^x
		, Sqr // take x^2: 0,1,4,9,16,25,...th negative samples
		, Tri // take x^3: 0,1,8,27,64,...th negative samples
		, poly // take x^a
		, Sqr1k, sqr1k, sqr, tri
		, none // do not generate negatives
		// None is a new option to not select any negatives from CWA,
		// and just use known negatives. This is intended to be used
		// with given_negative_samples set to true

	}

	public static enum LossMode {
		none, log, exp, hinge, sqr
	}
	

	public static enum ThreadTask { 
		RandomWalk, 
		EvaluateTrain, 
		EvaluateTest, 
		Predict, 
		CachedPredict, 
		Walk, 
		ExplorePaths, 
		Induction,
		none
	};
	
	public static class Prediction{
		public double score;
		public boolean label;
		public int source;
		public int target;
		public Prediction(double score, boolean label, int source, int target){
			this.score=score;
			this.label=label;
			this.source=source;
			this.target=target;
		}
		public String print(IGraph graph){
			return String.format("%.3f\t%s%s,%s",
					score, label?"*":"", 
					graph.getNodeName(source),
					graph.getNodeName(target));
		}
	}
}
