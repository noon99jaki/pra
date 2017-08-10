package edu.cmu.pra.foil;

import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.MapSX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.util.text.FString;

public class RelationLine implements IParseLine{

	public String 	name	;//relationName
	public String 	humanFormat	;
	public String 	populate	;
	public String 	generalizations	;
	public String 	domain	;
	public String 	range	;
	public String 	antisymmetric	;
	public String 	mutexExceptions	;
	public String 	knownNegatives	;
	public String 	inverse	;
	public String 	seedInstances	;
	public String 	seedExtractionPatterns	;
	public String 	nrOfValues	;
	public String 	nrOfInverseValues	;
	public String 	requiredForDomain	;
	public String 	requiredForRange	;
	public String 	editDate	;
	public String 	author	;
	public String 	description	;
	public String 	freebaseID	;
	public String 	comment	;

	public RelationLine() {
		
	}
	public RelationLine(RelationLine line) {
		copy(line);		
	}
	RelationLine inverse() {
		RelationLine line = new RelationLine(this);
		line.range = this.domain;
		line.domain = this.range;
		line.name = "_"+ this.name;
		line.inverse = this.name;
		
		line.humanFormat =humanFormat.replaceAll("arg1","arg2").replaceAll("arg2","arg1");
		return line;
	}
	public void copy(RelationLine line) {
		name	=line.	name	;
		humanFormat	=line.	humanFormat	;
		populate	=line.	populate	;
		generalizations	=line.	generalizations	;
		domain	=line.	domain	;
		range	=line.	range	;
		antisymmetric	=line.	antisymmetric	;
		mutexExceptions	=line.	mutexExceptions	;
		knownNegatives	=line.	knownNegatives	;
		inverse	=line.	inverse	;
		seedInstances	=line.	seedInstances	;
		seedExtractionPatterns	=line.	seedExtractionPatterns	;
		nrOfValues	=line.	nrOfValues	;
		nrOfInverseValues	=line.	nrOfInverseValues	;
		requiredForDomain	=line.	requiredForDomain	;
		requiredForRange	=line.	requiredForRange	;
		editDate	=line.	editDate	;
		author	=line.	author	;
		description	=line.	description	;
		freebaseID	=line.	freebaseID	;
		comment	=line.	comment	;
		
	}
	public String toString(){
		return name;
	}
	public boolean parseLine(String line){
		VectorS vs= FString.splitVS(line,"\t");
		if (vs.size()<6) return false;
		
		int i=0;
		name	= vs.get(i);++i;  
		humanFormat	=vs.get(i);++i;  
		populate	=vs.get(i);++i;  
		generalizations	=vs.get(i);++i;  
		domain	=vs.get(i);++i;  
		range	=vs.get(i);++i;  
		antisymmetric	=vs.get(i);++i;  
		mutexExceptions	=vs.get(i);++i;  
		knownNegatives	=vs.get(i);++i;  
		inverse	=vs.get(i);++i;  
		seedInstances	=vs.get(i);++i;  
		seedExtractionPatterns	=vs.get(i);++i;  
		nrOfValues	=vs.get(i);++i;  
		nrOfInverseValues	=vs.get(i);++i;  
		requiredForDomain	=vs.get(i);++i;  
		requiredForRange	=vs.get(i);++i;  
		editDate	=vs.get(i);++i;  
		author	=vs.get(i);++i;  
		description	=vs.get(i);++i;  
		freebaseID	=vs.get(i);++i;  
		comment	=vs.get(i);++i;  
		
		name= FString.getLastSection(name, ':');
		range= range.toLowerCase();
		domain= domain.toLowerCase();
		return true;
	}
	
	public static Seq<RelationLine> reader(String fn){
		return reader(fn, false);
	}
	
	public static Seq<RelationLine> reader(String fn,boolean bSkipTitle){
		return new SeqTransform<RelationLine>(	RelationLine.class, fn, bSkipTitle);
	}
	

	private static MapSX<RelationLine> relation_info_ =null;
	public static MapSX<RelationLine> loadRelationInfor(){
		if (relation_info_ == null) { 
			System.out.println("\n loadRelationInfor()");
			relation_info_ = new MapSX<RelationLine>(RelationLine.class);
			
			for (RelationLine line: RelationLine.reader("relations",true)){
				String rel=line.name.toLowerCase();
				relation_info_.put(rel, line);
				relation_info_.put( "_"+rel, line.inverse());
			}
		}
		return relation_info_;
	}
	
	public static RelationLine getRelationInfor(String rel) {
		return loadRelationInfor().get(rel);
	}
	
	public static String getDescription(String rel) {
		return loadRelationInfor().get(rel).humanFormat;
	}
	public static String getDomain(String rel) {
		return loadRelationInfor().get(rel).domain;
	}
	public static String getRange(String rel) {
		return loadRelationInfor().get(rel).range;
	}
	public static String getName(String rel) {
		return loadRelationInfor().get(rel).name;
	}
	public static String getInverse(String rel) {
		return loadRelationInfor().get(rel).inverse;
	}
	
	static MapSS relation_range_ = null;
	public static MapSS getRanges() {
		if (relation_range_==null) {
			relation_range_= new MapSS();
			for (RelationLine line: loadRelationInfor().values())
				relation_range_.put(line.name, line.range);
		}
		return relation_range_;
	}
	
	static MapSS relation_domain_ = null;
	public static MapSS getDomains() {
		if (relation_domain_==null) {
			relation_domain_= new MapSS();
			for (RelationLine line: loadRelationInfor().values())
				relation_domain_.put(line.name, line.domain);
		}
		return relation_domain_;
	}
}