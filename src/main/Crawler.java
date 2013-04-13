/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;
import org.apache.commons.lang3.StringEscapeUtils;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.plaf.metal.MetalIconFactory;
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

    private String frontUrl = "http://www.tripadvisor.com/Restaurant_Review-g45963-d422627-Reviews";
    private String backUrl = "-Eiffel_Tower_Restaurant_at_Paris_Las_Vegas-Las_Vegas_Nevada.html";
    private String ajaxUrl = "http://www.tripadvisor.com/ExpandedUserReviews-g45963-d422627?target=156507866&context=1&reviews=156507866&servlet=Restaurant_Review&expand=1&extraad=true";
    private String targetNo;
    private String site;
    private String parameter;
    private int numberOfReviews;
    private int step;
    private String startPage;
    private final String dataFolder = "./data";
    private final Pattern datePattern = Pattern.compile("\\d{1,2}/\\d{1,2}/\\d{2,4}");

    public Crawler() {
        String temp = "";
        temp = "?target=";
        int index = this.ajaxUrl.indexOf(temp);
        if (index == -1) {
            return;
        }
        int s = this.ajaxUrl.indexOf("&", index);
        targetNo = this.ajaxUrl.substring(index + temp.length(), s);
    }

    private void getDomain() {
        String temp = "http://";
        int s, e;
        s = frontUrl.indexOf(temp);
        if (s == -1) {
            return;
        }
        temp = "www.";
        s = frontUrl.indexOf(temp, s);
        if (s == -1) {
            return;
        }
        e = frontUrl.indexOf(".com", s);
        if (e == -1) {
            return;
        }
        site = frontUrl.substring(s + 4, e);
    }

    private void parseURL() {
        if (frontUrl.length() == 0) {
            return;
        }
        String temp = "http://";
        if (frontUrl.indexOf(temp) != 0) {
            return;
        }
    }

    private void getStep() {
        switch (site) {
            case "yelp":
                step = 20;
                break;
            case "tripadvisor":
                step = 10;
                break;
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
        String temp = frontUrl + "-or" + reviewNumber + backUrl;
        StringBuilder contentBuilder = new StringBuilder();
        try {
            final URL urlObj = new URL(frontUrl + temp);
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

    private boolean isReview(String id) {
        if (id.contains("review_")) {
            return true;
        } else {
            return false;
        }
    }

    private ArrayList<String> getReviewsList(Document document, int reviewNo) {
        ArrayList<String> list;
        Element element = document.getElementById("REVIEWS");
        Elements nodeList = element.children();
        list = new ArrayList<String>();
        int i = 0;
        for (Element node : nodeList) {
            if (isReview(node.attr("id"))) {
                String review = getReviewFromLI(node, reviewNo);
                if (review.equals("")) {
                    continue;
                }
                list.add(review);
            }
        }
        return list;
    }

    private String getStars(Element node) {
        node = node.child(0);
        node = node.child(1);
        node = node.child(1);
        node = node.child(0);
        node = node.child(0);
        String stars = node.attr("content");
        stars = "<stars>" + stars + "</stars>";
        return stars;
    }

    private String getDate(Element node) {
        node = node.child(0);
        node = node.child(1);
        node = node.child(1);
        node = node.child(1);
        node.select("span").remove();

        String date = node.html();
        date = date.replaceAll("Reviewed ", "");
        date = "<date>" + date + "</date>";
        return date;
    }

    private String getUser(Element node) {
        node = node.child(0);
        node = node.child(0);
        node = node.child(0);
        node = node.child(0);
        node = node.child(1);
        node = node.child(0);
        String user = node.html();
        user = "<user>" + "http://www.tripadvisor.com/members/" + user + "</user>";
        return user;

    }

    private String getReview(String id) {
        System.out.println(id);
        String ajaxRequest = ajaxUrl.replaceAll(targetNo, id);
        StringBuilder contentBuilder = new StringBuilder();
        try {
            final URL urlObj = new URL(ajaxRequest);
            BufferedReader in = null;
            in = new BufferedReader(new InputStreamReader(urlObj.openStream()));

            String line;

            while ((line = in.readLine()) != null) {
                contentBuilder.append(line);
            }

            in.close();
        } catch (Exception e) {
        }
        String content = contentBuilder.toString();

        Document document = Jsoup.parse(content);
        Element node = document.getElementById("UR" + id);
        Elements nodes = node.getElementsByClass("entry");
        node = nodes.first();
        node = node.child(0);
        clearElement(node);
        String review = node.html();
        review = review.replaceAll("&nbsp;", " ");
        review = "<review>" + review + "</review>";
        return review;
    }

    private String getReviewId(String id) {
        String[] list = id.split("_");
        return list[1];
    }

    private String getTitle(Element node) {
        node = node.child(0);
        node = node.child(1);
        node = node.child(0);
        node = node.child(0);
        String title = node.html();
        title = title.substring(1, title.length() - 1);
        title = "<title>" + title + "</title>";
        return title;
    }

    private String getReviewFromLI(Element node, int reviewNo) {
        if (node == null) {
            return "";
        }
        String id = node.attr("id");
        id = getReviewId(id);
        // if there is any issue with the review, we have to ignore some post,
        // fortunately, not so many post has issue
        try {
            StringBuilder content = new StringBuilder();
            String starsTag = getStars(node);
            String urlTag = "<url>" + frontUrl + "-or" + reviewNo + backUrl + "</url>";
            String dateTag = getDate(node);
            String userTag = getUser(node);
            String titleTag = getTitle(node);
            String reviewTag = getReview(id);
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
        } catch (Exception e) {
        }
        return "";
    }

    private void clearElement(Element element) {
        element.select("br").remove();
        element.select("a").remove();
    }

    private void getAllReviews() {
        int currentReviewIndex = 300;
        while (currentReviewIndex < numberOfReviews - 1) {
            try {
                System.out.println(currentReviewIndex);
                ArrayList<String> list = getReviewsList(getPageFromReview(currentReviewIndex), currentReviewIndex);
                for (String review : list) {
                    File file = createDataFile((currentReviewIndex + 1) + ".txt");
                    review = "<doc id='" + (currentReviewIndex + 1) + "'>" + review + "</doc>";
                    writeToFile(file, review);
                    currentReviewIndex++;
                }
                System.out.println("Sleep");
                Thread.sleep(0);
            } catch (InterruptedException ex) {
                Logger.getLogger(Crawler.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private void getNumberOfReviews() {
        String temp = "reviews_header";
        int index = startPage.indexOf(temp);
        if (index == -1) {
            return;
        }
        index = startPage.indexOf(">", index);
        int endIndex = startPage.indexOf("<", index);
        temp = startPage.substring(index + 1, endIndex);
        String[] list = temp.split(" ");
        temp = list[0];
        temp = temp.replaceAll(",", "");
        try {
            numberOfReviews = Integer.parseInt(temp);
        } catch (Exception e) {
            System.err.println("Can not read the number of review.");
        }
    }

    private void getStartPage() {
        StringBuilder contentBuilder = new StringBuilder();
        String url = frontUrl + backUrl;
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
        parseURL();
        getDomain();
        getStartPage();
        getNumberOfReviews();
        getStep();
        createDataFolder();
        getAllReviews();
    }
    private String convertISOtoASCII(String line){
        line = StringEscapeUtils.unescapeHtml4(line);
        line = line.replaceAll("&", "and");
        return line;
        //return Normalizer.normalize(line, Normalizer.Form.NFC);
    }
    public void combineXML() {
        File dataDir = new File(dataFolder);
        File xmlFile = createDataFile("data.xml");
        File[] dataFiles = dataDir.listFiles();
        int length = dataFiles.length;
        int i;
        File dataFile;
        BufferedReader in;
        StringBuilder sb;
        String line;
        try {
            FileWriter out = new FileWriter(xmlFile);
            out.write("<root>" + "\n");
            for (i = 1; i < length-1; i++) {
                dataFile = new File(dataFolder + "/" + i + ".txt");

                in = new BufferedReader(new FileReader(dataFile));
                sb = new StringBuilder();
                line = in.readLine();

                while (line != null) {
                    sb.append(line);
                    sb.append("\n");
                    line = in.readLine();
                }
                in.close();
                line = convertISOtoASCII(sb.toString());
                
                out.write(line + "\n");
                
            }
            out.write("</root>" + "\n");
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
