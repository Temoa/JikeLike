package me.temoa.jikelike;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;


public class LikeView extends View {

    private static final String TAG = "LikeView";

    private int width, height;

    private Paint mPaint;

    private int numHeight;
    private int numWidth;

    private int offset = 0;

    private int currentNumber = 0;

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

        calculationTextSize();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(300, numHeight);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawing(canvas);
    }

    private void drawing(Canvas canvas) {
        int nextNumber = currentNumber + 1;
        String currentNumberStr = String.valueOf(currentNumber);
        String nextNumberStr = String.valueOf(nextNumber);

        if (isAnim) {
            int bitCount = numberOfBits(currentNumberStr, nextNumberStr);
            Log.d(TAG, "drawing: bitCount: " + bitCount);

            String frontText = currentNumberStr.substring(0, currentNumberStr.length() - bitCount);
            Log.d(TAG, "drawing: frontText: " + frontText);

            int beginIndex = currentNumberStr.length() - bitCount - 1 < 0 ? 0 : currentNumberStr.length() - bitCount - 1;
            String behindText = currentNumberStr.substring(beginIndex, currentNumberStr.length());
            Log.d(TAG, "drawing: beginIndex: " + beginIndex + " behindText: " + behindText);

            int frontTextWidth = (int) mPaint.measureText(frontText);
            int behindTextWidth = (int) mPaint.measureText(behindText);

            int frontTextX = (width - numWidth) / 2;
            int x1 = frontTextX + frontTextWidth;
            int x2 = x1 + behindTextWidth;

            canvas.drawText(frontText, frontTextX, numHeight, mPaint);
            canvas.save();
            canvas.clipRect(x1, 0, x2, height);
            canvas.drawText(currentNumberStr, (width - numWidth) / 2, numHeight - offset, mPaint);
            canvas.drawText(nextNumberStr, (width - numWidth) / 2, numHeight * 2 - offset, mPaint);
            canvas.restore();
        } else {
            canvas.drawText(currentNumberStr, (width - numWidth) / 2, numHeight - offset, mPaint);
            canvas.drawText(nextNumberStr, (width - numWidth) / 2, numHeight * 2 - offset, mPaint);
        }
    }

    private int numberOfBits(String currentNum, String nextNum) {
        int diffBitCount = 0;
        for (int i = 0; i < currentNum.length(); i++) {
            if (currentNum.charAt(i) != nextNum.charAt(i)) {
                diffBitCount++;
            }
        }
        return diffBitCount;
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
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                isAnim = false;
                isPlaying = false;
            }
        });
        isAnim = true;
        isPlaying = true;
        numberAnim.start();
    }

    private void calculationTextSize() {
        Paint.FontMetrics fm = mPaint.getFontMetrics();
        numHeight = (int) (fm.bottom - fm.top);

        String targetStr = String.valueOf(currentNumber);
        numWidth = (int) mPaint.measureText(targetStr);

        Rect rect = new Rect();
        mPaint.getTextBounds(targetStr, 0, targetStr.length(), rect);
        numHeight = rect.height();
    }

    public void setNumber(int number) {
        currentNumber = number;
        calculationTextSize();
        invalidate();
    }
}
