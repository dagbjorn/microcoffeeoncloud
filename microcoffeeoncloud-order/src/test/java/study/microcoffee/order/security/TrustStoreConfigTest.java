package study.microcoffee.order.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;

/**
 * Unit tests of {@link TrustStoreConfig}.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
@TestPropertySource("/application-test.properties")
public class TrustStoreConfigTest {

    @Autowired
    private TrustStoreConfig trustStoreConfig;

    @After
    public void tearDown() {
        System.clearProperty(TrustStoreConfig.SYSTEM_PROPERTY_TRUSTSTORE);
        System.clearProperty(TrustStoreConfig.SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD);
    }

    @Test
    public void initTrustStoreSystemPropertiesShouldSetSystemProperties() throws Exception {
        trustStoreConfig.initTrustStoreSystemProperties();

        assertThat(System.getProperty(TrustStoreConfig.SYSTEM_PROPERTY_TRUSTSTORE)).isNotNull();
        assertThat(System.getProperty(TrustStoreConfig.SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD)).isNotNull();
    }

    @Configuration
    @Import({ TrustStoreConfig.class })
    static class Config {
    }
}
