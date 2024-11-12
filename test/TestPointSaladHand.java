import game.cards.card.PointSaladCard;
import game.cards.hand.PointSaladHand;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Test class for the PointSaladHand class
 * The priority of this test to check the methods of the PointSaladHand class
 **/
public class TestPointSaladHand {
    private class Sut {
        private PointSaladHand hand;
        public Sut() {
            hand = new PointSaladHand();
        }
    }

    // Test that the hand is empty when created, that it is not empty after adding a card, and empty after removing it
    @Test
    public void testAddAndRemoveFromHand() {
        Sut sut = new Sut();
        assertTrue(sut.hand.isEmpty());
        PointSaladCard newCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "1 point per PEPPER");
        sut.hand.addCard(newCard);
        assertEquals(1, sut.hand.getNumCards());
        assertFalse(sut.hand.isEmpty());
        PointSaladCard removedCard = sut.hand.removeCard(newCard);
        assertEquals(newCard, removedCard);
        assertTrue(sut.hand.isEmpty());
    }

    @Test
    public void testStringRepresentation() {
        Sut sut = new Sut();
        PointSaladCard newCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "1 point per PEPPER");
        newCard.criteriaSideUp = false;
        sut.hand.addCard(newCard);
        Assert.assertTrue(sut.hand.stringifyHand().contains("PEPPER"));
    }
}