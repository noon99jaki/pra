package edu.cmu.lti.util.text;


import java.lang.*;
import java.io.*;
import java.util.*;

import edu.cmu.lti.util.system.FSystem;

/* Copyright 2002 Erik Peterson 
 Code and program free for non-commercial use.
 Contact erik@mandarintools.com for fees and
 licenses for commercial use.
 */

public class EncodingConverter{
	// Simplfied/Traditional character equivalence hashes
	protected Hashtable s2thash, t2shash, normalizeHash;
//	Supported Encoding Types
	public static int GB2312        = 0;
	public static int GBK           = 1;
	public static int HZ            = 2;
	public static int BIG5          = 3;
	public static int CNS11643      = 4;
	public static int UTF8          = 5;
	public static int UTF8T         = 6;
	public static int UTF8S         = 7;
	public static int UNICODE       = 8;
	public static int UNICODET      = 9;
	public static int UNICODES      = 10;
	public static int ISO2022CN     = 11;
	public static int ISO2022CN_CNS = 12;
	public static int ISO2022CN_GB  = 13;
	public static int EUC_KR  = 14;
	
	public static int ASCII         = 15;
	public static int OTHER         = 16;
	
  private String charMappingFileLocation;
  private String normalizeMappingFileLocation;
	
	public static int TOTALTYPES    = 17;
	
	// Names of the encodings as understood by Java
	public static String[] javaname;
	// Names of the encodings for human viewing
	public static String[] nicename;
	// Names of charsets as used in charset parameter of HTML Meta tag
	public static String[] htmlname;
	
	private static void init(){
		javaname = new String[TOTALTYPES];
		nicename = new String[TOTALTYPES];
		htmlname = new String[TOTALTYPES];
		
		// Assign encoding names
		javaname[GB2312] = "GB2312";
		javaname[HZ] = "ASCII";  // What to put here?  Sun doesn't support HZ
		javaname[GBK] = "GBK";
		javaname[ISO2022CN_GB] = "ISO2022CN_GB";
		javaname[BIG5] = "BIG5";
		javaname[CNS11643] = "EUC-TW";
		javaname[ISO2022CN_CNS] = "ISO2022CN_CNS";
		javaname[ISO2022CN] = "ISO2022CN";
		javaname[UTF8] = "UTF8";
		javaname[UTF8T] = "UTF8";
		javaname[UTF8S] = "UTF8";
		javaname[UNICODE] = "Unicode";
		javaname[UNICODET] = "Unicode";
		javaname[UNICODES] = "Unicode";
		javaname[EUC_KR] = "EUC_KR";
		javaname[ASCII] = "ASCII";
		javaname[OTHER] = "ISO8859_1";
		
		// Assign encoding names
		htmlname[GB2312] = "GB2312";
		htmlname[HZ] = "HZ-GB-2312";  
		htmlname[GBK] = "GB2312";
		htmlname[ISO2022CN_GB] = "ISO-2022-CN-EXT";
		htmlname[BIG5] = "BIG5";
		htmlname[CNS11643] = "EUC-TW";
		htmlname[ISO2022CN_CNS] = "ISO-2022-CN-EXT";
		htmlname[ISO2022CN] = "ISO-2022-CN";
		htmlname[UTF8] = "UTF-8";
		htmlname[UTF8T] = "UTF-8";
		htmlname[UTF8S] = "UTF-8";
		htmlname[UNICODE] = "UTF-16";
		htmlname[UNICODET] = "UTF-16";
		htmlname[UNICODES] = "UTF-16";
		htmlname[EUC_KR] = "EUC-KR";
		htmlname[ASCII] = "ASCII";
		htmlname[OTHER] = "ISO8859-1";
		
		// Assign Human readable names
		nicename[GB2312] = "GB-2312";
		nicename[HZ] = "HZ";
		nicename[GBK] = "GBK";
		nicename[ISO2022CN_GB] = "ISO2022CN-GB";
		nicename[BIG5] = "Big5";
		nicename[CNS11643] = "CNS11643";
		nicename[ISO2022CN_CNS] = "ISO2022CN-CNS";
		nicename[ISO2022CN] = "ISO2022 CN";
		nicename[UTF8] = "UTF-8";
		nicename[UTF8T] = "UTF-8 (Trad)";
		nicename[UTF8S] = "UTF-8 (Simp)";
		nicename[UNICODE] = "Unicode";
		nicename[UNICODET] = "Unicode (Trad)";
		nicename[UNICODES] = "Unicode (Simp)";
		nicename[EUC_KR] = "EUC-KR";
		nicename[ASCII] = "ASCII";
		nicename[OTHER] = "OTHER";
	}

