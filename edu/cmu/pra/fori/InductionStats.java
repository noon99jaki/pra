package edu.cmu.pra.fori;

import java.io.BufferedWriter;
import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.MapXD;
import edu.cmu.lti.algorithm.container.MapXX;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.pra.CTag;
import edu.cmu.pra.fori.Feature.BaseFeature;
import edu.cmu.pra.fori.Feature.MapBfX;
import edu.cmu.pra.fori.FeatureStat.MapIStat;
import edu.cmu.pra.fori.FeatureStat.NormalizationMode;

// The gradients of candidate features
// need to keep track of which "candidates" are already in the model?
public class InductionStats implements IPlusObjOn {
  public int num_errors = 0;
  // gradient (or succ probability) of concatenating random walk X and Y
  public MapIX<MapIStat> rwX_RrwY_grad = new MapIX<MapIStat>(MapIStat.class);
  public MapIX<MapIStat> RrwX_rwY_grad = new MapIX<MapIStat>(MapIStat.class);

  // gradient of multiplying a base feature with an existing feature
  // to save memory we combine bf_grad and bf_f_grad here
  public MapXX<BaseFeature, MapIStat> bf_f_grad =
      new MapXX<BaseFeature, MapIStat>(BaseFeature.class, MapIStat.class);

  public MapBfStat bf_grad = new MapBfStat();
  public MapBfBfStat bf_bf_grad = new MapBfBfStat();

  public static class MapBfBfStat extends MapBfX<MapBfStat> {
    public MapBfBfStat() {
      super(MapBfStat.class);
    }

    public MapBfBfStat plusOnOrdered(BaseFeature key, MapXD<BaseFeature> values, double weight) {
      getC(key).plusOnOrdered(key, values, weight);
      return this;
    }
  }

  public void normalize(NormalizationMode mode, int num_query) {
    for (MapIStat x : rwX_RrwY_grad.values())
      x.normalize(mode, num_query);
    for (MapIStat x : RrwX_rwY_grad.values())
      x.normalize(mode, num_query);
    for (MapIStat x : bf_f_grad.values())
      x.normalize(mode, num_query);
    bf_grad.normalize(mode, num_query);
    for (MapBfStat x : bf_bf_grad.values())
      x.normalize(mode, num_query);
  }

  public static class MapBfStat extends MapBfX<FeatureStat> {
    public MapBfStat() {
      super(FeatureStat.class);
    }

    public MapBfStat plusOn(BaseFeature bf, double weight) {
      getC(bf).plusOn(weight);
      return this;
    }

    public MapBfStat plusOn(MapXD<BaseFeature> values, double weight) {
      for (Map.Entry<BaseFeature, Double> i : values.entrySet())
        plusOn(i.getKey(), weight * i.getValue());
      return this;
    }

    public MapBfStat plusOnOrdered(BaseFeature key, MapXD<BaseFeature> values, double weight) {
      for (Map.Entry<BaseFeature, Double> i : values.entrySet())
        if (key.compareTo(i.getKey()) == -1) plusOn(i.getKey(), weight * i.getValue());
      return this;
    }

    public double getHit(BaseFeature bf) {
      FeatureStat stat = this.get(bf);
      if (stat == null) return 0.0;
      return stat.hit_;
    }

    public void normalize(NormalizationMode mode, int num_query) {
      for (FeatureStat i : values())
        i.normalize(mode, num_query);
    }
  }


  public String toString() {
    return "#errors=" + num_errors
        + " #rwX_RrwY=" + rwX_RrwY_grad.sum(CTag.size)
        + " #RrwX_rwY=" + RrwX_rwY_grad.sum(CTag.size)
        + " #bf_f=" + bf_f_grad.sum(CTag.size)
        + " #bf=" + bf_grad.size() + " #bf_bf=" + bf_bf_grad.sum(CTag.size);
  }

  public void clear() {
    num_errors = 0;
    rwX_RrwY_grad.clear();
    RrwX_rwY_grad.clear();
    bf_f_grad.clear();
    bf_grad.clear();
    bf_bf_grad.clear();
  }

  public InductionStats plusObjOn(Object x) {
    if (x == null) return this;
    return plusOn((InductionStats) x);
  }

  public InductionStats plusOn(InductionStats x) {
    rwX_RrwY_grad.plusObjOn(x.rwX_RrwY_grad);
    RrwX_rwY_grad.plusObjOn(x.RrwX_rwY_grad);
    bf_f_grad.plusObjOn(x.bf_f_grad);
    bf_bf_grad.plusObjOn(x.bf_bf_grad);
    bf_grad.plusObjOn(x.bf_grad);
    num_errors += x.num_errors;
    return this;
  }

  public void print(FORIModel model, BufferedWriter inspect) {
    FFile.writeln(inspect, "\n" + toString());

    double th_abs = 0.01;
    FFile.writeln(inspect, "\nrwX_RrwY_grad[" + rwX_RrwY_grad.sum(CTag.size) + "]");
    FFile.writeln(inspect, rwX_RrwY_grad.join("\t", "\n"));

    FFile.writeln(inspect, "\nRrwX_rwY_grad[" + RrwX_rwY_grad.sum(CTag.size) + "]");
    FFile.writeln(inspect, RrwX_rwY_grad.join("\t", "\n"));

    FFile.writeln(inspect, "\nbf_f_grad[" + bf_f_grad.sum(CTag.size) + "]");
    // int num_bf_grad=0;
    for (Map.Entry<BaseFeature, MapIStat> j : bf_f_grad.entrySet()) {
      String j_name = j.getKey().getName(model, true);

      for (Map.Entry<Integer, FeatureStat> i : j.getValue().entrySet()) {
        FeatureStat value = i.getValue();
        if (Math.abs(value.eval_) < th_abs) continue;
        if (i.getKey() == -1) {
          FFile.writeln(inspect, value + "\t" + j_name);
        }
        else {
          String i_name = model.features_().get(i.getKey()).getName(model, true);
          FFile.writeln(inspect, value + "\t" + j_name + "\t" + i_name);
        }
      }
    }

    FFile.writeln(inspect, "\nbf_grad [" + bf_grad.size() + "]");
    for (Map.Entry<BaseFeature, FeatureStat> i : bf_grad.entrySet()) {
      FeatureStat value = i.getValue();
      if (Math.abs(value.eval_) < th_abs) continue;

      FFile.writeln(inspect, value + "\t" + i.getKey().getName(model, true));
    }

    FFile.writeln(inspect, "\nbf_bf_grad[" + bf_bf_grad.sum(CTag.size) + "]");
    for (Map.Entry<BaseFeature, MapBfStat> j : bf_bf_grad.entrySet()) {
      String j_name = j.getKey().getName(model, true);

      for (Map.Entry<BaseFeature, FeatureStat> i : j.getValue().entrySet()) {
        FeatureStat value = i.getValue();
        if (Math.abs(value.eval_) < th_abs) continue;
        String i_name = i.getKey().getName(model, true);
        FFile.writeln(inspect, value + "\t" + j_name + "\t" + i_name);
      }
    }
  }
}
