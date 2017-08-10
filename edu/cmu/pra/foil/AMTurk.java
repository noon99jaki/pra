package edu.cmu.pra.foil;

import java.io.BufferedWriter;

import edu.cmu.lti.algorithm.Interfaces.IParseLine;
import edu.cmu.lti.algorithm.container.MapII;
import edu.cmu.lti.algorithm.container.MapSI;
import edu.cmu.lti.algorithm.container.MapSS;
import edu.cmu.lti.algorithm.container.VectorI;
import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.algorithm.sequence.SeqTransform;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.util.file.FTable;
import edu.cmu.lti.util.file.MapSW;
import edu.cmu.lti.util.file.MapXW;
import edu.cmu.lti.util.run.Param;
import edu.cmu.lti.util.system.FSystem;
import edu.cmu.lti.util.text.FString;


public class AMTurk {
	
	public static class ResultLine implements IParseLine{
		public String 	hitid	;
		public String 	hittypeid	;
		public String 	title	;
		public String 	description	;
		public String 	keywords	;
		public String 	reward	;
		public String 	creationtime	;
		public String 	assignments	;
		public String 	numavailable	;
		public String 	numpending	;
		public String 	numcomplete	;
		public String 	hitstatus	;
		public String 	reviewstatus	;
		public String 	annotation	;
		public String 	assignmentduration	;
		public String 	autoapprovaldelay	;
		public String 	hitlifetime	;
		public String 	viewhit	;
		public String 	assignmentid	;
		public String 	workerid	;
		public String 	assignmentstatus	;
		public String 	autoapprovaltime	;
		public String 	assignmentaccepttime	;
		public String 	assignmentsubmittime	;
		public String 	assignmentapprovaltime	;
		public String 	assignmentrejecttime	;
		public String 	deadline	;
		public String 	feedback	;
		public String 	reject	;
		public VectorS vAnswers=new VectorS();
		public int getNumTrue(){
			int i=0;
			for (String a: vAnswers)
				if (a.equals("True"))
					++i;
			return i;
		}
		public String toString(){
			return vAnswers.join(" ");
		}
		public boolean parseLine(String line){
			VectorS vs= FString.splitVS(line,"\t");
			int i=0;
			hitid	= FString.removeQuotes(vs.get(i));++i;
			hittypeid	= FString.removeQuotes(vs.get(i));++i;
			title	= FString.removeQuotes(vs.get(i));++i;
			description	= FString.removeQuotes(vs.get(i));++i;
			keywords	= FString.removeQuotes(vs.get(i));++i;
			reward	= FString.removeQuotes(vs.get(i));++i;
			creationtime	= FString.removeQuotes(vs.get(i));++i;
			assignments	= FString.removeQuotes(vs.get(i));++i;
			numavailable	= FString.removeQuotes(vs.get(i));++i;
			numpending	= FString.removeQuotes(vs.get(i));++i;
			numcomplete	= FString.removeQuotes(vs.get(i));++i;
			hitstatus	= FString.removeQuotes(vs.get(i));++i;
			reviewstatus	= FString.removeQuotes(vs.get(i));++i;
			annotation	= FString.removeQuotes(vs.get(i));++i;
			assignmentduration	= FString.removeQuotes(vs.get(i));++i;
			autoapprovaldelay	= FString.removeQuotes(vs.get(i));++i;
			hitlifetime	= FString.removeQuotes(vs.get(i));++i;
			viewhit	= FString.removeQuotes(vs.get(i));++i;
			assignmentid	= FString.removeQuotes(vs.get(i));++i;
			workerid	= FString.removeQuotes(vs.get(i));++i;
			assignmentstatus	= FString.removeQuotes(vs.get(i));++i;
			autoapprovaltime	= FString.removeQuotes(vs.get(i));++i;
			assignmentaccepttime	= FString.removeQuotes(vs.get(i));++i;
			assignmentsubmittime	= FString.removeQuotes(vs.get(i));++i;
			assignmentapprovaltime	= FString.removeQuotes(vs.get(i));++i;
			assignmentrejecttime	= FString.removeQuotes(vs.get(i));++i;
			deadline	= FString.removeQuotes(vs.get(i));++i;
			feedback	= FString.removeQuotes(vs.get(i));++i;
			reject	= FString.removeQuotes(vs.get(i));++i;

			for (;i<vs.size();++i)
				vAnswers.add(FString.removeQuotes(vs.get(i)));
			return true;
		}
		public static String sanitize(String name){
			int i=name.lastIndexOf(':');
			if (i>0)
				name=name.substring(i+1);
			name=name.replace(',', '_');
			return name;
		}
		public static Seq<ResultLine> reader(String fn){
			return reader(fn, false);
		}
		public static Seq<ResultLine> reader(String fn,boolean bSkipTitle){
			return new SeqTransform<ResultLine>(	ResultLine.class, fn, bSkipTitle);
		}
	}
	

