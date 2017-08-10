package edu.cmu.lti.algorithm.learning.data;

import java.io.Serializable;

import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;

public class Index implements Serializable{
	private static final long serialVersionUID = 2008042701L; 
	
	/*want to move these functions to TMap?
	 * seem a little too specific 
	 * lets be lazy*/
	public MapSI m =new MapSI();//feature name--> ID
	public VectorS v=new VectorS();//list of feature names
	public VectorI vn=new VectorI();//feature frequence
	/**	 * 
	 * @param n: minimun number of occurance
	 */
	public VectorI shrinkInfrequentFeature(int n){
		VectorI vidx = vn.idxLargerThan(n);
		VectorI vi = vidx.reversIdx(v.size());
		v=v.sub(vidx);
		vn= vn.sub(vidx);
		m= (MapSI) m.sub(v);
		return vi;
	}
	public int index(String key){
		int id=-1;
		if (m.containsKey(key)){
			id= m.get(key);
			vn.set(id, vn.get(id)+1);
		}
		else{
			id = m.size();
			m.put(key,id);
			v.add(key);
			vn.add(1);
		}		
		return id;
	}
	public SetI index(SetS m){
		SetI m1 = new SetI();
		for (String key: m)
			m1.add(index(key));
		return m1;
	}
	
	public int map(String key){
		if (m.containsKey(key))
			return m.get(key);
		return -1;
	}
	public SetI map(SetS m){
		SetI m1 = new SetI();
		for (String key: m)
			m1.add(index(key));
		return m1;
	}
}
