/**
 * 
 */
package edu.cmu.lti.nlp.chinese.consts;

/**
 * @author nlao
 *
 */
public class Punctuation {
	public static final  String JUHAO="。";
	public static final  String WENHAO="？";
	public static final  String DOUHAO="，";
	public static final  String FENHAO="；";
	public static final  String TANHAO="！";
	public static final  String QIANYINHAO="“";
	public static final  String HOUYINHAO="”";
	public static final  String MAOHAO="：";
	public static final  String DI="第";
	public static final  String QIANSHUMING="《";
	public static final  String HOUSHUMING="》";
	public static final  String QINGWEN="请问";
	public static final  String DANQIANYINHAO="‘";
	public static final  String DANHOUYINHAO="’";
	public static final  String ZUOKUOHU="（";
	public static final  String YOUKUOHU="）";
	protected Punctuation(){}
	public static final Punctuation instance = new Punctuation();
	//for other classes to content-assist this class
}