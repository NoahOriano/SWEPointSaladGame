import game.cards.card.PointSaladCard;
import game.cards.deck.PointSaladDeck;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

/**
 * Test class for the PointSaladDeck class
 * The priority of the tests are to ensure that the deck is initialized correctly with the correct number of cards,
 **/
public class TestPointSaladDeck {

    @Test // Test that the deck is initialized with the correct number of cards
    public void testPointSaladDeckSize() {
        PointSaladDeck deck;
        for (int i = 2; i < 6; i++) {
            deck = new PointSaladDeck(i);
            assertEquals(i * 18, deck.getNumCards()); // 3 cards per veggie, 6 veggies
        }
    }

    @Test // Test that the deck is shuffled when shuffle is called
    public void testPointSaladDeckShuffle() {
        PointSaladDeck deck = new PointSaladDeck(2);
        deck.shuffle();
        assertEquals(36, deck.getNumCards());
        PointSaladCard topCard = deck.getCard(0);
        int shuffleCount = 0;
        while (topCard == deck.getCard(0) && shuffleCount < 20) { // Technically a 1 in 36^20 chance of false failure
            deck.shuffle();
            shuffleCount++;
        }
        assertNotEquals(topCard, deck.getCard(0));
    }

    @Test // Test that draw() draws the top card and removes it from the deck
    public void testPointSaladDeckDraw() {
        PointSaladDeck deck = new PointSaladDeck(2);
        PointSaladCard topCard = deck.getCard(0);
        PointSaladCard drawnCard = deck.draw();
        assertEquals(topCard, drawnCard);
        assertEquals(35, deck.getNumCards());
    }

    @Test // Test that the number of each veggie card is correct
    public void testPointSaladDeckVeggieCount() {
        PointSaladDeck deck = new PointSaladDeck(3);
        int[] veggieCounts = new int[6];
        for (int i = 0; i < deck.getNumCards(); i++) {
            PointSaladCard card = deck.getCard(i);
            switch (card.vegetable) {
                case PEPPER -> veggieCounts[0]++;
                case TOMATO -> veggieCounts[1]++;
                case CARROT -> veggieCounts[2]++;
                case ONION -> veggieCounts[3]++;
                case LETTUCE -> veggieCounts[4]++;
                case CABBAGE -> veggieCounts[5]++;
            }
        }
        for (int i = 0; i < 6; i++) {
            assertEquals(9, veggieCounts[i]);
        }
    }
}