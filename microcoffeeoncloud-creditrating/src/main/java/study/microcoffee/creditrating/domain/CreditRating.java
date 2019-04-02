package study.microcoffee.creditrating.domain;

/**
 * Holder object of a credit rating.
 */
public class CreditRating {

    private int rating;

    // Needed by Jackson ObjectMapper.
    public CreditRating() {
    }

    public CreditRating(int rating) {
        this.rating = rating;
    }

    public int getRating() {
        return rating;
    }

    @Override
    public String toString() {
        return String.format("CreditRating [rating=%d]", rating);
    }
}
