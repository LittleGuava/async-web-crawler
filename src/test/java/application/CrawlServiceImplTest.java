package application;

import com.axreng.backend.application.CrawlerServiceImpl;
import com.axreng.backend.domain.model.CrawlJob;
import com.axreng.backend.domain.model.CrawlStatus;
import com.axreng.backend.domain.port.CrawlerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class CrawlServiceImplTest {

    @Mock
    private CrawlerRepository repository;

    private CrawlerServiceImpl service;

    @BeforeEach
    void setUp() {
        repository = mock(CrawlerRepository.class);
        service = new CrawlerServiceImpl(repository);
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void shouldStartCrawlingWithValidKeyword() {
        String keyword = "security";

        CrawlJob job = service.startCrawling(keyword);

        assertNotNull(job);
        assertEquals(keyword, job.getKeyword());
        assertEquals(CrawlStatus.ACTIVE, job.getStatus());
        verify(repository).save(job);
    }

    @Test
    void shouldThrowExceptionForInvalidKeyword() {
        String shortKeyword = "abc";

        assertThrows(IllegalArgumentException.class, () -> {
            service.startCrawling(shortKeyword);
        });

        String longKeyword = "a".repeat(33);

        assertThrows(IllegalArgumentException.class, () -> {
            service.startCrawling(longKeyword);
        });

        verify(repository, never()).save(any());
    }

    @Test
    void shouldThrowExceptionWhenCrawlJobNotFound() {
        String id = "nonexistent";
        when(repository.findById(id)).thenReturn(Optional.empty());

        assertThrows(IllegalArgumentException.class, () -> {
            service.getCrawlStatus(id);
        });
    }
}
