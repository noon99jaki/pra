package edu.cmu.pra.fori;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIMapID;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.MapXD;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorB;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.math.FInformation;
import edu.cmu.lti.algorithm.structure.KeepTopK;
import edu.cmu.lti.algorithm.structure.KeepUniqTopK;
import edu.cmu.lti.algorithm.structure.KeepUniqTopKwCost;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.LearnerFORI;
import edu.cmu.pra.LearnerPRA;
import edu.cmu.pra.CTag.ThreadTask;
import edu.cmu.pra.LearnerFORI.FORIThreadPool;
import edu.cmu.pra.fori.Feature.BaseFeature;
import edu.cmu.pra.fori.Feature.RandomWalk;
import edu.cmu.pra.fori.Feature.SourceType;
import edu.cmu.pra.fori.Feature.TargetType;
import edu.cmu.pra.fori.FeatureStat.MapIStat;
import edu.cmu.pra.fori.FeatureStat.NormalizationMode;
import edu.cmu.pra.fori.InductionStats.MapBfStat;
import edu.cmu.pra.graph.Graph;

// a subsystem to evaluation features
public class Induce {

  public static enum ConjunctionMode {
    bfbf, bff
  }
  // we evaluate features by the following metrics
  public static enum FeatureSelectionMode {
    a, // accuracy
    A, // accuracy (macro)
    m, // mutual information
    M, // mutual information (macro)
    g, // gradient (linearized at 0)
    G, // gradient (linearized at y-hat)
    none
  }

  LearnerFORI l;
  FORIModel model_;
  Graph graph_;

  public Induce(LearnerFORI learner) {
    p = new Param();
    l = learner;
    model_ = learner.model_;
    graph_ = learner.graph_;
    this.checkZInductionNodes();
  }

  public Param p;

  public static class Param extends edu.cmu.lti.util.run.Param implements Serializable {
    private static final long serialVersionUID = 2008042701L; // YYYYMMDD

    public Param() {
      super(LearnerPRA.class);
      parse();
    }

    public String code_;
    public boolean concatenate_paths;
    public boolean induce_bf;
    public boolean conjunctions;
    public boolean constant_paths;
    public boolean quantifiers;
    public int min_feature_hit;
    public double min_feature_accuracy;
    public int min_feature_support;

    public boolean induction = false;
    public boolean half_signals = false;
    public boolean one_cat = false;
    public boolean improving_cat = false;
    public boolean semi_improving_cat = false;

    public double gradient_threshold;
    public double signal_threshold;

    public boolean backward_rw = false;
    public boolean X_induction = false;
    public boolean Y_induction = false;
    public FeatureSelectionMode feature_selection_mode;

    public String forbid_z_node_types;

    public NormalizationMode acc_normalize;
    public boolean conditional_acc;
    public double induction_th;
    public int induction_batch_size;
    public boolean dedup;

    public ConjunctionMode conjunction_mode;
    public boolean silent_induction = true;
    public boolean inspect_induction;

    public void parse() {
      inspect_induction = getBoolean("inspect_induction", false);
      silent_induction = getBoolean("silent_induction", true);
      dedup = getBoolean("dedup", true);
      conjunction_mode =
          ConjunctionMode.valueOf(getString("conjunction_mode", ConjunctionMode.bfbf.name()));

      forbid_z_node_types = getString("forbid_z_node_types", null);

      half_signals = getBoolean("half_signals", false);
      gradient_threshold = getDouble("gradient_threshold", 0.01);
      signal_threshold = getDouble("signal_threshold", 0.0);

      induce_bf = getBoolean("induce_bf", false);
      backward_rw = getBoolean("backward_rw", false);
      X_induction = getBoolean("X_induction", false);
      Y_induction = getBoolean("Y_induction", false);
      feature_selection_mode = FeatureSelectionMode.valueOf(
          getString("feature_selection_mode", FeatureSelectionMode.a.name()));

      induction_th = getDouble("induction_th", 0.001);

      concatenate_paths = getBoolean("concatenate_paths", false);
      conjunctions = getBoolean("conjunctions", false);
      constant_paths = getBoolean("constant_paths", false);

      quantifiers = getBoolean("quantifiers", false);
      induction_batch_size = getInt("induction_batch_size", 500);
      one_cat = getBoolean("one_cat", false);
      improving_cat = getBoolean("improving_cat", false);
      semi_improving_cat = getBoolean("semi_improving_cat", false);
      induction = induce_bf || concatenate_paths;

      min_feature_support = getInt("min_feature_support", 2);
      min_feature_hit = getInt("min_feature_hit", 2);
      min_feature_accuracy = getDouble("min_feature_accuracy", 0.01);
      acc_normalize =
          NormalizationMode.valueOf(getString("acc_normalize", NormalizationMode.NQ.name()));
      conditional_acc = getBoolean("conditional_acc", false);
      // max_epoch = getInt("max_epoch", 10);
      // reset_weights = getBoolean("reset_weights", true);

      if (min_feature_support < min_feature_hit) min_feature_support = (int) min_feature_hit;


      code_ += "_" + feature_selection_mode;// + getString("feature_selection_th", "");
      code_ = "_s" + min_feature_support + "_h" + min_feature_hit + "_a"
          + getString("min_feature_accuracy", null) + acc_normalize + (conditional_acc ? "c" : "u");



      if (induction) {

        if (concatenate_paths) {
          code_ += "_C";
        }

        if (induce_bf) {
          if (this.half_signals) code_ += "h";

          if (X_induction) code_ += "X";
          if (Y_induction) code_ += "Y";

          if (quantifiers) code_ += "q";
          if (constant_paths) {
            code_ += "z";
            if (forbid_z_node_types != null) code_ += "-" + forbid_z_node_types;
          }

          if (conjunctions) code_ += "_" + conjunction_mode.name();

          code_ += "_th" + getString("induction_th", null);
          code_ += "_S" + getString("signal_threshold", "");
          code_ += "_" + getString("induction_batch_size", "");
          if (dedup) code_ += "uniq";
        }


        // code_ += "_e" + max_epoch;
        // if(reset_weights) code_ += "r";
        if (backward_rw) code_ += "b";
        if (one_cat) code_ += "o";
        if (improving_cat) code_ += "i";

      }
    }
  }


