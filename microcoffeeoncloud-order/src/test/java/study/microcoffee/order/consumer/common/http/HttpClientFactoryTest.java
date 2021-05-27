package study.microcoffee.order.consumer.common.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link HttpClientFactory}.
 */
class HttpClientFactoryTest {

    @Test
    void createDefaultClientShouldCreateHttpClient() {
        CloseableHttpClient httpClient = HttpClientFactory.createDefaultClient(-1);

        assertThat(httpClient).isNotNull();
    }

    @Test
    void createTrustAnyHostnameClientShouldCreateHttpClient() {
        CloseableHttpClient httpClient = HttpClientFactory.createTrustAnyHostnameClient(60);

        assertThat(httpClient).isNotNull();
    }
}
