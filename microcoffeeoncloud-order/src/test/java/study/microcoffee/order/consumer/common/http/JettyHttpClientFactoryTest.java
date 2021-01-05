package study.microcoffee.order.consumer.common.http;

import static org.assertj.core.api.Assertions.assertThat;

import org.eclipse.jetty.client.HttpClient;
import org.junit.jupiter.api.Test;

import study.microcoffee.order.common.logging.JettyHttpClientLogEnhancer;

/**
 * Unit tests of {@link JettyHttpClientFactory}.
 */
public class JettyHttpClientFactoryTest {

    @Test
    public void createDefaultClientShouldCreateHttpClient() {
        HttpClient httpClient = JettyHttpClientFactory.createDefaultClient(30, new JettyHttpClientLogEnhancer(false));

        assertThat(httpClient).isNotNull();
    }
}
