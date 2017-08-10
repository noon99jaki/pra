/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.ArrayList;


/**
 * @author nlao
 *This class is an extension to Vector&lt;Boolean&gt;
 */
public class VectorB  extends VectorX<Boolean>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Boolean newValue(){//weakness of Java template
		return false;
	}	
	public VectorB newInstance(){
		return new VectorB();
	}		
	
	public VectorB(Boolean[] v) {
		super(v);
	}
	public VectorB(VectorX<Boolean> v) {
		super(v, Boolean.class);
	}	
	public VectorB(int n){
		super(n, false);//, Integer.class);
	}
	public VectorB(int n, boolean x){
		super(n,x);
	}	
	public VectorB(){
		super(Boolean.class);
	}
	public boolean and() {
		boolean a=true;
		for (int i = 0; i < size(); ++i){
			if (get(i)==null){	a=false; break;	}
			if (get(i)==false){
				a=false; break;
			}			
		}
		return a;
	}	
	public VectorB sub(ArrayList<Integer> vi) {
		VectorB v=new  VectorB(super.sub(vi));
		return v;
	}		
}
