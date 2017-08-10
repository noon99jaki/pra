package edu.cmu.lti.nlp.parsing.srl;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.nlp.parsing.srl.PropBank.TagSrl;
import edu.cmu.lti.nlp.parsing.tree.TreeParse.Tag;

public class PBMeta {
//public static class PBMeta{
	public String file_name, gold, verb;
	public int i_sent, i_pred, i_subcat;
	public VectorX<Tag> v_tag=new VectorX<Tag>(Tag.class);
	public void parse(String line){
		String[] vs = line.split (" ");
		file_name = vs[0];
		i_sent = Integer.parseInt(vs[1]);
		i_pred = Integer.parseInt(vs[2]);
		gold = vs[3];
		String[] vs2 = vs[4].split ("\\p{Punct}");
		verb = vs2[0];
		i_subcat = Integer.parseInt(vs2[1]);
		v_tag.setSize(vs.length-6);
		
		for (int i = 6; i<vs.length; ++i){
			Tag tag= new TagSrl();
			tag.parse(vs[i]);
			v_tag.set(i-6,  tag );
		}
		return;
	}
}