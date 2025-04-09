import com.webcrawler.backend.application.CrawlerServiceImpl;
import com.webcrawler.backend.domain.model.CrawlJob;
import com.webcrawler.backend.domain.model.CrawlStatus;
import com.webcrawler.backend.domain.port.CrawlerRepository;
import com.webcrawler.backend.domain.port.CrawlerService;
import com.webcrawler.backend.framework.adapter.in.web.CrawlController;
import com.webcrawler.backend.framework.adapter.out.persistence.InMemoryCrawlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class IntegrationTest {

    private CrawlerRepository repository;
    private CrawlerService service;
    private CrawlController controller;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCrawlRepository();
        service = new CrawlerServiceImpl(repository);
        controller = new CrawlController(service);

        System.setProperty("BASE_URL", "https://man7.org/linux/man-pages/");
        System.setProperty("MULTITHREAD_OPT", "false");
    }

    @Test
    void testCrawlWorkflow() {
        CrawlJob job = service.startCrawling("security");
        assertNotNull(job);
        assertEquals("security", job.getKeyword());
        assertEquals(CrawlStatus.ACTIVE, job.getStatus());

        CrawlJob retrievedJob = service.getCrawlStatus(job.getId());
        assertNotNull(retrievedJob);
        assertEquals(job.getId(), retrievedJob.getId());
    }
}
