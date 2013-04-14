/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.File;
import java.util.Hashtable;
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
public class Review {

    public int docID;
    public double stars;
    public String title;
    public String comment;

    public Review() {
        docID = 0;
        stars = 0.0;
        title = "";
        comment = "";
    }

    public Review(int docID, double stars, String title, String comment) {
        this.docID = docID;
        this.stars = stars;
        this.title = title;
        this.comment = comment;
    }
    
    public int getDocID()
    {
        return docID;
    }
    
    public double getStars()
    {
        return stars;
    }
    
    public String getTitle()
    {
        return title;
    }
    
    public String getComment()
    {
        return comment;
    }
    
    public static void main(String[] args)
    {
        //getReviewList("data.xml");
    }
}
