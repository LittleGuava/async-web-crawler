# Technical Test: Backend Software Developer

This project was developed as part of a technical test to implement an HTTP API for searching URLs containing a term provided by the user. The application uses a hexagonal architecture, allowing greater testability and maintainability.

---

## 🎯 **Objective**

Develop a Java application to navigate a website and list URLs containing a term provided by the user. The system supports multiple simultaneous searches and returns partial results while the search is in progress.

---

## 🚀 **How to Run the Application**

### **Prerequisites**

- **Docker** installed on your machine
- **Environment variable** `BASE_URL` configured to determine the base URL for link searches.


### **Steps to Start the Application**

1. Build the Docker image:
```bash
docker build . -t webcrawler/backend
```

2. Run the Docker container:
```bash
docker run -e BASE_URL=https://man7.org/linux/man-pages/ -p 4567:4567 --rm webcrawler/backend
```

The application will be available at: `http://localhost:4567`.

---

## 📚 **Project Structure**

The application follows the hexagonal architecture:

```
com.webcrawler.backend/
├── domain/
│   ├── model/             
│   │   ├── CrawlJob.java         # Represents the characteristics of a search job
│   │   ├── CrawlStatus.java      # Enum for status ("active" or "done")
│   ├── port/                    
│       ├── CrawlerService.java   # Port for executing crawling
│       ├── CrawlRepository.java  # Port for job persistence
├── application/
│   ├── CrawlServiceImpl.java     # Implements domain use cases
├── adapter/
│   ├── in/
│   │   ├── web/
│   │   │   ├── CrawlController.java # Controls API routes
│   │   │   ├── dto/
│   │   │       ├── CrawlRequest.java  # Representation of POST request
│   │   │       ├── CrawlResponse.java # Representation of GET response
│   ├── out/
│       ├── crawler/
│       │   ├── WebCrawlerImpl.java    # Search mechanism implementation
│       ├── persistence/
│           ├── InMemoryCrawlRepository.java # In-memory search repository
├── config/
│   ├── SparkConfig.java             # Spark server configuration
├── Main.java                        # Application entry point
└── test/
    ├── domain/
    │   ├── CrawlJobTest.java        # Unit tests for domain model
    ├── application/
    │   ├── CrawlServiceImplTest.java # Unit tests for use cases
    ├── adapter/
    │   ├── in/
    │   │   ├── web/
    │   │       ├── CrawlControllerTest.java # Tests for API controller
    │   ├── out/
    │       ├── crawler/
    │           ├── WebCrawlerImplTest.java # Tests for search mechanism
```

---

## ✨ **Main Features**

1. **POST `/crawl`**:
    - Starts a new search with the provided term.
    - **Request**:
```json
{
  "keyword": "security"
}
```

    - **Response**:
    ```json
{
"id": "abc12345"
}
```

2. **GET `/crawl/:id`**:
    - Returns the search status and URLs found so far.
    - **Response**:
```json
{
  "id": "abc12345",
  "status": "active",
  "urls": [
    "https://man7.org/linux/man-pages/man7/security.7.html",
    "https://man7.org/linux/man-pages/"
  ]
}
```

3. Support for **multiple simultaneous searches**.
4. Return of **partial results** during search execution.
5. **Parallel execution** controlled by environment variable (`MULTITHREAD_OPT`).

---

## 🔧 **Technologies Used**

- **Java 14**: Main language for implementation.
- **Maven**: Dependency and build manager.
- **Spark Java**: Framework for HTTP API.
- **Gson**: Library for JSON serialization/deserialization.
- **JUnit 5**: Framework for unit testing.
- **Mockito**: Mocking library to simulate dependencies in tests.

---

## ⚙️ **Environment Variables**

- `BASE_URL`: Defines the base URL where searches will be performed.
    - Default value: `https://man7.org/linux/man-pages/`
- `MULTITHREAD_OPT`: Defines whether the search will be performed in parallel.
    - Accepted values: `"true"` or `"false"`
    - Default value: `"false"`

---

## ✅ **Implemented Tests**

Tests cover all layers of the application:

1. **Domain (`CrawlJobTest`)**:
    - Validation of job creation and manipulation (e.g., adding found URLs).
2. **Application (`CrawlServiceImplTest`)**:
    - Tests use cases for starting searches and querying existing jobs.
3. **Input Adapters (API, `CrawlControllerTest`)**:
    - Validates expected behavior of POST `/crawl` and GET `/crawl/:id` endpoints.
4. **Output Adapters (`InMemoryCrawlRepositoryTest`)**:
    - Validates in-memory repository, ensuring correct persistence.
5. **WebCrawler (`WebCrawlerImplTest`)**:
    - Tests URL resolution logic and link extraction.

---

## 📊 **Performance Evaluation**

- Optimized parallel search using `ExecutorService`.
- Visited URLs are stored in a thread-safe structure (`ConcurrentHashMap` or `synchronizedSet`).
- Limit of 100 results per search, as specified in the test.

---

## 📝 **Notes**

1. The files `pom.xml` and `Dockerfile` were **not modified**, as requested in the technical test.
2. The application meets all functional requirements described, including:
    - Validation of `keyword` (minimum 4, maximum 32 characters).
    - Respecting the base URL, following only links within the same domain.
    - Partial result return via API while the search is still in progress.
3. The solution is designed to be testable, scalable, and easy to maintain.

---

💡 **Questions or suggestions? Feel free to reach out! 😊

---

## **Future Updates**

- Implement a file system cache to store search results.
- Fix tests (I didn't have time to fix them all).
- Add more unit tests to cover all edge cases.
- Implement a more robust error handling mechanism.

---