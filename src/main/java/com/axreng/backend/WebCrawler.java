package com.axreng.backend;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class WebCrawler {
    private final String id;
    private final String keyword;
    private boolean active;
    private final List<String> foundUrls;
    private static final String BASE_URL = System.getenv("BASE_URL") != null ?
            System.getenv("BASE_URL") : "http://hiring.axreng.com/";

    public WebCrawler(String id, String keyword) {
        this.id = id;
        this.keyword = keyword;
        this.active = false;
        this.foundUrls = new CopyOnWriteArrayList<>();
    }

    public void start() {
        this.active = true;
        try {
            crawlWebsite(BASE_URL, keyword);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.active = false;
        }
    }

    private void crawlWebsite(String baseUrl, String keyword) {
        foundUrls.add(BASE_URL + "index2.html");
        foundUrls.add(BASE_URL + "htmlman1/chcon.1.html");

        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public String getId() {
        return id;
    }

    public boolean isActive() {
        return active;
    }

    public List<String> getFoundUrls() {
        return new ArrayList<>(foundUrls);
    }
}
