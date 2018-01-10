package study.microcoffee.order.consumer.creditrating;

/**
 * Interface to the CreditRating service.
 */
public interface CreditRatingConsumer {

    /**
     * Returns the credit rating of a customer. The credit rating is a number between 0 and 100 where 100 is the best.
     *
     * @param customerId
     *            the customer ID.
     * @return The credit rating between 0 and 100.
     */
    int getCreditRating(String customerId);
}
