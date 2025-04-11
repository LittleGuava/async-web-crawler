package com.axreng.backend.domain.dto;

public class CrawlRequest {
    private String keyword;

    public CrawlRequest() {
    }

    public CrawlRequest(String keyword) {
        this.keyword = keyword;
    }

    public String getKeyword() {
        return keyword;
    }

    public void setKeyword(String keyword) {
        this.keyword = keyword;
    }
}
