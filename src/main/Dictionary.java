/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Hashtable;

/**
 *
 * @author WIN7
 */
public class Dictionary {
    Hashtable dict = new Hashtable();
    public int no_terms = 0;
    public void insert(int docID, String term)
    {
        if(dict.containsKey(term))
        {
            TermVector t = (TermVector) dict.get(term);
            t.updateTermVector(docID, term);
        }
        else
        {
            TermVector t = new TermVector(docID);
            dict.put(term, t);
            no_terms++;
        }
    }
}
