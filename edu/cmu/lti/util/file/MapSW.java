package edu.cmu.lti.util.file;

public class MapSW extends MapXW<String>{
	public MapSW(String basePath, String postfix){
		super(String.class, basePath,postfix);
	}
	
	public MapSW(String basePath){
		super(String.class, basePath);
	}
}
