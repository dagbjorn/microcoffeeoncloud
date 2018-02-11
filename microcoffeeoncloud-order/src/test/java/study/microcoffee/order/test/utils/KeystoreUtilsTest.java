package study.microcoffee.order.test.utils;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;

/**
 * Unit tests of {@link KeystoreUtils}.
 */
public class KeystoreUtilsTest {

    @After
    public void tearDown() {
        System.clearProperty(KeystoreUtils.SYSTEM_PROPERTY_TRUSTSTORE);
        System.clearProperty(KeystoreUtils.SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD);
    }

    @Test
    public void configureTruststoreShouldSetSystemProperties() throws Exception {
        KeystoreUtils.configureTruststore("microcoffee-keystore.jks", "12345678");

        assertThat(System.getProperty(KeystoreUtils.SYSTEM_PROPERTY_TRUSTSTORE)).contains("cacerts");
        assertThat(System.getProperty(KeystoreUtils.SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD)).isEqualTo("12345678");
    }
}