  private void loadProperties() throws Exception{
    try{
      Properties properties = new Properties();   
      File userProperties = new File( System.getProperty("javelin.home")+ "/conf", getClass().getName() + ".properties");
      if(!userProperties.exists())
        throw new IOException("Missing properties file for "+ getClass().getName());
      properties.load(new FileInputStream( userProperties));
		  
      charMappingFileLocation = ((String)properties.get("charMappingFile")).trim();  
      normalizeMappingFileLocation = ((String)properties.get("normalizeMappingFile")).trim();  
    } catch ( Exception e ) {
      System.err.println( "Caught exception while loading properties: "
      + e.getMessage() );
      throw new RuntimeException( "Could not load properties for " + getClass().getName());
    }
  }
	
	// Constructor
	public EncodingConverter() {
		init();
		String dataline;
		
		// Initialize and load in the simplified/traditional character hashses
		s2thash = new Hashtable();
		t2shash = new Hashtable();
		normalizeHash = new Hashtable();
		
		try {
      loadProperties();
			BufferedReader in = new BufferedReader(new InputStreamReader(new FileInputStream( new File(charMappingFileLocation)), "UTF8"));
			while ((dataline = in.readLine()) != null) {
				// Skip empty and commented lines
				if (dataline.length() == 0 || dataline.charAt(0) == '#') {
					continue;
				}
				
				// Simplified to Traditional, (one to many, but pick only one)
        String s= dataline.substring(1,2);
        String t= dataline.substring(0,1);
        if(!s2thash.containsKey(s.intern()))
				  s2thash.put(s.intern(),t);
				
				// Traditional to Simplified, (many to one)
				t2shash.put(t.intern(), s);
				//for (int i = 1; i < dataline.length(); i++) {
				//	t2shash.put(dataline.substring(i,i+1).intern(), dataline.substring(0,1));
				//}
			}
			
			in = new BufferedReader(new InputStreamReader(new FileInputStream( new File(normalizeMappingFileLocation)), "UTF8"));
			while ((dataline = in.readLine()) != null) {
				//Normalization terms
				normalizeHash.put(dataline.charAt(0), dataline.charAt(1));
			}
      in.close();
		}
		catch (Exception e) {
      e.printStackTrace();
			System.err.println(e);
		}
		
	}

	public String convertString(String dataline) {
    return convertString(dataline, UTF8T, UTF8S);
  }

	public String convertAnswerPattern(String dataline) {
    return convertAnswerPattern(dataline,UTF8T, UTF8S);
  }
	
