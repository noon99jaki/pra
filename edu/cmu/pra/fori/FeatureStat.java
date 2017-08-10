package edu.cmu.pra.fori;

import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.MapXX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;

public class FeatureStat implements IPlusObjOn {


  public static enum NormalizationMode {
    SUP, // support
    HIT, // hit
    NQ, // number of queries
    none
  }

  public int support_ = 0;
  public int hit_ = 0;
  public double eval_ = 0.0; // it can be accuracy, mutual infor, or gradient

  public FeatureStat() {

  }

  public void normalize(NormalizationMode mode, int num_query) {
    switch (mode) {
      case HIT:
        eval_ /= hit_;
        break;
      case SUP:
        eval_ /= support_;
        break;
      case NQ:
        eval_ /= num_query;
        break;
    }
  }

  public String print() {
    return String.format("%d\t%d\t%.3f", support_, hit_, eval_);
  }

  public void parseLine(VectorS row) {
    support_ = Integer.parseInt(row.get(0));
    hit_ = Integer.parseInt(row.get(1));
    eval_ = Double.parseDouble(row.get(2));
  }

  public void copy(FeatureStat x) {
    this.support_ = x.support_;
    this.hit_ = x.hit_;
    this.eval_ = x.eval_;
  }

  public FeatureStat plusOn(double eval) {
    ++support_;
    if (eval != 0.0) {
      ++hit_;
      eval_ += eval;
    }
    return this;
  }

  public String toString() {
    return String.format("%.2f[%d]", eval_, hit_);
  }

  public FeatureStat plusObjOn(Object m) {
    FeatureStat x = (FeatureStat) m;
    support_ += x.support_;
    hit_ += x.hit_;
    eval_ += x.eval_;
    return this;
  }

  public static class PathStat extends MapXX<VectorI, FeatureStat> {
    public PathStat() {
      super(VectorI.class, FeatureStat.class);
    }

    public void normalize(NormalizationMode mode, int num_query) {
      for (FeatureStat i : values())
        i.normalize(mode, num_query);
    }
  }

  public static class MapIStat extends MapIX<FeatureStat> {
    public MapIStat() {
      super(FeatureStat.class);
    }

    public MapIStat plusOn(int id, double weight) {
      getC(id).plusOn(weight);
      return this;
    }

    public MapIStat plusOn(MapID values, double weight) {
      for (Map.Entry<Integer, Double> i : values.entrySet())
        plusOn(i.getKey(), weight * i.getValue());
      return this;
    }

    public MapIStat plusOnMin(MapID values, double weight) {
      for (Map.Entry<Integer, Double> i : values.entrySet())
        plusOn(i.getKey(), Math.min(weight, i.getValue()));
      return this;
    }

    public double getHit(int id) {
      FeatureStat stat = this.get(id);
      if (stat == null) return 0.0;
      return stat.hit_;
    }

    public void normalize(NormalizationMode mode, int num_query) {
      for (FeatureStat i : values())
        i.normalize(mode, num_query);
    }
  }
}
