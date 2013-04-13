/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package main;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author VanDuyTuan
 */
public class Main {

    public static void main(String[] args) {
        // TODO code application logic here
        String url = "http://www.yelp.com/biz/brendas-french-soul-food-san-francisco";
        //url = "http://www.yelp.com/biz/restaurant-ember-singapore";
        Crawler crawler = new Crawler(url);

    }
}