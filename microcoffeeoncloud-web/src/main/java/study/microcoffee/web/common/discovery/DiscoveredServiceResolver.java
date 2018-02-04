package study.microcoffee.web.common.discovery;

/**
 * Interface to a resolver of discovered services.
 */
public interface DiscoveredServiceResolver {

    /**
     * Resolves the URL of a discovered service by its service ID.
     *
     * @param serviceId
     *            the service ID.
     * @return The URL of the discovered service; null if no such service has been discovered.
     */
    String resolveServiceUrl(String serviceId);
}
