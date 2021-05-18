package study.microcoffee.jwttest.keyprovider;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyFactory;
import java.security.KeyPair;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.UnrecoverableKeyException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateKeySpec;
import java.security.spec.RSAPublicKeySpec;

import com.auth0.jwt.interfaces.RSAKeyProvider;

import lombok.extern.slf4j.Slf4j;
import study.microcoffee.jwttest.exception.KeyProviderException;

/**
 * RSAKeyProvider implementation that gets keys from a Java keystore. The key provider supports signing of JWT tokens, in addition
 * to verification.
 */
@Slf4j
public class KeyStoreKeyProvider implements RSAKeyProvider {

    private KeyStoreConfig keyStoreConfig;

    private RSAPublicKey publicRsaKey;

    private RSAPrivateKey privateRsaKey;

    public KeyStoreKeyProvider(KeyStoreConfig keyStoreConfig) {
        this.keyStoreConfig = keyStoreConfig;

        try {
            String keyStoreFile = keyStoreConfig.getKeyStoreName();

            KeyStore keyStore = loadKeyStoreFromClasspath(keyStoreFile, keyStoreConfig.getKeyStorePassword());
            KeyPair keyPair = getKeyPair(keyStore, keyStoreConfig.getKeyAlias(), keyStoreConfig.getKeyPassword());

            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            RSAPublicKeySpec publicKeySpec = keyFactory.getKeySpec(keyPair.getPublic(), RSAPublicKeySpec.class);
            RSAPrivateKeySpec privateKeySpec = keyFactory.getKeySpec(keyPair.getPrivate(), RSAPrivateKeySpec.class);

            publicRsaKey = (RSAPublicKey) keyFactory.generatePublic(publicKeySpec);
            privateRsaKey = (RSAPrivateKey) keyFactory.generatePrivate(privateKeySpec);

        } catch (CertificateException | InvalidKeySpecException | IOException | KeyStoreException | NoSuchAlgorithmException
            | UnrecoverableKeyException e) {
            String message = "Error reading RSA keys with key alias '" + keyStoreConfig.getKeyAlias() + "' from keystore '"
                + keyStoreConfig.getKeyStoreName() + "'.";
            log.error(message, e);

            throw new KeyProviderException(message, e);
        }
    }

    private KeyStore loadKeyStoreFromClasspath(String keyStoreName, String password)
        throws CertificateException, IOException, KeyStoreException, NoSuchAlgorithmException {

        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(keyStoreName)) {
            if (input == null) {
                throw new FileNotFoundException("No keystore '" + keyStoreName + "' on classpath.");
            }

            KeyStore keystore = KeyStore.getInstance(keyStoreConfig.getKeyStoreType());
            keystore.load(input, password.toCharArray());

            return keystore;
        }
    }

    private KeyPair getKeyPair(KeyStore keyStore, String keyAlias, String keyPassword)
        throws KeyStoreException, UnrecoverableKeyException, NoSuchAlgorithmException {

        PrivateKey privateKey = (PrivateKey) keyStore.getKey(keyAlias, keyPassword.toCharArray());

        Certificate cert = keyStore.getCertificate(keyAlias);
        if (cert == null) {
            throw new KeyProviderException(
                "No key alias '" + keyStoreConfig.getKeyAlias() + "' in keystore '" + keyStoreConfig.getKeyStoreName() + "'.");
        }

        PublicKey publicKey = cert.getPublicKey();

        return new KeyPair(publicKey, privateKey);
    }

    @Override
    public RSAPublicKey getPublicKeyById(String keyId) {
        return publicRsaKey;
    }

    @Override
    public RSAPrivateKey getPrivateKey() {
        return privateRsaKey;
    }

    /**
     * Simple implementation that only returns the key alias.
     */
    @Override
    public String getPrivateKeyId() {
        return keyStoreConfig.getKeyAlias();
    }
}
