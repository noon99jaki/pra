package edu.cmu.lti.algorithm.container;

public class MapISetI  extends MapIX<SetI>{
	private static final long serialVersionUID = 2008042701L; // YYYYMMDD
	public MapISetI(){
		super(SetI.class);
	}
	public void chainRemove(int k, int j){
		SetI m=get(k);
		m.remove(j);
		
		if (m.size()==0)
			remove(k);
		
	}
	public SetI get(int i){
		return (SetI) super.get(i);
	}
	public SetI joinValues(){
		SetI v= new SetI();
		for (SetI x: this.values())
			v.addAll(x);
		return v;
	}
}
