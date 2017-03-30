package com.example.dovile.sumonoke;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;
import android.widget.FrameLayout;

import com.example.dovile.sumonoke.cards.Card;
import com.example.dovile.sumonoke.cards.Demon;
import com.example.dovile.sumonoke.cards.Hero;


public class CardAttackLayer extends View {
    private Paint paint;
    private RectF rect;
    private Path path;
    private Context context;
    private float ratio;
    private int pivotX;
    private int pivotXpx;
    private int pivotY;
    private int pivotYpx;
    private int radius;
    private int radiusPx;
    private GameBoard.Team team;
    private boolean show = false;
    private int angle = 0;


    public CardAttackLayer(Context context) {
        super(context);
        this.context = context;
        init();
        setWillNotDraw(false);

    }

    public CardAttackLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
        setWillNotDraw(false);
    }

    public CardAttackLayer(Context context, AttributeSet attrs, int defStyle) {
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
        setMeasuredDimension(radiusPx * 2, radiusPx * 2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        init();
        if(show){
//            if(team == MainActivity.ENEMY_TEAM){
//                paint.setShader(getColorShader(R.color.sumonoke_black));
//            }else{
//                paint.setShader(getColorShader(R.color.sumonoke_black));
//            }
//            canvas.drawCircle(pivotXpx,pivotYpx,radiusPx,paint);
            drawAttack(canvas);
        }
    }

    private void drawAttack(Canvas canvas){

        if (angle++ >360) {
            angle = 0;
        }

        Bitmap attack = BitmapFactory.decodeResource(getResources(), R.drawable.attack5);
        Bitmap bmp = Bitmap.createScaledBitmap(attack,radiusPx*2, radiusPx*2,false);
        canvas.rotate(angle, pivotXpx, pivotYpx); //Rotate the canvas.
        int top = 0;
        int left = 0;
        if(pivotXpx < radiusPx){
            left = pivotXpx - radiusPx;
        }
        if(pivotYpx < radiusPx){
            top = pivotYpx - radiusPx;
        }
        canvas.drawBitmap(bmp,left,top,paint); //Draw the ball on the rotated canvas.
        canvas.restore(); //Rotate the canvas back so that it looks like ball has rotated.

        //Call the next frame.
        invalidate();


    }

    private Shader getColorShader(int colorId){

        LinearGradient shader = new LinearGradient(0.40f, 0.0f, 100.60f, 100.0f,
                colorId,
                colorId,
                Shader.TileMode.CLAMP);
        return shader;
    }


    public int getAttackPivotX() {
        return pivotX;
    }

    public void setAttackPivotX(int pivotX) {
        this.pivotX = pivotX;
        this.pivotXpx = (int) (pivotX * ratio);
    }


    public int getAttackPivotY() {
        return pivotY;
    }

    public void setAttackPivotY(int pivotY) {
        this.pivotY = pivotY;
        this.pivotYpx = (int) (pivotY * ratio);
    }

    public int getRadius() {
        return radius;
    }

    public void setRadius(int radius) {
        this.radius = radius;
        this.radiusPx = (int) (radius * ratio);
    }

    public void setTeam(GameBoard.Team team) {
        this.team = team;
    }

    public boolean show() {
        return show;
    }

    public void setShow(boolean show) {
        this.show = show;
    }

    public GameBoard.Team getTeam() {
        return team;
    }

    public static CardAttackLayer addAttackLayer(Context context, Card card, int Ax,int Ay, int Ar, GameBoard.Team team, boolean show){
        int range = 0;
        if(card instanceof Demon){
            Demon demon = (Demon) card;
            range = demon.getRange();
        }else{
            Hero demon = (Hero) card;
            range = demon.getRange();
        }

        int radiusX1= 0;
        int radiusX2=0;

        int radiusY1= 0;
        int radiusY2=0;

        int marginLeft = 0;
        int marginTop = 0;


        int cX = Ax + Ar;//distance from 0 to card center on x axis
        int cY = Ay + Ar;// distance fom board top to card center on y axis

        if (cX - range < 0 && cX + range < 100){// left - not fit / right -fit
            radiusX1 = range - (range - cX );
            radiusX2 = range;
            marginLeft = 0;

        }else if(cX - range < 0 && cX + range > 100) {// left - not fit / right - not fit
            radiusX1 = range - (range - cX );
            radiusX2 = range - ((Ax+Ar+range) - 100);
            marginLeft = 0;

        }else if(cX - range > 0 && cX + range > 100){// left - fit / right - not fit
            radiusX1 = range;
            radiusX2 = range - ((Ax+Ar+range) - 100);
            marginLeft = 100 - (radiusX1 + radiusX2);

        }else{//// left - fit / right - fit
            radiusX1 = range;
            radiusX2 = range;
            marginLeft = cX - radiusX1;
        }

        if (cY - range < 0 && cY + range < 130){// top - not fit / bottom - fit
            radiusY1 = range - (range - cY );
            radiusX2 = range;
            marginTop = 0;
        }else if(cY - range < 0 && cY + range > 130){// top - not fit / bottom - not fit
            radiusY1 = range - (range - cY );
            radiusY2 = range - ((Ay+Ar+range) - 130);
            marginTop = 0;

        }else if(cY - range > 0 && cY + range > 130){// top - fit / bottom - not fit
            radiusY1 = range;
            radiusY2 = range - ((cY+range) - 130);
            marginTop = 130 - (radiusY1 + radiusY2);
        } else{// top - fit / bottom - fit
            radiusY1 = range;
            radiusY2 = range;
            marginTop = cY - radiusY1;
        }

        int width = radiusX1 + radiusX2;
        int height = radiusY1 + radiusY2;

        CardAttackLayer cardAttackLayer = new CardAttackLayer(context);
        cardAttackLayer.setAttackPivotX(radiusX1);
        cardAttackLayer.setAttackPivotY(radiusY1);
        cardAttackLayer.setRadius(range);
        cardAttackLayer.setTeam(team);
        cardAttackLayer.setShow(show);
        float ratio = ((MainActivity)context).getRatio();
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams((int) (width* ratio), (int) (height* ratio));
        params.setMargins((int) (marginLeft * ratio), (int) (marginTop * ratio), 0, 0);
        cardAttackLayer.setLayoutParams(params);
//        cardAttack.setBackground(getDrawable(R.drawable.border));
        return cardAttackLayer;
    }
}
