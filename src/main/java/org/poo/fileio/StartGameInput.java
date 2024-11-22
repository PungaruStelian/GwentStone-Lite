package org.poo.fileio;

import java.util.ArrayList;
import java.util.List;

public final class StartGameInput {
    private static final int TABLE_ROWS = 4;
    private static final int FRONT_ROW_PLAYER_ONE = 2;
    private static final int FRONT_ROW_PLAYER_TWO = 1;
    private static final int BACK_ROW_PLAYER_ONE = 3;
    private static final int BACK_ROW_PLAYER_TWO = 0;

    private int playerOneDeckIdx;
    private int playerTwoDeckIdx;
    private int shuffleSeed;
    private Hero playerOneHero;
    private Hero playerTwoHero;
    private int startingPlayer;
    private Player playerOne;
    private Player playerTwo;
    private ArrayList<ArrayList<Cards>> table;

    /**
     * Constructor
     */
    public StartGameInput() {
        playerOne = new Player();
        playerTwo = new Player();
    }

    /**
     * getter for playerOne
     */
    public Player getPlayerOne() {
        return playerOne;
    }

    /**
     * getter for playerTwo
     */
    public Player getPlayerTwo() {
        return playerTwo;
    }

    /**
     * getter for current player
     */
    public Player getCurrentPlayer() {
        if (getStartingPlayer() == 1) {
            return playerOne;
        } else {
            return playerTwo;
        }
    }

    /**
     * getter for enemy player rows
     */
    public ArrayList<Integer> getEnemyRows() {
        if (getStartingPlayer() == 2) {
            return new ArrayList<Integer>(List.of(FRONT_ROW_PLAYER_ONE, BACK_ROW_PLAYER_ONE));
        } else {
            return new ArrayList<Integer>(List.of(FRONT_ROW_PLAYER_TWO, BACK_ROW_PLAYER_TWO));
        }
    }

    /**
     * getter for player rows
     */
    public ArrayList<Integer> getPlayerRows() {
        if (getStartingPlayer() == 1) {
            return new ArrayList<Integer>(List.of(FRONT_ROW_PLAYER_ONE, BACK_ROW_PLAYER_ONE));
        } else {
            return new ArrayList<Integer>(List.of(FRONT_ROW_PLAYER_TWO, BACK_ROW_PLAYER_TWO));
        }
    }

    /**
     * @return Getter for table
     */
    public ArrayList<ArrayList<Cards>> getTable() {
        return table;
    }

    /**
     * @param table Setter for table
     */
    public void setTable(final ArrayList<ArrayList<Cards>> table) {
        this.table = new ArrayList<>();
        for (int i = 0; i < TABLE_ROWS; i++) {
            this.table.add(new ArrayList<>());
        }
    }

    /**
     * @return Getter for playerOneDeckIdx
     */
    public int getPlayerOneDeckIdx() {
        return playerOneDeckIdx;
    }

    /**
     * @param playerOneDeckIdx Setter for playerOneDeckIdx
     */
    public void setPlayerOneDeckIdx(final int playerOneDeckIdx) {
        this.playerOneDeckIdx = playerOneDeckIdx;
    }

    /**
     * @return Getter for playerTwoDeckIdx
     */
    public int getPlayerTwoDeckIdx() {
        return playerTwoDeckIdx;
    }

    /**
     * @param playerTwoDeckIdx Setter for playerTwoDeckIdx
     */
    public void setPlayerTwoDeckIdx(final int playerTwoDeckIdx) {
        this.playerTwoDeckIdx = playerTwoDeckIdx;
    }

    /**
     * @return Getter for shuffleSeed
     */
    public int getShuffleSeed() {
        return shuffleSeed;
    }

    /**
     * @param shuffleSeed Setter for shuffleSeed
     */
    public void setShuffleSeed(final int shuffleSeed) {
        this.shuffleSeed = shuffleSeed;
    }

    /**
     * @return Getter for playerOneHero
     */
    public Hero getPlayerOneHero() {
        return playerOneHero;
    }

    /**
     * @param playerOneHero Setter for playerOneHero
     */
    public void setPlayerOneHero(final Hero playerOneHero) {
        this.playerOneHero = playerOneHero;
    }

    /**
     * @return Getter for playerTwoHero
     */
    public Hero getPlayerTwoHero() {
        return playerTwoHero;
    }

    /**
     * @param playerTwoHero Setter for playerTwoHero
     */
    public void setPlayerTwoHero(final Hero playerTwoHero) {
        this.playerTwoHero = playerTwoHero;
    }

    /**
     * @return Getter for startingPlayer
     */
    public int getStartingPlayer() {
        return startingPlayer;
    }

    /**
     * @param startingPlayer Setter for startingPlayer
     */
    public void setStartingPlayer(final int startingPlayer) {
        this.startingPlayer = startingPlayer;
    }
}
