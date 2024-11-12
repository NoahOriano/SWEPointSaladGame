package game.cards.hand;

import game.cards.card.PointSaladCard;

import java.util.ArrayList;

public class PointSaladHand implements Hand<PointSaladCard> {

    private final ArrayList<PointSaladCard> cards = new ArrayList<>();

    @Override
    public void addCard(PointSaladCard card) {
        cards.add(card);
    }

    @Override
    public PointSaladCard removeCard(PointSaladCard card) {
        return cards.remove(getCardIndex(card));
    }

    @Override
    public int getNumCards() {
        return cards.size();
    }

    @Override
    public PointSaladCard getCard(int index) {
        return cards.get(index);
    }

    @Override
    public PointSaladCard[] getCards() {
        return cards.toArray(new PointSaladCard[0]);
    }

    @Override
    public int getCardIndex(PointSaladCard card) {
        for (int i = 0; i < getNumCards(); i++) {
            if (getCard(i).equals(card)) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    @Override
    public void clear() {
        cards.clear();
    }

    public int countVegetable(PointSaladCard.Vegetable vegetable) {
        int count = 0;
        for (PointSaladCard card : cards) {
            if (card.vegetable == vegetable && !card.criteriaSideUp) {
                count++;
            }
        }
        return count;
    }

    public int countTotalVegetables() {
        int count = 0;
        for (PointSaladCard card : cards){
            if (!card.criteriaSideUp) {
                count++;
            }
        }
        return count;
    }

    public int countTotalCriteria() {
        int count = 0;
        for (PointSaladCard card : cards){
            if (card.criteriaSideUp) {
                count++;
            }
        }
        return count;
    }

    @Override
    public String stringifyHand() {
        StringBuilder handStringBuilder = new StringBuilder("Criteria("+countTotalCriteria()+"):\t");
        for (int i = 0; i < cards.size(); i++) {
            if (cards.get(i).criteriaSideUp && cards.get(i).vegetable != null) {
                handStringBuilder.append("[").append(i).append("] ").append(cards.get(i).criteria)
                        .append(" (").append(cards.get(i).vegetable.toString()).append(")").append("\t");
            }
        }
        handStringBuilder.append("\nVegetables(").append(countTotalVegetables()).append("):\t");
        //Sum up the number of each vegetable and show the total number of each vegetable
        for (PointSaladCard.Vegetable vegetable : PointSaladCard.Vegetable.values()) {
            int count = countVegetable(vegetable);
            if (count > 0) {
                handStringBuilder.append(vegetable).append(": ").append(count).append("\t");
            }
        }
        return handStringBuilder.toString();
    }
}
