package edu.cmu.lti.algorithm.string.editdistance;

import edu.cmu.lti.algorithm.container.VectorI;

/************************************************************************
Given a string s1x, this class will find the LIS(longest increasing sequence).
The result is in vHead.pInstanceS
************************************************************************/



public class EditDistanceLIS {
	public VectorI vpAsc;
	public VectorI s1x;
	public VectorI vHead;


	public void FindLIS()	{
		vpAsc.setSize(s1x.size());
	  vHead.clear();
	  //TRACE("|head| = %d  |asc|=%d \n",vHead.size(),vpAsc.size());
		if (s1x.size()==0) return;
		int l,r,m;
		vHead.add(0);
		vpAsc.set(0, -1);

		for (int p =1; p<s1x.size(); ++p){
			l= 0; r=vHead.size()-1;
			if (s1x.get(p) > s1x.get(vHead.get(r)) ){
				r = vHead.size();
				vHead.add(0);
			}
			else{
				while ( l+1< r  )	{
					m = (r + l) /2;
					if (s1x.get(vHead.get(m))  < s1x.get(p))					
						l = m;
					else 
						r = m;
				}
				if (s1x.get(vHead.get(l)) >= s1x.get(p))
					r = l;
			}
			
			if(r == 0)
				vpAsc.set(p,-1);
			else
				vpAsc.set(p,vHead.get(r-1));
			vHead.set(r, p);

			/* Debuging code below
	#ifdef _DEBUG
			for (int i = 0; i< vHead.size(); ++i)
			{
				TRACE("head >> ");
				for (int j = vHead[i].pInstanceS; j>=0; j= vpDec[j])
				{
					TRACE("  %d",s1x[j]);
				}
				TRACE("\n");
			}
	#endif

	#ifdef _DEBUG


			TRACE("vpAsc= ");
			for (int i =0; i<vpAsc.size(); ++i)
			{
				TRACE("  %d",vpAsc[i]);
			}
			TRACE("\n");

			TRACE("vHead= ");
			for (int i =0; i<vHead.size(); ++i)
			{
				TRACE("  %d(v%d)",s1x[vHead[i].pInstanceS],vHead[i].iValue);
			}
			TRACE("\n");
	#endif*/

		}    
		//TRACE("|head| = %d  |asc|=%d \n",vHead.size(),vpAsc.size());
		return;


	};
}
