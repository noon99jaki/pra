
Ni Lao, 2011.3.8
this package has many operations that are not memory efficient
need a long time to reimplement them

Ni Lao, 2008.10.3	the load function in FFile?
	VectorS vs= FFile.loadVS(fn);
	VectorS vs= VectorS.load(fn);
	FFile should not know a very specific class like VectorVectorS
	
Ni Lao, 2008.7.3	there is clash for MapSD
	public Double getDouble(String name){
		if (name.equals("sum")) return this.sum();
		return null;
	}
is this a good solution? 
	public Double getDouble(String name){
		Double d = super.getDouble(name);
		if (d!=null) return d;
		return get(name);
	}
should TVector.plusOn work for y=null?

Ni Lao, 2008.5.7
move from TMapMapXXD to 

Ni Lao, 2008.4.17
GetInstance()

Ni Lao, 2008.3.17
the bad thing about enumerate is that 
you cannot define its interface in the containers
good news is that we can treat them as Strings 

getXXX is supposed to get a single element
subXXX is supposed to get a set of elements


generally speaking, toXXX() is better than fromXXX()
because you can concatenate the operations

but fromXXX() is also good
because java does not allow converting super class to subclass
even if their is no new member variable at all
then we need to duplicate the data when doing down-casting


java template is too weak, how can we share code like this?
	public Double sum() {


MyVector without templeate, use Vector<Object>?
	but you will need to delegate all member functions of Vector?
	not if you derive Vector<Object>
but get() of Vector<Object> cannot return Tree

configuration
pattern matching
	same as Perl
	
Ni Lao, 2008.2.17

