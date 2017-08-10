package edu.cmu.lti.util.text;


public class PrefixTree {
	public static class Node{
		public char c;
		public int count=0;
		public Node[] vChildren=new Node[256];
		public Node(char c){
			this.c=c;
		}
		public void toString(StringBuffer sb){//int nIndent){
			if (c!= '\0')
				sb.append(c);
			if (count!= 0)
				sb.append("#"+count);
			
			sb.append("(");
			for (Node n: vChildren)
				if (n!=null)
					n.toString(sb);
			sb.append(")");
		}
	}
	
	public Node root=new Node('\0');
	public int addString(String s){
		Node p=root;
		for (int i=0; i<s.length(); ++i){
			char c= s.charAt(i);
			if (p.vChildren[c]==null){
				p.vChildren[c]= new Node(c);
			}
			p=p.vChildren[c];
		}
		++p.count;
		return p.count;
	}
	public int checkString(String s){
		Node p=root;
		for (int i=0; i<s.length(); ++i){
			char c= s.charAt(i);
			p=p.vChildren[c];
			if (p==null)
				return 0;
		}
		return p.count;
	}
	public String toString(){
		StringBuffer sb = new StringBuffer();
		root.toString(sb);
		return sb.toString();
	}
	public static void main(String args[]) {
		PrefixTree tree = new PrefixTree();
		tree.addString("abcd");
		tree.addString("abcd");
		tree.addString("abcde");
		System.out.println(tree);
		System.out.println(tree.checkString("abcd"));
		System.out.println(tree.checkString("abcdef"));
		return;
	}
}
