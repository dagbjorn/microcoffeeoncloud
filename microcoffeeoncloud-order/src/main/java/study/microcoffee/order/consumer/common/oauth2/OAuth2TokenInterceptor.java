package study.microcoffee.order.consumer.common.oauth2;

import java.io.IOException;

import org.springframework.http.HttpRequest;
import org.springframework.http.client.ClientHttpRequestExecution;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.security.oauth2.client.OAuth2AuthorizeRequest;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClient;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.core.OAuth2AccessToken;

import lombok.extern.slf4j.Slf4j;

/**
 * Spring ClientHttpRequestInterceptor that gets an access token from the OAuth2 provider and adds the token in an Authorization
 * header.
 */
@Slf4j
public class OAuth2TokenInterceptor implements ClientHttpRequestInterceptor {

    private OAuth2AuthorizedClientManager authorizedClientManager;

    private ClientRegistration clientRegistration;

    public OAuth2TokenInterceptor(OAuth2AuthorizedClientManager authorizedClientManager, ClientRegistration clientRegistration) {
        this.authorizedClientManager = authorizedClientManager;
        this.clientRegistration = clientRegistration;
    }

    @Override
    public ClientHttpResponse intercept(HttpRequest request, byte[] body, ClientHttpRequestExecution execution) throws IOException {
        OAuth2AuthorizeRequest authorizeRequest = OAuth2AuthorizeRequest //
            .withClientRegistrationId(clientRegistration.getRegistrationId()) //
            .principal("order-service") //
            .build();

        OAuth2AuthorizedClient authorizedClient = authorizedClientManager.authorize(authorizeRequest);
        if (authorizedClient == null) {
            throw new IllegalStateException("Failed to get access token for '" + clientRegistration.getRegistrationId()
                + "', returned authorizedClient is null");
        }

        log.debug(oAuth2AccessTokenAsString(authorizedClient.getAccessToken()));

        request.getHeaders().setBearerAuth(authorizedClient.getAccessToken().getTokenValue());

        return execution.execute(request, body);
    }

    private String oAuth2AccessTokenAsString(OAuth2AccessToken accessToken) {
        StringBuilder builder = new StringBuilder();
        builder.append("OAuth2AccessToken { ");
        builder.append("tokenType=" + accessToken.getTokenType().getValue());
        builder.append(", scopes=" + accessToken.getScopes());
        builder.append(", issuedAt=" + accessToken.getIssuedAt());
        builder.append(", expiredAt=" + accessToken.getExpiresAt());
        builder.append(", tokenValue=" + accessToken.getTokenValue());
        builder.append(" }");
        return builder.toString();
    }
}
