package com.axreng.backend.domain.dto;

import java.util.List;

public class CrawlResponse {
    private String id;
    private String status;
    private List<String> urls;

    public CrawlResponse(String id, String status, List<String> urls) {
        this.id = id;
        this.status = status;
        this.urls = urls;
    }

    public CrawlResponse(String id) {
        this.id = id;
        this.status = null;
        this.urls = null;
    }

    public String getId() {
        return id;
    }

    public String getStatus() {
        return status;
    }

    public List<String> getUrls() {
        return urls;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setUrls(List<String> urls) {
        this.urls = urls;
    }
}
