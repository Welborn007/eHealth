package kesari.com.kesarie_healthmonitoring.Utils;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import kesari.com.kesarie_healthmonitoring.Profile.ProfileMain;

/**
 * Created by kesari on 26/04/17.
 */

public class SharedPrefUtil {
    public static String PREF_NAME = "Media";
    private static String KEY_USER = "user";

    public static ProfileMain getUser(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        String data = preferences.getString(KEY_USER, null);
        Gson gson = new Gson();
        if (data == null)
            return null;
        else
            return gson.fromJson(data, ProfileMain.class);
    }


    public static void setUser(Context context, String value) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().putString(KEY_USER, value).apply();

    }


    public static void setClear(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        preferences.edit().remove(KEY_USER).commit();
    }


}
