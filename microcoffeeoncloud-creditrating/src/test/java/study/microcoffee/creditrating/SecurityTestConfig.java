package study.microcoffee.creditrating;

import static org.mockito.Mockito.mock;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.jwt.JwtDecoder;

/**
 * Test configuration class for Spring Security OAuth 2.0. This test configuration overrides the default Spring Security settings
 * and mocks the beans so OIDC discovery doesn’t happen.
 * <p>
 * Credits to <a href=
 * "https://developer.okta.com/blog/2019/04/15/testing-spring-security-oauth-with-junit#how-to-handle-oidc-discovery-in-spring-boot-integration-tests">Upgrading
 * Spring Security OAuth and JUnit Tests through the 👀 of a Java Hipster</a> for sharing this code.
 */
@TestConfiguration
public class SecurityTestConfig {

    /**
     * Registers a dummy client to avoid OIDC discovery during unit testing of an OAuth2 client.
     */
    @Bean
    ClientRegistrationRepository clientRegistrationRepository() {
        return new InMemoryClientRegistrationRepository(clientRegistration());
    }

    private ClientRegistration clientRegistration() {
        Map<String, Object> metadata = new HashMap<>();
        metadata.put("issuer", "https://mock.org");

        return ClientRegistration.withRegistrationId("mockclient") //
            .redirectUri("{baseUrl}/{action}/oauth2/code/{registrationId}") // The default redirect URI template
            .clientAuthenticationMethod(ClientAuthenticationMethod.BASIC) //
            .authorizationGrantType(AuthorizationGrantType.CLIENT_CREDENTIALS) //
            .scope("creditrating") //
            .authorizationUri("https://mock.org/login/oauth/authorize") //
            .tokenUri("https://mock.org/login/oauth/access_token") //
            .jwkSetUri("https://mock.org/oauth/jwk") //
            .userInfoUri("https://api.mock.org/user") //
            .providerConfigurationMetadata(metadata) //
            .userNameAttributeName("id") //
            .clientName("Client Name") //
            .clientId("client-id") //
            .clientSecret("client-secret") //
            .build();
    }

    /**
     * Creates a mock implementation to avoid OIDC discovery during unit testing of an OAuth2 resource server.
     */
    @Bean
    JwtDecoder jwtDecoder() {
        return mock(JwtDecoder.class);
    }
}
