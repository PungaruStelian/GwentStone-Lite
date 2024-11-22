package org.poo.fileio;

import com.fasterxml.jackson.annotation.JsonIgnore;

public final class Hero extends Cards {
    private static final int FULL_HEALTH = 30;

    private int health = FULL_HEALTH;
    @JsonIgnore
    private int attackDamage;

    // Constructor
    public Hero() {
        super();
    }

    /**
     * @param amount the amount that will be added to the health
     */
    public void increaseHealth(final int amount) {
        this.health += amount;
    }

    /**
     * @param amount the amount that will be subtracted from the health
     */
    public void decreaseHealth(final int amount) {
        this.health -= amount;
    }

    /**
     * @return Getter for attackDamage
     */
    @Override
    public int getHealth() {
        return health;
    }

    /**
     * @param health Setter for health
     */
    @Override
    public void setHealth(final int health) {
        this.health = health;
    }
}
