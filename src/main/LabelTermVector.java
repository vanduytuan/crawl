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
public class LabelTermVector {

    Hashtable[] label_tf;
    Hashtable[] label_tf_idf;
    double label_tf_idf_sum[];

    //constructor
    public LabelTermVector() {
        label_tf = new Hashtable[3];
        label_tf_idf = new Hashtable[3];
        for (int i = 0; i < 3; i++) {
            label_tf[i] = new Hashtable();
            label_tf_idf[i] = new Hashtable();
        }
        label_tf_idf_sum = new double[3];
        for (int i = 0; i < 3; i++) {
            label_tf_idf_sum[i] = 0.0;
        }
    }

    public double computeIDF(double df_value, int total_terms) {
        return Math.log10(total_terms / df_value);
    }

    public void buildLabelTermVector(Hashtable<String, DocumentTermVector> termListVector,
            Hashtable<Integer, Double> reviewClassification, int total_terms) {

        double[] termLabel = new double[3];
        double df_value, idf_value, tf_idf_value;
        Enumeration e = termListVector.keys();

        while (e.hasMoreElements()) {
            for (int i = 0; i < 3; i++) {
                termLabel[i] = 0;
            }

            String term = e.nextElement().toString();
            DocumentTermVector t = (DocumentTermVector) termListVector.get(term);
            t.NormalizeTF();
            Enumeration e1 = t.getTF().keys();
            while (e1.hasMoreElements()) {
                int docID = (int) e1.nextElement();
                int polarity = (int) (reviewClassification.get(docID) + 1);
                termLabel[polarity] += (double) (t.getTF().get(docID));
            }

            /*
            if ("had".equals(term)) {
                for (int i = 0; i < 3; i++) {
                    System.out.println("Nghia " + termLabel[i]);
                }
            }
            */
            
            for (int i = 0; i < 3; i++) {
                df_value = ((DocumentTermVector) termListVector.get(term)).getDF();
                idf_value = computeIDF(df_value, total_terms);
                tf_idf_value = termLabel[i] * idf_value;
                label_tf[i].put(term, termLabel[i]);
                label_tf_idf[i].put(term, tf_idf_value);
                label_tf_idf_sum[i] += tf_idf_value;
            }
        }
    }
}
