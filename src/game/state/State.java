package game.state;

import network.Message;

/**
 * Generic game state to be used by the server
 * The most important methods are handlePlayerAction and isGameOver, along with the message methods
 * The server will call handlePlayerAction for each player in turn until the game is over
 * The server will also call the message methods to retrieve messages to send to the players
 * In this way the server does not need to know any implementation details of the game
 **/
public interface State {

    /**
     * Handle player action, returning true if successful false otherwise
     **/
    boolean handlePlayerAction(int player, String action);

    /**
     * Get the current player
     **/
    int getCurrentPlayer();

    /**
     * Get the number of players
     **/
    int getNumPlayers();

    /**
     * Check if the game is over
     **/
    boolean isGameOver();

    /**
     * Gets the message to be sent to everyone or null for nothing
     **/
    Message getPublicMessage();

    /**
     * Gets the message to be sent to a specific player on the current round or null for nothing
     **/
    Message getPlayerMessage(int player);
}
