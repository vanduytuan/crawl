/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Classifier;

import WordProcessing.Stemmer;
import java.util.Hashtable;
import java.util.StringTokenizer;
import main.Dictionary;
import main.Review;
import main.StopWordList;

/**
 *
 * @author WIN7
 */
public class NaiveBayesClassifier {

    Dictionary d = new Dictionary();

    public static void main(String[] args) {
        NaiveBayesClassifier nbc = new NaiveBayesClassifier();
        Dictionary d = nbc.d;
        int total_reviews = d.dictionaryBuilder("labelData.xml");
        Hashtable<String, Double>[] bayesTermWeight = d.getNaiveBayesTermWeight();
        Review[] testReviewList = d.getTestReviewList();
        double[] polarity = new double[testReviewList.length];
        double[] confidence = new double[testReviewList.length];
        int[] classCounter = d.getClassCounter();
        double[] classPriorProb = new double[3];
        for (int i = 0; i < 3; i++) {
            classPriorProb[i] = classCounter[i] / 100.0;
        }
        for (int i = 0; i < testReviewList.length; i++) {
            double[] classifierResult = nbc.classifyReview(testReviewList[i], classPriorProb, bayesTermWeight);
            polarity[i] =  classifierResult[0];
            confidence[i] = classifierResult[1];
            System.out.println(testReviewList[i].getDocID() + " " + classifierResult[0] + " " + classifierResult[1]);
        }
    }

    public double[] classifyReview(Review review, double[] classPriorProb, Hashtable<String, Double>[] bayesTermWeight) {
        double[] cmap = new double[3];
        for (int i = 0; i < 3; i++) {
            cmap[i] = Math.log(classPriorProb[i]);
            //System.out.println("DKM "+cmap[i]);
        }
        Stemmer stemmer = new Stemmer();
        String comment = review.getComment();
        String rawReview = d.toRawReview(comment);
        StringTokenizer st = new StringTokenizer(rawReview);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (StopWordList.isStopWord(token)) {
                continue;
            }
            String stemWord = stemmer.stem(token);
            updateCMap(cmap, stemWord, bayesTermWeight);
        }
        for (int i = 0; i < 3; i++) {
            //System.out.println("CLGT "+cmap[i]);
        }

        double sum = 0.0, max = -10000;
        int polarity = 0;
        for (int i = 0; i < 3; i++) {
            sum += cmap[i];
            if (cmap[i] > max) {
                max = cmap[i];
                polarity = i;
            }
        }
        //System.out.println("WTF " + max + " " + sum);
        double confidence = max / sum;

        double[] classifierResult = new double[2];
        classifierResult[0] = polarity;
        classifierResult[1] = confidence;
        return classifierResult;
    }

    public void updateCMap(double[] cmap, String stemWord, Hashtable<String, Double>[] bayesTermWeight) {
        for (int i = 0; i < 3; i++) {
            if (bayesTermWeight[i].containsKey(stemWord)) {
                cmap[i] += Math.log((double) bayesTermWeight[i].get(stemWord));
            }
        }
    }
}