  public VectorB can_induce_z_paths_ = new VectorB();

  public void checkZInductionNodes() {
    can_induce_z_paths_.reset(graph_.getNumNodes(), true);
    if (p.forbid_z_node_types == null) return;

    SetS forbid_types = new SetS();
    for (String t : p.forbid_z_node_types.split(";"))
      forbid_types.add(t + "$");

    for (String type : forbid_types)
      for (int i = 0; i < graph_.getNumNodes(); ++i)
        if (graph_.getNodeName(i).startsWith(type)) can_induce_z_paths_.set(i, false);
  }

  public FORIQuery mean_query = new FORIQuery();

  public void updateMeanQuery() {// VectorX<FORIQuery> queries_){
    // update average statistics
    // negative_nodes.clear();
    mean_query.clear(model_.p.num_fields);
    for (FORIQuery query : l.queries_) {
      mean_query.good_.addAll(query.good_);
      mean_query.good_.addAll(query.bad_);
      mean_query.seeds_.plusOn(query.seeds_);
    }
    model_.updateFeatures(mean_query, null);
  }



  public double evaluate(FORIQuery query, double qG, double qB, double qV) {
    switch (p.feature_selection_mode) {
      case a:
        return qG / qV;
      case m: {
      double nG = query.good_.size();
      double nV = graph_.getNumNodes();
      // double nS = l.mean_query.good_.size();
      // double nS = nG+ query.bad_.size();
      // double qV= qG+qB;
      return FInformation.mutualInfo(nG, nV, qG, qV);
      // case A: break;
      // case M: break;
      // case G: break;
    }
      case g:
        return qG - qB;
      default:
        FSystem.dieShouldNotHappen();
    }
    return 0.0;
  }

  // , FeatureStat stat ){LearnerFORI l,
  public double evalYX(FORIQuery query, boolean reversed, MapID dist) {
    double qG = dist.sum(query.good_);
    double qB = dist.sum(query.bad_);
    double qV = reversed ? dist.sum() : 1.0;
    return evaluate(query, qG, qB, qV);
    // stat.plusOn(eval);
  }

  // FORIQuery query
  private void evalXYCat(MapIMapID node_rwX, MapIMapID node_rwY, boolean reversed,
      BufferedWriter inspect, MapIX<MapIStat> rwX_rwY_grad) {

    MapIMapID qG = sumOutZ(node_rwX, node_rwY);
    MapIMapID qV = reversed ? sumOutZ(node_rwX, this.mean_query.node_RrwY_) : null;


    for (Map.Entry<Integer, MapID> i : qG.entrySet()) {
      rwX_rwY_grad.getC(i.getKey()).plusOn(i.getValue(), 1.0);
    }

  }

  public void evalXYCat(FORIQuery query, BufferedWriter inspect, InductionStats gradients) {

    // concatenatePaths forward
    if (inspect != null) FFile.writeln(inspect, "cat rwX+RrwY");
    evalXYCat(query.node_rwX_, query.node_RrwY_, false, inspect, gradients.rwX_RrwY_grad);

    // concatenatePaths backward
    if (inspect != null) FFile.writeln(inspect, "cat RrwX+rwY");
    evalXYCat(query.node_RrwX_, query.node_rwY_, true, inspect, gradients.RrwX_rwY_grad);

    // stat.plusOn(eval);
  }

