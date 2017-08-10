package edu.cmu.lti.algorithm.container;

import java.util.TreeMap;

import edu.cmu.lti.util.system.FSystem;


public class Test {
	public static class MapIF extends MapXX<Integer, Float>{
		public MapIF(){
			super(Integer.class, Float.class);
		}
	}
	

	public static class TList<K, V>{
		public Class ck=Object.class;
		public Class cx=Object.class;
		VectorX<K> vk;
		VectorX<V> vx;
		public TList(Class ck, Class cx){
			this.ck = ck; 
			this.cx = cx;
			vk= new VectorX<K>(ck);
			vx= new VectorX<V>(cx);
		}
		public void append(K k, V x){
			vk.add(k);
			vx.add(x);
		}
	}
	public static class ListIF extends TList<Integer, Float>{
		public ListIF(){
			super(Integer.class, Float.class);
		}
	}
	
	public static void testMemory1(){
		int n= 10000000;
		
		long m0=FSystem.memoryUsedM();
		MapID md= new MapID();
		for (int i=0; i<n; ++i)
			md.put(i,0.0);
		
		long m1=FSystem.memoryUsedM();
		MapIF mf= new MapIF();
		for (int i=0; i<n; ++i)
			mf.put(i,0.0f);

		long m2=FSystem.memoryUsedM();
		ListIF lf= new ListIF();
		for (int i=0; i<n; ++i)
			lf.append(i,0.0f);

		long m3=FSystem.memoryUsedM();
		System.out.println(String.format(
			"md=%d, mf=%d, lf=%d"
			, m1-m0, m2-m1, m3-m2));
		
		FSystem.memoryUsage();
	}
	static int n= 1000000;
	public static void testMemoryMD(){
		
		long m0=FSystem.memoryUsedM();
		
		MapID md= new MapID();
		for (int i=0; i<n; ++i)
			md.put(i,0.0);
		System.out.println(FSystem.memoryUsage());
		//used(114M)=total(221M)-free(108M), max(899M)
	}
	public static void testMemoryMF(){
		
		long m0=FSystem.memoryUsedM();
		
		MapIF md= new MapIF();
		for (int i=0; i<n; ++i)
			md.put(i,0.0f);
		System.out.println(FSystem.memoryUsage());
		//used(113M)=total(221M)-free(109M), max(899M)
	}
	public static void testMemoryLF(){
		
		long m0=FSystem.memoryUsedM();
		
		ListIF lf= new ListIF();
		for (int i=0; i<n; ++i)
			lf.append(i,0.0f);
		System.out.println(FSystem.memoryUsage());
		//used(113M)=total(221M)-free(109M), max(899M)
	}
	public static void main(String[] args) {
		testMemoryLF();
		//used(71M)=total(187M)-free(116M), max(899M)
		try {

		} catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
