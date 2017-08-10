package edu.cmu.lti.tools.visualize.r;

import edu.cmu.lti.algorithm.container.VectorD;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.container.VecVecD;
import edu.cmu.lti.tools.visualize.graphviz.Graphviz;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
	
/**
 * Instead of learn R language
 * you just need to call functions here. 
 * @author nlao
 * 
 */
public class RWrapper {
	
	
	/*
	public void drawBarPlot(String fn, String title, VectorS xLabel
			, String yLabel, VectorI yValues){

		String fp = outputFolder+"/"+fn;
		StringBuffer rBuf = new StringBuffer();
		rBuf.append("bitmap(\""+fp+"\", width=350/72, height=270/72, pointsize=12, res=72)\n");
		//rBuf.append("png(\""+pngPathSafe+"\", 350, 270, 10)\n");
		rBuf.append("vy = c("+yValues.join(", ") + ")\n");
		rBuf.append("vx = c(\""+xLabel.join("\",\"") + "\")\n");
		//rBuf.append("label = c(\"Wrong\", \"Correct\")\n");
		rBuf.append("title = \""+title+"\"\n");
		rBuf.append("m = matrix(c(W,R), 2, 9, T, list(label,ne))\n");
		rBuf.append("par(cex=0.7,las=3)\n");
		rBuf.append("barplot(m,legend = rownames(m), main = title, xlab=\"Atype\", ylab=\"Frequency\")\n");
		rBuf.append("dev.off()\n");
		runR( rBuf.toString(), batchPath );
	}
	
	public void drawHistogram(int[] inputData, int maxValue, String title , String outputPngPath){
	String batchPath = Config.outPath+"/"+title+".r";
	String pngPath   = outputPngPath;
	String pngPathSafe  = pngPath.replaceAll("\\\\","/");
	StringBuffer input = new StringBuffer();
	for(int i=0;i<inputData.length;i++){
		if (inputData[i]>=1 && inputData[i]<=20){
			if(input.length()>0){
				input.append(", ");
			}
			input.append(inputData[i]);
		}
	}
	StringBuffer rBuf = new StringBuffer();
	rBuf.append("# "+p.rPath+" CMD BATCH "+batchPath+"\n");
	rBuf.append(title + " = c("+ input.toString() +")\n");
	//rBuf.append("png(\""+pngPathSafe+"\", 240, 270, 10)\n");
	rBuf.append("bitmap(\""+pngPathSafe+"\", width=240/72, height=270/72, pointsize=12, res=72)\n");
	//rBuf.append("hist("+title+", breaks=c(0:"+(maxValue+1)+"),col=\"gray\")\n"); 
	rBuf.append("hist("+title+", col=\"gray\")\n");
	rBuf.append("dev.off()\n");
	runR( rBuf.toString(), batchPath );
}

	*/

	
	public static class Param extends edu.cmu.lti.util.run.Param {
		private static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public String rPath;
		public String rCmdPath;
		public boolean includeOrigin;
			
		//rCmdPath = System.getProperty("javelin.home")+"/bin/generateRGraph.sh";
		//FFile.saveString("#!/bin/sh\n", rCmdPath);

		public Param(){//Class c) {
			super(RWrapper.class);
			parse();
		}

		public void parse() {
			rPath=getString("rPath", 	"/usr/local/bin/R");
			includeOrigin=getBoolean("includeOrigin", true);
		}
	}
	Param p = new Param();

	StringBuffer rBuf;
	//public String outputFolder= null;
	String fn= null;
	int width=640, height=480;
	int res=100;
	int pointsize=12;
	
