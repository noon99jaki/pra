package edu.cmu.lti.tools.visualize.graphviz;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.tools.visualize.graphviz.Tags.EArrowType;
import edu.cmu.lti.tools.visualize.graphviz.Tags.EShape;
import edu.cmu.lti.tools.visualize.graphviz.Tags.EStyle;
import edu.cmu.lti.util.html.EColor;
/**
 * see complete lists of attributes at www.graphviz.org/doc/info/
 * @author nlao
 * 
 */
public class LabeledObj implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	//public MapSS ms = new MapSS();		
	public SetS ms = new SetS();	
	public static int max_len=15;
	
	
	public static String format(MapSS ms){
		if (ms==null) return "";
		if (ms.size()==0) return "";
		return "["+ ms.join("=", ", ")+"]";
	}
	public static String format(SetS ms){
		if (ms==null) return "";
		if (ms.size()==0) return "";
		return "["+ ms.join(", ")+"]";
	}
	
	
	public String format(){
		return format(ms);
	}
	public LabeledObj setColor(EColor c){
		ms.add("color="+c.name());
		return this;
	}
	/*public LabeledObj setColor(String c){
		ms.add("color="+c);
		return this;
	}
	public LabeledObj setColor(EColorX c){
		ms.add("color="+c.name());
		return this;
	}*/
	public LabeledObj setWeight(double d){
		ms.add(String.format("weight=%.4f",d)); 
		return this;
	}
	public LabeledObj setStyle(EStyle c){
		ms.add("style="+c.name());
		return this;
	}
	public String breakString(String txt){
		if (txt.length() <= this.max_len) 
			return txt;
		StringBuffer sb= new StringBuffer();
		String[] vs= txt.split(" ");
		
		int len=0;
		for (String s: vs){
			if (len > this.max_len){
				sb.append("\\l");
				len=0;
			}
			else if (len>0)
				sb.append(" ");
			len += s.length();
			sb.append(s);
		}
		return sb.toString();
	}
	public LabeledObj setLabel(String c){			
		ms.add("label=\""+breakString(c)+"\"");
		return this;
	}	
	public LabeledObj setShape(EShape c){
		ms.add("shape="+c.name());
		return this;
	}	
	public LabeledObj setDir(EArrowType c){
		ms.add("dir="+c.name());
		return this;
	}	

	public LabeledObj setArrowTail(EArrowType c){
		ms.add("arrowtail="+c.name());
		return this;
	}	
	public LabeledObj setArrowHead(EArrowType c){
		ms.add("arrowhead="+c.name());
		return this;
	}	
	public LabeledObj setArrowHeadTail(EArrowType c){
		ms.add("arrowtail="+c.name());
		ms.add("arrowhead="+c.name());
		return this;
	}	
	/*		public Node put(String key, String value){
	ms.add(key+"="+ value);
	//ms.put(key, value);
	return this;
}*/
}