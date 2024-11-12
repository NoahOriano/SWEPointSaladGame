package game.scoring;

/**
 * Interface for scoring hands. Scoring needs to be implemented for each game individually.
 * @param <THand> the type of hand to score
 */
public interface Scoring<THand> {
    /**
     * Calculate the scores of each hand
     * @param hands the hands to calculate the scores of
     * @return the scores of each hand
     */
    int[] calculateScoresOfHands(THand[] hands);
}
