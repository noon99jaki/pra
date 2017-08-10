/**
 * 
 */
package edu.cmu.lti.nlp.chinese.consts;

import edu.cmu.lti.algorithm.container.SetS;

/**
 * @author nlao
 *
 */
public class UnitWeight {

	public static String[] vs = { "微克", "毫克", "克", "千克", "盎斯", "盎司", "磅", "英磅", "英吨", "英两", "吨",
			"公担", "公吨", "公斤", "斤", "市斤", "市两", "克拉" };
	public static SetS m = new SetS();
	static {m.load(vs);	}
}
