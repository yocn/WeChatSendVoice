package com.yocn.af.view.activity;

import android.os.Bundle;
import android.widget.LinearLayout;

import com.yocn.af.R;

public class WeChatSendVoiceActivity extends BaseActivity {

    private LinearLayout optionLl;

    protected void useWindowParams() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_voice);
        optionLl = findViewById(R.id.ll_option);
    }

}
