/**
 * 
 */
package edu.cmu.lti.algorithm.container;

import java.util.ArrayList;
import java.util.Map;
import java.util.Set;

import edu.cmu.lti.algorithm.Interfaces.CTag;
import edu.cmu.lti.util.system.FSystem;

/**
 * @author nlao
 *	a matrix consists of column vectors
 */
public class VecMapID extends VectorX<MapID> { 
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public VecMapID newInstance(){
		return new VecMapID();
	}	

	public VecMapID(){
		super(MapID.class);
	}	
	public VecMapID(int n){
		super(n, MapID.class);
	}	
	public MapID  merge(){		
		MapID  v = new MapID();
		for ( MapID m : this ) 
			v.plusOn(m);		
		return v;
	}

	/**
	 * each MapID_i is treated as a distribution p(X_i=x)
	 * @return p(sum_i(X_i)=x)
	 */
	public MapID  convolve(){		
		MapID  v = new MapID();
		v.put(0, 1.0);
		for ( MapID m : this ) 
			v= v.convolve(m);				
		return v;
	}
	public Double get(int i, int j){
		return getE(i).get(j);
	}
	public Double set(int i, int j, Double x){
		return getE(i).put(j, x);
	}	
	public Double getC(int i, int j){
		return get(i).getC(j);
	}	
	public void plusOn(int i, int j, Double x){
		getE(i).plusOn(j,x);
	}		
	public VecMapID plusOn(VecMapID vm){
		for (int i=0; i<vm.size(); ++i){
			getE(i).plusOn(vm.get(i));
		}
		return this;
	}	
	public VecMapID plusOn(int i, MapXD<Integer> m){
		getE(i).plusOn(m);
		return this;
	}	
	public VecMapID minusOn(int i, MapXD<Integer> m){
		getE(i).minusOn(m);
		return this;
	}		
	public VecMapID plusOn( MapXD<Integer> m, int j){
		if (m==null) return this;
		for ( Map.Entry<Integer, Double> e : m.entrySet() ) {
			Integer k = e.getKey();
			Double x = e.getValue();
			getE(k).plusOn(j,x);
		}		
		return this;
	}			
	public VecMapID minusOn( MapXD<Integer> m, int j){
		if (m==null) return this;
		for ( Map.Entry<Integer, Double> e : m.entrySet() ) {
			Integer k = e.getKey();
			Double x = e.getValue();
			getE(k).minusOn(j,x);
		}		
		return this;
	}			
//	public void addOn(int i, int j, Double x){	extend(i).plusOn(j,x);	}
	public static VecMapID I(int p){
		VecMapID v= new VecMapID();
		v.init(p);//		v.ensureCapacity(p);
		for (int i=0;i<p; ++i)
			v.get(i).add(i);
		return v;
	}
	
	public VectorD multiply( MapID m){
		//MapID
		VectorD v = new VectorD();
		v.ensureCapacity(this.size());
		for ( MapID m1: this ) {
			v.add(m1.inner(m));
		}		
		return v;
	}	
	public VectorD multiply( VectorD m){
		//MapID
		VectorD v = new VectorD();
		v.ensureCapacity(this.size());
		for ( MapID m1: this ) {
			v.add(m1.dotProd(m));
		}		
		return v;
	}		
	public VectorD multiply(double[] m){
		VectorD v = new VectorD();
		v.ensureCapacity(this.size());
		for ( MapID m1: this ) 
			v.add(m1.inner(m));
		
		return v;
	}		
	
	public MapID weightedSum( VectorD weights){
		FSystem.checkVectorSizes(size(), weights.size());
		MapID v = new MapID();
		for (int i=0; i<size();++i) 
			v.plusOn(get(i),weights.get(i));		
		return v;
	}	
	
	public void weightedSum( VectorD weights, MapID v){
		FSystem.checkVectorSizes(size(), weights.size());
		v.setAll(0.0);
		for (int i=0; i<size();++i) 
			v.plusOn(get(i),weights.get(i));		
	}	
	
	public MapID weightedSum( double[] weights){
		FSystem.checkVectorSizes(size(), weights.length);
		MapID v = new MapID();
		for (int i=0; i<size();++i) 
			v.plusOn(get(i),weights[i]);		
		return v;
	}	
	
	public VectorD multiplySum( VecMapID vm){
		VectorD v = new VectorD();
		v.ensureCapacity(vm.size());
		for ( MapID m: vm ) {
			v.add( multiply(m).sum());
		}			
		return v;
	}
	
	public VecMapID multiply( VecMapID vm){
		VecMapID vm1 = new VecMapID();
		vm1.ensureCapacity(vm.size());
		for ( MapID m: vm ) 
			vm1.add((MapID) multiply(m).toMapNonZero());				
		return vm1;
	}
	
