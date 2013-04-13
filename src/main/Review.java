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
    
    public static Review[] getReviewList(String filename) {
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
    
    public static void main(String[] args)
    {
        getReviewList("data.xml");
    }
}
