package edu.cmu.lti.util.run;

import java.io.BufferedReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import edu.cmu.lti.algorithm.container.VectorS;
import edu.cmu.lti.util.file.FFile;
import edu.cmu.lti.algorithm.sequence.Seq;
import edu.cmu.lti.util.system.FSystem;


//use the Param class
public class Scheduler {
	public static class Param extends edu.cmu.lti.util.run.Param {
		public static final long serialVersionUID = 2008042701L; // YYYYMMDD
		public VectorS vParam;//= new VectorS();	
		public String user=null;
		public String regxGrep=null;
		public boolean bNoRoot=true;
		public boolean bBigCPU=true;
		public Param(){//Class c) {
			super(Scheduler.class);
			parse();
		}

		public void parse() {
			user= getString("user", "nlao");
			bNoRoot= getBoolean("bNoRoot", true);
			bBigCPU= getBoolean("bBigCPU", true);
			regxGrep= getString("topFilter", "");
		}
	}
/*

sub kill_list{
	foreach my $l (@list)	{
#		print $l;
		chomp $l;chomp $l;
		if ($l =~ /\#/) {last;}
		if ( $l =~ /\> (.*?) (\d+)/)  {
			$machine = $1;
			print $1." ".$2."\n";
			my $cmd="ssh ".$machine." \'cd $dir ;perl ~/code_perl/gale/kill_usaf.pl\'";
			print $cmd."\n";
			system($cmd);
		}
	}
}
sub kill_process{
	my $procname=shift;
	my $foo;
	$foo=`ps -ef`;
	my @parr=split /\n/,$foo;
	foreach my $pstr (@parr){
		chomp $pstr;
		if($pstr =~ /^(\S+)\s+(\d+)\s+(\d+)\s+\S+\s+\S+\s+\S+\s+\S+\s+(.*?)$/){
		#lezhao   18888 19371  1 21:17 pts/9    00:00:00 wget --tries=1 -T 60 -q -O temp-boston-19371-14173.txt http://www.angelhealingcenter.com/DietaryFactors.htm
			my $user=$1;my $pid=$2;my $fid=$3;my $strcmd=$4;
			next if(!($user =~ /nlao/));
			next if(!($strcmd =~ /$procname/));
			`kill -9 $pid`;print STDERR "\n!!!Killing $pstr !\n";
		}
	}
}*/
	
	static String ssh= "ssh -i /home/nlao/.ssh/id_dsa ";
	static void tune(String machine,	String folder ){
		FFile.mkdirs(folder);
		//open $fo, ">$dir/job.$machine$id.sh" 
		//or die "Can't open file: $dir/job.$machine$id.sh\n $!\n";
		String cmd=String.format(
				"%s %s \"cd %s ; ../tune.pl\" >& tune.%d.cout &"
				,ssh,machine, folder, folder);
		FSystem.cmd(cmd);
	}
	
	static void collect(String folder ){
		FFile.mkdirs(folder);
		//open $fo, ">$dir/job.$machine$id.sh" 
		//or die "Can't open file: $dir/job.$machine$id.sh\n $!\n";
		String cmd=String.format(
				"cd %s ; ../CollectScores.pl\""	,folder);
		FSystem.cmd(cmd);
	}
	
	
	static void tune_list(){
		BufferedReader br = FFile.newReader("schedule");
		if (br==null){
			System.err.print("need schedule file");
			return ;
		}
		String line=null;
		while ( (line = FFile.readLine(br))!=null){
			if (line.startsWith("#")) break;
			
			System.out.println(line);
			
			String vs[]= line.split("\t");
			tune(vs[0],vs[1]);
		}
		FFile.close(br);
	}
	
