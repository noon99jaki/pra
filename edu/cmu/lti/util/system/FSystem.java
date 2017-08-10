/**
 * 
 */
package edu.cmu.lti.util.system;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao
 *
 */
public class FSystem {


	public static String cmd(String cmd) {
		return cmd(cmd, null);
	}
	public static String cmd(String cmd, String dir) {
		return cmd(cmd, dir, Long.MAX_VALUE); //Integer.MAX_VALUE);//
	}
	public static boolean silent_cmd=true;
	public static String cmd(String cmd, String dir, long timeout) {
		if (!silent_cmd)		System.out.println("cmd=" + cmd + " @"+dir);
//		System.out.println("dir=" + dir);
		
		try {
			File folder = dir!=null ? new File(dir): null;
			
			Process child = Runtime.getRuntime().exec(cmd, null, folder	);
			String cout = FString.inputStream2String(child.getInputStream());
			String err = FString.inputStream2String(child.getErrorStream());
			System.out.println(err);
			child.waitFor();
			//child.wait(timeout);// not working
			child.destroy();
			return cout;
		} catch (Exception e) {//IOException e
			System.err.println(e.getClass().getName());
			Throwable cause = e.getCause();
			if (cause!=null) 		System.err.println(cause.getMessage());
			e.printStackTrace();
			return "";
		}
	}
	
	public static void cmdNoReturn(String cmd, String output) {
		System.out.println("cmd=" + cmd);
		
    try {
      Runtime.getRuntime();
      //Process pr = rt.exec("cmd /c dir");
      Process pr = Runtime.getRuntime().exec(cmd);

      BufferedReader input = new BufferedReader(new InputStreamReader(pr.getInputStream()));

      BufferedWriter bw = FFile.newWriter(output);
      String line=null;
      while((line=input.readLine()) != null)    	FFile.writeln(bw,line);
      FFile.close(bw);

      int exitVal = pr.waitFor();
      System.out.println("Exited with error code "+exitVal);
	
	  } catch(Exception e) {
	      System.out.println(e.toString());
	      e.printStackTrace();
	  }
	}
	

	public static BufferedReader cmdGetStream(String cmd) {
		try {
			Process child = Runtime.getRuntime().exec(cmd, null, null);
			//InputStream is = child.getInputStream();
			return new BufferedReader(new InputStreamReader(child.getInputStream()));
		} catch (Exception e) {//IOException e
			System.err.println(e.getClass().getName());
			System.err.println(e.getCause().getMessage());
			e.printStackTrace();
			return null;
		}
	}

	public static String methodName() {
		//return  new System.Diagnostics.StackTrace().GetFrame(0).GetMethod().Name;
		return null;
	}

	//extends String {
	//left()
	//right();

	public static String readLine() {
		String s = null;
		try {
			s = (new BufferedReader(new InputStreamReader(System.in))).readLine();
		} catch (IOException ioe) {
			FSystem.die("IO error trying to read from keyboard");
		}
		return s;
	}

	/**
	 	
	Format Pattern 	Result
	"yyyy.MM.dd G 'at' hh:mm:ss z"	
	1996.07.10 AD at 15:08:56 PDT

	"EEE, MMM d, ''yy" 	
	Wed, July 10, '96

	"h:mm a" 	
	12:08 PM

	"hh 'o''''clock' a, zzzz" 	
	12 o'clock PM, Pacific Daylight Time

	"K:mm a, z" 	
	0:00 PM, PST

	"yyyyy.MMMMM.dd GGG hh:mm aaa"	
	1996.July.10 AD 12:08 PM
	 */
	public static String formatDate1(Date date, String format) {
		SimpleDateFormat df = new SimpleDateFormat(format);
		///usr2/local/jdk1.5.0_07/jre/lib/zi/US/Eastern
		//TimeZone tz = TimeZone.getTimeZone("ECT");
		//TimeZone tz = TimeZone.getTimeZone("GMT");
		return df.format(date);
	}

	//DateFormat df =  (SimpleDateFormat)DateFormat.getDateTimeInstance(
	//DateFormat.LONG,	 DateFormat.LONG, Locale.US);
	//df.setTimeZone(tz);
	//numerical time format
	public static String printTimeNumeric() {
		return (new SimpleDateFormat("yyyyMMdd_HHmmss"))//"EEE MMM dd HH:mm:ss zzz yyyy"
				.format(new Date());
	}
	
