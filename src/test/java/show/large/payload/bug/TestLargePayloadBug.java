package show.large.payload.bug;

import com.intuit.karate.junit5.Karate;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TestLargePayloadBug {
    static final String CANNED_RESPONSE = "{\"message\": \"OK\"}";

    static HttpServer server;
    static int port;
    static String uri;

    /**
     * Fire up a vanilla JDK11 server
     * @throws IOException
     */
    @BeforeAll
    static void beforeAll() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(HTTP_OK, CANNED_RESPONSE.length());
            try (var responseBody = exchange.getResponseBody()) {
                responseBody.write(CANNED_RESPONSE.getBytes(UTF_8));
            }
        });
        server.start();

        port = server.getAddress().getPort();
        uri = "http://localhost:" + port + "/";
    }

    /**
     * Tear down the server
     */
    @AfterAll
    static void afterAll() {
        server.stop(0);
    }

    /**
     * Ensure the server is up and running
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @Test
    void testServerUp() throws IOException, InterruptedException {
        var response = HttpClient.newHttpClient().send(HttpRequest.newBuilder(URI.create(uri))
                .build(), HttpResponse.BodyHandlers.ofString(UTF_8));
        assertEquals(HTTP_OK, response.statusCode());
        assertEquals(CANNED_RESPONSE, response.body());
    }

    /**
     * Run the {@link Karate} feature
     *
     * @return
     */
    @Karate.Test
    Karate TestLargePayload() {
        return Karate.run("large-payload")
                .systemProperty("portNum", Integer.toString(port))
                .systemProperty("uri", uri)
                .relativeTo(getClass());
    }
}
