package com.example.epicsrenewal;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import java.util.ArrayList;

public class ChartView extends View {
    private Paint linePaint;
    private Paint borderPaint;
    private ArrayList<Float> lineDataPoints;

    public ChartView(Context context) {
        super(context);
        init();
    }

    public ChartView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ChartView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        linePaint = new Paint();
        linePaint.setColor(Color.RED);
        linePaint.setStrokeWidth(7);
        linePaint.setAntiAlias(true);

        borderPaint = new Paint();
        borderPaint.setColor(Color.BLACK);
        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(4);
    }


    public void setLineDataPoints(ArrayList<Float> dataPoints) {
        lineDataPoints = dataPoints;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        float chartWidth = getWidth();
        float chartHeight = getHeight();

        // Draw the border
        RectF borderRect = new RectF(0, 0, chartWidth, chartHeight);
        canvas.drawRect(borderRect, borderPaint);

        // Calculate chart content area without border
        float contentLeft = getPaddingLeft();
        float contentTop = getPaddingTop();
        float contentRight = chartWidth - getPaddingRight();
        float contentBottom = chartHeight - getPaddingBottom();

        // Draw the line chart
        if (lineDataPoints != null && lineDataPoints.size() >= 4) {
            for (int i = 0; i < lineDataPoints.size() - 2; i += 2) {
                float x1 = lineDataPoints.get(i);
                float y1 = lineDataPoints.get(i+1);
                float x2 = lineDataPoints.get(i+2);
                float y2 = lineDataPoints.get(i+3);
                canvas.drawLine(contentRight-x1, y1, contentRight-x2, y2, linePaint);
            }
        }
    }
}
