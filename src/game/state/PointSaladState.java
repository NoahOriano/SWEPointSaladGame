package game.state;

import game.cards.card.PointSaladCard;
import game.cards.deck.PointSaladDeck;
import game.cards.hand.PointSaladHand;
import game.scoring.PointSaladScoring;
import game.scoring.Scoring;
import network.Message;

public class PointSaladState implements State {
    public final int GARDEN_WIDTH = 3;
    public final int GARDEN_HEIGHT = 2;

    private int currentPlayer;
    private final int numPlayers;
    private final PointSaladHand[] hands;
    private final PointSaladDeck deck;
    private final PointSaladCard[] criteriaCards;
    private final PointSaladCard[][] pointCards;

    public enum PHASE {TAKE_CARD, CONVERT_CARD}

    ;
    private PHASE currentPhase;
    boolean generalMessageSent;
    private Scoring<PointSaladHand> scoring;

    public PointSaladState(int numberOfPlayers) {
        criteriaCards = new PointSaladCard[GARDEN_WIDTH];
        pointCards = new PointSaladCard[GARDEN_WIDTH][GARDEN_HEIGHT];
        hands = new PointSaladHand[numberOfPlayers];
        for (int i = 0; i < numberOfPlayers; i++) {
            hands[i] = new PointSaladHand();
        }
        deck = new PointSaladDeck(numberOfPlayers);
        this.numPlayers = numberOfPlayers;
        this.currentPlayer = (int) (Math.random() * (numberOfPlayers)); // Random player starts
        this.currentPhase = PHASE.TAKE_CARD;
        this.generalMessageSent = false;
        this.scoring = new PointSaladScoring();
        initializeGarden();
    }

    /**
     * Initializes the garden with criteria and veggie cards
     **/
    private void initializeGarden() {
        // Go through each criterion and point card location and draw from the deck
        for (int i = 0; i < GARDEN_WIDTH; i++) {
            criteriaCards[i] = deck.draw();
            for (int j = 0; j < GARDEN_HEIGHT; j++) {
                pointCards[i][j] = deck.draw();
                pointCards[i][j].criteriaSideUp = false;
            }
        }
    }

    public PHASE getGamePhase() {
        return this.currentPhase;
    }

    @Override
    public boolean handlePlayerAction(int player, String action) {
        System.out.println("Player " + player + " action: " + action);
        if (action == null) return false;
        boolean actionSuccess = false;
        if (currentPhase == PHASE.TAKE_CARD) {
            actionSuccess = handleTakeCardAction(player, action);
        } else if (currentPhase == PHASE.CONVERT_CARD) {
            actionSuccess = handleConvertCardAction(player, action);
        }
        if (actionSuccess) {
            progressPhase();
        }
        return actionSuccess;
    }

    /**
     * Swaps the phase and progresses the game to the next player if necessary, called after a successful action
     **/
    private void progressPhase() {
        generalMessageSent = false;
        if (currentPhase == PHASE.TAKE_CARD) {
            swapPhase();
        } else {
            nextPlayer();
            swapPhase();
        }
    }

    /**
     * Handles the action of taking a card
     * Handles both taking criteria and point cards and incorrect inputs
     * Returns true if the action was successful, false otherwise
     **/
    private boolean handleTakeCardAction(int player, String action) {
        action = action.trim().toLowerCase();
        // Replace all characters that are not 0-2 or a-f with empty string
        action = action.replaceAll("[^0-2a-f]", "");
        if (action.length() == 0 || action.length() > 2) {
            return false;
        }
        if (action.charAt(0) <= '9' && action.charAt(0) >= '0') { // Action is to take criteria
            return playerTakeCriteriaCard(player, Integer.parseInt(action));
        } else if (action.charAt(0) <= 'z' && action.charAt(0) >= 'a') { // Action is to take point card
            // Record the first action/card taken
            boolean firstSuccess = false;
            int row = (action.charAt(0) - 'a') / GARDEN_WIDTH;
            int col = (action.charAt(0) - 'a') % GARDEN_WIDTH;
            firstSuccess = playerTakeVeggieCard(player, col, row);
            // Try to take second card if that was requested
            if (action.length() == 2 && action.charAt(1) <= 'z' && action.charAt(1) >= 'a') {
                row = (action.charAt(1) - 'a') / GARDEN_WIDTH;
                col = (action.charAt(1) - 'a') % GARDEN_WIDTH;
                // Ensures that the second card is taken only if the first card was successfully taken
                return firstSuccess && playerTakeVeggieCard(player, col, row);
            } else {
                return firstSuccess;
            }
        }
        return false;
    }

