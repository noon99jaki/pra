/**
 * 
 */
package edu.cmu.lti.nlp;

import edu.cmu.lti.algorithm.container.MapSS;

/**
 * @author nlao
 *
 */
public class CLang {

	public static final String en_US = "en_US";
	public static final String zh_CN = "zh_CN";
	public static final String ja_JP = "ja_JP";
	public static final String ko_KR="ko_KR";
	public static final String zh_TW="zh_TW";	
/*	public static final String ch = "ch";
	public static final String jp = "jp";
	public static final String en = "en";
	*/
	public static final String EN = "EN";
	public static final String JA = "JA";
	public static final String CS = "CS";
	public static final String CT = "CT";

	public static final String E = "E";
	public static final String J = "J";
	public static final String C = "C";
	
	public static MapSS mLongShort = new MapSS();
	public static MapSS mShortLong = new MapSS();
	public static MapSS mLongChar = new MapSS();
	
	static{
		mLongChar.put(en_US, E);
		mLongChar.put(zh_CN, C);
		mLongChar.put(ja_JP, J);

		mLongShort.put(en_US, EN);
		mLongShort.put(zh_CN, CS);
		mLongShort.put(zh_TW, CT);
		mLongShort.put(ja_JP, JA);

		mShortLong.put(EN, en_US );
		mShortLong.put(CS, zh_CN );
		mShortLong.put(CT, zh_TW );
		mShortLong.put(JA, ja_JP );

	}
	
}
