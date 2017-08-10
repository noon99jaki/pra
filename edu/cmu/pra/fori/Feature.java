package edu.cmu.pra.fori;

// A feature is defined as the conjunction of a set of base features

import edu.cmu.lti.algorithm.Hash;
import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.container.MapXX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.graph.GraphWalker;


// each feature has the format of <feature>&<feature>&...&<feature>
public class Feature implements Comparable<Feature>, ICloneable {
  public SetI base_features_ = new SetI();

  // public VectorI base_features_ = new VectorI();

  // public static final String separator = "~";

  public Feature(Feature x) {
    base_features_.addAll(x.base_features_);
  }

  public Feature clone() {
    return new Feature(this);
  }

  public int compareTo(Feature c) {
    return base_features_.compareTo(c.base_features_);
  }

  public int hashCode() {
    return base_features_.hashCode();
  }

  // Used to compare arrays for content values.
  public boolean equals(Object obj) {
    return base_features_.equals(((Feature) obj).base_features_);
  }

  public Feature() {}

  public Feature(int bf_id) {
    base_features_.add(bf_id);
  }

  public Feature(String name, FORIModel model) {
    for (String base_feature : name.split("&"))
      base_features_.add(model.addBaseFeature(base_feature));
  }

  public String toString() {
    return base_features_.join("&");
  }

  public String getName(FORIModel model) {
    return getName(model, false);
  }

  public String getName(FORIModel model, boolean short_name) {

    StringBuffer sb = new StringBuffer();
    for (int bf : base_features_) {
      if (sb.length() > 0) sb.append("&");
      sb.append(model.base_features_().get(bf).getName(model, short_name));
    }
    return sb.toString();
  }


  public static enum TargetType {

    Y, // (0),
    X, // (1),
    z, // (2),
    n, // (3), // X to nothing
    a, // (4), // X to anything
    unknown;// (5);
    public String toString() {
      return this.name();
    }
    // public int id;
    // TargetType(int id){ this.id = id;}
  }

  public static enum SourceType {
    X, // (0),
    Y, // (1),
    z, // (2),
    _X, // (3),
    _Y, // (4),
    _z, // (5),
    unknown; // (6);
    public String toString() {
      return this.name();
    }
    // public int id;
    // SourceType(int id){ this.id = id;}

  }

  // Define different types of random walks
  // each random walk has the format of
  // <fromType><node_id>:<path>
  public static class RandomWalk implements Comparable<RandomWalk>, ICloneable {

    public FeatureStat stat = new FeatureStat();

    public int parent_ = -1;

    // from_ and to_ are either node ids or field ids
    public SourceType source_type_ = SourceType.unknown;
    public int from_ = -1;
    public int path_ = -1;

    public RandomWalk(SourceType source_type, int from, int path) {
      this.source_type_ = source_type;
      this.from_ = from;
      this.path_ = path;
      if (source_type.equals(SourceType.Y) || source_type.equals(SourceType._Y)) from_ = -1;
    }

    public RandomWalk() {
      this(SourceType.unknown, -1, -1);
    }

    public RandomWalk(RandomWalk rw) {
      this(rw.source_type_, rw.from_, rw.path_);
      this.stat.copy(rw.stat);
      this.parent_ = rw.parent_;
    }

    public RandomWalk clone() {
      return new RandomWalk(this);
    }

    public int hashCode() {
      int hash = path_;
      hash = Hash.incrementalHash(hash, source_type_.hashCode());
      hash = Hash.incrementalHash(hash, from_);
      return hash;
    }

    // Used to compare arrays for content values.
    public boolean equals(Object obj) {
      return this.compareTo((RandomWalk) obj) == 0;
    }

    public int compareTo(RandomWalk c) {

      if (path_ > c.path_) return 1;
      if (path_ < c.path_) return -1;

      int result = source_type_.compareTo(c.source_type_);
      if (result != 0) return result;

      if (from_ > c.from_) return 1;
      if (from_ < c.from_) return -1;

      return 0;
    }

    public RandomWalk(String name, FORIModel model) {
      int p = name.indexOf(GraphWalker.field_separator);
      FSystem.checkTrue(p > 0, "expect random walk in the format <fromType>[id]:<path>");

      this.path_ = model.addPath(name.substring(p + 1));

      if (name.startsWith("_")) {
        String to_str = name.substring(2, p);
        switch (name.charAt(1)) {
          case 'Y':
            source_type_ = SourceType._Y;
            break;
          case 'X':
            source_type_ = SourceType._X;
            from_ = Integer.parseInt(to_str);
            break;
          case 'z':
            source_type_ = SourceType._z;
            from_ = model.graph_.getNodeId(to_str);
            break;
        }
      } else {
        String to_str = name.substring(1, p);
        switch (name.charAt(0)) {
          case 'Y':
            source_type_ = SourceType.Y;
            break;
          case 'X':
            source_type_ = SourceType.X;
            from_ = Integer.parseInt(to_str);
            break;
          case 'z':
            source_type_ = SourceType.z;
            from_ = model.graph_.getNodeId(to_str);
            break;
        }
      }
      return;
    }

