package edu.cmu.lti.algorithm.structure;

import java.util.ArrayList;

public class MyStack <A>{
  private ArrayList<A> list = new ArrayList<A>();

  public A push(A item){
    list.add(item);
    return item;
  }
  public A add(A item){
  	return push(item);
  }
  public boolean isEmpty(){
    return list.isEmpty();
  }

  public int size(){
    return list.size();
  }

  public A peek(){
	if (list.size()==0) return null;
    return list.get(list.size()-1);
  }

  /**
  * Look at the item at index counting from the top
  */
  public A peek(int index){
    if(list.size() < index+1)  return null;
    return list.get(list.size()-1-index);
  }

  public A pop(){
	if (list.size()==0) return null;
    return list.remove(list.size()-1);
  }
  
  public A[] popAll(A[] arr){
    return list.toArray(arr);
  }
  
}
