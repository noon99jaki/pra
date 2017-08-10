/**
 * 
 */
package edu.cmu.lti.util.html;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Map;

import edu.cmu.lti.algorithm.container.MapIMapIS;
import edu.cmu.lti.algorithm.container.MapSMapSD;
import edu.cmu.lti.algorithm.container.MapSD;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VecVecI;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.html.FHtml.CStyle;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;

/**
 * @author nlao  * 
 */
public class HtmlPage implements Serializable {
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public static final String _pageBegin = "<html>\n<head>\n"
			+ "<meta http-equiv=\"Content-Type\" content=\"text/html; charset=utf-8\">"
			+ "\n%s\n<title>%s</title>\n</head>\n" + "<body>\n<p>%s</p>\n";

	public static final String _pageEnd = "</body></html>";
	public static final String _tableEnd = "</table><br>\n";
	public int repeat_title_ = -1;	// repeat the title once every few rows
	public String title = "";
	public String folder = "";//to store it subnodes
	public String file = null;

	public static String _script = "";

	public HtmlPage(String title) {
		this(title, ".");
	}

	public HtmlPage(String title, String folder) {
		this(title, folder, title);
	}

	public HtmlPage(String title, String folder, String file) {
		if (!folder.endsWith("/")) folder += "/";
		this.title = title;
		//this.content = content;//"<br>"+
		this.folder = folder;
		this.file = file;
		FFile.mkdirs(folder);
		writer_ = FFile.newWriter(getURL());
		FFile.write(writer_, String.format(_pageBegin, _script, title, FSystem
				.printTime()));
	}

	public String getURL() {
		return folder + file + ".html";
	}

	public String getFilePath() {
		return folder + file;
	}
	public BufferedWriter writer_ = null;

	//public TableHtml(){	}

	public HtmlPage addPre(String txt) {
		endTable();
		FFile.write(writer_, "<pre>\n" + txt + "</pre>\n");
		return this;
	}

	public HtmlPage addTxt(String txt) {
		endTable();
		FFile.writeln(writer_, txt);
		return this;
	}
	
	public void copyFile(String file, String code) {
		addTxt(FHtml.addHref(code, code));
		FFile.copyFile(file, this.folder + code);
	}

	public HtmlPage addCode(String txt) {
		FFile.write(writer_, txt);
		return this;
	}

	public void newTable(String name, String title) {
		newTable(name, title, CStyle.dftStyle);

	}

	public void newTable(String name, String title, String style) {
		newTable(name, FString.splitVS(title, "\t"), style);
	}

	public void endTable() {
		if (table_open_) FFile.write(writer_, _tableEnd);
		table_open_ = false;
	}

	public static final String tableStyle = "";

	//"+CStyle.center+"  id=\"table1\"
	//"\n<table  border=\"1\">\n";//width=\"100%%\"

	public void newTable(String name, VectorS title) {
		newTable(name, title, CStyle.dftStyle);
	}
	public VectorS title_ = null;
	boolean table_open_ = false;

	public void newTable(String name, VectorS title, String style) {
		if (style == null) style = "";
		if (table_open_) endTable();

		if (name != null)
			FFile.writeln(writer_, FHtml.p(name, CStyle.alignCenter + CStyle.bold));
		
		FFile.writeln(writer_,"<table " + style + ">");

		title_ = title;
		addTitleRow();//writeRow(title, CStyle.center, true);
		table_open_ = true;
	}


	/**
	 * directly create a table with content
	 * @param name
	 * @param TITLE
	 * @param table
	 */
	public void createTable(String name, String table) {
		VectorX<VectorS> vvs = FString.parseTable(table, "\n", "\t");
		newTable(name, vvs.get(0));
		for (int i = 1; i < vvs.size(); ++i)
			this.addRow(vvs.get(i));
	}

	VectorS texts_ = new VectorS();
	VectorS styles_ = new VectorS();

	public void clearStyledRow() {
		texts_.clear();
		styles_.clear();
	}

	public void writeStyledRow() {
		writeRow(texts_, styles_);

	}

	public void addStyledCell(String txt, String style) {
		styles_.add(style);
		texts_.add(txt);
	}

	public void addCell(String txt) {
		texts_.add(txt);
	}

	public void addRowF(String sRow, Object... args) {
		addRow(String.format(sRow, args));
	}

	public void addRow(String sRow) {
		addRow(sRow, "");
	}

