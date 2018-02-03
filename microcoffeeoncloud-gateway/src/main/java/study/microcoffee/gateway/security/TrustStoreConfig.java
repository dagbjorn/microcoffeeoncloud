package study.microcoffee.gateway.security;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import study.microcoffee.gateway.utils.TempFileUtils;

/**
 * Configures the trust store to use by setting the standard system properties.
 *
 * <ul>
 * <li>javax.net.ssl.trustStore</li>
 * <li>javax.net.ssl.trustStorePassword</li>
 * </ul>
 */
@Component
public class TrustStoreConfig {

    public static final String SYSTEM_PROPERTY_TRUSTSTORE = "javax.net.ssl.trustStore";
    public static final String SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";

    private final Logger logger = LoggerFactory.getLogger(TrustStoreConfig.class);

    @Value("${app.ssl.truststore}")
    private String trustStoreFileName;

    @Value("${app.ssl.truststore.password}")
    private String trustStorePassword;

    @PostConstruct
    public void initTrustStoreSystemProperties() throws IOException {
        URL input = TrustStoreConfig.class.getClassLoader().getResource(trustStoreFileName);
        File tempTrustStoreFile = TempFileUtils.putInTempFile(input, "certs");

        System.setProperty(SYSTEM_PROPERTY_TRUSTSTORE, tempTrustStoreFile.getAbsolutePath());
        System.setProperty(SYSTEM_PROPERTY_TRUSTSTORE_PASSWORD, trustStorePassword);

        logger.info("Setting {} to {}", SYSTEM_PROPERTY_TRUSTSTORE, tempTrustStoreFile.getAbsolutePath());
    }
}