	static void top_list(){
		//#  PID USER PR  NI  VIRT  RES  SHR S %CPU %MEM TIME+  COMMAND
		//  0			1		2		3    4     5    6  7  8    9   10        11 
		//#28418 hustlf    25   0  994m 988m 1644 R 99.0 12.2   5500:40 massspec_kmeans
		//#lezhao   18888 19371  1 21:17 pts/9    00:00:00 wget --tries=1 -T 60 -q -O temp-boston-19371-14173.txt http://www.angelhealingcenter.com/DietaryFactors.htm
		Pattern pa = Pattern.compile(	
				"^\\S+\\s+\\S+\\s+\\d+\\s+\\d+\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+(\\S+)\\s+\\S+\\s+(\\S+)");
		//     0			  1		    2	    	3       4       5       6       7       8         9       10        11 
		System.out.print(
				"  PID USER      PR  NI  VIRT  RES  SHR S %CPU %MEM    TIME+  COMMAND\n");
		for (String machine:  FFile.enuLines("machines",true))	{
			System.out.print("\n"+ machine+">>\n");
			//String cmd=ssh+machine +" \"top \\-b \\-n 1 | grep nlao \"";
			String cmd=ssh+machine +" top -b -n 1";
			//if (p.regxGrep.length()>0)
				//cmd += " | grep "+p.regxGrep;
			
			//my $cmd=`ssh $machine \'top \-b \-n 1\'`;
			String vs[]=FSystem.cmd(cmd).split("\n");
			
			//for(String line: vs){
			for (int i=7; i<vs.length;++i){
				String line= vs[i];

				if (p.regxGrep.length()>0)
					if (!line.matches(p.regxGrep))
						continue;
				
				String vc[]=line.trim().split("\\s+");
				//if (vc.length<8) continue;
				
				double CPU= Double.parseDouble(vc[8]);
				double MEM= Double.parseDouble(vc[9]);
				if (p.bBigCPU)
					if (CPU<10)
						continue;
				
				if (p.bNoRoot)
					if (line.indexOf("root")>=0)
						continue;
				/*Matcher m = pa.matcher( line );
				if(!m.find()) continue;
				String cpu=m.group(1);
				if(Double.parseDouble(cpu) < 2) continue;;
				*/				
				if (line.length()>1)
					System.out.println(line);
			}	
		}
	}

	
	static void who_list(){
		for (String machine:  FFile.enuLines("machines"))	{
			System.out.print("\n"+ machine+">>\n");
			String cmd=ssh+machine +" who";
			String vs[]=FSystem.cmd(cmd).split("\n");
			//String rlt=FSystem.cmd(cmd,true,true);
			//System.out.println(rlt);
			for(String line: vs)
				if (line.length()>1)
					System.out.println(line);
		}
	}
	
	static void monitor_list(){
		Pattern pa = Pattern.compile(	
				"^(\\S+)\\s+(\\d+)\\s+(\\d+)\\s+\\S+\\s+\\S+\\s+\\S+\\s+\\S+\\s+(.*?)$");
	//#lezhao   18888 19371  1 21:17 pts/9    00:00:00 wget --tries=1 -T 60 -q -O temp-boston-19371-14173.txt http://www.angelhealingcenter.com/DietaryFactors.htm
			
		for (String machine:  FFile.enuLines("machines"))	{
			System.out.print("\n"+ machine+">>\n");
			String cmd="ssh -i /home/nlao/.ssh/id_dsa "+machine +" \"ps -ef\"";
			String rlt=FSystem.cmd(cmd);
			
			for (String line:rlt.split("\n"))		{
				Matcher m = pa.matcher( line );
				
				if(!m.find()) continue;
				String user=m.group(1);
				String pid=m.group(2);
				String fid=m.group(3);
				String strcmd=m.group(4);
				if(! user.equals(p.user)) continue;
				System.out.print(line+"\n");
			}	
		}
	}

	static Param p=null;
	public static void main(String[] args) {
		Param.overwriteFrom("conf");
		if (args.length==0){
			System.err.print("need to specify task=tune|mon|top|kill");
			return;
		}
		//args[0]="top";
		if (args.length>=2)
			Param.overwrite(args[1]);
		
		p= new Param();
		
		String task= args[0];//"top"
		
		if (task.equals("tune")){tune_list();}
		else if (task.equals("mon")){monitor_list();}
		//else if (task.equals("kill")){kill_list();}
		else if (task.equals("top")){top_list();}
		else if (task.equals("who")){who_list();}
		else{	System.err.print("unknown task="+task);}

	}
}
