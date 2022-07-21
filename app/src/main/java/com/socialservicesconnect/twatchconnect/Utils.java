package com.socialservicesconnect.twatchconnect;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class Utils {
    public static String HTTP_URL = "https://";

    public static String MAIN_URL = ".socialservicesconnect.com/wp-json/civicrm/v3/rest?key=";

    //    public static String MAIN_URL = ".socialservicesconnect.com/wp-json/civicrm/v3/rest?key=QcUVqxT1QBbchOzKrkiGq6sBTYh4LzKV6AC2kCbQ&json=1&api_key=XE90yscAqYLIxXZp9KOsO3ru";
    public static String AUTH_URL = ".socialservicesconnect.com/civicrm/devices/auth";
    public static String BASE_URL = "https://test.socialservicesconnect.com/wp-json/civicrm/v3/rest?key=QcUVqxT1QBbchOzKrkiGq6sBTYh4LzKV6AC2kCbQ&json=1&api_key=XE90yscAqYLIxXZp9KOsO3ru";
    public static String SUB_DOMAIN = "sub_domain";
    public static String KEY_LAST_UPDATED = "last_updated";
    public static String KEY_IS_TIMER_STARTED = "timer";

    public static String KEY_API_KEY = "api_key";
    public static String KEY = "key";
    public static String KEY_SITE_NAME = "site_name";

    public static String getPreferences(String key, Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        String userName = sharedPreferences.getString(key, "");
        return userName;
    }

    public static int getPreferencesInt(String key, Context context) {

        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        int int_value = sharedPreferences.getInt(key, 0);
        return int_value;
    }

    public static boolean savePreferences(String key, String value,
                                          Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, value);
        editor.commit();
        return true;
    }

    public static boolean savePreferencesInt(String key, int value,
                                             Context context) {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(key, value);
        editor.commit();
        return true;
    }


}
