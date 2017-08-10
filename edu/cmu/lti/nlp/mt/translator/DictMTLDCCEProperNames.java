/*
 * Frank Lin
 *
 */

package edu.cmu.lti.nlp.mt.translator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.nlp.CLang;

public class DictMTLDCCEProperNames extends DictMFTranslator{
	
	static final long serialVersionUID=1;


	public DictMTLDCCEProperNames(){
		String[] EC_FILES={
			"ldc_propernames_industry_ec_v1.beta.txt",
			"ldc_propernames_org_ec_v1.beta.txt",
			"ldc_propernames_other_ec_v1.beta.txt",
			"ldc_propernames_people_ec_v1.beta.txt",
			"ldc_propernames_place_ec_v1.beta.txt",
			"ldc_propernames_press_ec_v1.beta.txt"
		};

		String[] CE_FILES={
			"ldc_propernames_industry_ce_v1.beta.txt",
			"ldc_propernames_org_ce_v1.beta.txt",
			"ldc_propernames_other_ce_v1.beta.txt",
			"ldc_propernames_people_ce_v1.beta.txt",
			"ldc_propernames_place_ce_v1.beta.txt",
			"ldc_propernames_press_ce_v1.beta.txt"
		};		
		mm_files.getC(CLang.en_US).getC(CLang.zh_CN).addAll(EC_FILES);
		mm_files.getC(CLang.zh_CN).getC(CLang.en_US).addAll(CE_FILES);
		setTypeCap("UNKNOWN, TERM, TERM.ACRONYM, TERM.OOV");
	}

	Pattern pattern=Pattern.compile("(.+?)\\s+/(.*)/");
	public boolean parseLine(String line){
		Matcher m=pattern.matcher(line);
		if(!m.find()) return false;
		
		//get dictionary data and fix them up
		String[] targets=m.group(2).split("/");
		for(int i=0;i<targets.length;i++){
			dict.add(m.group(1).trim().toLowerCase()
					,targets[i].trim().toLowerCase());
		}			
		return true;
	}

}
