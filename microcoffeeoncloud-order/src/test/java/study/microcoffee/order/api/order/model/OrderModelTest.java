package study.microcoffee.order.api.order.model;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.modelmapper.ModelMapper;

import study.microcoffee.order.domain.DrinkType;
import study.microcoffee.order.domain.Order;

/**
 * Unit tests of {@link OrderModel} mapping.
 */
public class OrderModelTest {

    private ModelMapper modelMapper = new ModelMapper();

    @Test
    public void orderModelShouldMapToOrderEntity() {
        OrderModel orderModel = OrderModel.builder() //
            .id("1234567890abcdf") //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOptions(new String[] { "skimmed milk", "whipped cream" }) //
            .build();

        Order order = modelMapper.map(orderModel, Order.class);

        modelMapper.validate();

        assertThat(order.getId()).isEqualTo(orderModel.getId());
        assertThat(order.getType()).isEqualTo(orderModel.getType());
        assertThat(order.getSize()).isEqualTo(orderModel.getSize());
        assertThat(order.getDrinker()).isEqualTo(orderModel.getDrinker());
        assertThat(order.getSelectedOptions()).isEqualTo(orderModel.getSelectedOptions());
    }

    @Test
    public void orderEntityShouldMapToOrderModel() {
        Order order = Order.builder() //
            .id("1234567890abcdf") //
            .type(new DrinkType("Latte", "Coffee")) //
            .size("Small") //
            .drinker("Dagbjørn") //
            .selectedOptions(new String[] { "skimmed milk", "whipped cream" }) //
            .build();

        OrderModel orderModel = modelMapper.map(order, OrderModel.class);

        modelMapper.validate();

        assertThat(orderModel.getId()).isEqualTo(order.getId());
        assertThat(orderModel.getType()).isEqualTo(order.getType());
        assertThat(orderModel.getSize()).isEqualTo(order.getSize());
        assertThat(orderModel.getDrinker()).isEqualTo(order.getDrinker());
        assertThat(orderModel.getSelectedOptions()).isEqualTo(order.getSelectedOptions());
    }
}
