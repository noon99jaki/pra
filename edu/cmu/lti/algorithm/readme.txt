
2008.9.16	Remove ICopyable
hardly need copy if clone is implemented
	constructing  TreeDep from TreeSyntax
			TreeDep t=new TreeDep(this);
	
and we dont want to maintain two sets of things




how to clone final classes?
		try{
			for (V x: this)
				v.add((V)  ((ICloneable)x).clone());
		}
		catch(Exception e){
			v.addAll(this);
		}

Ni Lao, 2008.2.17
general algorithm/data structure in CS and math. not supposed to be independent tools.