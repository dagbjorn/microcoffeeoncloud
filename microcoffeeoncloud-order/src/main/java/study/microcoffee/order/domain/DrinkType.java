package study.microcoffee.order.domain;

/**
 * Holder object of a drink type that may be ordered.
 */
public class DrinkType {

    // "type": {
    // "name": "Americano",
    // "family": "Coffee"
    // }

    private String name;

    private String family;

    public DrinkType() {
    }

    public DrinkType(String name, String family) {
        this.name = name;
        this.family = family;
    }

    public String getName() {
        return name;
    }

    public String getFamily() {
        return family;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((family == null) ? 0 : family.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof DrinkType)) {
            return false;
        }
        DrinkType other = (DrinkType) obj;
        if (family == null) {
            if (other.family != null) {
                return false;
            }
        } else if (!family.equals(other.family)) {
            return false;
        }
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return String.format("DrinkType [name=%s, family=%s]", name, family);
    }
}
