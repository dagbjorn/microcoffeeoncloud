package study.microcoffee.gui.common.logging;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

/**
 * Application event listener what logs all discovered services.
 */
@Component
public class DiscoveredServiceLogger implements ApplicationListener<ContextRefreshedEvent> {

    private final Logger logger = LoggerFactory.getLogger(DiscoveredServiceLogger.class);

    @Autowired
    private DiscoveryClient discoveryClient;

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        List<String> services = discoveryClient.getServices();
        if (services.isEmpty()) {
            logger.info("DISCOVERED SERVICES: none");
            return;
        }

        services.forEach((String serviceName) -> {
            discoveryClient.getInstances(serviceName).forEach((ServiceInstance s) -> {
                logger.info("DISCOVERED SERVICE: serviceId={}, uri={}, host={}, port={}, metadata={}", s.getServiceId(), s.getUri(),
                    s.getHost(), s.getPort(), s.getMetadata());
            });
        });
    }
}
