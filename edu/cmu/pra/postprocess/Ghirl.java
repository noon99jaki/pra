package edu.cmu.pra.postprocess;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.SetI;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.Pipe;
import edu.cmu.lti.util.file.FFile;



/**
 * NIES need the following things 
 * @author nlao
 ==========
edges.ghirl
==========
# create edge: edge citingPaperCitesCitedPaper <citing_pmid> <cited_pmid>
(PMC.ref.pair.SGD)edge citingPaperCitesCitedPaper 10097122 9836640
--
# create edge: edge paperIsWrittenByAuthor <pmid> <author_name>
(PM.abstract)edge paperIsWrittenByAuthor 10793149 Lea_K
--
# create edges: edge keyGeneIsAssociatedWithAuxGene <key_gene_name>
<aux_gene_name>
() edge keyGeneIsAssociatedWithAuxGene 21S_RRNA 21S_rRNA_4
--
(SGD.g_liter)# create edge: edge geneIsMentionedInPaper <gene_name> <pmid>
edge geneIsMentionedInPaper CCS1 1001877
--
# create nodes: edge isa <gene_name> gene
edge isa 15S_RRNA gene
--
# create nodes: edge isa <pubmed_id> paper
edge isa 1001877 paper
--
# create nodes: edge isa <year_number> year
edge isa 1950 year
--
# create nodes: edge isa <author_name> author
edge isa &Rbreve;i&cbreve;icova_M author


==========
nodes.ghirl
==========
(PM.abstract)# Declare text nodes for authors with nodenames split on _ as terms
and link them to their anonymous nodes
node TEXT$Lea_K Lea K
edge hasKeywords Lea_K TEXT$Lea_K
--

(SGD.g_liter)# Declare text nodes for papers with citations as terms, and link them
with their anonymous nodes
node TEXT$47  Hartman FC, et al. (1975) The influence of pH on the
interaction of inhibitors with triosephosphate isomerase and
determination of the pKa of the active-site carboxyl group.
Biochemistry 14(24):5274-9
edge hasKeywords 47  TEXT$47

 */
public class Ghirl {
	
	
	/**
	1) PubMed ID (optional)		- the unique PubMed identifer for a reference
	2) citation (mandatory)		- the citation for the publication, as stored in SGD
	3) gene name (optional)		- Gene name, if one exists
	4) feature (optional)		- Systematic name, if one exists
	5) literature_topic (mandatory)	- all associated Literature Topics of the SGD Literature Guide
					  relevant to this gene/feature within this paper
					  Multiple literature topics are separated by a '|' character.
	6) SGDID (mandatory)		- the SGDID, unique database identifier, for the gene/feature

	47
	Hartman FC, et al. (1975) The influence of pH on the interaction of inhibitors with triosephosphate isomerase and determination of the pKa of the active-site carboxyl group. Biochemistry 14(24):5274-9
	TPI1
	YDR050C
	Other Features|Regulation of
	S000002457

	 */
	//public boolean bSingleFile=false;
	protected static void addGeneCitationFromFile() {//FromFile
		System.out.println("addGeneCitationFromFile()");

		BufferedWriter bw= FFile.newWriter("gene_literature.ghirl");
		
		SetI mGene= new SetI();		
		
		for (String line: FFile.enuLines("gene_literature.tab")){
			String[] vs = line.split("\t");			
			String pmid = vs[0]; 
			
			String citation= vs[1];
			String gene= vs[2].replace(',','_')
				.replace('(','_').replace(')','_');
			if (pmid.length()==0 || gene.length()==0)
				continue;
			
			int id= Integer.parseInt(pmid);
			FFile.write(bw, String.format(
				"edge geneIsMentionedInPaper %s %d\n",gene, id));
			
			if (!mGene.contains(id)){
					FFile.write(bw, String.format(
						"node TEXT$%d %s\n"	,id, citation));			
					FFile.write(bw, String.format(
							"edge hasKeywords %d  TEXT$%d\n"	,id, id));			
					mGene.add(id);
			}
			
		}
		FFile.close(bw);
		mGene.save("pmid_sgd.txt");
	}

	
	//edge keyGeneIsAssociatedWithAuxGene 21S_RRNA 21S_rRNA_4

	static SetS mGene= new SetS();
	public static void geneAs2Ghirl() {
		System.out.println("toGeneAsGhirl()");
	
		BufferedReader br = FFile.newReader("associations_per_gene.txt");

		BufferedWriter bw=//bSingleFile?bwDB:
			 FFile.newWriter("GeneAssoc.ghirl");


		for (String line = null; (line = FFile.readLine(br)) != null;) {
			String[] vs = line.split(",");
			String gene=vs[0];
			mGene.add(gene);
			
			for (int i=1;i<vs.length;++i){
				String agene= vs[i];
				mGene.add(agene);
				FFile.write(bw, "edge keyGeneIsAssociatedWithAuxGene " 
						+ gene + " " + agene + "\n");
				
			}
		}
		//if (!bSingleFile)		
		FFile.close(bw);
		FFile.close(br);	
		mGene.save("genes.txt");
	}
	private static void addGhirlNodes(BufferedWriter bw
			, String fn, String type){
		for (String line: FFile.enuLines(fn))
			FFile.write(bw, String.format(
				"edge isa %s %s\n",line, type));			
		FFile.flush(bw);
	}
	
	/*# create nodes: edge isa <gene_name> gene
edge isa 15S_RRNA gene
--
# create nodes: edge isa <pubmed_id> paper
edge isa 1001877 paper
--
# create nodes: edge isa <year_number> year
edge isa 1950 year
--
# create nodes: edge isa <author_name> author
edge isa &Rbreve;i&cbreve;icova_M author

*/
	public static void toGhirlNodes() {
		BufferedWriter bw= FFile.newWriter("nodes.ghirl");
		addGhirlNodes(bw,"pmid_sgd.txt", "paper");
		addGhirlNodes(bw,"authors.txt", "author");
		addGhirlNodes(bw,"genes.txt", "gene");
		//addGhirlNodes(bw,"journals.txt", "author");
		FFile.close(bw);
	}
	
	
	public static String fnAbs="../preprocess/pmid.sgd.crawl.ex";

	public static Pipe pipePickCitation= new Pipe<String[], String>(){
		public String transform(String[] v){
			if (v.length<11) return null;
			return v[0]+"\t"+v[5]+" (" +v[1]+") " +v[3]+" "+v[11] ;
			};};
	
	public static void extractPaperCitation() {
		FFile.enuRows(fnAbs)
		.select(pipePickCitation).save("paper.citation");	
	}
	
	public static Pipe pipePickPAC= new Pipe<String[], String>(){
		public String transform(String[] v){
			if (v.length<11) return null;
			return v[0]+" | "+v[5]+" | (" +v[1]+") " +v[3]+" "+v[11] ;
			// pmid year authors 
			};};
	
	public static void extractPaperAuthorCitation() {
		FFile.enuRows(fnAbs)
		.select(pipePickPAC).save("yeast2.paper.author.citation");	
	}
	public static void main(String args[]) {
		
		//addGeneCitationFromFile();
		//geneAss2Ghirl();
		
		//toGhirlNodes() ;
		//(new YeastData()).run();
		extractPaperAuthorCitation();
	}
}
