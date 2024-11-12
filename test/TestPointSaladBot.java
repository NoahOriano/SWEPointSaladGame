import game.bot.PointSaladBot;
import game.state.PointSaladState;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for PointSaladBot
 * Tests that the output of the bot is valid.
 */
public class TestPointSaladBot {
    /**
     * Plays a game with the just bots and expects it to terminate.
     */
    @Test
    public void testBotThroughGamePlay() {
        PointSaladState state = new PointSaladState(3);
        PointSaladBot bot = new PointSaladBot(state);
        // Number of maximum turns to simulate.
        // If the game is not over after this many turns, it is stuck in an infinite loop.
        int count = 120;

        while(!state.isGameOver()){
            count--;
            if(count == 0){
                Assert.fail("Game did not terminate after 240 turns");
            }
            state.handlePlayerAction(state.getCurrentPlayer(), bot.getAction(state.getCurrentPlayer()));
            // N for no conversion
            state.handlePlayerAction(state.getCurrentPlayer(), "N");
        }
        Assert.assertTrue(state.isGameOver());
    }
}
