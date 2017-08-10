package edu.cmu.pra.data;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapIS;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

/**
 * This class prepare the fly data
 * @author nlao
 *
 */
public class Fly {
	/*
	COPY (SELECT id, secondaryidentifier FROM bioentity 
	where secondaryidentifier is not null)
	to '/usb2/nlao/fly2/bioentitySecond'; 


	 */
	/*

	CREATE TABLE publication(
	  pages text,
	  intermine_year integer,
	  volume text,
	  issue text,
	  pubmedid text,
	  id integer NOT NULL,
	  journal text,
	  title text,
	  firstauthor text,
	  "class" text,
	  CONSTRAINT publication_pkey UNIQUE (id)
	)
		 */
	/*
	


		COPY (SELECT bp.publications, textcat_all(secondaryidentifier || ' ') 
		FROM bioentitypublications as bp, bioentity as b
		where bp.bioentity=b.id 
		GROUP BY bp.publications)	
		to '/usb2/nlao/fly2/paper2bioentity'; 
		
		COPY bioentitypublications 	
		to '/usb2/nlao/fly2/bioentitypublications'; 
		
		COPY (SELECT publications, bioentity FROM bioentitypublications ORDER by publications)  	
		to '/usb2/nlao/fly2/publication2bioentity'; 
		
		
		
		COPY (SELECT p.pubmedid, textcat_all(primaryidentifier || ' ') 
		FROM bioentitypublications as bp, bioentity as b, publication as p
		where bp.bioentity=b.id and bp.publications=p.id
		GROUP BY p.pubmedid )	
		to '/usb2/nlao/fly2/pmid2bioFirst'; 



		
	COPY (SELECT p.pubmedid, textcat_all(g.secondaryidentifier || ' ') 
	FROM bioentitypublications as bp, bioentity as b, publication as p, gene as g
	where bp.bioentity=b.id and bp.publications=p.id and b.secondaryidentifier=g.secondaryidentifier
	and b.secondaryidentifier is not null
	GROUP BY p.pubmedid )	
	to '/usb2/nlao/fly2/pmid2gene2nd'; 
	COPY pmc_ref_pair from '/usr0/home/nlao/yeast/data/run/SGD.PMC.ref.pair.txt' DELIMITERS E'\t';

	COPY (SELECT textcat_all(g.primaryidentifier || ' ') 
	FROM gene as g, intergenicregion as igr, adjacentgenesintergenicregion as a
	where igr.id=a.intergenicregion and a.adjacentgenes=g.id
	GROUP BY igr.id order by igr.id)	
	to '/usb2/nlao/fly2/pmid2bioFirst'; 


	COPY (select g1.id, g2.id
	from gene as g1, gene as g2
	where g1.downstreamintergenicregionid=g2.upstreamintergenicregionid  
	)to '/usb2/nlao/fly2/geneAjacencyID';
	
	COPY (select g1.secondaryidentifier, g2.secondaryidentifier
	from interactinggenesinteractions as ig , interaction as i, gene as g1, gene as g2
	where ig.interactions =i.id and ig.interactinggenes=g1.id and i.geneid=g2.id
	 and i.interactiontype='physical'  
	order by ig.interactinggenes) 
	to '/usb2/nlao/fly2/geneInterPhysical'; 

	COPY (select g1.secondaryidentifier, g2.secondaryidentifier
	from interactinggenesinteractions as ig , interaction as i, gene as g1, gene as g2
	where ig.interactions =i.id and ig.interactinggenes=g1.id and i.geneid=g2.id
	 and i.interactiontype='genetic'  
	order by ig.interactinggenes) 
	to '/usb2/nlao/fly2/geneInterGenetic'; 



	//and i.interactiontype='physical'
	 */

	/*
	COPY (select g.secondaryidentifier, textcat_all(p.primaryidentifier|| ' ') 
	from genesproteins as gp, protein as p,  gene as g
	where gp.proteins=p.id and gp.genes=g.id   
	and g.secondaryidentifier is not null and p.primaryidentifier is not null
	group by g.secondaryidentifier) 
	to '/usb2/nlao/fly2/gene2ndProt1st'; 	

	 * 
	COPY (select secondaryidentifier from gene)	to '/usb2/nlao/fly2/gene.2ndId';
	COPY (select secondaryidentifier from bioentity)	to '/usb2/nlao/fly2/bioentity.2ndId';
	COPY (SELECT id, pubmedid FROM publication) to '/usb2/nlao/fly2/paperId'; 

	public static void dbPublication() {
		COPY (select pubmedid, intermine_year, journal,firstauthor, title 
		from publication where journal is not null) 	
		to '/usb2/nlao/fly2/publications'; 
	}
	
		//	COPY 232098

	COPY ( 
	SELECT p.pubmedid , textcat_all(a.name|| ',') 
	FROM authorspublications as ap, author as a , publication as p
	where p.id=ap.publications and ap.authors= a.id 
	group by p.pubmedid
	)to '/usb2/nlao/fly2/publicationsauthors'; 

	COPY (select g1.secondaryidentifier, i.interactiontype, g2.secondaryidentifier
	from interactinggenesinteractions as ig , interaction as i, gene as g1, gene as g2
	where ig.interactions =i.id and ig.interactinggenes=g1.id and i.geneid=g2.id  
	order by ig.interactinggenes) 
	to '/usb2/nlao/fly2/geneInteraction';


	 */

