/**
 * 
 */
package edu.cmu.lti.tools.visualize.graphviz;



/**
 * @author nlao
 * see complete lists at www.graphviz.org/doc/info/
 */
public class Tags {

	public static enum ETag {
		label,color,fontsize,fontname,style,fontcolor;
	}
	
	public static enum EStyle {
		filled,bold, dotted,dashed,invis;
	}
/*	public static enum EFontname {
		Palatino_Italic("Palatino-Italic");
		private EFontname(){}
		private EFontname(String name){
			this.name = name;
		}
		private String name = null;
		public String toString(){
			if (name != null) return name;
			return name();
		}
	}*/
	public static class CFontname {
		public static final String Palatino_Italic="Palatino-Italic";
	}
/*	public static enum EColor {
		slateblue, hotpink,
		red, green, blue, cyan, magenta, yellow, orange;
	//	public static final String title = "color";
	}*/
	public static enum EShape{
		box, polygon, ellipse, point, egg, triangle, diamond
		, trapezium, parallelogram, hexagon, octagon, doublecircle
		, tripleoctagon, invtriangle, invtrapezium		
	}
	
	public static enum EArrowType{
		normal, dot, odot, inv, invdot, invodot, none
	}	
	public static enum EOutFormat{
		bmp//Windows Bitmap Format 
		, canon 
		, dot 
		, xdot//DOT 
		, cmap//Client-side imagemap (deprecated) 
		, dia//Dia format 
		, eps//Encapsulated PostScript 
		, fig//FIG 
		, gd 
		, gd2//GD/GD2 formats 
		, gif//GIF 
		, gtk//GTK canvas 
		, hpgl//HP-GL/2 
		, ico//Icon Image File Format 
		, imap 
		, cmapx//Server-side and client-side imagemaps 
		, imap_np 
		, cmapx_np//Server-side and client-side imagemaps 
		, ismap//Server-side imagemap (deprecated) 
		, jpg 
		, jpeg 
		, jpe//JPEG 
		, mif//FrameMaker MIF format 
		, mp//MetaPost 
		, pcl//PCL 
		, pdf//Portable Document Format (PDF) 
		, pic//PIC 
		, plain 
		//, plain-ext//Simple text format 
		, png//Portable Network Graphics format 
		, ps//PostScript 
		, ps2//PostScript for PDF 
		, svg 
		, svgz//Scalable Vector Graphics 
		, tga//Truevision Targa Format (TGA) 
		, tif 
		, tiff//TIFF (Tag Image File Format) 
		, vml 
		, vmlz//Vector Markup Language (VML) 
		, vrml//VRML 
		, vtx//Visual Thought format 
		, wbmp//Wireless BitMap format 
		, xlib//Xlib canvas 
		;		
	};////gtk;//eps;//fig;//hpgl;//png;

	
}

/*
digraph G {
xyz [label = "hello\nworld",color="slateblue",fontsize=24,fontname="Palatino-Italic",style=filled,fontcolor="hotpink"];
node [style=filled];
red [color=red];
green [color=green];
blue [color=blue,fontcolor=black];
cyan [color=cyan];
magenta [color=magenta];
yellow [color=yellow];
orange [color=orange];
red -> green;
red -> blue;
blue -> cyan;
blue -> magenta;
green -> yellow;
green -> orange;
}
*/


/*
digraph G {
graph [center=true rankdir=LR bgcolor="#808080"]
edge [dir=none]
node [width=0.3 height=0.3 label=""]
{ node [shape=circle style=invis]
	1 2 3 4 5 6 7 8  10 20 30 40 50 60 70 80
}
{ node [shape=circle]
	a b c d e f g h  i j k l m n o p  q r s t u v w x
}
{ node [shape=diamond]
	A B C D E F G H  I J K L M N O P  Q R S T U V W X
}
1 -> a -> {A B} [color="#0000ff"]
2 -> b -> {B A} [color="#ff0000"]
3 -> c -> {C D} [color="#ffff00"]
4 -> d -> {D C} [color="#00ff00"]
5 -> e -> {E F} [color="#000000"]
6 -> f -> {F E} [color="#00ffff"]
7 -> g -> {G H} [color="#ffffff"]
8 -> h -> {H G} [color="#ff00ff"]
{ edge [color="#ff0000:#0000ff"]
	A -> i -> {I K}
	B -> j -> {J L}
}
{ edge [color="#00ff00:#ffff00"]
	C -> k -> {K I}
	D -> l -> {L J}
}
{ edge [color="#00ffff:#000000"]
	E -> m -> {M O}
	F -> n -> {N P}
}
{ edge [color="#ff00ff:#ffffff"]
	G -> o -> {O M}
	H -> p -> {P N}
}
{ edge [color="#00ff00:#ffff00:#ff0000:#0000ff"]
	I -> q -> {Q U}
	J -> r -> {R V}
	K -> s -> {S W}
	L -> t -> {T X}
}
{ edge [color="#ff00ff:#ffffff:#00ffff:#000000"]
	M -> u -> {U Q}
	N -> v -> {V R}
	O -> w -> {W S}
	P -> x -> {X T}
}
{ edge [color="#ff00ff:#ffffff:#00ffff:#000000:#00ff00:#ffff00:#ff0000:#0000ff"]
	Q -> 10
	R -> 20
	S -> 30
	T -> 40
	U -> 50
	V -> 60
	W -> 70
	X -> 80
}
}
*/