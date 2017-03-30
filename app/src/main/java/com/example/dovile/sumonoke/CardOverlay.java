package com.example.dovile.sumonoke;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.applantation.android.svg.SVGParser;
import com.example.dovile.sumonoke.cards.Demon;
import com.example.dovile.sumonoke.cards.Hero;


public class CardOverlay extends View{
    private Paint paint;
    private RectF rect;
    private Path path;
    private Context context;
    private int radius = 5;
    private int radiusPX = 10;
    private BoardPlace boardPlace;
    private float ratio;

    private static final int HP_ICON = 1;
    private static final int ATK_ICON = 2;
    private static final int DEF_ICON = 3;
    private static final int HEAL_ICON = 4;
    private static final int DEATH_ICON = 5;

    public static final int DEFAULT_STATE = 0;
    public static final int DEFENCE_STATE = 1;
    public static final int HEALTH_REDUCTION_STATE = 2;
    public static final int DEAD_STATE = 3;
    public static final int HEALING_STATE = 4;
    public static final int SELF_HEAL_STATE = 5;


    private int card_state = DEFAULT_STATE;
    private int reduced_hp = 0;
    private int fade_alpha = 0;


    public CardOverlay(Context context) {
        super(context);
        this.context = context;
        init();
        setWillNotDraw(false);

    }

    public CardOverlay(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        setWillNotDraw(false);


    }

    public CardOverlay(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        init();
        setWillNotDraw(false);

    }

    private void init() {
        rect = new RectF(0, 0, getWidth(), getHeight());
        paint = new Paint();
        path = new Path();
        paint.setAntiAlias(false);
        ratio = ((MainActivity)context).getRatio();
    }

        @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(radiusPX * 2, radiusPX * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        switch (card_state){
            case DEFAULT_STATE:
                drawCardStats(canvas);
                break;
            case DEFENCE_STATE:
                drawDefense(canvas);
                break;
            case HEALTH_REDUCTION_STATE:
                drawHealthReduction(canvas,reduced_hp);
                break;
            case DEAD_STATE:
                drawDeath(canvas);
                break;
            case HEALING_STATE:
                break;
            case SELF_HEAL_STATE:
                break;
        }
    }

    private void drawDeath(Canvas canvas){
        paint.setShader(getColorShader(R.color.sumonoke_black));
        canvas.drawCircle(radiusPX, radiusPX, radiusPX, paint);
        paint.setShader(null);
        paint.setColor(Color.RED);
        drawSVGPath(canvas, DEATH_ICON, radiusPX / 2, radiusPX / 2, 2f, 2f);
    }

    private void drawDefense(Canvas canvas){
        paint.setColor(Color.GREEN);
        if(fade_alpha > 0 && fade_alpha <= 255){//fade in
            paint.setAlpha(fade_alpha);
        }else if(fade_alpha > 255 && fade_alpha <= 500){// fade out
            paint.setAlpha(500 - fade_alpha);
        }else if (fade_alpha > 500 && fade_alpha <= 755){// fade in
            paint.setAlpha(fade_alpha - 500);
        }else if(fade_alpha > 755 && fade_alpha <= 1000){// fade out
            paint.setAlpha(1000 - fade_alpha);
        }

        drawSVGPath(canvas, DEF_ICON, radiusPX / 2, radiusPX / 2, 2f, 2f);
        if (fade_alpha+5 < 1000){
            fade_alpha += 5;
        }else{
            card_state = DEFAULT_STATE;
            fade_alpha = 0;
        }
        invalidate();
    }

    private void drawHealthReduction(Canvas canvas, int end_hp){

        if(reduced_hp != 0 && boardPlace.getCard() != null){
            int start_hp = boardPlace.getCard().getHp();
            paint.setColor(Color.RED);
            drawSVGPath(canvas, HP_ICON, radiusPX / 2, radiusPX / 2, 1.2f, 1.2f);
            if(start_hp != end_hp) {

//                paint.setShader(getColorShader(R.color.sumonoke_red));
//                canvas.drawCircle(radiusPX, radiusPX, radiusPX, paint);
//                paint.setShader(null);
//                int xPos = (canvas.getWidth() / 2);
//                int yPos = (int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2)) ;

                paint.setColor(Color.RED);

                canvas.drawText("" + start_hp,  radiusPX / 2, radiusPX + radiusPX/2, paint);
                boardPlace.getCard().setHp(start_hp - 1);
            }else {
                card_state = DEFAULT_STATE;
            }
            invalidate();
        }
    }

    private void drawCardStats(Canvas canvas){
        if (boardPlace.getCard() != null) {
            paint.setStyle(Paint.Style.FILL);
            if(boardPlace.getCard() instanceof Demon){
                drawDemonStats(canvas);
            }else{
                drawHeroStats(canvas);
            }

        }
    }