    /**
     * Handles the action of converting cards
     * Returns true if the action was successful, false otherwise
     **/
    private boolean handleConvertCardAction(int player, String action) {
        if (action.contains("n") || action.contains("N")) {
            return true;
        }
        action = action.trim().toLowerCase();
        action = action.replaceAll("[^0-9]", "");
        // Find the number of criteria cards the player has
        int totalCards = hands[player].countTotalCriteria() + hands[player].countTotalVegetables();
        // Should only remove one card. When a card is removed, false is returned to get more input
        try {
            int cardIndex = Integer.parseInt(action);
            if (cardIndex < 0 || cardIndex >= totalCards) {
                return false;
            }
            // Get the card to be converted
            PointSaladCard card = hands[player].getCard(cardIndex);
            // Convert the card into a veggie card.
            card.criteriaSideUp = false;
        } catch (NumberFormatException e) {
            return false;
        }
        return false;
    }

    /**
     * Used by bots only to determine the criteria cards in the garden
     */
    public PointSaladCard[] getCriteriaCards() {
        return criteriaCards;
    }

    /**
     * Used by bots only to determine the veggie cards in the garden
     */
    public PointSaladCard[][] getVeggieCards() {
        return pointCards;
    }

    /**
     * Used by bots to determine the points gained from each point card and their current hand
     */
    public PointSaladHand[] getHands() {
        return hands;
    }

    /**
     * Take a criteria card from the garden
     * Validity should be checked before called
     **/
    private boolean playerTakeCriteriaCard(int player, int criteriaCardIndex) {
        // Check that the row and col are valid
        if (criteriaCardIndex < 0 || criteriaCardIndex >= GARDEN_WIDTH) {
            return false;
        }
        // Take the card into the player's hand
        if (criteriaCards[criteriaCardIndex] != null) {
            hands[player].addCard(criteriaCards[criteriaCardIndex]);
            criteriaCards[criteriaCardIndex] = deck.draw();
            return true;
        }
        return false;
    }

    /**
     * Take a point card from the garden
     * Validity should be checked before called
     **/
    private boolean playerTakeVeggieCard(int player, int col, int row) {
        // Check that the row and col are valid
        if (col < 0 || col >= GARDEN_WIDTH || row < 0 || row >= GARDEN_HEIGHT) {
            return false;
        }
        // Take the card into the player's hand
        if (pointCards[col][row] != null) {
            hands[player].addCard(pointCards[col][row]);
            pointCards[col][row] = criteriaCards[col];
            // If the criteria card was null, the deck is empty and there is no card to flip
            if (pointCards[col][row] != null) {
                pointCards[col][row].criteriaSideUp = false;
                criteriaCards[col] = deck.draw();
            }
            return true;
        }
        return false;
    }

    /**
     * Progresses the game to the next player
     **/
    private void nextPlayer() {
        this.currentPlayer = (this.currentPlayer + 1) % this.numPlayers;
    }

    /**
     * Swaps the current phase between taking cards and converting cards
     **/
    private void swapPhase() {
        if (currentPhase == PHASE.TAKE_CARD) {
            currentPhase = PHASE.CONVERT_CARD;
        } else {
            currentPhase = PHASE.TAKE_CARD;
        }
    }

    /**
     * Gets the points of a player
     **/
    private int getPlayerPoints(int player) {
        return scoring.calculateScoresOfHands(hands)[player];
    }

    @Override
    public int getCurrentPlayer() {
        return this.currentPlayer;
    }

    @Override
    public int getNumPlayers() {
        return this.numPlayers;
    }

    @Override
    public boolean isGameOver() {
        // Check if there are any remaining cards to retrieve
        boolean gameOver = true;
        for (int i = 0; i < GARDEN_WIDTH; i++) {
            for (int j = 0; j < GARDEN_HEIGHT; j++) {
                if (pointCards[i][j] != null) {
                    gameOver = false;
                    break;
                }
            }
            if (criteriaCards[i] != null) {
                gameOver = false;
            }
        }
        return gameOver;
    }

    /**
     * Determines if the given player is winning, or tied for winning
     **/
    private boolean playerWinning(int player) {
        // Uses the scoring class to determine the winner by finding the player with the most points
        int maxPoints = 0;
        for (int i = 0; i < numPlayers; i++) {
            int playerPoints = getPlayerPoints(i);
            if (playerPoints > maxPoints) {
                maxPoints = playerPoints;
            }
        }
        return getPlayerPoints(player) == maxPoints;
    }

