package edu.cmu.pra.data;

import edu.cmu.lti.util.db.DBSchema;
import edu.cmu.lti.util.db.PostgreSQL;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.run.Param;

public class CiteSeer {
	public static void dumpSchema(){//, String fn){
		String dbName="citeseerx";
		String fn=dbName +".sch";
		PostgreSQL db = new PostgreSQL();
		//PostgreSQL db = new PostgreSQL();
		DBSchema sch=(DBSchema) FFile.loadObject(fn+".obj");
		if (sch==null){
			sch=db.getSchema(dbName);		
			//db.findInterestingTextFields(sch);
			FFile.saveObject(sch, fn+".obj");
			sch.save(fn+".txt");
		}
	}
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		dumpSchema();
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
