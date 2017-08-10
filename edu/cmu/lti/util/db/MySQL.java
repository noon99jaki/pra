package edu.cmu.lti.util.db;

import edu.cmu.lti.algorithm.container.VectorS;



public class MySQL extends ADataBase{

	public MySQL(){//Class c
		super(MySQL.class,"mysql", "com.mysql.jdbc.Driver");
		
		p.server="hops";p.port="3306";	p.db = "citeseerx";
		p.user ="bbd";	p.pass="citeseer$";		
			
//		p.url = "jdbc:mysql://"+p.server+":3306/"
	//		+p.db+"?useUnicode=true&characterEncoding=utf8";
		
		this.connect();
	}
	public static void main(String args[]) {
		MySQL db = new MySQL();
		VectorS vs= db.getColumnStrings("show tables");
		return;
	}
}
