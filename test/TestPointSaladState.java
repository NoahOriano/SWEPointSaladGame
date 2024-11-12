import game.cards.card.PointSaladCard;
import game.state.PointSaladState;
import org.junit.Assert;
import org.junit.Test;

/**
 * Test class for the PointSaladState class
 * Tests that state is initialized correctly and ensures action validity, alongside game termination
 **/
public class TestPointSaladState {
    @Test
    public void testPointSaladStateInitialization() {
        PointSaladState state = new PointSaladState(6);
        // Check basic game state values
        Assert.assertEquals(6, state.getNumPlayers());
        Assert.assertEquals(3, state.GARDEN_WIDTH);
        Assert.assertEquals(2, state.GARDEN_HEIGHT);
        Assert.assertFalse(state.isGameOver());

        // Check that the garden and criteria are initialized
        PointSaladCard[][] garden = state.getVeggieCards();
        for(int i = 0; i < state.GARDEN_WIDTH; i++) {
            for(int j = 0; j < state.GARDEN_HEIGHT; j++) {
                Assert.assertNotNull(garden[i][j]);
            }
        }
        PointSaladCard[] criteria = state.getCriteriaCards();
        for (PointSaladCard criterion : criteria) {
            Assert.assertNotNull(criterion);
        }

        // Check that the hands are initialized and empty
        for(int i = 0; i < state.getNumPlayers(); i++) {
            Assert.assertEquals(0, state.getHands()[i].countTotalCriteria());
            Assert.assertEquals(0, state.getHands()[i].countTotalVegetables());
        }
    }

    @Test
    public void testPointSaladGameTermination(){
        PointSaladState state = new PointSaladState(6);
        Assert.assertFalse(state.isGameOver());
        // Number of maximum turns to simulate. Set to 40 turns per player (very high).
        // If the game is not over after this many turns, it is stuck in an infinite loop.
        int count = 240;

        // Players will only draw one card per turn, never convert, and the game will end when the deck is empty
        String[] actions = {"A", "B", "C", "D", "E", "F"};
        int currentAction = 0;
        while(!state.isGameOver()){
            count--;
            if(count == 0){
                Assert.fail("Game did not terminate after 240 turns");
            }
            // Actions rotate to ensure that each garden spot is chosen
            state.handlePlayerAction(state.getCurrentPlayer(), actions[currentAction]);
            currentAction = (currentAction + 1) % 6;
            // N for no conversion
            state.handlePlayerAction(state.getCurrentPlayer(), "N");
        }
        Assert.assertTrue(state.isGameOver());
    }

    @Test
    public void testPointSaladGameActionValidity(){
        PointSaladState state = new PointSaladState(6);
        // Check that the game is in a valid state
        Assert.assertFalse(state.isGameOver());
        Assert.assertEquals(PointSaladState.PHASE.TAKE_CARD, state.getGamePhase());
        int cur = state.getCurrentPlayer();
        // Three valid locations to take a card from is invalid, must be 1-2
        Assert.assertFalse(state.handlePlayerAction(cur, "ABC"));
        // N is not valid in the take card phase (can't take zero cards)
        Assert.assertFalse(state.handlePlayerAction(cur, "N"));
        // Z is not a valid card location
        Assert.assertFalse(state.handlePlayerAction(cur, "Z"));
        // Now a valid action, AB
        Assert.assertTrue(state.handlePlayerAction(cur, "AB"));

        // Check that the game is in a valid state
        Assert.assertEquals(PointSaladState.PHASE.CONVERT_CARD, state.getGamePhase());
        // A is not a valid action in the convert card phase
        Assert.assertFalse(state.handlePlayerAction(cur, "A"));
        // N is a valid action in the convert card phase
        Assert.assertTrue(state.handlePlayerAction(cur, "N"));
    }

    @Test
    public void testGetMessage() {
        PointSaladState state = new PointSaladState(6);
        // Test that the game start public message shows the garden and criteria cards
        String message = state.getPublicMessage().message();
        Assert.assertTrue(message.contains("Criteria Cards"));
        Assert.assertTrue(message.contains("Veggie Cards"));
        // Test that only the current player gets a player message
        int cur = state.getCurrentPlayer();
        for(int i = 0; i < state.getNumPlayers(); i++) {
            if(i == cur) {
                Assert.assertNotNull(state.getPlayerMessage(i));
            } else {
                Assert.assertNull(state.getPlayerMessage(i));
            }
        }
    }
}
