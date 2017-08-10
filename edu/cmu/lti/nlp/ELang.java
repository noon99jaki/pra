/**
 * 
 */
package edu.cmu.lti.nlp;

import java.io.Serializable;
import java.util.Locale;

/**
 * @author nlao
 * Keeps track of languages.
 */
public enum ELang  implements Serializable{

/*  en_US("American English",new Locale("en","US")),
  ja_JP("Japanese",new Locale("ja","JP")),
  jp_JP("Japanese",new Locale("ja","JP")),
  zh_TW("Traditional Chinese",new Locale("zh","TW")),
  zh_CN("Simplified Chinese",new Locale("zh","CN"))*/;

  protected final String description;
  protected final Locale locale;

  ELang(String description,Locale locale) {
      this.description = description;
      this.locale = locale;
  }
  
  public String description() { return description; }
  
  public Locale locale(){ return locale;}
}
