package com.webcrawler.backend.domain.port;

import com.webcrawler.backend.domain.model.CrawlJob;
import java.util.Optional;

public interface CrawlerRepository {
    void save(CrawlJob job);
    Optional<CrawlJob> findById(String id);
}
