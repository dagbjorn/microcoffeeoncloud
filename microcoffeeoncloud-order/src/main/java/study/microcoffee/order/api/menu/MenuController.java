package study.microcoffee.order.api.menu;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.tags.Tag;
import study.microcoffee.order.SwaggerConfig;
import study.microcoffee.order.repository.MenuRepository;

/**
 * Controller class of the Menu REST API for returning the coffee shop menu.
 */
@CrossOrigin( //
    origins = { //
        "http://localhost:3000", // Needed for local React dev on port 3000.
        "https://localhost:8443" // Needed for Swagger from gateway (devlocal).
    })
@RestController
@RequestMapping(path = "/api/coffeeshop", produces = MediaType.APPLICATION_JSON_VALUE)
@Tag(name = SwaggerConfig.MENU_TAG, description = SwaggerConfig.MENU_DESCRIPTION)
public class MenuController {

    private Logger logger = LoggerFactory.getLogger(MenuController.class);

    private MenuRepository menuRepository;

    public MenuController(MenuRepository menuRepository) {
        this.menuRepository = menuRepository;
    }

    /**
     * Returns the menu of the coffee shop.
     * <p>
     * On success, HTTP status 200 (OK) is returned.
     *
     * @return The JSON formatted menu of the coffee shop.
     */
    @GetMapping(path = "/menu")
    @GetCoffeeMenuSwagger
    public Object getCoffeeMenu() {
        logger.debug("GET /menu");

        return menuRepository.getCoffeeMenu();
    }
}
