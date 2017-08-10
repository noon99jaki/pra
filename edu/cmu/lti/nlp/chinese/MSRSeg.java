/**
 * 
 */
package edu.cmu.lti.nlp.chinese;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.nlp.Interfaces.ISegWord;
import edu.cmu.lti.nlp.chinese.consts.City;
import edu.cmu.lti.nlp.chinese.consts.Country;
import edu.cmu.lti.nlp.chinese.consts.Province;
import edu.cmu.lti.nlp.chinese.consts.Punctuation;
import edu.cmu.lti.nlp.chinese.consts.UnitArea;
import edu.cmu.lti.nlp.chinese.consts.UnitLength;
import edu.cmu.lti.nlp.chinese.consts.UnitWeight;
import edu.cmu.lti.nlp.parsing.CNE;
import edu.cmu.lti.nlp.parsing.tree.CTag;
import edu.cmu.lti.nlp.parsing.tree.Token;
import edu.cmu.lti.nlp.parsing.tree.VectorToken;
/**
 * @author nlao
 *
 */
public class MSRSeg implements ISegWord {
	//socket connection to MSRSegServer (only one object instance, can't run IX and QA in the same JVM)
	//public static final MSRSegClient instance = new MSRSegClient();	
	private static  MSRSeg instance =null;
	public static MSRSeg getInstance() {
		if (instance==null) 	 instance = new MSRSeg();		
		return instance;
	}	
	
	protected static MapSS typeMap = new MapSS();
	private MSRSeg(){
		typeMap.put("P","PERSON");
		typeMap.put("L","LOCATION");
		typeMap.put("O","ORGANIZATION");
		typeMap.put("int","INT");
		typeMap.put("dat","DATE");
		typeMap.put("dur","DURATION");
		typeMap.put("mea","MEASURE");
		typeMap.put("mon","MONEY");
		typeMap.put("tim","TIME");
		typeMap.put("per","PERCENT");
		typeMap.put("ema","EMAIL");
		typeMap.put("pho","PHONE");
		typeMap.put("www","WWW");
		typeMap.put("qut","QUOTED");
		typeMap.put("buk","BOOKTITLE");
	}
	
	MSRSegClient client = MSRSegClient.getInstance();
	SetS country = Country.m;
	SetS province = Province.m;
	SetS city = City.m;
	SetS unitArea = UnitArea.m;
	SetS unitLength = UnitLength.m;
	SetS unitWeight = UnitWeight.m;
	
	public String[] defaultDate;
	protected static final Pattern pNE = Pattern.compile("\\[(.+?) (.*?)(\\])");
	protected static Pattern locWithInOrg = Pattern.compile("(([^市县省]{2,3}[市县省])+).*");

	/**
	 * return a list of terms from the MSRSeg output, together with NE extracted and inserted into the terms
	 *
	 */
	protected void refineMSRSegOutput(String[] parts) {
		return;
	}

	public String refineDate(String txt){
		String datePart =null;
		if (txt.equals("今年")) {
			datePart = defaultDate[0] + "年";
		} else if (txt.equals("去年")) {
			datePart = (Integer.parseInt(defaultDate[0]) - 1) + "年";
		} else if (txt.equals("前年")) {
			datePart = (Integer.parseInt(defaultDate[0]) - 2) + "年";
		} else if (txt.equals("明年")) {
			datePart = (Integer.parseInt(defaultDate[0]) + 1) + "年";
		} else if (txt.matches("今[天日]")) {
			datePart = defaultDate[0] + "年" + defaultDate[1] + "月" + defaultDate[2]
					+ "日";
		} else if (txt.matches("明[天日]")) {
			datePart = defaultDate[0] + "年" + defaultDate[1] + "月"
					+ (Integer.parseInt(defaultDate[2]) + 1) + "日";
		} else if (txt.matches("昨[天日]")) {
			datePart = defaultDate[0] + "年" + defaultDate[1] + "月"
					+ (Integer.parseInt(defaultDate[2]) - 1) + "日";
		}
		return datePart;
	}
	private String refineType(String txt, String type){
		String val = typeMap.get(type);
		
		if (val.equals("INT")) {
			if (txt.startsWith(Punctuation.DI)) 
				return CNE.ORDINAL;
			 else 
				return CNE.CARDINAL;			
		} 
		if (val.equals(CNE.PERSON)) {
			if ( country.contains(txt) || city.contains(txt)) return CNE.LOCATION;
		} 
		if (val.equals(CNE.ORGANIZATION)) {
		} 
		if (defaultDate != null && val.equals(CNE.DATE)) {
		}
		//if (val.matches("TIME|EMAIL|PHONE|WWW")) return null; 
		return val;
	}	

	public VectorToken refineMSRSegOutput(String rawSegText) {
		String[] parts = rawSegText.split("/");
		VectorToken vt = new VectorToken();

		for (int i = 0; i < parts.length; i++) {
			String txt = parts[i];
			txt = txt.trim();
			if (txt.length()==0)
				continue;
			Token t = new Token();
			Matcher matcherNE = pNE.matcher(txt);
			if (matcherNE.matches()) {
				// System.out.print("matches index "+pattIndex);
				String type = matcherNE.group(1).trim();
				txt = matcherNE.group(2);
				//txt = txt.toLowerCase();
				type = refineType(txt, type);
				if (type!=null)
					t.ms.put(CTag.NE, type);
			}
			if (txt.matches("\\w")){
				while(i<parts.length-1){
					String txt1= parts[i+1];
					if (!txt1.matches("\\w"))
						break;
					++i;
					txt += txt1;
				}
			}
			t.ms.put(CTag.text, txt);
			vt.add(t);
		}
		return vt;
	}

	//an overridden getMSRSegOutput() method with caching functions
	public VectorToken segWord(String text) {
		//toSegment = toSegment.replaceAll("日圆","日元");
		//toSegment = dynastyReplacementPattern.matcher(toSegment).replaceAll("$1朝");
		//text = text.replaceAll("[\r\n\t]", "");
		String rawSegText =client.process(text);
		return refineMSRSegOutput(rawSegText);
	}
}
