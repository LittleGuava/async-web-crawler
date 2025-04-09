package com.axreng.backend.config;

import com.axreng.backend.adapter.in.web.CrawlController;
import spark.Service;

public class SparkConfig {
    private final Service http;
    private final CrawlController crawlController;

    public SparkConfig(CrawlController crawlController) {
        this.http = Service.ignite().port(4567);
        this.crawlController = crawlController;
        configureRoutes();
    }

    private void configureRoutes() {
        http.post("/crawl", crawlController.handlePostCrawl());
        http.get("/crawl/:id", crawlController.handleGetCrawl());
    }
}
