package edu.cmu.pra.data;

import java.io.BufferedWriter;

import org.w3c.dom.Element;
import org.w3c.dom.Node;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.xml.FDom;

public class ACLExtractor {
	
	static ACLXmlExtractor ae= new ACLXmlExtractor();

	
	public static String fdXml="acl-arc-090501d1/metadata/anthology-XML/";
	public static void extract(){
		BufferedWriter bwOutput=FFile.newWriter("extracted");
		
		int nErr=0;
		int nGood=0;
		for (String fn: FFile.getFiles(fdXml, ".*.xml", true)){
			//System.out.println(fn);
			ACLMetaInfo.CorpusID=fn.split("\\.")[1];
			
			//Element d= FDom.loadFile(fdXml+fn,"100315-acl.dtd");
			String txt=FFile.loadString(fdXml+fn);
			txt=txt.replaceAll("& ", " &amp; ");
			txt=txt.replaceAll("Q&A", " Q&amp;A");
			txt=txt.replaceAll("R&D", " R&amp;D");
			Element d= FDom.loadTxt(txt, "100315-acl.dtd", "volume");
			
			if (d==null){
				System.out.println("error loading="+fn);
				++nErr;
				continue;
			}
			
			ACLMetaInfo.CorpusID=FDom.getAttribute(d,"id");	
			
			for (Node c= d.getFirstChild(); c!=null; c=c.getNextSibling()){
				if (!ae.extract(c)) continue;
				ae.info.normalize();
				FFile.write(bwOutput	,ae.info.print()+"\n");
			}
			++nGood;
		}
		FFile.close(bwOutput);
		System.out.println("nError="+nErr);
		System.out.println("nGood="+nGood);
		
	}
	
	public static void main(String args[]) {
		extract();
	}
}
