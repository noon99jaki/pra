package edu.cmu.lti.algorithm.math.rand;
import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
/**
 * multinomial distribution over a set of integers
 * @author nlao
 */
public class MultinomI extends MultinomN{// implements IDrawRandV{
	public int draw(){
		return vi.get(super.draw());
	}
	public VectorI vi;	
	
	public MultinomI(MapID mDist){
		this( mDist.toVectorKey(),  mDist.ValuesToVector());	
	}
	
	public MultinomI(VectorI vi, VectorD vd	){
		super(vd);
		this.vi= vi;
	}	
	
}
