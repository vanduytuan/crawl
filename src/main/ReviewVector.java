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
public class ReviewVector {

    Hashtable[] reviewVector;

    public void buildReviewVector(String filename) {
        Dictionary d = new Dictionary();
        int total_reviews = d.reviewListScanner(filename);

        reviewVector = new Hashtable[total_reviews + 1];
        Hashtable termsVector = d.getTermsVector();

        Enumeration e = termsVector.keys();

        while (e.hasMoreElements()) {
            String term = e.nextElement().toString();
            TermVector t = (TermVector) termsVector.get(e);

            Hashtable tf_idf = t.getTF_IDF();

            for (int i = 1; i <= total_reviews; i++) {
                String key = i + "";
                if (tf_idf.containsKey(key)) {
                    reviewVector[i].put(term, tf_idf.get(key));
                } else {
                    reviewVector[i].put(term, 0.0);
                }
            }
        }

    }
}