  private MapIMapID sumOutZ(MapIMapID node_rwX, MapIMapID node_rwY) {
    MapIMapID result = new MapIMapID();
    for (Map.Entry<Integer, MapID> i_node : node_rwY.entrySet()) {
      int node = i_node.getKey();
      MapID rwY = i_node.getValue();

      MapID rwX = node_rwX.get(node);
      if (rwX == null) continue;

      for (Map.Entry<Integer, Double> i_X : rwX.entrySet()) {
        double Xn_prob = i_X.getValue();
        int Xrw = i_X.getKey();
        if (Xn_prob < p.signal_threshold) continue; // skip small signals

        result.getC(Xrw).plusOn(rwY, Xn_prob);
      }
    }
    return result;
  }

  // if (inspect != null) {
  // FFile.writeln(inspect, node+"\t" + learner_.graph_.getNodeName(node) );
  // FFile.writeln(inspect, "\t[rwX] " + rwX.join("=", " "));
  // FFile.writeln(inspect, "\t[rwY] " + rwY.join("=", " "));
  // }



  // update potential base feature signals given a random walk
  // average values of base features over Y specifies is the source
  // -3: any node
  // -2: all nodes in objective function good and bad
  // -1: true target nodes (good), which is always stored in query.random_walks_
  // >=0: a particular node
  private void updateBFStats(FORIQuery query, int rw_id, int Y, MapXD<BaseFeature> base_features) {// FeatureValues
                                                                                                   // values

    RandomWalk rw = model_.random_walks_().get(rw_id);
    MapID dist = query.random_walks_.get(rw_id);

    BaseFeature bf = new BaseFeature(TargetType.unknown, rw_id, -1);
    boolean xy_induction = false;
    switch (rw.source_type_) {
      case _X:
      case X: {
      if (p.X_induction) {
        if (xy_induction) {
          Double value = null;
          bf.target_type_ = TargetType.Y;
          if (Y == -3) {
            value = dist.sum();
          } else if (Y == -2) {
            value = dist.sum(query.bad_);// selected_
          } else if (Y == -1) {
            value = dist.sum(query.good_);
          } else {
            value = dist.get(Y);
          }
          base_features.plusOn(bf, value);
        }

        if (p.constant_paths) {
          bf.target_type_ = TargetType.z;
          for (Map.Entry<Integer, Double> it : dist.entrySet()) {
            bf.to_ = it.getKey();
            if (can_induce_z_paths_.get(bf.to_)) base_features.plusOn(bf, it.getValue());
          }
        }

        if (p.quantifiers) {
          bf.target_type_ = TargetType.a;
          base_features.plusOn(bf, dist.sum());

          bf.target_type_ = TargetType.n;
          base_features.plusOn(bf, (dist.size() == 0) ? 1.0 : 0.0);
        }
      }
    }
        break;
      case Y:
      case _Y: // random walk type will be changed to z later
        if (p.Y_induction) {
          if (p.constant_paths) {
            if (Y == -2 || Y == -3) {
              dist = model_.getRandomWalk(query, rw_id, Y);
            } else if (Y == -1) query.random_walks_.get(rw_id);
            // else dist = query.target_Yrandom_walks_.get(Y).get(rw_id);

            bf.target_type_ = TargetType.z;
            for (Map.Entry<Integer, Double> it : dist.entrySet()) {
              bf.to_ = it.getKey();
              if (!can_induce_z_paths_.get(bf.to_)) continue;

              double value = it.getValue();
              if (rw.source_type_.equals(SourceType.Y)) value /= query.good_.size();
              base_features.plusOn(bf, value);
            }
          }
        }
        break;
      default:
        break;
    }
    return;
  }

  // update mean values
  private void updateFStats(FORIQuery query, int f_id, int Y, MapID features) {
    MapID dist = query.features_.get(f_id);
    if (dist.size() == 0) return;

    Feature f = model_.features_().get(f_id);
    if (p.one_cat) if (f.base_features_.size() > 1) return;

    double bias = dist.getD(-1, 0.0);
    if (dist.containsKey(-1)) dist.remove(-1);

    if (Y == -3) { // all
      double mean = bias + dist.average();
      if (mean != 0.0) features.plusOn(f_id, mean);
    } else if (Y == -2) { // selected
      double mean = bias + dist.average(query.bad_);// selected_
      if (mean != 0.0) features.plusOn(f_id, mean);
    } else if (Y == -1) {// good
      double mean = bias + dist.average(query.good_);
      if (mean != 0.0) features.plusOn(f_id, mean);
    } else { // a particular node Y
      double mean = bias + dist.get(Y);
      if (mean != 0.0) features.plusOn(f_id, mean);
    }
    return;
  }

