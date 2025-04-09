import com.axreng.backend.domain.model.CrawlJob;
import com.axreng.backend.domain.model.CrawlStatus;
import com.axreng.backend.domain.port.CrawlerService;
import com.axreng.backend.framework.adapter.in.api.CrawlController;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import spark.Request;
import spark.Response;
import spark.Route;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class CrawlControllerTest {

    @Mock
    private CrawlerService crawlerService;

    @Mock
    private Request request;

    @Mock
    private Response response;

    private CrawlController controller;
    private Gson gson;

    @BeforeEach
    void setUp() {
        crawlerService = mock(CrawlerService.class);
        controller = new CrawlController(crawlerService);
        gson = new Gson();
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void postCrawlShouldReturnIdForValidKeyword() throws Exception {
        String requestBody = "{\"keyword\": \"security\"}";
        when(request.body()).thenReturn(requestBody);

        CrawlJob job = mock(CrawlJob.class);
        when(job.getId()).thenReturn("12345678");
        when(crawlerService.startCrawling("security")).thenReturn(job);

        Route route = controller.handlePostCrawl();
        Object result = route.handle(request, response);

        verify(response).status(200);
        verify(response).type("application/json");

        String expectedJson = "{\"id\":\"12345678\"}";
        assertEquals(expectedJson, result);
    }

    @Test
    void postCrawlShouldReturn400ForMissingKeyword() throws Exception {
        // Arrange
        String requestBody = "{}"; // No keyword
        when(request.body()).thenReturn(requestBody);

        // Act
        Route route = controller.handlePostCrawl();
        Object result = route.handle(request, response);

        // Assert
        verify(response).status(400);
        assertEquals("{\"error\": \"Keyword is required\"}", result);
    }

    @Test
    void getCrawlShouldReturnCrawlJobStatusAndUrls() throws Exception {
        // Arrange
        String id = "12345678";
        when(request.params(":id")).thenReturn(id);

        CrawlJob job = mock(CrawlJob.class);
        when(job.getId()).thenReturn(id);
        when(job.getStatus()).thenReturn(CrawlStatus.ACTIVE);
        List<String> urls = Arrays.asList("http://example.com/page1", "http://example.com/page2");
        when(job.getFoundUrls()).thenReturn(urls);

        when(crawlerService.getCrawlStatus(id)).thenReturn(job);

        Route route = controller.handleGetCrawl();
        Object result = route.handle(request, response);

        verify(response).status(200);
        verify(response).type("application/json");

        assertTrue(result.toString().contains("\"id\":\"12345678\""));
        assertTrue(result.toString().contains("\"status\":\"active\""));
        assertTrue(result.toString().contains("\"urls\""));
    }

    @Test
    void getCrawlShouldReturn404ForNonexistentId() throws Exception {
        String id = "nonexistent";
        when(request.params(":id")).thenReturn(id);

        when(crawlerService.getCrawlStatus(id))
                .thenThrow(new IllegalArgumentException("Crawler not found"));

        Route route = controller.handleGetCrawl();
        Object result = route.handle(request, response);

        verify(response).status(404);
        assertEquals("{\"error\": \"Crawler not found\"}", result);
    }
}
