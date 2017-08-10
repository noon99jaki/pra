package edu.cmu.lti.nlp.sent;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeDep;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Node;
/**
 * @author nlao
 * put any sentence related analysis here
 * 
 */
public class SentAnalysis implements Serializable, IGetStrByStr {
	private static final long serialVersionUID = 2008042701L; 

	public String getString(String name){
		if (name.equals("sent")) return sent;
		return null;
	}


	//text representation of this sentence
	public String sent;
	public SentAnalysis(String sent){
		this.sent = sent;
	}
	//to store any possible annotatio we put on a sentence
	public Token tFeature= new Token();

	//segmented and tagged sentence
	public VectorToken v_token = null;//new TVector<Token>(Token.class);

	//there are the pars trees of this sentence
	public TreeSyntax treeSyx=null;// = new TVector<TreeParse>(TreeParse.class);
	//public TreeDep treeDep=null;
	//public TreeSyntax treeSyxPhrase=null;
	public TreeDep treeDepPhrase=null;
	
	//pointers to nodes in parse tree (DepPhrase?)
	public VectorI vi_entity = new VectorI();
	
	//TODO: move it into tPatterns?
	//public int i_QEntity;	//id of the entity in question
	
/*	public void from(SentAnalysis sa){
		this.sent = sa.sent;
		this.v_token = sa.v_token;
		this.tPatterns = sa.tPatterns;
		this.treeSyx = sa.treeSyx;
		this.treeDep = sa.treeDep;
		this.treeSyxPhrase = sa.treeSyxPhrase;
		this.treeDepPhrase = sa.treeDepPhrase;
		this.vi_entity = sa.vi_entity;
		this.i_QEntity = sa.i_QEntity;
	}*/
	
	/**assuming subject is the first NP before root*/
	public int findSubj(){
		if (vi_entity.size()==0) return -1;
		int id = vi_entity.get(0);
		int ih = treeSyx.getNode(id).getHead();
		int irh = treeSyx.getRoot().getHead();
		if ( ih	> irh) 
			return -1;
		//if (id >this.treeDepPhrase.root)	return -1;
		return id;
	}
	
	public VectorX<Object> getEntityToken(){
		return treeSyx.vNode.getVO("token",vi_entity);
	}
	public VectorX<Token> getVToken(int id){
		Node n = treeSyx.getNode(id);
		return v_token.sub(n.ib,n.ie);
	}
	public VectorX<Object> getEntitieVToken(){
		VectorX<Object> vvt = null;//new TVector<Object>(Object.class);
		for (int id: vi_entity )
			vvt.add(getVToken(id));
		return vvt;
	}
	public String toString() {
		StringBuilder sb = new StringBuilder();
		//if (this.treeSyx!=null) 	sb.append(treeSyx);
		//sb.append(v_token.join("\n")+"\n");
		sb.append(this.sent).append("\n");
		if (treeDepPhrase!=null) 	{
			sb.append(treeDepPhrase);
			sb.append("\nEntities: "+vi_entity+"\n");		
			VectorX<Object> vt=getEntityToken();
			sb.append(vt.joinIndexed(")","\n")+"\n");
			/*		for (int id: vi_entity){
			Node n = treeDepPhrase.getNode(id);
			sb.append("E"+id+" "+n.t.toString(6)+"\n");
			}*/
			//sb.append("QEntity: "+i_QEntity	+ "("+ vi_entity.get(i_QEntity)+ ")\n");				
		}
		sb.append("\nPatterns:\n"+ tFeature.print()+"\n");			
		//sb.append("\nPatterns:\n"+ tFeature.print()+"\n");			

		return sb.toString();
	}
	public String print() {
		StringBuilder sb = new StringBuilder();
		//if (question != null) {		sb.append("Question = " + question + "\n");		}
		sb.append("terms:\n" + v_token.join("\n")+"\n");
		if (treeSyx!=null) 	sb.append(treeSyx);
		//if (treeSyxPhrase!=null) 	sb.append(treeSyxPhrase);
		//if (treeDep!=null) 	sb.append(treeDep);
		sb.append(this.toString());
		return sb.toString();

	}	

}