  // post the values of candidate bf, and existing f to values
  private void updateStats(FORIQuery query, int Y, FeatureSetValues values) {
    for (int rw_id = 0; rw_id < model_.random_walks_().size(); ++rw_id)
      updateBFStats(query, rw_id, Y, values.base_features);

    switch (p.conjunction_mode) {
      case bff: {
      for (int f_id = model_.p.bias ? 1 : 0; f_id < model_.features_().size(); ++f_id)
        updateFStats(query, f_id, Y, values.features);
    }
        break;
    }

  }



  // averaged micro mutual information
  protected void induceBFbyMI(FORIQuery query, InductionStats g, BufferedWriter inspect) {

    // if (query.bad_.size()==0) return;

    FeatureSetValues valuesAll = new FeatureSetValues();
    this.updateStats(query, -3, valuesAll);

    FeatureSetValues valuesY = new FeatureSetValues();
    this.updateStats(query, -1, valuesY);

    if (inspect != null) {
      FFile.write(inspect, "\n[values Y]");
      valuesY.print(inspect, model_);

      FFile.write(inspect, "\n[values All]");
      valuesAll.print(inspect, model_);

    }
    switch (p.conjunction_mode) {
      case bff: {
      for (Map.Entry<BaseFeature, Double> j : valuesAll.base_features.entrySet()) {
        double bad = j.getValue();// * query.bad_.size();
        double Y = valuesY.base_features.getD(j.getKey(), 0.0);// *query.good_.size();
        double mi = FInformation.mutualInfo(query.good_.size(), query.bad_.size(), Y, bad, 1.0);
        g.bf_f_grad.getC(j.getKey()).getC(-1).plusOn(mi);
      }
    }
        break;
      case bfbf: {
      for (Map.Entry<BaseFeature, Double> j : valuesAll.base_features.entrySet()) {
        double bad = j.getValue();
        double Y = valuesY.base_features.getD(j.getKey(), 0.0);
        double mi = FInformation.mutualInfo(query.good_.size(), query.bad_.size(), Y, bad, 1.0);
        g.bf_grad.getC(j.getKey()).plusOn(mi);

        if (p.conjunctions) {
          for (Map.Entry<BaseFeature, Double> i : valuesAll.base_features.entrySet()) {
            double bad1 = i.getValue();
            double Y1 = valuesY.base_features.getD(i.getKey(), 0.0);

            double mi1 =
                FInformation.mutualInfo(query.good_.size(), query.bad_.size(), Y, bad, 1.0);
          }
          // g.bf_bf_grad.plusOnOrdered(j.getKey(), valuesY.base_features, Y);
        }
      }
    }
        break;
    }

  }

  protected void induceBFbyAccuracy(FORIQuery query, InductionStats g, BufferedWriter inspect) {
    // if (query.bad_.size()==0) return;

    FeatureSetValues valuesY = new FeatureSetValues();
    this.updateStats(query, -1, valuesY);
    if (inspect != null) {
      FFile.write(inspect, "\n[values Y]");
      valuesY.print(inspect, model_);
    }

    switch (p.conjunction_mode) {
      case bff: {
      for (Map.Entry<BaseFeature, Double> j : valuesY.base_features.entrySet()) {
        double Y = j.getValue();

        MapIStat grads = g.bf_f_grad.getC(j.getKey());
        grads.getC(-1).plusOn(Y);

        if (p.conjunctions) grads.plusOnMin(valuesY.features, Y);
      }
    }
        break;
      case bfbf: {
      for (Map.Entry<BaseFeature, Double> j : valuesY.base_features.entrySet()) {
        double Y = j.getValue();
        g.bf_grad.getC(j.getKey()).plusOn(Y);

        if (p.conjunctions) g.bf_bf_grad.plusOnOrdered(j.getKey(), valuesY.base_features, Y);
      }
    }
        break;
    }

  }

