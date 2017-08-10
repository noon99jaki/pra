package edu.cmu.pra.model;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.TMapIX;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.CTag;
import edu.cmu.pra.graph.GraphWalker;

public class PathNode implements IGetStrByStr {
	public TMapIX<PathNode> relation_children_ = new TMapIX<PathNode>(PathNode.class);
	public boolean is_target_ = false;
	public double accuracy_;
	public int hit_;
	
	public int num_steps_ = -1;
	
	public MapII relation_count_ = new MapII();
	public int relation_ = -1; //which relation leads to this node?
	public PathNode parent_ = null;
	
	public PRAModel model_;
	public PathNode(PRAModel model, PathNode parent, int relation, int num_steps) {
		this.model_ = model;
		this.relation_ = relation;
		this.parent_ = parent;
		this.num_steps_ = num_steps;
		
		if (parent!=null && relation_ ==-1)
			FSystem.die("relation ids are needed for non-root nodes");
	}
	
  // Generate a path name which is a comma separate list of relations
  public String getName() {	//IGraph graph,
  	StringBuffer name = new StringBuffer();
  	getNameRecur(name);
  	return name.toString();
  }
  
  void getNameRecur(StringBuffer name) {	//IGraph graph, 
    if (parent_ == null) return;

    if (parent_.parent_ != null) {
      parent_.getNameRecur(name);
      name.append(GraphWalker.relation_separator);
    }
    name.append(model_.graph_.getEdgeTypeName(relation_));
  }

	public void getPathNodesRecur(VectorX<PathNode> nodes, boolean target_only) {
		if (!target_only || this.is_target_) nodes.add(this);
		for (PathNode child : relation_children_.values())
			child.getPathNodesRecur(nodes, target_only);
	}

	public PathNode extend(int relation) {
		int num_steps = num_steps_ + 1;
		
		if (model_.walker_.no_cost_relations_.contains(relation)) --num_steps;
		
		return new PathNode(model_, this, relation,	num_steps);
	}

	public String getString(String key) {
//		if (key.equals(CTag.nameS)) return short_name_;
		if (key.equals(CTag.name)) return getName();		
		return null;
	}

	public String toString() {
		return getName();
	}

}