package edu.cmu.lti.util.file;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.util.ArrayList;

import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.TMapSVecSa;
import edu.cmu.lti.algorithm.container.SetS;
import edu.cmu.lti.algorithm.container.VecMapSI;
import edu.cmu.lti.algorithm.container.VecVecS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.math.rand.FRand;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

// this class should be simplified with lambda expressions
public class FTable {

	public static void dedupe(String fn) {
		dedupe(fn, fn + ".dedupe");
	}

	public static void dedupe(String fn, String fn1) {
		SetS.fromFile(fn).save(fn1);
	}

	//static int nLine=0;

	public static boolean splitByProb(String fn, double p) {
		return splitByProb(fn, p, fn + ".p" + p, fn + ".p" + (1 - p));
	}
	public static boolean subsetByProb(String fn, double p) {
		return splitByProb(fn, p, fn + ".p" + p, null);
	}
	public static boolean subsetByProb(String fn, String p) {
		return subsetByProb(fn, Double.parseDouble(p));
	}
	public static boolean splitByProb(String fn, String p) {
		return splitByProb(fn, Double.parseDouble(p));
	}
	
	public static boolean subsetByProbA(String fn, String p, String output) {
		return splitByProb(fn, Double.parseDouble(p), output, null);
	}
	public static boolean splitByProbA(String fn, String p, String output1, String output2) {
		return splitByProb(fn, Double.parseDouble(p),output1, output2);
	}

	public static boolean splitByProb(String fn, double p, String fn1, String fn2) {
		BufferedReader br = FFile.newReader(fn);
		BufferedWriter bw1 = FFile.newWriter(fn1);
		BufferedWriter bw2 = fn2==null?null:FFile.newWriter(fn2);

		String line = null;
		while ((line = FFile.readLine(br)) != null)
			if (FRand.drawBoolean(p)) FFile.writeln(bw1, line);
			else if (bw2!=null) FFile.writeln(bw2, line);

		FFile.close(br);
		FFile.close(bw1);
		FFile.close(bw2);
		return true;
	}

	public static boolean countNonempltyCells(String fn) {
		BufferedReader br = FFile.newReader(fn);
		if (br == null) return false;
		String line = null;
		//VectorI vC=new VectorI();
		int vC[] = new int[10000];
		int nCol = 0;
		while ((line = FFile.readLine(br)) != null) {
			String vs[] = line.split("\t");
			if (vs.length > nCol) nCol = vs.length;
			//vC.extend(vs.length);
			for (int i = 0; i < vs.length; ++i)
				if (vs[i].length() > 0) ++vC[i];
			//vC.set(i,vC.g)
		}
		FFile.close(br);

		BufferedWriter bw = FFile.newWriter(fn + ".count");
		if (bw == null) return false;
		for (int i = 0; i < nCol; ++i)
			FFile.write(bw, vC[i] + "\t");

		FFile.close(bw);
		return true;
	}

	public static boolean filterByFreq(String fn, int iCol, int th) {
		return filterByFreq(fn, iCol, th, String.format("%s.c%dfq%d", fn, iCol, th));
	}

