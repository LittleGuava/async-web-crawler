package adapter.out.crawler;

import com.axreng.backend.domain.model.CrawlJob;
import com.axreng.backend.domain.model.CrawlStatus;
import com.axreng.backend.framework.adapter.out.persistence.InMemoryCrawlRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

public class InMemoryCrawlRepositoryTest {

    private InMemoryCrawlRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryCrawlRepository();
    }

    @Test
    void shouldSaveAndRetrieveCrawlJob() {
        CrawlJob job = new CrawlJob("security");
        String id = job.getId();

        repository.save(job);
        Optional<CrawlJob> retrieved = repository.findById(id);

        assertTrue(retrieved.isPresent());
        assertSame(job, retrieved.get());
    }

    @Test
    void shouldReturnEmptyOptionalForNonexistentId() {
        Optional<CrawlJob> result = repository.findById("nonexistent");

        assertFalse(result.isPresent());
    }

    @Test
    void shouldUpdateExistingCrawlJob() {

        CrawlJob job = new CrawlJob("security");
        String id = job.getId();
        repository.save(job);

        job.addFoundUrl("http://example.com");
        job.markAsDone();
        repository.save(job);

        // Assert
        Optional<CrawlJob> retrieved = repository.findById(id);
        assertTrue(retrieved.isPresent());
        assertEquals(1, retrieved.get().getFoundUrls().size());
        assertEquals(CrawlStatus.DONE, retrieved.get().getStatus());
    }
}
