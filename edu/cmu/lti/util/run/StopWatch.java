package edu.cmu.lti.util.run;

/**
 * Manages time elapsed in a certain module.
  * @author ni lao
 */


import java.io.Serializable;
import java.text.Format;

import edu.cmu.lti.util.system.FSystem;

public class StopWatch implements Serializable {

	public static final long serialVersionUID = 20061207L; // YYYYMMDD

	private Long start_time_;
	//private Long elapsedTime;
	
	public String currentHour(){		
		return String.format("%.2fh", getSec()/3600 );		
	}
	
	public StopWatch() {
	//	startTime   = new LinkedHashMap<String, Long>();
//		elapsedTime = new LinkedHashMap<String, Long>();
		start();
	}	

	public void start() {
		start_time_=System.currentTimeMillis();
	}

	public long getMilSec() {
		return System.currentTimeMillis() -start_time_;
	}
	public String  stopSec3D( ) {
		return String.format("%.3f",getSec());
	}
	

	public double getSec() {
		return getMilSec() / 1000.0f;
	}
	public int getSecI() {
		return (int) (getMilSec() / 1000.0f);
	}
	
	public void printTime(String task){
		System.out.println(task + " take time= "+msec2FString(getMilSec()));
	}
	
	public String msec2FString( long time ) {
		double ss=  (time/1000.0f);
		double mm = ss/60;
		double hh = mm/60;
		return String.format("%.1fh=%.1fm=%.1fs"	,hh,mm,ss);		
		//return formatter.format(new java.util.Date(time));
	}
	
	/*
	 * 		double ss = ( sec %60); 	sec/=60;
		double mm = ( sec % 60 ); sec/=60;
		double hh = ( sec  );
		return hh+"="+mm+"="+ss;
	 */
	public String msec2string( long time ) {
		return String.format("%.1fs", ((float)time)/1000);
	}
	public Format formatter=new java.text.SimpleDateFormat("hh:mm:ss");
	
	
	public static void main(String[] args) {
		StopWatch sw= new StopWatch();
		FSystem.sleep(1000);
		sw.printTime("sleep");

		
	}
}