	public static boolean filterByFreq(String fn, int iCol, int th, String fn1) {
		BufferedReader br = FFile.newReader(fn);
		BufferedWriter bw = FFile.newWriter(fn1);
		if (br == null) return false;
		if (bw == null) return false;
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			String vs[] = line.split("\t");
			if (vs.length > iCol) if (Integer.parseInt(vs[iCol]) >= th) FFile.writeln(bw, line);
		}
		FFile.close(br);
		FFile.close(bw);
		return true;
	}

	public static VectorS loadAColumn(String fn, int iCol) {
		return loadAColumn(fn, iCol, "\t");
	}

	public static VectorS loadAColumn(String fn, int iCol, String c) {
		return loadAColumn(fn, iCol, false, c);
	}

	public static VectorS loadAColumn(String fn, int iCol, boolean bSkipTitle) {
		return loadAColumn(fn, iCol, bSkipTitle, "\t");
	}

	public static String title = null;
	public static String vTitle[] = null;

	public static VectorS loadAColumn(String fn, int iCol, boolean bSkipTitle, String sep) {
		VectorS vs = new VectorS();
		for (VectorS v : FFile.enuRows(fn, sep, bSkipTitle))
			vs.add(v.get(iCol));
		return vs;
	}

	public static boolean filterByColumn(String fn, int iCol, SetS mKeep) {
		return filterByColumn(fn, fn + ".f" + iCol, iCol, mKeep, "\t");
	}

	public static boolean filterByColumn(String fn, String fn1, int iCol, SetS mKeep) {
		return filterByColumn(fn, fn1, iCol, mKeep, "\t");
	}

	public static boolean filterByColumn(String fn, String fn1, int iCol, SetS mKeep, String sep) {
		BufferedWriter bw = FFile.newWriter(fn1);
		if (bw == null) return false;
		for (VectorS vs : FFile.enuRows(fn, sep, false))
			if (mKeep.contains(vs.get(iCol))) 
				FFile.writeln(bw, vs.join(sep));//FFile.line_);
		FFile.close(bw);
		return true;
	}

	public static boolean splitItems(String fn, String fn1) {
		return splitItems(fn, fn1, " ");
	}

	public static boolean splitItems(String fn, String fn1, String cSep) {
		BufferedWriter bw = FFile.newWriter(fn1);
		if (bw == null) return false;
		for (String line : FFile.enuLines(fn))
			for (String item : FString.split(line, cSep))
				FFile.writeln(bw, item);

		FFile.close(bw);
		return true;
	}

	public static boolean countCellsFreq(String fn) {
		return countCellsFreq(fn, 0);
	}

	public static boolean countCellsFreq(String fn, int iCol) {
		return countCellsFreq(fn, iCol, fn + ".fq" + iCol);
	}

	public static boolean countCellsFreq(String fn, int iCol, String fn1) {
		MapSI msi = new MapSI();
		for (VectorS vs : FFile.enuRows(fn))
			if (vs.size() > iCol) msi.plusOn(vs.get(iCol));
		msi.saveSortedByValue(fn1);
		return true;
	}

	public static void statTable(String fn) {
		statTable(fn, null);
	}

	/*	public static void statTable(String fn, String sTitle){
			statTable(fn, sTitle.split(" ")); 
		}*/
	public static void statTable(String fn, String vsTitle[]) {// String fn1) {
		VecMapSI vm = new VecMapSI();
		if (vsTitle != null) vm.init(vsTitle.length);

		MapSI msi = new MapSI();
		for (VectorS vs : FFile.enuRows(fn)) {
			if (vsTitle != null) 
				if (vs.size() < vsTitle.length) 
					FSystem.die("bad line=" + vs.join());//FFile.line_);

			for (int i = 0; i < vs.size(); ++i)
				vm.getE(i).plusOn(vs.get(i));
		}

		for (int i = 0; i < vsTitle.length; ++i) {
			if (vsTitle != null) System.out.print(vsTitle[i] + "\t");
			else System.out.print("C" + i + "\t");

			System.out.println(vm.get(i).joinTop(10, ") ", "\t", true));

		}
		//return true;		
	}

	public static void sortLinesByStrColumn(String file, int column, String sep) {
		TMapSVecSa key_lines = new TMapSVecSa();
		for (VectorS line: FFile.enuRows(file, sep)) {
			key_lines.getC(line.get(column)).add(line.join(sep));//FFile.line_);
		}		
		key_lines.toVectorV().save(file + ".sortedByCol" + column);
	}



	public static boolean subColumns(String fn, ArrayList<Integer> vKeep) {
		return subColumns(fn, vKeep, fn + ".cols", "\t");
	}

	public static boolean subColumns(String fn, 
			ArrayList<Integer> selected_columns, 
			String output_file, String sep) {
		BufferedWriter bw = FFile.newWriter(output_file);
		if (bw == null) return false;
		for (VectorS vs : FFile.enuRows(fn, sep)) {
			FFile.writeln(bw, vs.sub(selected_columns).join(sep));
		}
		FFile.close(bw);
		return true;
	}
	
	public static boolean addPrefixes(String fn, 
			String prefixes,	String output_file, String sep) {
		VectorS prefix = new VectorS(prefixes.split("-"));

		BufferedWriter bw = FFile.newWriter(output_file);
		if (bw == null) return false;		
		
		for (VectorS vs : FFile.enuRows(fn, sep)) 
			FFile.writeln(bw, prefix.joinS(vs, "", sep));
		
		FFile.close(bw);
		return true;
	}
	
	
