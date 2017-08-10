package edu.cmu.lti.util.xml;

import java.io.FileInputStream;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;

import edu.cmu.lti.util.file.FFile;



public class FDom {
	public static String getChildText(Element e){//Node node) {
		if(e==null)		return null;

		NodeList nl=e.getChildNodes();
		for(int i=0;i<nl.getLength();i++){
			if(nl.item(i).getNodeType()==Node.TEXT_NODE)
				return nl.item(i).getNodeValue();			
		}
		return null;
	}
	public static String getChildText(Element e, String name){
		Element eatype=getElement(e,name);
		if (eatype == null) 
			return null;
		return eatype.getTextContent().trim();
	}
	public static Element getElement(Element e, String tag) {
		NodeList nl = e.getElementsByTagName(tag);
		if (nl.getLength() == 0) return null;
		return (Element) nl.item(0);
	}
	public static void print(Node e){
		printRecur(e,0);
	}
	public static String getAttribute(Node e,String name){
		return e.getAttributes().getNamedItem(name).getNodeValue();
	}	
	public static Boolean getFeatureYN(Node e, String name){
		String value=getAttribute(e,name);
		if (value.equals("Y")) return true;
		if (value.equals("N")) return false;
		return null;
	}

	
	private static void printRecur(Node e, int tab){
		System.out.println(e.getNodeName());
		tab+=1;
		for (Node n =  e.getFirstChild(); n!=null; n=n.getNextSibling())
			printRecur(n, tab);
		
	}

  private static final String PROPS_DTD_URI =
    "http://java.sun.com/dtd/properties.dtd";

    private static final String PROPS_DTD =
    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
    "<!-- DTD for properties -->"                +
    "<!ELEMENT properties ( comment?, entry* ) >"+
    "<!ATTLIST properties"                       +
        " version CDATA #FIXED \"1.0\">"         +
    "<!ELEMENT comment (#PCDATA) >"              +
    "<!ELEMENT entry (#PCDATA) >"                +
    "<!ATTLIST entry "                           +
        " key CDATA #REQUIRED>";


  private static class Resolver implements EntityResolver {
    public InputSource resolveEntity(String pid, String sid)
        throws SAXException
    {
        if (sid.equals(PROPS_DTD_URI)) {
            InputSource is;
            is = new InputSource(new StringReader(PROPS_DTD));
            is.setSystemId(PROPS_DTD_URI);
            return is;
        }
        throw new SAXException("Invalid system identifier: " + sid);
    }
}

  public static DocumentBuilder getDocBuilder()  {
		try {			
	    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
	    //dbf.setNamespaceAware(true);
	    dbf.setValidating(true);
	    dbf.setIgnoringElementContentWhitespace(true);// it never works
	    dbf.setIgnoringComments(true);
	    
	    DocumentBuilder db= dbf.newDocumentBuilder();
	    db.setErrorHandler(null);
	    //db.setEntityResolver(		new Resolver());
	    
	  //  db.setEntityResolver(  (DefaultHandler)Class.forName
	   // 		("com.sun.enterprise.config.serverbeans.ServerValidationHandler").newInstance());
	    
	    
	    return db;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
  }
  
	public static Element loadFile(String fn, String fnDTD){
		try {
			DocumentBuilder db= 	getDocBuilder();
			
      Document xmlDocument = db.parse(new FileInputStream(fn));
      DOMSource source = new DOMSource(xmlDocument);

      // Use the tranformer to validate the document
      StreamResult result = new StreamResult(System.out);  
      
      
      TransformerFactory tf = TransformerFactory.newInstance();
      Transformer transformer = tf.newTransformer();
      transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM,	fnDTD);
      transformer.transform(source, result);
      
			return	null;//	db.parse(new FileInputStream(fn))	.getDocumentElement();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
  
	public static Element loadFile(String fn){
		try {
			return 	getDocBuilder()
				.parse(new FileInputStream(fn))
				.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	
	
	//public static String header=	"<?xml version=\"1.0\"  encoding=\"UTF-8\"?>\n";
	public static Element loadTxt(String txt,String fnDTD, String rootTag){
		
		txt= txt.replace("<"+rootTag+" "
				, "<!DOCTYPE "+rootTag+" SYSTEM \""+fnDTD+"\">\n" +"<"+rootTag+" ");
		
		return loadTxt(txt);
	}
	
	
	public static Element loadTxt(String txt){
		try {
			return getDocBuilder()
				.parse(new InputSource(new StringReader(txt)))
				.getDocumentElement();
		} catch (Exception e) {
			e.printStackTrace();
			return null;		
		}
	}
	
	
	public static Element loadHtmlFile(String fn){
	/*  try {
	    DOM dom = new DOM();
	    dom.setProperty("sax.driver", "com.docuverse.html.swing.SAXDriver");
	    dom.setFactory(new HTMLFactory());
	    HTMLDocument doc = (HTMLDocument)dom.readDocument(new FileInputStream(new File(fn)));
	    return doc.getDocumentElement();
		} catch (IOException e) {
		    //throw new ServletException(e.getMessage());
		}*/
    return null;

	}
	
	
	public Element LoadHTML(String filename){
		
		FileInputStream inStream;
		String s = new String();
		String urlIS = new String(filename);
		
		try	{
			DOMParser parser = new DOMParser();
			parser.parse(new InputSource(new FileInputStream(urlIS)));
			Document doc= parser.getDocument();
			return doc.getDocumentElement();
			//HTMLInputElement hi = (HTMLInputElement)doc.getElementById("search");
		}
		catch (Exception e)	{
			return null;
		}

/*		StringWriter sw = new StringWriter();
		Transformer t = null;
		try
		{
			t = TransformerFactory.newInstance().newTransformer(); 
		}
		catch (Exception e)
		{
			return "";
		}
		t.setOutputProperty(OutputKeys.METHOD, "html");
		t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATIO N, "yes");
		try
		{
			t.transform(new DOMSource(doc), new StreamResult(sw));
		}
		catch (Exception e)
		{
			return "";
		}
		s = sw.toString();
		return s;*/
	}

	public static void main(String args[]) {
		FFile.removeNonAsc("pmid.crawl");
		/*
		String fn="failed/10021457";
		Element e= FDom.loadFile(fn);
		FDom.print(e);*/
		return;
	}
}
