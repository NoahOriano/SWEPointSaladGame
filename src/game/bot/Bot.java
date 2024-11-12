package game.bot;

/**
 * Bot which contains the logic for non-human players.
 * Bots should be able to determine their actions based on the current game state.
 * The server will call getAction for a given player ID,
 * the bot will return the expected action for that player as a String.
 */
public interface Bot {
    /**
     * Get the expected AI action for given player
     **/
    String getAction(int Player);
}
