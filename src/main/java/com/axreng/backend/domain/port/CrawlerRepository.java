package com.axreng.backend.domain.port;

import com.axreng.backend.domain.model.CrawlJob;
import java.util.Optional;

public interface CrawlerRepository {
    void save(CrawlJob job);
    Optional<CrawlJob> findById(String id);
}