	public void addRow(String... args) {
		writeRow(new VectorS(args), "");
	}

	public void addRow(String sRow, String style) {
		writeRow(FString.splitVS(sRow, "\t"), style);
	}

	public void addRow(VectorS vsRow) {
		writeRow(vsRow, "");
	}

	public void addRowVTxt() {
		addRow(texts_);
	}

	public void addStyledRow(String style) {
		writeRow(texts_, style);
	}

	public void addString(String txt) {
		FFile.write(writer_, txt);
	}

	public void addTitleRow() {
		addTitleRow(null);
	}
	
	// set the id of a title line
	public void addTitleRow(String id) {
		if (id!=null) title_.set(0,id);
		writeRow(title_, CStyle.center + CStyle.bold
				+ FHtml.backGround(EColor.lightblue));
		--num_rows_so_far_;
	}
	public int num_rows_so_far_=0;
	private void checkRepeatedTitle(){
		++num_rows_so_far_;
		if (repeat_title_>1) 
			if (num_rows_so_far_ % repeat_title_==0)
				this.addTitleRow(num_rows_so_far_ + "");		
	}
	public void writeRow(VectorS vs, String style) {
		FFile.write(writer_, "<tr" + style + ">\t");
		for (int i = 0; i < vs.size(); ++i)
			FFile.write(writer_, " <td>" + vs.get(i) + "</td>");
		FFile.write(writer_, "</tr>\n");
		checkRepeatedTitle();
	}


	public void writeRow(VectorS vs, VectorS styles) {
		checkRepeatedTitle();
		String style = "";
		FFile.write(writer_, "<tr" + style + ">\t");
		for (int i = 0; i < vs.size(); ++i) {
			String sty = styles.get(i);
			if (sty == null) sty = "";
			String txt = vs.get(i);
			if (txt == null) txt = "";
			FFile.write(writer_, "\t<td " + sty + ">" + txt + "</td>");
		}
		FFile.write(writer_, "</tr>\n");
	}

	public static String formatHtmlRow(VectorI vi, MapIMapIS mms, String style) {
		StringBuffer sb = new StringBuffer();
		sb.append("<tr" + style + ">\n");
		for (int i = 0; i < vi.size();) {
			int id = vi.get(i);
			if (id == 0) {
				sb.append("\t<td></td>");
				++i;
			} else {
				sb.append(String.format("\t<td colspan=\"%d\" >%s</td>", id, mms
						.get(id).get(i)));
				i += id;
			}
		}
		sb.append("</tr>\n");
		return sb.toString();
	}

	public static String formatHtmlRow(VectorI vi, MapIMapIS mms,
			MapIMapIS mmStyle) {
		StringBuffer sb = new StringBuffer();
		sb.append("<tr" + CStyle.center + " >\n");//"+style+"
		for (int i = 0; i < vi.size();) {
			int id = vi.get(i);
			if (id == 0) {
				sb.append("\t<td></td>");
				++i;
			} else {
				sb.append(String.format("\t<td %s colspan=\"%d\" >%s</td>", mmStyle
						.get(id).get(i), id, mms.get(id).get(i)));
				i += id;
			}
		}
		sb.append("</tr>\n");
		return sb.toString();
	}

	/**format a irregular shaped table
	 * MapMapIIS mms:	len-->ib-->text
	 * 
	 * */
	public void addChart(MapIMapIS mms, int width, String style) {
		FCharter chart = new FCharter(width);
		VecVecI vvi = chart.doChart(mms);
		//for (VectorI vi: vvi){
		for (int i = vvi.size() - 1; i >= 0; --i)
			FFile.write(writer_, formatHtmlRow(vvi.get(i), mms, style));
		this.endTable();
		return;
	}

	/**format a irregular shaped table
	 * MapMapIIS mms:	len-->ib-->text
	 * MapMapIIS mmStyle:	len-->ib-->style
	 * */
	public void addChart(MapIMapIS mms, int width, MapIMapIS mmStyle) {
		FCharter chart = new FCharter(width);
		VecVecI vvi = chart.doChart(mms);
		//for (VectorI vi: vvi){
		for (int i = vvi.size() - 1; i >= 0; --i)
			FFile.write(writer_, formatHtmlRow(vvi.get(i), mms, mmStyle));
		//this.endTable();
		return;
	}

