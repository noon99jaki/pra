package edu.cmu.pra.graph;

import java.io.BufferedWriter;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.container.MapVecII;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.file.FFile;

public class Graph extends AGraph {
  public VectorX<MapVecII> nodes_ = new VectorX<MapVecII>(MapVecII.class);

  public Graph() {}

  public int getNumNodes() {
    return this.nodes_.size();
  }

  protected void loadNodes(String graph_folder) {
    if (nodes_.size() == 0)
      nodes_ = (VectorX<MapVecII>) FFile.loadObject(graph_folder + node_file_);
  }

  public Set<Integer> getNodeOutlinkTypes(int node) {
    return this.nodes_.get(node).keySet();
  }

  public void saveNodes(String node_file) {
    FFile.saveObject(nodes_, node_file);
  }

  public int addNode() {
    nodes_.add(new MapVecII());
    return nodes_.size() - 1;
  }

  public void addEdge(int rel, int e1, int e2) {
    nodes_.get(e1).getC(rel).add(e2);
  }

  public void edgeDedupe() {
    for (MapVecII map : nodes_)
      for (Map.Entry<Integer, VectorI> it : map.entrySet())
        it.setValue(it.getValue().toSet().toVector());
  }


  // public int num_edges_ = -1;
  public VectorI getEdges(int node, int relation) {
    return nodes_.get(node).get(relation);
  }


  public VectorI getEdgeCounts() {
    VectorI counts = new VectorI();
    counts.reset(this.edgeType_index_.size());
    for (MapVecII node : this.nodes_) {
      for (Map.Entry<Integer, VectorI> it : node.entrySet())
        counts.plusOn(it.getKey(), it.getValue().size());
    }
    return counts;
  }

  public void saveEdges(String edge_file) {
    BufferedWriter writer = FFile.newWriter(edge_file);
    for (int id = 0; id < this.node_index_.size(); ++id) {
      String src = this.getNodeName(id);
      for (Map.Entry<Integer, VectorI> it : nodes_.get(id).entrySet()) {
        String relation = this.getEdgeTypeName(it.getKey());
        for (int i : it.getValue()) {
          String tgt = this.getNodeName(i);
          FFile.writeln(writer, relation + "\t" + src + "\t" + tgt);
        }
      }
    }
    FFile.close(writer);
    return;
  }


  public boolean save2CompactGhirlFormat(SetS msTextRel, String folder) {
    // boolean bSortLinks = true;
    //
    // if (bSortLinks) {
    // folder += ".sortLink";
    // }
    // FFile.mkdirs(folder);
    // // Katie wants nodes to be sorted..
    // // vEnt.getVS(CTag.ghirlName).save(folder+"/graphNode.pct");
    // // vEnt.enuString(CTag.pctLinks).save(folder+"/graphRow.pct",null);
    //
    // VectorS vL = edgeType_index_.list_;
    // VectorI idxL = vL.sortId();
    // if (bSortLinks) {
    // vL.sub(idxL).save(folder + "/graphLink.pct");
    // for (int i = 0; i < idxL.size(); ++i)
    // FSystem.dieNotImplemented();
    // //edgeType_index_.vs.get(idxL.get(i)).id=i; //overwrite the relation id
    // } else vL.save(folder + "/graphLink.pct");
    //
    // // int totLink=vEnt.sumI(CTag.nOutLink);
    // // System.out.println(nLinks+" vs. "+ totLink);
    //
    // FFile.saveString(folder + "/graphSize.pct", edgeType_index_.size() + " "
    // + nodes_.size());
    //
    // VectorS vNames = nodes_.getVS(CTag.ghirlName);
    // VectorI idxN = vNames.sortId();
    // vNames.sub(idxN).save(folder + "/graphNode.pct");
    //
    // VectorI vNodeReIdx = idxN.reversIdx();
    // BufferedWriter bw = FFile.newWriter(folder + "/graphRow.pct");
    // for (TypedNode e : nodes_) {
    // FFile.writeln(bw, e.printPCTLinks(this, vNodeReIdx));
    // }
    // //vEnt.enuString(CTag.pctLinks).save(folder+"/graphRow.pct",null);

    return true;
  }

}
