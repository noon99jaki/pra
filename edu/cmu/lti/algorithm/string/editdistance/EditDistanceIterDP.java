package edu.cmu.lti.algorithm.string.editdistance;

import edu.cmu.lti.algorithm.container.VectorI;

public class EditDistanceIterDP extends AEditDistance {


	
	//why  recur???? isn't it DP?
	int recur(int x1, int y1, int x2, int y2){

		//TRACE("\n\n<<<<<< x1=%d x2=%d y1=%d y2=%d >>>>>>",x1,x2,y1,y2);
		if(x1 > x2 )	return 0;
		if (y1>y2)	{
			for(int i=x1; i<=x2;i++)
				ans[i]=-1;
				//TRACE(" \n---------d------------ans[%d]=%d",i,-1);
			return 0;
		}
		int lastP , thisP;//pair
		int lastS , thisS;//segment
		int lastD ; //direction
		//int i,j;
		int xm  = (x1 + x2)/2;


		////////////////////////////////////upper half//////////////////////////////////////
		//memset(pair,sizeof(pair), 0);			memset(dir,sizeof(dir), 2);

//		for(int i = y1; i<= y2; ++i) {pair[i] = 0;dir[i]=2;seg[i] = 0;}
		for(int i = 0; i<= pair.length-1; ++i) {
			pair[i]=0;
			dir[i]=2;
			seg[i]=0;
		}

		for(int j = x1; j<=xm; ++j)	{

			lastP = 0;
			lastS = 0;
			lastD = 2;
			//TRACE_debug(dir);
			//TRACE_debug(pair);
			//TRACE("  %d=%d-%d %d=%d",x1,j-1, x2, y1,y2);
			//TRACE("\n");
			for(int i = y1; i<= y2; ++i){

				dir[i] = 2; thisP = pair[i]; thisS = seg[i];
				if (i > y1) if (pair[i-1] > thisP 
						||(pair[i-1]==thisP && seg[i-1] < thisS))	{
					dir[i] = 1; thisP = pair[i-1]; thisS = seg[i-1];
				}
				
				if (s1[j] == s2[i]) 
					if (lastP >= thisP  || (lastP == thisP -1 
							&& (lastS < thisS || (lastS==thisS && lastD ==0))))
					{
						//TRACE("s1x=%d iBlank=%d\n",s1[j-1] , idBlank);
						//TRACE("\ns1[%d]=%d s2[%d]=%d", j,s1[j-1],i,s2[i-1]);
						dir[i] = 0; thisP = 1 + lastP; 
						thisS = lastS;
						if (lastD!=0) ++thisS;
					}
					lastP =  pair[i];  pair[i] = thisP; 
					lastS = seg[i]; seg[i] = thisS;
					lastD = dir[i];
			}
		}
		//TRACE_debug(dir);
		//TRACE_debug(pair);
		//TRACE("  %d=%d-%d %d=%d",x1,j-1, x2, y1,y2);
		//TRACE("\n");



		////////////////////////////////////lower half//////////////////////////////////////
		for(int i = y1; i<= y2; ++i) {
			pair_[i] = 0;dir_[i]=2;seg_[i] = 0;
		}
		for(int i = 0; i<= pair.length-1; ++i) {
			pair_[i] = 0;dir_[i]=2;seg_[i] = 0;
		}//MUZE
		
		for(int j = x2; j> xm; --j){
			lastP = 0;
			lastS = 0;
			lastD = 2;
			//TRACE_debug(dir_);
			//TRACE_debug(pair_);
			//TRACE("  %d-%d=%d %d=%d  ",x1,j+1, x2, y1,y2);
			//TRACE("\n");
			for(int i = y2; i>=y1; --i)	{
				dir_[i] = 2; thisP = pair_[i];thisS = seg_[i];

				if (i<y2)
				if (pair_[i+1] > thisP||(pair_[i+1]==thisP && seg_[i+1] < thisS))	{
					dir_[i] = 1; 
					thisP = pair_[i+1];
					thisS = seg_[i+1];
				}
				if (s1[j] == s2[i])//&& s1[j] != idBlank)
					if (lastP >= thisP || (lastP == thisP -1 && (lastS < thisS || (lastS==thisS && lastD ==0)))) 
					{
						//TRACE("\ns1[%d]=%d s2[%d]=%d", j,s1[j-1],i,s2[i-1]);
						dir_[i] = 0; thisP = 1 + lastP;
						thisS = lastS;
						if (lastD!=0) ++thisS;

					}
					lastP = pair_[i];  pair_[i] = thisP; 
					lastS = seg_[i]; seg_[i] = thisS;
					lastD = dir_[i];
			}
		}
		//TRACE_debug(dir_);
		//TRACE_debug(pair_);
		//TRACE("  %d-%d=%d %d=%d  ",x1,j+1, x2, y1,y2);
		//TRACE("\n");


		int MaxP = 0, Maxi= y1;
		int MinS =0;

		for(int i = y1; i< y2; ++i){
			if(xm == 20){
				//TRACE("WOWO MaxP=%d MinS=%d Maxi=%d\n",MaxP, MinS,Maxi);
				//TRACE("WOWO pair[i]=%d+%d=%d seg[i]=%d i=%d\n",pair[i], pair_[i+1],pair[i]+pair_[i+1], seg[i]+seg_[i+1],i);
			}
			pair[i] += pair_[i+1];
			seg[i] += seg_[i+1];
			if (dir[i] ==0 && dir_[i+1]==0) --seg[i];

			if (pair[i] > MaxP || (pair[i]==MaxP && seg[i] < MinS)){
				MaxP = pair[i]; 
				MinS = seg[i];
				Maxi = i;
			}
		}
		//ASSERT(dir[Maxi] ==0|| dir[Maxi] ==2);
		//TRACE("\nmax pair %d+%d=%d", pair[Maxi]-pair_[Maxi+1], pair_[Maxi+1],pair[Maxi]);

		//TRACE_debug(pair);
		int l,r;
		l=Maxi; r=Maxi +1;
		if ( MaxP < pair_[y1] || (MaxP == pair_[y1]&& MinS > seg_[y1]))	{
			MaxP = pair_[y1];
			MinS = seg_[y1];
			l = y1-1;
			r = y1;
		}
		
		if ( MaxP < pair[y2] || (MaxP == pair[y2]&& MinS > seg[y2])){
			MaxP = pair[y2];
			MinS = seg[y2];
			l = y2;
			r = y2+1;
		}

		for( ; r<= y2 && dir[r] == 1 
			&& pair[r] == MaxP && seg[r] == MinS ; ++r);

		ans[xm] = -1;
		//TRACE(" \n---------f------------ans[%d](%d)=%d",xm,Maxi,-1);
		
		if (l>=y1 && dir[l] == 0)	{ 
			ans[xm] = l; //TRACE(" \n---------a------------ans[%d](%d)=%d",xm,Maxi,l);
			-- l;
		}


		recur(x1,y1,xm -1, l);
		recur(xm +1, r, x2,y2);
		return MaxP;

	}

	void TRACE_debug(VectorI v, int iMap){
		//TRACE("\n");
		for(int  i = 0; i< v.size(); ++i)	{
			if (v.get(i)>=0) 	{
				switch(iMap) {
						case 0:
							//TRACE(" %d", v[i]);
							break;
						case 1:
							//TRACE(" %d", s1[i-1]);
							break;
						default:
							break;
				}
			}
			else
			{
				//TRACE(" _");
			}
		}
	}

	/*public void Clear(){
		s1.clear();
		s2.clear();
	}*/
	
	public int[] dir, pair, seg;
	public int[] dir_, pair_, seg_;

	public int Align(){
		dir = new int[s2.length];
		pair = new int[s2.length];
		seg = new int[s2.length];
		dir_ = new int[s2.length];
		seg_ = new int[s2.length];
		return recur(0,0,s1.length-1,s2.length-1);	
	}
};
