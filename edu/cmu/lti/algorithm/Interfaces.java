/**
 * 
 */
package edu.cmu.lti.algorithm;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import org.w3c.dom.Element;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.util.file.FFile;

/**
 * Interfaces let container to operate on abstract objects
 * @author nlao
 *
 */
public class Interfaces {
	public static class CTag{
		public static final String length = "length";
		public static final String size = "size";
		public static final String value = "value";
		public static final String key = "key";
		public static final String toString = "toString";
		
	}
	
	public static interface IFromFile {
/*	public static VectorS fromFile(String fn);

	public static VectorS fromFile(String fn, int iCol);
	public static VectorS fromFile(String fn, int iCol, String sep);
	public static VectorS fromLine(String x, String sep) ;*/
	}
	public static interface IParseLine {
		public boolean parseLine(String line);	
	}
	public static interface IPlusObjOn {
		public Object plusObjOn(Object x);	
	}
	public static interface IPlusObj {
		public Object plusObj(Object x);	
	}
	public static interface IMinusObjOn {
		public Object minusObjOn(Object x);	
	}	
	public static interface IMultiplyOn {
		public Object multiplyOn(Double x);	
	}
	public static interface IMultiply {
		public Object multiply(Double x);
	}
	public static interface ILength {
		public int length();
	}
	public static interface IGetStrByStr {
		public String getString(String name);
	}
	public static interface IGetMapIDByStr {
		public MapID getMapID(String name);
	}
	
	public static interface IGetStrByInt {
		public String getString(int id);
	}
	public static interface IGetIntByStr {
		public Integer getInt(String name);
	}
	public static interface ISetDblByStr {
		public void setDouble(String name,Double d);
	}
	public static interface ISetIntByStr {
		public void setInt(String name, Integer i);
	}
	
	public static interface IGetObjByStr {
		public Object getObj(String name);
	}
	public static interface IGetObjByStrInt {
		public Object getObj(String name, int id);
	}
	public static interface IGetDblByStr {
		public Double getDouble(String name);
	}
	public static interface IGetBoolByStr {
		public Boolean getBoolean(String name);
	}
	public static interface IGetDblByInt {
		public Double getDouble(Integer i);
	}
	public static interface IGetClass {	
		//	public  java.lang.Class getClass();	
	}
	
	public static interface ISerializableObj {	
		//http://java.sun.com/developer/technicalArticles/Programming/serialization/

		//private void onWriteObject(ObjectOutputStream out) throws IOException
		//out.defaultWriteObject(); 
		//private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
		
	}
	

	public static interface ICloneable {
		public Object clone()  ;//throws CloneNotSupportedException ;
	}
	public static interface IAddOn {
		public Object addOn(Object x);
		
	}
	public static interface IAdd {
		public Object add(Object x);
	}
	
	/**
	 * write an object into what ever format the user want
	 * @author nlao
	 *
	 */
	public static interface  IWrite {
		public BufferedWriter write(BufferedWriter writer );//throws IOException;
	}
	public static interface IParseXML {		
		public  Object parseXML(Element e);
	}
	public static interface IToXML {		
		public  String toXML();
	}
	
	public static interface IUpdateIdx {
		//public void updateIdx(ArrayList<Integer> vi);
		public void updateIdx(VectorI vi);
	}
	public static interface IRead {
		public BufferedReader read(BufferedReader reader );// throws IOException;
	}
	
	
}
