package study.microcoffee.order.consumer.creditrating;

/**
 * Holder object of a credit rating.
 */
public class CreditRating {

    private int creditRating;

    // Needed by Jackson ObjectMapper.
    public CreditRating() {
    }

    public CreditRating(int creditRating) {
        this.creditRating = creditRating;
    }

    public int getCreditRating() {
        return creditRating;
    }

    @Override
    public String toString() {
        return String.format("CreditRating [creditRating=%d]", creditRating);
    }
}
