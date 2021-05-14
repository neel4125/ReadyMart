package com.codecoy.ecommerce.adminmodule;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;

public class Utils {

    public static String TAG = "DEBUG";


    public static void Log(String msg, Context context) {
        Log.d(TAG, msg);
    }



    public static String getPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPreferences.getString(key, "");
        return userName;
    }


    public static boolean savePreferences(String key, String value,
                                          Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        return true;
    }


}
