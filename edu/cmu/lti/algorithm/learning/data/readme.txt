
the goal is to share code as much as possible
//but how general can you go?
//lets start from specific ones, then generalize on the way



DataSet = TVector<IInstance>
or
TDataSet<K> = TVector<K>?
	+ you can do:  for (InstanceBinaryS ins : ds)
	- but you know nothing about IInstance inside TDataSet code
		which is not acceptable

*IInstance or Instance?
Instance can give you default implementation


