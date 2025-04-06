package com.axreng.backend;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import spark.Service;
import spark.Spark;

import spark.Spark.*;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

        http.post("/crawl", (request, response) -> {
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
        });

        http.get("/crawl/:id", (request, response) -> {
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
        });
    }
}

