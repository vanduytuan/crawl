/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.select.Elements;

/**
 *
 * @author VanDuyTuan
 */
public class Crawler {

    private String url = "http://www.yelp.com/biz/brendas-french-soul-food-san-francisco";
    private String site;
    private String parameter;
    private int numberOfReviews;
    private int step;
    private String startPage;
    private final String dataFolder = "./data";
    private final Pattern datePattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2,4}");

    
    public Crawler(String url) {
        
        this.url = url;
        parseURL();
        getDomain();
        getStartPage();
        getNumberOfReviews();
        getStep();
        createDataFolder();
        getAllReviews();

    }
    private void getDomain() {
        String temp = "http://";
        int s, e;
        s = url.indexOf(temp);
        if (s == -1) {
            return;
        }
        temp = "www.";
        s = url.indexOf(temp, s);
        if (s == -1) {
            return;
        }
        e = url.indexOf(".com", s);
        if (e == -1) {
            return;
        }
        site = url.substring(s + 4, e);
    }

    private void parseURL() {
        if (url.length() == 0) {
            return;
        }
        String temp = "http://";
        if (url.indexOf(temp) != 0) {
            return;
        }
    }

    private void getStep() {
        switch (site) {
            case "yelp":
                step = 20;
        }
    }

    private void createDataFolder() {
        (new File(dataFolder)).mkdirs();
    }

    private File createDataFile(String fileName) {
        File file = new File(dataFolder + "/" + fileName);
        return file;
    }

    private void writeToFile(File file, String content) {
        try {
            BufferedWriter out = new BufferedWriter(new FileWriter(file));
            out.write(content);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Document getPageFromReview(int reviewNumber) {
        String temp = "?start=" + reviewNumber;
        StringBuilder contentBuilder = new StringBuilder();
        try {
            final URL urlObj = new URL(url + temp);
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(urlObj.openStream()));

            String line;

            while ((line = in.readLine()) != null) {
                contentBuilder.append(line);
            }

            in.close();
        } catch (IOException e) {
            System.err.println("Can not read the main page for crawling.");
            e.printStackTrace();
        }
        Document doc = Jsoup.parse(contentBuilder.toString());
        return doc;
    }

    private String[] getReviewsList(Document document, int reviewNo) {
        String[] list;
        Element element = document.getElementById("reviews-other");
        Element ulObject = element.child(1);
        Elements nodeList = ulObject.children();
        list = new String[nodeList.size()];
        int i = 0;
        for (Element node : nodeList) {
            list[i++] = getReviewFromLI(node, reviewNo);
        }
        return list;
    }

    private String getStars(Element node) {
        node = node.child(1);
        node = node.child(1);
        node = node.child(0);
        node = node.child(0);
        node = node.child(0);
        node = node.child(0);
        String stars = node.attr("title");
        String[] list = stars.split(" ");
        stars = "<stars>" + list[0] + "</stars>";
        return stars;
    }

    private String getDate(Element node) {
        node = node.child(1);
        node = node.child(1);
        node = node.child(0);
        node = node.child(2);
        String date = node.html();
        // search for the date
        Matcher matcher = datePattern.matcher(date);
        while (matcher.find()) {
            date = matcher.group();
            break;
        }
        date = "<date>" + date + "</date>";
        return date;
    }

    private String getUser(Element node) {
        node = node.child(1);
        node = node.child(0);
        node = node.child(0);
        node = node.child(0);
        node = node.child(0);
        String user = node.absUrl("href");
        user = "<user>" + user + "</user>";
        return user;

    }

    private String getReview(Element node) {
        node = node.child(1);
        node = node.child(1);
        node = node.child(1);
        clearElement(node);
        String review = node.html();
        review = "<review>" + review + "</review>";
        return review;
    }

    private String getReviewFromLI(Element node, int reviewNo) {
        if (node == null) {
            return "";
        }
        StringBuilder content = new StringBuilder();
        String starsTag = getStars(node);
        String urlTag = "<url>" + url + "?start=" + reviewNo + "</url>";
        String dateTag = getDate(node);
        String userTag = getUser(node);
        String titleTag = "<title></title>";
        String reviewTag = getReview(node);
        String polarityTag = "<polarity>NULL</polarity>";
        String confidenceTag = "<confidence>NULL</confidence>";
        content.append(starsTag);
        content.append(urlTag);
        content.append(dateTag);
        content.append(userTag);
        content.append(titleTag);
        content.append(reviewTag);
        content.append(polarityTag);
        content.append(confidenceTag);
        String reviewXML = content.toString();
        return reviewXML;
    }

    private void clearElement(Element element) {
        element.select("br").remove();
        element.select("a").remove();
    }

    private void getAllReviews() {
        int currentReviewIndex = 0;
        while (currentReviewIndex < numberOfReviews) {
            try {
                System.out.println(currentReviewIndex);
                String[] list = getReviewsList(getPageFromReview(currentReviewIndex), currentReviewIndex);
                for (String review : list) {
                    File file = createDataFile((currentReviewIndex + 1) + ".txt");
                    review = "<doc id='" + (currentReviewIndex + 1) + "'>" + review + "</doc>";
                    writeToFile(file, review);
                    currentReviewIndex++;
                }
                System.out.println("Sleep");
                Thread.sleep(15000);
            } catch (InterruptedException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void getNumberOfReviews() {
        String temp = "reviewCount";
        int index = startPage.indexOf(temp);
        if (index == -1) {
            return;
        }
        index = startPage.indexOf(">", index);
        int endIndex = startPage.indexOf("<", index);
        temp = startPage.substring(index + 1, endIndex);
        try {
            numberOfReviews = Integer.parseInt(temp);
        } catch (Exception e) {
            System.err.println("Can not read the number of review.");
        }
    }

    private void getStartPage() {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            final URL urlObj = new URL(url);
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(urlObj.openStream()));

            String line;

            while ((line = in.readLine()) != null) {
                contentBuilder.append(line);
            }

            in.close();
        } catch (IOException e) {
            System.err.println("Can not read the main page for crawling.");
            e.printStackTrace();
        }
        startPage = contentBuilder.toString();
    }

    public void crawl() {
    }
}