	public VecMapID sub(Set<Integer> vi) {
		return (VecMapID) super.sub(vi);
	}
	public VecMapID sub(ArrayList<Integer> vi) {
		return (VecMapID) super.sub(vi);
	}
	public VecMapID subRows(Set<Integer> vi) {
		VecMapID vm = new VecMapID();
		vm.ensureCapacity(size());
		for ( MapID m: this ) {
			vm.add((MapID) m.sub(vi));
		}			
		return vm;
	}
	public VecMapID subRows(ArrayList<Integer> vi) {
		VecMapID vm = new VecMapID();
		vm.ensureCapacity(size());
		for ( MapID m: this ) {
			vm.add((MapID) m.sub(vi));
		}			
		return vm;
	}
	/** Sherman-Morrison updating formula
	 * replace the iTH column of C matrix with new vector vNew
	 * A'=A-a_i * dc'A /(1+dc'a_i)
	 * */
	public VecMapID SMUpdate(int i, MapID vOld, MapID vNew){
		MapID dc= vNew.minus(vOld);
		MapID ai=(MapID) get(i).clone();
		double under = 1+dc.inner(ai);
		this.minusOuterOn(multiply(dc),ai.devide(under));
		return this;
	}
	
	/**swap column i1 and i2*/
	public VecMapID swap(int i1, int i2){
		return (VecMapID) super.swap(i1, i2);
	}
	
	public VecMapID plusOuterOn(ArrayList< Double> v, MapID m) {
		for (int i=0; i<v.size();++i){
			Double x = v.get(i);
			if (x!=0.0) 
				this.get(i).plusOn(m,x);
		}
		return this;
	}	
	public VecMapID minusOuterOn(ArrayList< Double> v,MapID m) {
		for (int i=0; i<v.size();++i){
			Double x = v.get(i);
			if (x!=0.0) 
				this.get(i).plusOn(m,-x);
		}
		return this;
	}	
	
	public VecMapID plusOuterOn(MapID m1, MapID m2) {
		for (Map.Entry<Integer, Double> e: m1.entrySet())
			getE(e.getKey()).plusOn(m2,e.getValue());		
		return this;
	}	
	public VecMapID plusOuterOn(MapID m1,VectorI vidx1
			, MapID m2, VectorI vidx2) {
		if (m1.size()==0 || m2.size()==0)
			return this;
		for (Map.Entry<Integer, Double> e: m1.entrySet()){
			int k=vidx1.get(e.getKey());
			getE(k).plusOn(m2,vidx2,e.getValue());		
		}
		return this;
	}	
	
	/*	public VectorD getVectorSum(){
	VectorD v = new VectorD();//size());
	v.ensureCapacity(size());		
	for ( MapID m : this ) {
		v.add(m.sum());
	}		
	return v;
}*/
	public VecMapID catOn(VecMapID v)	{
		return (VecMapID) super.catOn(v);
	}
	public VecMapID upperTrinagleOn(){
		VectorI vd= new VectorI();
		for (int i=0; i<this.size(); ++i){
			MapID m = get(i);
			if (m.size()==0) continue;
			vd.clear();
			for (int id: m.keySet()){
				if (id>=i) break;
				getE(id).plusOn(i, m.get(id));		
				vd.add(id);
			}
			for (int id: vd)
				m.remove(id);
			/*vd.addAll(m.keySet());
			for (int id: vd)
				if (id<i){
					getE(id).plusOn(i, m.get(id));
					m.remove(id);
				}*/
		}
		return this;
	}
	public int totalSize(){
		return getVI(CTag.size).sum();
	}
	public String toString(){
		VectorI vi = getVI(CTag.size);		
		return "#elements="+vi.sum()+"\n"+vi.join(",");
	}
	public void clearElements(){
		for (MapID m: this)
			m.clear();
	}
	public VectorD getRowV(int i){
		VectorD v= new VectorD();
		v.ensureCapacity(size());
		for (MapID m: this){
			Double d=m.get(i);
			if (d!=null) v.add(d);
			else v.add(0.0);
		}			
		return v;
	}
	public VecVecD getRowsVVNega(VectorI vi){
		VecVecD vv= new VecVecD();
		vv.ensureCapacity(vi.size());
		for (int i: vi)
			vv.add(getRowVNega(i));
		return vv;
	}
	public VecVecD getRowsVV(VectorI vi){
		VecVecD vv= new VecVecD();
		vv.ensureCapacity(vi.size());
		for (int i: vi)
			vv.add(getRowV(i));
		return vv;
	}
	public VectorD getRowVNega(int i){
		VectorD v= new VectorD();
		v.ensureCapacity(size());
		for (MapID m: this){
			Double d=m.get(i);
			if (d!=null) v.add(-d);
			else v.add(0.0);
		}			
		return v;
	}
	public MapID getRowMap(int i){
		MapID m= new MapID();
		for (int j=0; j<size(); ++j){
			Double d=get(j).get(i);
			if (d!=null) m.put(j,d);
		}			
		return m;
	}
	public MapID getRowMNega(int i){
		MapID m= new MapID();
		for (int j=0; j<size(); ++j){
			Double d=get(j).get(i);
			if (d!=null) m.put(j,-d);
		}			
		return m;
	}
	public VecMapID getRowsVM(VectorI vi){
		VecMapID vv= new VecMapID();
		vv.ensureCapacity(vi.size());
		for (int i: vi)
			vv.add(getRowMap(i));
		return vv;
	}
	public VecMapID getRowsVMNega(VectorI vi){
		VecMapID vv= new VecMapID();
		vv.ensureCapacity(vi.size());
		for (int i: vi)
			vv.add(getRowMNega(i));
		return vv;
	}
	public boolean containsKey(int key){
		for (MapID m: this)
			if (m.containsKey(key))
				return true;
		return false;
	}
	public MapID maxSum(){
		MapID rlt= new MapID();
		for (MapID m: this)
			rlt.maxOn(m);
		return rlt;
	}
}
