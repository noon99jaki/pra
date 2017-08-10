package edu.cmu.lti.util.db;

import java.io.BufferedWriter;
import java.io.Serializable;
import java.util.Map;

import edu.cmu.lti.algorithm.Interfaces.IGetIntByStr;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.VectorX;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VecVecS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.pra.CTag;

/**
 * TODO: convert two-column tables to relation
 * TODO: remove isolated tables
 * @author nlao
 *
 */
public class DBSchema implements Serializable{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD

	public static class Field implements Serializable{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public int id;
		public char type;
		public String name;
		public int iForeignTable=-1;
		public boolean bInterestingText=false;
		public Field(int id, char type, String name){
			this.id=id;
			this.type = type;
			this.name=name;			
		}
		
		public String toString(){
			if (iForeignTable==-1)
				return name+ "("+type+")";
			else
				return name+ "("+type+") FK="+iForeignTable;
		}
	}
	public static class Table implements IGetIntByStr, Serializable{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public VectorX<Field> vField= new VectorX<Field>(Field.class);
		public String name;
		public int nRow=0;
		public int id ;
		public int iPrimaryKey=-1;
		public boolean bIsLink=false;
		public boolean bHasFK=false;
		public Table(int id, String name){
			//super(Tuple.class);
			this.id=id;
			this.name=name;
		}
		public String toString(){
			return name+ " (nRow="+nRow+")\n"+vField.join("\n");
		}
		public Integer getInt(String name){
			if (name.equals(CTag.size))
				return nRow;
			return null;			
		}
		public Field addField(String type, String name){
			Field f= new Field(vField.size(), type.charAt(0), name);
			vField.add(f);
			return f;
		}
	}
	public static class Index implements Serializable{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public VectorX<Field> vTuple= new VectorX<Field>(Field.class);
		
	}
	public static class ForeignKey  implements IGetIntByStr, Serializable{
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD

		public int iTable;
		public int iCol;
		public int iTableF;
		//public int iColForeign;
		public int nRow=0;
		public ForeignKey(int iTable, int iCol, 
				int iTableF){//, int iColForeign){
			//super(Tuple.class);
			this.iTable=iTable;
			this.iCol=iCol;
			this.iTableF=iTableF;
			//this.iTable=iTable;
			
		}
		
		public Integer getInt(String name){
			if (name.equals(CTag.size))
				return nRow;
			return null;			
		}
	}
	
	public String name;
	public VectorX<Table> vTable= new VectorX<Table>(Table.class);
	public MapSI mTable= new MapSI();
	
	public VectorX<ForeignKey> vFK= new VectorX<ForeignKey>(ForeignKey.class);
	
	
	public Table addTable(String name){//Table t){
		Table t = new Table(vTable.size(), name);
		mTable.put(t.name, vTable.size());
		vTable.add(t);
		return t;
	}
	public Table getTable(String name){
		Integer i= mTable.get(name);
		if (i==null) return null;
		return vTable.get(i);
	}
	public DBSchema(String name){
		this.name=name;
	}
	public void loadTableAcronym(String fn){
		VecVecS vvs=FTable.loadTable(fn);
		miTableAcronym.clear();
		for (VectorS vs: vvs){
			//String tableName= vs.get(0);
			Integer i= mTable.get(vs.get(0));
			if (i==null){
				System.err.println("unable to find TABLE=" +vs.get(0));
				continue;
			}				
			miTableAcronym.put(vs.get(1), i);
		}
	}
	public MapSI miTableAcronym= new MapSI();
	public Integer matchTableName(String fieldName){
		Integer i= mTable.get(fieldName);
		if (i!=null) return i;
		
		if (fieldName.endsWith("s")){
			String foreign=fieldName.substring(0, fieldName.length()-1);
			i= mTable.get(foreign);
			if (i!=null) return i;
		}
		
		if (fieldName.endsWith("id")){
			String foreign=fieldName.substring(0, fieldName.length()-2);
			i= mTable.get(foreign);
			if (i!=null) return i;
		}
		
		for (Map.Entry<String,Integer> e: miTableAcronym.entrySet()){
			String acronym=e.getKey();
			if (fieldName.endsWith(acronym) ||
				fieldName.endsWith(acronym+"s")||
					fieldName.endsWith(acronym+"id")){
				return e.getValue(); 
			}
		}
		
		for (Table t: this.vTable)
			if (fieldName.endsWith(t.name) ||
				fieldName.endsWith(t.name+"s")||
					fieldName.endsWith(t.name+"id")){
				//System.out.println("very fuzzy FK match: FIELD="
						//+fieldName +" TABLE="+t.name);
				return t.id; 
			}
		
		return null;
	}
	public void findForeignKeys(){
		System.out.println("findForeignKeys()\n");
		int nLinkFK=0;
		int nLinkFKRow=0;
		for (Table t: this.vTable){
			for (Field f: t.vField){
				if (f.type!='i') continue;
				Integer i=matchTableName(f.name);
				if (i==null) continue;
				if (i== t.id){
					t.iPrimaryKey=f.id;
					continue;// matching itself is uninteresting
				}
				
				ForeignKey fk=new ForeignKey(t.id, f.id, i);
				fk.nRow=t.nRow;
				vFK.add(fk);
				f.iForeignTable= i;
				t.bHasFK=true;
			}				
			//if (t.vField.size()<=3)		System.out.println("\n\n"+t);
			if (t.vField.size()==2){
				if (t.vField.get(0).iForeignTable!=-1)
					if (t.vField.get(1).iForeignTable!=-1){
						nLinkFK+=1;
						nLinkFKRow+= t.nRow;//*2;
						t.bIsLink=true;
					}
			}
			else{
				if (t.iPrimaryKey==-1){
					System.err.println(
						"missing primary key TABLE="+t.name);//			continue;
				}
			}
					
		}
		System.out.println(String.format(
				"nLinkFK=%d, nLinkFKRow=%d"
				, nLinkFK, nLinkFKRow));
		
		return;
	}
	public String toString(){
		//return name+ " #table="+vTable.size()+"\n"
			//+vTable.join("\n\n");
		return String.format("%s #table=%d #rows=%d #FK=%d #FKRows=%d"
			, name,vTable.size(), vTable.getVI(CTag.size).sum()
			, vFK.size(), vFK.getVI(CTag.size).sum());
	}
	public void save(String fn){
		BufferedWriter bw= FFile.newWriter(fn);
		for (Table t: this.vTable){
			FFile.write(bw, t.name+ "("+t.nRow+")\n");
			for (Field f: t.vField)
				if (f.iForeignTable!=-1){
					Table ft= vTable.get(f.iForeignTable);
					FFile.write(bw, f.name+ "-->"+ft.name+"\n");
				}
			FFile.write(bw,"\n");
		}
		FFile.close(bw);
	}
}
