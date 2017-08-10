package edu.cmu.pra.fori;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapXD;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.pra.fori.Feature.BaseFeature;


// The mean values of (base) features
public class FeatureSetValues implements IPlusObjOn, Serializable {
  private static final long serialVersionUID = 2008042701L;

  // public VectorD feature_means = new VectorD();
  public MapID features = new MapID();

  public MapXD<BaseFeature> base_features = new MapXD<BaseFeature>(BaseFeature.class);

  public String toString() {
    return "#feature=" + features.size() + " #base_feature=" + base_features.size();
  }

  public void print(BufferedWriter writer, FORIModel model) {
    if (features.size() > 0) {
      FFile.writeln(writer, "\n[FeatureStats]");
      // for (int i=0; i<model.features_().size(); ++i)
      for (Map.Entry<Integer, Double> it : features.entrySet()) {
        int i = it.getKey();
        double value = it.getValue();
        FFile.writeln(writer, String.format(
            "f%d\t%.3f\t%s", i, value, model.features_().get(i).getName(model, true)));
      }
    }
    if (base_features.size() > 0) {
      FFile.writeln(writer, "\n[BFStats]");
      for (Map.Entry<BaseFeature, Double> it : base_features.entrySet())
        FFile.writeln(
            writer, String.format("%.3f\t%s", it.getValue(), it.getKey().getName(model, true)));
    }
  }

  public void clear() {
    features.clear();
    base_features.clear();
  }

  public FeatureSetValues plusObjOn(Object x) {
    if (x == null) return this;
    return plusOn((FeatureSetValues) x);
  }

  public FeatureSetValues plusOn(FeatureSetValues x) {
    features.plusObjOn(x.features);
    base_features.plusObjOn(x.base_features);
    return this;
  }

  public FeatureSetValues multiplyOn(Double x) {
    features.multiplyOn(x);
    base_features.multiplyOn(x);
    return this;
  }

  // remove signals with small magnitude
  // only look for positive features
  public FeatureSetValues getSignals2(FeatureSetValues means, double signal_threshold) {
    FeatureSetValues sig = new FeatureSetValues();

    for (Map.Entry<Integer, Double> it : features.entrySet()) {
      double signal = it.getValue();
      Double mean = means.features.get(it.getKey());
      if (mean != null) signal -= mean;

      if (Math.abs(signal) <= signal_threshold) continue;
      sig.features.put(it.getKey(), signal);
    }

    for (Map.Entry<BaseFeature, Double> it : base_features.entrySet()) {
      double signal = it.getValue();

      Double mean = means.base_features.get(it.getKey());
      if (mean != null) signal -= mean;

      if (Math.abs(signal) <= signal_threshold) continue;
      sig.base_features.put(it.getKey(), signal);
    }

    return sig;
  }

  // remove signals with small magnitude
  // look for both positive and negative features
  public FeatureSetValues getSignals(FeatureSetValues means, double signal_threshold) {
    FeatureSetValues sig = new FeatureSetValues();

    for (Map.Entry<Integer, Double> it : means.features.entrySet()) {
      double mean = it.getValue();
      double signal = features.getD(it.getKey(), 0.0) - mean;

      if (Math.abs(signal) < signal_threshold) continue;
      sig.features.put(it.getKey(), signal);
    }

    for (Map.Entry<BaseFeature, Double> it : means.base_features.entrySet()) {
      double mean = it.getValue();
      double signal = base_features.getD(it.getKey(), 0.0) - mean;

      if (Math.abs(signal) < signal_threshold) continue;
      sig.base_features.put(it.getKey(), signal);
    }
    return sig;
  }
}
