package edu.cmu.lti.util.html;

public class FHtml {
	public static class CStyle {
		public static final String white=" bgcolor=\"#ffffff\"";	
		public static final String red=" bgcolor=\"#ffb0b0\"";	
		public static final String green=" bgcolor=\"#b0ffb0\"";	
		public static final String blue=" bgcolor=\"#b0b0ff\"";	
		public static final String yellow=" bgcolor=\"#80b0b0\"";	
		public static final String purple=" bgcolor=\"#b080b0\"";	
		
		public static final String center=" align=\"center\"";
		public static final String border1= " border=\"1\"";
		public static final String border0= " border=\"0\"";
		public static String alignRight= " align=\"right\"";
		public static String alignCenter= " align=\"center\"";
		public static String italic=" style=\"font-style: italic;\"";
		public static String bold=	" style=\"font-weight: bold;\"";
		public static final String collapse=	"style=\"border-collapse: collapse\"";
		//cornsilk3
		public static final String width100=" width=\"100%";
		//id="table2" 
		public static final String cellspacing0="cellspacing=\"0\"";
		public static final String cellpadding0=	"cellpadding=\"0\""; 
		public static final String cellpadding2=	"cellpadding=\"2\""; 
		public static final String cellpadding4=	"cellpadding=\"4\""; 
		public static final String bordercolorlight0=" bordercolorlight=\"#000000" ;
		public static final String bordercolordark0=	"bordercolordark=\"#000000\""; 

		public static String dftStyle=	center+border0+cellspacing0+cellpadding4+collapse;
	}
	public static String p(String txt, String style){
		return "<p "+style+">"+ txt +"</p>"; 
	}
	public static String addBold(String s){
		return "<b>"+ s +"</b>"; 
	}
	public static String addFC(String s, EColor c){		
		return "<font color=\"#"+c.code+"\">"+ s +"</font>"; 
	}
	//<font bgcolor="#CC00dd">asdf</font>
	public static String addBG(String txt,EColor c){//String c){//
		return addBG(txt,c.code);
	}
	public static String addBG(String txt,String c){// 
		return "<span style=\"background-color: "+
			c+"\">"+ txt +"</span>"; 
	}
	public static String backGround(EColor color){
		if (color==null) return "";
		return backGround(color.code);
	}
	public static String backGround(String color){
		if (color==null) return "";
		return " bgcolor=\""+color+"\"";	
	}
	
	public static String addImage(String url){
		return "<img border=\"0\" src=\""+ url + "\">"; 
	}	

	public static String addHref(String s, String url){
		return "<a href=\""+ url + "\">"+ s +"</a>"; 
	}
	public static String addName(String s, String name){
		return "<a name=\""+ name + "\">"+ s +"</a>"; 
	}
	static public String bold(String txt){
		return "<b>"+txt+"</b>";
	}
}
