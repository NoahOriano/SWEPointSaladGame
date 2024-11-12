import game.cards.card.PointSaladCard;
import game.cards.deck.PointSaladDeck;
import game.cards.hand.PointSaladHand;
import game.scoring.PointSaladScoring;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * Test class for the PointSaladScoring class
 * This is by far the most robust test class, as it tests the scoring of the criteria cards.
 * The priority of this test is to ensure that the scoring of the criteria cards is correct.
 * The tests are broken down by the way they are grouped in the scoring function, and labeled by their relevant IDs.
 **/
public class TestPointSaladScoring {

    // Note: order of veggies is defined in PointSaladCard.Vegetable
    // The order is: PEPPER, LETTUCE, CARROT, CABBAGE, ONION, TOMATO

    /**
     * Helper function to generate a hand with the given number of each veggie
     */
    private PointSaladHand generateHandWithVeggies(int[] veggieCounts) {
        // Generate an array of veggies from veggie counts
        // Then fill it with a veggie for each count of each veggie
        ArrayList<PointSaladCard.Vegetable> veggies = new ArrayList<>();
        for (int i = 0; i < veggieCounts.length; i++) {
            for (int j = 0; j < veggieCounts[i]; j++) {
                veggies.add(PointSaladCard.Vegetable.values()[i]);
            }
        }

        PointSaladHand hand = new PointSaladHand();
        for (PointSaladCard.Vegetable veggie : veggies) {
            PointSaladCard newCard = new PointSaladCard(veggie, null);
            newCard.criteriaSideUp = false;
            hand.addCard(newCard);
        }
        return hand;
    }

    /**
     * Check that every criteria card is accounted for by creating a deck
     * of all possible cards and checking that each card is present.
     * Since I didn't want the scoring to throw exceptions this checks logs
     * for errors (which are printed if the criteria card is not found).
     */
    @Test
    public void testAllCriteriaCardsAccountedFor() {
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream originalOut = System.out;

        // Setup custom output stream to catch logging
        System.setOut(new PrintStream(outContent));

        // If a card is missing from the scoring, an error will be logged
        PointSaladDeck deck = new PointSaladDeck();
        deck.initializeDeck(6);
        PointSaladHand hand = new PointSaladHand();
        for (PointSaladCard card : deck.getDeck()) {
            card.criteriaSideUp = true;
            hand.addCard(card);
        }
        PointSaladScoring scoring = new PointSaladScoring();
        scoring.calculateScoresOfHands(new PointSaladHand[]{hand});

        assertFalse(outContent.toString().contains("ERROR"));

        // Restore streams
        System.setOut(originalOut);
    }

