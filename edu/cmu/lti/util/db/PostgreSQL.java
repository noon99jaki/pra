package edu.cmu.lti.util.db;

import java.io.BufferedReader;
import java.sql.ResultSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.util.db.DBSchema.Field;
import edu.cmu.lti.util.db.DBSchema.Table;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;

public class PostgreSQL extends ADataBase {
	public PostgreSQL() {
		super(PostgreSQL.class,"postgresql", "org.postgresql.Driver");
		//p.driver = "org.postgresql.Driver";

		//p.server="malt";p.port="5432";	p.db = "aarnold";
		//p.user ="aarnold";	p.pass="";		
		p.server="hops";p.port="5432";	p.db = "flymine_17";
		p.user ="postgres";	p.pass="admin";		
		this.connect();
		// jdbc:postgresql://199.199.199.199/test
	}
	
	/*
	 * SELECT a.attname as \"column\", pg_catalog.format_type(a.atttypid,
a.atttypmod) as \"datatype\"
FROM pg_catalog.pg_attribute a
WHERE a.attnum > 0
AND NOT a.attisdropped
AND a.attrelid = (
SELECT c.oid
FROM pg_catalog.pg_class c
LEFT JOIN pg_catalog.pg_namespace n ON n.oid = c.relnamespace
WHERE c.relname ~ '^(TABLE_NAME_HERE)$'
AND pg_catalog.pg_table_is_visible(c.oid)
)
	 */
	/*
    DROP TABLE IF EXISTS demo CASCADE;
    DROP SEQUENCE IF EXISTS <name>;
    DROP VIEW IF EXISTS <name> CASCADE;
    DROP INDEX IF EXISTS <name> CASCADE;
create table demo (code int, text varchar(20))
	 */
	public boolean tableExists(String name) {
		System.err.println("tableExists() not implemented yet by" 
				+ this.getClass().getName());
		return false;
	}


	public DBSchema getSchema(String dbName){
		System.out.println("getSchema for "+dbName);
		BufferedReader br=FSystem.cmdGetStream("pg_dump -s "+dbName);
		
		DBSchema sch= new DBSchema(dbName);
		//Pattern paTable = Pattern.compile("CREATE TABLE (\\?*) \\(");
		//BufferedWriter bw = FFile.bufferedWriter("SGD.abstract.lines");
		String line = null;
		while ((line = FFile.readLine(br)) != null) {
			if (line.startsWith("CREATE TABLE"))
				getSchemaTable(sch, line, br);
		}
		//FFile.write(bw, pmid + "\t" + line + "\n");
		//FFile.close(bw);
		//createStatement();
	  try {
	  	ResultSet result = state.executeQuery(
	  		"SELECT relname, reltuples FROM pg_class");
	
			while (result.next()) { // process results one row at a time
			  String table = result.getString(1);
			  int rows = result.getInt(2);
			  Table t=sch.getTable(table);
			  if (t==null) continue;
			  t.nRow= rows;
			}
    } catch (Exception e) {
      e.printStackTrace();
    }
		sch.loadTableAcronym("table.acronym.txt");
    sch.findForeignKeys();
		return sch;
	}
	private void getSchemaTable(DBSchema sch, String line, BufferedReader br){
		final Pattern paTuple = Pattern.compile(" *(\\S+) +(\\S+)[, ]*.*");
		String tbName=line.substring(13,line.length()-2);
		Table t= sch.addTable(tbName);
		while ((line = FFile.readLine(br)) != null) {
			if (line.startsWith(")"))	break;
			
			Matcher ma= paTuple.matcher(line);
			if (!ma.matches()) {
				System.out.println("bad tuple: " + line);
				continue;
			}
			String fieldName = ma.group(1);
			String type= ma.group(2);
			Field f=t.addField(type, fieldName);
			if (fieldName.equals("id") 
					|| fieldName.equals(t.name+"id"))
				t.iPrimaryKey= f.id;
			//t.vField.add(new Field(type.charAt(0), tpName));

		}
		//return t;
	}


	
	public boolean tableSizes() {
		//System.err.println("tableSizes() not implemented yet by" 
				//+ this.getClass().getName());
		
	  try {
	  	ResultSet result = state.executeQuery(
	  		"SELECT relname, reltuples FROM pg_class");
	
			while (result.next()) { // process results one row at a time
			  String table = result.getString(1);
			  int rows = result.getInt(2);
			  
			}
    } catch (Exception e) {
      e.printStackTrace();
    }

		return false;
	}
	public static void dumpSchema(String dbName){//, String fn){
		String fn="schema."+dbName;

		PostgreSQL db = new PostgreSQL();
		try {
			DBSchema sch=(DBSchema) FFile.loadObject(fn+".obj");
			if (sch==null){
				sch=db.getSchema(dbName);		
				db.findInterestingTextFields(sch);
				FFile.saveObject(sch, fn+".obj");
				sch.save(fn+".txt");
			}
			//db.dumpGraph(sch);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	public static void main(String args[]) {
		// db.test();
		/*Statement state = db.con.createStatement();
		ResultSet results = state.executeQuery(
				"select * from gene_names");
		if (results != null) 
			while (results.next()) 
				System.out.println( results.getString(1));			
		
		results.close();
		*/
	}
}
