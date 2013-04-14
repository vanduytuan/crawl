/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import com.aliasi.tokenizer.IndoEuropeanTokenizerFactory;
import com.aliasi.tokenizer.PorterStemmerTokenizerFactory;
import com.aliasi.tokenizer.TokenizerFactory;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.Tokenizer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.util.Version;


/**
 *
 * @author VanDuyTuan
 */
public class Main {

    public static void main(String[] args) {

        String test = "My husband and I enjoy trying different fine dining establishments for our date nights. The steak here was phenomenal, as was the asparagus and the potatoes. Definitely one of the best steaks we have had. Of course the restaurant is a little pricey, but worth every penny. The atmosphere was great, we were able to get in without a reservation with only about 15 minutes of waiting at the bar. We loved the view - I would recommend making a reservation to ensure you can have a table by the window. The service was also extremely good, once again some of the best service we have had. We will definitely come here again!";
        TokenizerFactory tf = IndoEuropeanTokenizerFactory.INSTANCE;
        com.aliasi.tokenizer.Tokenizer to = tf.tokenizer(test.toCharArray(), 0, test.length());
        String[] items = to.tokenize();
        for(String item : items){
            System.out.println(item);
        }
    }

}