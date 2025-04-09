package com.webcrawler.backend.framework.adapter.out.persistence;

import com.webcrawler.backend.domain.model.CrawlJob;
import com.webcrawler.backend.domain.port.CrawlerRepository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Implementação de um repositório em memória que utiliza um Map para armazenar os CrawlJobs.
 */
public class InMemoryCrawlRepository implements CrawlerRepository {

    private final Map<String, CrawlJob> repository;

    public InMemoryCrawlRepository() {
        this.repository = new ConcurrentHashMap<>();
    }

    @Override
    public void save(CrawlJob job) {
        repository.put(job.getId(), job);
    }

    @Override
    public Optional<CrawlJob> findById(String id) {
        return Optional.ofNullable(repository.get(id));
    }
}
