package edu.cmu.lti.nlp.parsing.tree;

import java.util.Collection;
import java.util.List;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;

public class VectorToken extends VectorX<Token> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VectorToken(){
		super(Token.class);
	}
	public VectorToken(VectorS vTxt){
		super(Token.class);
		this.ensureCapacity(vTxt.size());
		for (String txt:vTxt)
			this.add(new Token(txt));
	}
	public VectorToken(VectorS vTxt, VectorS vPos){
		super(Token.class);
		if (vTxt.size() != vPos.size()){
			System.err.println("txt and pos length differ");
			return;
		}
		this.ensureCapacity(vTxt.size());
		for (int i=0; i<vTxt.size() ; ++i){			
			this.add(new Token(vTxt.get(i), vPos.get(i)));
		}
	}
	public VectorToken newInstance(){
		return new VectorToken();
	}
	public VectorToken(Collection<Token> v){
		this();
		this.addAll(v);
	}
	public VectorToken(List<String> v){
		this();
		for (String txt: v)
			this.add(new Token(txt));
	}
}
