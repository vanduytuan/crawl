/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WordProcessing;

import main.Dictionary;

/**
 *
 * @author VanDuyTuan
 */
public class Stemmer {
    private static final Dictionary parser = new Dictionary();
    public String stem(String word){
        
        return parser.stem(word);
    }
    public static void main(String[] args){
        Stemmer stemmer = new Stemmer();
        String word, stemmedWord;
        word = "computer";
        stemmedWord = stemmer.stem(word);
        System.out.println(stemmedWord);
        word = "computing";
        stemmedWord = stemmer.stem(word);
        System.out.println(stemmedWord);
        
    }
}
