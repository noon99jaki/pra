package edu.cmu.lti.util.html;

import java.io.BufferedReader;
import java.util.Enumeration;

import javax.swing.text.AttributeSet;
import javax.swing.text.Element;
import javax.swing.text.ElementIterator;
import javax.swing.text.StyleConstants;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import javax.swing.text.html.HTML.Attribute;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.text.FString;

/**
 * to share parser with Xml, maybe we should use
 * Dom tree instead of swing text tree
 * @author nlao
 *
 */
public class FSwingHtml {
	public static Element loadElement(String txt){
		//HTMLDocument doc= FHtml.loadHtmlBody(txt);
		HTMLDocument doc= FSwingHtml.loadHtml(txt);
		Element e=doc.getDefaultRootElement();
		if (e==null){
			System.out.print("error load html");
			return null;
		}
		return e.getElement(1);

	}

	public static HTMLDocument loadHtmlBody(String txt){
		return loadHtml("<html><head></head><body>\n"
				+txt+"\n</body></html>");
	}
	public static HTMLDocument loadHtml(String txt){
		return loadHtml(FString.toBufferedReader(txt));
	}
	 
	public static HTMLDocument loadHtml(BufferedReader br){
	//String html){//
		try {
			HTMLEditorKit kit = new HTMLEditorKit();
			HTMLDocument doc = (HTMLDocument)kit.createDefaultDocument();
			doc.putProperty("IgnoreCharsetDirective"
					,Boolean.TRUE);//, new Boolean(true));

			kit.read(br,   doc,   0);  
		  br.close();
		  return doc;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;		
	}
	// Create parser (javax.swing.text.html.parser.ParserDelegator)
	/*			HTMLEditorKit.Parser parser = new ParserDelegator();
				// Get parser callback from document
				HTMLEditorKit.ParserCallback callback = doc.getReader(0);
				// Load it (true means to ignore character set)
				parser.parse(br, callback, true);
				*/
	

	public static String getText(Element e){
		try {
	    int startOffset = e.getStartOffset();
	    int endOffset = e.getEndOffset();
	    int length = endOffset - startOffset;
	    String s=e.getDocument().getText(startOffset, length);
	    return s.trim();
		} catch (Exception er) {
			er.printStackTrace();
		}
		return null;
	}
	
	//including text in children
	public static String getAllText(Element e){
		try {
      StringBuffer sb = new StringBuffer();
      int count = e.getElementCount();
      for (int i = 0; i < count; i++) {
        Element c = e.getElement(i);
        AttributeSet abC = c.getAttributes();
        if (abC.getAttribute(StyleConstants.NameAttribute) == HTML.Tag.CONTENT) 
          sb.append(getText(c));
      }
      return sb.toString();
		} catch (Exception er) {
			er.printStackTrace();
		}
		return null;
	}

	public static boolean hasAttribute(Element e, Attribute ab){
		AttributeSet mAb=e.getAttributes();
    for (Enumeration it = mAb.getAttributeNames() ; it.hasMoreElements() ;) {
    	Object s= it.nextElement();
      if (s.equals(ab))
      	return true;
    }
		return false;		
	}
	public static String getAttribute(Element e, Attribute ab){
		AttributeSet mAb=e.getAttributes();
    for (Enumeration it = mAb.getAttributeNames() ; it.hasMoreElements() ;) {
    	Object s= it.nextElement();
      if (s.equals(ab))
      	return (String) mAb.getAttribute(ab);
    }
		return null;
	}
	public static String getID(Element e){
		return getAttribute(e,HTML.Attribute.ID);
	}
	public static String getAlt(Element e){
		return getAttribute(e,HTML.Attribute.ALT);
	}	
	public static String getClass(Element e){
		return getAttribute(e,HTML.Attribute.CLASS);
	}
/*	public static String getClass(Element e){
		return (String) e.getAttributes()
			.getAttribute(HTML.Attribute.CLASS);
	}*/
	
	public static boolean withClass(Element e, String value){
		return e.getAttributes().containsAttribute(
				HTML.Attribute.class, value);
	}
	public static boolean withID(Element e, String value){
		return e.getAttributes().containsAttribute(
				HTML.Attribute.ID, value);
	}
	public static VectorX<Element> findChild(Element e
			, Attribute ab, String value){
    VectorX<Element> vE= new VectorX<Element>(Element.class);
    
    int count = e.getElementCount();
    for (int i = 0; i < count; i++) {
      Element c = e.getElement(i);
      AttributeSet mAb = c.getAttributes();
      if (mAb.containsAttribute(HTML.Attribute.ID, value))
      	vE.add(c);
    }
		return vE;
	}


	public static void extractAll(Element e	, Attribute ab, String value
			, Attribute abExtract,VectorS vs){
		for (Element c: findDecendent(e,ab, value))
			vs.add(FSwingHtml.getAttribute(c, abExtract));
		return;
	}
/*	public static VectorS extractAll(Element e
			, Attribute ab, String value, Attribute abExtract){
		VectorS vs=new VectorS();
		for (Element c: findDecendent(e,ab, value))
			vs.add(FHtml.getAttribute(c, abExtract));
		return vs;
	}*/
	public static void extractAll(Element e
			, Attribute ab,  Attribute abExtract,VectorS vs){
		//VectorS vs=new VectorS();
		for (Element c: findDecendent(e,ab))
			vs.add(FSwingHtml.getAttribute(c, abExtract));
		//return vs;
	}
	public static void extractAll(Element e	, Attribute ab,  VectorS vs){
		extractAll(e,ab,ab,vs);
	}
/*	public static VectorS extractAll(Element e, Attribute ab){
		VectorS vs=new VectorS();
		for (Element c: findDecendent(e,ab))
			vs.add(FHtml.getAttribute(c, abExtract));
		return vs;
	}*/
	public static VectorX<Element> matchChild(Element e
			, Attribute ab, String regex){
    VectorX<Element> vE= new VectorX<Element>(Element.class);
    
    int count = e.getElementCount();
    for (int i = 0; i < count; i++) {
      Element c = e.getElement(i);
      AttributeSet mAb = c.getAttributes();
      String s = (String) mAb.getAttribute(ab);	      
      if (s==null)continue;
//    System.out.println(name+" "+ab+"="+s);
      if (s.matches(regex))
        	vE.add(c);

    }
		return vE;
	}

	public static boolean hasAttribute(Element e, Attribute ab, String value){
		return e.getAttributes().containsAttribute(	ab,value);
	}
	public static boolean hasName(Element e, String name){
		return e.getAttributes().containsAttribute(Attribute.NAME,name);
	}
	public static boolean hasID(Element e, String name){
		return e.getAttributes().containsAttribute(Attribute.NAME,name);
	}
	public static Element findElement(HTMLDocument doc
			, Attribute ab, String value){
		
		return doc.getElement(doc.getDefaultRootElement(), ab, value);
		
    /*ElementIterator it = new ElementIterator(doc);
    Element e;
    while ((e = it.next()) != null) 
      if (hasAttribute(e,ab,value))return e;
		return null;*/	
	}

	public static VectorX<Element> findDecendent(Element e
			, Attribute ab, String value){
		VectorX<Element> vE= new VectorX<Element>(Element.class);
    ElementIterator it = new ElementIterator(e);
    Element c;
    while ((c = it.next()) != null){
    	System.out.println(ab+"="+FSwingHtml.getAttribute(c, ab));
      if (hasAttribute(c,ab,value))
      	vE.add(c);
    }
		return vE;	
	}
	
	public static VectorX<Element> findDecendent(Element e
			, Attribute ab){
		VectorX<Element> vE= new VectorX<Element>(Element.class);
    ElementIterator it = new ElementIterator(e);
    Element c;
    while ((c = it.next()) != null){
    	//System.out.println(ab+"="+FHtml.getAttribute(c, ab));
      if (hasAttribute(c,ab))  	vE.add(c);
    }
		return vE;	
	}
	/*
    // DOM to use for reading and writing
     DOM dom=null;

    // HTML Document representing the template
     HTMLDocument doc;

    dom = new DOM();
    dom.setProperty("sax.driver", 
        "com.docuverse.html.swing.SAXDriver");
    dom.setFactory(new HTMLFactory());
    doc = (HTMLDocument)dom.readDocument(input);

	 */
}
