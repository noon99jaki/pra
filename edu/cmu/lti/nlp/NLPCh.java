/**
 * 
 */
package edu.cmu.lti.nlp;

import junit.framework.Assert;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.chinese.Identifinder;
import edu.cmu.lti.nlp.chinese.MSRSeg;
import edu.cmu.lti.nlp.chinese.parser.MaxEntParserService;
import edu.cmu.lti.nlp.chinese.tagger.MaxEntTaggerService;
import edu.cmu.lti.nlp.parsing.tree.CPOS;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.sent.SentAnalyzer.CTag;


/**
 * @author nlao
 *
 */
public class NLPCh  extends NLP{
	//singlten pattern with lazy loading
	//public static final NLPCh instance = new NLPCh();	
	private static  NLPCh instance =null;
	public static NLPCh getInstance() {
		if (instance==null) 	 instance = new NLPCh();			
		return instance;
	}
	public static class Param extends edu.cmu.lti.util.run.Param{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public Param() {
			super(NLPCh.class);
			parse();
		}
		public void parse(){			
		}
	}	
	public Param p=new Param();
	
	public NLPCh(){
		taggerPOS = MaxEntTaggerService.getInstance();
		parser = MaxEntParserService.getInstance();
		//tagger = MaxEntTagger.getInstance();
		//parser = MaxEntParser.getInstance();
		segmentor = MSRSeg.getInstance();
		this.taggerNE = Identifinder.getInstance();
		
		regexBreakSent="[。！？]";
		m_fixPOS.put("?",CPOS.PU);
		m_fixPOS.put("列举",CPOS.VV);
		m_fixPOS.put("回头",CPOS.VV);
		
/*		//ACLIA1-CS-D57" TITLE="全球变暖人类是元凶
		vvs_fixSeg.add(new VectorS(	new String[] {"变","暖"}));
		m_fixTag.put("变暖","VV");
		m_fixTag.put("暖","JJ");
		
		//ACLIA1-CS-D83	美国 通过 对 华 永久 正常 贸易 关系 法案 ， 对 中国 有 何 重大 关系
		m_fixTag.put("永久","JJ");
		
		//ACLIA1-CS-D85"克 雅 氏 症 与 疯牛病 的 关联 是 什么 ？
		//m_fixTag.put("克","NN");
		//m_fixTag.put("雅","NN");
		//m_fixTag.put("氏","NN");
		//m_fixTag.put("症","NN");
		m_fixTag.put("克雅氏症","NN");
		vvs_fixSeg.add(new VectorS(	new String[] {"克","雅","氏","症"}));
		*/
	}
	
	//
	public static void main(String[] args) {
		NLP nlp = NLP.getInstance( CLang.zh_CN );
		//String sent = "美最大航空公司";
		String sent = "36岁的马兰,曾在《女附马》、《无事生非》等大戏中担任女主角,并获“文华奖”、“梅花奖”等荣誉";
		String sent1 = "曾在《女附马》、《无事生非》等大戏中担任女主角";
		//String sent2 = "并获“文华奖”、“梅花奖”等荣誉";
		String sent2 = "一九二七年大革命失败后";
		TreeSyntax  tr2 =nlp.synxParseSent(sent2);
		System.out.println(tr2.toString());
		return;
	}
}
