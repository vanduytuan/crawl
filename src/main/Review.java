/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author WIN7
 */
public class Review {

    int docID;
    double stars;
    String title;
    String comment;
    double polarity;

    public Review() {
        docID = 0;
        stars = 0.0;
        title = "";
        comment = "";
        polarity = 0;
    }

    public Review(int docID, double stars, String title, String comment) {
        this.docID = docID;
        this.stars = stars;
        this.title = title;
        this.comment = comment;
    }
    
    public Review(int docID, double stars, String title, String comment, double polarity) {
        this.docID = docID;
        this.stars = stars;
        this.title = title;
        this.comment = comment;
        this.polarity = polarity;
    }

    public int getDocID() {
        return docID;
    }

    public double getStars() {
        return stars;
    }

    public String getTitle() {
        return title;
    }

    public String getComment() {
        return comment;
    }

    public double getPolarity() {
        return polarity;
    }
}
