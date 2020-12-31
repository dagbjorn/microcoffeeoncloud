package study.microcoffee.order.consumer.common.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClients;

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
     * <li>SocketTimeout: The time waiting for data - after the connection was established; maximum time of inactivity between two
     * data packets.</li>
     * <li>ConnectionRequestTimeout: The time to wait for a connection from the connection manager/pool.</li>
     * </ul>
     *
     * @param timeout
     *            the request timeout in number of seconds.
     * @return The HTTP client.
     */
    public static CloseableHttpClient createDefaultClient(int timeout) {
        return HttpClients.custom() //
            .setDefaultRequestConfig(createRequestConfig(timeout)) //
            .setSSLHostnameVerifier(new DefaultHostnameVerifier()) //
            .build();
    }

    /**
     * Creates a HTTP client that has turned hostname verification off. Otherwise, identical to {@link createDefaultClient}.
     *
     * @param timeout
     *            the request timeout in number of seconds.
     * @return The HTTP client.
     */
    public static CloseableHttpClient createTrustAnyHostnameClient(int timeout) {
        return HttpClients.custom() //
            .setDefaultRequestConfig(createRequestConfig(timeout)) //
            .setSSLHostnameVerifier(new NoopHostnameVerifier()) //
            .build();
    }

    /**
     * Creates a HTTP async client with a configurable timeout.
     * <p>
     * The following timeout values are set using the value of the timeout parameter:
     * <ul>
     * <li>ConnectTimeout: The time to establish the connection with the remote host.</li>
     * <li>SocketTimeout: The time waiting for data - after the connection was established; maximum time of inactivity between two
     * data packets.</li>
     * <li>ConnectionRequestTimeout: The time to wait for a connection from the connection manager/pool.</li>
     * </ul>
     *
     * @param timeout
     *            the request timeout in number of seconds.
     * @return The HTTP async client.
     */
    public static CloseableHttpAsyncClient createAsyncClient(int timeout) {
        return HttpAsyncClients.custom() //
            .setDefaultRequestConfig(createRequestConfig(timeout)) //
            .setSSLHostnameVerifier(new DefaultHostnameVerifier()) //
            .build();
    }

    private static RequestConfig createRequestConfig(int timeout) {
        return RequestConfig.custom() //
            .setConnectTimeout(timeout * 1000) //
            .setSocketTimeout(timeout * 1000) //
            .setConnectionRequestTimeout(timeout * 1000) //
            .build();
    }
}
