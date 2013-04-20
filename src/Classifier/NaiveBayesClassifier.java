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

/**
 *
 * @author WIN7
 */
public class NaiveBayesClassifier {

    Dictionary d = new Dictionary();

    public static void main(String[] args) {
        NaiveBayesClassifier nbc = new NaiveBayesClassifier();
        Dictionary d = nbc.d;
        d.dictionaryBuilder("labelData.xml");

        //10-fold cross validation
        Dictionary[] dict = nbc.tenFoldValidation(d.getTrainingReviewList());
        //d = nbc.tenFoldValidation(d.getTrainingReviewList());
        int count = 0;
        for (int iteration = 0; iteration < 10; iteration++) {
            d = dict[iteration];
            Hashtable<String, Double>[] bayesTermWeight = d.getNaiveBayesTermWeight();
            Review[] testReviewList = d.getTestReviewList();
            double[] polarity = new double[testReviewList.length];
            double[] confidence = new double[testReviewList.length];
            int[] classCounter = d.getClassCounter();
            double[] classPriorProb = new double[3];
            for (int i = 0; i < 3; i++) {
                System.out.println("DKM " + classCounter[i]);
                classPriorProb[i] = classCounter[i] / 100.0;
            }
            for (int i = 0; i < testReviewList.length; i++) {
                double[] classifierResult = nbc.classifyReview(testReviewList[i], classPriorProb, bayesTermWeight);
                polarity[i] = classifierResult[0];
                confidence[i] = classifierResult[1];
                double true_value = testReviewList[i].getPolarity() + 1.0;
                System.out.println(testReviewList[i].getDocID() + " " + classifierResult[0] + " " + classifierResult[1] + " " + true_value);
                if(classifierResult[0] == true_value)
                    count++;
            }
            System.out.println("---------------");
        }
        System.out.println(count);
    }

    public double[] classifyReview(Review review, double[] classPriorProb, Hashtable<String, Double>[] bayesTermWeight) {
        double[] cmap = new double[3];
        for (int i = 0; i < 3; i++) {
            cmap[i] = classPriorProb[i];
            //System.out.println("DKM "+cmap[i]);
        }
        Stemmer stemmer = new Stemmer();
        String comment = review.getComment();
        String rawReview = d.toRawReview(comment);
        StringTokenizer st = new StringTokenizer(rawReview);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            //if (StopWordList.isStopWord(token)) {
            //continue;
            //}
            String stemWord = stemmer.stem(token);
            updateCMap(cmap, stemWord, bayesTermWeight);
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
                cmap[i] *= (double) bayesTermWeight[i].get(stemWord);
            }
        }
    }

    public Dictionary[] tenFoldValidation(Review[] trainingReviewList) {
        Dictionary[] dict = new Dictionary[10];
        Review[] newTrainingReviewList, newTestReviewList;
        newTrainingReviewList = new Review[108];
        newTestReviewList = new Review[12];

        for (int i = 0; i < 10; i++) {

            dict[i] = new Dictionary();

            for (int j = i; j < 108 + i; j++) {
                newTrainingReviewList[j - i] = trainingReviewList[j];
            }

            for (int j = 0; j < i; j++) {
                newTestReviewList[j] = trainingReviewList[j];
            }

            for (int j = 108 + i; j < 120; j++) {
                newTestReviewList[j - 108] = trainingReviewList[j];
            }

            dict[i].dictionaryBuilder(newTrainingReviewList, newTestReviewList);
            //System.out.println("WTF " + dict[i].getTrainingReviewList().length);
        }
        return dict;
    }
}
