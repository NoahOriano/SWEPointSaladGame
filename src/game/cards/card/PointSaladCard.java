package game.cards.card;

public class PointSaladCard implements Card {
        public enum Vegetable {
            PEPPER, LETTUCE, CARROT, CABBAGE, ONION, TOMATO
        }

        public Vegetable vegetable;
        public String criteria;
        public boolean criteriaSideUp;

        public PointSaladCard(Vegetable vegetable, String criteria) {
            this.vegetable = vegetable;
            this.criteria = criteria;
            this.criteriaSideUp = true;
        }

    @Override
        public String toString() {
        if (criteriaSideUp) {
            return criteria + " (" + vegetable + ")";
        } else {
            return vegetable.toString();
        }
    }
}
