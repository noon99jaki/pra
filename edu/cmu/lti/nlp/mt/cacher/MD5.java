/**
 * 
 */
package edu.cmu.lti.nlp.mt.cacher;
import java.security.*;
import edu.cmu.lti.util.*;
import edu.cmu.lti.util.text.FString;
/**
 * @author nlao
 *
 */
public class MD5 {	
	public static MessageDigest md5;
	static {
		try{
			md5= MessageDigest.getInstance("MD5");
		}
		catch(Exception e) {
			
		}
	}
	
	public static String hash(String txt){
		byte vb[]=hash(txt.getBytes());
//		return vb.toString();
		return FString.byteToHex(vb);
	}
	public static byte[] hash(byte[] buff){	
		md5.reset();
		md5.update(buff);
		return md5.digest();
	}
}
