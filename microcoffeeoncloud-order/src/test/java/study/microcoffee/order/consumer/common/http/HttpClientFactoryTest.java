package study.microcoffee.order.consumer.common.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link HttpClientFactory}.
 */
public class HttpClientFactoryTest {

    @Test
    public void createDefaultClientShouldCreateHttpClient() {
        CloseableHttpClient httpClient = HttpClientFactory.createDefaultClient(-1);

        assertThat(httpClient).isNotNull();
    }

    @Test
    public void createTrustAnyHostnameClientShouldCreateHttpClient() {
        CloseableHttpClient httpClient = HttpClientFactory.createTrustAnyHostnameClient(60);

        assertThat(httpClient).isNotNull();
    }

    @Test
    public void createAsyncClientShouldCreateHttpClient() {
        CloseableHttpAsyncClient httpClient = HttpClientFactory.createAsyncClient(30);

        assertThat(httpClient).isNotNull();
    }
}
