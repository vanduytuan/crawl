/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Enumeration;
import java.util.Hashtable;

/**
 *
 * @author WIN7
 */
public class TermVector {
    public int df;
    Hashtable tf;
    Hashtable tf_idf;
    public TermVector()
    {
        df = 0;
        tf = new Hashtable();
        tf_idf = new Hashtable();
    }
    
    public TermVector(int docID)
    {
        df = 1;
        tf = new Hashtable();
        tf_idf = new Hashtable();
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
    
    public void updateTF_IDF(int no_terms)
    {
        Enumeration e = tf.keys();
  
        while(e.hasMoreElements())
        {
            int docID = (int)e.nextElement();
            int tf_value = (int)tf.get(e);
            double tf_idf_value = (1+Math.log10(tf_value))*Math.log10(no_terms/df);
            tf_idf.put(docID, tf_idf_value);
        }
    }
}