    /**
     * Determines the winning player by finding the player with the most points
     * If there is a tie, the first player with the lower player number wins.
     **/
    private int winningPlayer() {
        int[] playerScores = scoring.calculateScoresOfHands(hands);
        int maxPoints = 0;
        int winningPlayer = 0;
        for (int i = 0; i < numPlayers; i++) {
            if (playerScores[i] > maxPoints) {
                maxPoints = playerScores[i];
                winningPlayer = i;
            }
        }
        return winningPlayer;
    }

    /**
     * Gets the message to be sent to everyone at the end of the game, showing the winner and their score
     **/
    private String getGameEndPublicString() {
        return "\nPlayer " + winningPlayer() + " won with " + getPlayerPoints(winningPlayer()) + " points!\n"
                + getPublicPlayerInformation();
    }

    /**
     * Gets the message to be sent to the player at the end of the game, showing their score and if they won or lost
     **/
    private String getGameEndPlayerString(int player) {
        String ret = "";
        if (playerWinning(player)) {
            ret = "Congratulations! You won with " + getPlayerPoints(player) + " points!";
        } else {
            ret = "Sorry, you lost. Better luck next time!";
        }
        ret += "\nYour score was: " + getPlayerPoints(player);
        return ret;
    }

    /**
     * Gets the public message to be sent to everyone. This is always a NO_ACTION message
     **/
    @Override
    public Message getPublicMessage() {
        // Only want to send a general message once per round (player change)
        if (!generalMessageSent) {
            this.generalMessageSent = true;
            if (isGameOver()) {
                return new Message(getGameEndPublicString(), Message.MESSAGE_TYPE.NO_ACTION);
            }
            if (currentPhase == PHASE.TAKE_CARD) {
                return new Message(getPublicPlayerInformation() + getGameDeckInformation(),
                        Message.MESSAGE_TYPE.NO_ACTION);
            } else {
                return null;
            }
        }
        return null;
    }

    @Override
    public Message getPlayerMessage(int player) {
        if (isGameOver()) {
            return new Message(getGameEndPlayerString(player), Message.MESSAGE_TYPE.GAME_OVER);
        }
        if (player == currentPlayer) {
            String message = "";
            if (currentPhase == PHASE.TAKE_CARD) {
                message = getPlayerHand(player) + getGameDeckInformation() + "\nChoose which cards to take. Ex: '1' or 'AB'";
            } else {
                message = getPlayerHand(player) + "\nPick cards to convert or n for none. Ex: '13' or 'n'";
            }
            return new Message(message, Message.MESSAGE_TYPE.ACTION_REQUESTED);
        }
        return null;
    }

    /**
     * For the Point Salad Game, every player knows each-others hands
     **/
    private String getPublicPlayerInformation() {
        StringBuilder playerInfoStringBuilder = new StringBuilder();
        int[] playerScores = scoring.calculateScoresOfHands(hands);
        playerInfoStringBuilder.append("Player Hands:");
        for (int i = 0; i < numPlayers; i++) {
            playerInfoStringBuilder.append("\nPlayer ").append(i).append(" (").append(playerScores[i])
                    .append(" points):\n").append(hands[i].stringifyHand()).append("\n");
        }
        return playerInfoStringBuilder.toString();
    }

    private String getPlayerHand(int player) {
        return "\nYour hand:\t" + hands[player].stringifyHand();
    }

    /**
     * For the Point Salad Game, the deck contains information about the criteria and veggie cards
     **/
    private String getGameDeckInformation() {
        StringBuilder deckStringBuilder = new StringBuilder();
        deckStringBuilder.append("\nCriteria Cards:\t");
        for (int i = 0; i < GARDEN_WIDTH; i++) {
            if (criteriaCards[i] != null) {
                deckStringBuilder.append("[").append(i).append("]  ").append(criteriaCards[i].toString()).append("\t");
            } else {
                deckStringBuilder.append("[").append(i).append("]  ").append("Empty").append("\t");
            }
        }
        deckStringBuilder.append("\nVeggie Cards:\t");
        for (int row = 0; row < GARDEN_HEIGHT; row++) {
            for (int col = 0; col < GARDEN_WIDTH; col++) {
                if (pointCards[col][row] != null) {
                    deckStringBuilder.append("[").append((char) ('A' + col + (row * GARDEN_WIDTH))).append("] ")
                            .append(pointCards[col][row].toString()).append("\t");
                } else {
                    deckStringBuilder.append("[").append((char) ('A' + col + (row * GARDEN_WIDTH)))
                            .append("] ").append("Empty").append("\t");
                }
            }
        }
        return deckStringBuilder.toString();
    }
}
