package edu.cmu.pra.graph;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIMapID;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.pra.graph.AGraph.VirtualMicroRelation;
import edu.cmu.pra.graph.GraphCreator.Format;

public class VirWeightedLinks implements VirtualMicroRelation, Serializable {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	Graph graph_;
	int relation_;
	//weighted links 
	public VectorX<MapIMapID> nodes_  = new VectorX<MapIMapID>(MapIMapID.class);

	public void initialize(Graph graph, int relation, String fn) {

		this.graph_ = graph;
		this.relation_ = relation;
	}

	public boolean load(Format form, String ent, String dist) {
		MapIMapID node = new MapIMapID();
		nodes_.add(node);


		for (String s2 : dist.split(" ")) {
			if (s2.length() == 0) continue;
			int i = s2.indexOf(':');
			if (i < 0) {
				System.err.println("Malformated micro virtual link line=" + dist);
				continue;
			}
			String n2 = s2.substring(0, i);
			double d = Double.parseDouble(s2.substring(i + 1));

			int e2 = graph_.tryAddNode(form.type2_, n2);

			node.getC(form.relation_).put(e2, d);
		}
		return true;
	}

	@Override public MapID getOutlinks(int iEnt) {//Entity e){//
		return nodes_.get(iEnt).get(relation_);
	}

}