  //doesn't normalize "()" to "（）"
	public String convertAnswerPattern(String dataline, int source_encoding, int target_encoding) {
		StringBuffer outline = new StringBuffer();
		int lineindex;
		
		if (source_encoding == HZ) {
			dataline = hz2gb(dataline);
		}
		for (lineindex = 0; lineindex < dataline.length(); lineindex++) {
			if ((source_encoding == GB2312 || source_encoding == GBK || source_encoding == ISO2022CN_GB ||
					source_encoding == HZ || 
					source_encoding == UNICODE || source_encoding == UNICODES || source_encoding == UTF8 ||
					source_encoding == UTF8S) 
					&&
					(target_encoding == BIG5 || target_encoding == CNS11643 || target_encoding == UNICODET ||
							target_encoding == UTF8T ||
							target_encoding == ISO2022CN_CNS)) {
				if (s2thash.containsKey(dataline.substring(lineindex, lineindex+1)) == true) {
					outline.append(s2thash.get(dataline.substring(lineindex, lineindex+1).intern()));
				} else {
					outline.append(dataline.substring(lineindex, lineindex+1));
				}
			} else if ((source_encoding == BIG5 || source_encoding == CNS11643 || source_encoding == UNICODET ||
					source_encoding == UTF8 || source_encoding == UTF8T ||
					source_encoding == ISO2022CN_CNS || source_encoding == GBK || source_encoding == UNICODE) 
					&&
					(target_encoding == GB2312 || target_encoding == UNICODES || target_encoding == ISO2022CN_GB ||
							target_encoding == UTF8S || target_encoding == HZ || target_encoding == UTF8 )) {

				if (t2shash.containsKey(dataline.substring(lineindex, lineindex+1)) == true) {
					outline.append(t2shash.get(dataline.substring(lineindex, lineindex+1).intern()));
          //if(dataline.substring(lineindex, lineindex+1).equals("麽")){
          //  System.err.println("found, "+t2shash.get(dataline.substring(lineindex, lineindex+1).intern()));
          //}
				} else {
					outline.append(dataline.substring(lineindex, lineindex+1));
          //if(dataline.substring(lineindex, lineindex+1).equals("麽")){
          //  System.err.println("not found");
          //}
				}
			} else {
				outline.append(dataline.substring(lineindex, lineindex+1));
			}
		}
		
		if (target_encoding == HZ) {
			// Convert to look like HZ
			return gb2hz(outline.toString());
		}
		
		String toNormalizeLine = outline.toString();
		outline = new StringBuffer();
		for (lineindex = 0; lineindex < toNormalizeLine.codePointCount(0,toNormalizeLine.length()); lineindex++) {
      char charToProcess = toNormalizeLine.charAt(lineindex);
			if ( normalizeHash.containsKey(charToProcess) == true) {
        if(charToProcess != '(' && charToProcess != ')')  
				  outline.append(normalizeHash.get(charToProcess));
        else
				  outline.append(charToProcess);
			} else {
				outline.append(charToProcess);
			}
		}
		//System.out.println(dataline);
		return outline.toString();
		
	}
	
