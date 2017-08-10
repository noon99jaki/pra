/**
 * 
 */
package edu.cmu.lti.nlp.chinese.consts;

import edu.cmu.lti.algorithm.container.SetS;

/**
 * @author nlao
 *
 */
public class UnitLength {
	public static String[] vs = { "呎", "毫", "分", "公分", "寸", "市寸", "英寸", "市尺", "台尺", "公尺", "英尺",
			"尺", "纳米", "毫微米", "微米", "毫米", "厘米", "分米", "米", "码", "千米", "哩", "里", "公里", "英里", "海里",
			"华里", "光年" };
	public static SetS m = new SetS();
	static {m.load(vs);	}
}
