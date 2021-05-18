package study.microcoffee.order;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.AuthorizedClientServiceOAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientManager;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProvider;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientProviderBuilder;
import org.springframework.security.oauth2.client.OAuth2AuthorizedClientService;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;

/**
 * Configuration class of OAuth2.
 */
@Configuration
public class OAuth2Config {

    /**
     * The OAuth2AuthorizedClientManager is responsible for managing the authorization (or re-authorization) of an OAuth 2.0 Client,
     * in collaboration with one or more OAuth2AuthorizedClientProvider(s).
     * <p>
     * The bean is using an AuthorizedClientServiceOAuth2AuthorizedClientManager which is designed to be used outside of a
     * HttpServletRequest context. (The DefaultOAuth2AuthorizedClientManager is designed to be used within the context of a
     * HttpServletRequest.)
     * <p>
     * A service application is a common use case for when to use an AuthorizedClientServiceOAuth2AuthorizedClientManager. Service
     * applications often run in the background, without any user interaction, and typically run under a system-level account
     * instead of a user account. An OAuth 2.0 Client configured with the client_credentials grant type can be considered a type of
     * service application.
     */
    @Bean
    public OAuth2AuthorizedClientManager authorizedClientManager(ClientRegistrationRepository clientRegistrationRepository,
        OAuth2AuthorizedClientService authorizedClientService) {

        OAuth2AuthorizedClientProvider authorizedClientProvider = OAuth2AuthorizedClientProviderBuilder.builder() //
            .clientCredentials() //
            .build();

        AuthorizedClientServiceOAuth2AuthorizedClientManager authorizedClientManager = new AuthorizedClientServiceOAuth2AuthorizedClientManager(
            clientRegistrationRepository, authorizedClientService);
        authorizedClientManager.setAuthorizedClientProvider(authorizedClientProvider);

        return authorizedClientManager;
    }
}
