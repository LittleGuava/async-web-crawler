package com.axreng.backend.domain.port;

import com.axreng.backend.domain.model.CrawlJob;

public interface CrawlerService {
    CrawlJob startCrawling(String keyword);
    CrawlJob getCrawlStatus(String id);
}
