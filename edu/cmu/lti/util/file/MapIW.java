package edu.cmu.lti.util.file;

public class MapIW extends MapXW<Integer>{
	public MapIW(String basePath, String postfix){
		super(String.class, basePath,postfix);
	}
	public MapIW(String basePath){
		super(Integer.class, basePath);
	}
}
