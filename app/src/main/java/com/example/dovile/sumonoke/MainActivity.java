package com.example.dovile.sumonoke;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.dovile.sumonoke.cards.Card;
import com.example.dovile.sumonoke.cards.CardController;
import com.example.dovile.sumonoke.cards.Demon;
import com.example.dovile.sumonoke.cards.Hero;

import java.util.LinkedList;
import java.util.List;
import static com.example.dovile.sumonoke.GameBoard.Team.*;
import static com.example.dovile.sumonoke.GameBoard.CardType.*;

public class MainActivity extends AppCompatActivity {



    private CardController cardController;
    private GameBoard gameBoard;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cardController = new CardController();
        createCardList();
        startGame();
    }

    private void startGame(){
        gameBoard = (GameBoard) findViewById(R.id.game_board);
        measureBoard();
        gameBoard.setCardController(cardController);
        gameBoard.removeAllViews();


        try {
            Hero enemy_hero = new Hero(1000, 40, "Akali", null, "hero_akali");
            gameBoard.addPlace(12, 20, 25, enemy_hero, ENEMY_TEAM, HERO, 0);
            Hero my_hero = new Hero(1000,40, "Karma", null, "hero_karma");
            gameBoard.addPlace(12, 70, 100, my_hero, MY_TEAM, HERO, 1);

            gameBoard.addPlace(10, 50, 30, null, ENEMY_TEAM, DEMON, 2);
            gameBoard.addPlace(10, 75, 25, null, MY_TEAM, DEMON, 3);

            gameBoard.addPlace(10, 10, 75, null, ENEMY_TEAM, DEMON, 4);
            gameBoard.addPlace(10, 15, 50, null, MY_TEAM, DEMON, 5);

            gameBoard.addPlace(10, 35, 85, null, ENEMY_TEAM, DEMON, 6);
            gameBoard.addPlace(10, 45, 105, null, MY_TEAM, DEMON, 7);

            gameBoard.addPlace(10, 60, 80, null, ENEMY_TEAM, DEMON, 8);
            gameBoard.addPlace(10, 50, 55, null, MY_TEAM, DEMON, 9);

            gameBoard.addPlace(10, 80, 45, null, ENEMY_TEAM, DEMON, 10);
            gameBoard.addPlace(10, 20, 110, null, MY_TEAM, DEMON, 11);

        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        }

        Button play_btn = (Button) findViewById(R.id.sumonoke_play);
        play_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gameBoard.allCardsSelected()) {
                    play();
                } else {
                    Toast.makeText(getBaseContext(), "Pick all cards", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void measureBoard(){
        DisplayMetrics metrics = new DisplayMetrics();
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        windowManager.getDefaultDisplay().getMetrics(metrics);
        int width_px = metrics.widthPixels;
        gameBoard.setBoardMeasurements(width_px * 130 / 100, width_px, width_px / 100);
    }

    private void play(){

        gameBoard.calculateEnemiesReached(MY_TEAM);
        gameBoard.calculateEnemiesReached(ENEMY_TEAM);

        List<BoardPlace> allPlaces = new LinkedList<>();
        allPlaces.addAll(gameBoard.getMyTeamPlaces());
        allPlaces.addAll(gameBoard.getEnemyTeamPlaces());
        attackTeam(allPlaces, 0);
    }

    private void createCardList(){
        LinearLayout cardsList = (LinearLayout) findViewById(R.id.card_list);
        List<Card> listOfCards = cardController.getCards();
        for(Card card: listOfCards){
            ListCard listCard1 = new ListCard(this);
            listCard1.setDemon((Demon) card);
            cardsList.addView(listCard1);
        }
    }

    public float getRatio() {
        return gameBoard.getRatio();
    }

    public GameBoard getGameBoard() {
        return gameBoard;
    }

    private void attackTeam(final List<BoardPlace> allPlaces, final int attacker_index){

        final BoardPlace attackerPlace = allPlaces.get(attacker_index);

        if (attackerPlace.getCard().getAtk() != 0) {

            if (attackerPlace.getCardAttackLayer() == null) {
                CardAttackLayer attack = CardAttackLayer.addAttackLayer(this, attackerPlace.getCard(), attackerPlace.getxCoord(), attackerPlace.getyCoord(), attackerPlace.getRadius(), attackerPlace.getTeam(), true);
                attackerPlace.setCardAttackLayer(attack);
                FrameLayout frame = (FrameLayout) getGameBoard().getChildAt(attackerPlace.getPlaceId());
                frame.addView(attack, 0);
            } else {
                attackerPlace.getCardAttackLayer().setShow(true);
                attackerPlace.getCardAttackLayer().invalidate();
            }

            final CardAttackLayer cardAttackLayer = attackerPlace.getCardAttackLayer();

            Handler handler = new Handler();
            handler.postDelayed(new Runnable() {
                public void run() {
                    // Actions to do after 10 seconds
                    cardAttackLayer.setShow(false);
                    cardAttackLayer.invalidate();
                    List<BoardPlace> enemies = attackerPlace.getEnemies();
                    for (BoardPlace defender : enemies) {
                        attackerPlace.attack(defender);
                    }
                    Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        public void run() {
                            if (attacker_index + 1 < allPlaces.size()) {
                                attackTeam(allPlaces, attacker_index + 1);
                            } else {
                                //TODO": heal allies
                                // check who died
                                checkDead();
                            }
                        }
                    }, 1000 * enemies.size());
                }
            }, 1000);
        }else{
            if (attacker_index + 1 < allPlaces.size()) {
                attackTeam(allPlaces, attacker_index + 1);
            } else {
                //TODO: check who died
                checkDead();
            }
        }
    }

    private void checkDead(){
        List<BoardPlace> allPlaces = new LinkedList<>();
        allPlaces.addAll(gameBoard.getMyTeamPlaces());
        allPlaces.addAll(gameBoard.getEnemyTeamPlaces());
        for (BoardPlace place: allPlaces){
            if(place.getCard().getHp() < 0){
                place.getCardOverlay().setState(CardOverlay.DEAD_STATE);
                place.getCardOverlay().invalidate();
                place.getCard().setAtk(0);
            }
        }
    }

}
