/*
 * Frank Lin
 * 
 */

package edu.cmu.lti.nlp.mt.util;

import java.util.*;

public class ScoreTable<T>{

	private Map<T,Double> map;

	public ScoreTable(){
		map=new HashMap<T,Double>();
	}
	
	public int size(){
		return map.size();
	}

	public void set(T key,double score){
		map.put(key,score);
	}

	public double get(T key){
		return map.get(key);
	}

	public void increment(T key,double increment){
		if(map.containsKey(key)){
			set(key,get(key)+increment);
		}
		else{
			set(key,increment);
		}
	}

	public void increment(T key){
		increment(key,1);
	}
	
	public void mergeAdd(ScoreTable<T> other){
		for(T key:other.keySet()){
			if(map.containsKey(key)){
				map.put(key,get(key)+other.get(key));
			}
			else{
				map.put(key,other.get(key));
			}
		}
	}

	public Set<T> keySet(){
		return map.keySet();
	}

	public List<T> sortedKeys(boolean ascend){
		List<T> list=new ArrayList<T>(keySet());
		if(ascend){
			Collections.sort(list,new ScoreTableComparator());
		}
		else{
			Collections.sort(list,Collections.reverseOrder(new ScoreTableComparator()));
		}
		return list;
	}
	
	public List<T> sortedKeys(){
		return sortedKeys(false);
	}

	public double[] scores(){
		Collection<Double> values=map.values();
		double[] scores=new double[values.size()];
		int i=0;
		for(Double value:values){
			scores[i]=value;
			i++;
		}
		return scores;
	}

	public String toString(){
		return map.toString();
	}

	private class ScoreTableComparator implements Comparator<T>{
		public int compare(T key1,T key2){
			if(get(key1)>get(key2)){
				return 1;
			}
			else if(get(key2)>get(key1)){
				return -1;
			}
			else{
				return 0;
			}
		}
	}

}
