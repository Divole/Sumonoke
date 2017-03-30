package com.example.dovile.sumonoke.cards;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class CardController {
    private List<Card> cards;

    public CardController() {
        createCards();
    }

    public Card getRandomAvatar(){
        Random r = new Random();
        final int min = 0;
        final int max = 4;
        final int random = r.nextInt((max - min) + 1) + min;
        return cards.get(random);
    }

    private void createCards(){
        cards = new LinkedList<>();

        Demon demon1 = new Demon(0,"Wind Spirit", "wind_spirit",295,30,70,7,1,35);
        cards.add(demon1);

        Demon demon2 = new Demon(1, "Swamp Spirit", "swamp_spirit",350,100,50,0,7,28);
        cards.add(demon2);


        Demon demon3 = new Demon(2, "Wise Fox", "fox_spirit",510,0,81,47,5,25);
        cards.add(demon3);

        Demon demon4 = new Demon(3, "Forest Spirit Spirit", "forest_spirit",420,117,35,0,1,29);
        cards.add(demon4);


        Demon demon5 = new Demon(4, "Kitsune", "kitsune",445,105,45,0,1,32);
        cards.add(demon5);
    }

    public List<Card> getCards() {
        return cards;
    }
}
