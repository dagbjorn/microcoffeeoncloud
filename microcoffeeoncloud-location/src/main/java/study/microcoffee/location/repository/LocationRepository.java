package study.microcoffee.location.repository;

/**
 * Interface to a Location repository for finding the geographical location of objects.
 */
public interface LocationRepository {

    /**
     * Finds the nearest coffee shop within maxdistance meters from the position given by the WGS84 latitude/longitude coordinates.
     *
     * @param latitude
     *            the WGS84 latitude of the given position.
     * @param longitude
     *            the WGS84 longitude of the given position.
     * @param maxDistance
     *            the maximum distance in meters from the given position to the position of the nearest coffee shop.
     * @return The nearest coffee shop formatted as JSON data; null if no coffee shop was found within the maximum distance allowed.
     */
    Object findNearestCoffeeShop(double latitude, double longitude, long maxDistance);
}
