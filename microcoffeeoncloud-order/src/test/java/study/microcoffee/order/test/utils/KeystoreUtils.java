package study.microcoffee.order.test.utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class for configuring Java keystores.
 * <p>
 * Note: This class is currently not in use since the application certificates are imported to the JDK cacerts in the Docker image.
 */
public abstract class KeystoreUtils {

    public static final String SYSTEM_PROPERTY_TRUSTSTORE = "javax.net.ssl.trustStore";
    public static final String SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";

    public static final String DEFAULT_TRUSTSTORE_FILENAME = "microcoffee-keystore.jks";
    public static final String DEFAULT_TRUSTSTORE_PASSWORD = "12345678";

    private static final Logger logger = LoggerFactory.getLogger(KeystoreUtils.class);

    /**
     * Configures the default truststore.
     *
     * @see KeystoreUtils#configureTruststore(String trustStoreFileName, String trustStorePassword)
     */
    public static void configureTruststore() throws IOException {
        configureTruststore(DEFAULT_TRUSTSTORE_FILENAME, DEFAULT_TRUSTSTORE_PASSWORD);
    }

    /**
     * Configures the given truststore file name as the truststore to use by setting the standard system properties
     * <code>javax.net.ssl.trustStore</code> and <code>javax.net.ssl.trustStorePassword</code>.
     * <p>
     * The truststore file must be available on the classpath. The truststore file may also be located in a jar file (on the
     * classpath). In order to support this, the contents of the truststore file is read and stored in a tempfile. The name of the
     * tempfile is prefixed by "cacerts".
     *
     * @param trustStoreFileName
     *            the name of the truststore file.
     * @param trustStorePassword
     *            the password of the truststore.
     * @throws IOException
     *             if an I/O error occurs.
     */
    public static void configureTruststore(String trustStoreFileName, String trustStorePassword) throws IOException {
        URL input = KeystoreUtils.class.getClassLoader().getResource(trustStoreFileName);
        File tempTrustStoreFile = TempFileUtils.putInTempFile(input, "cacerts");

        System.setProperty(SYSTEM_PROPERTY_TRUSTSTORE, tempTrustStoreFile.getAbsolutePath());
        System.setProperty(SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD, trustStorePassword);

        logger.info("Setting {} to {}", SYSTEM_PROPERTY_TRUSTSTORE, tempTrustStoreFile.getAbsolutePath());
    }

    /**
     * Clears the truststore system properties.
     */
    public static void clearTruststore() {
        System.clearProperty(SYSTEM_PROPERTY_TRUSTSTORE);
        System.clearProperty(SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD);
    }
}
