/**
 * 
 */
package edu.cmu.lti.nlp;

import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeSyntax;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;

/**
 * @author nlao
 *
 */
public class Interfaces {
  public static class Tag{
  	public int ib, ie;
  	public String type;
  	public String text;
    public Tag(int begin, int end, String neType, String neText){
      this.ib = begin;
      this.ie = end;
      this.type = neType;
      this.text = neText;
    }
  }
	public static interface ITagNE {
	  public VectorX<Tag> tagNE(String text);
	}
  
/*	public static interface IParseSeggedSent {
		public TreeParse parseSeggedSent(TVector<Token> vt);
	}
	public static interface ISynxParseSeggedSent {
		public TreeSyntax synxParseSeggedSent(TVector<Token> vt);
	}	
	
	public static interface IParseTaggedSent {
		public TreeParse parseTaggedSent(TVector<Token> vt);
	}	*/

	public static interface ISynxParseTaggedSent {
		public TreeSyntax synxParseTaggedSent(VectorToken vt);
	}	
	public static interface ISynxParseSent {
		public TreeSyntax synxParseSent(String sent);
	}	
	
	public static interface ISegWord {
		public VectorToken segWord(String sent);
	}	
	public static interface IBreakSent {
		public VectorS breakSent(String sent);
	}		
	public static interface ITagPOS {	
		public VectorToken tagPOS(VectorToken vt );
	}
	public static interface INLP
		extends  ISynxParseTaggedSent, ISegWord , IBreakSent, ITagPOS {	
	}
}
