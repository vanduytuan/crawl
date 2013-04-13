/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.Hashtable;
import java.util.StringTokenizer;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.util.Version;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;

/**
 *
 * @author WIN7
 */
public class Dictionary {
    Hashtable dict;
    int no_terms;
    
    public Dictionary()
    {
        dict = new Hashtable();
        no_terms = 0;
    }
    
    //update the dictionary
    public void updateDictionary(int docID, String term)
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
    
    //scan the review and build up the dictionary
    public void reviewScanner(int docID, String review)
    {
        String rawReview = toRawReview(review);
        StringTokenizer st = new StringTokenizer(rawReview);
        while (st.hasMoreTokens()) {
            String token = st.nextToken();
            if(StopWordList.isStopWord(token))
                continue;
            String stemWord = stem(st.nextToken());
            updateDictionary(docID, stemWord);       
        }
    }
    
    //stem a word
    public String stem(String word) {
        String result = word;
        try {
            EnglishAnalyzer en_an = new EnglishAnalyzer(Version.LUCENE_36);
            QueryParser parser = new QueryParser(Version.LUCENE_36, "", en_an);
            result = parser.parse(QueryParser.escape(word.toLowerCase())).toString();
        } catch (ParseException ex) {
            System.err.println(word);
            ex.printStackTrace();
        }
        if (result.length() > 0) {
            return result;
        } else {
            return word;
        }
    }
    
    //remove punctuation marks of the review
    public String toRawReview(String review)
    {
        String rawReview = review.replaceAll("\\p{Punct}+", " ");
        rawReview = rawReview.toLowerCase();
        return rawReview;
    }
    
    public static void main(String[] args)
    {
        String review = "MATLAB don't toolbox that can be used     for various tasks in text mining specifically i) indexing, ii) retrieval, iii) dimensionality reduction, iv) clustering, v) classification. Most of TMG is written in MATLAB and parts in Perl. It contains implementations of LSI, clustered LSI, NMF and other methods.";
        StringTokenizer st = new StringTokenizer(review);
        while (st.hasMoreTokens()) {
            System.out.println(st.nextToken());
        }
        System.out.println(Math.log10(99));
        //System.out.println(toRawReview(review));
    }
    
    
}
