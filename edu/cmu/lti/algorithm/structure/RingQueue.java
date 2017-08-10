package edu.cmu.lti.algorithm.structure;

import edu.cmu.lti.algorithm.container.VectorI;

/**
 * a queue with fixed memory usage
 * @author nlao
 *
 */
public class RingQueue {
	VectorI v=new VectorI();
	Integer head=null;//position of the first element
	Integer tail=null;//position after the last element
	public RingQueue(){
		//reset(0);
	}
	public void reset(int size){
		v.setSize(size);
		//v.setAll(0)
		head=0;
		tail=0;
		bFull=false;
	}
	public boolean bFull;
	
	public boolean push(int id){
		if (bFull)	return false;
		v.set(tail,id);
		++tail; if (tail==v.size()) tail=0;
		if (head==tail)//queue is full
			return bFull=true;
		return true;
	}
	public Integer pull(){
		if (head==tail){
			if (bFull)
				bFull=false;
			else	//empty
				return null;	
		}
		Integer id=v.get(head);
		++head; if (head==v.size()) head=0;
		return id;
	}
}
