package com.axreng.backend.domain.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CrawlJob {
    private final String id;
    private final String keyword;
    private CrawlStatus status;
    private final List<String> foundUrls;

    public CrawlJob(String keyword) {
        this.id = UUID.randomUUID().toString().substring(0, 8);
        this.keyword = keyword;
        this.status = CrawlStatus.ACTIVE;
        this.foundUrls = new ArrayList<>();
    }

    public String getId() { return id; }
    public String getKeyword() { return keyword; }
    public CrawlStatus getStatus() { return status; }
    public List<String> getFoundUrls() { return new ArrayList<>(foundUrls); }

    public void markAsDone() { this.status = CrawlStatus.DONE; }

    public void addFoundUrl(String url) {
        foundUrls.add(url);
    }
}
