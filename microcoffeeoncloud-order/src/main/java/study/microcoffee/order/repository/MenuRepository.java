package study.microcoffee.order.repository;

/**
 * Interface to the Menu repository for reading the coffee menu of the coffee shop.
 */
public interface MenuRepository {

    /**
     * Returns the menu of the coffee shop.
     *
     * @return The JSON formatted menu of the coffee shop.
     */
    Object getCoffeeMenu();
}
