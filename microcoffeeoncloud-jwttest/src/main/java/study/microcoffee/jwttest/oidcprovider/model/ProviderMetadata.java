package study.microcoffee.jwttest.oidcprovider.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Class defining the JSON object that contains the metadata returned by an OIDC Provider's WellKnown API.
 * <p>
 * Only a subset of the returned metadata is mapped.
 *
 * @see https://openid.net/specs/openid-connect-discovery-1_0.html#ProviderMetadata
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProviderMetadata {

    private String issuer;

    @JsonProperty("jwks_uri")
    private String jwksUri;

    @JsonProperty("subject_types_supported")
    private String[] subjectTypesSupported;

    @JsonProperty("authorization_endpoint")
    private String authorizationEndpoint;

    @JsonProperty("token_endpoint")
    private String tokenEndpoint;
}
