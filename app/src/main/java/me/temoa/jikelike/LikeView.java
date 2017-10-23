package me.temoa.jikelike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;

@SuppressWarnings("unused")
public class LikeView extends View {

    private static final String TAG = "LikeView";

    private Paint mPaint;
    private Path mPath;

    private int singleTextHeight;
    private int singleTextWidth;

    private int width, height;

    private Bitmap likeBitmap;
    private Bitmap unlikeBitmap;

    private int currentNumber = 0;
    private String originStr;
    private String nextStr;
    private String frontNoChangeStr;
    private String lastOriginStr;
    private String lastChangeStr;

    private int offset = 0;

    private boolean isAnim = false;
    private boolean isPlaying = false;

    public LikeView(Context context) {
        super(context);
        init();
    }

    public LikeView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setTextSize(96);

        mPath = new Path();
        calculationTextSize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(300, singleTextHeight + 20);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        mPath.moveTo(0, height);
        mPath.lineTo(width, height);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        anotherDrawing(canvas);
    }

    private void anotherDrawing(Canvas canvas) {
        if (!isAnim) {
            canvas.drawTextOnPath(originStr, mPath, 0, 0, mPaint);
        } else {
            canvas.drawTextOnPath(frontNoChangeStr, mPath, 0, 0, mPaint);
            int x1 = frontNoChangeStr.length() * singleTextWidth;
            canvas.drawTextOnPath(lastOriginStr, mPath, x1, -offset, mPaint);
            canvas.drawTextOnPath(lastChangeStr, mPath, x1, height - offset, mPaint);
        }
    }


    public void startAnim() {
        if (isPlaying) return;

        ValueAnimator numberAnim = ValueAnimator.ofInt(0, height);
        numberAnim.setDuration(300);
        numberAnim.setInterpolator(new LinearInterpolator());
        numberAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                offset = (int) animation.getAnimatedValue();
                invalidate();
            }
        });
        numberAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                isAnim = false;
                isPlaying = false;
                originStr = nextStr;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnim = false;
                isPlaying = false;
                originStr = nextStr;
            }
        });
        isAnim = true;
        isPlaying = true;
        numberAnim.start();
    }

    private void calculationTextChange() {
        int nextNumber = currentNumber + 1;
        originStr = String.valueOf(currentNumber);
        nextStr = String.valueOf(nextNumber);

        if (originStr.length() != nextStr.length()) {
            frontNoChangeStr = "";
            lastOriginStr = originStr;
            lastChangeStr = nextStr;
        } else {
            for (int i = 0; i < nextStr.length(); i++) {
                char curChar = originStr.charAt(i);
                char nextChar = nextStr.charAt(i);
                if (curChar != nextChar) {
                    frontNoChangeStr = originStr.substring(0, i);
                    lastOriginStr = originStr.substring(i);
                    lastChangeStr = nextStr.substring(i);
                    break;
                }
            }
        }
    }

    private void calculationTextSize() {
        Rect rect = new Rect();
        mPaint.getTextBounds("0", 0, "0".length(), rect);
        singleTextHeight = rect.height();
        singleTextWidth = (int) mPaint.measureText("0");
    }

    public void setNumber(int number) {
        currentNumber = number;
        calculationTextSize();
        calculationTextChange();
        invalidate();
    }
}