	public static String printTimeShort() {
		return (new SimpleDateFormat("yyyy.MM.dd HH:mm:ss"))
				.format(new Date());
	}

	public static String printTime() {
		return (new Date()).toString();
	}

	public static void printMemoryTime() {
		System.out.println(" mem=" + printMemoryUsageG() + " time=" + printTimeShort() );
	}

	public static String memoryUsage() {
		long free = Runtime.getRuntime().freeMemory() / 1000000;
		long total = Runtime.getRuntime().totalMemory() / 1000000;
		long max = Runtime.getRuntime().maxMemory() / 1000000;
		long used = total - free;
		return String.format("used(%dM)=total(%dM)-free(%dM), max(%dM)",
				used, total, free, max);
	}

	public static int memoryUsedM() {//in mb
		long free = Runtime.getRuntime().freeMemory() / 1000000;
		long total = Runtime.getRuntime().totalMemory() / 1000000;
		long used = total - free;
		return (int) used;
	}
	
	public static int memoryFreeM() {//in mb
		return  (int) Runtime.getRuntime()	.freeMemory() / 1000000;
	}
	public static double memoryUsedG() {//in gb
		return memoryUsedM() / 1000.0;
	}
	public static String printMemoryUsageG() {
		return String.format("%.1fG", memoryUsedG());
	}

	public static String printMemoryUsageM() {
		return memoryUsedM() +"M";
	}

	public static String printMemoryUsageRate() {//rate
		double max = Runtime.getRuntime().maxMemory() / 1000000.0;
		double used = memoryUsedM();
		return String.format("%.0fM/%.0fM", used, max);
	}

	public static double memoryRate() {
		double max = Runtime.getRuntime().maxMemory() / 1000000.0;
		return memoryUsedM() / max;
	}

	public static String formatTime(long sec) {
		long min = sec / 60;
		long hour = min / 60;
		long day = hour / 24;
		return String.format("%ds=%dm=%dh=%dd", sec, min, hour, day);
	}

	public static void sleep(long milSec) {
		try {
			Thread.sleep(milSec);
		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}

	public static void die(String msg) {
		System.err.println(msg);
		for (StackTraceElement e : Thread.currentThread().getStackTrace())
			System.err.println("\t" + e.toString());
		throw new IllegalStateException(msg);
		//System.exit(-1);
	}

	public static void dieNotImplemented() {
		die("not implemented");
	}

	public static void dieShouldNotHappen() {
		die("This should not happen");
	}

	public static void checkVectorSizes(int expected, int actual) {
		if (expected != actual)
			die("unmatched vector dimensions " 
					+ expected + "(expected) vs."
					+ actual + "(actual)");
	}
	public static void checkTrue(boolean value, String msg) {
		if (!value)	die(msg);
	}
	public static void checkTrue(boolean value) {
		if (!value)	dieShouldNotHappen();
	}
	
	public static void _tmp2() {
		//The following will get you a string that contains the full path
		//to the users working directory
		String wd = System.getProperty("user.dir");
		//The following will get you a string that contains the full path
		//to the users home directory
		String home = System.getProperty("user.home");
		//It is customary to put the config file in the users home directory.
		//Also, don't forget to use the platform independent separator
		//character which is "\" for windows and "/" for linux, unix
		//Here is how to get the separator
		String fs = System.getProperty("file.separator");
		//Or you can create a File object like so
		File configfile = new File(home, ".config");
		//where home is the string from above and ".config" is
		//the name of the config file you want to use		
	}

	public static String getPWD() {
		return System.getProperty("user.dir");
	}

	public static String getHomeDir() {
		return System.getProperty("user.home");
	}

	public static String getProcessName() {
		return System.getProperty("user.dir");
	}

	public static BufferedReader getStdIn() {
		try {
			return new BufferedReader(new InputStreamReader(System.in, "UTF8"));
		} catch (Exception e) {
			System.err.println("cannot open stdIn");
			return null;
		}
	}

	public static BufferedReader brSTD = null;

	public static String readSTDIN() {
		if (brSTD == null) brSTD = new BufferedReader(new InputStreamReader(
				System.in));
		try {
			return brSTD.readLine();
		} catch (Exception e) {
			System.err.println("cannot read stdIn");
			return null;
		}
	}
}
