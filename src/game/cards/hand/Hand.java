package game.cards.hand;

/**
 * Generic Hand Interface, represents the cards that a player holds
 **/
public interface Hand<T> {

    /**
     * Add a card to the hand
     **/
    public void addCard(T card);

    /**
     * Remove a card from the hand
     **/
    public T removeCard(T card);

    /**
     * Get the number of cards in the hand
     **/
    public int getNumCards();

    /**
     * Get the card at a specific index
     **/
    public T getCard(int index);

    /**
     * Get all of the cards in the hand
     **/
    public T[] getCards();

    /**
     * Get the index of a specific card
     **/
    public int getCardIndex(T card);

    /**
     * Check if the hand is empty
     **/
    public boolean isEmpty();

    /**
     * Clear the hand
     **/
    public void clear();

    /**
     * Return a string representation of the hand
     **/
    public String stringifyHand();
}
