package edu.cmu.lti.algorithm.math;

/** container for static methods, such as log base 2 */
public class Log2
{
    private static int[] table = {0};
    /**
     *  return floor(log base 2(n)).
     *  @param n the integer to evaluate floor(log base 2) at
     *  @return the logarithm, base 2, of that integer rounded down
     *  @exception IllegalArgumentException if n <= 0
     */
    public static int lg2(int n)
    {
        if (n <= 0)
            throw new IllegalArgumentException("Log of number <= 0");
        if (n >= table.length)
        {   // double-check works here because table length never decreases
            synchronized(Log2.class)
            {
                if (n >= table.length)
                {
                    int newLen = 1;
                    while (newLen <= n)
                    {
                        int next = newLen + newLen;
                        if (next < newLen)
                            throw new IllegalArgumentException("Overflow");
                        newLen = next;
                    }
                    // newLen is a power of two so the table at
                    // least doubles each time and the cost of rebuilding
                    // it repeatedly is small
                    int[] newTable = new int[newLen];
                    newTable[1] = 0;
                    for (int i = 2; i < newLen; i++)
                        newTable[i] = newTable[i >> 1] + 1;
                    table = newTable;
                }
            }
        }
        // table.length may have changed since the if statement, but it can
        // only have increased, so this is safe
        return table[n];
    }
    /**
     * Return the number of contiguous zeros at the lsb of the input.
     * @param n evaluate lowZeros here. Must be > 0
     * @return the number of continguous zeros terminating at the
     *  low order bit
     * @exception IllegalArgumentException if n <= 0
     */     
    public static int lowZeros(int n)
    {       
        if (n <= 0)
            throw new IllegalArgumentException("out of range: " + n);
        // abc100..0 ^ abc011..1 = 11..1
        // n zeros => n+1 1s
        int tail = n ^ (n - 1);        
        return Log2.lg2(tail + 1) - 1;        
    }
}
