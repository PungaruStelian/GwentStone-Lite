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
                game.getPlayerOne().addCardInHand(playerOneDeck.remove(0));
            }

            if (!playerTwoDeck.isEmpty()) {
                game.getPlayerTwo().addCardInHand(playerTwoDeck.remove(0));
            }

            // Initialize the game table
            startGame.setTable(new ArrayList<>());

            // Process each action in the game
            for (ActionsInput action : game.getActions()) {
                String command = action.getCommand();
                ObjectNode result = objectMapper.createObjectNode();
                result.put("command", command);

                switch (command) {
                    case "getPlayerDeck":
                        result.put("playerIdx", action.getPlayerIdx());
                        if (action.getPlayerIdx() == 1) {
                            result.set("output", objectMapper.valueToTree(playerOneDeck));
                        } else {
                            result.set("output", objectMapper.valueToTree(playerTwoDeck));
                        }
                        break;

                    case "getPlayerHero":
                        result.put("playerIdx", action.getPlayerIdx());
                        if (action.getPlayerIdx() == 1) {
                            result.set("output", objectMapper.valueToTree(startGame.getPlayerOneHero()));
                        } else {
                            result.set("output", objectMapper.valueToTree(startGame.getPlayerTwoHero()));
                        }
                        break;

                    case "getPlayerTurn":
                        result.put("output", startGame.getStartingPlayer());
                        break;

                    case "endPlayerTurn":
                        game.getCurrentPlayer().setEndTurn(true);
                        for (Integer i : game.getPlayerRows()) {
                            for (Cards card : startGame.getTable().get(i)) {
                                card.setFrozen(false);
                            }
                        }
                        startGame.setStartingPlayer(3 - startGame.getStartingPlayer());

                        if (game.getPlayerOne().isEndTurn() && game.getPlayerTwo().isEndTurn()) {
                            game.getPlayerOne().setEndTurn(false);
                            game.getPlayerTwo().setEndTurn(false);

                            game.getPlayerOne().incrementManaBonus();
                            game.getPlayerTwo().incrementManaBonus();

                            game.getPlayerOne().setMana(game.getPlayerOne().getMana()
                                    + game.getPlayerOne().getManaBonus().getManaBonus());
                            game.getPlayerTwo().setMana(game.getPlayerTwo().getMana()
                                    + game.getPlayerTwo().getManaBonus().getManaBonus());

                            if (!playerOneDeck.isEmpty()
                                    && game.getPlayerOne().getHand().size() < TABLE_COLUMNS) {
                                game.getPlayerOne().addCardInHand(playerOneDeck.remove(0));
                            }

                            if (!playerTwoDeck.isEmpty()
                                    && game.getPlayerTwo().getHand().size() < TABLE_COLUMNS) {
                                game.getPlayerTwo().addCardInHand(playerTwoDeck.remove(0));
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
                        break;

                    case "placeCard":
                        int row;
                        Cards cardToPlace = game.getCurrentPlayer().getHand().get(action.getHandIdx());

                        // Determinăm rândul în funcție de proprietățile cardului și poziția jucătorului
                        if (cardToPlace.isBackrow()) {
                            row = startGame.getStartingPlayer() == 1 ? BACK_ROW_PLAYER_ONE : BACK_ROW_PLAYER_TWO;
                        } else if (cardToPlace.isFrontrow()) {
                            row = startGame.getStartingPlayer() == 1 ? FRONT_ROW_PLAYER_ONE : FRONT_ROW_PLAYER_TWO;
                        } else {
                            row = -1; // Invalid placement
                        }

                        if (row != -1) {
                            if (game.getCurrentPlayer().getMana() < cardToPlace.getMana()) {
                                result.put("handIdx", action.getHandIdx());
                                result.put("error", "Not enough mana to place card on table.");
                                output.add(result);
                            } else if (startGame.getTable().get(row).size() >= TABLE_COLUMNS) {
                                result.put("handIdx", action.getHandIdx());
                                result.put("error", "Cannot place card on table since row is full.");
                                output.add(result);
                            } else {
                                game.getCurrentPlayer().decreaseMana(cardToPlace.getMana());
                                startGame.getTable().get(row).add(cardToPlace);
                                game.getCurrentPlayer().getHand().remove(action.getHandIdx());
                            }
                        } else {
                            result.put("handIdx", action.getHandIdx());
                            result.put("error", "Invalid card placement.");
                            output.add(result);
                        }
                        break;

                    case "getCardsInHand":
                        result.put("playerIdx", action.getPlayerIdx());
                        if (action.getPlayerIdx() == 1) {
                            result.set("output", objectMapper.valueToTree(game.getPlayerOne().getHand()));
                        } else {
                            result.set("output", objectMapper.valueToTree(game.getPlayerTwo().getHand()));
                        }
                        break;

                    case "getCardsOnTable":
                        result.set("output", objectMapper.valueToTree(startGame.getTable()));
                        break;

                    case "getPlayerMana":
                        result.put("playerIdx", action.getPlayerIdx());
                        if (action.getPlayerIdx() == 1) {
                            result.put("output", game.getPlayerOne().getMana());
                        } else {
                            result.put("output", game.getPlayerTwo().getMana());
                        }
                        break;

                    case "cardUsesAttack":
                        result.set("cardAttacker",
                                objectMapper.valueToTree(action.getCardAttacker()));
                        result.set("cardAttacked",
                                objectMapper.valueToTree(action.getCardAttacked()));
                        if ((startGame.getStartingPlayer() == 1
                                && (action.getCardAttacked().getX() == FRONT_ROW_PLAYER_ONE
                                || action.getCardAttacked().getX() == BACK_ROW_PLAYER_ONE))
                                || (startGame.getStartingPlayer() == 2)
                                && (action.getCardAttacked().getX() == FRONT_ROW_PLAYER_TWO
                                || action.getCardAttacked().getX() == BACK_ROW_PLAYER_TWO)) {
                            result.put("error",
                                    "Attacked card does not belong to the enemy.");
                            output.add(result);
                            break;
                        }
                        if (startGame.getTable().get(action.getCardAttacker().getX()).
                                get(action.getCardAttacker().getY()).hasAttacked()) {
                            result.put("error",
                                    "Attacker card has already attacked this turn.");
                            output.add(result);
                            break;
                        }
                        if (startGame.getTable().get(action.getCardAttacker().getX()).
                                get(action.getCardAttacker().getY()).getFrozen()) {
                            result.put("error", "Attacker card is frozen.");
                            output.add(result);
                            break;
                        }
                        if (action.getCardAttacked().getX() >= startGame.getTable().size()
                                || action.getCardAttacked().getY() >= startGame.getTable().
                                get(action.getCardAttacked().getX()).size()) {
                            result.put("error", "Invalid coordinates.");
                            output.add(result);
                            break;
                        }
                        if (!Objects.equals(startGame.getTable().get(action.getCardAttacked()
                                .getX()).get(action.getCardAttacked().getY()).getName(), "Goliath")
                                && !Objects.equals(startGame.getTable().
                                get(action.getCardAttacked().
                                        getX()).get(action.getCardAttacked().getY()).
                                getName(), "Warden")) {
                            outerLoop:
                            for (Integer i : game.getEnemyRows()) {
                                for (Cards card : startGame.getTable().get(i)) {
                                    if (Objects.equals(card.getName(), "Goliath")
                                            || Objects.equals(card.getName(), "Warden")) {
                                        result.put("error",
                                                "Attacked card is not of type 'Tank'.");
                                        output.add(result);
                                        break outerLoop;
                                    }
                                }
                            }
                        }
                        int damage = startGame.getTable().get(action.getCardAttacker().getX()).
                                get(action.getCardAttacker().getY()).getAttackDamage();
                        int health = startGame.getTable().get(action.getCardAttacked().getX()).
                                get(action.getCardAttacked().getY()).getHealth();
                        if (health <= damage) {
                            startGame.getTable().get(action.getCardAttacked().getX()).
                                    remove(action.getCardAttacked().getY());
                        } else {
                            startGame.getTable().get(action.getCardAttacked().getX()).
                                    get(action.getCardAttacked().getY()).
                                    setHealth(health - damage);
                        }
                        startGame.getTable().get(action.getCardAttacker().getX()).
                                get(action.getCardAttacker().getY()).setAttacked(true);
                        break;

                    case "getCardAtPosition":
                        result.put("x", action.getX());
                        result.put("y", action.getY());
                        if (startGame.getTable().size() <= action.getX()
                                || startGame.getTable().get(action.getX()).size() <= action.getY()
                                || startGame.getTable().get(action.getX()).
                                get(action.getY()) == null) {
                            result.put("output", "No card available at that position.");
                        } else {
                            result.set("output", objectMapper.valueToTree(startGame.getTable().
                                    get(action.getX()).get(action.getY())));
                        }
                        break;

                    case "cardUsesAbility":
                        result.set("cardAttacker", objectMapper.valueToTree(action.getCardAttacker()));
                        result.set("cardAttacked", objectMapper.valueToTree(action.getCardAttacked()));

                        Cards attackerCard = startGame.getTable()
                                .get(action.getCardAttacker().getX())
                                .get(action.getCardAttacker().getY());
                        Cards attackedCard = startGame.getTable()
                                .get(action.getCardAttacked().getX())
                                .get(action.getCardAttacked().getY());

                        int enemyFrontRow = (startGame.getStartingPlayer() == 1) ? FRONT_ROW_PLAYER_TWO : FRONT_ROW_PLAYER_ONE;
                        int enemyBackRow = (startGame.getStartingPlayer() == 1) ? BACK_ROW_PLAYER_TWO : BACK_ROW_PLAYER_ONE;

                        // Verificare dacă atacatorul este înghețat sau a folosit deja abilitatea
                        if (attackerCard.getFrozen()) {
                            result.put("error", "Attacker card is frozen.");
                            output.add(result);
                            break;
                        }
                        if (attackerCard.hasAttacked() || attackerCard.isUsedAbility()) {
                            result.put("error", "Attacker card has already used its ability.");
                            output.add(result);
                            break;
                        }

                        // Comportament specific pentru "Disciple"
                        if (Objects.equals(attackerCard.getName(), "Disciple")) {
                            int allyFrontRow = (startGame.getStartingPlayer() == 1) ? FRONT_ROW_PLAYER_ONE : FRONT_ROW_PLAYER_TWO;
                            int allyBackRow = (startGame.getStartingPlayer() == 1) ? BACK_ROW_PLAYER_ONE : BACK_ROW_PLAYER_TWO;

                            if (action.getCardAttacked().getX() != allyFrontRow
                                    && action.getCardAttacked().getX() != allyBackRow) {
                                result.put("error", "Attacked card does not belong to the current player.");
                                output.add(result);
                            } else {
                                attackedCard.increaseHealth(2);
                                attackerCard.setUsedAbility(true);
                            }
                            break;
                        }

                        // Comportament specific pentru alte tipuri de cărți
                        if (Objects.equals(attackerCard.getName(), "The Ripper")
                                || Objects.equals(attackerCard.getName(), "Miraj")
                                || Objects.equals(attackerCard.getName(), "The Cursed One")) {

                            // Verificare dacă atacăm o carte inamică validă
                            if (action.getCardAttacked().getX() == enemyFrontRow
                                    || action.getCardAttacked().getX() == enemyBackRow) {
                                boolean hasTank = startGame.getTable().get(enemyFrontRow).stream()
                                        .anyMatch(card -> Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(), "Warden"))
                                        || startGame.getTable().get(enemyBackRow).stream()
                                        .anyMatch(card -> Objects.equals(card.getName(), "Goliath") || Objects.equals(card.getName(), "Warden"));

                                if (hasTank && !Objects.equals(attackedCard.getName(), "Goliath")
                                        && !Objects.equals(attackedCard.getName(), "Warden")) {
                                    result.put("error", "Attacked card is not of type 'Tank'.");
                                    output.add(result);
                                    break;
                                }

                                // Aplicare efecte speciale
                                attackerCard.setUsedAbility(true);
                                switch (attackerCard.getName()) {
                                    case "The Ripper":
                                        attackedCard.decreaseAttackDamage(2);
                                        break;
                                    case "The Cursed One":
                                        attackedCard.switchHealthAndAttack();
                                        if (attackedCard.getHealth() <= 0) {
                                            startGame.getTable().get(action.getCardAttacked().getX())
                                                    .remove(action.getCardAttacked().getY());
                                        }
                                        break;
                                    case "Miraj":
                                        int attackerHealth = attackerCard.getHealth();
                                        int attackedHealth = attackedCard.getHealth();
                                        attackedCard.setHealth(attackerHealth);
                                        attackerCard.setHealth(attackedHealth);
                                        break;
                                }
                            } else {
                                result.put("error", "Attacked card does not belong to the enemy.");
                                output.add(result);
                            }
                        }
                        break;

                    case "useAttackHero":
                        if (action.getCardAttacker().getX() >= startGame.getTable().size()
                                || action.getCardAttacker().getY() >= startGame.getTable().
                                get(action.getCardAttacker().getX()).size()) {
                            break;
                        }
                        if (startGame.getTable().
                                get(action.getCardAttacker().getX()).
                                get(action.getCardAttacker().getY()).getFrozen()) {
                            result.set("cardAttacker",
                                    objectMapper.valueToTree(action.getCardAttacker()));
                            result.put("error", "Attacker card is frozen.");
                            output.add(result);
                            break;
                        }
                        if (startGame.getTable().
                                get(action.getCardAttacker().getX()).
                                get(action.getCardAttacker().getY()).hasAttacked()
                                || startGame.getTable().
                                get(action.getCardAttacker().getX()).
                                get(action.getCardAttacker().getY()).isUsedAbility()) {
                            result.set("cardAttacker",
                                    objectMapper.valueToTree(action.getCardAttacker()));
                            result.put("error", "Attacker card has already attacked this turn.");
                            output.add(result);
                            break;
                        }

                        outerloop:
                        for (Integer i : game.getPlayerRows()) {
                            for (Cards card : startGame.getTable().get(i)) {
                                if (Objects.equals(card.getName(), "Goliath")
                                        || Objects.equals(card.getName(), "Warden")) {
                                    result.set("cardAttacker",
                                            objectMapper.valueToTree(action.getCardAttacker()));
                                    result.put("error",
                                            "Attacked card is not of type 'Tank’.");
                                    output.add(result);
                                    break outerloop;
                                }
                            }
                        }
                        if (startGame.getStartingPlayer() == 1) {
                            startGame.getPlayerTwoHero().decreaseHealth(startGame.getTable().
                                    get(action.getCardAttacker().getX()).
                                    get(action.getCardAttacker().getY()).getAttackDamage());
                            if (startGame.getPlayerTwoHero().getHealth() <= 0) {
                                GameInput.incrementPlayerOneWins();
                                result.remove("command");
                                result.put("gameEnded", "Player one killed the enemy hero.");
                                output.add(result);
                            }
                        } else {
                            startGame.getPlayerOneHero().decreaseHealth(startGame.getTable().
                                    get(action.getCardAttacker().getX()).
                                    get(action.getCardAttacker().getY()).getAttackDamage());
                            if (startGame.getPlayerOneHero().getHealth() <= 0) {
                                GameInput.incrementPlayerTwoWins();
                                result.remove("command");
                                result.put("gameEnded", "Player two killed the enemy hero.");
                                output.add(result);
                            }
                        }
                        startGame.getTable().get(action.getCardAttacker().getX()).
                                get(action.getCardAttacker().getY()).setAttacked(true);
                        break;

                    case "useHeroAbility":
                        int currentPlayerIdx = startGame.getStartingPlayer();
                        Hero currentHero = (currentPlayerIdx == 1) ? startGame.getPlayerOneHero() : startGame.getPlayerTwoHero();
                        Hero enemyHero = (currentPlayerIdx == 1) ? startGame.getPlayerTwoHero() : startGame.getPlayerOneHero();

// Verificare mana suficientă pentru abilitate
                        if (currentHero.getMana() > game.getCurrentPlayer().getMana()) {
                            result.put("affectedRow", action.getAffectedRow());
                            result.put("error", "Not enough mana to use hero's ability.");
                            output.add(result);
                            break;
                        }

// Verificare dacă abilitatea a fost deja folosită
                        if (currentHero.isUsedAbility()) {
                            result.put("affectedRow", action.getAffectedRow());
                            result.put("error", "Hero has already attacked this turn.");
                            output.add(result);
                            break;
                        }

// Logica pentru eroi specifici
                        if (Objects.equals(currentHero.getName(), "Lord Royce") || Objects.equals(currentHero.getName(), "Empress Thorina")) {
                            int EnemyFrontRow = (startGame.getStartingPlayer() == 1) ? FRONT_ROW_PLAYER_TWO : FRONT_ROW_PLAYER_ONE;
                            int EnemyBackRow = (startGame.getStartingPlayer() == 1) ? BACK_ROW_PLAYER_TWO : BACK_ROW_PLAYER_ONE;

                            if ((currentPlayerIdx == 1 && action.getAffectedRow() > 1)
                                    || (currentPlayerIdx == 2 && action.getAffectedRow() < 2)) {
                                result.put("affectedRow", action.getAffectedRow());
                                result.put("error", "Selected row does not belong to the enemy.");
                                output.add(result);
                                break;
                            }

                            currentHero.setUsedAbility(true);
                            game.getCurrentPlayer().decreaseMana(currentHero.getMana());

                            if (Objects.equals(currentHero.getName(), "Lord Royce")) {
                                // Înghețare cărți
                                for (Cards card : startGame.getTable().get(action.getAffectedRow())) {
                                    card.setFrozen(true);
                                }
                            } else {
                                // Eliminare carte cu cea mai mare viață
                                int maxHealthIdx = 0;
                                List<Cards> raw = startGame.getTable().get(action.getAffectedRow());
                                for (int i = 0; i < raw.size(); i++) {
                                    if (raw.get(i).getHealth() > raw.get(maxHealthIdx).getHealth()) {
                                        maxHealthIdx = i;
                                    }
                                }
                                raw.remove(maxHealthIdx);
                            }
                        } else {
                            int allyFrontRow = (currentPlayerIdx == 1) ? FRONT_ROW_PLAYER_ONE : FRONT_ROW_PLAYER_TWO;
                            int allyBackRow = (currentPlayerIdx == 1) ? BACK_ROW_PLAYER_ONE : BACK_ROW_PLAYER_TWO;

                            if ((currentPlayerIdx == 1 && action.getAffectedRow() < 2)
                                    || (currentPlayerIdx == 2 && action.getAffectedRow() > 1)) {
                                result.put("affectedRow", action.getAffectedRow());
                                result.put("error", "Selected row does not belong to the current player.");
                                output.add(result);
                                break;
                            }

                            currentHero.setUsedAbility(true);
                            game.getCurrentPlayer().decreaseMana(currentHero.getMana());

                            if (Objects.equals(currentHero.getName(), "King Mudface")) {
                                // Creștere viață pentru cărți
                                for (Cards card : startGame.getTable().get(action.getAffectedRow())) {
                                    card.increaseHealth(1);
                                }
                            } else {
                                // Creștere atac pentru cărți
                                for (Cards card : startGame.getTable().get(action.getAffectedRow())) {
                                    card.decreaseAttackDamage(-1);
                                }
                            }
                        }
                        break;


                    case "getFrozenCardsOnTable":
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
            game.getPlayerOne().getHand().clear();
            game.getPlayerTwo().getHand().clear();
            for (ArrayList<Cards> row : startGame.getTable()) {
                row.clear();
            }
        }

        // Custom pretty printer to remove space before colon
        DefaultPrettyPrinter prettyPrinter = new DefaultPrettyPrinter() {
            @Override
            public DefaultPrettyPrinter createInstance() {
                return new DefaultPrettyPrinter(this);
            }

            @Override
            public DefaultPrettyPrinter withSeparators(final Separators separators) {
                this._separators = separators;
                this._objectFieldValueSeparatorWithSpaces = String.valueOf(separators.getObjectFieldValueSeparator());
                return this;
            }
        }.withArrayIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE)
                .withObjectIndenter(DefaultIndenter.SYSTEM_LINEFEED_INSTANCE);

        ObjectWriter objectWriter = objectMapper.writer(prettyPrinter);
        objectWriter.writeValue(new File(filePath2), output);
    }
}
