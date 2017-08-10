package edu.cmu.pra.fori;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIMapID;
import edu.cmu.lti.algorithm.container.MapIMapIX;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.pra.model.Query;

public class FORIQuery extends Query {

  public MapID target_gradients = new MapID();

  // need to keep this in memory when paths grow
  // the Y-paths use all Y nodes as seeds
  public VecMapID random_walks_ = new VecMapID();
  // the Y-paths use one of the Y nodes as seed
  public MapIMapIX<MapID> target_Yrandom_walks_ = new MapIMapIX<MapID>(MapID.class);

  // a mapping from nodes to their random walks values
  // used for path concatenations
  public MapIMapID node_rwX_ = new MapIMapID();
  public MapIMapID node_RrwY_ = new MapIMapID();

  public MapIMapID node_RrwX_ = new MapIMapID();
  public MapIMapID node_rwY_ = new MapIMapID();

  public MapIMapID node_RrwB_ = new MapIMapID();

  // base feature values in sparse
  // -1 will be shared by all instances
  public VecMapID base_features_ = new VecMapID();


  // signal of features and base features
  // used for inducing new conjunction/base features

  public void clearFeatures() {
    super.clearFeatures();
    random_walks_.clear();
    node_rwX_.clear();
    node_RrwX_.clear();
    node_rwY_.clear();
    node_RrwY_.clear();

    node_RrwB_.clear();

    base_features_.clear();
    // macro_values.clear();
    // micro_values.clear();
  }
}