    /**
     * ID 4, 8, 9, 10, 14, 15, 16, 17 : X/VEGGIE
     * These are grouped since the function for determining score
     * is the same for all of them regardless of veggies per card
     */
    @Test
    public void testSingleVeggieScoring() {
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{3, 4, 2, 3, 1, 1}); // Four lettuce
        PointSaladCard scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "2 / LETTUCE");
        scoringCard.criteriaSideUp = true;
        hand1.addCard(scoringCard);
        PointSaladScoring scoring = new PointSaladScoring();
        assertEquals(8, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
        PointSaladHand hand2 = generateHandWithVeggies(new int[]{3, 0, 2, 3, 1, 1}); // Zero lettuce
        hand2.addCard(scoringCard);
        assertEquals(0, scoring.calculateScoresOfHands(new PointSaladHand[]{hand2})[0]);
    }

    /** ID 18 : COMPLETE SET = 12 */
    @Test
    public void testCompleteSetScoring() {
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{3, 4, 2, 3, 2, 2}); // Two Sets
        PointSaladCard scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "COMPLETE SET = 12");
        scoringCard.criteriaSideUp = true;
        hand1.addCard(scoringCard);
        PointSaladScoring scoring = new PointSaladScoring();
        assertEquals(24, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]); // Two Sets
    }

    /**
     * ID 11,12,13 : a + b + c = 8
     */
    @Test
    public void testGroupOfThreeDifferentScoring() {
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{0, 2, 2, 3, 2, 2}); // Two Complete trios
        PointSaladCard scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "TOMATO + LETTUCE + CARROT = 8");
        scoringCard.criteriaSideUp = true;
        hand1.addCard(scoringCard);
        PointSaladScoring scoring = new PointSaladScoring();
        assertEquals(16, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
    }

    @Test
    public void testGroupOfThreeSameScoring() {
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{0, 3, 0, 1, 2, 2}); // Three lettuce
        PointSaladCard scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "LETTUCE + LETTUCE + LETTUCE = 8");
        scoringCard.criteriaSideUp = true;
        hand1.addCard(scoringCard);
        PointSaladScoring scoring = new PointSaladScoring();
        assertEquals(8, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
    }

    /**
     * ID 5,6,7 : a + b = 5
     */
    @Test
    public void testGroupOfTwoScoring() {
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{0, 2, 2, 0, 0, 0}); // Two Complete pairs
        PointSaladHand hand2 = generateHandWithVeggies(new int[]{2, 0, 2, 5, 4, 3}); // Zero Complete pairs
        PointSaladCard scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "LETTUCE + CARROT = 6");
        scoringCard.criteriaSideUp = true;
        hand1.addCard(scoringCard);
        hand2.addCard(scoringCard);
        PointSaladScoring scoring = new PointSaladScoring();
        assertEquals(12, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
        assertEquals(0, scoring.calculateScoresOfHands(new PointSaladHand[]{hand2})[0]);
    }

    /**
     * ID 18 : MOST/FEWEST TOTAL VEGETABLE = x
     * ID 1 : MOST a = x
     * ID 2 : FEWEST a = x
     */
    @Test
    public void testMostAndFewestScoring() {
        // Create 3 hands of veggies, each with a different number of each veggie
        // Zero Peppers, most Lettuce, most total veggies
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{0, 5, 5, 5, 5, 2});
        // Zero Lettuce, most Peppers
        PointSaladHand hand2 = generateHandWithVeggies(new int[]{5, 0, 3, 3, 2, 2});
        // Zero Carrots, fewest total veggies, tied for most Cabbage
        PointSaladHand hand3 = generateHandWithVeggies(new int[]{2, 2, 0, 5, 2, 2});
        PointSaladCard criteriaCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "MOST LETTUCE = 10");
        criteriaCard.criteriaSideUp = true;
        hand1.addCard(criteriaCard); // Hand1 should now score at 10
        criteriaCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "MOST TOTAL VEGETABLE = 10");
        hand1.addCard(criteriaCard); // Hand1 should now score at 20
        criteriaCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "FEWEST TOTAL VEGETABLE = 7");
        hand1.addCard(criteriaCard); // Hand1 should now score at 20 still
        criteriaCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "MOST PEPPER = 10");
        criteriaCard.criteriaSideUp = true;
        hand2.addCard(criteriaCard); // Hand2 should now score at 10
        criteriaCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "MOST CABBAGE = 10");
        criteriaCard.criteriaSideUp = true;
        hand3.addCard(criteriaCard); // Hand3 should now score at 10
        criteriaCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "FEWEST TOTAL VEGETABLE = 7");
        hand3.addCard(criteriaCard); // Hand3 should now score at 17
        criteriaCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "FEWEST CARROT = 7");
        hand3.addCard(criteriaCard); // Hand3 should now score at 24
        PointSaladScoring scoring = new PointSaladScoring();
        int[] scores = scoring.calculateScoresOfHands(new PointSaladHand[]{hand1, hand2, hand3});
        assertEquals(20, scores[0]);
        assertEquals(10, scores[1]);
        assertEquals(24, scores[2]);
    }

    /**
     * ID 18 x / MISSING VEGETABLE TYPE
     */
    @Test
    public void testMissingVeggieType() {
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{0, 2, 0, 3, 0, 2}); // Three missing types
        PointSaladCard scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "5 / MISSING VEGETABLE TYPE");
        scoringCard.criteriaSideUp = true;
        hand1.addCard(scoringCard);
        PointSaladScoring scoring = new PointSaladScoring();
        assertEquals(15, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
    }

    /**
     * ID 18 x / VEGETABLE TYPE >=y
     */
    @Test
    public void testGreaterThanScoring() {
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{0, 2, 3, 3, 0, 2}); // Four >=2, two >=3
        PointSaladCard scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "5 / VEGETABLE TYPE >=3");
        scoringCard.criteriaSideUp = true;
        hand1.addCard(scoringCard);
        PointSaladScoring scoring = new PointSaladScoring();
        assertEquals(10, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
        scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "3 / VEGETABLE TYPE >=2");
        hand1.addCard(scoringCard);
        assertEquals(22, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
    }

    /** ID 3 : VEGGIE: EVEN=7, ODD=3 */
    @Test
    public void testEvenOddScoring(){
        // Even lettuce, odd carrots
        PointSaladHand hand1 = generateHandWithVeggies(new int[]{0, 4, 3, 3, 0, 2});
        PointSaladCard scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "LETTUCE: EVEN=7, ODD=3");
        scoringCard.criteriaSideUp = true;
        hand1.addCard(scoringCard);
        PointSaladScoring scoring = new PointSaladScoring();
        assertEquals(7, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
        scoringCard = new PointSaladCard(PointSaladCard.Vegetable.PEPPER, "CARROT: EVEN=7, ODD=3");
        hand1.addCard(scoringCard);
        assertEquals(10, scoring.calculateScoresOfHands(new PointSaladHand[]{hand1})[0]);
    }
}
