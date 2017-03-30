package com.example.dovile.sumonoke;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.dovile.sumonoke.cards.Card;
import com.example.dovile.sumonoke.cards.Demon;
import com.example.dovile.sumonoke.cards.Hero;

import java.util.List;
import static com.example.dovile.sumonoke.GameBoard.CardType.*;
import static com.example.dovile.sumonoke.GameBoard.Team.*;


public class BoardPlace extends View{

    private Paint paint;
    private RectF rect;
    private Path path;
    private Card card;
    private GameBoard.Team team;
    private int xCoord;
    private int yCoord;
    private int radius = 5;
    private int radiusPX = 10;
    private int colorId = 0;
    private CardOverlay cardOverlay;
    private CardAttackLayer cardAttackLayer;
    private GameBoard.CardType slotType;
    private int placeId;
    private List<BoardPlace> enemies;
    private List<BoardPlace> allies;


    public BoardPlace(Context context) {
        super(context);
    }

    public BoardPlace(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public BoardPlace(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    private void init() {
        rect = new RectF(0.1f, 0.1f, getWidth(), getHeight());
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(true);
    }

        @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(radiusPX * 2, radiusPX * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        init();

        if(card == null){
            paint.setShader(getBitmapShader(R.drawable.avatar_background, radiusPX));
        }else{
            int drawableResourceId;
            if (card instanceof Demon){
                drawableResourceId = this.getResources().getIdentifier(((Demon) card).getImgName(), "drawable", getContext().getPackageName());

            }else{
                drawableResourceId = this.getResources().getIdentifier(((Hero) card).getImgName(), "drawable", getContext().getPackageName());
            }
            paint.setShader(getBitmapShader(drawableResourceId, radiusPX));
        }

        canvas.drawCircle(radiusPX, radiusPX, radiusPX, paint);

        paint.setStyle(Paint.Style.FILL);
        if (ENEMY_TEAM.equals(team)){
            paint.setShader(getBitmapShader(R.drawable.enemy_container, radiusPX));
        }else{
            paint.setShader(getBitmapShader(R.drawable.player_container, radiusPX));
        };
        canvas.drawCircle(radiusPX, radiusPX, radiusPX, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN){

            if(MY_TEAM.equals(team) && !HERO.equals(slotType)){
                Toast.makeText(getContext(), "Slot selected", Toast.LENGTH_SHORT).show();
                ((MainActivity) getContext()).getGameBoard().setSelectedPlace(this);
            }else{
                ((MainActivity) getContext()).getGameBoard().setSelectedPlace(null);
            }

            if(cardAttackLayer == null && card != null){

                CardAttackLayer cardAttackLayer = CardAttackLayer.addAttackLayer(getContext(),card, getxCoord(), getyCoord(), getRadius(), team, true);
                setCardAttackLayer(cardAttackLayer);
                FrameLayout frame = (FrameLayout) ((MainActivity) getContext()).getGameBoard().getChildAt(placeId);
                frame.addView(cardAttackLayer, 0);

            }else if(cardAttackLayer != null && card != null){
                if(cardAttackLayer.show()){
                    cardAttackLayer.setShow(false);
                    cardAttackLayer.setBackground(null);
                }else{
                    cardAttackLayer.setShow(true);
                }
                cardAttackLayer.invalidate();
            }
        }
        return true;
    }

    private Shader getColorShader(int colorId){
        if (ENEMY_TEAM.equals(team)){
            colorId = getContext().getResources().getColor(R.color.sumonoke_red);
        }else{
            colorId = getContext().getResources().getColor(R.color.sumonoke_green);
        }
        LinearGradient shader = new LinearGradient(0.40f, 0.0f, 100.60f, 100.0f,
                colorId,
                colorId,
                Shader.TileMode.CLAMP);
        return shader;
    }

    private BitmapShader getBitmapShader(int drawableResourceId, int r){
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableResourceId);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, r * 2, r * 2, true);
        BitmapShader shader = new BitmapShader(scaledBmp, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
        return  shader;
    }

    private Bitmap getBitmap(int drawableResourceId, int r){
        Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableResourceId);
        Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, r*2, r*2, true);
        return  bmp;
    }

    public void setCard(Card card) {
        this.card = card;
    }

    public Card getCard() {
        return card;
    }

    public void setTeam(GameBoard.Team team) {
        this.team = team;
    }

    public int getxCoord() {
        return xCoord;
    }

    public void setxCoord(int xCoord) {
        this.xCoord = xCoord;
    }

    public int getyCoord() {
        return yCoord;
    }

    public void setyCoord(int yCoord) {
        this.yCoord = yCoord;
    }

    public int getRadius() {
        return radius;
    }

    public int getRadiusPX() {
        return radiusPX;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.radiusPX = (int) (radius * ((MainActivity)getContext()).getRatio());
    }

    public double getDistanceTo(BoardPlace place){
        return Math.abs(Math.sqrt(((xCoord - place.getxCoord()) * (xCoord - place.getxCoord())) + ((yCoord - place.getyCoord()) * (yCoord - place.getyCoord()))));
    }

    public void attack(BoardPlace place){

        if(!HERO.equals(slotType)){

            double dist = getDistanceTo(place);
            Card enemy_card = place.getCard();
            Demon my_demon = (Demon) card;
            if(dist < my_demon.getRange()){

                if(enemy_card instanceof  Demon){

                    Demon enemy_demon = (Demon) enemy_card;
                    if((my_demon.getAtk() > enemy_demon.getDef())){

                    // if attack breached, defender card overlay show health reduction state
                        int newHP = enemy_demon.getHp() + enemy_demon.getDef() - my_demon.getAtk();
                        place.getCardOverlay().setReduced_hp(newHP);
                        place.getCardOverlay().setState(CardOverlay.HEALTH_REDUCTION_STATE);
                        place.getCardOverlay().invalidate();

                    }else{

                    // if attack didn't breach defence, defender card overlay show defence status
                        place.getCardOverlay().setFadeAlpha(1);
                        place.getCardOverlay().setState(CardOverlay.DEFENCE_STATE);
                        place.getCardOverlay().invalidate();
                    }

                }else{

                    Hero enemy_hero = (Hero) enemy_card;
                    if(dist < my_demon.getRange()){
                        enemy_hero.setHp(enemy_hero.getHp() - my_demon.getAtk());
                    }

                    place.setCard(enemy_hero);
                    place.getCardOverlay().invalidate();
                }
            }
        }
    }

    public CardOverlay getCardOverlay() {
        return cardOverlay;
    }

    public void setCardOverlay(CardOverlay cardOverlay) {
        this.cardOverlay = cardOverlay;
    }

    public CardAttackLayer getCardAttackLayer() {
        return cardAttackLayer;
    }

    public void setCardAttackLayer(CardAttackLayer cardAttackLayer) {
        this.cardAttackLayer = cardAttackLayer;
    }

    public GameBoard.CardType getSlotType() {
        return slotType;
    }

    public void setSlotType(GameBoard.CardType slotType) {
        this.slotType = slotType;
    }

    public int getPlaceId() {
        return placeId;
    }

    public void setPlaceId(int placeId) {
        this.placeId = placeId;
    }

    public List<BoardPlace> getEnemies() {
        return enemies;
    }

    public void setEnemies(List<BoardPlace> enemies) {
        this.enemies = enemies;
    }

    public List<BoardPlace> getAllies() {
        return allies;
    }

    public void setAllies(List<BoardPlace> allies) {
        this.allies = allies;
    }

    public GameBoard.Team getTeam() {
        return team;
    }
}