	public static void _dbPublicationAuthors() {
		BufferedWriter bw = FFile.newWriter("pmid2authors");
		for (String line : FFile.enuLines("publicationsauthors")) {
			line = line.replace(' ', '_');
			line = line.replace(',', ' ');
			line.trim();
			FFile.writeln(bw, line);
		}
		FFile.close(bw);
	}

	public static void _createScenarios(String pmcEx, String pmEx) {
		//SetI mPMC= SetI.fromFile("pmids.pmc");

		MapIS mRef = MapIS.fromFile(pmcEx);
		MapIS mGene = MapIS.fromFile("pmid2gene2nd");
		MapIS mProtein = MapIS.fromFile("pmid2protein1st");

		BufferedWriter bw = FFile.newWriter("papers.csv");
		FFile.writeln(bw, "paper,year,journal,author,word,paper,gene,protein");

		for (PMAbsInfor abs : PMAbsInfor.reader(pmEx)) {
			int i = 0;
			int id = Integer.parseInt(abs.pmid);
			if (!mRef.containsKey(id)) continue;

			String pmidrefs = mRef.getD(id);
			String genes = mGene.getD(id);
			String protein = mProtein.getD(id);

			//		FFile.write(bw,"%s,%s,%s,%s,%s,%s,%s,%s\n"
			//		,abs.pmid,abs.year,abs.journal,abs.vAuthors.
			//	,abs.title,pmidrefs,genes,protein);
		}
		FFile.close(bw);
	}

	public static void _printPaperDB(BufferedWriter writer, String pmid, String year, String journal,
			String firstauthor, String title, String authors, String pmidrefs, String genes,
			String proteins) {

		FFile.write(writer, "Year(" + pmid + "," + year + ")\n");
		FFile.write(writer, "Journal(" + pmid + "," + journal + ")\n");

		if (authors.length() > 0) FFile.write(writer, "Author(" + pmid + "," + authors.trim() + ")\n");
		FFile.write(writer, "FAuthor(" + pmid + "," + firstauthor + ")\n");

		if (title.length() > 0) FFile.write(writer, "Title(" + pmid + "," + title + ")\n");
		if (pmidrefs.length() > 0) FFile.write(writer, "Cites(" + pmid + "," + pmidrefs + ")\n");
		if (genes.length() > 0) FFile.write(writer, "Gene(" + pmid + "," + genes.trim() + ")\n");
		if (proteins.length() > 0) FFile.write(writer, "Protein(" + pmid + "," + proteins.trim() + ")\n");
		//m.addAll(title.split(" "));
	}

	public static void _filterBioentities(String fn) {
		BufferedWriter bw = FFile.newWriter(fdData + "BioEnt.db");
		for (VectorS vs : FFile.enuRows(fn)) {
			if (vs.size() < 2) continue;
			if (vs.get(1).length() == 0) continue;
			FFile.writeln(bw, "BioEnt(" + vs.get(0) + "," + vs.get(1).trim() + ")");
		}
		FFile.close(bw);

	}

