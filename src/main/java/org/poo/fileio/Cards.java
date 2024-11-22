package org.poo.fileio;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.ArrayList;

public class Cards {
    private int mana;
    private int attackDamage;
    private int health;
    private String description;
    private ArrayList<String> colors;
    private String name;
    @JsonIgnore
    private boolean attacked;
    @JsonIgnore
    private boolean frozen;
    @JsonIgnore
    private boolean usedAbility;
    @JsonIgnore
    private boolean backrow;
    @JsonIgnore
    private boolean frontrow;

    /**
     * Constructor
     */
    public Cards() {
        this.attacked = false;
        this.frozen = false;
        this.usedAbility = false;
        this.backrow = false;
        this.frontrow = false;
    }

    /**
     * Constructor to copy data from CardInput
     * @param cardInput the CardInput object
     */
    public Cards(final CardInput cardInput) {
        this.mana = cardInput.getMana();
        this.attackDamage = cardInput.getAttackDamage();
        this.health = cardInput.getHealth();
        this.description = cardInput.getDescription();
        this.colors = new ArrayList<>(cardInput.getColors());
        this.name = cardInput.getName();
        this.attacked = false;
        this.frozen = false;
        this.usedAbility = false;
        this.backrow = false;
        this.frontrow = false;
        initializeCard();
    }

    /**
     * Initialize the card
     */
    public void initializeCard() {
        if (this.name != null) {
            if (this.name.equals("Sentinel") || this.name.equals("Berserker")
                    || this.name.equals("Disciple") || this.name.equals("The Cursed One")) {
                setBackrow(true);
            }
            if (this.name.equals("Goliath") || this.name.equals("Warden")
                    || this.name.equals("The Ripper") || this.name.equals("Miraj")) {
                setFrontrow(true);
            }
            if (this.name.equals("Disciple") || this.name.equals("The Cursed One")) {
                setAttackDamage(0);
            }
        }
    }

    /**
     * Increase the attack damage of the card
     * @param amount the amount that will be added to the attack damage
     */
    public void decreaseAttackDamage(final int amount) {
        this.attackDamage -= amount;
        if (this.attackDamage < 0) {
            this.attackDamage = 0;
        }
    }

    /**
     * Switch the health and attack damage of the card
     */
    public void switchHealthAndAttack() {
        int temp = this.health;
        this.health = this.attackDamage;
        this.attackDamage = temp;
    }

    /**
     * setter for frozen member
     * @param frozen boolean
     */
    public void setFrozen(boolean frozen) {
        this.frozen = frozen;
    }

    /**
     * getter for frozen member
     * @return boolean
     */
    public boolean getFrozen() {
        return frozen;
    }

    /**
     * getter for backrow member
     * @return boolean
     */
    public boolean isBackrow() {
        return backrow;
    }

    /**
     * setter for backrow member
     * @param backrow boolean
     */
    public void setBackrow(boolean backrow) {
        this.backrow = backrow;
    }

    /**
     * getter for frontrow member
     * @return boolean
     */
    public boolean isFrontrow() {
        return frontrow;
    }

    /**
     * setter for frontrow member
     * @param frontrow boolean
     */
    public void setFrontrow(boolean frontrow) {
        this.frontrow = frontrow;
    }

    /**
     * getter for attacked member
     * @return boolean
     */
    public boolean hasAttacked() {
        return attacked;
    }

    /**
     * setter for attacked member
     * @param hasAttacked boolean
     */
    public void setAttacked(final boolean hasAttacked) {
        this.attacked = hasAttacked;
    }

    /**
     * getter for usedAbility member
     * @return boolean
     */
    public boolean isUsedAbility() {
        return usedAbility;
    }

    /**
     * setter for usedAbility member
     * @param usedAbility boolean
     */
    public void setUsedAbility(final boolean usedAbility) {
        this.usedAbility = usedAbility;
    }

    /**
     * @return Getter for mana member
     */
    public int getMana() {
        return mana;
    }

    /**
     * @param mana Setter for mana member
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * @return Getter for attackDamage member
     */
    public int getAttackDamage() {
        return attackDamage;
    }

    /**
     * @param attackDamage Setter
     * for attackDamage member
     */
    public void setAttackDamage(final int attackDamage) {
        this.attackDamage = attackDamage;
    }

    /**
     * @return Getter for health member
     */
    public int getHealth() {
        return health;
    }

    /**
     * @param health Setter for health member
     */
    public void setHealth(final int health) {
        this.health = health;
    }

    /**
     * Increase the health of the card
     * @param amount the amount that will be added to the health
     */
    public void increaseHealth(final int amount) {
        this.health += amount;
    }

    /**
     * Decrease the health of the card
     * @param amount the amount that will be subtracted from the health
     */
    public void decreaseHealth(final int amount) {
        this.health -= amount;
    }

    /**
     * @return Getter for description member
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Setter for description member
     */
    public void setDescription(final String description) {
        this.description = description;
    }

    /**
     * @return Getter for colors member
     */
    public ArrayList<String> getColors() {
        return colors;
    }

    /**
     * @param colors Setter for colors member
     */
    public void setColors(final ArrayList<String> colors) {
        this.colors = colors;
    }

    /**
     * @return Getter for name member
     */
    public String getName() {
        return name;
    }

    /**
     * @param name Setter for name member
     */
    public void setName(final String name) {
        this.name = name;
    }
}