	public static void generateInputFile(String fmTitle
			,	VectorS vItems, VectorS vLabels){//, String fn){
		
		String fn ="AMT."+AMTurk.fdHit+"/.AMT.input";
		StringBuffer sb=new StringBuffer();
		sb.append("id");
		for (int i=1;i<=nItemPerHit;++i)
			sb.append(fmTitle.replaceAll("%d",i+""));
		String title=sb.toString();
		
		{
			BufferedWriter bw= FFile.newWriter(fn);
			FFile.writeln(bw, title);
			int nHit=(int) Math.ceil((double)vItems.size()/nItemPerHit) ;
			for (int h=0;h<nHit; ++h){
				FFile.write(bw, h+"");
				for (int i=0;i<nItemPerHit;++i)
					FFile.write(bw, vItems.getCir( h*nItemPerHit+i ));
				FFile.write(bw, "\n");
			}
			FFile.close(bw);
		}
		
		if (vLabels.size()==vItems.size()){
			BufferedWriter bw= FFile.newWriter(fn+".gold");
			int nHit=(int) Math.ceil((double)vItems.size()/nItemPerHit) ;
			for (int h=0;h<nHit; ++h){
				//FFile.write(bw, h+"");
				FFile.write(bw,vLabels.join(
						" ",h*nItemPerHit, h*nItemPerHit+nItemPerHit));
				//for (int i=0;i<nItemPerHit;++i)
					//FFile.write(bw, "\t"+vLabels.getCir( h*nItemPerHit+i ));
				FFile.write(bw, "\n");
			}
			FFile.close(bw);
		}
		
		FFile.copyFile(fn, fdAMT+fdHit+"q.input");
		System.out.println("copy .input to " + fdAMT+fdHit+"q.input");

	}
	
	public static enum EAssignmentStatus{
		Submitted,Rejected;
	}
	public static void collectResults(){//String fnMissing){
		System.out.println("\n collectResults()");
		
		VectorS vMissing=FFile.loadLines("AMT."+fdHit+".missing");
		VectorS vHID=FTable.loadAColumn(fdAMT+fdHit+fnSucc,0,true);
		MapSI mHID=vHID.toMapValueId();
		
		
		MapXW.bSilent=true;

		
		VectorI vnTrue=new VectorI(vMissing.size());
		VectorI vnFalse=new VectorI(vMissing.size());
		
		String HTypeId=null;
		for (ResultLine l: ResultLine.reader(fdAMT+fdHit+fnResult,true)){
			if (l.assignmentstatus.equals(EAssignmentStatus.Rejected.name()))
				continue;
			
			HTypeId=l.hittypeid;
			Integer  iHit= mHID.get(l.hitid);
			if (iHit==null)
				FSystem.die("cannot find HID="+l.hitid);
			
			for (int i=0; i<nItemPerHit;++i){
				int idx=iHit*nItemPerHit+i;
				if (idx>=vMissing.size()) break;
				String answer=l.vAnswers.get(i);
				if (answer.equals("True"))
					vnTrue.plusOn(idx,1);
				else if (answer.equals("False"))
					vnFalse.plusOn(idx,1);
				else
					FSystem.die("unknown label="+answer);
			}
		}
		
		
		VectorS vsLabel= new VectorS();
		VectorS vsConflict= new VectorS();	
		VectorS vsIncomp= new VectorS();
		VectorS vsInc= new VectorS();	vsInc.add("hitid	hittypeid");
		
		MapII mAssCount= new MapII();
		
		String fdLabels="AMT."+fdHit+"label/";FFile.mkdirs(fdLabels);
		MapSW mBW= new MapSW(fdLabels);
		for (int i=0; i<vMissing.size();++i){
			String line=vMissing.get(i);
			
			int iItem= i%nItemPerHit;
			int nT=vnTrue.get(i);
			int nF=vnFalse.get(i);
			int L= (nT>nF)?1:0;
			String hid= vHID.get(i/nItemPerHit);
			
			vsLabel.add(line +"\t"+L);
			mAssCount.plusOn(nT+nF, 1);
			
			String pos=hid+"\t"+iItem+"\tnT="+nT+" nF="+nF	+"\t"+line;
			if (nT>0&& nF>0)
				vsConflict.add(pos);
			
			if (nT+nF<nAssignment){
				vsIncomp.add(pos);
				vsInc.add(hid+"\t"+HTypeId);
			}
			
			String vs[]=line.split("\t");
			mBW.writeln(vs[0], vs[1]+"\t"+vs[2]+"\t"+nT+"/"+nF);
			
		}
		mBW.closeAll();

		vsLabel.save(fnLabel);
		vsConflict.save(fnLabel+".conflict");
		vsIncomp.save(fnLabel+".incomp");
		vsInc.save(fdAMT+fdHit+ "incomp.succ");
		
		System.out.println("#label="+vsLabel.size());
		System.out.println("#conflict="+vsConflict.size());
		System.out.println("#incompleted="+vsIncomp.size());
		System.out.println("#Ass Countd=\n"
				+		mAssCount.join(" Asgnmt-->", "\n"));
	}
	
