package edu.cmu.lti.nlp.parsing.SRParser;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.TreeParse;

/**
*  This class contains a collection of utility methods
*/

public abstract class FeatureExtractor{
  //private static Logger log = Logger.getLogger( FeatureExtractor.class );
  public String lastAction = "NONE";
  public int bracketCount = 0;
  FeatureExtractor(){
  	reset();
  }
  public void reset(){
    lastAction = "NONE";
    bracketCount = 0;
  	
  }
  //public abstract Token extractFeature(	TVector<Token> queue ,VectorI stack);
  public abstract SetS extractFeature(
  	VectorX<Token> queue ,VectorI stack, TreeParse tree);

}
