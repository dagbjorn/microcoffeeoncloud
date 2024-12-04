package study.microcoffee.order.consumer.common.http;

import javax.net.ssl.HostnameVerifier;

import org.apache.hc.client5.http.config.ConnectionConfig;
import org.apache.hc.client5.http.config.RequestConfig;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.ssl.DefaultClientTlsStrategy;
import org.apache.hc.client5.http.ssl.DefaultHostnameVerifier;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.core5.http.io.SocketConfig;
import org.apache.hc.core5.ssl.SSLContexts;
import org.apache.hc.core5.util.Timeout;

/**
 * Factory class for creating HTTP clients.
 */
public class HttpClientFactory {

    private HttpClientFactory() {
    }

    /**
     * Creates a HTTP client with a configurable timeout.
     * <p>
     * The following timeout values are set using the value of the timeout parameter:
     * <ul>
     * <li>ConnectTimeout: The time to establish the connection with the remote host.</li>
     * <li>SoTimeout: The time waiting for data - after the connection was established; maximum time of inactivity between two data
     * packets.</li>
     * <li>ConnectionRequestTimeout: The time to wait for a connection from the connection manager/pool.</li>
     * </ul>
     *
     * @param timeout
     *            the request timeout in number of seconds.
     * @return The HTTP client.
     */
    public static CloseableHttpClient createDefaultClient(int timeout) {
        return createHttpClient(timeout, new DefaultHostnameVerifier());
    }

    /**
     * Creates a HTTP client that has turned hostname verification off. Otherwise, identical to {@link createDefaultClient}.
     *
     * @param timeout
     *            the request timeout in number of seconds.
     * @return The HTTP client.
     */
    public static CloseableHttpClient createTrustAnyHostnameClient(int timeout) {
        return createHttpClient(timeout, new NoopHostnameVerifier());
    }

    private static CloseableHttpClient createHttpClient(int timeout, HostnameVerifier hostnameVerifier) {
        return HttpClients.custom() //
            .setDefaultRequestConfig(RequestConfig.custom() //
                .setConnectionRequestTimeout(Timeout.ofSeconds(timeout)) //
                .build()) //
            .setConnectionManager(PoolingHttpClientConnectionManagerBuilder.create() //
                .setTlsSocketStrategy(new DefaultClientTlsStrategy(SSLContexts.createSystemDefault(), hostnameVerifier)) //
                .setDefaultSocketConfig(SocketConfig.custom() //
                    .setSoTimeout(Timeout.ofSeconds(timeout)) //
                    .build()) //
                .setDefaultConnectionConfig(ConnectionConfig.custom() //
                    .setConnectTimeout(Timeout.ofSeconds(timeout)) //
                    .build()) //
                .build()) //
            .build();
    }
}
