package study.microcoffee.order.domain;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

/**
 * Unit tests of {@link DrinkType}.
 */
class DrinkTypeTest {

    @Test
    void equalsWhenDrinkTypeIsEqualShouldReturnTrue() {
        DrinkType drink1 = new DrinkType("Americano", "Coffee");
        DrinkType drink2 = new DrinkType("Americano", "Coffee");

        assertThat(drink1).isEqualTo(drink2);
    }

    @Test
    void equalsWhenDrinkTypeIsNotEqualShouldReturnFalse() {
        DrinkType drink1 = new DrinkType("Americano", "Coffee");
        DrinkType drink2 = new DrinkType("Latte", "Coffee");

        assertThat(drink1).isNotEqualTo(drink2);
    }

    @Test
    void equalsWhenDrinkFamilyIsNotEqualShouldReturnFalse() {
        DrinkType drink1 = new DrinkType("Regular", "Coffee");
        DrinkType drink2 = new DrinkType("Regular", "Tea");

        assertThat(drink1).isNotEqualTo(drink2);
    }
}
