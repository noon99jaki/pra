package edu.cmu.pra.model;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapIMapID;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.CTag;
import edu.cmu.pra.fori.FeatureStat;
import edu.cmu.pra.fori.FeatureStat.PathStat;
import edu.cmu.pra.graph.GraphWalker;

// * a PathTree is a set of type paths starting from some entity type 
// * @author nlao
public class PathTree implements Serializable {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public static int num_explored_features;
	public PRAModel model_;
	public PathNode root_ = null;

	public VectorX<PathNode> nodes_ = new VectorX<PathNode>(PathNode.class); //nodes of target type
	public VectorS path_names_ = new VectorS();

	//following reversed links to produce pack propagation
	public boolean bReversedPath = false;
	
	public int field_;
	public int max_step_;
	public PathTree(PRAModel model, int field, int max_step) {//, int field
		this.field_ = field;
		this.max_step_ = max_step;
		this.model_ = model;
		root_ = new PathNode(model, null, -1, 0);
	}
	
	public void clear() {
		root_ = new PathNode(model_, null, -1, 0);
		nodes_.clear();
	}

	public VectorX<PathNode> getPathNodes() {
		VectorX<PathNode> vN = new VectorX<PathNode>(PathNode.class);
		root_.getPathNodesRecur(vN, false);
		return vN;
	}

	private static int nMinSupport = -1;

	public void indexPaths() {
		nodes_.clear();
		root_.getPathNodesRecur(nodes_, true);
		path_names_ = nodes_.getVS(CTag.name);
	}

	public PathNode addPath(String path) {
		PathNode node = root_;
		if (path.length() >0 )
		for (String name : path.split(GraphWalker.relation_separator)) {
			int relation = this.model_.graph_.getEdgeType(name);
			FSystem.checkTrue(relation!=-1, "unknown edge type=" +name);

			if (!node.relation_children_.containsKey(relation)) 
				node.relation_children_.put(relation, node.extend(relation));

			node = node.relation_children_.get(relation);
		}
		return node;
	}
	
	public PathNode addPath(VectorX<Integer> relations) {
		PathNode node = root_;
		for (Integer relation : relations) {

			PathNode child = node.relation_children_.get(relation);
			if (child == null) { 
				child = node.extend(relation);
				node.relation_children_.put(relation, child);
			}
			node = child;
		}
		return node;
	}

	double GetProbReachTarget(MapID dist0, Query query) { //SetI good) {
	  double num_reach_target = 0.0;
	  
	  MapID dist = new MapID();
	  dist.putAll(dist0);
//	  model_.walker_.applyFilters(query, dist);
	  
	  double sum = dist.sum();
	  if (sum > 0.0) {
	    for (int i: query.good_)  num_reach_target += dist.getD(i, 0.0);
      num_reach_target/=sum;
	  }
	  return num_reach_target;
	}

	
	private SetI poolRelations(MapIMapID distributions) {
		SetI relations = new SetI();
		for (Map.Entry<Integer, MapID> it: distributions.entrySet()) {
			for (Map.Entry<Integer, Double> e : it.getValue().entrySet())
				relations.addAll(model_.graph_.getNodeOutlinkTypes(e.getKey()));
		}
		return relations;
	}


	private MapID poolRelationsDist(MapID distribution) {
		MapID rel_weight = new MapID();
		for (Map.Entry<Integer, Double> it : distribution.entrySet())
			rel_weight.plusOn(model_.graph_.getNodeOutlinkTypes(it.getKey()), it.getValue());
		return rel_weight;
	}
	
	private SetI poolRelations(MapID distribution) {
		SetI relations = new SetI();
		for (Map.Entry<Integer, Double> it : distribution.entrySet())
			relations.addAll(model_.graph_.getNodeOutlinkTypes(it.getKey()));
		return relations;
	}
	
	public void explorePaths(Query query,  PathStat path_stat) {//int field_,
		MapID dist = new MapID(query.seeds_.get(field_), 1.0);
		VectorI path= new VectorI();
		path.add(field_);
		
		Double particle=model_.p.max_num_exploration_particles<1? null:
			(double) model_.p.max_num_exploration_particles;

		explorePathsRecur(query, path_stat, path, dist, particle, max_step_);
		return;
	}
	
