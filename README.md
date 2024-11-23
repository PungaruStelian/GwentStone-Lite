# GwentStone-Lite

## Description

This project implements a card game where two players compete against each other. Each player selects a deck, which is shuffled at the beginning of the game to ensure unpredictability. The game is deterministic, with a seed provided for the shuffling process. Players are assigned heroes randomly, and the starting player is specified in the input. The more detailed statement can be found: https://ocw.cs.pub.ro/courses/poo-ca-cd/teme/2024/tema

## Game Setup

1. **Deck Selection and Shuffling**:
   - Each player selects a deck specified in the input.
   - The selected deck is shuffled using the `Random` class with a provided seed to ensure deterministic results.

2. **Hero Assignment**:
   - Each player is assigned a hero randomly, as specified in the input.

3. **Starting Player**:
   - The player who starts the first round is specified in the input.

## Game Rounds

### Beginning of a Round

- At the start of each round, both players draw the first available card from their shuffled deck and add it to their hand.
- Players receive mana at the beginning of each round, starting with 1 mana in the first round and incrementing by 1 each round up to a maximum of 10 mana.

### Player Actions

During a player's turn, they can perform multiple actions:

1. **Placing a Card on the Table**:
   - Players can place cards from their hand onto the table if they have enough mana.
   - Cards must be placed on the appropriate row based on the card type.
   - Variables:
      - `handIdx`: Index of the card in the player's hand.
      - `row`: The row on the table where the card will be placed.
      - `mana`: The mana cost of the card.
   - Invalid actions are checked and appropriate error messages are printed.

2. **Attacking with a Card**:
   - Cards on the table can attack enemy cards.
   - The attack reduces the health of the targeted card by the attacker's damage.
   - Variables:
      - `cardAttacker`: Coordinates of the attacking card.
      - `cardAttacked`: Coordinates of the attacked card.
      - `attackDamage`: The damage dealt by the attacking card.
      - `health`: The health of the attacked card.
   - Invalid actions are checked and appropriate error messages are printed.

3. **Using a Card's Ability**:
   - Cards on the table can use their special abilities on other cards.
   - Each ability has a different effect.
   - Variables:
      - `cardAttacker`: Coordinates of the card using the ability.
      - `cardAttacked`: Coordinates of the target card.
      - `ability`: The specific ability being used.
   - Invalid actions are checked and appropriate error messages are printed.

4. **Attacking the Enemy Hero**:
   - Cards on the table can attack the enemy hero, reducing their health.
   - If the hero's health drops to 0 or below, the game ends, and the attacking player wins.
   - Variables:
      - `cardAttacker`: Coordinates of the attacking card.
      - `hero`: The enemy hero being attacked.
      - `attackDamage`: The damage dealt to the hero.
   - Invalid actions are checked and appropriate error messages are printed.

5. **Using a Hero's Ability**:
   - Heroes can use their abilities on a row of cards on the table.
   - Each hero has a unique ability.
   - Variables:
      - `hero`: The hero using the ability.
      - `affectedRow`: The row of cards affected by the ability.
      - `ability`: The specific ability being used.
   - Invalid actions are checked and appropriate error messages are printed.

### End of a Turn

- At the end of a player's turn, their frozen cards are unfrozen.
- If both players have ended their turns, the round ends, and a new round begins with the steps described above.

## Debug Commands

The server can request various parameters to verify the implementation:

1. **Getting Cards in Hand**:
   - Outputs all cards in the specified player's hand.
   - Variables:
      - `playerIdx`: Index of the player whose hand is being queried.

2. **Getting Cards in Deck**:
   - Outputs all cards in the specified player's deck.
   - Variables:
      - `playerIdx`: Index of the player whose deck is being queried.

3. **Displaying Cards on the Table**:
   - Outputs all cards present on the table, row by row.

4. **Getting the Active Player**:
   - Outputs the current active player.

5. **Getting a Player's Hero**:
   - Outputs the hero of the specified player.
   - Variables:
      - `playerIdx`: Index of the player whose hero is being queried.

6. **Displaying a Card at a Given Position**:
   - Outputs the card at the specified position on the table or an error message if no card is present.
   - Variables:
      - `x`: Row index on the table.
      - `y`: Column index on the table.

7. **Getting a Player's Mana**:
   - Outputs the current mana of the specified player.
   - Variables:
      - `playerIdx`: Index of the player whose mana is being queried.

8. **Displaying Frozen Cards on the Table**:
   - Outputs all cards on the table that are frozen.

## Statistics Commands

1. **Total Games Played**:
   - Outputs the total number of games played.

2. **Total Games Won**:
   - Outputs the total number of games won by each player.
   - Variables:
      - `playerIdx`: Index of the player whose wins are being queried.

## Conclusion

This project implements a comprehensive card game with various actions and commands, ensuring a deterministic and fair gameplay experience. The game logic handles all specified actions, checks for invalid actions, and provides appropriate error messages.