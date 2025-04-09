package com.webcrawler.backend.domain.port;

import com.webcrawler.backend.domain.model.CrawlJob;

public interface CrawlerService {
    CrawlJob startCrawling(String keyword);
    CrawlJob getCrawlStatus(String id);
}
