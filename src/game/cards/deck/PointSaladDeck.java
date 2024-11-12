package game.cards.deck;
import game.cards.card.PointSaladCard;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

public class PointSaladDeck implements Deck<PointSaladCard>{

    private final String MANIFEST_PATH = "src/game/cards/deck/PointSaladManifest.json";
    private ArrayList<PointSaladCard> deck = new ArrayList<>();
    private int numPlayers;

    public PointSaladDeck() {
        numPlayers = -1; // Signal that the deck has not been initialized
    }

    /** Constructor for PointSaladDeck to also initialize the deck **/
    public PointSaladDeck(int numPlayers) {
        initializeDeck(numPlayers);
    }

    /** Utility method to shuffle sub-decks **/
    private void shuffleDeck(List<PointSaladCard> deck) {
        Collections.shuffle(deck);
    }

    /** Initialize the deck with the appropriate number of cards
     *  For each player, there are 3 cards of each vegetable type
     *  The deck is shuffled after initialization
     *  The card types and criteria exist in the PointSaladManifest file
     **/
    @Override
    public void initializeDeck(int numPlayers) {
        if(numPlayers < 2 || numPlayers > 6) {
            throw new IllegalArgumentException("Number of players must be between 2 and 6");
        }
        this.numPlayers = numPlayers; // Update the game number of players

        ArrayList<PointSaladCard> deckPepper = new ArrayList<>();
        ArrayList<PointSaladCard> deckLettuce = new ArrayList<>();
        ArrayList<PointSaladCard> deckCarrot = new ArrayList<>();
        ArrayList<PointSaladCard> deckCabbage = new ArrayList<>();
        ArrayList<PointSaladCard> deckOnion = new ArrayList<>();
        ArrayList<PointSaladCard> deckTomato = new ArrayList<>();

        try (InputStream fInputStream = new FileInputStream(MANIFEST_PATH);
             Scanner scanner = new Scanner(fInputStream, StandardCharsets.UTF_8).useDelimiter("\\A")) {

            // Read the entire JSON file into a String
            String jsonString = scanner.hasNext() ? scanner.next() : "";

            // Parse the JSON string into a JSONObject
            JSONObject jsonObject = new JSONObject(jsonString);

            // Get the "cards" array from the JSONObject
            JSONArray cardsArray = jsonObject.getJSONArray("cards");

            // Iterate over each card in the array
            for (int i = 0; i < cardsArray.length(); i++) {
                JSONObject cardJson = cardsArray.getJSONObject(i);

                // Get the criteria object from the card JSON
                JSONObject criteriaObj = cardJson.getJSONObject("criteria");

                // Add each vegetable card to the deck with its corresponding criteria
                deckPepper.add(new PointSaladCard(PointSaladCard.Vegetable.PEPPER, criteriaObj.getString("PEPPER")));
                deckLettuce.add(new PointSaladCard(PointSaladCard.Vegetable.LETTUCE, criteriaObj.getString("LETTUCE")));
                deckCarrot.add(new PointSaladCard(PointSaladCard.Vegetable.CARROT, criteriaObj.getString("CARROT")));
                deckCabbage.add(new PointSaladCard(PointSaladCard.Vegetable.CABBAGE, criteriaObj.getString("CABBAGE")));
                deckOnion.add(new PointSaladCard(PointSaladCard.Vegetable.ONION, criteriaObj.getString("ONION")));
                deckTomato.add(new PointSaladCard(PointSaladCard.Vegetable.TOMATO, criteriaObj.getString("TOMATO")));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Shuffle each deck
        shuffleDeck(deckPepper);
        shuffleDeck(deckLettuce);
        shuffleDeck(deckCarrot);
        shuffleDeck(deckCabbage);
        shuffleDeck(deckOnion);
        shuffleDeck(deckTomato);

        int cardsPerVeggie = this.numPlayers * 3;

        ArrayList<PointSaladCard> deck = new ArrayList<>();
        for(int i = 0; i < cardsPerVeggie; i++) {
            deck.add(deckPepper.remove(0));
            deck.add(deckLettuce.remove(0));
            deck.add(deckCarrot.remove(0));
            deck.add(deckCabbage.remove(0));
            deck.add(deckOnion.remove(0));
            deck.add(deckTomato.remove(0));
        }
        this.deck = deck;
        shuffle();
    }

    @Override
    public void shuffle() {
        Collections.shuffle(deck);
    }

    @Override
    public PointSaladCard draw() {
        if(deck.isEmpty()) {
            return null;
        }
        return deck.remove(0);
    }

    @Override
    public PointSaladCard getCard(int n) {
        return deck.get(n);
    }


    @Override
    public List<PointSaladCard> getDeck() {
        return deck;
    }

    @Override
    public int getNumCards() {
        return deck.size();
    }

    @Override
    public boolean isEmpty() {
        return deck.isEmpty();
    }
}
