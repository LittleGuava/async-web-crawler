package com.axreng.backend;

import com.axreng.backend.adapter.in.web.CrawlController;
import com.axreng.backend.adapter.out.persistence.InMemoryCrawlRepository;
import com.axreng.backend.application.CrawlerServiceImpl;
import com.axreng.backend.config.SparkConfig;
import com.axreng.backend.domain.port.CrawlerRepository;
import com.axreng.backend.domain.port.CrawlerService;

public class Main {
    public static void main(String[] args) {
        // Instanciação dos componentes
        CrawlerRepository repository = new InMemoryCrawlRepository();
        CrawlerService service = new CrawlerServiceImpl(repository);
        CrawlController controller = new CrawlController(service);

        // Configuração do servidor HTTP
        new SparkConfig(controller);
    }
}
