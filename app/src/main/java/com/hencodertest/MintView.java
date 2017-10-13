package com.hencodertest;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.text.DecimalFormat;

/**
 * Created by pjj on 2017/10/13.
 */

public class MintView extends View {
    private static final String TAG = "MintView";
    private Paint mRulerPaint;
    private Paint mRulerTextPaint;

    private int mRulerSpaceSize = 40;
    private int mRulerHeight = 150;
    private int mRulerStartX;
    private int mRulerLineWidth = 5;

    private int mScreenWidth;
    private int mTotalWidth = (mRulerSpaceSize * 10 * 50);
    private float mPrevMovePos, mCurrentMovePos;
    private float mMoveDistance, mMoveOffset;

    private OnSizeChangeListener mOnSizeChangeListener;
    private DecimalFormat mDataFormat = new DecimalFormat("0.0");

    public MintView(Context context) {
        this(context, null);
    }

    public MintView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MintView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mScreenWidth = getResources().getDisplayMetrics().widthPixels;
        mRulerPaint = new Paint();
        mRulerPaint.setAntiAlias(true);
        mRulerPaint.setColor(getResources().getColor(R.color.ruler_color));

        mRulerTextPaint = new Paint();
        mRulerTextPaint.setAntiAlias(true);
        mRulerTextPaint.setColor(getResources().getColor(R.color.text_color));
        mRulerTextPaint.setTextSize(60);
    }

    @Override
    protected void dispatchDraw(Canvas canvas) {
        super.dispatchDraw(canvas);
        canvas.drawColor(Color.parseColor("#F4D03F"));
        drawPointer(canvas);
        drawRuler(canvas);
    }

    private void drawPointer(Canvas canvas) {
        mRulerStartX = mScreenWidth / 2;
        Rect pointer = new Rect(mRulerStartX, 0, mRulerStartX + 10, mRulerHeight);
        canvas.drawLine(pointer.left, 0, pointer.left + mTotalWidth, 0, mRulerPaint);
    }

    private void drawRuler(Canvas canvas) {
        int left = mRulerStartX;
        int top = 0;
        int lineHeight;
        int index = 0;
        String text = "0";
        Rect textBound = new Rect();
        for (int n = 0; n <= 500; n++) {
            if (n % 10 == 0 || n == 0) {
                lineHeight = mRulerHeight;
                mRulerTextPaint.getTextBounds(text, 0, text.length(), textBound);
                canvas.drawText(text, left - textBound.width() / 2, top + mRulerHeight + textBound.height() + 50, mRulerTextPaint);
                index += 1;
                text = Integer.toString(index);
            } else {
                lineHeight = mRulerHeight / 2;
            }
            canvas.drawRect(left, top, left + mRulerLineWidth, top + lineHeight, mRulerPaint);
            left += mRulerSpaceSize;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                mPrevMovePos = event.getX();
                break;
            case MotionEvent.ACTION_MOVE:
                handleActionMove(event);
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                handleActionUp();
                break;
        }
        return true;
    }

    private void handleActionMove(MotionEvent event) {
        mCurrentMovePos = event.getX();
        mMoveOffset = (mPrevMovePos - mCurrentMovePos);

        if ((mMoveOffset < 0 && mMoveDistance <= 0) || (mMoveOffset > 0 && mMoveDistance >= mTotalWidth)) {
            return;
        }

        scrollBy((int) mMoveOffset, 0);
        mMoveDistance += (int) mMoveOffset;
        mPrevMovePos = mCurrentMovePos;

        if (mOnSizeChangeListener != null) {
            onSizeChange();
        }
    }

    private void handleActionUp() {
        if (mMoveDistance < 0) {
            scrollBy((int) -mMoveDistance, 0);
            mMoveDistance = 0;
        }
        if (mMoveDistance > mTotalWidth) {
            scrollBy((int) -(mMoveDistance - mTotalWidth), 0);
            mMoveDistance = mTotalWidth;
        }

        mPrevMovePos = 0;
        mMoveOffset = 0;

        if (mOnSizeChangeListener != null) {
            onSizeChange();
        }
    }

    private void onSizeChange() {
        float pos = (mMoveDistance / (mRulerSpaceSize + mRulerLineWidth) / 10);
        mOnSizeChangeListener.onSizeChange(mDataFormat.format(pos) + "");
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSpec = MeasureSpec.makeMeasureSpec(mTotalWidth + mScreenWidth / 2, MeasureSpec.AT_MOST);
        int heightSpec = MeasureSpec.makeMeasureSpec(280, MeasureSpec.AT_MOST);
        setMeasuredDimension(widthSpec, heightSpec);
    }

    interface OnSizeChangeListener {
        void onSizeChange(String weight);
    }

    public void setOnSizeChangeListener(OnSizeChangeListener onSizeChangeListener) {
        this.mOnSizeChangeListener = onSizeChangeListener;
    }
}
