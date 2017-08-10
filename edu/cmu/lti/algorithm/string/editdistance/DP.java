package edu.cmu.lti.algorithm.string.editdistance;

public class DP {

    /**
     * The complexity is O(nm) where n=a.length() and m=b.length().
     */
    public static int editDistance( String[] a, String[] b ) {
        return new DP(a,b).calc();
    }

    /** cost vector. */
    private int[] cost;
    /** back buffer. */
    private int[] back;

    /** Two strings to be compared. */
    private final String[] a,b;

    private DP( String[] a, String[] b ) {
        this.a=a;
        this.b=b;
        cost = new int[a.length+1];
        back = new int[a.length+1]; // back buffer

        for( int i=0; i<=a.length; i++ )
            cost[i] = i;
    }
    
    /**
     * Swaps two buffers.
     */
    private void flip() {
        int[] t = cost;
        cost = back;
        back = t;
    }

    private int min(int a,int b,int c) {
        return Math.min(a,Math.min(b,c));
    }

    private int calc() {
        for( int j=0; j<b.length; j++ ) {
            flip();
            cost[0] = j+1;
            for( int i=0; i<a.length; i++ ) {
                int match = ( a[i].equals(b[j]) )?0:1;
                cost[i+1] = min( back[i]+match, cost[i]+1, back[i+1]+1 );
            }
        }
        return cost[a.length];
    }
    
    public static void main(String[] args) {
    	String[] s1 = {"A","B","C","#","D","#","#"};
    	String[] s2 = {"#","A","#","B","C","D","E"};
    	
    	System.out.println( DP.editDistance(s1, s2) );
    	System.out.println( DP.editDistance(s2, s1) );
	}
}


