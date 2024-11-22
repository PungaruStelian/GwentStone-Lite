package org.poo.fileio;

public final class ActionsInput {
    private String command;
    private int handIdx;
    private Coordinates cardAttacker;
    private Coordinates cardAttacked;
    private int affectedRow;
    private int playerIdx;
    private int x;
    private int y;

    /**
     * Constructor
     */
    public ActionsInput() {
    }

    /**
     * Getter for command
     */
    public String getCommand() {
        return command;
    }

    /**
     * Setter for command
     */
    public void setCommand(final String command) {
        this.command = command;
    }

    /**
     * Getter for handIdx
     */
    public int getHandIdx() {
        return handIdx;
    }

    /**
     * @param handIdx Setter for handIdx
     */
    public void setHandIdx(final int handIdx) {
        this.handIdx = handIdx;
    }

    /**
     * @return Getter for cardAttacker
     */
    public Coordinates getCardAttacker() {
        return cardAttacker;
    }

    /**
     * @param cardAttacker Setter for cardAttacker
     */
    public void setCardAttacker(final Coordinates cardAttacker) {
        this.cardAttacker = cardAttacker;
    }

    /**
     * @return Getter for cardAttacked
     */
    public Coordinates getCardAttacked() {
        return cardAttacked;
    }

    /**
     * @param cardAttacked Setter for cardAttacked
     */
    public void setCardAttacked(final Coordinates cardAttacked) {
        this.cardAttacked = cardAttacked;
    }

    /**
     * @return Getter for affectedRow
     */
    public int getAffectedRow() {
        return affectedRow;
    }

    /**
     * @param affectedRow Setter for affectedRow
     */
    public void setAffectedRow(final int affectedRow) {
        this.affectedRow = affectedRow;
    }

    /**
     * @return Getter for playerIdx
     */
    public int getPlayerIdx() {
        return playerIdx;
    }

    /**
     * @param playerIdx Setter for playerIdx
     */
    public void setPlayerIdx(final int playerIdx) {
        this.playerIdx = playerIdx;
    }

    /**
     * @return Getter for x
     */
    public int getX() {
        return x;
    }

    /**
     * @param x Setter for x
     */
    public void setX(final int x) {
        this.x = x;
    }

    /**
     * @return Getter for y
     */
    public int getY() {
        return y;
    }

    /**
     * @param y Setter for y
     */
    public void setY(final int y) {
        this.y = y;
    }
}
