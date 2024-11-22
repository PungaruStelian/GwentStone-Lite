package org.poo.fileio;

import java.util.ArrayList;

public final class GameInput {

    private StartGameInput startGame;
    private ArrayList<ActionsInput> actions;
    private static int totalGamesPlayed;
    private static int playerOneWins;
    private static int playerTwoWins;

     /**
     * Constructor
     */
    public GameInput() {
    }

    /**
     * Deep copy of a deck
     * @param originalDeck ArrayList of Cards
     * @return copiedDeck ArrayList of Cards
     */
    public static ArrayList<Cards> deepCopyDeck(final ArrayList<CardInput> originalDeck) {
        ArrayList<Cards> copiedDeck = new ArrayList<>();
        for (CardInput cardInput : originalDeck) {
            Cards copiedCard = new Cards(cardInput);
            copiedDeck.add(copiedCard);
        }
        return copiedDeck;
    }

    /**
     * Getter for totalGamesPlayed member
     * @return total number of games played
     */
    public static int getTotalGamesPlayed() {
        return totalGamesPlayed;
    }

    /**
     * Increment totalGamesPlayed member
     */
    public static void incrementTotalGames() {
        totalGamesPlayed++;
    }

    public static void setTotalGamesPlayed(final int totalGamesPlayed) {
        GameInput.totalGamesPlayed = totalGamesPlayed;
    }

    /**
     * Increment playerOneWins member
     */
    public static void incrementPlayerOneWins() {
        playerOneWins++;
    }

    /**
     * Increment playerTwoWins member
     */
    public static void incrementPlayerTwoWins() {
        playerTwoWins++;
    }

    /**
     * Getter for playerOneWins member
     * @return number of wins for player one
     */
    public static int getPlayerOneWins() {
        return playerOneWins;
    }

    /**
     * Getter for playerTwoWins member
     * @return number of wins for player two
     */
    public static int getPlayerTwoWins() {
        return playerTwoWins;
    }

    /**
     * Getter for startGame member
     * @return startGame member
     */
    public StartGameInput getStartGame() {
        return startGame;
    }

    /**
     * Setter for startGame member
     * @param startGame StartGameInput
     */
    public void setStartGame(final StartGameInput startGame) {
        this.startGame = startGame;
    }

    /**
     * Getter for actions member
     * @return actions member
     */
    public ArrayList<ActionsInput> getActions() {
        return actions;
    }

    /**
     * Setter for actions member
     * @param actions ArrayList of ActionsInput
     */
    public void setActions(final ArrayList<ActionsInput> actions) {
        this.actions = actions;
    }
}
