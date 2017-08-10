package edu.cmu.pra.fori;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.MapXX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.pra.CTag;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.model.Query;

public class FORINode implements IGetStrByStr {
  public MapIX<FORINode> relation_children_ = new MapIX<FORINode>(FORINode.class);
  public int id_ = -1;
  public int inverse_id_ = -1;
  public int num_steps_ = -1;
  public int relation_ = -1; // which relation leads to this node?
  public FORINode parent_ = null;
  public FORIModel model_;

  public FORINode(int id, FORIModel model, FORINode parent, int relation, int num_steps) {
    this.id_ = id;
    this.model_ = model;
    this.relation_ = relation;
    this.parent_ = parent;
    this.num_steps_ = num_steps;
  }

  public String toString() {
    return id_ + "\t" + getName() + "\tinv=" + inverse_id_;
  }

  // public MapXX<SetI,MapID> cache_ = new MapXX<SetI,MapID>(SetI.class, MapID.class);
  // public MapXX<SetI,MapID> cache_reversed_ = new MapXX<SetI,MapID>(SetI.class, MapID.class);

  // do PCRW starting from seeds, add the results to data matrix A
  public MapID walk(Query query, SetI seeds, boolean reversed, boolean cache) {

    MapID result = null;

    // if (cache) { // some how it is not working. Taking a lot of memory and never stops
    // if (reversed) {
    // synchronized (cache_reversed_) {
    // result = cache_reversed_.get(seeds);
    // }
    // }
    // else {
    // synchronized (cache_) {
    // result = cache_.get(seeds);
    // }
    // }
    // if (result != null) return result;
    // }

    if (parent_ == null) {
      result = seeds.size() == 0 ? FORIModel.empty_distribution : new MapID(seeds, 1.0);
      // new MapID(seeds, 1.0 / seeds.size());
    } else {
      MapID from = parent_.walk(query, seeds, reversed, cache);
      result = new MapID();
      if (from.size() > 0) model_.walker_.step(query, from, 1.0, relation_, reversed, result);
      if (model_.walker_.p.RW_renormalize) result.normalizeOn();
    }

    // if (cache){
    // if (reversed) {
    // synchronized (cache_reversed_) {
    // cache_reversed_.put(seeds, result);
    // }
    // }
    // else {
    // synchronized (cache_) {
    // cache_.put(seeds, result);
    // }
    // }
    // }
    return result;
  }

  public String getName() {
    return getName(false);
  }

  // Generate a path name which is a comma separate list of relations
  public String getName(boolean short_name) { // IGraph graph,
    StringBuffer name = new StringBuffer();
    getNameRecur(name, short_name);
    return name.toString();
  }

  void getNameRecur(StringBuffer name, boolean short_name) { // IGraph graph,
    if (parent_ == null) return;

    if (parent_.parent_ != null) {
      parent_.getNameRecur(name, short_name);
      name.append(GraphWalker.relation_separator);
    }
    name.append(model_.graph_.getEdgeTypeName(relation_));
  }

  public FORINode extend(int relation) {
    int num_steps = num_steps_ + 1;

    if (model_.walker_.no_cost_relations_.contains(relation)) --num_steps;

    return new FORINode(-1, model_, this, relation, num_steps);
  }

  public String getString(String key) {
    // if (key.equals(CTag.nameS)) return short_name_;
    if (key.equals(CTag.name)) return getName();
    return null;
  }
}
