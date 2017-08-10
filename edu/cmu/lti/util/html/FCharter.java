/**
 * 
 */
package edu.cmu.lti.util.html;

import java.util.Map;

import edu.cmu.lti.algorithm.container.MapIS;
import edu.cmu.lti.algorithm.container.MapIMapIS;
import edu.cmu.lti.algorithm.container.MapISetI;
import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.MapIX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VecVecI;

/**
 * @author nlao
 * this class arrange blocks into a width-fixed table
 */
public class FCharter{
	private int chartWidth;
	VecVecI vvi= new VecVecI();
	
	public FCharter(int chartWidth){
		this.chartWidth = chartWidth;			
	}
	
	/**
	 * sample:
	 * |AA|  |B|CCC| |  becomes
	 *  2, -1, 0, 0, 1, 3,-1,-1, 0
	 * @param mm
	 * @return: vvi: each cell point to its text
	 */
	public VecVecI doChart(MapISetI mm ){
		for (Map.Entry<Integer, SetI> e: mm.entrySet()){
			int len = e.getKey();
			for (Integer ib: e.getValue())
				insert(len, ib);
		}
		return vvi;
	}
	
	public VecVecI doChart(MapIMapIS mms ){
		for (Map.Entry<Integer, MapIX<String> > e: mms.entrySet()){
			int len = e.getKey();
			for (Integer ib: e.getValue().keySet())
				insert(len, ib);
		}
		return vvi;
	}
	
	private void insert(int len, int ib ){
		for (VectorI vi: vvi){
			if(tryInsert(len, ib, vi)) 
				return;
		}
		VectorI vi = new VectorI(chartWidth,0);
		vvi.add(vi);
		insert( len, ib,  vi);
	}
	private boolean tryInsert(int len, int ib, VectorI vi){
		for (int i=ib; i< ib+len; ++i)
			if (vi.get(i)!=0)
				return false;
		insert( len, ib,  vi);		
		return true;
	}
	private void insert(int len, int ib, VectorI vi){
		vi.setRange(ib, ib+len, -1);
		vi.set(ib, len);
	}	
}