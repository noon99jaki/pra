package edu.cmu.lti.algorithm.learning.classifier;

import edu.cmu.lti.algorithm.container.MapID;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VecMapID;
import edu.cmu.lti.algorithm.learning.Learner;
import edu.cmu.lti.algorithm.learning.data.DataSet;
import edu.cmu.lti.algorithm.learning.data.DataSetBinaryI;
import edu.cmu.lti.algorithm.learning.data.Instance;


public class L1SVM extends ClassifierVote{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public Instance parseLine(String line){return null;};

	public static class Param extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public Param() {
			super(L1SVM.class);
			parse();
		}
		public void parse(){			
		}
	}	
	public Param p=new Param();
	
	//public static class C2L1SVM extends C2Linear{}
	
	/**
	 * find the solution path to the function
	 * L = sum_i( [1- Ins_i'w]_+ )
	 * with s=|w| gradually increasing
	 */
	public static class C2L1SVMTrainer{
		private static  C2L1SVMTrainer instance =null;
		public static C2L1SVMTrainer getInstance() {
			if (instance==null) 	 instance = new C2L1SVMTrainer();			
			return instance;
		}
		
		
		/**dimension of feature space*/
		public int p; 
		
		/**number of samples*/
		public int n; 
		
		/**current w*/
		//public VectorD w = new VectorD();
		public MapID w = new MapID();
		

		
		private void initSolution(){//is the origin
			viU=new VectorI();//.clear();		//no sample is selected
			viU_ = VectorI.seq(n);	//all samples are penalized
			vU = new VectorI(n,-1);
			
			viE = VectorI.seq(p);	//all features are selected
			viE_=new VectorI();//.clear();
			vE = new VectorI(p,0); //all E constraints activated

			viL = VectorI.seq(n);	//all samples are penalized		

			E = VecMapID.I(p);				
			A = VecMapID.I(p);	//initial directions are the axis			
			C = VecMapID.I(p);		
			
			//vA = 
			w.clear();	//start from origion			
			v_dsdw = VectorD.I(p);
			v_dsdt =(VectorD)	v_dsdw.clone();//.copy(v_dsdw);

			v_dLdw = U.sub(viL).sum().toVector(p);			
			v_dLdt = (VectorD)v_dLdw.clone();			
			
			v_dLds =v_dLdt.devide(v_dsdt);
			vA = v_dLds.signI();
		}		
		//private MapID dLdw = new MapID();
		
		boolean kickU;//kick U constraint or E constraint?
		int idxKick, iKick, ia;
		/**  */
		private boolean findBestDirection(){
			/**get dLds along candidate dimensions A*/
			//VectorD v_dLdsDim = new VectorD();
			//VectorD v_dLdsIns = new VectorD();			
			//VectorD v_dLds =v_dLdt.devide(v_dsdt);
			ia = v_dLds.idxMax();	//this is the best direction
			a = A.get(ia).multiply((double) vA.get(ia));			
			// this.viEA.contains(iKick)
			if (ia<viE.size()){//it is a E direction		
				kickU=false;
				idxKick = ia;
				iKick = viE.get(idxKick);
			}
			else{//it is a U direction		
				kickU=true;
				idxKick=ia-viE.size();
				iKick = viU.get(idxKick);
			}			
			return v_dLds.get(ia) > 0;
		}
		
		double t;
		MapID a;//the selected direction;
		boolean hitU;//hit U constraint or E constraint?
		int idxHit, iHit ;//the index of U/E matrix (sample id, or feature id)
		VectorD vRawLike;//untruncated loss, useful for calculating loss
		/**between w+a*t  and all the not seleted hyper-planes */
		private void findFirstInterception(){

			/**find interceptions t, with  iU_ (or iL?)
			 * u_i'(w_k + t_i*a_idx)=1
			 * therefore t_i = (1-u_i'*w_k) / u_i'*a_idx
			 * shut, java is much cumbersome than matlab
			 **/
			//VectorD upper = U_.multiply(w).minusOn(1.0).negativeOn();
			//vRawLoss=U.multiply(w).minusOn(1.0).negativeOn();
			VectorD upper = vRawLike.sub(viU_).negatOn();
			VectorD lower = U.sub(viU_).multiply(a);
			VectorD vt0 = upper.devide(lower);
			MapID mt0 = new MapID(); mt0.load(viU_,vt0);
			
			mt0=(MapID) mt0.subLargerThan(0.0);
			idxHit= mt0.idxMin();
			iHit = viU_.get(idxHit);
			t= mt0.get(idxHit);
			hitU=true;
			if (viE_.size()==0) return;
			
			/**find interceptions t, with  iE_ 
			 * e_i'(w_k + t_i*a_idx)=0
			 * therefore t_i = w_k_i / a_idx_i
			 **/
			MapID mt1 =(MapID)  w.sub(viE_).devide(a.sub(viE_));
			//VectorD vt1 = w.subV(viE_).devide(a.subV(viE_));
			
			mt1=(MapID) mt1.subLargerThan(0.0);
			if (mt1.size()==0) return;
			
			int i1=mt1.idxMin();
			double t1 = mt1.get(i1);
			
			if (t1 <t){	//an E constraint is selected
				hitU=false;
				t=t1;
				idxHit= i1;
				iHit = viE_.get(idxHit);
			}
			return;// -1;
		}		
		
		
		/** all the samples u_1..u_n*/
		public VecMapID U = new VecMapID();
		VecMapID E;//=VectorMapID.I(p);//the basis
		/**
		 * constraint matrix C=[E_miE|U_miU]'
		 * CA=I  : A is the inverse of C
		 * which is also the possible directions to update w */
		VecMapID A;// = new VectorMapID();
		VecMapID C;// for debugging purpose only
		
		/**selected constraints*/
		//in a p dimensional space, p constrains are needed to define a point
		//a constraint can either be the two following form
		//*probably a vector version is better? 
		//	isnt it more confusing to have index for the index?
		//	it becomes handy to put viE and viU together to get A
		//	#really want viX to be sorted?no
		//*a mask (side sign) is better? needed for determin side
		VectorI viE;// = new VectorI();//E constraints: w'e_i = 0
		VectorI viE_;// = new VectorI();//complement to set E 		
		VectorI vE;// = new VectorI();//side sign {-1,0,1}
		VectorI viU;// = new VectorI();//U constraints: w'Ins_i =1
		VectorI viU_;// = new VectorI();//complement to set U 		
		VectorI vU;// = new VectorI();//side sign {-1,0,1}
		//VectorI viA = new VectorI();// =[viE viU]
		
		/** constraint ia will be replaced by findFirstInterception()*/
		private void updateMatrix(){
			/**replace the iKick-th column in C with iHit-th E/U*/
			if (hitU){	
				//vNew = U.get(idxHit);
//				miL.from(VectorI.seq(n));	//all samples are penalized			
				if (kickU){
					A.SMUpdate(ia,U.get(iKick), U.get(iHit));
					viU.set(idxKick, iHit);
					viU_.set(idxHit, iKick);
				}
				else{//need to move
					A.SMUpdate(ia,E.get(iKick), U.get(iHit));
					if (idxKick<viE.size()-1)
						A.swap(idxKick, viE.size()-1);
					viE.remove(idxKick);
					viE_.add(iKick);
					viU.pushFrontOn(iHit);
					viU_.remove(idxHit);
				}
			}
			else{//hitE
				//vNew = E.get(idxHit);		
				if (kickU){//need to move
					A.SMUpdate(ia,U.get(iKick), E.get(iHit));
					if (idxKick>0)
						A.swap(ia, viE.size());
					viE.add(iHit);
					viE_.remove(idxHit);
					viU.remove(idxKick);
					viU_.add(iKick);
				}
				else{
					A.SMUpdate(ia,E.get(iKick), E.get(iHit));
					viE.set(idxKick, iHit);
					viE_.set(idxHit, iKick);
					
				}
			}
			
			//sanity check for SM updtate
			C=(VecMapID) E.sub(viE).catOn(U.sub(viU));
			VecMapID vvm1 = C.multiply(A);
			return ;
			//SMUpdate(iKick, vNew);
		}
		
		
		/** ds/dt for each U directions
		 * for all E directions ds/dt is always 1? 
		 * t is the length of solution path
		 * 	//better use map?*/
		
		VectorD v_dsdt,v_dsdw;
		VectorD v_dLdt, v_dLdw;
		//VectorD v_dLdtBase;// the base version, assuming all selected U has zero derivative
		VectorD v_dLds;// = new VectorD();
		VectorI viL;// = new VectorI();//samples that have non zero panelty
		/**when crossing E constraints, ds/dt will differ
		 * when crossing U constraints, dL/dt will differ
		 * for each direction in A, dL/ds will have two values
		 * */
		VectorI vA = new VectorI();//indicate which direction is better {-1,1}
		
		private void updateDerivative(){
			if (hitU){	
				int iSign = vU.get(iHit);
				if (iSign==-1){// previously penalized
					//exclude from penalized set L					
					viL.remove(viL.idxFirst(iHit));
					//exclude from  base derivative v_dLdw
					v_dLdw.minusOn(U.get(iHit));
				}
				vU.set(iHit, 0);
			}
			else{//hit E
				int iSign = vE.get(iHit);
				v_dsdw.set(ia,(double) -iSign);
				vE.set(iHit, 0);	
				//v_dLds.set(ia, v_dLdt.get(ia)/v_dsdt.get(ia));
			}
			
			int iSign =vA.get(ia);
			if (kickU){	
				if (iSign==-1)	//going to be penalized
					v_dLdw.minusOn(U.get(iKick));	
				vU.set(iKick, iSign);
			}
			else{//kickE
				v_dsdw.set(ia,(double) iSign);
				vE.set(iKick, iSign);				
			}
			
			/**dL/dt_i = dL/dw' * a_i  */
			v_dLdt = A.multiply(v_dLdw);//v_dLdw =  sum(U(viL));  viL=vU<0
			
			/**ds/dt_i = sign(w)' * a_i  */
			v_dsdt = A.multiply(v_dsdw); //v_dsdw = vE>=0
			
			v_dLds =v_dLdt.devide(v_dsdt);
			
			vA = new VectorI(p,1);//starting from the positive direction
			
			/** calculate dL/ds for alternate directions*/ 
			for (int i=0; i<p;++i){//viE.size(); ++i){
				//dLdt_i for the reversed direction
				//VectorD  dsdt_i = v_dsdt.get(i);
				double dLds_i;
				if (i<viE.size()){//E constraints
					double dsdt_i = v_dsdt.get(i) - 2*A.get(i).get(i);
					dLds_i=   v_dLdt.get(i)/dsdt_i;
				}
				else{
					MapID u = U.get(viU.get(i-viE.size()));
					double dLdt_i = v_dLdt.get(i) - A.get(i).inner(u);
					dLds_i= dLdt_i/v_dsdt.get(i);
				}
				if (dLds_i <= v_dLds.get(i)) continue;
				//replaced
				vA.set(i,-1);
				v_dLds.set(i, dLds_i);
			}
			return ;
		}
		
		/** to avoid the negativeOn operation,
		 * to have (like)'=u 
		 * lets use likelihood instead of loss  */
		public double like(MapID w){
			vRawLike=U.multiply(w).minusOn(1.0);//.negativeOn();
			return like();
		}
		public double like(){//MapID w){
			return vRawLike.sub(viL).sum();
		}
		
		/**history w*/
		public VectorX<MapID> vw ;//= new TVector<VectorD>(VectorD.class);
		/**history likelihood*/
		public VectorD vL;//= new VectorD();
		
		public void train(){
			vw = new VectorX<MapID>(MapID.class);
			vL= new VectorD();

			initSolution();
			while(true){//findNextSolution()){
				//double dLds; //
				vw.add((MapID) w.clone());
				vL.add(like(w));
				
				if (!findBestDirection())
					break;
				//if (v_dLds.get(idx)<0) return false;
				findFirstInterception();
				w.plusOn(a.multiply(t));
				updateMatrix();			 			
				updateDerivative();	
			}
			
		}
	}
	public void train(DataSet ds){
		return ;
	}
	public C2Linear trainBinarized(DataSet ds, VectorI vp, VectorI vn){
		return null;
	}
	public C2Linear trainBinarized(DataSetBinaryI ds, VectorI vp, VectorI vn){
		//augment data here?
		
		C2L1SVMTrainer trainer = C2L1SVMTrainer.getInstance();
		trainer.U = C2Linear.xyb(ds, vp, vn);
		trainer.p = ds.idxFeature.m.size();
		trainer.n = trainer.U.size();
		trainer.train();
		return null;//new C2Linear(trainer.w);
	}
	
	public L1SVM(){
		super(C2Linear.class);
	}

	//public class L1SVM2C {}

	
/*	public abstract DataSet testBinarized(DataSet dsTest){
		return null;
	}
	public abstract void trainBinarized(DataSet dsTrain){
		
	}*/
	public static void main(String[] args) {
		try {
			Param.overwrite(args);
			L1SVM svm = new L1SVM();
			DataSet ds = svm.newDataSet();
			ds.add(svm.parseLine("+1 a:0.5 b:1.0"));
			ds.add(svm.parseLine("-1 a:-1.0 b:-0.5"));
			ds.add(svm.parseLine("+1 a:0.5 b:1.0"));
			svm.train(ds);
			
		} catch ( Exception e ) {
			e.printStackTrace();
		}
		return;
	}
}