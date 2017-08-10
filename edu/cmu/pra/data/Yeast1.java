package edu.cmu.pra.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.sql.ResultSet;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.db.PostgreSQL;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.algorithm.sequence.Seq;

public class Yeast1 {

	String dbFile = "yeast.db"; // file of entities and
	// relations
	PostgreSQL db;// = new PostgreSQL();
	BufferedWriter bwDB;
	BufferedWriter bwSchema;

	// String schemaFile="yeast.db.schema";
	public Yeast1() {
		//db = new PostgreSQL();
		//db.createStatement();
	}
	private void addAuthors(int pmid, VectorS vsAuthor) {
		if (vsAuthor.size() == 0) return;
		FFile.write(bwDB, "Author(" + pmid + "," + vsAuthor.join(" ") + ")\n");
		FFile
				.write(bwDB, "AuthorF(" + pmid + "," + vsAuthor.firstElement() + ")\n");
		FFile.write(bwDB, "AuthorL(" + pmid + "," + vsAuthor.lastElement() + ")\n");
	}


	private void addGeneCitationToDB() {//FromFile
		System.out.println("addGeneCitationToDB()");
		
		String table = "paper_genes";
		db.dropCreateTable(	table,"pmid integer, genes text");
		/*
		 create table paper_genes (pmid integer, genes text);
		 COPY paper_genes 	from '/usr0/home/nlao/yeast/data/run/GeneCitation.txt' DELIMITERS E'\t';

		 */
	}

	/**
PMC-ids.csv.gz is a comma separated file with the following fields:
0)Journal Title
1)ISSN
2)Electronic ISSN
3)Publication Year
4)Volume
5)Issue
6)Page
7)DOI (if available)
8)PMC ID
9)PubMed ID (if available)
Manuscript ID (if available)
Journal Title,ISSN,eISSN,Year,Volume,Issue,Page,DOI,PMCID,PMID,Manuscript Id,Release Date

create table PMCCoverage (pmid integer);

COPY PMCCoverage (PMID)
from '/usr0/home/nlao/yeast/data/run/PMCCoverage.txt' CSV;

from '~/yeast/data/run/PMC-ids.csv' CSV
	 */

