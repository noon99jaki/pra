/**
 * 
 */
package edu.cmu.lti.algorithm.container;


/**
 * @author nlao
 *
 */
public class VecVecX<V> extends VectorX<VectorX<V>>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VecVecX<V> newInstance(){
		return new VecVecX<V>(c);
	}		
	public VectorX<V> newValue(){
		return new VectorX<V>(c);
	}		
	public VecVecX(Class c){
		super( (new VectorX<V>(c)).getClass());
	}

	public V get(int i, int j) {
		return get(i).get(j);		
	}	
	public void setSize(int m, int n){
		this.setSize(m);
		for (int i=0;i<m; ++i){
			VectorX<V> v=newValue();
			v.setSize(n);
			this.set(i,v);
		}
	}
	public void setSizeLowerTriangle(int n){
		this.setSize(n);
		for (int i=1;i<=n; ++i){
			VectorX<V> v=newValue();
			v.setSize(i);
			this.set(i-1,v);
		}
	}
}
