package edu.cmu.lti.util.file;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapXX;

/**
 * have a set of writers
 * @author nlao
 *
 * @param <K>
 */
public class MapXW<K> extends MapXX<K,BufferedWriter>{
	public static boolean bSilent=false;
	public String basePath; 
	public String postfix; 
	
	public MapXW(Class ck, String basePath){
		this(ck,basePath,"");
	}
	public MapXW(Class ck,String basePath, String postfix){
		super(ck, BufferedWriter.class);
		this.basePath=basePath;
		this.postfix=postfix;
	}
	
	public void writeln(K key, String txt){
		FFile.writeln(getBW(key), txt);
	}
	
	public BufferedWriter getBW(K k){
		BufferedWriter bw = get(k);
		if (bw==null){
			String fn=basePath+k+postfix;
			if (!bSilent)
				System.out.print("\ncreated file="+fn);
			bw =  FFile.newWriter(	fn);
			put(k,bw);
		}
		return bw;
	}
	public void closeAll(){
		for (BufferedWriter bw: values())
			FFile.close(bw);
		this.clear();
	}
}
