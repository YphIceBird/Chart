package cn.yph.library;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author penghao
 * @Date 2017/8/9
 */

public class LineChart extends View {

    private static final String TAG = "YPH";

    //根据最大值和刻度来画x轴和y轴的坐标
    private float yMaxValue = 100;

    private int yPieces = 4;

    private float xMaxValue = 10;

    private int xPieces = 4;

    private List<PointData> mData;

    private Path linePath;

    private Paint linePaint;

    private Path coordinatePath;

    private Paint coordinatePaint;

    private Paint coordinateTextPaint;

    private String[] xCoordinateList;

    private String[] yCoordinateList;

    private int width;

    private int height;

    //坐标系Rect
    private Rect coordinateRect;

    //Y轴文字Rect
    private Rect yTextRect = new Rect();

    private int yTextMargin;

    //X轴文字Rect
    private Rect xTextRect = new Rect();

    private int xTextMargin;

    private boolean drawStartX = true;

    private boolean drawStartY = true;

    private float touchX;

    private float touchY;

    public LineChart(Context context) {
        super(context);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public LineChart(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        width = MeasureSpec.getSize(widthMeasureSpec);
        height = MeasureSpec.getSize(heightMeasureSpec);


        coordinateRect.left = yTextRect.width() + getPaddingLeft() + xTextMargin;
        coordinateRect.top = getPaddingTop() + yTextRect.height() / 2;
        coordinateRect.right = width - coordinateRect.left;
        coordinateRect.bottom = height - getPaddingBottom() - xTextRect.height() - yTextMargin;

        linePath.reset();
        for (int pos = 0; pos < mData.size(); pos++) {
            PointData pointData = mData.get(pos);
            float[] coordinatePos = convertPoint(pointData);
            if (pos == 0) {
                linePath.moveTo(coordinatePos[0], coordinatePos[1]);
            } else {
                linePath.lineTo(coordinatePos[0], coordinatePos[1]);
            }
        }

    }

    private void init() {
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStyle(Paint.Style.STROKE);
        linePaint.setColor(Color.BLUE);
        linePaint.setStrokeWidth(Utils.dp2px(getContext(), 2));

        coordinatePaint = new Paint();
        coordinatePaint.setAntiAlias(true);
        coordinatePaint.setStrokeWidth(Utils.dp2px(getContext(), 1));
        coordinatePaint.setStyle(Paint.Style.STROKE);

        coordinateTextPaint = new Paint();
        coordinateTextPaint.setAntiAlias(true);
        coordinateTextPaint.setTextSize(Utils.sp2px(getContext(), 13));
        coordinateTextPaint.setColor(Color.YELLOW);

        linePath = new Path();

        coordinatePath = new Path();

        String yTmpText = "100.00";
        coordinateTextPaint.getTextBounds(yTmpText, 0, yTmpText.length(), yTextRect);
        coordinateTextPaint.getTextBounds(yTmpText, 0, yTmpText.length(), xTextRect);

        yTextMargin = Utils.dp2px(getContext(), 5);
        xTextMargin = Utils.dp2px(getContext(), 3);

        coordinateRect = new Rect();
    }

    //根据数据返回每个点的x，y坐标
    private float[] convertPoint(PointData data) {
        float yLineLen = coordinateRect.height();
        float y = (1 - data.getValue() / yMaxValue) * yLineLen + coordinateRect.top;
        float x = data.getTime() / xMaxValue * coordinateRect.width() + coordinateRect.left;
        return new float[]{x, y};
    }

    public void setData(List<PointData> data) {
        this.mData = new ArrayList<>();
        mData.addAll(data);
    }

    private boolean isOnTouch = false;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                touchX = event.getX();
                touchY = event.getY();
                isOnTouch = coordinateRect.contains((int) touchX, (int) touchY);
                invalidate();
                return true;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                isOnTouch = false;
                invalidate();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        drawCoordinateSystem(canvas);
        drawLines(canvas);
        drawTouchValue(canvas);
    }

    private void drawTouchValue(Canvas canvas) {
        if (isOnTouch) {
            canvas.drawLine(touchX, coordinateRect.bottom, touchX, coordinateRect.top, coordinatePaint);
            canvas.drawLine(coordinateRect.left, touchY, coordinateRect.right, touchY, coordinatePaint);
            float xValue = (touchX - coordinateRect.left) / coordinateRect.width() * xMaxValue;
            float yValue = (coordinateRect.bottom - touchY) / coordinateRect.height() * yMaxValue;

            canvas.drawText("(x:" + xValue + ",y:" + yValue + ")", touchX, touchY, coordinateTextPaint);
        }
    }


    private void drawCoordinateSystem(Canvas canvas) {

        coordinatePath.moveTo(coordinateRect.left, coordinateRect.top);
        coordinatePath.lineTo(coordinateRect.left, coordinateRect.bottom);
        coordinatePath.lineTo(coordinateRect.right, coordinateRect.bottom);
        canvas.drawPath(coordinatePath, coordinatePaint);

        drawXCoordinate(canvas);

        drawYCoordinate(canvas);
    }

    private void drawXCoordinate(Canvas canvas) {
        Paint.FontMetrics fontMetrics = coordinateTextPaint.getFontMetrics();
        float y = height - fontMetrics.bottom - getPaddingBottom();

        for (int i = 0; i <= xPieces; i++) {
            if (!isDrawStartX() && i == 0) {
                continue;
            }
            String xBlockName = getXCoordinateValue(i);
            float xLine = i * coordinateRect.width() / xPieces + coordinateRect.left;
            float x = xLine - coordinateTextPaint.measureText(xBlockName) / 2;
            canvas.drawText(xBlockName, x, y, coordinateTextPaint);

            canvas.drawLine(xLine, coordinateRect.bottom, xLine, coordinateRect.top, coordinatePaint);
        }
    }


    private String getXCoordinateValue(int index) {
        String value;
        if (xCoordinateList != null && xCoordinateList.length != 0) {
            value = xCoordinateList[index];
        } else {
            value = String.valueOf(xMaxValue / xPieces * index);
        }
        return value;
    }

    private String getYCoordinateValue(int index) {
        String value;
        if (yCoordinateList != null && yCoordinateList.length != 0) {
            value = yCoordinateList[index];
        } else {
            value = String.valueOf(yMaxValue / yPieces * (yPieces - index));
        }
        return value;
    }

    private void drawYCoordinate(Canvas canvas) {
        Paint.FontMetrics fontMetrics = coordinateTextPaint.getFontMetrics();
        for (int i = yPieces; i >= 0; i--) {
            if (!isDrawStartY() && i == yPieces) {
                continue;
            }
            float yLine = coordinateRect.height() / yPieces * i + coordinateRect.top;
            float y = yLine + (fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom;
            String yValue = getYCoordinateValue(i);
            float x = coordinateRect.left - yTextMargin - coordinateTextPaint.measureText(yValue);
            canvas.drawText(yValue, x, y, coordinateTextPaint);

            canvas.drawLine(coordinateRect.left, yLine, coordinateRect.right, yLine, coordinatePaint);
        }

    }

    private void drawLines(Canvas canvas) {

        canvas.drawPath(linePath, linePaint);
    }


    public boolean isDrawStartX() {
        return drawStartX;
    }

    public boolean isDrawStartY() {
        return drawStartY;
    }
}