    private void drawHeroStats(Canvas canvas){
        Hero demon = ((Hero)boardPlace.getCard());
        paint.setShader(getColorShader(R.color.sumonoke_black));
        canvas.drawCircle(radiusPX, radiusPX, radiusPX, paint);

        int fontSize = (int) (2*ratio);
        int x1 = (int) (fontSize*2);
        int x2 = (int) (fontSize*1.1);

        paint.setShader(null);

        int y11 = (radiusPX*2)-getYCoordinate( radiusPX - fontSize, radiusPX, x1) + fontSize/2;
        paint.setColor(Color.GREEN);
        paint.setTextSize(fontSize);
        drawSVGPath(canvas, HP_ICON, x1 - (fontSize - 2), y11 - fontSize, 0.5f, 0.5f);
        canvas.drawText("" + demon.getHp(), x1 + 5, y11, paint);
    }

    private void drawDemonStats(Canvas canvas){
        Demon demon = ((Demon)boardPlace.getCard());
        paint.setShader(getColorShader(R.color.sumonoke_black));
        canvas.drawCircle(radiusPX, radiusPX, radiusPX, paint);

        int fontSize = (int) (2*ratio);
        int x1 = (int) (fontSize*2);
        int x2 = (int) (fontSize*1.1);

        paint.setShader(null);

        int y11 = (radiusPX*2)-getYCoordinate( radiusPX - fontSize, radiusPX, x1) + fontSize/2;
        paint.setColor(Color.RED);
        paint.setTextSize(fontSize);
        drawSVGPath(canvas, ATK_ICON, x1 - (fontSize - 2), y11 - fontSize, 0.5f, 0.5f);
        canvas.drawText("" + demon.getAtk(), x1 + 5, y11, paint);

        int y12 = (radiusPX*2)-getYCoordinate( radiusPX - fontSize, radiusPX, x2) + fontSize/2;
        paint.setColor(Color.GREEN);
        paint.setTextSize(fontSize);
        drawSVGPath(canvas, HP_ICON, x2 - (fontSize - 2), y12 - fontSize, 0.5f, 0.5f);
        canvas.drawText("" + demon.getHp(), x2 + 5, y12, paint);

        int y13 = getYCoordinate( radiusPX - fontSize, radiusPX, x2) + fontSize/2;
        paint.setColor(Color.BLUE);
        paint.setTextSize(fontSize);
        drawSVGPath(canvas, DEF_ICON, x2 - (fontSize - 2), y13 - fontSize, 0.5f, 0.5f);
        canvas.drawText("" + demon.getDef(), x2 + 5, y13, paint);

        int y14 = getYCoordinate( radiusPX - fontSize, radiusPX, x1) + fontSize/2;
        paint.setColor(Color.WHITE);
        paint.setTextSize(fontSize);
        drawSVGPath(canvas, HEAL_ICON, x1 - (fontSize - 2), y14 - fontSize, 0.5f, 0.5f);
        canvas.drawText(""+demon.getRegen(), x1 + 5, y14, paint);
    }