	public String convertString(String dataline, int source_encoding, int target_encoding) {
		StringBuffer outline = new StringBuffer();
		int lineindex;
		
		if (source_encoding == HZ) {
			dataline = hz2gb(dataline);
		}
		for (lineindex = 0; lineindex < dataline.length(); lineindex++) {
			if ((source_encoding == GB2312 || source_encoding == GBK || source_encoding == ISO2022CN_GB ||
					source_encoding == HZ || 
					source_encoding == UNICODE || source_encoding == UNICODES || source_encoding == UTF8 ||
					source_encoding == UTF8S) 
					&&
					(target_encoding == BIG5 || target_encoding == CNS11643 || target_encoding == UNICODET ||
							target_encoding == UTF8T ||
							target_encoding == ISO2022CN_CNS)) {
				if (s2thash.containsKey(dataline.substring(lineindex, lineindex+1)) == true) {
					outline.append(s2thash.get(dataline.substring(lineindex, lineindex+1).intern()));
				} else {
					outline.append(dataline.substring(lineindex, lineindex+1));
				}
			} else if ((source_encoding == BIG5 || source_encoding == CNS11643 || source_encoding == UNICODET ||
					source_encoding == UTF8 || source_encoding == UTF8T ||
					source_encoding == ISO2022CN_CNS || source_encoding == GBK || source_encoding == UNICODE) 
					&&
					(target_encoding == GB2312 || target_encoding == UNICODES || target_encoding == ISO2022CN_GB ||
							target_encoding == UTF8S || target_encoding == HZ || target_encoding == UTF8 )) {

				if (t2shash.containsKey(dataline.substring(lineindex, lineindex+1)) == true) {
					outline.append(t2shash.get(dataline.substring(lineindex, lineindex+1).intern()));
          //if(dataline.substring(lineindex, lineindex+1).equals("麽")){
          //  System.err.println("found, "+t2shash.get(dataline.substring(lineindex, lineindex+1).intern()));
          //}
				} else {
					outline.append(dataline.substring(lineindex, lineindex+1));
          //if(dataline.substring(lineindex, lineindex+1).equals("麽")){
          //  System.err.println("not found");
          //}
				}
			} else {
				outline.append(dataline.substring(lineindex, lineindex+1));
			}
		}
		
		if (target_encoding == HZ) {
			// Convert to look like HZ
			return gb2hz(outline.toString());
		}
		
		String toNormalizeLine = outline.toString();
		outline = new StringBuffer();
		for (lineindex = 0; lineindex < toNormalizeLine.codePointCount(0,toNormalizeLine.length()); lineindex++) {
			if (normalizeHash.containsKey(toNormalizeLine.charAt(lineindex)) == true) {
				outline.append(normalizeHash.get(toNormalizeLine.charAt(lineindex)));
			} else {
				outline.append(toNormalizeLine.charAt(lineindex));
			}
		}
		//System.out.println(dataline);
		return outline.toString();
		
	}
	
	
	public String hz2gb(String hzstring) {
		byte[] hzbytes = new byte[2];
		byte[] gbchar = new byte[2];
		int byteindex = 0;
		StringBuffer gbstring = new StringBuffer("");
		
		try {
			hzbytes = hzstring.getBytes("8859_1");
		} 
		catch (Exception usee) { System.err.println("Exception " + usee.toString()); return hzstring; } 
		
		// Convert to look like equivalent Unicode of GB
		for (byteindex = 0; byteindex < hzbytes.length; byteindex++) {
			if (hzbytes[byteindex] == 0x7e) {
				if (hzbytes[byteindex+1] == 0x7b) {
					byteindex+=2;
					while (byteindex < hzbytes.length) {
						if (hzbytes[byteindex] == 0x7e && hzbytes[byteindex+1] == 0x7d) {
							byteindex++;
							break;
						} else if (hzbytes[byteindex] == 0x0a || hzbytes[byteindex] == 0x0d) {
							gbstring.append((char)hzbytes[byteindex]);
							break;
						}
						gbchar[0] = (byte)(hzbytes[byteindex] + 0x80);
						gbchar[1] = (byte)(hzbytes[byteindex+1] + 0x80);
						try {
							gbstring.append(new String(gbchar, "GB2312"));
						}  catch (Exception usee) { System.err.println("Exception " + usee.toString()); } 
						byteindex+=2;
					} 
				} else if (hzbytes[byteindex+1] == 0x7e) { // ~~ becomes ~
					gbstring.append('~');
				} else {  // false alarm
					gbstring.append((char)hzbytes[byteindex]);  
				}
			} else {
				gbstring.append((char)hzbytes[byteindex]);
			}
		}
		return gbstring.toString();
	}
	
