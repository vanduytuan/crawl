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
public class TermVector {
    public int df;
    Hashtable tf;
    public TermVector()
    {
        df = 0;
        tf = new Hashtable();
    }
    
    public TermVector(int docID)
    {
        df = 1;
        tf = new Hashtable();
        updateTF(docID);
    }
    
    public void updateTermVector(int docID, String term)
    {
        if(tf.containsKey(docID))
        {
            updateTF(docID);
        }
        else
        {
            tf.put(docID,1);
            df++;
        }
    }
    
    public void updateTF(int docID)
    {
        int tf_value = (int) tf.get(docID);
        tf_value++;
        tf.put(docID, tf_value);
    }
}
