package com.yocn.af.view.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Region;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.yocn.af.R;
import com.yocn.af.util.DisplayUtil;
import com.yocn.af.util.LogUtil;

import androidx.annotation.Nullable;

public class WeChatVoiceBottomArc extends View {
    private final int HEIGHT_MARGIN = 20;
    private Paint paint;
    private String type;
    private int height;
    private int screenWidth;
    private int[] screenWH;
    private Path path;

    public WeChatVoiceBottomArc(Context context) {
        this(context, null);
    }

    public WeChatVoiceBottomArc(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeChatVoiceBottomArc(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray style = context.obtainStyledAttributes(attrs, R.styleable.WeChatVoiceBottomArc);
        try {
            type = style.getString(R.styleable.WeChatVoiceBottomArc_type);
        } finally {
            style.recycle();
        }
    }

    private boolean isLightMode() {
        return "light".equals(type);
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        init();
    }

    private void init() {
        boolean isLight = isLightMode();
        height = getContext().getResources().getDimensionPixelSize(isLight ? R.dimen.arc_height_light : R.dimen.arc_height_dark);
        screenWH = DisplayUtil.getHW(getContext());
        screenWidth = screenWH[0];
        path = new Path();
        if (isLight) {
            initLight();
        } else {
            initDark();
        }
    }

    private void initLight() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFFCCC7CC);
        paint.setStyle(Paint.Style.FILL);
        LinearGradient linearGradient = new LinearGradient(screenWidth / 2, 0, screenWidth / 2, height,
                0xFF999999, 0xFFe6e6e6, Shader.TileMode.CLAMP);
        paint.setShader(linearGradient);
    }

    private void initDark() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(0xFF4c4c4c);
        paint.setStyle(Paint.Style.FILL);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        path.moveTo(0, height / 2);
        path.cubicTo(screenWidth / 4, 0, screenWidth * 3 / 4, 0, screenWidth, height / 2);
        path.lineTo(screenWidth, height);
        path.lineTo(0, height);
        path.lineTo(0, height / 2);
        path.close();
        canvas.drawPath(path, paint);
    }

    public boolean isOnRect(float x, float y) {
        float viewY = getY();
        return isInTriangle(new Point(screenWidth / 2, 0), new Point(0, height), new Point(screenWidth, height), new Point((int) x, (int) (y - viewY)));
    }

    public boolean isInTriangle(Point A, Point B, Point C, Point P) {
        double ABC = triAngleArea(A, B, C);
        double ABp = triAngleArea(A, B, P);
        double ACp = triAngleArea(A, C, P);
        double BCp = triAngleArea(B, C, P);
        if ((int) ABC == (int) (ABp + ACp + BCp)) {// 若面积之和等于原三角形面积，证明点在三角形内,这里做了一个约等于小数点之后没有算（25714.25390625、25714.255859375）
            return true;
        } else {
            return false;
        }
    }

    private double triAngleArea(Point A, Point B, Point C) {// 由三个点计算这三个点组成三角形面积
        double result = Math.abs((A.x * B.y + B.x * C.y
                + C.x * A.y - B.x * A.y - C.x
                * B.y - A.x * C.y) / 2.0D);
        return result;
    }

}