  public void induce(FORIQuery query, InductionStats gradients, BufferedWriter inspect) {
    if (inspect != null) FFile.writeln(inspect, query.print(l.model_.walker_));

    if (p.induce_bf) {
      switch (p.feature_selection_mode) {
        case a: {
        induceBFbyAccuracy(query, gradients, inspect);
      }
          break;
        case m: {
        induceBFbyMI(query, gradients, inspect);
      }
          break;
        //
        // case G:{
        // FeatureValues values = new FeatureValues();
        // this.updateStats(query, -1, values);
        // induceBFbyGradient(values, 1.0, gradients, inspect);
        // } break;
        // case g:{
        // for (int target: query.good_){
        // double gradient = query.target_gradients.get(target);
        // // skip targets with small gradients
        // if (gradient < p.gradient_threshold) continue;
        //
        // FeatureValues values = new FeatureValues();
        // this.updateStats(query, target, values);
        // induceBFbyGradient(values, gradient/query.good_.size(), gradients, inspect);
        // }
        // }
        default:
          break;
      }

    }

    if (p.concatenate_paths) {
      evalXYCat(query, inspect, gradients);
    }
    return;
  }



  private boolean addConcatenation(
      FeatureStat stat, RandomWalk rwX, RandomWalk rwY, BufferedWriter inspect) {

    int rw_id = model_.concatenatePath(rwX, rwY);
    if (rw_id == -1) return false;

    int f_id = model_.addRandomWalkAsFeature(rw_id);
    if (f_id < model_.param_names_.size()) return false;

    // x.getKey(), y.getKey(),
    String name = model_.random_walks_().get(rw_id).getName(model_, true);
    String str = String.format("%d\t%s\trw%d\trw%d\t%s",
        rw_id,
        stat.toString(),
        model_.addRandomWalk(rwX),
        model_.addRandomWalk(rwY),
        name);

    if (inspect != null) FFile.writeln(inspect, str);
    if (!p.silent_induction) System.out.println(str);
    return true;
  }


  // private double getBaseValue(SourceType type, int from, int path,
  // boolean inverse, boolean whole, FeatureGradients gradients) {
  //
  // double base_value = 0.0;
  // rw_.source_type_ = type;
  //
  //
  // rw_.from_ = from;
  //
  // for (FORINode n=model_.getNode(path); n!=null; n=n.parent_) {
  //
  // int path_id = inverse?n.inverse_id_: n.id_;
  // if (path_id==-1)
  // continue;
  //
  // rw_.path_ = path_id;
  //
  // Integer rw_id = model_.addRandomWalk(rw_);
  //
  // SupportHit values = gradients.rwX_rwY_grad.get(rw_id);
  // if (values !=null)
  // base_value= Math.max(base_value, Math.abs(values.hit_));
  // if (whole) break;
  // }
  // return base_value * 1.01;
  // }
  // RandomWalk rwX_ = new RandomWalk();
  // RandomWalk rwY_ = new RandomWalk();
  // //BaseFeature bf_ = new BaseFeature();
  //
  // private double getBaseValue(RandomWalk rwX, RandomWalk rwY, FeatureGradients gradients) {
  //
  // double base_value = 0.0;
  // rwX_.source_type_ = rwX.source_type_;
  // rwX_.from_ = rwX.from_;
  // rwX_.path_=0;
  // int rwX0= model_.addRandomWalk(rwX_);
  //
  // rwY_.source_type_ = rwY.source_type_;
  // rwY_.path_=0;
  // int rwY0= model_.addRandomWalk(rwY_);
  //
  //
  // for (FORINode n=model_.getNode(rwX.path_); n!=null; n=n.parent_) {
  // int path_id = n.id_;
  // if (path_id==-1) continue;
  //
  // rwX_.path_ = path_id;
  // Integer rw_id = model_.addRandomWalk(rwX_);
  //
  // SupportHit values = gradients.getRWGradient(rw_id, rwY0);
  // if (values !=null)
  // base_value= Math.max(base_value, Math.abs(values.hit_));
  // if (p.semi_improving_cat) break;
  // }
  //
  // for (FORINode n=model_.getNode(rwY.path_); n!=null; n=n.parent_) {
  // int path_id = n.id_;
  // if (path_id==-1) continue;
  //
  // rwY_.path_ = path_id;
  // Integer rw_id = model_.addRandomWalk(rwY_);
  //
  // SupportHit values = gradients.getRWGradient(rwX0, rw_id);
  // if (values !=null)
  // base_value= Math.max(base_value, Math.abs(values.hit_));
  // if (p.semi_improving_cat) break;
  // }
  // return base_value * 1.01;
  // }
  //
  // private double getBaseValue(RandomWalk rwX, RandomWalk rwY) {
  //
  // double base_value = 0.0;
  // rwX_.source_type_ = rwX.source_type_;
  // rwX_.from_ = rwX.from_;
  // rwX_.path_=0;
  // int rwX0= model_.addRandomWalk(rwX_);
  //
  // rwY_.source_type_ = rwY.source_type_;
  // rwY_.path_=0;
  // int rwY0= model_.addRandomWalk(rwY_);
  //
  //
  // for (FORINode n=model_.getNode(rwX.path_); n!=null; n=n.parent_) {
  // int path_id = n.id_;
  // if (path_id==-1) continue;
  //
  // rwX_.path_ = path_id;
  // Integer rw_id = model_.addRandomWalk(rwX_);
  //
  // SupportHit values = model_.random_walks_().get(rw_id).stat;
  // //gradients.getRWGradient(rw_id, rwY0);
  // if (values !=null)
  // base_value= Math.max(base_value, Math.abs(values.hit_));
  // if (p.semi_improving_cat) break;
  // }
  //
  // for (FORINode n=model_.getNode(rwY.path_); n!=null; n=n.parent_) {
  // int path_id = n.id_;
  // if (path_id==-1) continue;
  //
  // rwY_.path_ = path_id;
  // Integer rw_id = model_.addRandomWalk(rwY_);
  //
  // SupportHit values = model_.random_walks_().get(rw_id).stat;
  // //SupportHit values = gradients.getRWGradient(rwX0, rw_id);
  // if (values !=null)
  // base_value= Math.max(base_value, Math.abs(values.hit_));
  // if (p.semi_improving_cat) break;
  // }
  // return base_value * 1.01;
  // }


