package study.microcoffee.order.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Domain object for storing a coffee order.
 */
@Document(collection = "orders")
public class Order {

    // {
    // "type": {
    // "name": "Americano",
    // "family": "Coffee"
    // },
    // "size": "Small",
    // "drinker": "Dagbj�rn"
    // }

    @Id
    private String id;

    private long coffeeShopId;

    private String drinker;

    private String size;

    private DrinkType type;

    private String[] selectedOptions;

    public Order() {
    }

    private Order(Builder builder) {
        this.id = builder.id;
        this.coffeeShopId = builder.coffeeShopId;
        this.drinker = builder.drinker;
        this.size = builder.size;
        this.type = builder.type;
        this.selectedOptions = builder.selectedOptions.toArray(new String[0]);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getCoffeeShopId() {
        return coffeeShopId;
    }

    public void setCoffeeShopId(long coffeeShopId) {
        this.coffeeShopId = coffeeShopId;
    }

    public String getDrinker() {
        return drinker;
    }

    public String getSize() {
        return size;
    }

    public DrinkType getType() {
        return type;
    }

    public String[] getSelectedOptions() {
        return selectedOptions;
    }

    @Override
    public String toString() {
        return String.format("Order [id=%s, coffeeShopId=%s, type=%s, size=%s, selectedOptions=%s, drinker=%s]", //
            id, coffeeShopId, type, size, Arrays.toString(selectedOptions), drinker);
    }

    public static class Builder {

        private String id;

        private long coffeeShopId;

        private String drinker;

        private String size;

        private DrinkType type;

        private List<String> selectedOptions = new ArrayList<>();

        public Builder order(Order order) {
            this.id = order.getId();
            this.coffeeShopId = order.getCoffeeShopId();
            this.drinker = order.getDrinker();
            this.size = order.getSize();
            this.type = order.getType();
            this.selectedOptions = Arrays.asList(order.getSelectedOptions());
            return this;
        }

        public Builder id(String id) {
            this.id = id;
            return this;
        }

        public Builder coffeeShopId(long coffeeShopId) {
            this.coffeeShopId = coffeeShopId;
            return this;
        }

        public Builder drinker(String drinker) {
            this.drinker = drinker;
            return this;
        }

        public Builder size(String size) {
            this.size = size;
            return this;
        }

        public Builder type(DrinkType type) {
            this.type = type;
            return this;
        }

        public Builder selectedOption(String selectedOption) {
            this.selectedOptions.add(selectedOption);
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }
}