	/**adding a sparse matrix*/
	public void addVectorMap(VectorX<MapSD> vm) {
		for (String key : vm.sum().keySet()) {
			VectorD vd = vm.getVD(key);
			addRow(key + "\t" + vd.join("\t"));
		}
	}

	/**adding a sparse matrix*/
	public void newTable(String tableName, MapSMapSD mm) {
		VectorS v = (VectorS) mm.sum().toVectorKey();
		newTable(tableName, "\t" + mm.toVectorKey().join("\t"));
		for (String key : v) {
			VectorD vd = mm.getMDouble(key).ValuesToVector();
			addRow(key + "\t" + vd.join("\t"));
		}
	}

	public void printObj(MapSI m, String title) {
		endTable();
		FFile.write(writer_, "<b>" + title + "</b><br>");
		FFile.write(writer_, m.join("=", "<br>") + "<br>");
	}

	public void printTable(MapSI m, String title) {
		this.newTable(title, "key\tvalue");

		for (Map.Entry<String, Integer> e : m.entrySet()) {
			FFile.write(writer_, String.format("<tr><td>%s</td><td>%d</td></tr>\n", e
					.getKey(), e.getValue()));
		}
		endTable();
	}

	public HtmlPage extPageF(String ext) {
		//FFile.mkdirs(title+ext)
		return extPage(ext, title, ext);
	}

	public HtmlPage extPage(String ext) {
		return extPage(ext, null);
	}

	public int extTablePage(String ext, String file, String title) {
		if (!FFile.exist(file)) return 0;
		
		HtmlPage page = extPage(ext);
		page.newTable(ext, title);
		FFile.num_lines_ = 0;
		for (VectorS line : FFile.enuRows(file)) 	page.addRow(line);
		page.endTable();
		return FFile.num_lines_;
	}
	
	public HtmlPage extPageFolder(String ext, String subFolder) {
		return extPage(ext, subFolder, null);
	}

	public HtmlPage extPage(String ext, String newTitle) {
		return extPage(ext, newTitle, null);
	}
	public String relative_path_ = null;//relative path to parent page

	public HtmlPage extPage(String ext, String newTitle, String subFolder) {
		if (newTitle == null) newTitle = ext;//.tr("\\.");

		String fp = null;
		String fd = null;
		if (subFolder != null) {
			fp = subFolder + "/" + title + ext;
			fd = folder + "/" + subFolder;
		} else {
			fp = title + ext;
			fd = folder;
		}
		HtmlPage th = new HtmlPage(title + ext, fd);
		th.relative_path_ = fp + ".html";

		str_result_ = FHtml.addHref(newTitle, fp + ".html");
		if (write_) addString("  " + str_result_);

		children_.add(th);
		return th;
	}

	public BufferedWriter extFileF(String ext) {
		//FFile.mkdirs(title+ext)
		return extFile(ext, title, ext);
	}

	public BufferedWriter extFile(String ext) {
		return extFile(ext, null);
	}

	public BufferedWriter extFile(String ext, String subFolder) {
		return extFile(ext, subFolder, null);
	}

	public BufferedWriter extFile(String ext, String subFolder, String newTitle) {
		if (newTitle == null) newTitle = title + ext;

		String file_path = null;
		if (subFolder != null) file_path = subFolder + "/" + newTitle;
		else file_path = newTitle;

		str_result_ = FHtml.addHref(ext, file_path);
		if (write_) addString("  " + str_result_);
		BufferedWriter writer = FFile.newWriter(folder + file_path);
		writers_.add(writer);
		return writer;
	}

	public String extFileName(String ext) {
		String fp = this.file + ext;
		FHtml.addHref(ext, fp);
		return fp;
	}
	public boolean write_ = true;
	public String str_result_ = null;

	public VectorX<BufferedWriter> writers_ = new VectorX<BufferedWriter>(
			BufferedWriter.class);
	public VectorX<HtmlPage> children_ = new VectorX<HtmlPage>(HtmlPage.class);

	public void closeChildren() {
		for (BufferedWriter writer : writers_)	FFile.close(writer);
		writers_.clear();
		for (HtmlPage page : children_)	page.close();
		children_.clear();
	}

	public boolean close() {
		closeChildren();
		if (writer_ == null) return false;
		this.endTable();
		FFile.write(writer_, FSystem.printTime());
		FFile.write(writer_, _pageEnd);
		FFile.close(writer_);
		writer_ = null;
		return true;
	}

	public void addLink(String name, String url) {
		addRow(FHtml.addHref(name, url));
	}
}
