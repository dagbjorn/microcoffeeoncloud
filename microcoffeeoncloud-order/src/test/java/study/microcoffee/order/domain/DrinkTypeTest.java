package study.microcoffee.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

/**
 * Unit tests of {@link DrinkType}.
 */
public class DrinkTypeTest {

    @Test
    public void equalsWhenDrinkTypeIsEqualShouldReturnTrue() {
        DrinkType drink1 = new DrinkType("Americano", "Coffee");
        DrinkType drink2 = new DrinkType("Americano", "Coffee");

        assertThat(drink1.equals(drink2)).isTrue();
    }

    @Test
    public void equalsWhenDrinkTypeIsNotEqualShouldReturnFalse() {
        DrinkType drink1 = new DrinkType("Americano", "Coffee");
        DrinkType drink2 = new DrinkType("Latte", "Coffee");

        assertThat(drink1.equals(drink2)).isFalse();
    }

    @Test
    public void equalsWhenDrinkFamilyIsNotEqualShouldReturnFalse() {
        DrinkType drink1 = new DrinkType("Regular", "Coffee");
        DrinkType drink2 = new DrinkType("Regular", "Tea");

        assertThat(drink1.equals(drink2)).isFalse();
    }
}
