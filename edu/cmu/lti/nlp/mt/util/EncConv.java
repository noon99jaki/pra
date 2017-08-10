package edu.cmu.lti.nlp.mt.util;

/*Frank Lin
 *
 */

import java.io.*;

public class EncConv{
	
	public static final int FLUSH_BUFFER=500000;
	
	public static void convert(String inEncoding,String outEncoding,InputStream inStream,OutputStream outStream)throws Exception{
		BufferedReader reader=new BufferedReader(new InputStreamReader(inStream,inEncoding));
		PrintWriter writer=new PrintWriter(new OutputStreamWriter(outStream,outEncoding),true);
		int nextChar;
		int toFlush=0;
		while((nextChar=reader.read())!=-1){
			writer.write((char)nextChar);
			toFlush++;
			if(toFlush>FLUSH_BUFFER){
				writer.flush();
				toFlush=0;
			}
		}
		reader.close();
		writer.close();
	}

	public static void main(String[] args)throws Exception{

		if(args.length==2){
			convert(args[0],args[1],System.in,System.out);
		}
		else if(args.length==4){
			File[] inputFiles=(new File(args[2])).listFiles();
			for(int i=0;i<inputFiles.length;i++){
				if(inputFiles[i].isFile()){
					System.out.println("Processing: "+inputFiles[i]);
					convert(args[0],args[1],new FileInputStream(inputFiles[i]),new FileOutputStream(args[3]+"/"+inputFiles[i].getName()+"."+args[1]));
				}
			}
			System.out.println("done.");
		}
		else{
			System.out.println("Usage:");
			System.out.println("java ConvEnc IN_ENC OUT_ENC < INPUT_FILE > OUTPUT_FILE");
			System.out.println("Or:");
			System.out.println("java ConvEnc IN_ENC OUT_ENC INPUT_DIR OUTPUT_DIR");
		}
	}
}
