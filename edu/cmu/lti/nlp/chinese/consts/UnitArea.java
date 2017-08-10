package edu.cmu.lti.nlp.chinese.consts;

import edu.cmu.lti.algorithm.container.SetS;

public class UnitArea {
	public static String[] vs = { "公顷", "垧", "亩", "英亩", "坪", "平米", "平方分米", "平方毫米", "平方里米", "平方米",
			"平方里", "平方公里", "平方千米", "平方英尺", "平方英寸", "平方英里", "平方尺" };
	public static SetS m = new SetS();
	static {m.load(vs);	}	
}
