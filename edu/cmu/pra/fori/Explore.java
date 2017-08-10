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
import edu.cmu.lti.util.run.Counter;
import edu.cmu.lti.util.run.StopWatch;
import edu.cmu.lti.util.system.ThreadPool.WorkThread;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.LearnerFORI;
import edu.cmu.pra.LearnerPRA;
import edu.cmu.pra.CTag.ThreadTask;
import edu.cmu.pra.LearnerFORI.FORIThreadPool;
import edu.cmu.pra.fori.Feature.RandomWalk;
import edu.cmu.pra.fori.FeatureStat.PathStat;
import edu.cmu.pra.graph.GraphWalker;

// sub system for path exploration
public class Explore {

  LearnerFORI l;

  public Explore(LearnerFORI learner) {
    l = learner;
    p = new Param();
  }

  public Param p;

  public static class Param extends edu.cmu.lti.util.run.Param implements Serializable {
    private static final long serialVersionUID = 2008042701L; // YYYYMMDD

    public Param() {
      super(LearnerPRA.class);
      parse();
    }

    public int max_num_exploration_queries;
    public int max_num_exploration_particles;
    public VectorI max_steps = new VectorI();
    public String max_steps_str;
    public String no_cost_relations;
    public String no_repeat_relations;

    public String code_;

    public void parse() {
      max_num_exploration_queries = getInt("max_num_exploration_queries", -1);;
      max_num_exploration_particles = getInt("max_num_exploration_particles", -1);;

      max_steps_str = getString("max_steps", "3");
      max_steps = VectorI.from(FString.splitVS(max_steps_str, "-"));

      no_cost_relations = getString("no_cost_relations", null);
      no_repeat_relations = getString("no_repeat_relations", null);

      code_ = "M" + max_steps_str;
      if (max_num_exploration_particles != -1)
        code_ += String.format("_p%.0e", (double) max_num_exploration_particles);

      if (no_cost_relations != null) code_ += "_" + no_cost_relations;


    }
  }

  private MapII poolRelations(MapIMapID distributions, boolean reversed) {
    MapII relation_counts = new MapII();
    for (Map.Entry<Integer, MapID> query_dist : distributions.entrySet()) {
      for (int node : query_dist.getValue().keySet()) {
        Set<Integer> relations = l.model_.graph_.getNodeOutlinkTypes(node);
        if (!reversed) {
          relation_counts.plusOn(relations);
        } else {
          for (int relation : relations) {
            Integer inverse = l.model_.walker_.getInversedRelation(relation);
            if (inverse == null) continue;
            relation_counts.plusOn(relation);
          }
        }
      }
    }
    return relation_counts;
  }

  private SetI poolRelations(MapID distribution) {
    SetI relations = new SetI();
    for (Map.Entry<Integer, Double> it : distribution.entrySet())
      relations.addAll(l.model_.graph_.getNodeOutlinkTypes(it.getKey()));
    return relations;
  }

  public void exploreAQueryFieldRecur(FORIQuery query,
      int credit0,
      MapID dist0,
      Double particle0,
      boolean reversed,
      VectorI path,
      BufferedWriter inspect,
      PathStat path_stat) {

    if (inspect != null) {
      StringBuffer sb = new StringBuffer();
      sb.append(path.firstElement().toString());
      for (int i = 1; i < path.size(); ++i)
        sb.append(GraphWalker.relation_separator)
            .append(l.model_.graph_.getEdgeTypeName(path.get(i)));
      FFile.writeln(inspect, sb.toString());
    }

    int relation0 = path.size() > 1 ? path.lastElement() : -1;

    path_stat.getC(path).plusOn(l.induce_.evalYX(query, reversed, dist0));

    if (credit0 == 0) return;

    SetI relations = poolRelations(dist0);
    if (relations.size() == 0) return;

    Double particle = particle0 == null ? null : particle0 / relations.size();
    if (particle != null) if (particle < 1.0) {
      int i = relations.sample();
      relations.clear();
      relations.add(i);
      particle = particle0;
    }

    for (int relation : relations) {
      // if (particle < 1.0) if (!FRand.drawBoolean(particle)) continue;
      if (!l.model_.walker_.canFollow(relation0, relation, reversed)) continue;

      MapID dist = new MapID();
      l.model_.walker_.step(query, dist0, 1.0, relation, reversed, dist);
      if (dist.size() == 0) continue;

      path.add(relation);

      int credit = credit0 - 1;
      if (l.model_.walker_.no_cost_relations_.contains(relation)) ++credit;

      exploreAQueryFieldRecur(query, credit, dist, particle, reversed, path, inspect, path_stat);
      path.pop();
    }

    return;
  }

