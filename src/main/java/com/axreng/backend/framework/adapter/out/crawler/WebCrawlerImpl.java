package com.axreng.backend.framework.adapter.out.crawler;

import com.axreng.backend.domain.model.CrawlJob;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WebCrawlerImpl {
    private final String baseUrl;
    private final String keyword;
    private final Set<String> visitedUrls = Collections.synchronizedSet(new HashSet<>());
    private final Queue<String> urlsToVisit = new LinkedList<>();
    private static final Pattern HREF_PATTERN = Pattern.compile("href=[\"']([^\"']+)[\"']");
    private static final int MAX_RESULTS = 100;
    private final int threadCount;
    private final ExecutorService executorService;

    public WebCrawlerImpl(String baseUrl, String keyword) {
        this.baseUrl = baseUrl;
        this.keyword = keyword.toLowerCase();
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        this.threadCount = Math.min(2 * availableProcessors, 16);
        this.executorService = Executors.newFixedThreadPool(threadCount);
    }

    public void crawl(CrawlJob job) {
        urlsToVisit.add(baseUrl);

        boolean useMultithread = "true".equalsIgnoreCase(System.getenv("MULTITHREAD_OPT"));

        if (useMultithread) {
            crawlParallel(job);
        } else {
            crawlSequential(job);
        }

        if (useMultithread) {
            executorService.shutdown();
        }
    }

    private void crawlSequential(CrawlJob job) {
        while (!urlsToVisit.isEmpty() && job.getFoundUrls().size() < MAX_RESULTS) {
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
                    job.addFoundUrl(currentUrl);
                }

                extractLinks(pageContent, currentUrl);

            } catch (Exception e) {
                System.err.println("Error processing URL: " + currentUrl);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    private void crawlParallel(CrawlJob job) {
        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < threadCount; i++) {
            futures.add(executorService.submit(() -> processUrlQueue(job)));
        }

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void processUrlQueue(CrawlJob job) {
        while (true) {
            String currentUrl;
            synchronized (urlsToVisit) {
                if (urlsToVisit.isEmpty() || job.getFoundUrls().size() >= MAX_RESULTS) {
                    return;
                }
                currentUrl = urlsToVisit.poll();
            }

            if (currentUrl == null) return;

            boolean shouldProcess;
            synchronized (visitedUrls) {
                shouldProcess = !visitedUrls.contains(currentUrl);
                if (shouldProcess) {
                    visitedUrls.add(currentUrl);
                }
            }

            if (!shouldProcess) {
                continue;
            }

            try {
                String pageContent = fetchUrl(currentUrl);

                if (pageContent == null) {
                    continue;
                }

                if (pageContent.toLowerCase().contains(keyword)) {
                    job.addFoundUrl(currentUrl);
                }

                List<String> newUrls = new ArrayList<>();
                extractLinksToList(pageContent, currentUrl, newUrls);

                synchronized (urlsToVisit) {
                    for (String newUrl : newUrls) {
                        if (!visitedUrls.contains(newUrl)) {
                            urlsToVisit.add(newUrl);
                        }
                    }
                }

            } catch (Exception e) {
                System.err.println("Error processing URL: " + currentUrl);
            }

            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
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

    private void extractLinks(String html, String baseUrlStr) {
        Matcher matcher = HREF_PATTERN.matcher(html);
        while (matcher.find()) {
            String href = matcher.group(1);
            String absoluteUrl = resolveUrl(href, baseUrlStr);

            if (absoluteUrl != null && absoluteUrl.startsWith(this.baseUrl)
                    && !visitedUrls.contains(absoluteUrl)) {
                urlsToVisit.add(absoluteUrl);
            }
        }
    }

    private void extractLinksToList(String html, String baseUrlStr, List<String> result) {
        Matcher matcher = HREF_PATTERN.matcher(html);
        while (matcher.find()) {
            String href = matcher.group(1);
            String absoluteUrl = resolveUrl(href, baseUrlStr);

            if (absoluteUrl != null && absoluteUrl.startsWith(this.baseUrl)) {
                result.add(absoluteUrl);
            }
        }
    }

    private String resolveUrl(String href, String base) {
        try {
            if (href.startsWith("http://") || href.startsWith("https://")) {
                return href;
            } else if (href.startsWith("/")) {
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
}
