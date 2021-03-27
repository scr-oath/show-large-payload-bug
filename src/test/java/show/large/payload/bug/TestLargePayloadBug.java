package show.large.payload.bug;

import com.intuit.karate.junit5.Karate;
import com.sun.net.httpserver.HttpServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.nio.charset.StandardCharsets.UTF_8;

public class TestLargePayloadBug {
    static final String CANNED_RESPONSE = "{\"message\": \"OK\"}";

    static HttpServer server;
    static int port;
    static String uri;

    /**
     * Fire up a vanilla JDK11 server
     *
     * @throws IOException
     */
    @BeforeAll
    static void beforeAll() throws IOException {
        server = HttpServer.create(new InetSocketAddress("localhost", 0), 0);
        server.createContext("/", exchange -> {
            exchange.sendResponseHeaders(HTTP_OK, CANNED_RESPONSE.length());
            try (OutputStream responseBody = exchange.getResponseBody()) {
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
