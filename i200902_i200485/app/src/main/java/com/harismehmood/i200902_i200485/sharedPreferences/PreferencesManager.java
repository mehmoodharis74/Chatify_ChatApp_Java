package com.harismehmood.i200902_i200485.sharedPreferences;

import android.content.Context;
import android.content.SharedPreferences;

import com.harismehmood.i200902_i200485.utilities.Constants;

public class PreferencesManager {
    public final SharedPreferences sharedPreferences;

    public PreferencesManager(Context context) {
        this.sharedPreferences = context.getSharedPreferences(Constants.KEY_UserPREFERENCE, Context.MODE_PRIVATE);
    }
    public void putBoolean(String key, boolean value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(key, value);
        editor.apply();
    }

    public boolean getBoolean(String key) {
        return sharedPreferences.getBoolean(key, false);
    }
    public void putString(String key, String value) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.apply();
    }
    public String getString(String key) {
        return sharedPreferences.getString(key, "");
    }
    public void clear() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }
}