	public String gb2hz(String gbstring) {
		StringBuffer hzbuffer;
		byte[] gbbytes = new byte[2];
		int i;
		boolean terminated = false;
		
		hzbuffer = new StringBuffer("");
		try {
			gbbytes = gbstring.getBytes("GB2312");
		}
		catch (Exception usee) { System.err.println(usee.toString()); return gbstring; }
		
		for (i = 0; i < gbbytes.length; i++) {
			if (gbbytes[i] < 0) {
				hzbuffer.append("~{");
				terminated = false;
				while (i < gbbytes.length) {
					if (gbbytes[i] == 0x0a || gbbytes[i] == 0x0d) {
						hzbuffer.append("~}" + (char)gbbytes[i]);
						terminated = true;
						break;
					} else if (gbbytes[i] >= 0) {
						hzbuffer.append("~}" + (char)gbbytes[i]);
						terminated = true;
						break;
					}
					hzbuffer.append((char)(gbbytes[i] + 256 - 0x80));
					hzbuffer.append((char)(gbbytes[i+1] + 256 - 0x80));
					i+=2;		    
				}
				if (terminated == false) {
					hzbuffer.append("~}");
				}
			} else {
				if (gbbytes[i] == 0x7e) {
					hzbuffer.append("~~");
				} else {
					hzbuffer.append((char)gbbytes[i]);
				}
			}
		}
		return new String(hzbuffer);
	}
	
	
	public void convertFile(File inputFile, File outputFile, int source_encoding, int target_encoding) {
		BufferedReader srcbuffer;
		BufferedWriter outbuffer;
		String dataline;
		
		if(inputFile.isDirectory()){
			if(!outputFile.exists()){
				outputFile.mkdirs();
			}else 
				if(!outputFile.isDirectory())
					FSystem.die("InputFile is a directory while output file is not");
			String[] children = inputFile.list();
			for(int i=0;i<children.length;i++){
				convertFile(new File(inputFile, children[i]), new File(outputFile, children[i]), source_encoding, target_encoding);
			}
		}else{
			
			try {
				srcbuffer = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile), javaname[source_encoding]));
				outbuffer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(outputFile), javaname[target_encoding]));
				while ((dataline = srcbuffer.readLine()) != null) {
					outbuffer.write(convertString(dataline, source_encoding, target_encoding));
					outbuffer.newLine();
				}
				srcbuffer.close();
				outbuffer.close();
			}
			catch (Exception ex) {
        ex.printStackTrace();
				System.err.println(ex);
			}
			
		}
	}
	
	private static void printHelpMsg(){
		System.out.println("Usage:  java $ThisClass$ -[gbc8ui2nkr][gbc8uts2nkr] in_file out_file");
		System.out.println("  g = GB2312, b = Big5, c = CNS11643, 8 = UTF-8, 6 = UTF-8 (simp),");
		System.out.println("  7 = UTF-8 (trad), u = Unicode, t = Unicode (traditional characters),");
		System.out.println("  s = Unicode (simplified characters),");
		System.out.println("  i = ISO-2022-CN, 2 = ISO-2022-CN-GB, n = ISO-2022-CN-CNS,");
		System.out.println("  k = GBK, h = HZ, r = EUC-KR");
	}
	

	public static void run(String argc[]) {
		int codetypes[];
		char codetype;
		Vector inputfiles = new Vector();
		EncodingConverter zhcoder = new EncodingConverter();
		if(argc.length == 0){
			printHelpMsg();
			FSystem.dieShouldNotHappen();
		}
		
		
		// Determine source and target encodings, store in codetypes
		codetypes = new int[2];
		argc[0] = argc[0].toLowerCase();
		for (int i = 0; i < 2; i++) {
			codetype = argc[0].charAt(i+1);
			// Print Help
			if (codetype == 'h') {
				printHelpMsg();
				FSystem.dieShouldNotHappen();
			}
			
			if (codetype == 'g') {
				codetypes[i] = GB2312;
			} else if (codetype == 'h') {
				codetypes[i] = HZ;
			} else if (codetype == 'b') {
				codetypes[i] = BIG5;
			} else if (codetype == 'c') {
				codetypes[i] = CNS11643;
			} else if (codetype == '8') {
				codetypes[i] = UTF8;
			} else if (codetype == '6') {
				codetypes[i] = UTF8S;
			} else if (codetype == '7') {
				codetypes[i] = UTF8T;
			} else if (codetype == 'u') {
				codetypes[i] = UNICODE;
			} else if (codetype == 't') {
				codetypes[i] = UNICODET;
			} else if (codetype == 's') {
				codetypes[i] = UNICODES;
			} else if (codetype == 'i') {
				codetypes[i] = ISO2022CN;
			} else if (codetype == '2') {
				codetypes[i] = ISO2022CN_GB;
			} else if (codetype == 'n') {
				codetypes[i] = ISO2022CN_CNS;
			} else if (codetype == 'k') {
				codetypes[i] = GBK;
			} else if (codetype == 'r') {
				codetypes[i] = EUC_KR;
			};
		}
		
		// Call the file convert function with appropriate arguments
		zhcoder.convertFile(new File(argc[1]), new File(argc[2]), codetypes[0], codetypes[1]);
	}
	
	public static void main(String argc[]) {
		run(argc);
	}	
}
