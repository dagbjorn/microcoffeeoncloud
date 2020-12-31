package study.microcoffee.order.consumer.common.http;

import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.util.ssl.SslContextFactory;

/**
 * Factory class for creating Jetty HTTP clients.
 */
public class JettyHttpClientFactory {

    private JettyHttpClientFactory() {
    }

    /**
     * Creates a HTTP client with a configurable timeout.
     * <p>
     * The following timeout values are set using the value of the timeout parameter:
     * <ul>
     * <li>ConnectTimeout: The maximumtime to establish the connection with the remote host.</li>
     * <li>IdleTimeout: The maximum time a connection can be idle.</li>
     * <li>AddressResolutionTimeout: The maximum time to wait for a socket address resolution.</li>
     * </ul>
     *
     * @param timeout
     *            the request timeout in number of seconds.
     * @return The HTTP client.
     */
    public static HttpClient createDefaultClient(int timeout) {
        SslContextFactory.Client sslContextFactory = new SslContextFactory.Client();

        HttpClient httpClient = new HttpClient(sslContextFactory);
        httpClient.setConnectTimeout(timeout * 1000L);
        httpClient.setIdleTimeout(timeout * 1000L);
        httpClient.setAddressResolutionTimeout(timeout * 1000L);

        return httpClient;
    }
}
