/**
 * we may need this concept sometime
 */
package edu.cmu.lti.nlp.parsing.tree;

/**
 * @author nlao
 *
 */
public class Span {

    /**
     * begin character offset of this term
     */
    protected int begin;

    /**
     * end character offset of this term (inclusive)
     */
    protected int end;
    /**
     * Orders two Term objects based on their begin and end offsets. Terms with
     * smaller begin offsets come before those with larger begin offsets,
     * irrespective of term length. Terms with the same begin offset are ordered
     * based on term length; longer terms are ordered before (less than) shorter
     * terms.
     * @param t the Term object to compare with this one.
     * @return a negative integer, zero, or positive integer if this term is
     * less than, equal, or greater than the given term.
     */
    public int compareTo(Token t)
    {
        return 0;//text.compareTo(t.text);
    }
}
