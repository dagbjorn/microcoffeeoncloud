package study.microcoffee.location.exception;

public class CoffeeShopNotFoundException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public CoffeeShopNotFoundException() {
        super();
    }

    public CoffeeShopNotFoundException(String message) {
        super(message);
    }
}
