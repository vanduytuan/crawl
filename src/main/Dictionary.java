/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import WordProcessing.Stemmer;
import java.io.File;
import java.util.Hashtable;
import java.util.StringTokenizer;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
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

    Hashtable termsVector;
    int total_terms;

    public Dictionary() {
        termsVector = new Hashtable();
        total_terms = 0;
    }

    public Hashtable getTermsVector() {
        return termsVector;
    }

    public int getTotalTerms() {
        return total_terms;
    }

    //update the dictionary
    public void updateDictionary(int docID, String term) {
        if (termsVector.containsKey(term)) {
            TermVector t = (TermVector) termsVector.get(term);
            t.updateTermVector(docID, term);
        } else {
            TermVector t = new TermVector(docID);
            termsVector.put(term, t);
            total_terms++;
        }
    }

    //scan the review and build up the dictionary
    public void reviewScanner(Review review) {
        int docID = review.getDocID();
        Stemmer stemmer = new Stemmer();
        String comment = review.getComment();
        String rawReview = toRawReview(comment);
        StringTokenizer st = new StringTokenizer(rawReview);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if (StopWordList.isStopWord(token)) {
                continue;
            }
            String stemWord = stemmer.stem(st.nextToken());
            updateDictionary(docID, stemWord);
        }
    }

    //scan all the reviews
    public int reviewListScanner(String filename) {
        Review[] reviewList = getReviewList(filename);
        int total_reviews = reviewList.length;

        for (int i = 0; i < total_reviews; i++) {
            reviewScanner(reviewList[i]);
        }

        return total_reviews;
    }

    public Review[] getReviewList(String filename) {
        Review[] reviews = null;
        try {
            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document document = docBuilder.parse(new File(filename));

            // normalize text representation
            document.getDocumentElement().normalize();

            NodeList docNL = document.getElementsByTagName("doc");
            int totalDocs = docNL.getLength();
            reviews = new Review[totalDocs];

            for (int i = 0; i < totalDocs; i++) {
                Node doc = docNL.item(i);
                Element docE = (Element) doc;

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
                reviews[i] = new Review(docID, stars, comment, title);
                System.out.println(docID);
                System.out.println(stars);
                System.out.println(comment);
                System.out.println(title);
                System.out.println();
                //System.out.println(review.comment);
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
        return reviews;
        //System.exit (0);
    }//end of main

    //remove punctuation marks of the review
    public String toRawReview(String review) {
        String rawReview = review.replaceAll("\\p{Punct}+", " ");
        rawReview = rawReview.toLowerCase();
        return rawReview;
    }

    public static void main(String[] args) {
        String review = "MATLAB don't toolbox that can be used for various tasks in text mining specifically i) indexing, ii) retrieval, iii) dimensionality reduction, iv) clustering, v) classification. Most of TMG is written in MATLAB and parts in Perl. It contains implementations of LSI, clustered LSI, NMF and other methods.";
        StringTokenizer st = new StringTokenizer(review);
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
        System.out.println(Math.log10(99));
        //System.out.println(toRawReview(review));
    }
}
