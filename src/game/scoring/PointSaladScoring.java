package game.scoring;

import game.cards.card.PointSaladCard;
import game.cards.hand.PointSaladHand;

import java.util.ArrayList;

import static java.lang.Math.min;

/**
 * Scoring for the Point Salad game
 * IMPORTANT SCORING NOTES:
 * If multiple players have the most/least of a vegetable, the player with the scorecard
 * will get the points. Ex: two players have 0 lettuce, the least lettuce scorecard still scores.
 */
public class PointSaladScoring implements Scoring<PointSaladHand> {

    private final String[] veggies = {"PEPPER", "LETTUCE", "CARROT", "CABBAGE", "ONION", "TOMATO"};

    private int veggieToInt(String veggie) {
        return switch (veggie) {
            case "PEPPER" -> 0;
            case "LETTUCE" -> 1;
            case "CARROT" -> 2;
            case "CABBAGE" -> 3;
            case "ONION" -> 4;
            case "TOMATO" -> 5;
            default -> -1;
        };
    }

    /**
     * Calculate the scores of each hand
     * @param hands the hands to calculate the scores of
     * @return the scores of each hand
     */
    @Override
    public int[] calculateScoresOfHands(PointSaladHand[] hands) {
        // Count the number of each vegetable in each hand
        int[][] veggieCounts = new int[hands.length][6];
        for (int i = 0; i < hands.length; i++) {
            for (int j = 0; j < hands[i].getNumCards(); j++) {
                PointSaladCard card = hands[i].getCard(j);
                if (!card.criteriaSideUp) veggieCounts[i][card.vegetable.ordinal()]++;
            }
        }
        // Calculate the score of each hand
        int[] scores = new int[hands.length];
        for (int i = 0; i < hands.length; i++) {
            for (int j = 0; j < hands[i].getNumCards(); j++) {
                PointSaladCard card = hands[i].getCard(j);
                if (card.criteriaSideUp) {
                    String criteria = card.criteria;

                    // CASES FOR MOST AND FEWEST
                    if (criteria.contains("MOST")) {
                        if (criteria.contains("TOTAL")) {
                            scores[i] += scoreMostTotal(veggieCounts, i);
                        }
                        for (String veggie : veggies) {
                            if (criteria.contains(veggie)) {
                                scores[i] += scoreMostVeggie(veggieCounts, i, veggieToInt(veggie));
                            }
                        }
                    } else if (criteria.contains("FEWEST")) {
                        if (criteria.contains("TOTAL")) {
                            scores[i] += scoreFewestTotal(veggieCounts, i);
                        }
                        for (String veggie : veggies) {
                            if (criteria.contains(veggie)) {
                                scores[i] += scoreFewestVeggie(veggieCounts, i, veggieToInt(veggie));
                            }
                        }
                    }

                    // CASES FOR EVEN/ODD
                    else if (criteria.contains("EVEN")) {
                        for (String veggie : veggies) {
                            if (criteria.contains(veggie)) {
                                scores[i] += scoreEvenVeggie(veggieCounts, i, veggieToInt(veggie));
                            }
                        }
                    }

                    // CASES FOR THE UNIQUE CARDS (type and complete set)
                    else if (criteria.contains("TYPE")) {
                        if (criteria.contains(">=3")) {
                            scores[i] += scoreTypeGreaterEqualThree(veggieCounts, i);
                        } else if (criteria.contains(">=2")) {
                            scores[i] += scoreTypeGreaterEqualTwo(veggieCounts, i);
                        } else if (criteria.contains("MISSING")) {
                            scores[i] += scoreTypeMissing(veggieCounts, i);
                        }
                    } else if (criteria.contains("COMPLETE")) {
                        scores[i] += scoreCompleteSet(veggieCounts, i);
                    }

                    // CASES FOR STANDARD SCORING CARDS
                    // All that is left here is combined score + cards and standard x/veggie cards
                    else if (criteria.contains("/")) {
                        scores[i] += scoreIndividualVeggieCard(veggieCounts, i, criteria);
                    } else if (criteria.contains("+")) {
                        scores[i] += scoreCombinedVeggieCard(veggieCounts, i, criteria);
                    }
                    else {
                        // THIS SHOULD NOT BE REACHED
                        System.out.println("ERROR: Criteria not found");
                    }
                }
            }
        }
        return scores;
    }

    /**
     * Scoring for the most total veggies cards
     */
    private int scoreMostTotal(int[][] veggieCounts, int player) {
        boolean hasMost = true;
        int[] totalVeggies = new int[veggieCounts.length];
        for (int i = 0; i < veggieCounts.length; i++) {
            totalVeggies[i] = 0;
            for (int j = 0; j < veggieCounts[i].length; j++) {
                totalVeggies[i] += veggieCounts[i][j];
            }
        }
        for (int i = 0; i < veggieCounts.length; i++) {
            if (totalVeggies[i] > totalVeggies[player]) {
                hasMost = false;
                break;
            }
        }
        return hasMost ? 10 : 0; // Returns 10 points if the player has the most total veggies
    }

