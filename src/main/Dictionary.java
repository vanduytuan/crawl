/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import WordProcessing.Stemmer;
import java.io.File;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 *
 * @author WIN7
 */
public class Dictionary {

    Hashtable<String, DocumentTermVector> termListVector;
    Hashtable<String, DocumentTermVector> titleListVector;
    Hashtable<Integer, Double> reviewClassification;
    Hashtable<String, Double>[] bayesTermWeight;
    Review[] trainingReviewList;
    Review[] testReviewList;
    int[] classCounter;
    int total_terms;

    //constructor
    public Dictionary() {
        termListVector = new Hashtable();
        reviewClassification = new Hashtable();
        bayesTermWeight = new Hashtable[3];
        classCounter = new int[3];
        for (int i = 0; i < 3; i++) {
            classCounter[i] = 0;
            bayesTermWeight[i] = new Hashtable();
        }

        total_terms = 0;
    }

    //accessors
    public Hashtable<String, DocumentTermVector> gettermListVector() {
        return termListVector;
    }

    public Hashtable<Integer, Double> getReviewClassification() {
        return reviewClassification;
    }

    public Hashtable<String, Double>[] getNaiveBayesTermWeight() {
        return bayesTermWeight;
    }

    public int getTotalTerms() {
        return total_terms;
    }

    public int[] getClassCounter() {
        return classCounter;
    }

    public Review[] getTrainingReviewList() {
        return trainingReviewList;
    }

    public Review[] getTestReviewList() {
        return testReviewList;
    }

    //update the dictionary
    public void updateDictionary(int docID, String term) {
        if (termListVector.containsKey(term)) {
            DocumentTermVector t = (DocumentTermVector) termListVector.get(term);
            t.updateDocumentTermVector(docID);
        } else {
            DocumentTermVector t = new DocumentTermVector(docID);
            termListVector.put(term, t);
            total_terms++;
        }
    }

    //scan a single review
    public void reviewScanner(Review review) {
        int docID = review.getDocID();
        double polarity = review.getPolarity();
        classCounter[(int) (polarity + 1)]++;
        reviewClassification.put(docID, polarity);
        Stemmer stemmer = new Stemmer();
        String comment = review.getComment();
        String rawReview = toRawReview(comment);
        StringTokenizer st = new StringTokenizer(rawReview);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            //if (StopWordList.isStopWord(token)) {
                //continue;
            //}
            String stemWord = stemmer.stem(token);
            updateDictionary(docID, stemWord);
        }
    }

    //scan all the reviews and build up the dictionary
    public int dictionaryBuilder(String filename) {
        getTrainingReviewList(filename);
        int total_reviews = trainingReviewList.length;

        for (int i = 0; i < total_reviews; i++) {
            reviewScanner(trainingReviewList[i]);
        }

        //update the tf_idf of the term
        LabelTermVector labeltermvector = new LabelTermVector();
        labeltermvector.buildLabelTermVector(termListVector, reviewClassification, total_terms);

        Enumeration e = labeltermvector.label_tf_idf[0].keys();
        while (e.hasMoreElements()) {
            String term = e.nextElement().toString();
            for (int i = 0; i < 3; i++) {
                bayesTermWeight[i].put(term, computeBayesTermWeight(labeltermvector, term, i));
            }
        }

        return total_reviews;
    }

    public int dictionaryBuilder(Review[] trainingReviewList, Review[] testReviewList) {
        this.trainingReviewList = trainingReviewList;
        this.testReviewList = testReviewList;
        int total_reviews = trainingReviewList.length;

        for (int i = 0; i < total_reviews; i++) {
            reviewScanner(trainingReviewList[i]);
        }

        //update the tf_idf of the term
        LabelTermVector labeltermvector = new LabelTermVector();
        labeltermvector.buildLabelTermVector(termListVector, reviewClassification, total_terms);

        Enumeration e = labeltermvector.label_tf_idf[0].keys();
        while (e.hasMoreElements()) {
            String term = e.nextElement().toString();
            for (int i = 0; i < 3; i++) {
                bayesTermWeight[i].put(term, computeBayesTermWeight(labeltermvector, term, i));
            }
        }

        return total_reviews;
    }

    //compute Bayes term weight
    public double computeBayesTermWeight(LabelTermVector labeltermvector, String term, int i) {
        double tf_idf_value = (double) labeltermvector.label_tf_idf[i].get(term);
        double weight = (tf_idf_value + 1) / (labeltermvector.label_tf_idf_sum[i] + total_terms);
        //double weight = tf_idf_value;
        return weight;
    }

    public void getTrainingReviewList(String filename) {
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(new File(filename));

            // normalize text representation
            document.getDocumentElement().normalize();

            NodeList docNL = document.getElementsByTagName("doc");
            int totalDocs = docNL.getLength();
            trainingReviewList = new Review[120];
            testReviewList = new Review[1191];
            int trainingCount = 0, testCount = 0;

            for (int i = 0; i < totalDocs; i++) {

                Node doc = docNL.item(i);
                Element docE = (Element) doc;

                NodeList polarityNL = docE.getElementsByTagName("polarity");
                Element polarityE = (Element) polarityNL.item(0);

                NodeList starNL = docE.getElementsByTagName("stars");
                Element starE = (Element) starNL.item(0);

                NodeList reviewNL = docE.getElementsByTagName("review");
                Element reviewE = (Element) reviewNL.item(0);


                NodeList titleNL = docE.getElementsByTagName("title");
                Element titleE = (Element) titleNL.item(0);

                int docID = Integer.parseInt(docE.getAttribute("id"));
                double stars = Double.parseDouble(starE.getFirstChild().getNodeValue());
                String comment = reviewE.getFirstChild().getNodeValue();
                String title = titleE.getFirstChild().getNodeValue();
                if ("NULL".equals(polarityE.getFirstChild().getNodeValue().toString())) {
                    testReviewList[testCount] = new Review(docID, stars, comment, title);
                    testCount++;
                } else {
                    double polarity = Double.parseDouble(polarityE.getFirstChild().getNodeValue());
                    trainingReviewList[trainingCount] = new Review(docID, stars, comment, title, polarity);
                    trainingCount++;
                }
            }

        } catch (SAXParseException err) {
            System.out.println("** Parsing error" + ", line "
                    + err.getLineNumber() + ", uri " + err.getSystemId());
            System.out.println(" " + err.getMessage());

        } catch (SAXException e) {
            Exception x = e.getException();
            ((x == null) ? e : x).printStackTrace();

        } catch (Throwable t) {
            t.printStackTrace();
        }
        //System.exit (0);
    }//end of main

    //remove punctuation marks of the review
    public String toRawReview(String review) {
        String rawReview = review.replaceAll("\\p{Punct}+", " ");
        rawReview = rawReview.toLowerCase();
        return rawReview;
    }

    public static void main(String[] args) {
        Dictionary d = new Dictionary();
        int total_reviews = d.dictionaryBuilder("labelData.xml");
        Hashtable[] test = d.bayesTermWeight;
        Enumeration e = test[2].keys();
        double sum = 0.0;
        //int count = 0;
        while (e.hasMoreElements()) {
            String term = e.nextElement().toString();
            sum += (double) test[2].get(term);
            //count++;
            System.out.println(term + " " + test[2].get(term));
        }
        System.out.println("DKM " + sum);
        System.out.println(d.classCounter[0] + " " + d.classCounter[1] + " " + d.classCounter[2]);
    }
}