  private void exploreAQueryField(
      FORIQuery query, int field, boolean reversed, BufferedWriter inspect, PathStat path_stat) {
    int max_step = p.max_steps.get(field);

    // query id --> distribution
    MapID dist = field == l.model_.p.num_fields ? new MapID(query.good_, 1.0) : // Y paths
        new MapID(query.seeds_.get(field), 1.0); // X paths


    if (reversed) field = -field - 1;
    VectorI path = new VectorI();
    path.add(field);

    Double particle =
        p.max_num_exploration_particles < 1 ? null : (double) p.max_num_exploration_particles;

    exploreAQueryFieldRecur(query, max_step, dist, particle, reversed, path, inspect, path_stat);
    return;
  }


  // each path is a integer vector or a string?
  public void exploreAQuery(FORIQuery query, BufferedWriter inspect, PathStat path_stat) {

    if (inspect != null) FFile.writeln(inspect, query.print(l.model_.walker_));

    Counter.count50.stepDot();
    for (int field = 0; field < l.model_.p.num_fields; ++field) {
      exploreAQueryField(query, field, false, inspect, path_stat);
      if (l.induce_.p.backward_rw) exploreAQueryField(query, field, true, inspect, path_stat);
    }

    if (l.induce_.p.induction) {
      if (l.induce_.p.backward_rw)
        exploreAQueryField(query, l.model_.p.num_fields, false, inspect, path_stat);
      exploreAQueryField(query, l.model_.p.num_fields, true, inspect, path_stat);
    }
    // for (PathTree tree : trees_) tree.explorePaths(query, path_stat);
  }


  public double exploreQueries(VectorX<FORIQuery> queries, String code) {
    // VectorX<FORIQuery> selected_queries_,
    System.out.println("explorePaths()");
    StopWatch watch = new StopWatch();
    l.model_.clear();

    PathStat path_stat = new PathStat();
    if (l.p.multi_threaded) {
      for (WorkThread thread : l.pool_.threads_)
        ((FORIThreadPool.WorkThread) thread).path_stat_.clear();

      l.pool_.runTask(ThreadTask.ExplorePaths, queries);

      for (WorkThread thread : l.pool_.threads_)
        path_stat.plusOn(((FORIThreadPool.WorkThread) thread).path_stat_);
    } else {
      BufferedWriter inspect =
          (l.p.per_query_inspection) ? FFile.newWriter(l.out_folder_ + code + ".explore.Q") : null;

      for (FORIQuery query : queries)
        exploreAQuery(query, inspect, path_stat);

      if (inspect != null) FFile.close(inspect);
    }

    path_stat.normalize(l.induce_.p.acc_normalize, queries.size());


    System.out.println("#ExpPath=" + path_stat.size());
    createModel(path_stat, l.out_folder_ + code + ".explore");
    // p.min_feature_support, p.min_feature_hit, p.min_feature_accuracy,
    path_stat.clear();
    l.model_.saveModel(l.out_folder_ + code + ".initial_model", false);
    watch.printTime("exploration");
    return watch.getSec();
  }

  // int min_support, int min_hit, double min_acc,
  public void createModel(PathStat path_stat, String out_file) {
    BufferedWriter writer = FFile.newWriter(out_file);

    for (Map.Entry<VectorI, FeatureStat> it : path_stat.entrySet()) {
      VectorI relations = it.getKey();
      FeatureStat stat = it.getValue();
      if (stat.support_ < l.induce_.p.min_feature_support) continue;

      if (!l.induce_.p.induction) // no need for less accurate paths
        if (stat.hit_ < l.induce_.p.min_feature_hit
            || stat.eval_ < l.induce_.p.min_feature_accuracy) continue;

      int path = l.model_.tree_.addPath(relations.sub(1));
      int rw_id = l.model_.addRandomWalk(relations.get(0), path);
      RandomWalk rw = l.model_.random_walks_().get(rw_id);
      rw.stat.copy(stat);
      FFile.writeln(writer, stat.print() + "\t" + rw.getName(l.model_));

      if (stat.hit_ < l.induce_.p.min_feature_hit) continue;
      if (stat.eval_ < l.induce_.p.min_feature_accuracy) continue;

      switch (rw.source_type_) {
        case X:
        case _X:
          l.model_.addRandomWalkAsFeature(rw_id);
      }
    }
    l.model_.indexFeatures();
    FFile.close(writer);
    return;
  }

}
