package org.poo.fileio;

import java.util.ArrayList;


public class Player {
    private int mana;
    private ManaBonus manaBonus;
    private boolean endTurn;
    private final ArrayList<Cards> hand;

    /**
     * Constructor
     */
    public Player() {
        this.mana = 1;
        this.manaBonus = new ManaBonus(1);
        this.endTurn = false;
        this.hand = new ArrayList<>();
    }

    /**
     * @return Getter for manaBonus member
     */
    public ManaBonus getManaBonus() {
        return manaBonus;
    }

    /**
     * Increment manaBonus member
     */
    public void incrementManaBonus() {
        this.manaBonus.setManaBonus(this.manaBonus.getManaBonus() + 1);
    }

    /**
     * Add a card in player's hand
     * @param card the card that will be added in hand
     */
    public void addCardInHand(final Cards card) {
        hand.add(card);
    }

    /**
     * @return Getter for hand member
     */
    public ArrayList<Cards> getHand() {
        return hand;
    }

    /**
     * Getter for mana member
     * @return amount of mana the player hah
     */
    public int getMana() {
        return mana;
    }

    /**
     * Setter for mana member
     * @param mana int number
     */
    public void setMana(final int mana) {
        this.mana = mana;
    }

    /**
     * Decrease mana
     * @param amount int number
     */
    public void decreaseMana(int amount) {
        this.mana -= amount;
    }

    /**
     * Increase mana
     */
    public void increaseMana() {
        this.mana += this.manaBonus.getManaBonus();
    }

    /**
     * @return Getter for endTurn member
     */
    public boolean isEndTurn() {
        return endTurn;
    }

    /**
     * @param endTurn Setter for endTurn member
     */
    public void setEndTurn(boolean endTurn) {
        this.endTurn = endTurn;
    }
}
