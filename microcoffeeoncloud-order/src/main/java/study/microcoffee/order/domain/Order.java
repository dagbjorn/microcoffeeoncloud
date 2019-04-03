package study.microcoffee.order.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

/**
 * Domain object for storing a coffee order.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(toBuilder = true)
@ToString
@Document(collection = "orders")
public class Order {

    @Id
    private String id;

    private long coffeeShopId;

    private String drinker;

    private String size;

    private DrinkType type;

    private String[] selectedOptions;
}
