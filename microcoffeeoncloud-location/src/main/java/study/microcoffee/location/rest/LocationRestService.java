package study.microcoffee.location.rest;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.microcoffee.location.exception.CoffeeShopNotFoundException;
import study.microcoffee.location.repository.LocationRepository;

/**
 * Class implementing the Location REST service for finding the geographical location of objects.
 * <p>
 * The implementation supports CORS (Cross-Origin Resource Sharing).
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/coffeeshop", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class LocationRestService {

    private Logger logger = LoggerFactory.getLogger(LocationRestService.class);

    private LocationRepository locationRepository;

    // Doesn't really need @Autowired when only having a single constructor. Spring sorts it out by itself.
    @Autowired
    public LocationRestService(LocationRepository locationRepository) {
        this.locationRepository = locationRepository;
    }

    /**
     * Finds the nearest coffee shop within maxdistance meters from the position given by the WGS84 latitude/longitude coordinates.
     *
     * @param latitude
     *            the WGS84 latitude of the given position.
     * @param longitude
     *            the WGS84 longitude of the given position.
     * @param maxDistance
     *            the maximum distance in meters from the given position to the position of the nearest coffee shop.
     * @return The nearest coffee shop formatted as JSON data.
     * @throws CoffeeShopNotFoundException
     *             if no coffee shop was found within the maximum distance allowed.
     */
    @GetMapping(path = "/nearest/{latitude}/{longitude}/{maxdistance}")
    public Object getNearestCoffeeShop(@PathVariable("latitude") double latitude, @PathVariable("longitude") double longitude,
        @PathVariable("maxdistance") long maxDistance) {

        logger.debug("GET /nearest/{}/{}/{}", latitude, longitude, maxDistance);

        Object coffeeShop = locationRepository.findNearestCoffeeShop(latitude, longitude, maxDistance);
        if (coffeeShop == null) {
            throw new CoffeeShopNotFoundException(
                String.format("No coffee shop found within a distance of %d meters from position lat/long=%f/%f", maxDistance,
                    latitude, longitude));
        }

        return coffeeShop;
    }

    @ExceptionHandler(CoffeeShopNotFoundException.class)
    void handleCoffeeShopNotFoundException(CoffeeShopNotFoundException e, HttpServletResponse response) throws IOException {
        response.sendError(HttpStatus.NO_CONTENT.value(), e.getMessage());
    }
}