  public static class CandidateCat {
    RandomWalk rwX_;
    RandomWalk rwY_;
    FeatureStat stat_;

    public CandidateCat(RandomWalk rwX, RandomWalk rwY, FeatureStat stat) {
      rwX_ = rwX;
      rwY_ = rwY;
      stat_ = stat;
    }
  }

  // FeatureGradients gradients,
  private void scanCat(MapIX<MapIStat> rwX_rwY_grad, KeepTopK topK) {

    // for (Map.Entry<Integer, MapID> y : gradients.rwY_rwX_grad.entrySet()) {
    for (Map.Entry<Integer, MapIStat> x : rwX_rwY_grad.entrySet()) {
      RandomWalk rwX = model_.random_walks_().get(x.getKey());
      if (rwX.path_ == 0) continue;
      if (p.one_cat) {
        FORINode nodeX = model_.tree_.nodes_.get(rwX.path_);
        if (nodeX.num_steps_ > model_.p.max_steps.get(rwX.from_)) continue;
      }

      int relX = model_.tree_.nodes_.get(rwX.path_).relation_;

      double base_valueY = 0;
      // for (Map.Entry<Integer, Double> x : y.getValue().entrySet()) {
      for (Map.Entry<Integer, FeatureStat> y : x.getValue().entrySet()) {
        RandomWalk rwY = model_.random_walks_().get(y.getKey());

        if (rwY.path_ == 0) continue;

        int relY = model_.tree_.nodes_.get(rwY.path_).relation_;
        relY = model_.walker_.getInversedRelation(relY);
        if (!model_.walker_.canFollow(relX, relY)) continue;

        if (p.one_cat) {
          FORINode nodeY = model_.tree_.nodes_.get(rwY.path_);
          if (nodeY.num_steps_ > model_.p.max_steps.get(model_.p.num_fields)) continue;
        }

        if (y.getValue().hit_ < p.min_feature_hit) continue;
        if (y.getValue().eval_ < p.min_feature_accuracy) continue;

        // if (p.improving_cat) if (abs < getBaseValue(rwX, rwY)) continue;
        int id = topK.addAbsValue(y.getValue().eval_);
        if (id >= 0) topK.objects_.set(id, new CandidateCat(rwX, rwY, y.getValue()));
      }
    }
    return;
  }

  public boolean concatenatePaths(InductionStats gradients, BufferedWriter inspect) {
    int new_cat = 0;
    KeepUniqTopK topK = new KeepUniqTopK(1000);//

    scanCat(gradients.rwX_RrwY_grad, topK);
    scanCat(gradients.RrwX_rwY_grad, topK);

    if (topK.size() == 0) return false;

    if (inspect != null) {
      FFile.writePrint(inspect, String.format("Threshold %.1e for %d candidates, min_acc=%.1e\n",// ,
                                                                                                 // min_th=%.1e
          topK.getThreshold(), topK.size(), p.min_feature_accuracy));
      FFile.writeln(inspect, "\n[new paths]");
    }

    for (Object o : topK.objects_) {
      CandidateCat c = (CandidateCat) o;
      if (addConcatenation(c.stat_, c.rwX_, c.rwY_, inspect)) ++new_cat;
    }

    System.out.println(" added " + new_cat + " concatenations ");

    return new_cat > 0;
  }