	/*
	COPY (select g1.secondaryidentifier, g2.secondaryidentifier
	from gene as g1, gene as g2
	where g1.downstreamintergenicregionid=g2.upstreamintergenicregionid  
	and g1.secondaryidentifier is not null
	and g2.secondaryidentifier is not null) 
	to '/usb2/nlao/fly2/geneAjacency2nd';

	COPY (select i.interactiontype, g1.secondaryidentifier, textcat_all(g2.secondaryidentifier || ' ') 
	from interactinggenesinteractions as ig , interaction as i, gene as g1, gene as g2
	where ig.interactions =i.id and ig.interactinggenes=g1.id and i.geneid=g2.id
	and g1.secondaryidentifier is not null
	group by g1.secondaryidentifier, i.interactiontype) 
	to '/usb2/nlao/fly2/geneInter2nd'; 
	 */
	/**
	 * need to generate the following files
	 * 
	CREATE AGGREGATE textcat_all( 
	basetype    = text, 
	sfunc       = textcat, 
	stype       = text, 
	initcond    = '' 
	); 
	 * 
	COPY (SELECT p.pubmedid, textcat_all(g.primaryidentifier || ' ') 
	FROM bioentitypublications as bp, bioentity as b, publication as p, gene as g
	where bp.bioentity=b.id and bp.publications=p.id and b.primaryidentifier=g.primaryidentifier
	 and b.primaryidentifier is not null
	GROUP BY p.pubmedid )	
	to '/usb2/nlao/fly2/pmid2gene1st'; 

	COPY (SELECT p.pubmedid, textcat_all(t.primaryidentifier || ' ') 
	FROM bioentitypublications as bp, bioentity as b
		, publication as p, protein as t
	where bp.bioentity=b.id and bp.publications=p.id 
	and b.primaryidentifier=t.primaryidentifier
	 and b.primaryidentifier is not null
	GROUP BY p.pubmedid )	
	to '/usb2/nlao/fly2/pmid2protein1st'; 

	COPY (SELECT p.pubmedid, textcat_all(t.primaryidentifier || ' ') 
	FROM bioentitypublications as bp, bioentity as b
		, publication as p, protein as t
	where bp.bioentity=b.id and bp.publications=p.id 
		and b.primaryidentifier=t.primaryidentifier
	 	and b.primaryidentifier is not null
	GROUP BY p.pubmedid )	
	to '/usb2/nlao/fly2/pmid2proteinName'; 
	
	COPY (SELECT p.pubmedid, textcat_all(b.primaryidentifier || ' ') 
	FROM bioentitypublications as bp, bioentity as b, publication as p
	where bp.bioentity=b.id and bp.publications=p.id
	GROUP BY p.pubmedid )	
	to '/usb2/nlao/fly2/pmid2bioentity1st'; 
	//COPY 127121

	COPY (select g.primaryidentifier, textcat_all(p.secondaryidentifier|| ' ') 
	from genesproteins as gp, protein as p,  gene as g
	where gp.proteins=p.id and gp.genes=g.id   
	and g.primaryidentifier is not null and p.secondaryidentifier is not null
	group by g.primaryidentifier) 
	to '/usb2/nlao/fly2/gene1stProt2nd'; 
	
	COPY (select g.primaryidentifier, textcat_all(p.primaryidentifier|| ' ') 
	from genesproteins as gp, protein as p,  gene as g
	where gp.proteins=p.id and gp.genes=g.id   
	and g.primaryidentifier is not null and p.primaryidentifier is not null
	group by g.primaryidentifier) 
	to '/usb2/nlao/fly2/gene1stProt1st'; 	


	COPY (select i.interactiontype, g1.primaryidentifier, textcat_all(g2.primaryidentifier || ' ') 
	from interactinggenesinteractions as ig , interaction as i, gene as g1, gene as g2
	where ig.interactions =i.id and ig.interactinggenes=g1.id and i.geneid=g2.id
	and g1.primaryidentifier is not null
	group by g1.primaryidentifier, i.interactiontype) 
	to '/usb2/nlao/fly2/geneInter'; 


	COPY (select g1.primaryidentifier, g2.primaryidentifier
	from gene as g1, gene as g2
	where g1.downstreamintergenicregionid=g2.upstreamintergenicregionid  
	and g1.primaryidentifier is not null
	and g2.primaryidentifier is not null) 
	to '/usb2/nlao/fly2/geneAjacency';

	COPY (select  * from gene limit 100 ) to '/usb2/nlao/fly2/h100.gene'; 
	COPY (select  * from bioentity limit 100) to '/usb2/nlao/fly2/h100.bioentity'; 
	COPY (select  * from protein limit 100) to '/usb2/nlao/fly2/h100.protein'; 

	 */
	public static void processAll() {
		{
			System.out.println("geneInter.db");
			BufferedWriter bw = FFile.newWriter(fdData + "geneInter.db");
			for (VectorS vs : FFile.enuRows("geneInter")) {
				if (vs.get(2).length() == 0) continue;
				SetS mG = new SetS(vs.get(2)); //want to dedupe
				FFile.write(bw, vs.get(0) + "(" + vs.get(1) + "," + mG.join(" ") + ")\n");
			}
			FFile.close(bw);
		}

		{
			System.out.println("gene2Protein.db");
			BufferedWriter bw = FFile.newWriter(fdData + "gene2Protein.db");

			for (String line : FFile.enuLines("gene1stProt2nd")) {
				String vs[] = FString.split(line, "\t");
				FFile.write(bw, "GP(" + vs[0] + "," + vs[1].trim() + ")\n");
			}
			FFile.close(bw);
		}

		{
			System.out.println("geneAjacency.db");
			BufferedWriter bw = FFile.newWriter(fdData + "geneAjacency.db");
			for (String line : FFile.enuLines("geneAjacency")) {
				String vs[] = FString.split(line, "\t");
				FFile.write(bw, "Down(" + vs[0] + "," + vs[1] + ")\n");
			}
			FFile.close(bw);
		}

		{
			System.out.println("BioRef.db");
			BufferedWriter bw = FFile.newWriter(fdData + "BioRef.db");
			for (VectorS vs : FFile.enuRows("pmid2bioentity1st"))
				if (vs.size() == 2)
					FFile.writeln(bw, "Ref(" + vs.get(0) + "," + vs.get(1).trim() + ")");
			FFile.close(bw);
		}

		/*		{System.out.println("GeneRef.db");
					BufferedWriter bw = FFile.newWriter(fdData+"GeneRef.db");
					for (String vs[]: FFile.enuRows("pmid2gene1st"))
						if (vs.length==2)
							FFile.writeln(bw,"Gene("+vs[0]+","+vs[1].trim()+")");
					FFile.close(bw);
				}	
				
				{System.out.println("ProtRef.db");
					BufferedWriter bw = FFile.newWriter(fdData+"ProtRef.db");
					for (String vs[]: FFile.enuRows("pmid2protein1st"))
						if (vs.length==2)
							FFile.writeln(bw,"Protein("+vs[0]+","+vs[1].trim()+")");
					FFile.close(bw);			
				}
				*/

	}