	private void addPMCidToDB() {//FromFile
		System.out.println("addPMCidToDB()");
		
		String table = "PMCCoverage";//pmcids";
		db.dropCreateTable(	table,"pmid integer");//, pmcid integer");

		BufferedReader br = FFile.newReader("PMC-ids.csv");
		BufferedWriter bw = FFile.newWriter("PMCCoverage.txt");
		FFile.readLine(br);
		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split(",");
			String pmid = vs[9];
			String pmcid= vs[8];
			if (pmid.length()<3 || pmcid.length()<3)
				continue;
			if (pmid.indexOf('P')>=0)
				continue;
			//String values = String.format("%s,%s", pmid,pmcid);
			//db.insertRow(table, pmid);//values);
			FFile.write(bw, pmid+"\n");
		}
		FFile.close(br);
		FFile.close(bw);
	}


	

	
	String fdRun="/usr0/home/nlao/yeast/data/run/";
	private void addPMCRefToDB() {
		System.out.println("addPMCRefToDB()");
		String table = "pmc_ref";
		db.dropCreateTable(table,"pmid integer, pmidRefs text, nRefs int");

		table = "pmc_ref_pair";
		db.dropCreateTable(	table,"pmid integer, pmidRef integer");
		
		/*
		COPY pmc_ref 	from '/usr0/home/nlao/yeast/data/run/PMC.ref.txt' DELIMITERS E'\t';
		COPY pmc_ref_pair from '/usr0/home/nlao/yeast/data/run/PMC.ref.pair.txt' DELIMITERS E'\t';
*/
	}

	private void addPMCRefSGDToDB() {

		String table = "pmc_ref_sgd";
		db.dropCreateTable(table,"pmid integer, pmidRefs text, nRefs int");
		
		/*
		table = "sgd_pmc_ref_pair";
		db.dropCreateTable(	table,"pmid integer, pmidRef integer");
		
		COPY pmc_ref_sgd 	
		from '/usr0/home/nlao/yeast/data/run/PMC.ref.SGD.txt' 
		DELIMITERS E'\t';
		
		COPY pmc_ref_pair from '/usr0/home/nlao/yeast/data/run/SGD.PMC.ref.pair.txt' DELIMITERS E'\t';
		 */
	}
	//0 pmid, 1year, 2journal, 3author, 4title
	private void addAbstractToDB() {
		System.out.println("addTitleJournalToDB()");

		String table = "Abstract";
		db.dropCreateTable(	table,
			"pmid integer, year integer, journal character varying(150), authors text, title text");
		for (String line: FFile.enuLines("SGD.abstract.txt")){
			String[] vs = line.split("\t");
			vs[3] = vs[3].replaceAll("'", "''");
			vs[3] = vs[3].replaceAll(",", " ");
			vs[4] = db.normalizeString(FPaper.processTitle(vs[4]));
			String values = String
					.format("%s,%s,\'%s\',\'%s\',\'%s\'", (Object[]) vs);// (
																																// Object
																																// )
			db.insertRow(table, values);
		}
	}

	protected void _addGeneCitationFromDB() {
		System.out.println("addGeneCitationFromDB()");
		/*
		 * CREATE TABLE gene_citation ( 
		 * 1) gene_citation_id integer NOT NULL, 
		 * 1) pubmed_id integer, 
		 * 1) citation text NOT NULL, 
		 * 1) gene_name character varying(150),
		 * 1) feature_name character varying(150), 
		 * 1) literature_topic text, 
		 * 1) gene_sgdid character varying(20), 
		 * )
		 */
		FFile.write(bwSchema, "Gene(paper,gene)\n");
		try {
			VectorS vs = new VectorS();
			ResultSet rlt = db.state.executeQuery(
				"select pubmed_id,gene_name from gene_citation");
			// citation,
			if (rlt == null) return;
			while (rlt.next()) {
				int pmid = rlt.getInt(1);
				// String citation = rlt.getString(2);
				String gene = rlt.getString(2);
				//addGeneCitation(pmid, gene);
				FFile.write(bwDB, "Gene(" + pmid + "," + gene + ")\n");

			}
			rlt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void addAbstractInfor(String fn) {
		System.out.println("addAbstractInfor()");
		
		
		/*FFile.write(bwSchema, "Year(paper,year)\n");
		FFile.write(bwSchema, "Journal(paper,journal)\n");

		FFile.write(bwSchema, "Author(paper,author)\n");
		FFile.write(bwSchema, "FAuthor(paper,author)\n");
		FFile.write(bwSchema, "LAuthor(paper,author)\n");

		FFile.write(bwSchema, "Title(paper,word)\n");*/

		BufferedWriter bw=//bSingleFile?bwDB:
			 FFile.newWriter("AbstractInfor.db");

		for (String line: FFile.enuLines(fn)){
			String[] vs = line.split("\t");
			String pmid=vs[0];
			String year=vs[1];
			String journal=vs[2];
			String authors=vs[4];
			String title=FPaper.processTitle(vs[5]);
			
			//addTitleJournal(Integer.parseInt(vs[0]), vs[2], vs[4]);
			FFile.write(bw, "Year(" + pmid + "," + year + ")\n");
			FFile.write(bw, "Journal(" + pmid + "," + journal + ")\n");
			
			
			if (authors.length()>0){
				FFile.write(bw, "Author(" + pmid + "," + authors + ")\n");
				String[] vsAuthor= authors.split(" ");
				FFile.write(bw, "FAuthor(" + pmid + "," + vsAuthor[0] + ")\n");
				FFile.write(bw, "LAuthor(" + pmid + "," + vsAuthor[vsAuthor.length-1] + ")\n");
			}
			
			FFile.write(bw, "Title(" + pmid + "," + title + ")\n");

		}
		//if (!bSingleFile)		
		FFile.close(bw);
	}

	protected void addPMCRefSGD() {
		System.out.println("addPMCRefSGD()");
		FFile.write(bwSchema, "Cites(paper,paper)\n");

		BufferedReader br = FFile.newReader("PMC.ref.SGD.txt");

		BufferedWriter bw=//bSingleFile?bwDB:
			 FFile.newWriter("PMCRefSGD.db");

		//FFile.readLine(br);
		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split("\t");
			String pmid=vs[0];
			String refs=vs[1];

			if (refs.length()==0)
				continue;
			FFile.write(bw, "Cites(" + pmid + "," + refs + ")\n");
		}
		//if (!bSingleFile)		
		FFile.close(bw);
		FFile.close(br);
	}
	
	protected void addYears() {
		System.out.println("addYears()");
		FFile.write(bwSchema, "Before(year,year)\n");

		BufferedWriter bw=//bSingleFile?bwDB:
		 FFile.newWriter("Years.db");

		for (int i=1950; i<=2008; ++i)
			FFile.write(bw, "Before(" + (i-1) + "," + i + ")\n");
		//if (!bSingleFile)		
		FFile.close(bw);

	}
	

	private void addAllFromDB() {
		/*
		
			COPY (select a.*,r.pmidrefs, g.genes 
		from pmc_ref_sgd as r, abstract as a , paper_genes as g
		where r.pmid=a.pmid and r.pmid=g.pmid)
		to '/usr0/home/nlao/yeast/data/run/papers.tab' DELIMITERS E'\t';
			
		COPY pmc_ref_pair from '/usr0/home/nlao/yeast/data/run/SGD.PMC.ref.pair.txt' DELIMITERS E'\t';
		
select a.*,r.pmidrefs, g.genes from pmc_ref_sgd as r, abstract as a , paper_genes as g
where r.pmid=a.pmid and r.pmid=g.pmid order by a.year limit 10;

		 */
		
	}

	public void run() {
		// FFile.saveString("", dbFile + ".schema");
		//bwDB = FFile.bufferedWriter(dbFile);
		bwSchema = FFile.newWriter(dbFile + ".schema");
		
		// addAuthors();
		// addTitleJournalFromFile();
		//addAbstractInfor();
		
		//addGeneAssociations();
		
		//addYears();
		
		//FFile.close(bwDB);
		FFile.close(bwSchema);
		// db.test();
	}
	
}