  private BaseFeature normalize(BaseFeature bf) {
    RandomWalk rw = model_.random_walks_().get(bf.random_walk_);
    switch (rw.source_type_) {
      case _Y:
      case Y:
        if (bf.target_type_.equals(TargetType.z)) {
          SourceType type = (rw.source_type_ == SourceType.Y) ? SourceType._z : SourceType.z;
          int rw_id = model_.addReversedPath(type, bf.to_, rw.path_);
          bf = new BaseFeature(TargetType.Y, rw_id, -1);
        } else {
          FSystem.dieShouldNotHappen();
        }
        break;
      default:
    }
    // if (rw.source_type_.equals(SourceType._Y)) { }
    return bf;
  }

  VectorS already_exists_ = new VectorS();

  private boolean addConjunction(
      FeatureStat stat, BaseFeature bf, int fid, BufferedWriter inspect) {
    bf = normalize(bf);
    int bf_id = model_.addBaseFeature(bf);

    Feature conj = new Feature(bf_id);

    if (fid != -1) {
      Feature f = model_.features_().get(fid);
      conj.base_features_.addAll(f.base_features_);
    }

    int f_id = model_.addFeature(conj);
    String str = String.format("%d\t%s\t%s", f_id, stat.toString(), conj.getName(model_, true));

    if (f_id < model_.param_names_.size()) {
      already_exists_.add(str);
      return false;
    }

    if (!p.silent_induction) System.out.println(str);
    if (inspect != null) FFile.writeln(inspect, str);
    return true;
  }

  private boolean addConjunction(
      FeatureStat value, BaseFeature bf1, BaseFeature bf2, BufferedWriter inspect) {
    bf1 = normalize(bf1);
    int id1 = model_.addBaseFeature(bf1);
    Feature conj = new Feature(id1);

    if (bf2 != null) {
      bf2 = normalize(bf2);
      int id2 = model_.addBaseFeature(bf2);
      conj.base_features_.add(id2);
    }

    int f_id = model_.addFeature(conj);

    String str = value + "\t" + model_.features_().get(f_id).getName(model_, true);

    if (f_id < model_.param_names_.size()) {
      already_exists_.add(str);
      return false;
    }

    if (!p.silent_induction) System.out.println(str);
    if (inspect != null) FFile.writeln(inspect, str);
    return true;
  }

  public static class CandidateBfF {
    BaseFeature bf_;
    Integer f_;
    FeatureStat stat_;

    public CandidateBfF(BaseFeature bf, Integer f, FeatureStat stat) {
      this.bf_ = bf;
      this.f_ = f;
      this.stat_ = stat;
    }
  }

  public boolean induceBfF(InductionStats gradients, BufferedWriter inspect) {
    int new_conj = 0;
    // KeepTopK topK = new KeepTopK(p.induction_batch_size);
    // KeepUniqTopK topK = new KeepUniqTopK(p.induction_batch_size);
    KeepUniqTopKwCost topK = new KeepUniqTopKwCost(p.induction_batch_size);

    for (Map.Entry<BaseFeature, MapIStat> it : gradients.bf_f_grad.entrySet()) {
      RandomWalk rw = model_.random_walks_().get(it.getKey().random_walk_);
      int len = model_.tree_.nodes_.get(rw.path_).num_steps_;

      for (Map.Entry<Integer, FeatureStat> i : it.getValue().entrySet()) {
        FeatureStat stat = i.getValue();
        if (stat.hit_ < p.min_feature_hit) continue;
        if (stat.eval_ < p.induction_th) continue;

        int id = topK.addValueCost(stat.eval_, (double) len, null);
        if (id >= 0) topK.objects_.set(id, new CandidateBfF(it.getKey(), i.getKey(), stat));
      }
    }
    if (topK.size() == 0) return false;

    if (inspect != null) FFile.writeln(inspect, "\n[new features]");
    if (!p.silent_induction) System.out.println("\n[new features]");

    FFile.writePrint(inspect, String.format("Threshold %.1e for %d candidates, th_g=%.1e\n",
        topK.getThreshold(), topK.size(), p.induction_th));

    for (Object o : topK.objects_) {
      CandidateBfF c = (CandidateBfF) o;
      if (addConjunction(c.stat_, c.bf_, c.f_, inspect)) ++new_conj;
    }
    System.out.println(" added " + new_conj + " conjunctions");

    return new_conj > 0;
  }

  public static class CandidateBfBf {
    BaseFeature bf1_;
    BaseFeature bf2_;
    FeatureStat stat_;

    public CandidateBfBf(BaseFeature bf1, BaseFeature bf2, FeatureStat stat) {
      this.bf1_ = bf1;
      this.bf2_ = bf2;
      this.stat_ = stat;
    }
  }

