package edu.cmu.lti.util.run;

import java.io.File;

import edu.cmu.lti.algorithm.Hash;
import edu.cmu.lti.util.file.FFile;

/**
 * Help cache middle result for efficiency
 * @author nlao
 *
 */
public class Cacher{

	//private String cacheDir;
	public String path_;
	//public boolean doHash =false;
	public static enum NormStyle{
		none, md5, word_char
	}
	public NormStyle norm_style= NormStyle.word_char;

	public Cacher(String path){
		//this.cacheDir=cacheDir;
		if (!path.endsWith(File.separator)) path += File.separator;
		this.path_= path;
		FFile.mkdirs(path);
	}
	public Cacher(String path, NormStyle norm_style){
		this(path);
		this.norm_style = norm_style;
	}
	/*public Cacher(String path, boolean doHash){
		this(path);
		this.doHash = doHash;
	}*/
	public void saveString(String code,String data){
		FFile.saveString(path_+code, data);
	}
	public String loadString(String code){
		return FFile.loadString(path_+code);
	}


	//public static MD5 md5 = new MD5();
	
	/**
	 * make sure it is lawful file name string
	 * @param txt
	 */
	public String normalizeCode(String txt){
		switch(norm_style){
		case none:	
			return txt;
		case md5: 
			return Hash.hash(txt);
		case word_char: 
			return txt.replaceAll("\\p{Punct}", "_");
			//return txt.replaceAll("\\W", "_");
		}
		return txt;
	}
	public String getCode(Object data){
		return ((IGetCode)data).getCode();
	}
	
	public void saveObj(Object data){
		saveObj(data, getCode(data));
	}	
	public Object loadObj(Object data){
		return loadObj(getCode(data));
	}
	
	public Object loadObj(String code){
		//System.out.println("loading obj\n\t"+path+code );
		System.out.print("loading cached obj "+code );
		code = normalizeCode(code);
		Object o=  FFile.loadObject(path_+code+".obj");
		if (o!=null)	
			System.out.println("\t ... done");
		else
			System.out.println("\t ... not found");
		return o; 
	}
	public void saveObj( Object data, String code){
		System.out.println("saving obj "+ code );
		code = normalizeCode(code);
		FFile.saveObject(data, path_+code+".obj");
	}

}


