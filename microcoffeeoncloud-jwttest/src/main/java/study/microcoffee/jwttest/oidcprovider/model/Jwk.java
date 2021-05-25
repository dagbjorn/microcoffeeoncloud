package study.microcoffee.jwttest.oidcprovider.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Class defining the JSON object that contains a JSON Web Key (public key) returned by an OIDC Provider's JWKS API.
 *
 * @see https://tools.ietf.org/html/rfc7517
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class Jwk {

    private String kty;

    private String use;

    private String alg;

    private String kid;

    private String n;

    private String e;

    private List<String> x5c;
}
