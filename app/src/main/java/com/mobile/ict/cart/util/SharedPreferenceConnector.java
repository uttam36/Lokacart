package com.mobile.ict.cart.util;

/**
 * Created by Vishesh on 16-03-2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class SharedPreferenceConnector {

    public static final String PREF_NAME = "LOKACART_PREFERENCES";
    public static final int MODE = Context.MODE_PRIVATE;

    public static void writeBoolean(Context context, String key, Boolean value)
    {
        getEditor(context).putBoolean(key, value).commit();
    }

    public static Boolean readBoolean(Context context, String key, Boolean defValue)
    {
        return getPreferences(context).getBoolean(key, defValue);
    }

    public static void writeInteger(Context context, String key, int value)
    {
        getEditor(context).putInt(key, value).commit();
    }

    public static int readInteger(Context context, String key, int defValue)
    {
        return getPreferences(context).getInt(key, defValue);
    }

    public static void writeString(Context context, String key, String value)
    {
        getEditor(context).putString(key, value).commit();
    }

    public static String readString(Context context, String key, String defValue)
    {
        return getPreferences(context).getString(key, defValue);
    }

    public static SharedPreferences getPreferences(Context context) {
        return context.getSharedPreferences(PREF_NAME, MODE);
    }

    public static Editor getEditor(Context context) {
        return getPreferences(context).edit();
    }
}