//	public static void subColumns(String fn, 
//			String selected_columns, 	String output_file) {
//		subColumns(fn, selected_columns, selected_columns, "\t");
//	}
	
	public static boolean subColumns(String fn, 
			String selected_columns, 	String output_file, String sep) {

		VectorI columns =  VectorI.parse(selected_columns, "-");
		return subColumns(fn, columns, output_file, sep);
	}
	
	public static boolean mergeColumns(String fn, VectorI vKeep, String fn1) {
		BufferedWriter bw = FFile.newWriter(fn1);
		if (bw == null) return false;
		for (String line : FFile.enuLines(fn)) {
			VectorS vs = FString.splitVS(line, "\t");
			if (vs.firstElement().length() == 0) continue;
			for (String cell : vs.sub(vKeep))
				for (String item : cell.split(" "))
					if (item.length() > 0) FFile.writeln(bw, item);
		}
		FFile.close(bw);
		return true;
	}

	/*** 
	 * @param fn
	 * @param signal_ seperator
	 * @return vector of vector
	 * @throws IOException
	 */
	public static VecVecS loadTable(String fn) {
		return loadTable(fn, "\t");
	}

	public static VecVecS loadTable(String fn, String cRow) {
		return FString.parseTable(FFile.loadString(fn), "\n", cRow);
	}

	public static VecVecS loadCharTable(String fn) {
		VecVecS vvs = new VecVecS();
		for (String line : FFile.enuLines(fn)) {
			if (line.startsWith("#")) continue;
			VectorS vs = new VectorS(line.toCharArray());
			vvs.add(vs);
		}
		return vvs;
	}

	public static boolean subColumn(String fn, int iCol) {
		return subColumn(fn, iCol, fn + ".col" + iCol);
	}

	public static boolean subColumn(String fn, int iCol, String fn1) {
		return subColumn(fn, iCol, fn1, "\t");
	}

	public static boolean subColumn(String fn, int column, String fn1, String c) {

		/*	new ILineTransformer() {
				public String transform(String line){
					return FString.split(line,c)[iCol]);
				}
			}
			*/
		BufferedWriter bw = FFile.newWriter(fn1);
		if (bw == null) return false;
		for (VectorS row : FFile.enuRows(fn, c))
			FFile.writeln(bw, row.get(column));
		FFile.close(bw);
		return true;
	}

	public static void mergeFilesUniq(String vf, String fnOut) {
		mergeFilesUniq(vf.split(" "), fnOut);
	}

	public static void mergeFilesUniq(String vf[], String fnOut) {
		SetS m = new SetS();
		for (String fn : vf)
			m.addAll(FFile.loadLines(fn));
		if (m.contains("")) m.remove("");
		m.save(fnOut);
		System.out.println(vf.length + " files merged");
	}

	public static void mergeFilesUniq(String f1, String f2, String fnOut) {
		SetS m1 = SetS.fromFile(f1);
		SetS m12 = new SetS();
	}

	public static void main(String args[]) {
		
		String task = args[0];
		if (task.equals("subsetByProb")) subsetByProb(args[1],args[2]);
		else if (task.equals("splitByProb")) splitByProb(args[1],args[2]);
		
		else if (task.equals("subsetByProbA")) subsetByProbA(args[1],args[2],args[3]);
		else if (task.equals("splitByProbA")) splitByProbA(args[1],args[2],args[3],args[4]);
		else if (task.equals("subColumns")){
			if (args.length ==5 )
				subColumns(args[1],args[2],args[3],args[4]);
			else 
				if (args.length ==4 )
					subColumns(args[1],args[2],args[3], "\t");
		}
		else if (task.equals("addPrefixes"))	addPrefixes(args[1],args[2],args[3], "\t");
		else FSystem.die("unknown task="+task);
	}
}
