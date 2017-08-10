package edu.cmu.lti.util.run;

import java.io.BufferedWriter;

import edu.cmu.lti.util.file.FFile;

public class CollectPattern {
	public static class newWriter extends BufferedWriter{
		public newWriter(){
			super(null);
		}
		
	}
	
	public static String fdOut="CollectPattern/";
	
	public static void collect(String pattern) {
		FFile.mkdirs(fdOut);
		BufferedWriter bw= FFile.newWriter(fdOut+pattern);
		String fdScore="sCV.scores";
		for (String fn: FFile.getFileNames(fdScore, pattern)){
			String vs[]= FFile.loadString(fdScore+fn).split("\n");
			
		}
		FFile.close(bw);
	}
	public static void main(String[] args) {
		collect("");
	}
}
