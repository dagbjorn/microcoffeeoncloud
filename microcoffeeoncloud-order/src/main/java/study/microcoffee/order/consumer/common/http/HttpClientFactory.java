package study.microcoffee.order.consumer.common.http;

import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

/**
 * Factory class for creating HTTP clients.
 */
public class HttpClientFactory {

    /**
     * Creates a HTTP client that has turned hostname verification off.
     * <p>
     * In addition, the following timeout values are set using the value of the timeout parameter:
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
    public static CloseableHttpClient createTrustAnyHostnameClient(int timeout) {
        RequestConfig requestConfig = RequestConfig.custom() //
            .setConnectTimeout(timeout * 1000) //
            .setSocketTimeout(timeout * 1000) //
            .setConnectionRequestTimeout(timeout * 1000) //
            .build();

        CloseableHttpClient httpClient = HttpClients.custom() //
            // .setSSLHostnameVerifier(new NoopHostnameVerifier()) // Not needed with current wildcard certificate.
            .setDefaultRequestConfig(requestConfig) //
            .build();

        return httpClient;
    }
}
