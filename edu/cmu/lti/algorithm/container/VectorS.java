/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByInt;
import edu.cmu.lti.algorithm.Interfaces.IPlusObjOn;
import edu.cmu.lti.algorithm.Interfaces.IWrite;
import edu.cmu.lti.util.file.FFile;

/**
 * @author nlao
 *This class is an extension to Vector&lt;String&gt;
 */
public class VectorS extends VectorX<String> 
	implements Serializable, Cloneable, IPlusObjOn, IGetStrByInt{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VectorS newInstance(){
		return new VectorS();
	}	
	public String getString(int id){
		return get(id);
	}
	public String newValue(){//weakness of Java template
		return null;///TODO: is this correct?
	}
	public MapSI newMapValueId(){
		return new MapSI();
	}
	public VectorS plusObjOn(Object m){
		addAll((VectorS) m);
		return this;
	}
	public VectorI getVI(String name){
		VectorI v = new VectorI(size());
		for (int i=0; i<size(); ++i){
			if (name.equals("length"))
				v.set(i, get(i).length());			
		}
		return v;		
	}
	public static VectorS fromFile(String fn){
		return FFile.loadLines(fn);
	}
	public VectorS sub(Collection<Integer> vi) {
		return (VectorS) super.sub(vi);
	}
	
	public VectorS(char [] vc) {
		super(String.class);
		this.ensureCapacity(vc.length);
		for (char c: vc){
			this.add(""+c);
		}
	}	
	public VectorS(String[] v) {
		super(v);
	}
	public VectorS(Collection<String> v){
		super(v, String.class);
	}

	public VectorS(String x) {
		super(x.split(" "));
	}	

	public VectorS(){
		super(String.class);
	}
	public VectorS(int n){
		super(n,String.class);
	}

	public VectorS parseXMLString(Element e, String tag){
		clear();
		NodeList v = e.getElementsByTagName(tag);
		for (int i = 0; i < v.getLength(); i++) {
			add(  v.item(i).getTextContent().trim());
		}		
		return this;
	}
	public VectorI getLength(){
		VectorI v = new VectorI(size());
		for (int i=0; i<size(); ++i){
			v.set(i,get(i).length());			
		}
		return v;		
	}	
	public SetS newSetValue(){
		return new SetS();
	}
	public String[] toArray(){
		return super.toArray(new String[size()]);
	}
	public VectorS subMatch(String rgx){
		VectorS vs = new VectorS();
		for (String s: this)
			if (s.matches(rgx))
				vs.add(s);			
		return vs;
	}
	public VectorS sub(ArrayList<Integer> vi) {
		return (VectorS) super.sub(vi);
	}	
	public VectorS sub(Integer[] vi) {
		return (VectorS) super.sub(new VectorI(vi));
	}	
	public VectorI idxMatches(String regx) {
		VectorI v=new  VectorI();
		v.ensureCapacity(size());
		for (int i=0; i<size(); ++i){
			if (get(i).matches(regx)) v.add(i);
		}
		return v;
	}
/*	public VectorB toVectorB(String equ) {
		VectorB v=new  VectorB();
		v.ensureCapacity(size());
		for (int i=0; i<size(); ++i){
			if (get(i).equals(eq(regx)) v.add(i);
		}
		return v;
	}*/
	public static String join(String[] v,String c) {
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < v.length; i++) {
			if (i > 0)
				sb.append(c);
			sb.append(v[i]);
		}
		return (sb.toString());
	}
	public int totLength(){
		int l=0;
		for (String s: this)
			if (s!=null)
			l+= s.length();
		return l;
	}
	public String parseLine(String line){
		return line;
	}
	public SetS toSet (){
		return (SetS)super.toSet();
	}
	public int matchFist(String regx){
		for (int i=0; i<size(); ++i)
			if (get(i).matches(regx))
				return i;
		return -1;
	}
	public int indexFist(String subStr){
		for (int i=0; i<size(); ++i)
			if (get(i).indexOf(subStr)>=0)
				return i;
		return -1;
	}
	public int indexFist(String [] vSubStr){
		int i;
		for (int j=0;j<vSubStr.length;++j)
			if ((i=indexFist(vSubStr[j]))>=0)
				return i;
		return -1;
	}
	public static VectorS newEmpty(int n){
		VectorS s = new VectorS();
		s.ensureCapacity(n);
		return s;
	}
	public VectorS intersect(Collection<String> m){
		return (VectorS) super.intersect(m);
	}
	
	
	public static VectorS fromLine(String x, String sep) {
		return new VectorS(x.split(sep));
	}	
/*	public SetS(Iterable<String> v){
		super(v);
	}
	public static VectorS fromFile(String fn){
		return new VectorS(FFile.enuLines(fn));
	}*/
	public MapSI toMapValueId(){
		return (MapSI) super.toMapValueId();
	}
	public VectorD toVD(){
		VectorD vd=new VectorD();
		for (String s: this){
			if (s==null)
				vd.add(null);
			else
				vd.add(Double.parseDouble(s));
		}
		return vd;
	}
	
	// TODO: why not do it this way in VectorX?
	public BufferedWriter write(BufferedWriter writer) {
		for (String x : this) 
			FFile.write(writer, x.toString());	
		return writer;
	}
}
