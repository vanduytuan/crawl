/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WordProcessing;

import main.Dictionary;
import org.apache.lucene.analysis.en.EnglishAnalyzer;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.util.Version;

/**
 *
 * @author VanDuyTuan
 */

public class Stemmer {
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
}
