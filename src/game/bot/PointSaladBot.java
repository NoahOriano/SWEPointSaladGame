package game.bot;

import game.cards.card.PointSaladCard;
import game.cards.hand.PointSaladHand;
import game.scoring.PointSaladScoring;
import game.state.PointSaladState;

import java.util.ArrayList;
import java.util.Comparator;

/**
 * A bot that plays the Point Salad game.
 * The bot will choose the best action to take based on the current state of the game.
 * Uses greedy logic to determine the best card to take.
 * Does not convert cards, but plays reasonably well (beats the developer more than they would like to admit).
 * Due to greedy logic and the design of the game, this bot likes to take missing and fewest card criteria early game,
 * as they give points right away, and then the bot will limit the veggie variety they take, and often take high-scoring
 * criteria later in the game. This is actually a reasonable strategy for the game, and the bot can compete with humans.
 **/
public class PointSaladBot implements Bot {
    PointSaladState state;

    public PointSaladBot(PointSaladState pointSaladState) {
        this.state = pointSaladState;
    }

    private PointSaladHand copyHand(PointSaladHand hand) {
        PointSaladHand newHand = new PointSaladHand();
        for (PointSaladCard card : hand.getCards()) {
            newHand.addCard(card);
        }
        return newHand;
    }

    /**
     * Returns the action for the bot to take.
     **/
    @Override
    public String getAction(int Player) {
        if (state.getGamePhase() == PointSaladState.PHASE.TAKE_CARD) {
            return determineTakeCardAction(Player);
        } else {
            return determineConvertCardAction(Player);
        }
    }

    /**
     * Determines the bot action for convert card phase
     * For now, bots do not convert cards
     **/
    private String determineConvertCardAction(int Player) {
        return "n";
    }

    /**
     * Determines the best action for the bot to take when taking a card
     **/
    private String determineTakeCardAction(int Player) {
        // Find the best action for the bot
        class BotAction {
            public final String action;
            public final int score;

            public BotAction(String action, int score) {
                this.action = action;
                this.score = score;
            }
        }
        class ActionComparator implements Comparator<BotAction> {
            @Override
            public int compare(BotAction a, BotAction b) {
                return a.score - b.score;
            }
        }
        PointSaladScoring scoring = new PointSaladScoring();
        PointSaladHand currentHand = state.getHands()[Player];
        int currentScore = scoring.calculateScoresOfHands(new PointSaladHand[]{currentHand})[0];

        ArrayList<BotAction> criteriaActions = new ArrayList<BotAction>();
        // Find every criteria action and its gained score
        PointSaladCard[] criteriaCards = state.getCriteriaCards();
        for (int i = 0; i < criteriaCards.length; i++) {
            if (criteriaCards[i] != null) {
                PointSaladHand newHand = copyHand(currentHand);
                newHand.addCard(criteriaCards[i]);
                int newScore = scoring.calculateScoresOfHands(new PointSaladHand[]{newHand})[0];
                criteriaActions.add(new BotAction(Integer.toString(i), newScore));
            }
        }

        // Find every veggie card and its gained score
        PointSaladCard[][] veggieCards = state.getVeggieCards();
        ArrayList<BotAction> veggieActions = new ArrayList<BotAction>();
        for (int col = 0; col < veggieCards.length; col++) {
            for (int row = 0; row < veggieCards[0].length; row++) {
                if (veggieCards[col][row] != null) {
                    PointSaladHand newHand = copyHand(currentHand);
                    newHand.addCard(veggieCards[col][row]);
                    int newScore = scoring.calculateScoresOfHands(new PointSaladHand[]{newHand})[0];
                    veggieActions.add(new BotAction(Character.toString((char) ('A' + col + (row * veggieCards.length))), newScore));
                }
            }
        }

        // Determine best action
        criteriaActions.sort(new ActionComparator());
        veggieActions.sort(new ActionComparator());
        int bestCriteriaScore = criteriaActions.size() > 0 ? criteriaActions.get(criteriaActions.size() - 1).score : 0;
        int bestVeggieScore;
        if (veggieActions.size() >= 2) {
            bestVeggieScore = veggieActions.get(veggieActions.size() - 1).score + veggieActions.get(veggieActions.size() - 2).score;
        } else if (veggieActions.size() == 1) {
            bestVeggieScore = veggieActions.get(0).score;
        } else {
            bestVeggieScore = 0;
        }

        // The bot will choose veggie cards rather than criteria if the score is equal
        if (bestVeggieScore >= bestCriteriaScore) {
            if (veggieActions.size() == 1) {
                return veggieActions.get(0).action;
            } else if (veggieActions.size() >= 2) {
                // Return two best veggie options
                return veggieActions.get(veggieActions.size() - 1).action + veggieActions.get(veggieActions.size() - 2).action;
            }
        }

        // Returns the best criteria action if veggie actions were not chosen
        if (criteriaActions.size() > 0) {
            return criteriaActions.get(criteriaActions.size() - 1).action;
        }
        return "NAB"; // Should never happen
    }
}
