package com.webcrawler.backend;

import com.webcrawler.backend.framework.adapter.in.web.CrawlController;
import com.webcrawler.backend.framework.adapter.out.persistence.InMemoryCrawlRepository;
import com.webcrawler.backend.application.CrawlerServiceImpl;
import com.webcrawler.backend.framework.config.SparkConfig;
import com.webcrawler.backend.domain.port.CrawlerRepository;
import com.webcrawler.backend.domain.port.CrawlerService;

public class Main {
    public static void main(String[] args) {
        CrawlerRepository repository = new InMemoryCrawlRepository();
        CrawlerService service = new CrawlerServiceImpl(repository);
        CrawlController controller = new CrawlController(service);

        new SparkConfig(controller);
    }
}