	public void openImage(String fn){
		this.fn = fn;
		//String fp = outputFolder+"/"+fn;
		rBuf = new StringBuffer();
		rBuf.append(String.format(
			"bitmap(\"%s\", width=%d/%d, height=%d/%d, pointsize=%d, res=%d)\n\n"
			,fn,width,res, height,res, pointsize, res));
		
	}
	public void closeImage(){
		rBuf.append("dev.off()\n");
		fn = "x";
		FFile.saveString(fn+".r" , rBuf.toString());
		String r = p.rPath;
		if ( p.rPath.indexOf("afs")!=-1 ){
			r = "/bin/sh "+p.rPath;
		} else {
			r = "\""+p.rPath+"\"";
		}
		//String cmd ="sh "+ r+" CMD BATCH \""+fn+".r\"";
		String cmd =p.rPath+" CMD BATCH "+fn+".r";
		FSystem.cmd(cmd);
		return;
	}
	
/*
 * 				,vvx.getVDouble("min").min()
				,vvx.getVDouble("max").max()
				,vvy.getVDouble("min").min()
				,vvy.getVDouble("max").max());

 */
	protected void addPlot( String title	,String xLabel,String yLabel
			, double xmin, double xmax, double ymin, double ymax){
		xmin = Math.min(0,xmin);
		xmax = Math.max(0,xmax);
		ymin = Math.min(0,ymin);
		ymax = Math.max(0,ymax);
		
		rBuf.append(String.format(
			"plot(c(0),c(0),main =\"%s\", xlab=\"%s\",ylab=\"%s\""
				+",xlim =c(%f,%f), ylim = c(%f,%f), type= \"n\" )\n\n"
				, title,xLabel, yLabel,xmin, xmax,ymin, ymax));
	}
	protected void addPlot( String title	,String xLabel,String yLabel
			,VectorD vx,VectorD vy){
		if (p.includeOrigin){
			vx.add(0.0);
			vy.add(0.0);
		}
			
		rBuf.append(String.format(
				"plot(c(%s),c(%s) ,main =\"%s\", xlab=\"%s\",ylab=\"%s\", type=\"n\")\n\n"
				,vx.join(", ")	,vy.join(", ")	
				, title,xLabel, yLabel));
		
	}
	//, legend=c(\"a\",\"b\")
	protected void addLine(VectorD vx,VectorD vy,int iType ){
		if (vx.size() != vy.size()){
			System.err.println("x and y lengths differ");
			return;
		}
			
		rBuf.append(String.format(
			"lines(c(%s),c(%s) ,type=\"l\",lwd=2, col=%d, lty = %d)\n\n"
			,vx.join(", ")	,vy.join(", "), iType, iType
			));		//\"black\"
	}
	
	public void drawXYPlot( String title,String xLabel,String yLabel
			, VecVecD vvx	, VecVecD vvy){
		addPlot(title,xLabel,yLabel, vvx.sum(), vvy.sum());
		addGrid();
		for (int i=0; i<vvx.size(); ++i)
			addLine(vvx.get(i), vvy.get(i), i+1);
	}
	public void drawXYPlot( String title
			,String xLabel,String yLabel
			, VectorD vx	, VectorD vy){
		addPlot(title,xLabel,yLabel, vx, vy);
		addGrid();
		addLine(vx, vy,1);
	}
	/*legend =
	 * plot(c(0),c(0),xlim =c(-3,3), ylim = c(-3,3)
,xlab="theta (C=2,N=10)",ylab="R(theta, theta hat)")
lines(x,y,type="l",lwd=2, col="green", lty = 1)
lines(x,x,type="l",lwd=2, col="red", lty = 2)

## right-justifying a set of labels: thanks to Uwe Ligges
x <- 1:5; y1 <- 1/x; y2 <- 2/x
plot(rep(x, 2), c(y1, y2), type="n", xlab="x", ylab="y")
lines(x, y1); lines(x, y2, lty=2)
temp <- legend("topright", legend = c(" ", " "),
               text.width = strwidth("1,000,000"),
               lty = 1:2, xjust = 1, yjust = 1,
               title = "Line Types")
text(temp$rect$left + temp$rect$w, temp$text$y,
     c("1,000", "1,000,000"), pos=2)
	 */
	private String quotedJoin(VectorS vs){
		return "c(\""+vs.join("\", \"")+ "\")";
	}
	public void addLegend(VectorS vName){///double x, double y,
		rBuf.append(String.format(
			"legend(\"%s\",legend =%s, col= 1:%d,lwd=3, lty = 1:%d, xjust = 1, yjust = 1)\n\n"	
			,legendPosition, quotedJoin(vName), vName.size(),vName.size()));
	}
	public String legendPosition="bottomright";//"topleft";
	
	public void addGrid(){
		rBuf.append(String.format(
			"grid()\n\n"
				));
	}
	
	public static void main(String[] args) {
//		int[] inputData = {1,2,3,4,5,1,2,3,4,15,14,10,3,2,2,3,4,4,5,2,2,2,1,2,2,1,2,3,2};
//		Config c = new Config("JAVELIN/LIGHT","./");
//		Graph g = new Graph();
//		g.drawHistogram(inputData, Config.rsHistogramOutPath);
		
		RWrapper wrapper = new RWrapper();
		wrapper.openImage("aaa.png");		
		VecVecD vvx = new VecVecD();
		VecVecD vvy = new VecVecD();
		VectorS vs = new VectorS("A B");
		vvx.add(new VectorD(new double[]{1.0,3.0,4.0}));
		vvy.add(new VectorD(new double[]{1.0,1.0,3.0}));	
		vvx.add(new VectorD(new double[]{1.5,5.0}));
		vvy.add(new VectorD(new double[]{0.0,3.0}));	
		wrapper.drawXYPlot("title", "xLabel",  "yLabel"	, vvx, vvy);
		wrapper.addLegend(new VectorS("A B"));
		wrapper.closeImage();
		return;
	}
}
