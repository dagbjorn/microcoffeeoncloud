package study.microcoffee.jwttest.oidcprovider.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * Class defining the JSON object that contains a JSON Web Key Set (set of public keys) returned by an OIDC Provider's JWKS API.
 *
 * @see https://tools.ietf.org/html/rfc7517
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@ToString
@JsonIgnoreProperties(ignoreUnknown = true)
public class JwkSet {

    private List<Jwk> keys;

    /**
     * Returns the JWK object of the specified RSA kid in the JWK set; null if no such kid is found of key type RSA.
     *
     * @param kid
     *            the key identifier.
     * @return The JWK object of specified kid; otherwise null.
     */
    public Jwk findJwkByKid(String kid) {
        return keys.stream() //
            .filter(key -> key.getKty().equals("RSA") && key.getKid().equals(kid)) //
            .findFirst() //
            .orElse(null);
    }
}
