package game.cards.deck;

import game.cards.card.Card;

import java.util.List;

/**
 * Generic Deck Interface, representing a deck of cards to be drawn from
 **/
public interface Deck<T> {
    /**
     * Initialize the deck with the appropriate number of cards
     **/
    void initializeDeck(int numPlayers);

    /**
     * Shuffle the deck
     **/
    void shuffle();

    /**
     * Draw the top card from the deck
     **/
    Card draw();

    /**
     * Retrieve the Nth card in the deck, with 0 being the top
     **/
    Card getCard(int n);

    /**
     * Retrieve the entire deck
     **/
    List<T> getDeck();

    /**
     * Retrieve the number of cards in the deck
     **/
    int getNumCards();

    /**
     * Check if the deck is empty
     **/
    boolean isEmpty();
}
