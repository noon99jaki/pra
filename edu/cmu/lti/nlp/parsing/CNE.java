/**
 * 
 */
package edu.cmu.lti.nlp.parsing;

/**
 * @author nlao
 *
 */
public class CNE {
	
	public static final String CARDINAL = "CARDINAL";
	public static final String DATE = "DATE";
	public static final String DURATION = "DURATION";

	public static final String LOCATION = "LOCATION";
	public static final String MEASURE = "MEASURE";
	public static final String MONEY = "MONEY";

	public static final String ORDINAL = "ORDINAL";
	public static final String ORGANIZATION = "ORGANIZATION";
	
	public static final String PERCENT = "PERCENT";
	public static final String PERSON = "PERSON";
	public static final String PHONE = "PHONE";
	public static final String TIME = "TIME";
	public static final String WWW = "WWW";
	public static final String[] vNE=
		new String[]{
		CARDINAL, DATE,DURATION
		,LOCATION,	MEASURE,	MONEY
		,	ORDINAL,	ORGANIZATION
		,PERCENT,	PERSON
		//,	PHONE
		,	TIME
		//,WWW
	};
	/*
*/

	//public static final String DATE = "DATE";
	//public static final String DATE = "DATE";
	
	//text sentence www phone cardinal 
	//time percent quoted person money booktitle ordinal date 
	//email location duration organization measure block


}
