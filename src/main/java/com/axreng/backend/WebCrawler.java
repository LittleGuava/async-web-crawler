package com.axreng.backend;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawler {
    private final String id;
    private final String keyword;
    private boolean active;
    private final List<String> foundUrls;
    private final Set<String> visitedUrls;
    private final Queue<String> urlsToVisit;
    private static final String BASE_URL = System.getenv("BASE_URL");

    private static final Pattern HREF_PATTERN = Pattern.compile("href=[\"']([^\"']+)[\"']");
    private static final int MAX_RESULTS = 100;

    public WebCrawler(String id, String keyword) {
        this.id = id;
        this.keyword = keyword.toLowerCase(); // Para busca case-insensitive
        this.active = false;
        this.foundUrls = new CopyOnWriteArrayList<>();
        this.visitedUrls = new CopyOnWriteArraySet<>();
        this.urlsToVisit = new LinkedList<>();
    }

    public void start() {
        this.active = true;
        try {
            urlsToVisit.add(BASE_URL);
            crawlWebsite();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.active = false;
        }
    }

    private void crawlWebsite() {
        while (!urlsToVisit.isEmpty() && foundUrls.size() < MAX_RESULTS) {
            String currentUrl = urlsToVisit.poll();

            if (visitedUrls.contains(currentUrl)) {
                continue;
            }

            try {
                visitedUrls.add(currentUrl);

                String pageContent = fetchUrl(currentUrl);
                if (pageContent == null) {
                    continue;
                }

                if (pageContent.toLowerCase().contains(keyword)) {
                    foundUrls.add(currentUrl);
                }

                extractLinks(pageContent, currentUrl);

            } catch (Exception e) {
                System.err.println("Error processing URL: " + currentUrl);
                e.printStackTrace();
            }

            // Pequena pausa para evitar sobrecarga no servidor
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private String fetchUrl(String urlString) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            int status = connection.getResponseCode();
            if (status != 200) {
                System.err.println("Received non-200 status code: " + status + " for URL: " + urlString);
                return null;
            }

            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(connection.getInputStream()));
            StringBuilder content = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                content.append(line);
                content.append(System.lineSeparator());
            }
            reader.close();

            return content.toString();

        } catch (Exception e) {
            System.err.println("Error fetching URL: " + urlString);
            e.printStackTrace();
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private void extractLinks(String html, String baseUrl) {
        Matcher matcher = HREF_PATTERN.matcher(html);
        while (matcher.find()) {
            String href = matcher.group(1);
            String absoluteUrl = resolveUrl(href, baseUrl);

            // Adiciona à fila apenas URLs que pertencem ao mesmo domínio base
            if (absoluteUrl != null && absoluteUrl.startsWith(BASE_URL)
                    && !visitedUrls.contains(absoluteUrl)) {
                urlsToVisit.add(absoluteUrl);
            }
        }
    }

    private String resolveUrl(String href, String base) {
        try {
            if (href.startsWith("http://") || href.startsWith("https://")) {
                return href;
            } else if (href.startsWith("/")) {
                // Link absoluto no mesmo domínio
                URL baseUrl = new URL(base);
                return baseUrl.getProtocol() + "://" + baseUrl.getHost() +
                        (baseUrl.getPort() > 0 ? ":" + baseUrl.getPort() : "") + href;
            } else if (!href.startsWith("#")) {
                // Link relativo, excluindo links de âncora
                int lastSlashIndex = base.lastIndexOf('/');
                if (lastSlashIndex > 7) {  // 7 é o tamanho de "http://"
                    return base.substring(0, lastSlashIndex + 1) + href;
                } else {
                    return base + "/" + href;
                }
            }
        } catch (MalformedURLException e) {
            System.err.println("Error resolving URL: " + href);
        }
        return null;
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
