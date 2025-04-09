package com.axreng.backend;

import com.axreng.backend.framework.adapter.in.api.CrawlController;
import com.axreng.backend.framework.adapter.out.persistence.InMemoryCrawlRepository;
import com.axreng.backend.application.CrawlerServiceImpl;
import com.axreng.backend.framework.config.SparkConfig;
import com.axreng.backend.domain.port.CrawlerRepository;
import com.axreng.backend.domain.port.CrawlerService;

public class Main {
    public static void main(String[] args) {
        CrawlerRepository repository = new InMemoryCrawlRepository();
        CrawlerService service = new CrawlerServiceImpl(repository);
        CrawlController controller = new CrawlController(service);

        new SparkConfig(controller);
    }
}
