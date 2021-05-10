package com.knu.eattogether;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class MySharedPreferences {

    static final String PREF_EMAIL = "email";
    static final String PREF_PWD = "pwd";
    static final String PREF_CHECKED = "checkbox";

    static SharedPreferences getSharedPreferences(Context ctx){
        return PreferenceManager.getDefaultSharedPreferences(ctx);
    }

    public static void setPref(Context ctx, String Email, String Pwd, Boolean Checked)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.putString(PREF_EMAIL, Email);
        editor.putString(PREF_PWD, Pwd);
        editor.putBoolean(PREF_CHECKED, Checked);
        editor.commit();
    }

    public static String getPrefEmail(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_EMAIL, "");
    }

    public static String getPrefPwd(Context ctx)
    {
        return getSharedPreferences(ctx).getString(PREF_PWD, "");
    }

    public static Boolean getPrefChecked(Context ctx)
    {
        return getSharedPreferences(ctx).getBoolean(PREF_CHECKED, false);
    }

    public static void clearPref(Context ctx)
    {
        SharedPreferences.Editor editor = getSharedPreferences(ctx).edit();
        editor.clear();
        editor.commit();
    }

}
