package org.poo.main;

import com.fasterxml.jackson.core.util.DefaultPrettyPrinter;
import com.fasterxml.jackson.core.util.DefaultIndenter;
import com.fasterxml.jackson.core.util.Separators;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import org.poo.checker.Checker;
import org.poo.checker.CheckerConstants;
import org.poo.fileio.GameInput;
import org.poo.fileio.StartGameInput;
import org.poo.fileio.Cards;
import org.poo.fileio.Hero;
import org.poo.fileio.Input;
import org.poo.fileio.ActionsInput;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Objects;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public final class Main {
    private static final int FRONT_ROW_PLAYER_ONE = 2;
    private static final int FRONT_ROW_PLAYER_TWO = 1;
    private static final int BACK_ROW_PLAYER_ONE = 3;
    private static final int BACK_ROW_PLAYER_TWO = 0;
    private static final int TABLE_COLUMNS = 5;
    private static final int TABLE_ROWS = 4;
    private static final int SUM_PLAYERS_INDEX = 3;

    private Main() {
    }

    /**
     * Main function
     * @param args arguments
     * @throws IOException exception
     */
    public static void main(final String[] args) throws IOException {
        File directory = new File(CheckerConstants.TESTS_PATH);
        Path path = Paths.get(CheckerConstants.RESULT_PATH);

        if (Files.exists(path)) {
            File resultFile = new File(String.valueOf(path));
            for (File file : Objects.requireNonNull(resultFile.listFiles())) {
                file.delete();
            }
            resultFile.delete();
        }
        Files.createDirectories(path);

        for (File file : Objects.requireNonNull(directory.listFiles())) {
            String filepath = CheckerConstants.OUT_PATH + file.getName();
            File out = new File(filepath);
            boolean isCreated = out.createNewFile();
            if (isCreated) {
                action(file.getName(), filepath);
            }
        }

        Checker.calculateScore();
    }

    /**
     * Function that shows the frozen cards
     * @param startGame
     * @param result
     * @param objectMapper
     */
    private static void handleFrozenCards(final StartGameInput startGame,
                                          final ObjectNode result,
                                          final ObjectMapper objectMapper) {

        ArrayNode frozenCards = objectMapper.createArrayNode();
        for (int i = 0; i < TABLE_ROWS; i++) {
            for (Cards card : startGame.getTable().get(i)) {
                if (card.getFrozen()) {
                    ObjectNode frozenCard = objectMapper.valueToTree(card);
                    frozenCards.add(frozenCard);
                }
            }
        }
        result.set("output", frozenCards);
    }

    /**
     * Function that shows cards in player's deck
     * @param playerOneDeck
     * @param playerTwoDeck
     * @param action
     * @param result
     * @param objectMapper
     */
    private static void handlePlayerDeck(final List<Cards> playerOneDeck,
                                         final List<Cards> playerTwoDeck,
                                         final ActionsInput action,
                                         final ObjectNode result,
                                         final ObjectMapper objectMapper) {

        result.put("playerIdx", action.getPlayerIdx());
        if (action.getPlayerIdx() == 1) {
            result.set("output", objectMapper.valueToTree(playerOneDeck));
        } else {
            result.set("output", objectMapper.valueToTree(playerTwoDeck));
        }
    }

    /**
     * Function that shows player's hero
     * @param startGame
     * @param action
     * @param result
     * @param objectMapper
     */
    public static void handlePlayerHero(final StartGameInput startGame,
                                        final ActionsInput action,
                                        final ObjectNode result,
                                        final ObjectMapper objectMapper) {

        result.put("playerIdx", action.getPlayerIdx());
        if (action.getPlayerIdx() == 1) {
            result.set("output", objectMapper.valueToTree(startGame.getPlayerOneHero()));
        } else {
            result.set("output", objectMapper.valueToTree(startGame.getPlayerTwoHero()));
        }
    }

    /**
     * Function that ensures the player's turn is ended,
     * switches the turn and updates the game state
     * @param startGame
     * @param playerOneDeck
     * @param playerTwoDeck
     */
    public static void handlePlayerTurn(final StartGameInput startGame,
                                        final List<Cards> playerOneDeck,
                                        final List<Cards> playerTwoDeck) {

        startGame.getCurrentPlayer().setEndTurn(true);
        for (Integer i : startGame.getPlayerRows()) {
            for (Cards card : startGame.getTable().get(i)) {
                card.setFrozen(false);
            }
        }

        // switch the turn
        startGame.setStartingPlayer(SUM_PLAYERS_INDEX - startGame.getStartingPlayer());

        // check if both players have ended their turn
        if (startGame.getPlayerOne().isEndTurn()
                && startGame.getPlayerTwo().isEndTurn()) {

            startGame.getPlayerOne().setEndTurn(false);
            startGame.getPlayerTwo().setEndTurn(false);

            startGame.getPlayerOne().incrementManaBonus();
            startGame.getPlayerTwo().incrementManaBonus();

            startGame.getPlayerOne().setMana(startGame.getPlayerOne().getMana()
                    + startGame.getPlayerOne().getManaBonus().getManaBonus());
            startGame.getPlayerTwo().setMana(startGame.getPlayerTwo().getMana()
                    + startGame.getPlayerTwo().getManaBonus().getManaBonus());

            if (!playerOneDeck.isEmpty()
                    && startGame.getPlayerOne().getHand().size() < TABLE_COLUMNS) {
                startGame.getPlayerOne().addCardInHand(playerOneDeck.remove(0));
            }

            if (!playerTwoDeck.isEmpty()
                    && startGame.getPlayerTwo().getHand().size() < TABLE_COLUMNS) {
                startGame.getPlayerTwo().addCardInHand(playerTwoDeck.remove(0));
            }

            for (int i = 0; i < TABLE_ROWS; i++) {
                for (Cards card : startGame.getTable().get(i)) {
                    card.setAttacked(false);
                    card.setUsedAbility(false);
                }
            }

            startGame.getPlayerOneHero().setUsedAbility(false);
            startGame.getPlayerTwoHero().setUsedAbility(false);
        }
    }

    /**
     * Function that places a card on the table if the placement is valid and updates the game state
     * @param startGame
     * @param action
     * @param output
     * @param result
     */
    public static void handlePlaceCard(final StartGameInput startGame,
                                       final ActionsInput action,
                                       final ArrayNode output,
                                       final ObjectNode result) {

        int row;
        Cards cardToPlace = startGame.getCurrentPlayer().getHand().
                get(action.getHandIdx());

        // determine the turn based on card properties and player position
        if (cardToPlace.isBackrow()) {
            row = startGame.getStartingPlayer() == 1
                    ? BACK_ROW_PLAYER_ONE : BACK_ROW_PLAYER_TWO;
        } else if (cardToPlace.isFrontrow()) {
            row = startGame.getStartingPlayer() == 1
                    ? FRONT_ROW_PLAYER_ONE : FRONT_ROW_PLAYER_TWO;
        } else {
            row = -1; // Invalid placement
        }

        if (row != -1) {
            if (startGame.getCurrentPlayer().getMana() < cardToPlace.getMana()) {
                result.put("handIdx", action.getHandIdx());
                result.put("error", "Not enough mana to place card on table.");
                output.add(result);
            } else if (startGame.getTable().get(row).size() >= TABLE_COLUMNS) {
                result.put("handIdx", action.getHandIdx());
                result.put("error",
                        "Cannot place card on table since row is full.");
                output.add(result);
            } else {
                // Place the card on the table
                startGame.getCurrentPlayer().decreaseMana(cardToPlace.getMana());
                startGame.getTable().get(row).add(cardToPlace);
                startGame.getCurrentPlayer().getHand().remove(action.getHandIdx());
            }
        } else {
            result.put("handIdx", action.getHandIdx());
            result.put("error", "Invalid card placement.");
            output.add(result);
        }
    }

    /**
     * Function that shows the cards in player's hand
     * @param startGame
     * @param action
     * @param result
     * @param objectMapper
     */
    public static void handleCardsInHand(final StartGameInput startGame,
                                         final ActionsInput action,
                                         final ObjectNode result,
                                         final ObjectMapper objectMapper) {

        result.put("playerIdx", action.getPlayerIdx());
        if (action.getPlayerIdx() == 1) {
            result.set("output", objectMapper.valueToTree(startGame.getPlayerOne().getHand()));
        } else {
            result.set("output", objectMapper.valueToTree(startGame.getPlayerTwo().getHand()));
        }
    }

    /**
     * Function that shows the cards on the table
     * @param startGame
     * @param result
     */
    public static void handlePlayerMana(final StartGameInput startGame,
                                        final ActionsInput action,
                                        final ObjectNode result) {

        result.put("playerIdx", action.getPlayerIdx());
        if (action.getPlayerIdx() == 1) {
            result.put("output", startGame.getPlayerOne().getMana());
        } else {
            result.put("output", startGame.getPlayerTwo().getMana());
        }
    }

    /**
     * Function that shows the cards on the table
     * @param startGame
     * @param result
     * @param objectMapper
     */
    public static void handleCardAttack(final StartGameInput startGame,
                                        final ActionsInput action,
                                        final ArrayNode output, String lastError,
                                        final ObjectNode result,
                                        final ObjectMapper objectMapper) {

        // Store attacker and attacked card details in the result
        result.set("cardAttacker", objectMapper.valueToTree(action.getCardAttacker()));
        result.set("cardAttacked", objectMapper.valueToTree(action.getCardAttacked()));

        // Check for potential errors
        String currentError = validateAttack(startGame, action);

        // If an error is found and it's different from the last error, output it
        if (currentError != null && !currentError.equals(lastError)) {
            result.put("error", currentError);
            output.add(result);
            lastError = currentError; // Update last error
            return; // Stop further execution
        }

        // If no errors, process the attack
        processAttack(startGame, action);
        lastError = null; // Reset the last error
    }

    /**
     * Function that validates the attack and returns an error message if the attack is invalid.
     * @param startGame
     * @param action
     */
    private static String validateAttack(final StartGameInput startGame,
                                         final ActionsInput action) {

        int attackerX = action.getCardAttacker().getX();
        int attackerY = action.getCardAttacker().getY();
        int attackedX = action.getCardAttacked().getX();
        int attackedY = action.getCardAttacked().getY();

        Cards attacker = startGame.getTable().get(attackerX).get(attackerY);
        Cards attacked = (attackedX < startGame.getTable().size()
                && attackedY < startGame.getTable().get(attackedX).size())
                ? startGame.getTable().get(attackedX).get(attackedY) : null;

        // Ensure attacked card belongs to the enemy
        if ((startGame.getStartingPlayer() == 1 && (attackedX == FRONT_ROW_PLAYER_ONE
                || attackedX == BACK_ROW_PLAYER_ONE))
                || (startGame.getStartingPlayer() == 2 && (attackedX == FRONT_ROW_PLAYER_TWO
                        || attackedX == BACK_ROW_PLAYER_TWO))) {
            return "Attacked card does not belong to the enemy.";
        }

        // Check if attacker has already attacked
        if (attacker.hasAttacked()) {
            return "Attacker card has already attacked this turn.";
        }

        // Check if attacker is frozen
        if (attacker.getFrozen()) {
            return "Attacker card is frozen.";
        }

        // Ensure attacked card is within valid bounds
        if (attacked == null) {
            return "Invalid coordinates.";
        }

        // Validate that the attack respects 'Tank' rules
        boolean hasEnemyTank = startGame.getEnemyRows().stream()
                .flatMap(row -> startGame.getTable().get(row).stream())
                .anyMatch(card -> "Goliath".equals(card.getName())
                        || "Warden".equals(card.getName()));
        if (hasEnemyTank && !"Goliath".equals(attacked.getName())
                && !"Warden".equals(attacked.getName())) {
            return "Attacked card is not of type 'Tank'.";
        }

        return null; // No errors
    }

    /**
     * Function that processes the attack and updates the game state
     * @param startGame
     * @param action
     */
    private static void processAttack(final StartGameInput startGame,
                                      final ActionsInput action) {

        int attackerX = action.getCardAttacker().getX();
        int attackerY = action.getCardAttacker().getY();
        int attackedX = action.getCardAttacked().getX();
        int attackedY = action.getCardAttacked().getY();

        Cards attacker = startGame.getTable().get(attackerX).get(attackerY);
        Cards attacked = startGame.getTable().get(attackedX).get(attackedY);

        int damage = attacker.getAttackDamage();
        int remainingHealth = attacked.getHealth() - damage;

        // Update attacked card's health or remove it if health drops to zero
        if (remainingHealth <= 0) {
            startGame.getTable().get(attackedX).remove(attackedY);
        } else {
            attacked.setHealth(remainingHealth);
        }

        // Mark attacker as having attacked this turn
        attacker.setAttacked(true);
    }

    /**
     * Function that handles the card at a given position
     * @param startGame
     * @param action
     * @param result
     * @param objectMapper
     */
    public static void handleCardAtPosition(final StartGameInput startGame,
                                            final ActionsInput action,
                                            final ObjectNode result,
                                            final ObjectMapper objectMapper) {

        result.put("x", action.getX());
        result.put("y", action.getY());

        // Check if the card at the given position is valid
        if (startGame.getTable().size() <= action.getX()
                || startGame.getTable().get(action.getX()).size() <= action.getY()
                || startGame.getTable().get(action.getX()).
                get(action.getY()) == null) {
            result.put("output", "No card available at that position.");
        } else {
            result.set("output", objectMapper.valueToTree(startGame.getTable().
                    get(action.getX()).get(action.getY())));
        }
    }

    /**
     * Function that handles the card ability and updates the game state
     * @param startGame
     * @param action
     * @param output
     * @param lastError
     * @param result
     * @param objectMapper
     */
    public static void handleCardAbility(final StartGameInput startGame,
                                         final ActionsInput action,
                                         final ArrayNode output, String lastError,
                                         final ObjectNode result,
                                         final ObjectMapper objectMapper) {

        // add attacker and attacked card details to the result
        result.set("cardAttacker", objectMapper.valueToTree(action.getCardAttacker()));
        result.set("cardAttacked", objectMapper.valueToTree(action.getCardAttacked()));

        Cards attackerCard = startGame.getTable()
                .get(action.getCardAttacker().getX())
                .get(action.getCardAttacker().getY());
        Cards attackedCard = startGame.getTable()
                .get(action.getCardAttacked().getX())
                .get(action.getCardAttacked().getY());

        int enemyFrontRow = (startGame.getStartingPlayer() == 1)
                ? FRONT_ROW_PLAYER_TWO : FRONT_ROW_PLAYER_ONE;
        int enemyBackRow = (startGame.getStartingPlayer() == 1)
                ? BACK_ROW_PLAYER_TWO : BACK_ROW_PLAYER_ONE;

        // check for potential errors
        if (attackerCard.getFrozen()) {
            result.put("error", "Attacker card is frozen.");
            output.add(result);
            return;
        }

        // check if attacker has already attacked
        if (attackerCard.isUsedAbility() || attackerCard.hasAttacked()) {
            result.put("error", "Attacker card has already attacked this turn.");
            output.add(result);
            return;
        }

        // "Disciple" ability
        if (Objects.equals(attackerCard.getName(), "Disciple")) {
            int allyFrontRow = (startGame.getStartingPlayer() == 1)
                    ? FRONT_ROW_PLAYER_ONE : FRONT_ROW_PLAYER_TWO;
            int allyBackRow = (startGame.getStartingPlayer() == 1)
                    ? BACK_ROW_PLAYER_ONE : BACK_ROW_PLAYER_TWO;

            if (action.getCardAttacked().getX() != allyFrontRow
                    && action.getCardAttacked().getX() != allyBackRow) {
                result.put("error", "Attacked card does not belong to the current player.");
                output.add(result);
                return;
            }
            attackedCard.increaseHealth(2);
            attackerCard.setUsedAbility(true);
            return;
        }

        // "The Cursed One", "The Ripper" and "Miraj" abilities
        if (Objects.equals(attackerCard.getName(), "The Ripper")
                || Objects.equals(attackerCard.getName(), "Miraj")
                || Objects.equals(attackerCard.getName(), "The Cursed One")) {

            if (action.getCardAttacked().getX() == enemyFrontRow
                    || action.getCardAttacked().getX() == enemyBackRow) {

                // Check if the attacked card is a tank
                boolean hasTank = startGame.getTable().get(enemyFrontRow).stream()
                        .anyMatch(card -> Objects.equals(card.getName(), "Goliath")
                                || Objects.equals(card.getName(), "Warden"))
                        || startGame.getTable().get(enemyBackRow).stream()
                        .anyMatch(card -> Objects.equals(card.getName(), "Goliath")
                                || Objects.equals(card.getName(), "Warden"));

                // if the attacked card is not a tank, output an error
                if (hasTank && !Objects.equals(attackedCard.getName(), "Goliath")
                        && !Objects.equals(attackedCard.getName(), "Warden")) {
                    result.put("error", "Attacked card is not of type 'Tank'.");
                    output.add(result);
                    return;
                }

                attackerCard.setUsedAbility(true);
                switch (attackerCard.getName()) {
                    case "The Ripper":
                        attackedCard.decreaseAttackDamage(2);
                        break;

                    case "The Cursed One":
                        attackedCard.switchHealthAndAttack();
                        // if the attacked card's health drops to zero, remove it from the table
                        if (attackedCard.getHealth() <= 0) {
                            startGame.getTable()
                                    .get(action.getCardAttacked().getX())
                                    .remove(action.getCardAttacked().getY());
                        }
                        break;

                    case "Miraj":
                        // swap health values between the attacker and the attacked card
                        int attackerHealth = attackerCard.getHealth();
                        int attackedHealth = attackedCard.getHealth();
                        attackedCard.setHealth(attackerHealth);
                        attackerCard.setHealth(attackedHealth);
                        break;

                    default:
                        break;
                }
            } else {
                result.put("error", "Attacked card does not belong to the enemy.");
                output.add(result);
            }
        }
    }

    /**
     * Function that handles the attack on the enemy hero and updates the game state
     * @param startGame
     * @param action
     * @param output
     * @param result
     * @param objectMapper
     */
    public static void handleAttackHero(final StartGameInput startGame,
                                        final ActionsInput action,
                                        final ArrayNode output,
                                        final ObjectNode result,
                                        final ObjectMapper objectMapper) {
        // Check if the attacking card is frozen
        if (startGame.getTable()
                .get(action.getCardAttacker().getX())
                .get(action.getCardAttacker().getY()).getFrozen()) {
            result.set("cardAttacker",
                    objectMapper.valueToTree(action.getCardAttacker()));
            result.put("error", "Attacker card is frozen.");
            output.add(result);
            return;
        }

        // Check if the attacking card has already attacked or used its ability
        if (startGame.getTable()
                .get(action.getCardAttacker().getX())
                .get(action.getCardAttacker().getY()).hasAttacked()
                || startGame.getTable()
                .get(action.getCardAttacker().getX())
                .get(action.getCardAttacker().getY()).isUsedAbility()) {
            result.set("cardAttacker",
                    objectMapper.valueToTree(action.getCardAttacker()));
            result.put("error", "Attacker card has already attacked this turn.");
            output.add(result);
            return;
        }

        // Check if there is any 'Tank' card on the enemy rows
        int ok = 0;
        outerloop:
        for (Integer i : startGame.getEnemyRows()) {
            for (Cards card : startGame.getTable().get(i)) {
                if (Objects.equals(card.getName(), "Goliath")
                        || Objects.equals(card.getName(), "Warden")) {
                    result.set("cardAttacker",
                            objectMapper.valueToTree(action.getCardAttacker()));
                    result.put("error",
                            "Attacked card is not of type 'Tank'.");
                    output.add(result);
                    ok = 1; // Mark that a Tank card was found
                    break outerloop; // Exit both loops
                }
            }
        }

        // If a Tank card was found, terminate the method
        if (ok == 1) {
            return;
        }

        // Process the attack on the enemy hero
        if (startGame.getStartingPlayer() == 1) {
            // Player 1 attacks Player 2's hero
            startGame.getPlayerTwoHero().decreaseHealth(startGame.getTable()
                    .get(action.getCardAttacker().getX())
                    .get(action.getCardAttacker().getY()).getAttackDamage());

            // Check if Player 2's hero is defeated
            if (startGame.getPlayerTwoHero().getHealth() <= 0) {
                GameInput.incrementPlayerOneWins();
                result.remove("command");
                result.put("gameEnded", "Player one killed the enemy hero.");
                output.add(result);
            }
        } else {
            // Player 2 attacks Player 1's hero
            startGame.getPlayerOneHero().decreaseHealth(startGame.getTable()
                    .get(action.getCardAttacker().getX())
                    .get(action.getCardAttacker().getY()).getAttackDamage());

            // Check if Player 1's hero is defeated
            if (startGame.getPlayerOneHero().getHealth() <= 0) {
                GameInput.incrementPlayerTwoWins();
                result.remove("command");
                result.put("gameEnded", "Player two killed the enemy hero.");
                output.add(result);
            }
        }

        // Mark the attacking card as having attacked
        startGame.getTable().get(action.getCardAttacker().getX())
                .get(action.getCardAttacker().getY()).setAttacked(true);
    }

    /**
     * Function that handles the hero ability and updates the game state
     * @param startGame
     * @param action
     * @param output
     * @param result
     */
    public static void handleHeroAbility(final StartGameInput startGame,
                                         final ActionsInput action,
                                         final ArrayNode output,
                                         final ObjectNode result) {
        // Get the index of the current player
        int currentPlayerIdx = startGame.getStartingPlayer();

        // Select the current and enemy heroes based on the current player
        Hero currentHero = (currentPlayerIdx == 1)
                ? startGame.getPlayerOneHero() : startGame.getPlayerTwoHero();
        Hero enemyHero = (currentPlayerIdx == 1)
                ? startGame.getPlayerTwoHero() : startGame.getPlayerOneHero();

        // Check if the player has enough mana to use the hero's ability
        if (currentHero.getMana() > startGame.getCurrentPlayer().getMana()) {
            result.put("affectedRow", action.getAffectedRow());
            result.put("error", "Not enough mana to use hero's ability.");
            output.add(result);
            return;
        }

        // Check if the hero has already used their ability during this turn
        if (currentHero.isUsedAbility()) {
            result.put("affectedRow", action.getAffectedRow());
            result.put("error", "Hero has already attacked this turn.");
            output.add(result);
            return;
        }

        // Logic for specific heroes with abilities
        if (Objects.equals(currentHero.getName(), "Lord Royce")
                || Objects.equals(currentHero.getName(), "Empress Thorina")) {

            // Ensure that the affected row belongs to the enemy
            if ((currentPlayerIdx == 1 && action.getAffectedRow() > 1)
                    || (currentPlayerIdx == 2 && action.getAffectedRow() < 2)) {
                result.put("affectedRow", action.getAffectedRow());
                result.put("error", "Selected row does not belong to the enemy.");
                output.add(result);
                return;
            }

            // Mark the ability as used and reduce mana
            currentHero.setUsedAbility(true);
            startGame.getCurrentPlayer().decreaseMana(currentHero.getMana());

            // Perform ability-specific actions
            if (Objects.equals(currentHero.getName(), "Lord Royce")) {
                // Freeze cards in the affected row
                for (Cards card : startGame.getTable().
                        get(action.getAffectedRow())) {
                    card.setFrozen(true);
                }
            } else {
                // Remove the card with the highest health in the affected row
                int maxHealthIdx = 0;
                List<Cards> raw = startGame.getTable().
                        get(action.getAffectedRow());
                for (int i = 0; i < raw.size(); i++) {
                    if (raw.get(i).getHealth()
                            > raw.get(maxHealthIdx).getHealth()) {
                        maxHealthIdx = i;
                    }
                }
                raw.remove(maxHealthIdx);
            }
        } else {
            // Logic for non-specific heroes
            int allyFrontRow = (currentPlayerIdx == 1)
                    ? FRONT_ROW_PLAYER_ONE : FRONT_ROW_PLAYER_TWO;
            int allyBackRow = (currentPlayerIdx == 1)
                    ? BACK_ROW_PLAYER_ONE : BACK_ROW_PLAYER_TWO;

            // Ensure that the affected row belongs to the current player
            if ((currentPlayerIdx == 1 && action.getAffectedRow() < 2)
                    || (currentPlayerIdx == 2 && action.getAffectedRow() > 1)) {
                result.put("affectedRow", action.getAffectedRow());
                result.put("error",
                        "Selected row does not belong to the current player.");
                output.add(result);
                return;
            }

            // Mark the ability as used and reduce mana
            currentHero.setUsedAbility(true);
            startGame.getCurrentPlayer().decreaseMana(currentHero.getMana());

            // Perform ability-specific actions for King Mudface or other heroes
            if (Objects.equals(currentHero.getName(), "King Mudface")) {
                // Increase health for all cards in the affected row
                for (Cards card : startGame.getTable().
                        get(action.getAffectedRow())) {
                    card.increaseHealth(1);
                }
            } else {
                // Increase attack for all cards in the affected row
                for (Cards card : startGame.getTable().
                        get(action.getAffectedRow())) {
                    card.decreaseAttackDamage(-1);
                }
            }
        }
    }

    /**
     * Function that reads the input data, processes it and writes the output
     * @param filePath1 input file
     * @param filePath2 output file
     * @throws IOException exception
     */
    public static void action(final String filePath1,
                              final String filePath2) throws IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        Input inputData = objectMapper.readValue(new File(CheckerConstants.TESTS_PATH + filePath1),
                Input.class);

        // Create an ArrayNode to store the output results
        ArrayNode output = objectMapper.createArrayNode();

        // Set the total number of games played
        GameInput.setTotalGamesPlayed(inputData.getGames().size());
        for (GameInput game : inputData.getGames()) {
            StartGameInput startGame = game.getStartGame();

            // Deep copy and shuffle the decks for both players
            List<Cards> playerOneDeck = GameInput.deepCopyDeck(inputData.
                    getPlayerOneDecks().getDecks().get(startGame.getPlayerOneDeckIdx()));
            List<Cards> playerTwoDeck = GameInput.deepCopyDeck(inputData.
                    getPlayerTwoDecks().getDecks().get(startGame.getPlayerTwoDeckIdx()));

            Collections.shuffle(playerOneDeck, new Random(startGame.getShuffleSeed()));
            Collections.shuffle(playerTwoDeck, new Random(startGame.getShuffleSeed()));

            // Draw the first card for each player
            if (!playerOneDeck.isEmpty()) {
                startGame.getPlayerOne().addCardInHand(playerOneDeck.remove(0));
            }

            if (!playerTwoDeck.isEmpty()) {
                startGame.getPlayerTwo().addCardInHand(playerTwoDeck.remove(0));
            }

            // Initialize the game table
            startGame.setTable(new ArrayList<>());

            String lastError = null; // Ultima eroare întâlnită

            // Process each action in the game
            for (ActionsInput action : game.getActions()) {
                String command = action.getCommand();
                ObjectNode result = objectMapper.createObjectNode();
                result.put("command", command);
                switch (command) {
                    case "getPlayerDeck":
                        handlePlayerDeck(playerOneDeck, playerTwoDeck,
                                action, result, objectMapper);
                        break;

                    case "getPlayerHero":
                        handlePlayerHero(startGame, action, result, objectMapper);
                        break;

                    case "getPlayerTurn":
                        result.put("output", startGame.getStartingPlayer());
                        break;

                    case "endPlayerTurn":
                        handlePlayerTurn(startGame, playerOneDeck, playerTwoDeck);
                        break;

                    case "placeCard":
                        handlePlaceCard(startGame, action, output, result);
                        break;

                    case "getCardsInHand":
                        handleCardsInHand(startGame, action, result, objectMapper);
                        break;

                    case "getCardsOnTable":
                        result.set("output", objectMapper.valueToTree(startGame.getTable()));
                        break;

                    case "getPlayerMana":
                        handlePlayerMana(startGame, action, result);
                        break;

                    case "cardUsesAttack":
                        handleCardAttack(startGame, action, output,
                                lastError, result, objectMapper);
                        break;

                    case "getCardAtPosition":
                        handleCardAtPosition(startGame, action, result, objectMapper);
                        break;

                    case "cardUsesAbility":
                        handleCardAbility(startGame, action, output,
                                lastError, result, objectMapper);
                        break;

                    case "useAttackHero":
                        handleAttackHero(startGame, action, output, result, objectMapper);
                        break;

                    case "useHeroAbility":
                        handleHeroAbility(startGame, action, output, result);
                        break;


                    case "getFrozenCardsOnTable":
                        handleFrozenCards(startGame, result, objectMapper);
                        break;

                    case "getTotalGamesPlayed":
                        result.put("output", GameInput.getTotalGamesPlayed());
                        break;

                    case "getPlayerOneWins":
                        result.put("output", GameInput.getPlayerOneWins());
                        break;

                    case "getPlayerTwoWins":
                        result.put("output", GameInput.getPlayerTwoWins());
                        break;

                    default:
                        result.put("output", "Invalid command");
                        break;
                }
                if (!command.equals("endPlayerTurn") && !command.equals("placeCard")
                        && !command.equals("cardUsesAttack")
                        && !command.equals("cardUsesAbility")
                        && !command.equals("useAttackHero")
                        && !command.equals("useHeroAbility")) {
                    output.add(result);
                }
            }
            startGame.getPlayerOne().getHand().clear();
            startGame.getPlayerTwo().getHand().clear();
            for (ArrayList<Cards> row : startGame.getTable()) {
                row.clear();
            }
        }

        // Custom pretty printer to match the output with the reference
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter() {
            @Override
            public DefaultPrettyPrinter createInstance() {
                return new DefaultPrettyPrinter(this);
            }

            @Override
            public DefaultPrettyPrinter withSeparators(final Separators separators) {
                this._separators = separators;
                this._objectFieldValueSeparatorWithSpaces
                        = String.valueOf(separators.getObjectFieldValueSeparator());
                return this;
            }
        }.withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
                .withObjectIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        ObjectWriter objectWriter = objectMapper.writer(prettyPrinter);
        objectWriter.writeValue(new File(filePath2), output);
    }
}
