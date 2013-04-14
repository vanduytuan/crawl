/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package WordProcessing;

/**
 *
 * @author VanDuyTuan
 */
public class Term {
    private String lexicon;
    private String posTag;

    public void setPosTag(String posTag) {
        this.posTag = posTag;
    }

    public void setLexicon(String lexicon) {
        this.lexicon = lexicon;
    }

    public String getPosTag() {
        return posTag;
    }

    public String getLexicon() {
        return lexicon;
    }

    public Term(String lexicon, String posTag) {
        this.lexicon = lexicon;
        this.posTag = posTag;
    }

    public Term() {
    }

    
}
