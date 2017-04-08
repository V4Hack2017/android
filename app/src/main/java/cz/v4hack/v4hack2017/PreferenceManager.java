package cz.v4hack.v4hack2017;

import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

public class PreferenceManager {

    private static final String KEY_FAVORITE_LINES = "FAVORITE_LINES";
    private static SharedPreferences preferences;

    public static void init(SharedPreferences preferences) {
        PreferenceManager.preferences = preferences;
    }

    public static ArrayList<String> getFavoriteLines() {
        try {
            ArrayList<String> arrayList = new ArrayList<>();
            JSONArray jsonArray = new JSONArray(preferences.getString(KEY_FAVORITE_LINES, "[]"));
            for (int i = 0; i < jsonArray.length(); i++) {
                arrayList.add(jsonArray.getString(i));
            }
            return arrayList;
        } catch (JSONException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public static void setFavoriteLines(ArrayList<String> list) {
        JSONArray jsonArray = new JSONArray();
        for (String s : list) {
            jsonArray.put(s);
        }
        preferences.edit().putString(KEY_FAVORITE_LINES, jsonArray.toString()).apply();
    }
}
