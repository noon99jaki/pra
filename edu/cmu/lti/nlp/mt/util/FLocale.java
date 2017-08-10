package edu.cmu.lti.nlp.mt.util;

import java.util.*;

public class FLocale{
	
	public static Locale en_US=new Locale("en","US");
	public static Locale zh_CN=new Locale("zh","CN");
	public static Locale zh_TW=new Locale("zh","TW");
	public static Locale ja_JP=new Locale("ja","JP");
	public static Locale ko_KR=new Locale("ko","KR");
	
	public static Locale parseLocale(String language){
		String[] parts=language.split("_");
		if(parts.length==1){
			return new Locale(parts[0]);
		}
		else if(parts.length==2){
			return new Locale(parts[0],parts[1]);
		}
		else if(parts.length==3){
			return new Locale(parts[0],parts[1],parts[2]);
		}
		else{
			return null;
		}
	}

}
