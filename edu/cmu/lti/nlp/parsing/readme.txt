where to put evalb?

2008.7.10	should token has MapSS only?

2008.6.8	truncation
all the move up should be done in the function truncation()
not in some specific type of triming 


		\-(6)6~31, p=5, pos=NP posx=NP text=长 亚曼尼 （ S h e i k h A h m e d Z a k i Y a m a n i ）    
		|	\-(7)9~30, p=6, pos=NP posx=NP text=S h e i k h A h m e d Z a k i Y a m a n i    
		\-(8)31~32, p=5, pos=CC posx=CC text=并    
		\-(9)32~33, p=5, pos=VV posx=VV text=没有    
		\-(10)33~38, p=5, pos=PP posx=PP text=因    
		|	\-(11)34~38, p=10, pos=IP posx=IP text=涨    
		|		\-(12)34~36, p=11, pos=NP posx=NP text=油 价    
		|		\-(13)37~38, p=11, pos=NP posx=NP text=翻天    
		\-(14)38~39, p=5, pos=MSP posx=MSP text=而    
		\-(15)39~40, p=5, pos=VV posx=VV text=喝    

[1, 2, 4, 4, 3, 0, 6, 6, 6, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 7, 6, 8, 9, 10, 12, 12, 11, 13, 14, 15, 5]
[1, 2, 4, 3, 0,   6, 7, 6   , 8, 9, 10, 12, 11, 13, 14, 15, 5]


从 另一个 角度 来看 ， 油 盟 去年 没有 因 石油 短缺 危机 初 现 而 
			|		|	/-(20)25~27, pos=NP posx=NP text=石油 暴跌    
			|		|		\-(21)24~28, pos=PP posx=PP    
			|	/-(22)22~29, pos=IP posx=IP    


2008.4.25
simply use verb.c


member v, or inherit?
	TreeSRL(TreeParse t)

factorize (adding more class) is almost always a good thing
	seperate parse tree and SRL related stuff?

Type mismatch: cannot convert from MyVector<Integer> to VectorI
		public MyVector<Integer> vc = new MyVector<Integer>(); // children
		//public VectorI vc = new VectorI(); // children

many sentence has no arg

directly read from 
	ctb5.1/bracketed/chtb_001.fid 0 6 gold 同步.01 ----- 6:0-ARG1 6:0-rel
*(22 pos=NONE, text=*T*-1))
semantic/syntactic head

*toFeaturedGraph or fromParseTree?
	graph is more general than tree
	
*attach SRL tags to constituents

*ParseTree.Node is a map?
	no efficiency,	but very general
*FrameNet has  or derive ParseTree?
	has, for easier operations on tree
*headfinder in Tree or as a seperate class?
	#you can always split class later
	too heavy, split to a processor class
#featured tree is still ParseTree?
	featured graph instead