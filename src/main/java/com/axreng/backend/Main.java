package com.axreng.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import spark.Request;
import spark.Response;
import spark.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    private static final int PORT = 4567;
    private static final Map<String, WebCrawler> activeCrawlers = new ConcurrentHashMap<>();
    private static final Gson gson = new Gson();
    public static void main(String[] args) {
        Service http = Service.ignite()
                .port(PORT);

        getCrawlEndpoint(http);

        postCrawlEndpoint(http);
    }

    private static void postCrawlEndpoint(Service http) {
        http.get("/crawl/:id", (request, response) -> {
            return handlePostCrawl(request, response);
        });
    }

    public static String handlePostCrawl(Request request, Response response) {
        String id = request.params(":id");
        WebCrawler crawler = activeCrawlers.get(id);

        if (crawler == null) {
            response.status(404);
            return "{\"error\": \"Crawler not found\"}";
        }

        response.status(200);
        response.type("application/json");

        Map<String, Object> responseMap = new HashMap<>();
        responseMap.put("id", id);
        responseMap.put("status", crawler.isActive() ? "active" : "done");
        responseMap.put("urls", crawler.getFoundUrls());

        return gson.toJson(responseMap);
    }

    private static void getCrawlEndpoint(Service http) {
        http.post("/crawl", (request, response) -> {
            return handleGetCrawl(request, response);
        });
    }

    public static String handleGetCrawl(Request request, Response response) {
        JsonObject requestBody = gson.fromJson(request.body(), JsonObject.class);

        if (!requestBody.has("keyword")) {
            response.status(400);
            return "{\"error\": \"Keyword is required\"}";
        }

        String keyword = requestBody.get("keyword").getAsString();

        if (keyword.length() < 4 || keyword.length() > 32) {
            response.status(400);
            return "{\"error\": \"Keyword must be between 4 and 32 characters\"}";
        }

        String id = UUID.randomUUID().toString().substring(0, 8);

        WebCrawler crawler = new WebCrawler(id, keyword);
        activeCrawlers.put(id, crawler);

        new Thread(crawler::start).start();

        response.status(200);
        response.type("application/json");

        Map<String, String> responseMap = new HashMap<>();
        responseMap.put("id", id);

        return gson.toJson(responseMap);
    }
}

