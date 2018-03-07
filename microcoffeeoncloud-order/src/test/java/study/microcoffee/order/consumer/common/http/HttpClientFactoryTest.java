package study.microcoffee.order.consumer.common.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.apache.http.impl.client.CloseableHttpClient;
import org.junit.Test;

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
}
