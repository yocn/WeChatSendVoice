package com.yocn.af.view.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.yocn.af.R;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Arrays;

import androidx.annotation.IntDef;
import androidx.annotation.Nullable;

public class WeChatVoiceBubble extends View {
    // cancel、trans状态下的音波长度
    private final int NUM_CANCEL_VOICE = 10;
    // recording状态下的音波长度
    private final int NUM_RECORD_VOICE = 24;
    // 音波最短的高度
    private final int MIN_VOICE_HEIGHT = 10;
    private final int MAX_VOICE_HEIGHT = 24;
    // 无声音下音波循环波纹最短的长度
    private final int MIN_VOICE_SIMULATE_LENGTH = 10;
    // 音波线宽度
    private final int VOICE_LINE_WIDTH = 4;
    // 音波线之间间隔的宽度
    private final int VOICE_DIVIDER_WIDTH = 4;

    private Paint redPaint;
    private Paint greenPaint;
    private Paint writePaint;
    private Paint currPaint;
    private RectF translateRectF;
    private RectF cancelRectF;
    private RectF centerRectF;
    private RectF currRectF;
    private RectF targetRectF;
    private final PointF[] translateTrianglePoints = new PointF[3];
    private final PointF[] cancelTrianglePoints = new PointF[3];
    private final PointF[] centerTrianglePoints = new PointF[3];
    private final PointF[] currTrianglePoints = new PointF[3];
    private PointF[] targetTrianglePoints = new PointF[3];
    private float triangleHeight;
    private Path trianglePath;
    private final float triangleLine = getResources().getDimensionPixelOffset(R.dimen.height_triangle_line);
    private final int topDivider = getResources().getDimensionPixelOffset(R.dimen.height_top_divider);
    private float deltaLeftX = 0, deltaRightX = 0, deltaTopY = 0, deltaTriangleX = 0, deltaVoiceX = 0, deltaVoiceY = 0;
    private final int[] cancelVoiceData = new int[NUM_CANCEL_VOICE];
    private final int[] centerVoiceData = new int[NUM_RECORD_VOICE];
    private int cancelCurrIndex = NUM_CANCEL_VOICE + MIN_VOICE_SIMULATE_LENGTH, centerCurrIndex = NUM_RECORD_VOICE;
    private float cancelLineViewWidth, centerLineViewWidth, translateLineViewWidth;
    private RectF translateVoiceRectF;
    private RectF cancelVoiceRectF;
    private RectF centerVoiceRectF;
    private RectF currVoiceRectF;
    private RectF targetVoiceRectF;
    private boolean recording = true;
    private int controlSpeed = 0;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            SHOW_TYPE.TYPE_CENTER,
            SHOW_TYPE.TYPE_CANCEL,
            SHOW_TYPE.TYPE_TRANSLATE
    })
    public @interface SHOW_TYPE {
        int TYPE_CENTER = 101;
        int TYPE_CANCEL = 102;
        int TYPE_TRANSLATE = 103;
    }

    public WeChatVoiceBubble(Context context) {
        this(context, null);
    }

    public WeChatVoiceBubble(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeChatVoiceBubble(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    public void onWindowFocusChanged(boolean hasWindowFocus) {
        super.onWindowFocusChanged(hasWindowFocus);
    }

    private void init() {
        greenPaint = new Paint();
        greenPaint.setAntiAlias(true);
        greenPaint.setColor(0xFF00cb32);
        greenPaint.setStyle(Paint.Style.FILL);
        redPaint = new Paint();
        redPaint.setAntiAlias(true);
        redPaint.setColor(0xFFcb3a35);
        redPaint.setStyle(Paint.Style.FILL);
        writePaint = new Paint();
        writePaint.setAntiAlias(true);
        writePaint.setColor(0xFFffffff);
        writePaint.setStyle(Paint.Style.FILL);
        writePaint.setStrokeWidth(VOICE_LINE_WIDTH);
        currPaint = greenPaint;
        triangleHeight = (float) Math.sqrt(Math.pow(triangleLine, 2) - Math.pow(triangleLine / 2, 2));
        for (int i = 0; i < NUM_CANCEL_VOICE; i++) {
            cancelVoiceData[i] = MIN_VOICE_HEIGHT;
        }
        for (int i = 0; i < NUM_RECORD_VOICE; i++) {
            centerVoiceData[i] = MIN_VOICE_HEIGHT;
        }
        cancelLineViewWidth = translateLineViewWidth = NUM_CANCEL_VOICE * VOICE_LINE_WIDTH + (NUM_CANCEL_VOICE - 1) * VOICE_DIVIDER_WIDTH;
        centerLineViewWidth = NUM_RECORD_VOICE * VOICE_LINE_WIDTH + (NUM_RECORD_VOICE - 1) * VOICE_DIVIDER_WIDTH;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int height = getResources().getDimensionPixelSize(R.dimen.height_round_rect);
        float width = getMeasuredWidth();
        if (translateRectF == null) {
            translateRectF = new RectF(0, 0, width, height - triangleHeight);
            cancelRectF = new RectF(0, topDivider, height - triangleHeight, height - triangleHeight);
            centerRectF = new RectF(width / 2 - (height - triangleHeight), topDivider, width / 2 + (height - triangleHeight), height - triangleHeight);

            translateTrianglePoints[0] = new PointF(width - cancelRectF.width() / 2 - triangleLine / 2, height - triangleHeight - 1);
            translateTrianglePoints[1] = new PointF(width - cancelRectF.width() / 2, height);
            translateTrianglePoints[2] = new PointF(width - cancelRectF.width() / 2 + triangleLine / 2, height - triangleHeight - 1);

            cancelTrianglePoints[0] = new PointF(cancelRectF.width() / 2 - triangleLine / 2, height - triangleHeight - 1);
            cancelTrianglePoints[1] = new PointF(cancelRectF.width() / 2, height);
            cancelTrianglePoints[2] = new PointF(cancelRectF.width() / 2 + triangleLine / 2, height - triangleHeight - 1);

            centerTrianglePoints[0] = new PointF(width / 2 - triangleLine / 2, height - triangleHeight - 1);
            centerTrianglePoints[1] = new PointF(width / 2, height);
            centerTrianglePoints[2] = new PointF(width / 2 + triangleLine / 2, height - triangleHeight - 1);

            int translateLineViewRightMargin = 50;
            translateVoiceRectF = new RectF(
                    width - translateLineViewRightMargin - translateLineViewWidth,
                    height - triangleHeight - translateLineViewRightMargin - MAX_VOICE_HEIGHT,
                    width - translateLineViewRightMargin,
                    height - triangleHeight - translateLineViewRightMargin);

            float VOICE_LINE_VIEW_HEIGHT = MAX_VOICE_HEIGHT;
            cancelVoiceRectF = new RectF(cancelRectF.left + cancelRectF.width() / 2 - cancelLineViewWidth / 2,
                    cancelRectF.top + cancelRectF.height() / 2 - VOICE_LINE_VIEW_HEIGHT / 2,
                    cancelRectF.left + cancelRectF.width() / 2 + cancelLineViewWidth / 2,
                    cancelRectF.top + cancelRectF.height() / 2 + VOICE_LINE_VIEW_HEIGHT / 2);

            centerVoiceRectF = new RectF(centerRectF.left + centerRectF.width() / 2 - centerLineViewWidth / 2,
                    centerRectF.top + centerRectF.height() / 2 - VOICE_LINE_VIEW_HEIGHT / 2,
                    centerRectF.left + centerRectF.width() / 2 + centerLineViewWidth / 2,
                    centerRectF.top + centerRectF.height() / 2 + VOICE_LINE_VIEW_HEIGHT / 2);

            currVoiceRectF = new RectF(centerVoiceRectF);
            trianglePath = new Path();
            currRectF = new RectF(centerRectF);
            currTrianglePoints[0] = new PointF(centerTrianglePoints[0].x, centerTrianglePoints[0].y);
            currTrianglePoints[1] = new PointF(centerTrianglePoints[1].x, centerTrianglePoints[1].y);
            currTrianglePoints[2] = new PointF(centerTrianglePoints[2].x, centerTrianglePoints[2].y);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 圆角矩形
        refreshRectRectF();
        canvas.drawRoundRect(currRectF, 50, 50, currPaint);
        // 三角形
        refreshTriangleRectF();
        trianglePath.reset();
        trianglePath.setFillType(Path.FillType.EVEN_ODD);
        trianglePath.moveTo(currTrianglePoints[0].x, currTrianglePoints[0].y);
        trianglePath.lineTo(currTrianglePoints[1].x, currTrianglePoints[1].y);
        trianglePath.lineTo(currTrianglePoints[2].x, currTrianglePoints[2].y);
        trianglePath.close();
        canvas.drawPath(trianglePath, currPaint);

        startSimulateVoice();
        refreshVoiceRectF();
        float centerLineY = currVoiceRectF.top + currVoiceRectF.height() / 2;
        float lineStartX = currVoiceRectF.left;
        int[] currData = getVoiceLineData();
        // voiceView
        for (int i = 0; i < currData.length; i++) {
            canvas.drawLine(lineStartX + getLineStartX(i), centerLineY - currData[i] * 1f / 2,
                    lineStartX + getLineStartX(i), centerLineY + currData[i] * 1f / 2, writePaint);
        }
    }

    private int getLineStartX(int index) {
        int x = index * VOICE_LINE_WIDTH;
        if (x > 0) {
            x += index * VOICE_DIVIDER_WIDTH;
        }
        return x;
    }

    private int[] getVoiceLineData() {
        return recording ? centerVoiceData : cancelVoiceData;
    }

    private void refreshRectRectF() {
        if (!isSameRectRectF()) {
            currRectF.top += deltaTopY;
            currRectF.left += deltaLeftX;
            currRectF.right += deltaRightX;
            invalidate();
        }
    }

    private void refreshTriangleRectF() {
        if (!isSameTriangleRectF()) {
            currTrianglePoints[0].x += deltaTriangleX;
            currTrianglePoints[1].x += deltaTriangleX;
            currTrianglePoints[2].x += deltaTriangleX;
            invalidate();
        }
    }

    private void refreshVoiceRectF() {
        if (!isSameVoiceRectF()) {
            currVoiceRectF.left += deltaVoiceX;
            currVoiceRectF.top += deltaVoiceY;
            invalidate();
        }
    }

    private boolean isSameRectRectF() {
        if (targetRectF == null) {
            return true;
        }
        return Math.abs((currRectF.right - currRectF.left) - (targetRectF.right - targetRectF.left)) < 10;
    }

    private boolean isSameTriangleRectF() {
        if (targetTrianglePoints == null || targetTrianglePoints[0] == null) {
            return true;
        }
        return Math.abs(targetTrianglePoints[0].x - currTrianglePoints[0].x) < 10;
    }

    private boolean isSameVoiceRectF() {
        if (targetVoiceRectF == null) {
            return true;
        }
        return Math.abs(currVoiceRectF.left - targetVoiceRectF.left) < 10;
    }

    public void setShowType(@SHOW_TYPE int type) {
        switch (type) {
            case SHOW_TYPE.TYPE_CENTER:
                targetRectF = centerRectF;
                targetTrianglePoints = centerTrianglePoints;
                currPaint = greenPaint;
                targetVoiceRectF = centerVoiceRectF;
                recording = true;
                break;
            case SHOW_TYPE.TYPE_CANCEL:
                targetRectF = cancelRectF;
                targetTrianglePoints = cancelTrianglePoints;
                currPaint = redPaint;
                targetVoiceRectF = cancelVoiceRectF;
                recording = false;
                break;
            case SHOW_TYPE.TYPE_TRANSLATE:
                targetRectF = translateRectF;
                targetTrianglePoints = translateTrianglePoints;
                currPaint = greenPaint;
                targetVoiceRectF = translateVoiceRectF;
                recording = false;
                break;
            default:
        }
        int num = 10;
        deltaTopY = (targetRectF.top - currRectF.top) / num;
        deltaLeftX = (targetRectF.left - currRectF.left) / num;
        deltaRightX = (targetRectF.right - currRectF.right) / num;
        deltaTriangleX = (targetTrianglePoints[0].x - currTrianglePoints[0].x) / num;
        deltaVoiceX = (targetVoiceRectF.left - currVoiceRectF.left) / num;
        deltaVoiceY = (targetVoiceRectF.top - currVoiceRectF.top) / num;
        invalidate();
    }

    private void startSimulateVoice() {
        int VOICE_SPEED = 6;
        if (controlSpeed++ < VOICE_SPEED) {
            invalidate();
            return;
        }
        controlSpeed = 0;
        if (cancelCurrIndex <= 0) {
            cancelCurrIndex = NUM_CANCEL_VOICE + MIN_VOICE_SIMULATE_LENGTH;
        }
        if (centerCurrIndex <= 0) {
            centerCurrIndex = NUM_RECORD_VOICE + MIN_VOICE_SIMULATE_LENGTH;
        }
        if (recording) {
            centerCurrIndex--;
            Arrays.fill(centerVoiceData, MIN_VOICE_HEIGHT);
            for (int i = centerCurrIndex - (MIN_VOICE_SIMULATE_LENGTH / 2); i < centerCurrIndex + (MIN_VOICE_SIMULATE_LENGTH / 2); i++) {
                if (i > 0 && i < centerVoiceData.length) {
                    // radio范围[0,1]，离centerCurrIndex越近越靠近1
                    float radio = 1f - 1f * Math.abs(i - centerCurrIndex) / (1f * MIN_VOICE_SIMULATE_LENGTH / 2);
                    centerVoiceData[i] = (int) (MIN_VOICE_HEIGHT + radio * (MAX_VOICE_HEIGHT - MIN_VOICE_HEIGHT));
                }
            }
        } else {
            cancelCurrIndex--;
            Arrays.fill(cancelVoiceData, MIN_VOICE_HEIGHT);
            for (int i = cancelCurrIndex - (MIN_VOICE_SIMULATE_LENGTH / 2); i < cancelCurrIndex + (MIN_VOICE_SIMULATE_LENGTH / 2); i++) {
                if (i > 0 && i < cancelVoiceData.length) {
                    // radio范围[0,1]，离centerCurrIndex越近越靠近1
                    float radio = 1f - 1f * Math.abs(i - cancelCurrIndex) / (1f * MIN_VOICE_SIMULATE_LENGTH / 2);
                    cancelVoiceData[i] = (int) (MIN_VOICE_HEIGHT + radio * (MAX_VOICE_HEIGHT - MIN_VOICE_HEIGHT));
                }
            }
        }
        invalidate();
    }
}
