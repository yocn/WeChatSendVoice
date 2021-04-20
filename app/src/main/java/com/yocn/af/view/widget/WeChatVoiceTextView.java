package com.yocn.af.view.widget;

import android.content.Context;
import android.os.Vibrator;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatTextView;

public class WeChatVoiceTextView extends AppCompatTextView {
    private String TAG = "WeChatVoiceTextView";
    private WeChatParentViewGroup.OnVoiceViewStatusListener onVoiceViewStatusListener;

    public WeChatVoiceTextView(@NonNull Context context) {
        super(context);
    }

    public WeChatVoiceTextView(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public WeChatVoiceTextView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setListener(WeChatParentViewGroup.OnVoiceViewStatusListener onVoiceViewStatusListener) {
        this.onVoiceViewStatusListener = onVoiceViewStatusListener;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        boolean result = super.dispatchTouchEvent(ev);
//        LogUtil.d(TAG + "::dispatchTouchEvent:::" + result + "  " + ViewUtil.printEvent(ev));
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            vibrator(getContext());
            onVoiceViewStatusListener.showVoiceView();
            setText("松开 结束");
        }
        if (ev.getAction() == MotionEvent.ACTION_CANCEL || ev.getAction() == MotionEvent.ACTION_UP) {
            setText("按住 说话");
        }
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = super.onTouchEvent(event);
//        LogUtil.d(TAG + "::onTouchEvent::" + result + "  " + ViewUtil.printEvent(event));
        return true;
    }

    private void vibrator(Context context) {
        Vibrator vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
        long[] patter = {0, 100};
        vibrator.vibrate(patter, -1);
    }
}