    /**
     * Scoring for the fewest total veggie card
     */
    private int scoreFewestTotal(int[][] veggieCounts, int player) {
        boolean hasFewest = true;
        int[] totalVeggies = new int[veggieCounts.length];
        for (int i = 0; i < veggieCounts.length; i++) {
            totalVeggies[i] = 0;
            for (int j = 0; j < veggieCounts[i].length; j++) {
                totalVeggies[i] += veggieCounts[i][j];
            }
        }
        for (int i = 0; i < veggieCounts.length; i++) {
            if (totalVeggies[i] < totalVeggies[player]) {
                hasFewest = false;
                break;
            }
        }
        return hasFewest ? 7 : 0; // Returns 7 points if the player has the fewest total veggies
    }

    /**
     * Scoring for the most of a specific veggie cards
     */
    private int scoreMostVeggie(int[][] veggieCounts, int player, int veggie) {
        boolean hasMost = true;
        for (int[] veggieCount : veggieCounts) {
            if (veggieCount[veggie] > veggieCounts[player][veggie]) {
                hasMost = false;
                break;
            }
        }
        return hasMost ? 10 : 0; // Returns 10 points if the player has the most of a specific veggie
    }

    /**
     * Scoring for the fewest of a specific veggie cards
     */
    private int scoreFewestVeggie(int[][] veggieCounts, int player, int veggie) {
        boolean hasFewest = true;
        for (int[] veggieCount : veggieCounts) {
            if (veggieCount[veggie] < veggieCounts[player][veggie]) {
                hasFewest = false;
                break;
            }
        }
        return hasFewest ? 7 : 0; // Returns 7 points if the player has the fewest of a specific veggie
    }

    /**
     * Scoring for the veggie odd/even cards
     */
    private int scoreEvenVeggie(int[][] veggieCounts, int player, int veggie) {
        boolean hasEven = veggieCounts[player][veggie] % 2 == 0;
        return hasEven ? 7 : 3; // Returns 7 points if even, 3 points if odd
    }

    /**
     * Scoring for the "5 / VEGETABLE TYPE >=3" card
     */
    private int scoreTypeGreaterEqualThree(int[][] veggieCounts, int player) {
        int count = 0;
        for (int i = 0; i < veggieCounts[player].length; i++) {
            if (veggieCounts[player][i] >= 3) {
                count++;
            }
        }
        return count * 5; // Returns 5 points for each type with 3 or more veggies
    }

    /**
     * Scoring for the "3 / VEGETABLE TYPE >=2" card
     */
    private int scoreTypeGreaterEqualTwo(int[][] veggieCounts, int player) {
        int count = 0;
        for (int i = 0; i < veggieCounts[player].length; i++) {
            if (veggieCounts[player][i] >= 2) {
                count++;
            }
        }
        return count * 3; // Returns 3 points for each type with 2 or more veggies
    }

    /**
     * Scoring for the "5 / VEGETABLE TYPE MISSING" card
     */
    private int scoreTypeMissing(int[][] veggieCounts, int player) {
        int count = 0;
        for (int i = 0; i < veggieCounts[player].length; i++) {
            if (veggieCounts[player][i] == 0) {
                count++;
            }
        }
        return count * 5; // Returns 5 points for each type missing
    }

    /**
     * Scoring for the "COMPLETE SET = 12" card
     */
    private int scoreCompleteSet(int[][] veggieCounts, int player) {
        int count = veggieCounts[player][0];
        for (int i = 1; i < veggieCounts[player].length; i++) {
            count = min(count, veggieCounts[player][i]);
        }
        return count * 12; // Returns 12 points for each complete set
    }

    /**
     * Scoring for the x/veggie cards
     */
    private int scoreIndividualVeggieCard(int[][] veggieCounts, int player, String criteria) {
        // Remove all spaces from the criteria
        criteria = criteria.replaceAll("\\s", "");

        String[] split = criteria.split("[/,]"); // Split on the / and , characters
        int score = 0;
        for (int i = 0; i < split.length; i++) {
            for (String veggie : veggies) {
                if (split[i].contains(veggie)) {
                    // Get the number of veggies for the specific veggie
                    int veggieCount = veggieCounts[player][veggieToInt(veggie)];

                    // Get the score multiplier by retrieving the previous number
                    int multiplier = Integer.parseInt(split[i - 1]);
                    score += veggieCount * multiplier;
                }
            }
        }
        return score;
    }

    /**
     * Scoring for the combined veggie cards (ex: PEPPER + TOMATO)
     */
    private int scoreCombinedVeggieCard(int[][] veggieCounts, int player, String criteria) {
        // Remove all plus and equal signs from the criteria
        criteria = criteria.replaceAll("\\+", "");
        criteria = criteria.replaceAll("=", "");
        String[] split = criteria.split("\\s"); // Split on the space character
        // Get every veggie that exists and find how many times it occurs
        int[] occurrences = new int[veggies.length];
        for (String str : split) {
            for (int i = 0; i < veggies.length; i++) {
                if (str.equals(veggies[i])) {
                    occurrences[i]++;
                }
            }
        }
        // Find the limiting veggie for scoring
        ArrayList<Integer> possibleScores = new ArrayList<>();
        for (int i = 0; i < occurrences.length; i++) {
            if (occurrences[i] > 0) {
                possibleScores.add(veggieCounts[player][i] / occurrences[i]);
            }
        }
        // Sort to get the lowest score
        possibleScores.sort(Integer::compareTo);
        // Get the numerical score multiplier at the end of the split array
        return possibleScores.get(0) * Integer.parseInt(split[split.length - 1]);
    }
}
