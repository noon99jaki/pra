package edu.cmu.lti.util.xml;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;


//Xerces classes.
//import org.apache.xerces.dom.DocumentImpl;
//import org.apache.xml.serialize.*;


public class FXml {
	public static void sample()throws Exception {
	  int no = 2;

	  String root = "EnterRoot";
	  DocumentBuilderFactory dbf =   DocumentBuilderFactory.newInstance();
	  DocumentBuilder db =  dbf.newDocumentBuilder();
	  Document d = db.newDocument();
	  Element eRoot = d.createElement(root);
	      d.appendChild(eRoot);
	  for (int i = 1; i <= no; i++){

	    String element = "EnterElement";
	    String data = "Enter the data";
	    
	    Element e = d.createElement(element);
	    e.appendChild(d.createTextNode(data));
	    eRoot.appendChild(e);
	  }
	  TransformerFactory tf = TransformerFactory.newInstance();
    Transformer t = tf.newTransformer();
    DOMSource source = new DOMSource(d);
    StreamResult result =  new StreamResult(System.out);
    t.transform(source, result);

	}
	
	
	public static void sample1()throws Exception {
/*
		Element e = null;
		Node n = null;
		// Document (Xerces implementation only).
		Document xmldoc= new DocumentImpl();
		// Root element.
		Element root = xmldoc.createElement("USERS");
		String[] id = {"PWD122","MX787","A4Q45"};
		String[] type = {"customer","manager","employee"};
		String[] desc = {"Tim@Home","Jack&Moud","John D'oé"};
		for (int i=0;i<id.length;i++)
		{
		  // Child i.
		  e = xmldoc.createElementNS(null, "USER");
		  e.setAttributeNS(null, "ID", id[i]);
		  e.setAttributeNS(null, "TYPE", type[i]);
		  n = xmldoc.createTextNode(desc[i]);
		  e.appendChild(n);
		  root.appendChild(e);
		}
		xmldoc.appendChild(root);
		FileOutputStream fos = new FileOutputStream(filename);
		// XERCES 1 or 2 additionnal classes.
		OutputFormat of = new OutputFormat("XML","ISO-8859-1",true);
		of.setIndent(1);
		of.setIndenting(true);
		of.setDoctype(null,"users.dtd");
		XMLSerializer serializer = new XMLSerializer(fos,of);
		// As a DOM Serializer
		serializer.asDOMSerializer();
		serializer.serialize( xmldoc.getDocumentElement() );
		fos.close();*/
	}
	public static void main(String[] args) throws Exception {
		sample();
	}
}
