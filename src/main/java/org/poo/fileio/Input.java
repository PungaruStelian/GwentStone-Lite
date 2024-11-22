package org.poo.fileio;

import java.util.ArrayList;

public final class Input {
    private DecksInput playerOneDecks;
    private DecksInput playerTwoDecks;
    private ArrayList<GameInput> games;

    /**
     * Constructor
     */
    public Input() {
    }

    /**
     * @return Getter for games
     */
    public ArrayList<GameInput> getGames() {
        return games;
    }

    /**
     * @param games Setter for games
     */
    public void setGames(final ArrayList<GameInput> games) {
        this.games = games;
    }

    /**
     * @return Getter for playerOneDecks
     */
    public DecksInput getPlayerOneDecks() {
        return playerOneDecks;
    }

    /**
     * @param playerOneDecks Setter for playerOneDecks
     */
    public void setPlayerOneDecks(final DecksInput playerOneDecks) {
        this.playerOneDecks = playerOneDecks;
    }

    /**
     * @return Getter for playerTwoDecks
     */
    public DecksInput getPlayerTwoDecks() {
        return playerTwoDecks;
    }

    /**
     * @param playerTwoDecks Setter for playerTwoDecks
     */
    public void setPlayerTwoDecks(final DecksInput playerTwoDecks) {
        this.playerTwoDecks = playerTwoDecks;
    }
}
