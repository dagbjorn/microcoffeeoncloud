package study.microcoffee.order.api.order.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import study.microcoffee.order.domain.DrinkType;

/**
 * Model object for transferring a coffee order.
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class OrderModel {

    private String id;

    private Long coffeeShopId;

    private String drinker;

    private String size;

    private DrinkType type;

    private String[] selectedOptions;
}