  public boolean induceBfBf(InductionStats gradients, BufferedWriter inspect) {
    int new_conj = 0;

    KeepTopK topK = p.dedup ? new KeepUniqTopKwCost(p.induction_batch_size)
        : new KeepTopK(p.induction_batch_size);

    for (Map.Entry<BaseFeature, MapBfStat> j : gradients.bf_bf_grad.entrySet()) {
      RandomWalk rw = model_.random_walks_().get(j.getKey().random_walk_);
      int len = model_.tree_.nodes_.get(rw.path_).num_steps_;

      for (Map.Entry<BaseFeature, FeatureStat> i : j.getValue().entrySet()) {
        FeatureStat stat = i.getValue();
        if (stat.hit_ < p.min_feature_hit) continue;
        if (stat.eval_ < p.induction_th) continue;

        if (p.improving_cat) {
          FeatureStat s1 = gradients.bf_grad.get(j.getKey());
          if (s1 != null) if (s1.eval_ >= stat.eval_) continue;
          FeatureStat s2 = gradients.bf_grad.get(i.getKey());
          if (s2 != null) if (s2.eval_ >= stat.eval_) continue;
        }

        int id = topK.addValueCost(stat.eval_, (double) len, null);
        if (id >= 0) topK.objects_.set(id, new CandidateBfBf(j.getKey(), i.getKey(), stat));
      }
    }

    for (Map.Entry<BaseFeature, FeatureStat> j : gradients.bf_grad.entrySet()) {
      RandomWalk rw = model_.random_walks_().get(j.getKey().random_walk_);
      int len = model_.tree_.nodes_.get(rw.path_).num_steps_;

      FeatureStat stat = j.getValue();
      if (stat.hit_ < p.min_feature_hit) continue;
      if (stat.eval_ < p.induction_th) continue;

      int id = topK.addValueCost(stat.eval_, (double) len, null);
      if (id >= 0) topK.objects_.set(id, new CandidateBfBf(j.getKey(), null, stat));
    }

    if (topK.size() == 0) return false;

    if (inspect != null) FFile.writeln(inspect, "\n[new features]");
    if (!p.silent_induction) System.out.println("\n[new features]");

    FFile.writePrint(inspect, String.format("Threshold %.1e for %d candidates,  th_g=%.1e\n",
        topK.getThreshold(), topK.size(), p.induction_th));

    for (Object o : topK.objects_) {
      CandidateBfBf c = (CandidateBfBf) o;
      if (addConjunction(c.stat_, c.bf1_, c.bf2_, inspect)) ++new_conj;
    }
    System.out.println(" added " + new_conj + " conjunctions");

    return new_conj > 0;
  }

  public boolean induce(VectorX<FORIQuery> queries, String code) {

    InductionStats gradients = new InductionStats();

    BufferedWriter inspect =
        (p.inspect_induction) ? inspect = FFile.newWriter(l.out_folder_ + code + ".induction")
            : null;
    FFile.writePrint(inspect, "induction with #queries=" + queries.size() + "\n");

    if (l.p.multi_threaded) {
      l.pool_.runTask(ThreadTask.Induction, queries);

      for (edu.cmu.lti.util.system.ThreadPool.WorkThread i : l.pool_.threads_) {
        FORIThreadPool.WorkThread t = (FORIThreadPool.WorkThread) i;
        gradients.plusOn(t.gradients_);
        t.gradients_.clear();
      }
    } else {
      BufferedWriter inspectQ =
          l.p.per_query_inspection ? FFile.newWriter(l.out_folder_ + code + ".induction.Q") : null;
      for (FORIQuery query : queries)
        induce(query, gradients, inspectQ);
      if (inspectQ != null) FFile.close(inspectQ);
    }

    FFile.appendToFile(gradients.toString() + "\n", l.out_folder_ + l.model_log_file);
    System.out.println(gradients.toString());

    gradients.normalize(p.acc_normalize, queries.size());

    boolean new_features = false;

    if (p.concatenate_paths) if (concatenatePaths(gradients, inspect)) new_features = true;

    // if (p.conjunctions) {
    if (p.induce_bf) {
      switch (p.conjunction_mode) {
        case bff: {
        if (induceBfF(gradients, inspect)) new_features = true;
      }
          break;
        case bfbf: {
        if (induceBfBf(gradients, inspect)) new_features = true;
      }
          break;
      }
    }


    if (inspect != null) {
      FFile.writeln(inspect, "[already_exists_]");
      already_exists_.save(inspect);
    }
    already_exists_.clear();

    if (new_features) model_.indexFeatures();
    if (inspect != null) {
      FFile.writeln(inspect, "\n");
      FFile.writeln(inspect, model_.toString());
      gradients.print(model_, inspect);
      FFile.close(inspect);
    }

    System.out.println(FSystem.memoryUsage());

    return new_features;
  }

}
