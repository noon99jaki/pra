package edu.cmu.pra.data;

import edu.cmu.lti.algorithm.Interfaces.IGetStrByStr;
import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.util.text.FString;
import edu.cmu.pra.CTag;
/**
 * as described in
http://flybase.org/static_pages/docs/refman/refman-G.html#G.3.1
 */
public class FBGeneAssoc implements IParseLine, IGetStrByStr{
	
		
	public String 	database	=null;//	 # FB
	public String 	FlyBaseId	=null;//	id
	public String 	symbol	=null;//	# dpp
	public String 	GOQualifier	=null;//	 # NOT | contributes_to | colocalizes_with
	public String 	GOId	=null;//	id
	public String 	publicaton	=null;//	 # |-separated
	public String 	Evidence	=null;//	
	public String 	evidenceInfo	=null;//	 # special info for evidence codes IGI
	public String 	aspect	=null;//	
	public String 	fullName	=null;//	 #decapentaplegic
	public String 	synonym	=null;//	 # |-separated
	public String 	type	=null;//	 # gene
	public String 	taxon	=null;//	 #taxon:7227
	public String 	date	=null;//	 # yyyymmdd
	public String 	datasource 	=null;//	# FB or UniProtKB

	public static Seq<FBGeneAssoc> reader(String fn){
		return reader(fn, false);
	}
	public static Seq<FBGeneAssoc> reader(String fn,boolean bSkipTitle){
		return new SeqTransform<FBGeneAssoc>(	FBGeneAssoc.class, fn, bSkipTitle);
	}
	 
	
	public FBGeneAssoc(){
	}
	public FBGeneAssoc(String line){
		parseLine(line);
	}
	public String toString(){
		return FlyBaseId;
	}

	public void normalize(){

		return ;
	}
	@Override public String getString(String tag){
		if (tag.equals(CTag.PRA))	{
			if (synonym.length()==0) return null;
			return FlyBaseId+"\t"+synonym;
		}
		return null;
	}
	
	
	static String sEmpty="";//null;//
	public void clear(){
		database	=sEmpty;
		 FlyBaseId	=sEmpty;
		symbol	=sEmpty;
		GOQualifier	=sEmpty;
		GOId	=sEmpty;
		publicaton	=sEmpty;
		Evidence	=sEmpty;
		evidenceInfo	=sEmpty;
		aspect	=sEmpty;
		fullName	=sEmpty;
		synonym	=sEmpty;
		type	=sEmpty;
		taxon	=sEmpty;
		date	=sEmpty;
		datasource 	=sEmpty;
	};
	
	
	/**
	 * title line of the plain table format
	 */
	public static String getTitle(){
		return	"database\tFlyBaseId\tsymbol\tGOQualifier\tGOId\tpublicaton\tEvidence\tevidenceInfo\taspect\tfullName\tsynonym\ttype\ttaxon\tdate\tdatasource";

	}
	

	public String print(){
		return database
		+"\t"+ FlyBaseId
		+"\t"+symbol
		+"\t"+GOQualifier
		+"\t"+GOId
		+"\t"+publicaton
		+"\t"+Evidence
		+"\t"+evidenceInfo
		+"\t"+aspect
		+"\t"+fullName
		+"\t"+synonym
		+"\t"+type
		+"\t"+taxon
		+"\t"+date
		+"\t"+datasource ;
	}
	/**
	 * load from plain table format
	 */
	public String PaperID;
	public boolean parseLine(String line){
		if (line.startsWith("!")) return false;
		VectorS vs= FString.splitVS(line,"\t");
		int i=0;
		database	=vs.get(i);++i;
		 FlyBaseId	=vs.get(i);++i;
		symbol	=vs.get(i);++i;
		GOQualifier	=vs.get(i);++i;
		GOId	=vs.get(i);++i;
		publicaton	=vs.get(i);++i;
		Evidence	=vs.get(i);++i;
		evidenceInfo	=vs.get(i);++i;
		aspect	=vs.get(i);++i;
		fullName	=vs.get(i);++i;
		synonym	=vs.get(i);++i;
		type	=vs.get(i);++i;
		taxon	=vs.get(i);++i;
		date	=vs.get(i);++i;
		datasource 	=vs.get(i);++i;
		return true;
	}
	
	static int nFailure=0;

}