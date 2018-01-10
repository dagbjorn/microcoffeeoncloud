package study.microcoffee.order.rest.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import study.microcoffee.order.repository.MenuRepository;

/**
 * Class implementing the Menu REST service for returning the coffee shop menu.
 * <p>
 * The implementation supports CORS (Cross-Origin Resource Sharing).
 */
@CrossOrigin
@RestController
@RequestMapping(path = "/coffeeshop", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
public class MenuRestService {

    private Logger logger = LoggerFactory.getLogger(MenuRestService.class);

    @Autowired
    private MenuRepository menuRepository;

    /**
     * Returns the menu of the coffee shop.
     * <p>
     * On success, HTTP status 200 (OK) is returned.
     *
     * @return The JSON formatted menu of the coffee shop.
     */
    @GetMapping(path = "/menu")
    public Object getCoffeeMenu() {
        logger.debug("GET /menu");

        return menuRepository.getCoffeeMenu();
    }
}
