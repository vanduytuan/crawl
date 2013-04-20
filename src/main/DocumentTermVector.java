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
public class DocumentTermVector {

    int df;
    Hashtable<Integer, Double> tf;

    //constructor
    public DocumentTermVector() {
        df = 0;
        tf = new Hashtable();
    }

    public DocumentTermVector(int docID) {
        df = 1;
        tf = new Hashtable();
        tf.put(docID, 0.0);
    }

    public int getDF() {
        return df;
    }

    public Hashtable getTF() {
        return tf;
    }

    public void updateDocumentTermVector(int docID) {
        if (tf.containsKey(docID)) {
            updateTF(docID);
        } else {
            tf.put(docID, 1.0);
            df++;
        }
    }

    public void updateTF(int docID) {
        double tf_value = (double) tf.get(docID);
        tf_value++;
        tf.put(docID, tf_value);
    }

    //normalize tf value
    public void NormalizeTF() {
        Enumeration e = tf.keys();
        double sum = 0;
        int count = 0;
        while (e.hasMoreElements()) {
            int docID = (int) e.nextElement();
            sum += Math.pow((double) tf.get(docID), 2);
            count++;
        }
        sum = sum / count;
        sum = Math.sqrt(sum);

        if (sum != 0) {
            e = tf.keys();
            while (e.hasMoreElements()) {
                int docID = (int) e.nextElement();
                double tf_value = (double) tf.get(docID);
                tf_value = tf_value / sum;
                tf.put(docID, tf_value);
            }
        }
    }
    /*
     * public void updateTF_IDF(int no_terms) { Enumeration e = tf.keys();
     *
     * while(e.hasMoreElements()) { int docID = (int)e.nextElement(); int
     * tf_value = (int)tf.get(e); double tf_idf_value =
     * (1+Math.log10(tf_value))*Math.log10(no_terms/df); tf_idf.put(docID,
     * tf_idf_value); } }
     */
}
