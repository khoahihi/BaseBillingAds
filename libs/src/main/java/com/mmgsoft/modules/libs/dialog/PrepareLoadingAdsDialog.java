package com.mmgsoft.modules.libs.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import com.mmgsoft.modules.libs.R;

public class PrepareLoadingAdsDialog extends Dialog {
    public PrepareLoadingAdsDialog(Context context) {
        super(context, android.R.style.Theme_Black_NoTitleBar_Fullscreen);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_prepair_loading_ads);
    }
}
