package edu.cmu.lti.util.html;

import java.io.BufferedWriter;

import javax.swing.text.Element;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.TxtElementExtractor.Node.CallBack;
import edu.cmu.lti.util.text.FString;

/**
 * parse the file
 * http://www.graphviz.org/doc/info/colors.html
 * @author nlao
 *
 */
public class FColor {
	public static class RGB{
		int r,g,b;
		public RGB(String code){//#FFFFFF
			int i=Integer.parseInt(code.substring(1), 16);
			b =i %256; i=i>>8;
			g =i %256; i=i>>8;
			r =i;// (i %256); i=i>>8;			
			return;
		}
		public double getBrightness(){
			return (r+g+b)/(255.0+255.0+255.0);
		}
	}

	public class ColorHtmlExtractor extends TxtElementExtractor{//extends PMAbsInfor{
		public void postProcess(){
			return;
		};


		public ColorHtmlExtractor(){			
			root = new Node(null,null,new CallBack() {public boolean extract(Element e){
				return true;}});
			
		/*	root.newClass( "citation",new CallBack() {	public void extract(Element e){
				abs.addJournal( FHtml.getText(e.getElement(0)) );
				abs.addPage(FHtml.getText(e.getElement(1)));
				return;
				}});
	*/
			return;
		}
	}
	
	/**
	 * generate java code from a table of brewer colors
    value cat 1='div' 2='qual' 3='seq';
    value category 1='diverging' 2='qualitative' 3='sequential';

	 * @param fn
	 */
	public static String white="#FFFFFF";
	public static void generateCode(String fn){
		
		BufferedWriter bw = FFile.newWriter(fn+".java.frag");
		
		for (String line: FFile.enuLines(fn)){
			//*category, palette, nc, maxc, colors; 
			VectorS vs=FString.splitVS(line,"\t");
			
			int cat= Integer.parseInt(vs.get(0));
			String palette= vs.get(1);
			int nc= Integer.parseInt(vs.get(2));
			
			String name=palette.replaceAll("-", "")+nc;//.toLowerCase()
			
			FFile.write(bw, ","+name+"(new String[]{\""
					+vs.sub(4).join("\",\"")+"\"})\n");
		}
		FFile.close(bw);
		
	}
	public static void main(String[] args) {
		generateCode("brewer");
	}
}
