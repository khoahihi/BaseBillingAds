package com.mmgsoft.modules.libs.data.local.prefs;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

/**
 * Created by KhoaND32 on 9/6/20.
 */
public class AppPreferencesHelper implements PreferencesHelper {

    private static final String PREF_KEY_ACCESS_TOKEN = "PREF_KEY_ACCESS_TOKEN";

    private final SharedPreferences mPrefs;
    private Gson gson;

    public AppPreferencesHelper(Context context, String prefFileName, Gson gson) {
        this.gson = gson;
        mPrefs = context.getSharedPreferences(prefFileName, Context.MODE_PRIVATE);
    }

}

