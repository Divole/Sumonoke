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
import android.widget.Toast;

import com.example.dovile.sumonoke.cards.Demon;


public class ListCard extends View {
    private Paint paint;
    private RectF rect;
    private Path path;
    private Context context;
    private Demon demon;

    private OnTouchListener onTouchListener = new OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            System.out.println("Coordinates: " + motionEvent.getX() + "X" + motionEvent.getY());
            return false;
        }
    };

    public ListCard(Context context) {
        super(context);
        this.context = context;
        setWillNotDraw(false);
        setOnTouchListener(onTouchListener);
    }

    public ListCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        setWillNotDraw(false);
        setOnTouchListener(onTouchListener);

    }

    public ListCard(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        setWillNotDraw(false);
        setOnTouchListener(onTouchListener);
    }

    private void init() {
        System.out.println("LIST CARD: initialize view");
        rect = new RectF(0.1f, 0.1f, getWidth(), getHeight());
        paint = new Paint();
        path = new Path();

        if(demon == null){
            int colorId = context.getResources().getColor(R.color.sumonoke_blue);
            LinearGradient shader = new LinearGradient(0.40f, 0.0f, 100.60f, 100.0f,
                    colorId,
                    colorId,
                    Shader.TileMode.CLAMP);
            paint.setShader(shader);
        }else{
            int drawableResourceId = this.getResources().getIdentifier(demon.getImgName(), "drawable", context.getPackageName());
            Bitmap bmp = BitmapFactory.decodeResource(getResources(), drawableResourceId);
            Bitmap scaledBmp = Bitmap.createScaledBitmap(bmp, 90, 90, false);
            BitmapShader shader = new BitmapShader(scaledBmp, BitmapShader.TileMode.CLAMP, BitmapShader.TileMode.CLAMP);
            paint.setShader(shader);
        }

        paint.setAntiAlias(true);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        System.out.println("LIST CARD: measure view");
        setMeasuredDimension(100, 100);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        System.out.println("LIST CARD: draw canvas ");
        super.onDraw(canvas);
        init();
        path.moveTo(5, 5);
        path.lineTo(95, 5);
        path.lineTo(95, 95);
        path.lineTo(5, 95);
        path.close();
        path.setFillType(Path.FillType.EVEN_ODD);
        canvas.drawPath(path, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN){
            Toast.makeText(context, "Card selected", Toast.LENGTH_SHORT).show();
            ((MainActivity) context).getGameBoard().setCard(demon);
        }
        return true;
    }

    public void setDemon(Demon demon) {
        System.out.println("LIST CARD: set demon "+ demon.getImgName());
        this.demon = demon;
        invalidate();
    }

}
