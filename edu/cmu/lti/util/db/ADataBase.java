package edu.cmu.lti.util.db;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VecVecS;
import edu.cmu.lti.nlp.StopWord;
import edu.cmu.lti.nlp.Tokenizer;
import edu.cmu.lti.util.db.DBSchema.Field;
import edu.cmu.lti.util.db.DBSchema.Table;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

/**
 * an abstract database class
 * 
 * @author nlao
 * 
 */
public class ADataBase {
	public Connection con = null;
	// This is basically info the driver delivers
	// about the DB it just connected to. I use
	// it to get the DB version to confirm the
	// connection in this example.
	DatabaseMetaData dbmd = null;

	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String server;
		public String db;
		public String port;
		public String user;
		public String pass;
		public String driver = null;
		public String dbType = null;
		public String url = null;

		public Param(Class c) {// throws IOException{
			super(c);
			// port = getInt("port",9005);
			server = getString("server", "adb1.lti.cs.cmu.edu");
			port = getString("port", "5432");
			db = getString("db", "JP_QAC");
			user = getString("user", "root");
			pass = getString("pass", "dbmaster");
		}
	}
	public Param p = null;// new Param();

	public ADataBase(Class c, String dbType, String driver) {// , String
		// name) {
		p = new Param(c);
		p.dbType=dbType;
		p.driver = driver;
		// p.url = "jdbc:"+name+"://"+p.server+"/"
		// +p.db+"?useUnicode=true&characterEncoding=utf8";
	}

	protected void connect() {
		p.url = "jdbc:"+p.dbType+"://"+p.server+"/"+p.db;
		//+":"+p.port

		try {
			Class.forName(p.driver);
			con = DriverManager.getConnection(p.url, p.user, p.pass);
			dbmd = con.getMetaData(); // get MetaData to confirm
			System.out.println("Connecting to " 
					+ dbmd.getDatabaseProductName() + " "
					+ dbmd.getDatabaseProductVersion() 
					+ " successfully.\n");
			this.createStatement();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return;
	}

	protected void finalize() throws Throwable {
		super.finalize();
		con.close();
	}

	/**
	 * not recommended for large tables, cause all strings
	 * will be hold in memory
	 * assuming all columns are of String type
	 * @param query
	 *          e.g. "select * from gene_names"
	 * @return
	 */
	public VecVecS getStrings(String query) {
		VecVecS vvs = new VecVecS();
		try {
			//createStatement();
			ResultSet rlt = state.executeQuery(query);
			ResultSetMetaData meta = rlt.getMetaData();
			int nCol = meta.getColumnCount();
			if (rlt == null) return null;
			while (rlt.next()) {
				VectorS vs = new VectorS(nCol);
				for (int i = 1; i <= nCol; ++i)
					vs.set(i - 1, rlt.getString(i));
				// System.out.println( rlt.getString(1));
			}
			rlt.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return vvs;
	}
	//assuming one column, is of String type
	public VectorS getColumnStrings(String query) {
		VectorS vs = new VectorS();
		try {
			//createStatement();
			ResultSet rlt = state.executeQuery(query);
			if (rlt == null) return null;
			while (rlt.next()) 
				vs.add( rlt.getString(1));			
			rlt.close();
			//state.close();
		} catch (Exception e) {
			System.err.println("error executing query="+query);
			e.printStackTrace();			
		}
		return vs;
	}
	public Statement state = null;// =
																// db.con.createStatement();

	public Statement createStatement() {
		try {
			state = con.createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY); 
			con.setAutoCommit(false); 
			state.setFetchSize(1000);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return state;
	}
	public boolean tableExists(String name) {
		System.err.println("tableExists() not implemented yet by" 
				+ this.getClass().getName());
		return false;
	}
	
	public void dropCreateTable(String table, String variables) {
		System.out.println("create table: " + table + "\n");
		try {			
			state.executeUpdate("DROP TABLE IF EXISTS "+table+" CASCADE;");
			state.executeUpdate("create table "+table+" ("+variables+")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static String normalizeString(String s){
		s=s.replaceAll("'","''");
		return s;
	}
	public int nRow=0;
	public void insertRow(String table, String values) {
		++nRow;
		if (nRow==1000){
			nRow=0;
			System.out.print("k");
		}
		//System.out.println("create table: " + name + "\n");
		try {			
			state.executeUpdate("insert into "+table+" values ("+values+")");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public void test() throws SQLException {
		// create a statement that we can use later
		Statement state = con.createStatement();
		String sqlText = "create table jdbc_demo (code int, text varchar(20))";
		state.executeUpdate(sqlText);
		sqlText = "insert into jdbc_demo values (1,'One')";

		state.executeUpdate(sqlText);
		sqlText = "insert into jdbc_demo values (3,'Four')";
		state.executeUpdate(sqlText);
		state.executeUpdate(sqlText);
		
		
		sqlText = "update jdbc_demo set text = 'Three' where code = 3";
		state.executeUpdate(sqlText);
		System.out.println(state.getUpdateCount()
				+ " rows were update by this statement\n");
		
		
		System.out.println("\n\nNow demostrating a prepared statement...");
		sqlText = "insert into jdbc_demo values (?,?)";
		System.out.println("Looping three times filling in the fields...\n");
		PreparedStatement prep = con.prepareStatement(sqlText);
		for (int i = 10; i < 13; i++) {
			System.out.println(i + "...\n");
			prep.setInt(1, i); // set column one (code) to i
			prep.setString(2, "HiHo"); // Column two gets a string
			prep.executeUpdate();
		}
		prep.close();
		ResultSet results = state.executeQuery("select * from jdbc_demo");
		if (results != null) {
			while (results.next()) {
				System.out.println("code = " + results.getInt("code") + "; text = "
						+ results.getString(2) + "\n");
			}
		}
		results.close();
		sqlText = "drop table jdbc_demo";
		state.executeUpdate(sqlText);
	}
	public void findInterestingTextFields(DBSchema sch){
		System.out.println("findInterestingTextFields("+sch.name+")");
		int nSample=1000;
		//int nUniqValue=(int) Math.log(nSample);
		FFile.mkdirs("findInterestingTextFields");
		
		for (Table t: sch.vTable){
			for (Field f: t.vField){
				if (! (f.type=='t' || f.type=='c')) continue;
				VectorS vs=getColumnStrings("select "+f.name+" from "+t.name
						+" limit "+nSample );
				int nRow=vs.size();
				if (nRow==0) continue;
				int nTh=(int) Math.sqrt(nRow);
				int n= vs.toSet().size();
				
				if (n<nTh) continue;

				vs.save("findInterestingTextFields/"
						+n+"_"+t.name+"."+f.name);

				if (vs.totLength()/nRow > 1000	//remove big untextlike fields
						&& !f.name.equals("description")){
					System.err.println("too large field "+t.name+"."+f.name);
					continue;
				}
				
				
				System.out.println("Found "+t.name+"."+f.name
						+" with "+n+"/"+nRow);
				f.bInterestingText=true;
				
			}
		}
		return;
	}
	public void dumpGraph(DBSchema sch){
		System.out.println("dumpGraph("+sch.name+")");
		
		//this.createStatement();
		int maxRow=100000;
		boolean bWordLinks=true;
		
		String fd =bWordLinks
				?"topRow"+maxRow+".w/"
				:"topRow"+maxRow+"/";
		FFile.mkdirs(fd);
		
		try {			
			for (Table t: sch.vTable){
				if (t.nRow==0) continue;
				if (!t.bHasFK) continue;
				if (t.iPrimaryKey==-1 && ! t.bIsLink) continue;
				
				String cmd= "select * from "+t.name;
				if (maxRow>0)		cmd+= " limit "+maxRow;
				ResultSet results = state.executeQuery(cmd);
				
				if (results == null)	continue;
				
				System.out.println("dumping "+t.name);
				
				BufferedWriter bw= FFile.newWriter(fd+t.name);

				if (t.bIsLink){
					String ET0= sch.vTable.get(t.vField.get(0).iForeignTable).name;
					String ET1= sch.vTable.get(t.vField.get(1).iForeignTable).name;
					while (results.next()) {
						FFile.write(bw, "%s\t%s:%d\t%s:%d\n"
								,t.name,ET0,results.getInt(1)
								,ET1, results.getInt(2));
					}
				}
				else {
					while (results.next()) {
						String idPK = results.getInt(t.iPrimaryKey+1)+"";
						
						for (Field f: t.vField){
							if (bWordLinks && f.bInterestingText){								
								String txt = results.getString(f.id+1);
								if (txt==null)
									continue;
								for (String word:Tokenizer.tokenize(txt))
									FFile.write(bw, "W#%s\t%s:%s\tW:%s\n"
										,f.name,t.name,idPK	,word);
								
							}
							else{
								if (f.iForeignTable==-1) continue;
								String fTable= sch.vTable.get(f.iForeignTable).name;
								FFile.write(bw, "%s.%s\t%s:%s\t%s:%d\n"
									,t.name,fTable// f.name
									,t.name,idPK
									,fTable, results.getInt(f.id+1));
							}
						}
					}
				}
				results.close();
				FFile.close(bw);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		System.out.println("\n done");

	}
	
}
