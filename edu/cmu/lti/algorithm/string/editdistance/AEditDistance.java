package edu.cmu.lti.algorithm.string.editdistance;

public abstract class AEditDistance {//extends EditDistanceSparseDP{
	
	public int nMatch;
	public int idBlank;
	public int[] s1,s2;
	public int[] ans;

	/**
	 * 	Given two strings s1 and s2, 
			this function will find the optimal mapping from s1 to s2.
			ans has the same size as s1, 
			and it stores where each character of s1 is mapped to s2.
			The number of matched characters is returned, 
			and it is also stored in iScore menber variable.

	 */

	int iDist;

	public int Align(int[] s1, int[] s2){
		this.s1=s1;
		this.s2=s2;
		ans = new int[s1.length];
		for (int i=0; i<ans.length; ++i) 
			ans[i] = -1;

		nMatch = 	Align();
		iDist = s1.length + s2.length - 2*nMatch;
		return nMatch;
	}
	public abstract int  Align();
	
	//void Switch();	exchange s1 and s2
	public void Switch()	{
	/*	s2.clear();
		int iLast = -1;
		for(int i=0;i<s1.size(); ++ i )	{

			if (iLast > 0 && ans[i] != iLast +1) 
				s2.push_back(-1);
			

			if (ans[i] >0)
				s2.push_back(s1[i]);

			iLast = ans[i];
		}

		s1.clear();
		//TRACE("\nSwitch|s1|=%d |s2|=%d |ans|=%d "	, s1.size(),s2.size(), ans.size());
*/
	}
	/**
	 * with the alignment result stored in "ans",
 		s1 will be merged into s2 result in s3
	 */
	void Merge()	{
		int nL= s1.length+s2.length-this.nMatch;
		int[] s3 = new int[nL];
		//s2.clear();
		int iLastS3 = -1;

		int p=0;
		for(int i=0;i<s1.length; ++ i ){
			if (ans[i] >= 0){
				if(ans[i] != iLastS3 +1){
					for(int j = iLastS3 +1; j <ans[i]; ++j){
						s3[p]=s2[j];++p;
					}
				}
				iLastS3 = ans[i];
			}
			s3[p]=s1[i];++p;
		}
		
		for(int j = iLastS3 +1; j <s2.length; ++j){
			s3[p]=s2[j];++p;
		}
		//TRACE("\nMerge|s1|=%d |s2|=%d |ans|=%d ", s1.size(),s2.size(), ans.size());
	}		
}
