package com.yocn.af.view.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.yocn.af.R;
import com.yocn.af.util.DisplayUtil;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class WeChatVoiceView extends FrameLayout {
    private String TAG = "WeChatVoiceView";
    private WeChatVoiceBottomArc weChatVoiceBottomArcLight;
    private WeChatVoiceBottomArc weChatVoiceBottomArcDark;
    private final int ANIM_DURATION = 300;
    private final int ANIM_DURATION_TEXT = 500;
    private final int ANIM_DURATION_TEXT_BIGGER = 100;
    private ImageView voiceIv;
    private int bottomArcTransY;
    private TextView voiceTv;
    private TextView cancelTv;
    private TextView translateTv;
    private ObjectAnimator darkAlphaAnim;
    private ObjectAnimator lightAlphaAnim;
    boolean currentArcLight = true;
    boolean lightAniming = false;
    boolean darkAniming = false;
    private AnimatorSet bottomArcSet;
    private AnimatorSet textAnimSet;
    private int[] screenWH;
    private AnimatorSet cancelTvScaleBiggerAnim;
    private AnimatorSet translateTvScaleBiggerAnim;
    private AnimatorSet cancelTvScaleSmallAnim;
    private AnimatorSet translateTvScaleSmallAnim;
    private TextView cancelHintTv;
    private TextView translateHintTv;
    private WeChatVoiceBubble weChatVoiceBubble;

    public WeChatVoiceView(@NonNull Context context) {
        this(context, null);
    }

    public WeChatVoiceView(@NonNull Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeChatVoiceView(@NonNull Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_voice, this);
        initView();
        initAnimation();
    }

    private void initView() {
        voiceIv = findViewById(R.id.iv_voice);
        weChatVoiceBottomArcLight = findViewById(R.id.bottom_arc_light);
        weChatVoiceBottomArcDark = findViewById(R.id.bottom_arc_dark);
        weChatVoiceBubble = findViewById(R.id.bubble);
        cancelTv = findViewById(R.id.tv_cancel);
        translateTv = findViewById(R.id.tv_trans);
        bottomArcTransY = getResources().getDimensionPixelOffset(R.dimen.arc_height_light);
        voiceTv = findViewById(R.id.voice);
        cancelHintTv = findViewById(R.id.tv_cancel_text);
        translateHintTv = findViewById(R.id.tv_trans_text);
        screenWH = DisplayUtil.getHW(getContext());
    }

    private void initAnimation() {
        ObjectAnimator bottomArcTransYAnim = ObjectAnimator.ofFloat(weChatVoiceBottomArcLight, "translationY", bottomArcTransY, 0);
        ObjectAnimator bottomArcAlphaAnim = ObjectAnimator.ofFloat(weChatVoiceBottomArcLight, "alpha", 0f, 1f);
        ObjectAnimator voiceTvTransYAnim = ObjectAnimator.ofFloat(voiceTv, "translationY", 300, 0);
        ObjectAnimator voiceTvAlphaAnim = ObjectAnimator.ofFloat(voiceTv, "alpha", 0f, 1f);
        weChatVoiceBottomArcLight.setVisibility(View.VISIBLE);
        bottomArcSet = new AnimatorSet();
        bottomArcSet.playTogether(bottomArcTransYAnim, bottomArcAlphaAnim, voiceTvTransYAnim, voiceTvAlphaAnim);
        bottomArcSet.setDuration(ANIM_DURATION);
        bottomArcSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                weChatVoiceBottomArcDark.setVisibility(View.VISIBLE);
            }
        });
        ObjectAnimator cancelTvTransYAnim = ObjectAnimator.ofFloat(cancelTv, "translationY", 100, 0);
        ObjectAnimator cancelTvAlphaAnim = ObjectAnimator.ofFloat(cancelTv, "alpha", 0f, 1f);
        ObjectAnimator translateTvTransYAnim = ObjectAnimator.ofFloat(translateTv, "translationY", 100, 0);
        ObjectAnimator translateTvAlphaAnim = ObjectAnimator.ofFloat(translateTv, "alpha", 0f, 1f);
        textAnimSet = new AnimatorSet();
        textAnimSet.playTogether(cancelTvTransYAnim, cancelTvAlphaAnim, translateTvTransYAnim, translateTvAlphaAnim);
        textAnimSet.setDuration(ANIM_DURATION_TEXT);

        darkAlphaAnim = ObjectAnimator.ofFloat(weChatVoiceBottomArcLight, "alpha", 1f, 0f);
        darkAlphaAnim.setDuration(100);
        darkAlphaAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                darkAniming = false;
                currentArcLight = false;
                voiceIv.setImageResource(R.drawable.ic_voice_dark);
            }
        });

        lightAlphaAnim = ObjectAnimator.ofFloat(weChatVoiceBottomArcLight, "alpha", 0f, 1f);
        lightAlphaAnim.setDuration(100);
        lightAlphaAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                lightAniming = false;
                currentArcLight = true;
                voiceIv.setImageResource(R.drawable.ic_voice);
            }
        });

        float src = 1f, tar = 1.2f;
        ObjectAnimator cancelTvScaleXBiggerAnim = ObjectAnimator.ofFloat(cancelTv, "scaleX", src, tar);
        ObjectAnimator cancelTvScaleYBiggerAnim = ObjectAnimator.ofFloat(cancelTv, "scaleY", src, tar);
        ObjectAnimator cancelTvScaleXSmallAnim = ObjectAnimator.ofFloat(cancelTv, "scaleX", tar, src);
        ObjectAnimator cancelTvScaleYSmallAnim = ObjectAnimator.ofFloat(cancelTv, "scaleY", tar, src);

        cancelTvScaleBiggerAnim = new AnimatorSet();
        cancelTvScaleBiggerAnim.playTogether(cancelTvScaleXBiggerAnim, cancelTvScaleYBiggerAnim);
        cancelTvScaleBiggerAnim.setDuration(ANIM_DURATION_TEXT_BIGGER);
        cancelTvScaleBiggerAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                cancelTvAnimationing = false;
                cancelTvBig = true;
            }
        });
        cancelTvScaleSmallAnim = new AnimatorSet();
        cancelTvScaleSmallAnim.playTogether(cancelTvScaleXSmallAnim, cancelTvScaleYSmallAnim);
        cancelTvScaleSmallAnim.setDuration(ANIM_DURATION_TEXT_BIGGER);
        cancelTvScaleSmallAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                cancelTvAnimationing = false;
                cancelTvBig = false;
            }
        });

        ObjectAnimator translateTvScaleXBiggerAnim = ObjectAnimator.ofFloat(translateTv, "scaleX", src, tar);
        ObjectAnimator translateTvScaleYBiggerAnim = ObjectAnimator.ofFloat(translateTv, "scaleY", src, tar);
        ObjectAnimator translateTvScaleXSmallAnim = ObjectAnimator.ofFloat(translateTv, "scaleX", tar, src);
        ObjectAnimator translateTvScaleYSmallAnim = ObjectAnimator.ofFloat(translateTv, "scaleY", tar, src);

        translateTvScaleBiggerAnim = new AnimatorSet();
        translateTvScaleBiggerAnim.playTogether(translateTvScaleXBiggerAnim, translateTvScaleYBiggerAnim);
        translateTvScaleBiggerAnim.setDuration(ANIM_DURATION_TEXT_BIGGER);
        translateTvScaleBiggerAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                translateTvAnimationing = false;
                translateTvBig = true;
            }
        });
        translateTvScaleSmallAnim = new AnimatorSet();
        translateTvScaleSmallAnim.playTogether(translateTvScaleXSmallAnim, translateTvScaleYSmallAnim);
        translateTvScaleSmallAnim.setDuration(ANIM_DURATION_TEXT_BIGGER);
        translateTvScaleSmallAnim.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation, boolean isReverse) {
                translateTvAnimationing = false;
                translateTvBig = false;
            }
        });
    }


    private void startAnim() {
        bottomArcSet.start();
        textAnimSet.start();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
    }

    public void doStart() {
        new Handler(Looper.getMainLooper()).post(this::startAnim);
    }

    public void doDefault() {
        weChatVoiceBottomArcLight.setTranslationY(bottomArcTransY);
        weChatVoiceBottomArcDark.setVisibility(View.GONE);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        float x = ev.getX();
        float y = ev.getY();
        if (weChatVoiceBottomArcLight.isOnRect(x, y)) {
            tryChangeToLight();
            tryChangeCancelTextToSmall();
            tryChangeTranslateTextToSmall();
            voiceTv.setVisibility(View.VISIBLE);
            weChatVoiceBubble.setShowType(WeChatVoiceBubble.SHOW_TYPE.TYPE_CENTER);
        } else {
            // 不在区域里，看在屏幕左边右边
            tryChangeToDark();
            if (x < screenWH[0] / 2) {
                tryChangeCancelTextToBigger();
                tryChangeTranslateTextToSmall();
                weChatVoiceBubble.setShowType(WeChatVoiceBubble.SHOW_TYPE.TYPE_CANCEL);
            } else {
                tryChangeTranslateTextToBigger();
                tryChangeCancelTextToSmall();
                weChatVoiceBubble.setShowType(WeChatVoiceBubble.SHOW_TYPE.TYPE_TRANSLATE);
            }
            voiceTv.setVisibility(View.GONE);
        }
        return true;
    }

    private void setTextBigger(TextView text) {
        text.setTextColor(getResources().getColor(R.color.black));
        text.setBackgroundResource(R.drawable.bg_trans_oval_white);
    }

    private void setTextSmall(TextView text) {
        text.setTextColor(getResources().getColor(R.color.write));
        text.setBackgroundResource(R.drawable.bg_trans_oval);
    }

    boolean cancelTvBig = false;
    boolean translateTvBig = false;
    boolean cancelTvAnimationing = false;
    boolean translateTvAnimationing = false;

    private void tryChangeCancelTextToBigger() {
        if (cancelTvBig || cancelTvAnimationing) {
            return;
        }
        cancelTvAnimationing = true;
        setTextBigger(cancelTv);
        cancelHintTv.setVisibility(View.VISIBLE);
        cancelTvScaleBiggerAnim.start();
    }

    private void tryChangeCancelTextToSmall() {
        if (!cancelTvBig || cancelTvAnimationing) {
            return;
        }
        cancelHintTv.setVisibility(View.GONE);
        cancelTvAnimationing = true;
        setTextSmall(cancelTv);
        cancelTvScaleSmallAnim.start();
    }

    private void tryChangeTranslateTextToBigger() {
        if (translateTvBig || translateTvAnimationing) {
            return;
        }
        translateHintTv.setVisibility(View.VISIBLE);
        translateTvAnimationing = true;
        setTextBigger(translateTv);
        translateTvScaleBiggerAnim.start();
    }

    private void tryChangeTranslateTextToSmall() {
        if (!translateTvBig || translateTvAnimationing) {
            return;
        }
        translateHintTv.setVisibility(View.GONE);
        translateTvAnimationing = true;
        setTextSmall(translateTv);
        translateTvScaleSmallAnim.start();
    }

    private void tryChangeToDark() {
        if (!currentArcLight || darkAniming) {
            return;
        }
        darkAniming = true;
        darkAlphaAnim.start();
    }

    private void tryChangeToLight() {
        if (currentArcLight || lightAniming) {
            return;
        }
        lightAniming = true;
        lightAlphaAnim.start();
    }

}
