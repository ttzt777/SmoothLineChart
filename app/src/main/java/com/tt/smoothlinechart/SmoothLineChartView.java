package com.tt.smoothlinechart;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

/**
 *
 * Created by tt on 2017/11/29.
 */

public class SmoothLineChartView extends View {
    private static final float MIN_WIDTH = 100f;            // dp
    private static final float MIN_HEIGHT = 20f;            // dp

    private static final int DEFAULT_BG = 0xFFFFFFFF;
    private static final int DEFAULT_LINE_COLOR = 0xFF00A6DA;
    private static final int DEFAULT_RADIUS_X = 10;
    private static final int DEFAULT_RADIUS_Y = 10;
    private static final float DEFAULT_LINES_PADDING = 1.5f;       // dp

    private static final int DEFAULT_VALUE_SIZE = 21;

    private Paint mPaint;

    // 存放折线点的Y轴偏移 0-100，最下偏移到最上偏移
    private byte[] mValues = new byte[DEFAULT_VALUE_SIZE];
    private int mHeadIndex;             // 循环数据头部索引

    private boolean mDrawLine;          // 当接收到新数据才初始化折线

    private int mWidth;
    private int mHeight;

    public SmoothLineChartView(Context context) {
        this(context, null);
    }

    public SmoothLineChartView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
    }

    public void addNewValue(int value) {
        if (value < 0) {
            value = 0;
        } else if (value > 100) {
            value = 100;
        }

        mValues[mHeadIndex] = (byte) value;
        mHeadIndex++;

        if (mHeadIndex >= DEFAULT_VALUE_SIZE) {
            mHeadIndex = 0;
        }

        mDrawLine = true;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        if (widthMode == MeasureSpec.AT_MOST && heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getMinWidth(), getMinHeight());
        } else if (widthMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(getMinWidth(), heightSize);
        } else if (heightMode == MeasureSpec.AT_MOST) {
            setMeasuredDimension(widthSize, getMinHeight());
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mWidth = getWidth() - getPaddingLeft() - getPaddingRight();
        mHeight = getHeight() - getPaddingTop() - getPaddingBottom();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBackground(canvas);
        drawLineChart(canvas);
    }

    private void drawBackground(Canvas canvas) {
        mPaint.setColor(DEFAULT_BG);
        mPaint.setStyle(Paint.Style.FILL);
        Rect rect = new Rect(getPaddingLeft(), getPaddingTop(), mWidth + getPaddingLeft(), mHeight + getPaddingTop());
        canvas.drawRoundRect(new RectF(rect), DEFAULT_RADIUS_X, DEFAULT_RADIUS_Y, mPaint);
    }

    private void drawLineChart(Canvas canvas) {
        if (!mDrawLine) {
            return;
        }

        int partitionCount = DEFAULT_VALUE_SIZE - 1;

        int left = getPaddingLeft() + dp2px(DEFAULT_LINES_PADDING);
        int top = getPaddingTop() + dp2px(DEFAULT_LINES_PADDING);
        int right = mWidth + getPaddingLeft() - dp2px(DEFAULT_LINES_PADDING);
        int bottom = mHeight + getPaddingTop() - dp2px(DEFAULT_LINES_PADDING);

        float partitionSizeX = (right - left) / (float) partitionCount;

        mPaint.setColor(DEFAULT_LINE_COLOR);
        mPaint.setStrokeWidth(3);
        mPaint.setStyle(Paint.Style.STROKE);
        Path path = new Path();

        for (int i = 0; i < partitionCount; i++) {
            byte currentValue = mValues[(mHeadIndex + i) % DEFAULT_VALUE_SIZE];
            byte nextValue = mValues[(mHeadIndex + i + 1) % DEFAULT_VALUE_SIZE];

            Point start = new Point((int)(left + partitionSizeX * i), bottom - (bottom - top) * currentValue / 100);
            Point end = new Point((int)(left + partitionSizeX * (i + 1)), bottom - (bottom - top) * nextValue / 100);
            int centerX = (start.x + end.x) / 2;
            Point control1 = new Point(centerX, start.y);
            Point control2 = new Point(centerX, end.y);

            path.moveTo(start.x, start.y);
            path.cubicTo(control1.x, control1.y, control2.x, control2.y, end.x, end.y);

            canvas.drawPath(path, mPaint);
        }
    }

    private int getMinWidth() {
        return dp2px(MIN_WIDTH);
    }

    private int getMinHeight() {
        return dp2px(MIN_HEIGHT);
    }

    private int dp2px(float dp) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, getContext().getResources().getDisplayMetrics());
    }
}
