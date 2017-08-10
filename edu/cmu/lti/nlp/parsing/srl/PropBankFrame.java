/**
 * 
 */
package edu.cmu.lti.nlp.parsing.srl;

import java.io.File;
import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

import edu.cmu.lti.algorithm.Interfaces.IParseXML;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.xml.FDom;

/**
 * @author nlao
 *
 */
public class PropBankFrame {
//	v_topic.parseXML(e, CXML.TOPIC);
	//public static enum ETag{	frameset	}
	public static class CTag{
		public static final String frameset = "frameset";
	}
	public static class FrameSet implements IParseXML {
		public Object parseXML(Element e){
			
			return this;
		}
		
	}
	public static class Verb{
		public String txt;
	//	
		VectorX<FrameSet> v_fs= new VectorX<FrameSet>(FrameSet.class);
		public boolean parse(String fileName){
			Element e =  FDom.loadFile(fileName);		
			if (e == null) return false;
			v_fs.parseXML(e,CTag.frameset);// ETag.frameset.toString());

//			Element emeta= FXml.getElement(e,CXML.METADATA);
//			lang = FXml.getElement(emeta, CXML.LANGUAGE).getAttribute(CXML.TARGET);
//			corpus = FXml.getElement(emeta, CXML.CORPUS).getTextContent().trim();
			return true;
		}
		
	}
	MapSI msi = new MapSI();
	VectorX<Verb> v_verb= new VectorX<Verb>(Verb.class);
	public boolean parse(String folder){
		VectorS  vs= 	FFile.getFileNames(folder);
		//TVector<File> vf=
		for (String f: vs){
			Verb verb = new Verb();
			if (!verb.parse(folder+"/"+f))//.getPath()
				continue;
			msi.put(verb.txt, v_verb.size());
			v_verb.add(verb);
		}
		return true;
	}
	public static void main(String[] args) throws IOException,Exception {
		/*if (args.length < 1) {
			System.out.println("Usage: java FSRL file_in");
			System.exit(-1);
		}*/
		PropBankFrame fr = new PropBankFrame();
		//fr.parse("temp");
		fr.parse("/usr2/nlao/resources/LDC/cpb1.0/cpb-1.0/data/8/");//frames");
	}
}