    private void drawSVGPath(Canvas canvas, int shape, float tx, float ty, float sx, float sy){
        String pString = null;
        switch (shape){
            case HP_ICON:
                pString = "M12,21.35l-1.45,-1.32C5.4,15.36 2,12.28 2,8.5 2,5.42 4.42,3 7.5,3c1.74,0 3.41,0.81 4.5,2.09C13.09,3.81 14.76,3 16.5,3 19.58,3 22,5.42 22,8.5c0,3.78 -3.4,6.86 -8.55,11.54L12,21.35z";
                break;
            case ATK_ICON:
                pString = "M11.481,18.461 L10.601,17.579 22.985,7.868c0.333,-0.261 0.605,-0.776 0.633,-1.199l0.38,-5.722c0.017,-0.256 -0.074,-0.503 -0.248,-0.678 -0.175,-0.175 -0.422,-0.266 -0.678,-0.249l-5.722,0.369c-0.423,0.027 -0.938,0.299 -1.2,0.631l-9.733,12.367 -0.88,-0.882c-0.424,-0.425 -1.114,-0.425 -1.539,-0.001l-0.958,0.957c-0.425,0.424 -0.425,1.114 -0.001,1.539l1.87,1.874 -1.673,1.67c-0.855,-0.155 -1.771,0.096 -2.431,0.755 -1.071,1.069 -1.072,2.809 -0.003,3.879 1.069,1.071 2.809,1.072 3.879,0.004 0.661,-0.659 0.914,-1.574 0.76,-2.43l1.673,-1.67 1.87,1.874c0.424,0.425 1.114,0.425 1.539,0.001l0.958,-0.957c0.425,-0.424 0.425,-1.114 0.002,-1.539z";
                break;
            case DEF_ICON:
                pString = "m23.143,5.054c-0.007,-0.563 -0.44,-1.027 -1,-1.079 -4.654,-0.435 -8.453,-2.923 -9.705,-3.833 -0.262,-0.191 -0.616,-0.191 -0.878,0 -1.25,0.91 -5.048,3.397 -9.703,3.833 -0.56,0.051 -0.991,0.516 -1,1.079 -0.056,3.671 0.533,16.135 10.862,18.909 0.183,0.049 0.377,0.049 0.56,0 10.329,-2.771 10.918,-15.238 10.864,-18.909z";
                break;
            case HEAL_ICON:
                pString = "M12,2C6.48,2 2,6.48 2,12s4.48,10 10,10 10,-4.48 10,-10S17.52,2 12,2zm5,11h-4v4h-2v-4H7v-2h4V7h2v4h4v2z";
                break;
            case DEATH_ICON:
                pString = "m23.605,15.484c0.058,-0.138 0.096,-0.227 -0.002,0.002 -0.225,0.532 -0.091,0.215 -0.002,0.004 -0.631,1.486 -2.792,2.096 -3.953,0.799 -0.485,-0.541 -0.655,-1.244 -0.963,-1.881 -0.364,-0.75 -1.518,-1.474 -2.367,-1.055 0.559,0.392 1.101,0.653 1.524,1.21 0.414,0.546 0.748,1.375 0.347,2.014 -0.339,0.539 -1.103,0.816 -1.722,0.68 -0.715,-0.156 -0.972,-0.74 -1.165,-1.363 -0.231,-0.744 -0.488,-1.113 -1.334,-0.99 -0.569,0.083 -1.574,0.419 -1.528,1.129 0.048,0.735 0.58,1.444 0.967,2.049 0.463,0.725 0.993,1.399 1.164,2.258 0.308,1.545 -0.463,3.003 -2.142,3.218 -1.647,0.21 -2.595,-1.146 -2.415,-2.652 0.088,-0.74 0.491,-1.342 0.67,-2.049 0.222,-0.879 0.08,-1.799 -0.123,-2.666 -0.151,-0.642 -0.594,-1.334 -1.283,-1.535 -0.755,-0.22 -1.127,0.431 -1.22,1.04 -0.147,0.965 -0.604,1.891 -1.762,1.623 -0.699,-0.162 -1.22,-0.687 -1.406,-1.367 -0.208,-0.762 0.17,-1.38 0.646,-1.951 0.242,-0.291 0.889,-0.871 0.821,-1.3 -0.114,-0.724 -1.574,0.132 -1.81,0.307 -0.646,0.479 -1.298,0.904 -2.151,0.804 -0.789,-0.093 -1.539,-0.661 -1.898,-1.349 -0.776,-1.486 0.353,-3.099 1.764,-3.678 1.58,-0.649 2.984,0.402 4.586,0.402 0.315,0 1.299,-0.025 1.134,-0.567 -0.218,-0.713 -1.034,-1.069 -1.679,-1.307 -0.67,-0.247 -1.311,-0.548 -1.62,-1.231 -0.31,-0.686 -0.087,-1.468 0.42,-1.999 0.978,-1.024 2.709,-1.362 3.751,-0.242 0.466,0.501 0.656,1.14 0.865,1.769 0.054,0.163 0.176,0.711 0.456,0.595 0.427,-0.177 0.075,-0.7 -0.084,-0.93 -0.829,-1.21 -0.156,-2.908 1.502,-2.524 0.578,0.134 1,0.557 1.082,1.14 0.099,0.703 -0.313,1.624 0.517,2.039 0.674,0.337 0.575,-1.036 0.549,-1.351 -0.067,-0.815 -0.032,-1.64 0.228,-2.423 0.491,-1.481 2.197,-2.477 3.539,-1.255 1.183,1.077 1.084,2.886 -0.169,3.85 -0.546,0.421 -2.939,1.394 -1.641,2.252 1.324,0.876 3.982,-1.188 4.589,0.93 0.174,0.608 -0.131,1.258 -0.709,1.527 -0.406,0.189 -0.871,0.182 -1.305,0.12 -0.24,-0.034 -0.834,-0.27 -0.954,0.045 -0.514,1.349 1.94,2.571 2.974,2.645 0.96,0.068 1.901,-0.091 2.695,0.582 0.771,0.652 1.001,1.721 0.616,2.635z";
                break;
        }
        if (pString != null){
            path = SVGParser.parsePath(pString);
            Matrix scaleMatrix = new Matrix();
            scaleMatrix.setScale(sx, sy,0,0);
            scaleMatrix.postTranslate(tx, ty);
            path.transform(scaleMatrix);
            canvas.drawPath(path, paint);
        }
    }

    private int getYCoordinate( int r1, int r2, int x1){
        int y1;
        int x2 = r2;
        int y2 = r2;

        y1 = (int) (Math.sqrt((r1*r1) - ((x1 - x2)*(x1 - x2))) + y2);

        return y1;
    }

    private Shader getColorShader(int colorId){

        LinearGradient shader = new LinearGradient(0.40f, 0.0f, 100.60f, 100.0f,
                colorId,
                colorId,
                Shader.TileMode.CLAMP);
        return shader;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.radiusPX = (int) (radius * ((MainActivity)context).getRatio());
    }

    public void setBoardPlace(BoardPlace boardPlace) {
        this.boardPlace = boardPlace;
    }

    public void setState(int post_attack_action) {
        this.card_state = post_attack_action;
    }

    public void setReduced_hp(int reduced_hp) {
        this.reduced_hp = reduced_hp;
    }

    public void setFadeAlpha(int fade_alpha) {
        this.fade_alpha = fade_alpha;
    }
}
