/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

/**
 *
 * @author VanDuyTuan
 */
public class Main {
    
    public static void main(String[] args) {
        // TODO code application logic here
        String url = "";
        Crawler crawler = new Crawler(url);
        crawler.crawl();
    }

}