    public String toString() {
      return source_type_.name() + from_ + GraphWalker.field_separator + path_;
    }

    public String getName(FORIModel model) {
      return getName(model, false);
    }

    public String getName(FORIModel model, boolean short_name) {
      String path = model.tree_.nodes_.get(this.path_).getName(short_name);

      switch (source_type_) {
        case _X:
        case X:
          return source_type_.name() + from_ + GraphWalker.field_separator + path;
        case _Y:
        case Y:
          return source_type_.name() + GraphWalker.field_separator + path;
        case _z:
        case z:
          return source_type_.name() + model.walker_.getNodeName(from_, short_name)
              + GraphWalker.field_separator + path;
      }
      return null;
    }

    private SetI singletonSeed = null;

    public SetI getSingletonSeed() {
      if (singletonSeed == null) singletonSeed = new SetI(from_);
      return singletonSeed;
    }

  }

  public static class MapBfX<V> extends MapXX<BaseFeature, V> {
    public MapBfX(Class c) {
      super(BaseFeature.class, c);
    }
  }

  // Define different types of base features
  // each base feature has the format of
  // <toType><node_id>:<random walk>

  public static class BaseFeature implements Comparable<BaseFeature>, ICloneable {


    // from_ and to_ are either node ids or field ids
    public TargetType target_type_ = TargetType.unknown;
    public int random_walk_ = -1;
    public int to_ = -1;

    public BaseFeature clone() {
      return new BaseFeature(this);
    }

    public int hashCode() {
      int hash = random_walk_;
      hash = Hash.incrementalHash(hash, target_type_.hashCode());
      hash = Hash.incrementalHash(hash, to_);
      return hash;
    }

    // Used to compare arrays for content values.
    public boolean equals(Object obj) {
      return this.compareTo((BaseFeature) obj) == 0;
    }

    public int compareTo(BaseFeature c) {

      if (random_walk_ > c.random_walk_) return 1;
      if (random_walk_ < c.random_walk_) return -1;

      int result = target_type_.compareTo(c.target_type_);
      if (result != 0) return result;

      // if (!target_type_.equals(TargetType.Y)) {
      if (to_ > c.to_) return 1;
      if (to_ < c.to_) return -1;

      return 0;
    }

    public BaseFeature(BaseFeature x) {
      this(x.target_type_, x.random_walk_, x.to_);
    }

    public BaseFeature(TargetType type, int random_walk, int to) {
      this.target_type_ = type;
      this.random_walk_ = random_walk;
      this.to_ = to;
      if (type.equals(TargetType.Y)) to_ = -1;
    }

    public BaseFeature() {
      this(TargetType.unknown, -1, -1);
    }

    public BaseFeature(String name, FORIModel model) {
      int p = name.indexOf(GraphWalker.field_separator);
      FSystem.checkTrue(
          p > 0, "expect base feature in the format <toType>[node_id]:<fromType>[id]:<path>");

      random_walk_ = model.addRandomWalk(name.substring(p + 1));
      String to_str = name.substring(1, p);

      switch (name.charAt(0)) {
        case 'Y':
          target_type_ = TargetType.Y;
          break;
        case 'X':
          target_type_ = TargetType.X;
          to_ = Integer.parseInt(to_str);
          break;
        case 'z':
          target_type_ = TargetType.z;
          to_ = model.graph_.getNodeId(to_str);
          break;
        case 'n':
          target_type_ = TargetType.n;
          break;
        case 'a':
          target_type_ = TargetType.a;
          break;
      }
    }

    public String toString() {
      return target_type_.name() + to_ + GraphWalker.field_separator + random_walk_;
    }

    public String getName(FORIModel model, boolean short_name) {
      String RW_name = model.random_walks_().get(random_walk_).getName(model, short_name);
      switch (target_type_) {
        case Y:
          return "Y" + GraphWalker.field_separator + RW_name;
        case X:
          return "X" + to_ + GraphWalker.field_separator + RW_name;
        case z:
          return "z" + model.walker_.getNodeName(to_, short_name) + GraphWalker.field_separator
              + RW_name;
        case n:
          return "n" + GraphWalker.field_separator + RW_name;
        case a:
          return "a" + GraphWalker.field_separator + RW_name;
      }
      return null;
    }

  }
}
