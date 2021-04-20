package com.yocn.af.view.widget;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;

import com.yocn.af.R;
import com.yocn.af.util.LogUtil;

/**
 * https://blog.csdn.net/xyz_lmn/article/details/12517911
 * <p>
 * public boolean dispatchTouchEvent(MotionEvent ev) {
 * boolean consume = false;//事件是否被消费
 * if (onInterceptTouchEvent(ev)){//调用onInterceptTouchEvent判断是否拦截事件
 * consume = onTouchEvent(ev);//如果拦截则调用自身的onTouchEvent方法
 * }else{
 * consume = child.dispatchTouchEvent(ev);//不拦截调用子View的dispatchTouchEvent方法
 * }
 * return consume;//返回值表示事件是否被消费，true事件终止，false调用父View的onTouchEvent方法
 * }
 */
public class WeChatParentViewGroup extends RelativeLayout {
    private String TAG = "WeChatParentViewGroup";
    private WeChatVoiceTextView voiceTv;
    private WeChatVoiceView voiceView;
    private Rect voiceRect;

    public interface OnVoiceViewStatusListener {
        public void showVoiceView();
    }

    public WeChatParentViewGroup(Context context) {
        super(context);
        init();
    }

    public WeChatParentViewGroup(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public WeChatParentViewGroup(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
    }

    OnVoiceViewStatusListener onVoiceViewStatusListener = new OnVoiceViewStatusListener() {
        @Override
        public void showVoiceView() {
            voiceView.setVisibility(View.VISIBLE);
            voiceView.doStart();
        }
    };

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            voiceView.setVisibility(View.GONE);
            voiceView.doDefault();
        }
//        LogUtil.d(TAG + "::dispatchTouchEvent::" + result + "  " + ViewUtil.printEvent(ev));
        return result;
    }

    boolean hasIntercepted = false;

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        if (voiceRect == null) {
            voiceTv = findViewById(R.id.tv_voice);
            voiceView = findViewById(R.id.voice_view);
            final int[] locations = new int[2];
            voiceTv.getLocationOnScreen(locations);
            voiceTv.setListener(onVoiceViewStatusListener);
            voiceRect = new Rect(locations[0], locations[1], locations[0] + voiceTv.getWidth(), locations[1] + voiceTv.getHeight());
            LogUtil.d(TAG + "::voiceRect::" + voiceRect.toShortString());
        }
        float x = ev.getX();
        float y = ev.getY();
        boolean result;
        if (hasIntercepted) {
            result = false;
            hasIntercepted = false;
        } else {
            if (pointInRect(x, y, voiceRect)) {
                result = false;
            } else {
                hasIntercepted = true;
                result = true;
            }
        }

//        LogUtil.d(TAG + "::onInterceptTouchEvent::" + result + "  " + ViewUtil.printEvent(ev) + "      " + x + "/" + y + ":" + voiceRect.toShortString());
        return result;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = voiceView.onTouchEvent(event);
//        LogUtil.d(TAG + "::onTouchEvent::" + result + "    ");
        return result;
    }

    public static boolean pointInRect(float x, float y, Rect rect) {
        return x > rect.left && x < rect.right && y > rect.top && y < rect.bottom;
    }
}
