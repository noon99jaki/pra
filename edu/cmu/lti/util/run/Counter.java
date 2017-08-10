package edu.cmu.lti.util.run;

import edu.cmu.lti.util.system.FSystem;

public class Counter {
	public Counter parent=null;
	public Counter(int interval, char signal, Counter parent){
		this.interval_=interval;
		this.signal_=signal;
		this.parent=parent;
	}
	public Counter(int nInterval, char c){
		this.interval_=nInterval;
		this.signal_=c;
	}
	public Counter(int nInterval){
		this(nInterval, '#');
	}
	
	public int interval_;
	public int count_=0;
	public char signal_;
	
	public synchronized boolean step(){
		++count_;
		if (count_ % interval_!=0) return false;
		
		if (signal_=='#') {
			System.out.print(" "+count_ + " "); //+" "+FSystem.memoryUsageM());
			FSystem.printMemoryTime();
		}
		else if  (signal_=='\\'); 
		else	System.out.print(signal_);
		
		if (parent!=null)		parent.step();
		
		return true;
	}
	public boolean  stepDot(){
		return step('.');
	}
	public boolean  step(char signal){
		System.out.print(signal);
		return step();
	}
	public void clear(){
		count_=0;
	}
	
	public static Counter count50= new Counter(50);//,'\n');
	public static Counter count100= new Counter(100,'h');
	public static Counter count10= new Counter(10,'t');

}