	public static MapSS mHitAnswers=new MapSS();
	
	public static void loadHitAnswers(String fdPred){
		VectorS vAnswer= FFile.loadLines(fdPred+".AMT.input.gold");
		VectorS vHitId= FTable.loadAColumn(fdAMT+fdHit+"q.success",0,true);
		if (vAnswer.size()!=vHitId.size())
			FSystem.die("unequal size:"+vAnswer.size() +" vs "+vHitId.size());
		mHitAnswers=new MapSS();
		for (int i=0; i<vHitId.size();++i)
			mHitAnswers.put(vHitId.get(i),vAnswer.get(i));
		return;
	}
	
	
	public static void inspectUsers(String fdGold ){
		if (fdGold!=null)
			loadHitAnswers(fdGold);
		
		MapSI mUR= new MapSI();
		MapSI mUT=new MapSI();
		MapSI mUG=new MapSI();
		
		System.out.println("\n inspectUsers()");

		FFile.mkdirs(fdInspect);

		MapXW.bSilent=true;
		MapSW mBW= new MapSW(fdInspect);
		//MapSBw mAssignment= new MapSBw(fdHit+"user.assign/",".assign");
		
		for (ResultLine l: ResultLine.reader(fdAMT+fdHit+fnResult,true)){
			String gold=mHitAnswers.get(l.hitid);
			mBW.writeln(l.workerid, l.assignmentid 
					+"\t"+l.vAnswers.join(" ")+"\t"+gold);
			//mAssignment.writeln(l.workerid, l.assignmentid);
			mUR.plusOn(l.workerid, AMTurk.nItemPerHit);
			mUT.plusOn(l.workerid, l.getNumTrue());
			mUG.plusOn(l.workerid, FString.count(gold,'1'));
			
		}
		
		//mAssignment.closeAll();
		System.out.println("#users="+mBW.size());
		mBW.closeAll();
		for (String user: mUR.KeyToVecSortByValue(true))
			System.out.println(user+"\t"
					+mUG.getD(user)+"\t"+mUT.getD(user)+"/"+mUR.get(user));
	}
	
	
	public static void rejectUser(String users, String reason){
		System.out.println("\n rejectUser()"+users+" for "+reason);
		
		String vU[]=users.split(" ");
		BufferedWriter bw=FFile.newWriter(
				fdAMT+fdHit+vU[0]+"+"+vU.length+".reject");
		
		FFile.writeln(bw,"assignmentIdToReject\tassignmentIdToRejectComment" );
		
		for (String user: vU)
			for (String as: FFile.enuACol(fdInspect+user,0))
				FFile.writeln(bw, as+"\t"+reason);
		FFile.close(bw);
	}	
	
	public static String fdAMT="/usr0/nlao/work/aws-mturk-clt-1.3.0/hits/";
	public static String fdHit="exp01/";//"exp01 foil www2011  pra
	public static String fdAMTOut="AMT."+fdHit;
	public static String fdInspect=fdAMTOut+"user.inspect/";
	
	public static String fnLabel=fdAMTOut+".AMT";
	
	public static int nItemPerHit=5;
	public static int nAssignment=3;
	//public static String fdInspectUsers="inspectUsers/";

	public static String fnSucc="q.success";
//	public static String fnMissing
	public static String fnResult="q.results";
	
	public static void main(String args[]) {
		Param.overwriteFrom("conf");
		//inspectUsers(null);//"label/");
		
		
		//rejectUser(//"A1QX2RJC8SJUOH"
		//rejectUser(		"A3O0P4DUUCLGUJ AXKU1S6WY0M8T AVUZ94D6CRR1F A1M5E2S4NMRWR1"
		//rejectUser(	"AHJRFFJD507UF A1SO49BHE6EAQD AXKU1S6WY0M8T A132G78OURF23K" //A14IZ4KZV79G0Q
			//	,"no answer has correlation with the questions");

		collectResults();

	}
}
		