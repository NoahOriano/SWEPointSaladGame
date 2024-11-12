import game.cards.card.PointSaladCard;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

/**
 * Test class for the PointSaladCard class
 * The priority of this test is the toString method and with criteria side up and down
 **/
public class TestPointSaladCard {
    private class Sut {
        private PointSaladCard card;
        public Sut() {
            card = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "1 point per PEPPER");
        }
    }

    @Test
    public void testPointSaladCard() {
        Sut sut = new Sut();
        assertEquals(PointSaladCard.Vegetable.PEPPER, sut.card.vegetable);
        assertEquals("1 point per PEPPER", sut.card.criteria);
    }

    @Test
    public void testPointSaladCardToString() {
        Sut sut = new Sut();
        assertEquals("1 point per PEPPER (PEPPER)", sut.card.toString());
        sut.card.criteriaSideUp = false;
        assertEquals("PEPPER", sut.card.toString());
    }
}