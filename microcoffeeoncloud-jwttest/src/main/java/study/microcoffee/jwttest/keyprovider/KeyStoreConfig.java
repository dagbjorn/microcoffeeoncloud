package study.microcoffee.jwttest.keyprovider;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Configuration properties of test keystore.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class KeyStoreConfig {

    private String keyStoreName;

    private String keyStorePassword;

    private String keyStoreType;

    private String keyAlias;

    private String keyPassword;
}
