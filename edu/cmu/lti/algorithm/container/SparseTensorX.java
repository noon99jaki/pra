package edu.cmu.lti.algorithm.container;

// * data structure to store high dimensional array 
// * @author nlao
public class SparseTensorX<V> {
	public MapXX<Long, V> map_;
	// cardinalities to each dimention
	public VectorI cardinalities_ = new VectorI();

	public SparseTensorX(Class c) {
		map_ = new MapXX<Long, V>(Long.class, c);
	}

	public V get(VectorI v) {
		return get(vec2id(v, cardinalities_));
	}

	public V get(long id) {
		return map_.get(id);
	}

	public SparseTensorX<V> set(VectorI vec, V x) {
		map_.put(vec2id(vec, cardinalities_), x);
		return this;
	}

	public SparseTensorX<V> set(long id, V x) {
		map_.put(id, x);
		return this;
	}

	public static long vec2id(VectorI vec, VectorI cardinalities) {
		long id = 0;
		for (int i = vec.size() - 1; i >= 0; --i) {
			id = id * cardinalities.get(i);
			id = id + vec.get(i);
		}
		return id;
	}

	public static VectorI id2vec(long id, VectorI vCard) {
		int p = vCard.size();
		VectorI v = VectorI.zeros(p);
		for (int i = 0; i < p; ++i) {
			v.set(i, (int) (id % vCard.get(i)));
			id = (id - v.get(i)) / vCard.get(i);
		}
		return v;
	}
}
