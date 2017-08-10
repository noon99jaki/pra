/*Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.util;

public class AsianLanguageFormatPreprocessor{

  public String process(String term,int targetLanguage){

    String newTerm=term.trim();
    
    //replace different puncs with periods
    newTerm=newTerm.replaceAll("[\u3002\u00b7\u30fb]",".");

    //get rid of periods at the end
    if(newTerm.endsWith(".")){
      newTerm=newTerm.substring(0,newTerm.length()-1);
    }

    //change to single width chars
    newTerm=DoubleWidthChar.toSingleWidth(newTerm);

    return newTerm;

  }

}
