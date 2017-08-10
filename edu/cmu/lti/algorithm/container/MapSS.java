/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.ArrayList;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

/**
 * @author nlao
 *This class is an extension to TMap&lt;String, String&gt;
 */
public class MapSS extends MapSX< String> {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	//extends TMap<String, String> {// implements IParseXML {
	//public MapSS clone(){	
	//return (MapSS) super.clone();	}

	public MapSS newInstance(){
		return new MapSS();
	}
	

	public SetS newSetValue(){
		return new SetS();
	}
	
	public  MapSS parseXMLAttrbuteContent(
			Element e, String tag, String attribute){
		clear();
		NodeList v = e.getElementsByTagName(tag);
		for (int i = 0; i < v.getLength(); i++) {
			Element e1 = (Element) v.item(i);
			String k= e1.getAttribute(attribute);
			String x =  e1.getTextContent().trim();
			this.put(k,x);
		}		
		return this;	
	}
	public MapSS(){
		super(String.class);
	}	
	public String parseValue(String v){		
		return v;
	}
	
	public static MapSS fromFile(String fn, int col1, int col2, String sep){
		MapSS m=new MapSS();
		m.loadFile(fn,col1,col2,sep);
		return m;
	}
	public static MapSS fromFile(String fn){
		return fromFile(fn,"\t");
	}

	public static MapSS fromFile(String fn, String sep){
		MapSS map=new MapSS();
		map.loadFile(fn,sep);
		return map;
	}
	public static MapSS fromLine(String line){
		return fromLine(line, "=", " ");
	}
	public static MapSS fromLine(String line, String cSep, String c){
		MapSS m=new MapSS();
		m.loadLine(line, cSep, c);
		return m;
	}
	public static MapSS fromStrings(String[] vs){
		MapSS m=new MapSS();
		m.loadStrings(vs);
		return m;
	}
	
	
	public MapIS replaceMatchIdx(ArrayList<String> vx){
		return (MapIS)super.replaceMatchIdx(vx);
	}

	public MapIS newInstanceIX(){
		return new MapIS();
	}
}