	private void explorePathsRecur(Query query, 
			PathStat path_stat, VectorI path, MapID dist0, Double particle0, 
			int credit0){
		int relation0 =path.size() >1? path.lastElement() : -1;
		
		FeatureStat stat = path_stat.getC(path);
		double prob = 	GetProbReachTarget(dist0, query);
		++stat.support_;
		if (prob!=0.0){
			stat.hit_ ++;
			stat.eval_ += prob;
		}
		
		if (credit0 == 0) return;
		
		SetI relations = poolRelations(dist0);
		//MapID rel_weight = poolRelations(dist0);

		Double particle =particle0==null? null: particle0/ relations.size();
		if (particle!=null)	if (particle < 1.0) {
			int i = relations.sample();
			relations.clear();
			relations.add(i);
			particle = particle0;
		}
//		double particle =particle0/ relations.size();
//		if (particle < 1.0) {
//			int i = relations.sample();
//			relations.clear();
//			relations.add(i);
//			particle = particle0;
//		}
		
		for (int relation : relations) {
			//if (prob < 1.0)  if (!FRand.drawBoolean(prob)) continue;
			if (!model_.walker_.canFollow(relation0, relation)) continue;

			MapID dist = new MapID();
      model_.walker_.step(query, dist0, 1.0, relation, false, dist);
      if (dist.size() ==0) continue;

      path.add(relation);
      
      int credit =  credit0;
      if (!model_.walker_.no_cost_relations_.contains(relation)) --credit;
      
      explorePathsRecur(query, path_stat,  path,  dist,  particle,  credit);
      path.pop();
		}

		return;
	}

	// Cache data
//	public MapMapIIX<VecMapID> seed_time_data_ = new MapMapIIX<VecMapID>(VecMapID.class);
	
	// Cache data
	public MapIX<VecMapID> seed_cache_ = new MapIX<VecMapID>(VecMapID.class);

	//do PCRW starting from seeds, add the results to data matrix A
	public VecMapID walk(Query query, SetI seeds, BufferedWriter inspect) {
		//int field_, 
		VecMapID features = null;
		
		if (model_.p.cache_RW)
		if (seeds.size() == 1) {// only cache for single seed queries
			int k = seeds.first();
			synchronized (seed_cache_) {
				features = seed_cache_.get(k);
			}
			if (features != null) return features;
		}

		features = new VecMapID();
		
		walkRecur(query, field_, root_, new MapID(seeds, 1.0), features, inspect);
	//	walkRecur(query, field_, root_, new MapID(seeds, 1.0 / seeds.size()), features, inspect);

		if (model_.walker_.p.RW_renormalize) 
			for (MapID m : features) m.normalizeOn();

		if (model_.p.cache_RW)
		if (seeds.size() == 1) {// only cache for single seed queries
			int k = seeds.first();
			synchronized (seed_cache_) {
				seed_cache_.put(k, features);
			}
		}
		return features;
	}
	
	// a simplified interface for PCRW
	public VecMapID walk(Query query, int seed) {//, int field_
		return walk(query, new SetI(seed),  null);
	}
	public static final MapID empty_distribution = new MapID();

	// use a dual tree, but do backward rw from the source side
	public VecMapID walkBackward(Query query, SetI seeds, BufferedWriter inspect) {
		VecMapID features =  new VecMapID();
		
		for (PathNode node : this.nodes_){
			
			MapID dist = new MapID(seeds, 1.0 / seeds.size());
			for  (PathNode p = node;  p != this.root_; p=p.parent_) {
				MapID dist1 = new MapID();
				int relation= model_.walker_.getInversedRelation(p.relation_);
				if (relation==-1) {
					dist= empty_distribution;
					break;
				}
				model_.walker_.step(query,dist, 1.0, relation, true, dist1);
				dist = dist1;
			}
			features.add(dist);
			
	    if (inspect != null  && dist.size() > 0) {
    		FFile.writeln(inspect, String.format("%d:%s(%d)\t%s",
    				field_,  node.getName(), dist.size(),	
    				model_.walker_.printDistribution(dist, 5, "=", " ")));
	    }
		}
		return features;
	}
	
  // Do random walk recursively for all the paths beyond this node.
  // Any target node will append a distribution dist to the feature set features
	private void walkRecur(Query query, int field_, PathNode node, MapID dist, 
			VecMapID features, BufferedWriter inspect) {
		if (node.is_target_) 	features.add(dist);

		for (Map.Entry<Integer, PathNode> e : node.relation_children_.entrySet()) {
			int relation = e.getKey();
			PathNode child = e.getValue();
			MapID dist_child = new MapID();
			
			
			model_.walker_.step(query, dist, 
					model_.walker_.p.dampening, 	relation, false, dist_child);
			
//			if (child.is_target_) 	features.add(dist_child);

	    if (inspect != null  && dist_child.size() > 0) {
	    	synchronized (inspect) {
	    		FFile.writeln(inspect, String.format("%s%d:%s(%d)\t%s",
	    				child.is_target_? "*": "",
	    				field_,  child.getName(), dist_child.size(),	
	    				model_.walker_.printDistribution(dist_child, 5, "=", " ")));
	    	}
	    }
			walkRecur(query, field_, child, dist_child, features, inspect);
//		  model_.walker_.applyFilters(query, dist_child);
		}
	}

	public String toString() {
		return "PathTree with #node=" + this.nodes_.size();
	}


}