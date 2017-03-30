package com.example.dovile.sumonoke;

import android.content.Context;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.ViewManager;
import android.widget.FrameLayout;

import com.example.dovile.sumonoke.cards.Card;
import com.example.dovile.sumonoke.cards.CardController;
import com.example.dovile.sumonoke.cards.Demon;

import java.util.LinkedList;
import java.util.List;

import static com.example.dovile.sumonoke.GameBoard.Team.ENEMY_TEAM;
import static com.example.dovile.sumonoke.GameBoard.Team.MY_TEAM;


public class GameBoard extends FrameLayout {

    private List<BoardPlace> enemyTeamPlaces = new LinkedList<>();
    private List<BoardPlace> myTeamPlaces = new LinkedList<>();

    private CardController cardController;

    private BoardPlace selectedPlace;

    private int height_px;
    private int width_px;
    private float ratio;

    public float getRatio() {
        return ratio;
    }

    public static enum Team {
        ENEMY_TEAM,MY_TEAM;
    }
    public static enum CardType{
        HERO,DEMON;
    }

    public GameBoard(Context context) {
        super(context);
    }

    public GameBoard(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public GameBoard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void addPlace(int r, int x, int y, Card card, Team team, CardType slot_type, int placeId) throws IllegalArgumentException {

        if(x+r > 100) throw  new IllegalArgumentException();
        if(y+r > 130) throw  new IllegalArgumentException();
        if(x-(r/2) < 0) throw  new IllegalArgumentException();
        if(y-(r/2) < 0) throw  new IllegalArgumentException();

        if(card == null &&Team.ENEMY_TEAM.equals(team)){
            card = cardController.getRandomAvatar();
        }

        BoardPlace place = new BoardPlace(getContext());
        place.setxCoord(x);
        place.setyCoord(y);
        place.setRadius(r);
        place.setTeam(team);
        place.setSlotType(slot_type);
        place.setPlaceId(placeId);
        place.setCard(card);

        int dimen1 = (int) (r * ratio) * 2;
        int left1 = (int) (x * ratio);
        int top1 = (int) (y * ratio);

        FrameLayout.LayoutParams placeParams = new FrameLayout.LayoutParams(dimen1, dimen1);
        placeParams.setMargins(left1, top1, 0, 0);
        placeParams.gravity = Gravity.TOP;
        place.setLayoutParams(placeParams);
//        place.setBackground(getResources().getDrawable(R.drawable.border));

        CardOverlay placeOverlay = new CardOverlay(getContext());
//        placeOverlay.setBackground(getResources().getDrawable(R.drawable.border));
        int dimen = (int) ((r - 3) * ratio) * 2;
        int left = ((int) (x * ratio)) + (dimen1-dimen)/2;
        int top = ((int) (y * ratio)) + (dimen1-dimen)/2;
        FrameLayout.LayoutParams overlayParams = new FrameLayout.LayoutParams(dimen, dimen);
        placeOverlay.setRadius(r - 3);
        placeOverlay.setBoardPlace(place);
        overlayParams.setMargins(left, top, 0, 0);
        placeOverlay.setLayoutParams(overlayParams);
        place.setCardOverlay(placeOverlay);

        FrameLayout frame = new FrameLayout(getContext());
        frame.setLayoutParams(new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        frame.addView(place);
        frame.addView(placeOverlay);
        this.addView(frame,placeId);

        if(ENEMY_TEAM.equals(team)){
            enemyTeamPlaces.add(place);
        }else if(MY_TEAM.equals(team)){
            myTeamPlaces.add(place);
        }
    }

    public void setCardController(CardController cardController) {
        this.cardController = cardController;
    }

    public void setBoardMeasurements(int height_px, int width_px, int ratio) {
        this.height_px = height_px;
        this.width_px = width_px;
        this.ratio = ratio;
        FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) this.getLayoutParams();
        params.height = height_px;
        params.width = width_px;
        this.setLayoutParams(params);
    }

    public void calculateEnemiesReached(Team team){
        if (MY_TEAM.equals(team)){
            for (BoardPlace mPlace: myTeamPlaces) {
                List<BoardPlace> mEnemies = new LinkedList<>();
                int mRange = mPlace.getCard().getRange();
                for (BoardPlace ePlace: enemyTeamPlaces){
                    int dist =  (int)mPlace.getDistanceTo(ePlace);
                    if(dist < mRange && ePlace.getCard().getHp() > 0){
                        mEnemies.add(ePlace);
                    }
                }
                mPlace.setEnemies(mEnemies);
            }
        }else{
            for (BoardPlace ePlace: enemyTeamPlaces) {
                List<BoardPlace> eEnemies = new LinkedList<>();
                int mRange = ePlace.getCard().getRange();
                for (BoardPlace mPlace: myTeamPlaces){
                    int dist =  (int)ePlace.getDistanceTo(mPlace);
                    if(dist < mRange  && mPlace.getCard().getHp() > 0){
                        eEnemies.add(mPlace);
                    }
                }
                ePlace.setEnemies(eEnemies);
            }
        }
    }

    public void setCard(Demon demon){
        try {
            if(selectedPlace.getCardAttackLayer() != null){
                CardAttackLayer ca = selectedPlace.getCardAttackLayer();
                ((ViewManager)ca.getParent()).removeView(ca);
                selectedPlace.setCardAttackLayer(null);
            }
            selectedPlace.setCard(demon.clone());
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        selectedPlace.invalidate();
        selectedPlace.getCardOverlay().invalidate();

        CardAttackLayer ca =  selectedPlace.getCardAttackLayer();
        if (ca != null) {
            ca.invalidate();
        }
    }

    public boolean allCardsSelected(){
        boolean allSelected  = true;
        for (BoardPlace place:  myTeamPlaces) {
            if (place.getCard() == null){
                allSelected= false;
            }
        }
        return  allSelected;
    }

    public BoardPlace getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(BoardPlace selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    public List<BoardPlace> getEnemyTeamPlaces() {
        return enemyTeamPlaces;
    }

    public List<BoardPlace> getMyTeamPlaces() {
        return myTeamPlaces;
    }
}
