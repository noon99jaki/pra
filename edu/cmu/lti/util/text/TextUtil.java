package edu.cmu.lti.util.text;

public class TextUtil {
	
	public static boolean isSubstring( String s1, String s2 ) {
		try {
			if (s1 == null || s1.length()==0) return false;
			if (s2 == null || s2.length()==0) return false;
			
			return (s2.matches(".*"+s1+".*") || s1.matches(".*"+s2+".*"));
		} catch (Exception e) {
			System.err.println("Error! arg0: "+s1+", arg1: "+s2);
			return false;
		}
	}
	
	public static double coverage( String s1, String s2 ) {
		try {
			if (s1 == null || s1.length()==0) return 0;
			if (s2 == null || s2.length()==0) return 0;
			
			double c1 = 1.0D - (double)s1.replaceAll("["+s2+"]", "").length()/(double)s1.length();
			double c2 = 1.0D - (double)s2.replaceAll("["+s1+"]", "").length()/(double)s2.length();
			
			return c1*c2;
		} catch (Exception e) {
			System.err.println("Error! arg0: "+s1+", arg1: "+s2);
			e.printStackTrace();
			return 0;
		}
	}
	
	public static String padBack(String text, int length) {
		return padBack( text, length, " " );
	}
	
	public static String padBack(String text, int length, String padding) {
		if (text == null) {
			return padBack("null",length);
		}
		if (text.length()>=length) {
			return text;
		} else {
			StringBuilder sb = new StringBuilder(text);
			for (int i=text.length(); i<length; i++) {
				sb.append(padding);
			}
			text = sb.toString();
		}
		return text;
	}

	public static String padBack( int num, int length ) {
		return padBack(num+"", length);
	}
	
	public static String padBack( double num, int length ) {
		return padBack(num+"", length);
	}
	
	public static String padFront( String text, int length ) {
		return padFront( text, length, " " );
	}
	
	public static String padFront( String text, int length, String padding ) {
		if (text.length()>=length) {
			return text;
		} else {
			StringBuilder sb = new StringBuilder();
			for (int i=text.length(); i<length; i++) {
				sb.append(padding);
			}
			sb.append(text);
			text = sb.toString();
		}
		return text;
	}
		
	public static String padFront( int num, int length ) {
		return padFront(num+"", length);
	}
	
	public static String padFront( double num, int length ) {
		return padFront(num+"", length);
	}
}
