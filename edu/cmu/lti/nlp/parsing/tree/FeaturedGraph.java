/**
 *  data structure corresponds to graphic model
 *  Ni Lao 2008.3.3
 */
package edu.cmu.lti.nlp.parsing.tree;


import java.io.BufferedWriter;
import java.io.IOException;

import edu.cmu.lti.algorithm.Interfaces.IWrite;
import edu.cmu.lti.algorithm.container.MapXX;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.util.file.FFile;


/**
 * @author nlao
 *	it is a little wierd to let fgraph to have prefix data
 */
public class FeaturedGraph {
	
	public VectorX<Node> v_node = new VectorX<Node>(Node.class);
	public VectorX<Link> v_link = new VectorX<Link>(Link.class);
	public class Link implements IWrite{
		public  int inode1=-1;
		public  int inode2=-1;
		public Link(int i1, int i2){
			inode1=i1;inode2=i2;
		}
		public BufferedWriter write(BufferedWriter writer ){//throws IOException{
			FFile.write(writer,inode1+"\t"+inode2+"\n");
			return writer;
		}		
	}
	public static class Node  implements IWrite{//, IDoes {
		public static enum EFormat{
			GRMM, ASSERT
		}	

		//public static final int do_detach_tag=0;
		//extends TreeMap<String, String> 
		//TreeMap<String, String> m=new TreeMap<String, String>();
		public static EFormat format=EFormat.ASSERT;
		
		public String tag=null;
		public MapXX<String, String> ms=null; 
		public void detachTag(String s){
			//final String _dash="-";
			final String _O="O";
			tag=ms.get(s);
			if (tag==null) tag=_O;
			ms.remove(s);
		}
		public void normalizeText(String s, int max_len){
			String text= ms.get(s);
			if (text==null) return;
			if (text.length()> max_len) 	
				ms.put(s, "--");
				//m.remove(s);
			else
				ms.put(s, text.replace(' ', '_') );
			return;
		}
		public String prefix;
		public BufferedWriter write(BufferedWriter bw ){//throws IOException{
			switch(format){
			case GRMM:
				FFile.write(bw,tag+"\t----\t");
				FFile.write(bw,ms.join("=", "\t")+ "\n");
				break;
			case ASSERT:
				FFile.write(bw,prefix);
				FFile.write(bw,ms.join("=", "\t"));
				FFile.write(bw, "\t"+tag+"\n");
				break;
			}
	 		return bw;
		}
		
	}	
	public void detachTag(String s){
		for (Node n: v_node) n.detachTag(s);
	}
	public void normalizeText(String s, int max_len ){
		for (Node n: v_node) n.normalizeText(s, max_len);
	}	
	public boolean writeAssert(BufferedWriter writer ){//throws IOException{
		Node.format = Node.EFormat.ASSERT;
		v_node.write(writer);
		FFile.write(writer,"\n");
		return true;
	}	
	public boolean writeGRMM(	BufferedWriter bwN,BufferedWriter bwL ){//throws IOException{
		Node.format = Node.EFormat.GRMM;
		v_node.write(bwN);
		FFile.write(bwN,"\n");

		v_link.write(bwL);
		FFile.write(bwL,"\n");
		//for (Link x: v_link){	x.write(writer);	}		
 		return true;
	}	
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	}
	
}
