package study.microcoffee.gui.common.discovery;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.stereotype.Component;

/**
 * Implementation of a cacheable resolver of discovered services. If several service instances are available, one of them is
 * selected randomly.
 */
@Component
public class CacheableDiscoveredServiceResolver implements DiscoveredServiceResolver {

    private final Logger logger = LoggerFactory.getLogger(CacheableDiscoveredServiceResolver.class);

    private DiscoveryClient discoveryClient;

    public CacheableDiscoveredServiceResolver(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;
    }

    @Override
    @Cacheable("serviceUrls")
    public String resolveServiceUrl(String serviceId) {
        List<ServiceInstance> serviceInstances = discoveryClient.getInstances(serviceId);
        if (serviceInstances.isEmpty()) {
            return null;
        }

        int instanceNumber = ThreadLocalRandom.current().nextInt(serviceInstances.size());
        String serviceUrl = serviceInstances.get(instanceNumber).getUri().toString();

        logger.debug("Resolved URL of service ID: {}={}", serviceId, serviceUrl);

        return serviceUrl;
    }
}
