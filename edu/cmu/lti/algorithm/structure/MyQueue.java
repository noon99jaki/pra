package edu.cmu.lti.algorithm.structure;

import java.util.ArrayList;
import java.util.List;

public class MyQueue <A>{
  private ArrayList<A> list = new ArrayList<A>();

  public A push(A item){
    list.add(item);
    return item;
  }
  public A add(A item){
  	return push(item);
  }
  public boolean empty(){
    return list.isEmpty();
  }

  public int size(){
    return list.size();
  }

  public A peek(){
    if(list.isEmpty())
      return null;
    return list.get(0);
  }

  /**
  * Look at the item at index counting from the top
  */
  public A peek(int index){
    if(list.size() < index+1)
      return null;
    return list.get(index);
  }

  public A poll(){
    if(list.isEmpty())
      return null;
    return list.remove(0);
  }
  
  public List<A> asList(){
    return list;
  }
  
}
