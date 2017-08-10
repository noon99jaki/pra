package edu.cmu.lti.nlp.parsing.tree;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorB;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;

public class TreeDep 
	extends TreeParse implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public TreeDep() {
	}	
	public TreeDep(TreeParse t) {
		//super(t);
		root = t.root;
		vNode = t.vNode.clone();
		viNode =(VectorI) t.viNode.clone();
		vWord = (VectorS) t.vWord.clone();
	}
	

		
	protected void doTruncateMoveTextRecur(VectorB vkeep, int id){
		Node n= getNode(id);
		if (n.isLeaf()) return;
		for (int ic: n.vc)
			doTruncateMoveTextRecur(vkeep, ic);
		if (vkeep.sub(n.vc).and() )	return;
		//String s = t.v_word.sub(n.ib, n.ie).join(" ");
		VectorI vc= new VectorI(n.vc);
		vc.insertSortedOn(n.id);
		VectorS vs= vNode.getVS(CTag.text, vc);
		for (int i=0; i< vc.size(); ++i){
			int ic = vc.get(i);
			if (ic == id) continue;
			if (!vkeep.get(ic)) continue;
			vs.set(i,"<>");
		}
		String s =vs.join(" ");
		n.put(CTag.text, s);			
	}
/*	public VectorI doTruncate(VectorB vkeep){
		vkeep.set(root, true);
		//doTruncateMoveTextRecur(vkeep, root);
		return super.doTruncate(vkeep);
	}	*/
	//public void updateIdx(VectorI vi){
	//	super.updateIdx(vi);	}	
	
	protected void toStringPopulateRecur(int id,VectorX< MapII > vmb){
		Node n= getNode(id);
		if (n.isLeaf()) return;
		int level = n.getInt(CTag.level);
		int ib= n.vc.min(); if (id<ib) ib=id;
		int ie= n.vc.max();if (id>ie) ie=id;
		for (int i=ib; i<=ie; ++i){
			vmb.get(i).put(level+1 ,1);
		}
		ie=ie+1;		
		for (int ic: n.vc)	toStringPopulateRecur(ic, vmb);		
	}
	//private static VectorI vp=new VectorI();//mother id
	//private static VectorI vlevel=new VectorI();

	protected String toStringMeat(int level,VectorX< MapII > vmb){	
		StringBuffer sb = new StringBuffer();
		for (int id=0; id<vNode.size(); ++id){
			Node n = getNode(id);
			int lev = n.getInt(CTag.level);
			int ip = n.iparent;
			//sb.append( FString.repeat("\t", level));
			for (int j=0; j<lev; ++j){
				if (vmb.get(id).containsKey(j))
					sb.append("|\t");				
				else
					sb.append("\t");				
			}
			if (id==root) {sb.append( "--");}
			else {
				if (id < ip) {sb.append(   "/-");}
				else {sb.append( "\\-");}
			}
			sb.append(n.toString(level));
			sb.append("\n" );
			//		#printf fo  ("\t" x 5).chr(192);192	L234	F196 -
		}		
		sb.append( "\n"); 		
		return (sb.toString());
	}
	
	//assumes projectivity
	public String toString(){
		return toString(2);
	}
	public String toString(int level){
		StringBuffer sb = new StringBuffer();
		sb.append(vWord.join(" ")).append("\n");
		//sb.append(v_word.joinIndexed("_", " ") ).append("\n");
		//sb.append(v_inode.joinIndexed("_", " ") ).append("\n");

		setLevels();		
		VectorX< MapII > vmb=	new VectorX< MapII >(vNode.size()	, MapII.class);
		vmb.initAll();
		toStringPopulateRecur(root,vmb);		
		sb.append(toStringMeat(level, vmb));
		return (sb.toString());
	}
	/**
	 * reorder tree nodes according to word order
	 * only applys to dependency tree?
	 */
	public VectorI reorder(){
		return super.reorder(viNode.shrinkRepeated());
	}
}
