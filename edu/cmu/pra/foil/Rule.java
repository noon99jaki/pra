package edu.cmu.pra.foil;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.CTag;
import edu.cmu.pra.foil.FOIL.RuleLine;
import edu.cmu.pra.graph.Graph;
import edu.cmu.pra.graph.GraphWalker;
import edu.cmu.pra.graph.IGraph;

public class Rule {

	IGraph g;
	GraphWalker walker_;

	Node nTarget = null;
	static MapID mNull = new MapID();

	public static class Node implements IGetStrByStr {
		@Override public String getString(String tag) {
			if (tag.equals(CTag.rel)) return this.rel;
			return null;
		}

		public int r = -1;

		public String rel;
		public String target;
		public SetI secM = new SetI();
		public String clause;
		public VectorX<Node> vChildren = new VectorX<Node>(Node.class);
		public Node parent = null;
		public IGraph g = null;
		public boolean bGrounded;//=false;
		public int idxGrounded = -1;

		public Node(Node parent, String rel, String target, IGraph g) {// String clause
			// int idxGrounded){//Relation r,
			this.parent = parent;
			this.rel = rel;
			this.target = target;
			//this.r=r;
			//this.idxGrounded=idxGrounded;
			if (parent != null) parent.vChildren.add(this);
			//			this.clause=clause;
			bGrounded = !target.startsWith("?");
			this.g = g;
			if (g != null) {
				if (rel != null) {
					r = g.getEdgeType(rel);
					if (r == -1) FSystem.die("unknown relation=" + rel);
				}
				if (bGrounded) idxGrounded = g.getNodeId(target);

			}
		}

		//public Node extend(String rel,String target, String clause){
		//return new Node(this, rel,target, clause);		}
		//public Node extend(String rel){		return extend(rel,null);		}

		public String toString() {
			return "(" + rel + ")" + target;
		}

		public String toStringRecur(int nTab) {
			StringBuffer sb = new StringBuffer();
			Integer size = secM != null ? secM.size() : null;
			sb.append(String.format("%s%s(%d)\n", FString.repeat(" ", nTab),
					toString(), size));

			for (Node c : vChildren) {
				sb.append(c.toStringRecur(nTab + 1));
			}
			return sb.toString();
		}

		/*public Node extend(Relation r,int idxGrounded){
			return new Node(this, r,idxGrounded);
		}
		public Node extend(Relation r){
			return extend(r,-1);
		}*/
	}

	public String relation;
	public int nP; //positive
	public int nN; //negative
	public int nU; //unknown
	public double accuracy;
	public String txt;
	public boolean bGrounded = false;
	public Node root;
	public MapSX<Node> msNode = new MapSX<Node>(Node.class);
	public VectorX<Node> vNode = new VectorX<Node>(Node.class);

	public String getPath() {
		return "c(" + vNode.getVS(CTag.rel).join(")c(", 1) + ")c";
	}

	public Rule(RuleLine r, IGraph g) {
		//this.txt=form;
		this.g = g;
		parseRule(r);
	}

	public Rule(RuleLine r) {
		this(r, null);
	}

	public String toString() {
		return relation + "-->" + target + " (" + accuracy + ")\n"
				+ root.toStringRecur(0);
	}

	public String target = null;

	private void parseClause(String[] clause, int i) {
		if (clause.length != 3) FSystem.die("expect clause to have 3 parts="
				+ clause);
		String rel = clause[0];
		String arg1 = clause[1];
		String arg2 = clause[2];

//		if (RelationLine.mRelInv.containsKey(rel)) {
//			rel = "_" + RelationLine.mRelInv.get(rel);
//		}

		if (i == 1) {
			this.relation = rel;
			root = new Node(null, null, arg1, g);
			vNode.add(root);
			msNode.put(arg1, root);
			target = arg2;
			return;
		}

		//Relation r=g.

		if (!arg1.startsWith("?")) FSystem
				.die("expect 1st argument to be ungrounded=" + arg1);

		Node p = msNode.get(arg1);
		if (p == null) FSystem.die("cannot find node=" + arg1);

		Node c = msNode.get(arg2);
		if (c != null) FSystem.die("expect the rule to have tree structure="
				+ clause);

		c = new Node(p, rel, arg2, g);
		if (arg2.startsWith("?")) msNode.put(arg2, c);
		else bGrounded = true;

		vNode.add(c);
	}

	public void parseRule(RuleLine r) {//String form){
		vNode.clear();
		msNode.clear();

		String vs[] = r.stats.split(" ");
		if (vs.length != 4) FSystem.die("mal-formated rule=" + txt);
		accuracy = Double.parseDouble(vs[0]);
		nP = Integer.parseInt(vs[1]);
		nN = Integer.parseInt(vs[2]);
		nU = Integer.parseInt(vs[3]);

		this.txt = r.txt;
		int i = 0;
		for (String[] clause : r.vvsClause)
			parseClause(clause, ++i);

		if (target.startsWith("?")) nTarget = this.msNode.get(target);
		else nTarget = newGroundedNode(target);
		return;
	}

	public Node newGroundedNode(String name) {
		Node n = new Node(null, null, name, null);
		Integer idx = g != null ? g.getNodeId(name) : null;
		n.secM.add(idx);
		return n;
	}

	public boolean matchRecur(Node n) {
		if (n.secM.size() == 0) return false;

		if (n.bGrounded) if (!n.secM.contains(n.idxGrounded)) return false;

		for (Node c : n.vChildren) {
			c.secM.clear();
			if (c.r == -1) FSystem.die("relation=null");
			walker_.stepCoverage(null, n.secM, c.r, c.secM);
			if (!matchRecur(c)) return false;
		}
		return true;
	}

	public MapID match(int iEnt) {
		root.secM.clear();
		root.secM.add(iEnt);
		//System.out.println(this.txt);

		if (!matchRecur(root)) return mNull;

		MapID m = new MapID();
		m.plusOn(nTarget.secM, this.accuracy);

		//System.out.println(g.getNodeName(iEnt)
		//+"-->"+FString.join( g.getNodeName(nTarget.secM), ", "));
		return m;
	}
}
