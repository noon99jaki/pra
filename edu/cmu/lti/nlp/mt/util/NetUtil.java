/*Frank Lin
 *
 *This is a class of useful static methods for net-related tasks
 */

package edu.cmu.lti.nlp.mt.util;

import java.net.*;
import java.io.*;

public class NetUtil{

	public static final String DEFAULT_ENCODING="UTF-8";
	public static final String DEFAULT_USERAGENT="Mozilla/4.0 (compatible; MSIE 6.0)";
	public static final int DEFAULT_TIMEOUT=10000;

	public static String getPage(String urlString,String encoding,int timeout,String useragent){
		try{
			InputStreamReader pageReader=getPageReader(urlString,encoding,timeout,useragent);
			StringBuilder b=new StringBuilder();
			BufferedReader reader=new BufferedReader(pageReader);
			for(String nextLine;(nextLine=reader.readLine())!=null;){
				b.append(nextLine+"\n");
			}
			reader.close();
			return b.toString();
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static String getPage(String urlString,String encoding,int timeout){
		return getPage(urlString,encoding,timeout,DEFAULT_USERAGENT);
	}

	public static String getPage(String urlString,String encoding){
		return getPage(urlString,encoding,DEFAULT_TIMEOUT);
	}

	public static String getPage(String urlString){
		return getPage(urlString,DEFAULT_ENCODING);
	}

	public static InputStreamReader getPageReader(String urlString,String encoding,int timeout,String useragent){
		try{
			URL url=new URL(urlString);
			URLConnection connection=url.openConnection();
			connection.setConnectTimeout(timeout);
			connection.setReadTimeout(timeout);
			connection.setRequestProperty("User-Agent",useragent);
			connection.setRequestProperty("Accept-Charset",encoding);
			connection.connect();
			return new InputStreamReader(connection.getInputStream(),encoding);
		}
		catch(Exception e){
			e.printStackTrace();
			return null;
		}
	}

	public static InputStreamReader getPageReader(String urlString,String encoding,int timeout){
		return getPageReader(urlString,encoding,timeout,DEFAULT_USERAGENT);
	}
	
	public static InputStreamReader getPageReader(String urlString,String encoding){
		return getPageReader(urlString,encoding,DEFAULT_TIMEOUT);
	}
	
	public static InputStreamReader getPageReader(String urlString){
		return getPageReader(urlString,DEFAULT_ENCODING);
	}
	
}
