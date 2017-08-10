package edu.cmu.pra.fori;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.MapIMapID;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.fori.FeatureStat.PathStat;
import edu.cmu.pra.graph.GraphWalker;

// This is a structure more flexible than PathTree
// it caches random walk results at node level rather than at tree level
// This means that random walk don't need to be done in one depth first search
// which is useful when the tree grows incrementally during training
//
// this structure is agnostic to the indices of paths (don't have access to path ids)
//
public class FORITree implements Serializable {
  private static final long serialVersionUID = 2008042701L; // YYYYMMDD

  // public static int num_explored_features;
  public FORIModel model_;
  public FORINode root_ = null;

  public VectorX<FORINode> nodes_ = new VectorX<FORINode>(FORINode.class); // nodes of target type


  public FORITree(FORIModel model) {
    this.model_ = model;
    clear();
  }

  public void clear() {
    nodes_.clear();
    root_ = new FORINode(0, model_, null, -1, 0);
    root_.inverse_id_ = 0;

    nodes_.add(root_);
  }

  public FORINode addInversedNode(FORINode node) {
    FORINode result = root_;
    for (FORINode p = node; p.parent_ != null; p = p.parent_) {
      int relation = model_.walker_.getInversedRelation(p.relation_);
      if (relation == -1) return null;
      result = this.addNode(result, relation, false);
    }
    node.inverse_id_ = result.id_;
    result.inverse_id_ = node.id_;
    return result;
  }

  public FORINode addNode(FORINode parent, int relation) {
    return addNode(parent, relation, true);
  }

  public FORINode addNode(FORINode parent, int relation, boolean with_inverse) {
    FORINode child = parent.relation_children_.get(relation);
    if (child == null) {
      child = parent.extend(relation);
      parent.relation_children_.put(relation, child);
      child.id_ = nodes_.size();
      nodes_.add(child);
      if (with_inverse) addInversedNode(child);
    }
    return child;
  }

  public int addPath(String path) {
    FORINode node = root_;
    if (path.length() > 0) for (String name : path.split(GraphWalker.relation_separator)) {
      int relation = model_.graph_.getEdgeType(name);
      FSystem.checkTrue(relation != -1, "unknown edge type=" + name);
      node = addNode(node, relation);
    }
    return node.id_;
  }


  public int addPath(VectorI path) {
    FORINode node = root_;
    if (path.size() > 0) for (int relation : path)
      node = addNode(node, relation);
    return node.id_;
  }

  public FORINode addReversedPath(int path) {
    return addReversedPath(root_, path);
  }

  public FORINode addReversedPath(FORINode from, int path) {
    FORINode node = from;
    FORINode p = nodes_.get(path);
    while (p != root_) {
      int relation = this.model_.walker_.getInversedRelation(p.relation_);
      node = addNode(node, relation);
      p = p.parent_;
    }

    return node;
  }

  public FORINode concatenatePath(int Ypath, int Xpath) {
    // System.out.println("concatenate " + nodes_.get(Xpath) + " and " + nodes_.get(Ypath));
    return addReversedPath(nodes_.get(Xpath), Ypath);
  }



  // double getProbReachTarget(MapID dist0, FORIQuery query) { //SetI good) {
  // double all = dist0.sum();
  // if (all == 0.0) return 0.0;
  // double Y = dist0.sum(query.good_);
  // return Y/all;
  // }

  public String printLengthDistribution() {
    MapII counts = new MapII();
    for (FORINode n : this.nodes_)
      counts.plusOn(n.num_steps_);
    return counts.join("=", " ");
  }

  public String toString() {
    return "#node=" + this.nodes_.size();
  }
}
