package com.axreng.backend.application;

import com.axreng.backend.domain.model.CrawlJob;
import com.axreng.backend.domain.port.CrawlerRepository;
import com.axreng.backend.domain.port.CrawlerService;
import com.axreng.backend.framework.adapter.out.crawler.WebCrawlerImpl;

import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class CrawlerServiceImpl implements CrawlerService {
    private final CrawlerRepository repository;
    private final ExecutorService executorService;
    public static final String BASE_URL = System.getenv("BASE_URL") != null ?
            System.getenv("BASE_URL") : "http://hiring.axreng.com/";

    public CrawlerServiceImpl(CrawlerRepository repository) {
        this.repository = repository;
        this.executorService = Executors.newFixedThreadPool(10);
    }


    @Override
    public CrawlJob startCrawling(String keyword) {
        if (keyword == null || keyword.length() < 4 || keyword.length() > 32) {
            throw new IllegalArgumentException("Keyword must be between 4 and 32 characters");
        }

        CrawlJob job = new CrawlJob(keyword);
        repository.save(job);

        executorService.submit(() -> {
            try {
                WebCrawlerImpl crawler = new WebCrawlerImpl(BASE_URL, keyword);
                crawler.crawl(job);
                job.markAsDone();
                repository.save(job);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        return job;
    }

    @Override
    public CrawlJob getCrawlStatus(String id) {
        Optional<CrawlJob> job = repository.findById(id);
        return job.orElseThrow(() -> new IllegalArgumentException("Crawler not found"));
    }
}
