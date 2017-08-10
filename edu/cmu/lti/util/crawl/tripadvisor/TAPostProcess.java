package edu.cmu.lti.util.crawl.tripadvisor;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.MapIS;
import edu.cmu.lti.algorithm.container.MapISetI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.pra.CTag;

public class TAPostProcess {
	//MapIS mLocation= new MapIS();
	//MapIS mHotel= new MapIS();
	
	MapISetI mmLocationHotel= new MapISetI();
	MapISetI mmHotelReview= new MapISetI();
	//static MapII mHotelCount=new MapII();// for each location
	//static MapII mReviewCount=new MapII();// for each hotel

	//SetI mReview= new SetI();
	MapII mReview= new MapII();
	MapIS misReview = new MapIS();
	private void processAOutput(String fn){
		System.out.println("\nprocessAOutput="+fn);
		
		BufferedReader br= FFile.newReader(fn);
		String line=null;		
		while ((line=FFile.readLine(br))!=null){
			String vs[]= line.split("\t");
			try{
				int g= Integer.parseInt(vs[0]);
				int d= Integer.parseInt(vs[1]);
				int r= Integer.parseInt(vs[2]);
				if (mReview.containsKey(r)){
					//System.out.println(misReview.get(r));
					//System.out.println(line);
					continue;
				}
				mmLocationHotel.getC(g).add(d);
				mmHotelReview.getC(d).add(r);
				mReview.plusOn(r,1);
				//misReview.put(r,line);
				FFile.writeln(bwReviews, line);
			}
			catch(Exception e){
				System.err.println("error parsing line="+line);
				continue;
			}
		}
		FFile.close(br);
		System.out.println("\nReview="+mReview.size() 
				+"  "+FSystem.printMemoryUsageRate());

		return;	
	}
	protected BufferedWriter bwReviews=null;
	public void postProcess(){
		VectorS vF= FFile.getFileNames(".","output.\\d+.ex");
		// "output\\.\\d+")){r46011333
		bwReviews=FFile.newWriter("reviews");
		for (String fn:vF)
			processAOutput(fn);
		FFile.close(bwReviews);
		mmLocationHotel.getMInt(CTag.size).save("mLocationHotel");
		mmHotelReview.getMInt(CTag.size).save("mHotelReview");
		mReview.save("mReviewSize");
	}
	
	public static void main(String args[]) {
		TAPostProcess o= new TAPostProcess();
		o.postProcess();
		
		//postProcess();
	}
}

