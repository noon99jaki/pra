package edu.cmu.lti.algorithm.string.editdistance;

import edu.cmu.lti.algorithm.container.MapVecII;
import edu.cmu.lti.algorithm.container.VectorI;

/************************************************************************
Given two strings s1 and s2, this class will find the optimal mapping from s1 to s2.
ans has the same size as s1, and it stores where each character of s1 is mapped to s2.
CensusS2();must be called before FindLCS(); for some string preprocessing
************************************************************************/


public class EditDistanceLCS extends EditDistanceLIS{
	public VectorI s1,s2;
	public int iScore;
	public VectorI belong, ans;
	public MapVecII idx;
	//public MapVectorII::iterator it;
	
	public void Clear()	{
		s1.clear();
		s2.clear();
	}


	

	void CensusS2()	{
		idx.clear();
		for (int p =0; p<s2.size(); ++p)
			idx.get(s2.get(p)).add(p);   
	/*
	#ifdef _DEBUG
		for (it = idx.begin(); it!= idx.end(); ++it)
		{
			vector<int> & l = it->second;

			TRACE("%d>> ",it->first);
			for (int i = 0; i< l.size(); ++i)
			{
				TRACE("  %d",l[i]);
			}
			TRACE("\n");
		}
	#endif
		*/
	}
	void FindLCS(){
		s1x.clear();belong.clear();
		for (int p =0; p<s1.size(); ++p)		{
			VectorI  l  = idx.get(s1.get(p));
			if (l==null) continue;
			for (int i  =l.size()-1; i>=0; --i)	{
				s1x.add(l.get(i));
				belong.add(p);
			}
		}    
		//TRACE("|s1x| = %d\n",s1x.size());

	/*
	#ifdef _DEBUG

			TRACE("s1x= ");
			for (int i = 0; i< s1x.size(); ++i)
			{
				TRACE("  %d",s1x[i]);
			}
			TRACE("\n");
	#endif
	*/
		FindLIS();

		iScore = vHead.size();
		ans.reset(s1.size(),-1);
		
		if (vHead.size() > 0)
			for (int p = vHead.lastElement(); p>=0;  p=vpAsc.get(p))			
				ans.set(belong.get(p), s1x.get(p));			
		
		/*
		#ifdef _DEBUG

		TRACE("S= ");
		for (int i = 0; i< ans.size(); ++i)
		{
		TRACE("  %d",ans[i]);
		}
		TRACE("\n");
		#endif*/
		belong.clear();
		s1x.clear();
	}
	int Align(){
		//LIS method, need a lot of  memory
		// when vocabulary is small as in bioinformatic
		//TRACE("\nlcs|s1|=%d |s2|=%d  ", s1.size(),s2.size());
		
		CensusS2();
		FindLCS();
		return iScore;	
	}
}
