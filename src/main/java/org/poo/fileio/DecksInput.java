package org.poo.fileio;

import java.util.ArrayList;

public final class DecksInput {
    private int nrCardsInDeck;
    private int nrDecks;
    private ArrayList<ArrayList<CardInput>> decks;

    /**
     * Constructor
     */
    public DecksInput() {
    }

    /**
     * @return Getter for nrCardsInDeck
     */
    public int getNrCardsInDeck() {
        return nrCardsInDeck;
    }

    /**
     * @param nrCardsInDeck Setter for nrCardsInDeck
     */
    public void setNrCardsInDeck(final int nrCardsInDeck) {
        this.nrCardsInDeck = nrCardsInDeck;
    }

    /**
     * @return Getter for nrDecks
     */
    public int getNrDecks() {
        return nrDecks;
    }

    /**
     * @param nrDecks Setter for nrDecks
     */
    public void setNrDecks(final int nrDecks) {
        this.nrDecks = nrDecks;
    }

    /**
     * @return Getter for decks
     */
    public ArrayList<ArrayList<CardInput>> getDecks() {
        return decks;
    }

    /**
     * @param decks Setter for decks
     */
    public void setDecks(final ArrayList<ArrayList<CardInput>> decks) {
        this.decks = decks;
    }
}
