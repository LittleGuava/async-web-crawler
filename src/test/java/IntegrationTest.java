import com.axreng.backend.application.CrawlerServiceImpl;
import com.axreng.backend.domain.model.CrawlJob;
import com.axreng.backend.domain.model.CrawlStatus;
import com.axreng.backend.domain.port.CrawlerRepository;
import com.axreng.backend.domain.port.CrawlerService;
import com.axreng.backend.framework.adapter.in.api.CrawlController;
import com.axreng.backend.framework.adapter.out.persistence.InMemoryCrawlRepository;
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

        System.setProperty("BASE_URL", "http://hiring.axreng.com/");
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