	public static VectorS processGeneNames(String synonym) {
		//synonym=FPaper.processTitle(synonym);
		synonym = synonym.toLowerCase();
		VectorS vs = new VectorS();
		for (String s : FString.splitVS(synonym, "\\|")) {
			int i = s.indexOf(':');
			if (i >= 0) s = s.substring(i);
			vs.add(s);
		}

		return vs;
	}

	public static void bioSym() {

		int nLine = 0;
		SetS mGene = new SetS();
		int nSym = 0;
		BufferedWriter bw = FFile.newWriter(fdData + "BioSym.db");

		for (FBGeneAssoc i : FBGeneAssoc.reader("gene_association.fb.td")) {
			++nLine;
			if (mGene.contains(i.FlyBaseId)) continue;

			mGene.add(i.FlyBaseId);

			if (i.synonym.length() == 0) continue;
			String sym = processGeneNames(i.synonym).join(" ");
			//+FPaper.processTitle(i.synonym)
			if (sym.length() == 0) FSystem.dieShouldNotHappen();
			++nSym;
			FFile.writeln(bw, "Sym(" + i.FlyBaseId + "," + sym + ")");
		}
		FFile.close(bw);
		System.out.printf("nLine=%d nGene=%d nSym=%d", nLine, mGene.size(), nSym);
		//nLine=76809 nGene=12724 nSym=9139
		return;
	}
	public static String fdData = "../data/";

	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		//String dbName="flymine_20";
		//PostgreSQL.dumpSchema("flymine_20");
		//dbGeneReferences();
		//dbPublicationAuthors();

		//SetS m=SetS.fromFile("paperId", 1); m.save("pmid");
		//#pm=149890
		//#pmc=44821

		//filterBioentities("paper2bioentity");
		//createScenarios();

		//PM.addAbstractInfor("pmid.crawl.ex", "../data/papers.db");
		//PMC.citation2praFile("pmid.pmc.crawl.ex","../data/Ref.db");	
		//processAll();	
		//bioSym();
		//SeqLine.sSkipLine="!";		FTable.statTable("gene_association.fb.td"		," fromDb	 hasId	 hasSymbol	 hasGOQualifier	 hasGOId	 fromPublication	 hasEvidenceCode	 hasSpecialInfo	 ofAspect	 hasFullName	 hasSynonym	 isa	 hasTaxonomicIdentifier	 lastUpdated	 hasSource".split("\t"));

		//PRA.generateTextEdges("heading chemical protein gene");
		//Cite2Read.generateReadings(true, false, new Integer[] { 150, 200 });//10,20,30,50,70,100});//

	}
	/*
	 *    195668 cite.nP10
	   4366 cite.nP100
	   1635 cite.nP150
	  90876 cite.nP20
	    859 cite.nP200
	  49506 cite.nP30
	  31116 cite.nP40
	  21054 cite.nP50
	  10245 cite.nP70
	 */
}
