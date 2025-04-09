package domain;

import com.axreng.backend.domain.model.CrawlJob;
import com.axreng.backend.domain.model.CrawlStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

public class CrawlJobTest {

    @Test
    void shouldCreateCrawlJobWithCorrectKeyword() {
        String keyword = "security";
        CrawlJob job = new CrawlJob(keyword);

        assertEquals(keyword, job.getKeyword());
        assertEquals(CrawlStatus.ACTIVE, job.getStatus());
        assertTrue(job.getFoundUrls().isEmpty());
        assertEquals(8, job.getId().length());
    }

    @Test
    void shouldAddFoundUrl() {
        CrawlJob job = new CrawlJob("test");
        String url = "http://example.com";

        job.addFoundUrl(url);

        assertEquals(1, job.getFoundUrls().size());
        assertTrue(job.getFoundUrls().contains(url));
    }

    @Test
    void shouldMarkJobAsDone() {
        CrawlJob job = new CrawlJob("test");

        job.markAsDone();

        assertEquals(CrawlStatus.DONE, job.getStatus());
    }

    @Test
    void shouldReturnDefensiveCopyOfFoundUrls() {
        CrawlJob job = new CrawlJob("test");
        job.addFoundUrl("http://example.com");

        List<String> urls = job.getFoundUrls();
        urls.add("http://shouldnotbeadded.com");

        assertEquals(1, job.getFoundUrls().size());
    }
}