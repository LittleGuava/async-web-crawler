package com.axreng.backend.framework.adapter.in.api;

import com.axreng.backend.domain.dto.CrawlRequest;
import com.axreng.backend.domain.dto.CrawlResponse;
import com.axreng.backend.domain.port.CrawlerService;
import com.axreng.backend.domain.model.CrawlJob;
import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import spark.Route;

public class CrawlController {
    private final CrawlerService crawlerService;
    private final Gson gson;

    public CrawlController(CrawlerService crawlerService) {
        this.crawlerService = crawlerService;
        this.gson = new Gson();
    }

    public Route handlePostCrawl() {
        return (Request request, Response response) -> {
            try {
                CrawlRequest crawlRequest = gson.fromJson(request.body(), CrawlRequest.class);

                if (crawlRequest.getKeyword() == null) {
                    response.status(400);
                    return "{\"error\": \"Keyword is required\"}";
                }

                CrawlJob job = crawlerService.startCrawling(crawlRequest.getKeyword());

                response.status(200);
                response.type("application/json");

                return gson.toJson(new CrawlResponse(job.getId()));
            } catch (IllegalArgumentException e) {
                response.status(400);
                return "{\"error\": \"" + e.getMessage() + "\"}";
            }
        };
    }

    public Route handleGetCrawl() {
        return (Request request, Response response) -> {
            try {
                String id = request.params(":id");
                CrawlJob job = crawlerService.getCrawlStatus(id);

                response.status(200);
                response.type("application/json");

                return gson.toJson(new CrawlResponse(
                        job.getId(),
                        job.getStatus().toString().toLowerCase(),
                        job.getFoundUrls()
                ));
            } catch (IllegalArgumentException e) {
                response.status(404);
                return "{\"error\": \"Crawler not found\"}";
            }
        };
    }
}
