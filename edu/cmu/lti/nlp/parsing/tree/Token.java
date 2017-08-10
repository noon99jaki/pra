package edu.cmu.lti.nlp.parsing.tree;

import java.io.Serializable;

import edu.cmu.lti.algorithm.Interfaces.ICloneable;
import edu.cmu.lti.algorithm.Interfaces.IGetDblByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.minorthird.classify.ClassLabel;
import edu.cmu.minorthird.classify.Example;

/**
 *
 */
public class Token 
	implements Serializable, Cloneable, IPlusObjOn
		, IGetStrByStr,	IGetIntByStr, IGetDblByStr , ICloneable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Object plusObjOn(Object x){
		if (x == null) return this;
		Token e = (Token) x;
		this.m.addAll(e.m);
		this.mi.plusOn(e.mi);
		this.md.plusOn(e.md);
		this.ms.addOn(e.ms);
		return this;
	}
	
	
	public Token addOn(Token t) {
		ms.addOn(t.ms);
		md.addOn(t.md);
		mi.addOn(t.mi);
		m.addOn(t.m);
		return this;
	}
	@Override
	public Token clone() {//throws CloneNotSupportedException {
		//return super.clone();	}
		//public Token copy() {
		try{
			Token t   = (Token)super.clone();
			//Token t = new Token();
			t.ms = (MapSS) ms.clone();
			t.md = (MapSD) md.clone();
			t.mi = (MapSI) mi.clone();
			t.m = (SetS) m.copy();
			return t;			
		} catch ( Exception e ) {
			e.printStackTrace();
			System.exit(-1);
			return null;
		}
	}
	// all possible features
	public MapSS ms = new MapSS();
	public MapSD md = new MapSD();
	public MapSI mi = new MapSI();
	public SetS m = new SetS();
	/**
	 * Previously, a field called "List&lt;Token&gt; aliases" stored
	 * translations -- but I found it problematic. So, I created
	 * another field "List&lt;Token&gt; translations"  -Hideki 2008.04.09
	 */
	//instead of making Token too complex, I add v_keyTermMT
	//in QAResult -Ni 2008.6.4
	//public List<Token> translations = new ArrayList<Token>();

	public Token() {}

	public Token(String text) {
		ms.put(CTag.text, text);
	}
	public Token(String text, String pos) {
		if (text!= null)
			ms.put(CTag.text, text);
		if (pos!= null)
			ms.put(CTag.pos, pos);
	}
	public Double getDouble(String name) {
		return md.get(name);
	}

	public Integer getInt(String name) {
		return mi.get(name);
	}

	public String getString(String name) {
		return ms.get(name);
	}

	public String toString() {
		return toString(2);
	}
	public String print(int level) {
		StringBuffer sb = new StringBuffer();
		if (level >= 1) sb.append("ms=" + ms.join("=", ", ") +"\n");
//		if (level >= 2) sb.append("m=" + m.join(", ")+"\n");
		if (level >= 2) sb.append("m=\n" + m.join("\n")+"\n");
		if (level >= 3) sb.append("mi=" + mi.join("=",", ")+"\n");
		if (level >= 4) sb.append("md=" + md.join("=",", ")+"\n");
		return (sb.toString());
	}
	public String print() {
		StringBuffer sb = new StringBuffer();
		if (ms.size()>0) sb.append("ms=" + ms.join("=", ", ") +"\n");
		if (m.size()>0) sb.append("m=" + m.join(", ")+"\n");
//		if (m.size()>0) sb.append("m=\n" + m.join("\n")+"\n");
		if (mi.size()>0) sb.append("mi=" + mi.join("=",", ")+"\n");
		if (md.size()>0) sb.append("md=" + md.join("=",", ")+"\n");
		return (sb.toString());
	}
	
	public String toString(int level) {
		StringBuffer sb = new StringBuffer();
		if (level >= 1) sb.append(ms.toString());
		if (level >= 2) sb.append("/" + m.toString());
		if (level >= 3) sb.append("/" + mi.toString());
		if (level >= 4) sb.append("/" + md.toString());
		return (sb.toString());
	}

	public String getText() {
		return ms.get(CTag.text);
	}

	public void setText(String text) {
		ms.put(CTag.text, text);
	}
	
	public void put(String name, String value) {
		ms.put(name, value);
	}
	
	public String getPOS() {
		return ms.get(CTag.pos);
	}

	public void setPOS(String text) {
		ms.put(CTag.pos, text);
	}

	public void setScore(double d) {
		md.put(CTag.score, d);
	}

	public double getScore() {
		if ( md.get(CTag.score)==null ) {
			return 0.0d;
		} else {
			return md.get(CTag.score);
		}
	}
//	public MutableInstance toM3rdInstanceBinary(){
//		return m.toM3rdInstance();
//	}
	public ClassLabel getLabel(boolean bTraining){
		if ( bTraining ) {
			if ( m.contains("rel") ) {
				return ClassLabel.positiveLabel(1);
			} else {
				return ClassLabel.negativeLabel(-1);
			}
		} else {
			return new ClassLabel();
		}
	}
	public Example toM3rdExample(boolean bTraining){
		return null;//new Example( toM3rdInstanceBinary(), getLabel(bTraining) );
	}
}
/**   protected transient String translationType;

 * a type value used by the TM; YEAR, NUMBER, ACRONYM, GENERAL, OOV.

 * a score that could have multiple interpretations:
 * <ul>
 * <li>for translation terms, score is confidence</li>
 * <li>for keyword terms, score is priority</li>
 * <li>for aliases, score is relatedness</li>
 * </ul>
 */
