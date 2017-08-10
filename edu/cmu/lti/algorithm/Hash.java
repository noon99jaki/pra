/**
 * 
 */
package edu.cmu.lti.algorithm;
import java.security.*;
import edu.cmu.lti.util.*;
import edu.cmu.lti.util.text.FString;
/**
 * @author nlao
 *
 */
public class Hash {	
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
	
//public int hash(List list) {
//int hashCode = 1;
//Iterator<E> i = list.iterator();
//while (i.hasNext()) {
//    E obj = i.next();
//    hashCode = 31*hashCode + (obj==null ? 0 : obj.hashCode());
//}
//}
	
	public static int incrementalHash(int current_hash, int increamental_hash) {
		return 31*current_hash + increamental_hash;
	}
}
