package study.microcoffee.jwttest;

import java.security.interfaces.RSAPublicKey;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.Date;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator.Builder;
import com.auth0.jwt.algorithms.Algorithm;

import study.microcoffee.jwttest.keyprovider.KeyStoreConfig;
import study.microcoffee.jwttest.keyprovider.KeyStoreKeyProvider;

/**
 * Factory class to create miscellaneous test tokens.
 */
public class TestTokens {

    public static final String KEY_STORE_NAME = "microcoffee-keystore.jks";
    public static final String KEY_STORE_PASSWORD = "12345678";  // NOSONAR Allow for tests
    public static final String KEY_STORE_TYPE = "JKS";
    public static final String KEY_ALIAS = "localhost";
    public static final String KEY_PASSWORD = "12345678";   // NOSONAR Allow for tests
    public static final String KEY_TYPE = "RSA";

    // Default values in tokens
    public static final String DEFAULT_ACCESSTOKEN_ISS = "http://localhost:9999";

    private static KeyStoreKeyProvider rsaKeyProvider;

    static {
        KeyStoreConfig keyStoreConfig = new KeyStoreConfig();
        keyStoreConfig.setKeyStoreName(TestTokens.KEY_STORE_NAME);
        keyStoreConfig.setKeyStorePassword(TestTokens.KEY_STORE_PASSWORD);
        keyStoreConfig.setKeyStoreType(KEY_STORE_TYPE);
        keyStoreConfig.setKeyAlias(TestTokens.KEY_ALIAS);
        keyStoreConfig.setKeyPassword(TestTokens.KEY_PASSWORD);

        rsaKeyProvider = new KeyStoreKeyProvider(keyStoreConfig);
    }

    private TestTokens() {
    }

    public static class Access {

        private Access() {
        }

        /**
         * Returns a valid access token.
         */
        public static String valid() {
            return defaultToken() //
                .sign(algorithm());
        }

        /**
         * Returns an expired access token.
         */
        public static String expired() {
            return defaultToken() //
                .withExpiresAt(Timestamp.valueOf(LocalDateTime.now().minusMinutes(1))) //
                .sign(algorithm());
        }

        /**
         * Returns an access token signed with an unsupported algorithm (HMAC).
         */
        public static String invalidKeyType() {
            return defaultToken() //
                .sign(Algorithm.HMAC256("secretkey"));
        }

        /**
         * Returns an unsigned access token.
         */
        public static String unsigned() {
            String token = defaultToken() //
                .sign(algorithm());

            return token.substring(0, token.lastIndexOf('.'));
        }

        /**
         * Returns an access token with invalid signature.
         */
        public static String invalidSignature() {
            String token = defaultToken() //
                .sign(algorithm());

            return token.substring(0, token.lastIndexOf('.')) + ".invalid";
        }

        /**
         * Returns a builder of a minimum access token that only contain iat and exp claims.
         */
        public static Builder basic() {
            return JWT.create() //
                .withIssuedAt(new Date()) //
                .withExpiresAt(Timestamp.valueOf(LocalDateTime.now().plusHours(1)));
        }

        /**
         * Returns a builder of an access token that contains all mandatory claims.
         */
        public static Builder custom() {
            return defaultToken();
        }

        /**
         * Returns the Algorithm instance for signing custom basic or custom access tokens.
         */
        public static Algorithm algorithm() {
            return Algorithm.RSA256(rsaKeyProvider);
        }

        private static Builder defaultToken() {
            return basic() //
                .withIssuer(DEFAULT_ACCESSTOKEN_ISS);
        }
    }

    public static class PublicKey {

        private PublicKey() {
        }

        /**
         * Returns the RSA public key used for verification of test tokens.
         */
        public static RSAPublicKey getRsaKey() {
            return rsaKeyProvider.getPublicKeyById(null);
        }

        /**
         * Returns the modulus of the RSA public key used for verification of test tokens.
         */
        public static byte[] getModulus() {
            return rsaKeyProvider.getPublicKeyById(null).getModulus().toByteArray();
        }

        /**
         * Returns the URL Base64 encoded modulus of the RSA public key used for verification of test tokens.
         */
        public static String getModulusAsBase64() {
            return Base64.getUrlEncoder().encodeToString(getModulus());
        }

        /**
         * Returns the exponent of the RSA public key used for verification of test tokens.
         */
        public static byte[] getExponent() {
            return rsaKeyProvider.getPublicKeyById(null).getPublicExponent().toByteArray();
        }

        /**
         * Returns the URL Base64 encoded exponent of the RSA public key used for verification of test tokens.
         */
        public static String getExponentAsBase64() {
            return Base64.getUrlEncoder().encodeToString(getExponent());
        }
    }
}
