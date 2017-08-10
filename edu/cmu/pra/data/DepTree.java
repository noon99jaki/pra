package edu.cmu.pra.data;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.DataSplit;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

public class DepTree {

	public static String normalizeWord(String s){
		s=s.toLowerCase();
//		s= s.replaceAll("\\*", "0");
		s= s.replaceAll("[\\*,\\$]", "_");
//		s= s.replaceAll("0", "_");
//		s=s.replaceAll(",", "COMA");
		return s;
	}
	
	public static String normalizeRelation(String s){
		s=s.replaceAll("prep_", "pp_");
		return s.replaceAll("0", "_");
//		final int  c= 'A'-'a';
//		int i=s.indexOf('_');
//		if (i<0) return s;
//		return (char)(s.charAt(0)+c)+s.substring(i+1);
	}
	
	private static boolean parseLine(String pos_line, String dep_line, 
			int sentence_id, BufferedWriter writer){
		
		dep_line = dep_line.trim();
		if (dep_line.length() <3)
			return false;
		dep_line = dep_line.substring(1, dep_line.length()-2);

		
		VectorS words = new VectorS(); 
		VectorS POSs = new VectorS();
		VectorS tokens = new VectorS();
		int id=0;
		for (String token: pos_line.split(" ")){
			int p=token.lastIndexOf('/');
			if (p==-1) {
				System.err.println("bad token="+token);
				continue;
			}
			words.add( normalizeWord(token.substring(0, p)));//"w$"+
			POSs.add(normalizeWord(token.substring(p+1)));
			tokens.add("t$"+sentence_id +"-"+id);
			++id;
		}
		
		int nW= words.size();
		for (int i=0; i<nW; ++i) {
			String token = tokens.get(i);
			String word = "w$"+  words.get(i);	
			String pos = "p$"+ POSs.get(i);
			FFile.writeln(writer,"W\t"+token+"\t"+word);
			FFile.writeln(writer,"_W\t"+word+"\t"+token);
			
			FFile.writeln(writer,"POS\t"+token+"\t"+pos);
			FFile.writeln(writer,"_POS\t"+pos+"\t"+token);
		}
		
//		for (int i=1; i<nW; ++i) {
//			String token0 = tokens.get(i-1);
//			String token1 = tokens.get(i);
//			FFile.writeln(writer,"B\t"+token0 + "\t" + token1);
//			FFile.writeln(writer,"_B\t"+token1 + "\t" + token0);
//		}
		
		//  prep_after(finding-21, years-3
		//            p        a  q     b
		for (String edge: dep_line.split("\\), ")){
			int p=edge.indexOf('(');
			int q=edge.indexOf(", ");
			int a = edge.lastIndexOf('-', q);
			int b = edge.lastIndexOf('-');
			if ( !(p!=-1 && q!=-1 && a!=-1&& b!=-1 && p<a && a<q && q <b)) {
				System.err.println("bad edge="+edge);
				continue;
			}
			String R= normalizeRelation(edge.substring(0,p));			
			String W0=normalizeWord(edge.substring(p+1,a));			
			int i0= Integer.parseInt(edge.substring(a+1, q))-1;
			String W1=normalizeWord(edge.substring(q+2, b));
			int i1= Integer.parseInt(edge.substring(b+1))-1;
			
			if (!words.get(i0).startsWith(W0)){
				System.err.println("unmatched word "+W0+"@"+i0+" != "+words.get(i0));
				//continue;
			}
			if (!words.get(i1).startsWith(W1)	){
				System.err.println("unmatched word "+W1+"@"+i1+"!= "+words.get(i1));
				//continue;
			}
			String token0 = tokens.get(i0);
			String token1 = tokens.get(i1);			
			FFile.writeln(writer, R + "\t" + token0 + "\t" + token1);
			FFile.writeln(writer, "_"+ R + "\t" + token1 + "\t" + token0);
		}
//		if (failed){
//			System.err.println("lineW="+pos_line);
//			System.err.println("lineR="+dep_line+"\n");
//			++num_errors;

		return true;
	}
	
	//wnSent=6175 nErr=0
	public static void parsed2Graph(String fn){
		BufferedReader reader= FFile.newReader(fn);
		BufferedWriter writer= FFile.newWriter(fn+".edges");
		String pos_line=null;
		int sentence_id=0;
		int num_errors=0;
		
		while((pos_line=FFile.readLine(reader))!=null){
			String empty_line= FFile.readLine(reader);	// skip empty line
			String dep_line=FFile.readLine(reader);
			parseLine(pos_line, dep_line, sentence_id, writer);
			++sentence_id;
		}
		
		FFile.close(reader);
		FFile.close(writer);
		System.out.println("#sentences="+sentence_id+" #errors="+num_errors);
	}
	
	public static void filterEntities(String graph_folder, String file){
		if (!graph_folder.endsWith("/")) graph_folder += "/";
		
		String code = FString.getSecondLastSection(graph_folder, "/");
		SetS nodes = SetS.fromFile(graph_folder +"node_names");
		SetS result= new SetS();
		for (String node: FFile.enuLines(file)) {
			if (node.length()==0) continue;
			
			node = normalizeWord(node);
			if (!nodes.contains(node)) continue;	//"w$"+
			
			result.add(node);
		}
		result.save(file + "." + code);
	}
	
	public static void entity2query(String file, String num_folds_str){
		System.out.println("entity2query()");
		int num_folds=Integer.parseInt(num_folds_str);
		VectorS nodes= VectorS.fromFile(file);
		
		for (int i=0; i<nodes.size(); ++i) nodes.set(i, "w$"+nodes.get(i)); 
		
		BufferedWriter writter= FFile.newWriter(file+".q"+num_folds);		
		DataSplit split = new DataSplit(num_folds, nodes.size());

		for (int i=0; i<num_folds; ++i) {
			FFile.writeln(writter,	nodes.sub(split.fold_test_ids.get(i)).join(" ")
					+ "\t" + nodes.sub(split.fold_train_ids.get(i)).join(" "));
			
			System.out.println("fold"+i 
					+ "\t" + split.fold_test_ids.get(i).size() 
					+ "\t" + split.fold_train_ids.get(i).size());
		}
		FFile.close(writter);
	}

		//parsed2Graph("muc.parsed");
		//entity2query("muc.persons");
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		String task = args[0];
		if (task.equals("parsed2Graph")) parsed2Graph(args[1]);
		else if (task.equals("filterEntities")) filterEntities(args[1], args[2]);
		else if (task.equals("entity2query")) entity2query(args[1], args[2]);
		
		else FSystem.die("unknown task=" + task);
	}
}
