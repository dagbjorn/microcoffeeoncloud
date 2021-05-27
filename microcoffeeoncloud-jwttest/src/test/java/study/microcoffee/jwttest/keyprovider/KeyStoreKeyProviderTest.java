package study.microcoffee.jwttest.keyprovider;

import static org.assertj.core.api.Assertions.assertThat;

import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.auth0.jwt.interfaces.RSAKeyProvider;

import study.microcoffee.jwttest.TestTokens;
import study.microcoffee.jwttest.exception.KeyProviderException;

/**
 * Unit tests of {@link KeyStoreKeyProvider}.
 */
class KeyStoreKeyProviderTest {

    private KeyStoreConfig keyStoreConfig;

    @BeforeEach
    void setUp() {
        keyStoreConfig = new KeyStoreConfig();
        keyStoreConfig.setKeyStoreName(TestTokens.KEY_STORE_NAME);
        keyStoreConfig.setKeyStorePassword(TestTokens.KEY_STORE_PASSWORD);
        keyStoreConfig.setKeyStoreType(TestTokens.KEY_STORE_TYPE);
        keyStoreConfig.setKeyAlias(TestTokens.KEY_ALIAS);
        keyStoreConfig.setKeyPassword(TestTokens.KEY_PASSWORD);
    }

    @Test
    void getPublicKeyByIdShouldReturnPublicKey() {
        RSAKeyProvider rsaKeyProvider = new KeyStoreKeyProvider(keyStoreConfig);

        RSAPublicKey publicKey = rsaKeyProvider.getPublicKeyById(null);

        assertThat(publicKey.getAlgorithm()).isEqualTo("RSA");
        assertThat(publicKey.getFormat()).isEqualTo("X.509");
        assertThat(publicKey.toString()).contains("2048 bits");
    }

    @Test
    void getPublicKeyByIdWhenInvalidKeyStoreNameShouldThrowKeyProviderException() {
        keyStoreConfig.setKeyStoreName("nosuchkeystore.jks");

        Assertions.assertThrows(KeyProviderException.class, () -> {
            new KeyStoreKeyProvider(keyStoreConfig);
        });
    }

    @Test
    void getPublicKeyByIdWhenInvalidKeyStorePasswordShouldThrowKeyProviderException() {
        keyStoreConfig.setKeyStorePassword("nosuchpassword");

        Assertions.assertThrows(KeyProviderException.class, () -> {
            new KeyStoreKeyProvider(keyStoreConfig);
        });
    }

    @Test
    void getPrivateKeyShouldReturnPrivateKey() {
        RSAKeyProvider rsaKeyProvider = new KeyStoreKeyProvider(keyStoreConfig);

        RSAPrivateKey privateKey = rsaKeyProvider.getPrivateKey();

        assertThat(privateKey.getAlgorithm()).isEqualTo("RSA");
        assertThat(privateKey.getFormat()).isEqualTo("PKCS#8");
    }

    @Test
    void getPrivateKeyShouldWhenInvalidKeyAliasShouldThrowKeyProviderException() {
        keyStoreConfig.setKeyAlias("nosuchkeyalias");

        Assertions.assertThrows(KeyProviderException.class, () -> {
            new KeyStoreKeyProvider(keyStoreConfig);
        });
    }

    @Test
    void getPrivateKeyShouldWhenInvalidKeyPasswordShouldThrowKeyProviderException() {
        keyStoreConfig.setKeyPassword("nosuchpassword");

        Assertions.assertThrows(KeyProviderException.class, () -> {
            new KeyStoreKeyProvider(keyStoreConfig);
        });
    }

    @Test
    void getPrivateKeyIdShouldReturnKeyId() {
        RSAKeyProvider rsaKeyProvider = new KeyStoreKeyProvider(keyStoreConfig);

        String keyId = rsaKeyProvider.getPrivateKeyId();

        assertThat(keyId).isEqualTo(keyStoreConfig.getKeyAlias());
    }
}
