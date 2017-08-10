Ni Lao	6.16	weight the translators?
can close adb1 ssh window?
weighthed Tor: 
1.0 Keizo Obuchi 是谁？ [WebMTFreeTranslation]  
1.0 Keizo Obuchi是谁? [WebMTAmikai]     
1.0 谁是小渊惠三？ [WebMTGoogle]  
1.0 谁是小渕恵三？ [WebMTWorldLingo]
	
Ni Lao	4.8	<<Serverized>>
Hi Ni,

Here are steps for running servers.

# 1. Get the code from SVN
$ svn up

# 2. Compile and deploy the code
$ mvn compile antrun:run tomcat:undeploy tomcat:deploy -Pdev

# 3. Kill the rmiregistry process
$ pgrep -f 'rmiregistry' | xargs kill -KILL

# 4. Start a new rmiregistry process
$ rmiregistry 2001 &

# 5. Start servers by registering server classes to rmiregistry
$ ./target/bin/server-control &


Currently, server host and port are hard-coded to
adb1.lti.cs.cmu.edu and 2001. In the future, let's
make it configurable.

-Hideki

Ni Lao	4.1
#shadowed variable are evil
	cannot call super.p outside the class

Ni Lao	3.31
#compose all .conf in mt to a single .conf?
no need to read .conf at all! 
no need for	public SetS langCap;


#a job should contains multiple text for translation?
	no
Ni Lao	3.25
why used Locale instead of String to represent language?

Ni Lao, 2008.3.18
http://pondicherry:8000/projects/javelin/wiki/TranslationModuleGuide
this interface is cumbersome
			Source source=new Source(qSent,srcLocale,"SENTENCE");
			Translation trans=tm.translate(source,trgLocale);
the initialization load resource for all languages

 
                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                            