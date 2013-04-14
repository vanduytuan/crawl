/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WordProcessing;

import edu.stanford.nlp.parser.lexparser.LexicalizedParser;
import edu.stanford.nlp.trees.Tree;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author VanDuyTuan
 */
public class POSTagger {
    private static final LexicalizedParser parser = LexicalizedParser.loadModel();
    
    public ArrayList<Term> posTag(String sentence){
        Tree tree = parser.parse(sentence);
        List<Tree> trees = tree.getLeaves();
        Term term;
        ArrayList<Term> results = new ArrayList<Term>();
        for(Tree t : trees){
            term = new Term();
            term.setLexicon(t.value());
            term.setPosTag(t.parent(tree).value());
            results.add(term);
        }
        return results;
    }
    public static void main(String[] args){
        POSTagger tagger = new POSTagger();
        ArrayList<Term> list = tagger.posTag("Today is Sunday");
        for(Term t : list){
            System.out.println(t.getLexicon() + " : " + t.getPosTag());
        }
    }
}
