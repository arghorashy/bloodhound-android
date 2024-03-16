package com.mumbo.bloodhound;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class BloodhoundConfigAPI {
    private static String CONFIG_SHARED_PREFERENCES_KEY_PREFIX = "com.mumbo.bloodhound_config_key_";

    public static CompletableFuture<ArrayList<BloodhoundConfigRow>> genConfig(Context context, Profile profile) {
        BloodhoundConfigRow[] maybeConfig = BloodhoundConfigAPI.getCachedConfig(context, profile);
        if (maybeConfig != null) {
            CompletableFuture<ArrayList<BloodhoundConfigRow>> res = new CompletableFuture<>();
            res.complete(new ArrayList(Arrays.asList(maybeConfig)));
            return res;
        }
        return BloodhoundConfigAPI.genFetchConfig(context, profile);
    }

    public static CompletableFuture<ArrayList<BloodhoundConfigRow>> genFetchConfig(Context context, Profile profile) {
        CompletableFuture<ArrayList<BloodhoundConfigRow>> configFuture = GoogleSheetsAPI.readFromConfigSheet(context, profile);
        configFuture.thenAccept(config -> BloodhoundConfigAPI.cacheConfig(context, profile, config));

        return configFuture;
    }

    private static BloodhoundConfigRow[] getCachedConfig(Context context, Profile profile) {
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, 0);
        String key = CONFIG_SHARED_PREFERENCES_KEY_PREFIX + profile.name;
        String json = pref.getString(key, "");
        if (json.isEmpty()) {
            return null;
        }
        return BloodhoundConfigRow.fromJsonArray(json);
    }

    private static void cacheConfig(Context context, Profile profile, ArrayList<BloodhoundConfigRow> config) {
        Gson gson = new Gson();
        String json = gson.toJson(config);
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        String key = CONFIG_SHARED_PREFERENCES_KEY_PREFIX + profile.name;
        editor.putString(key, json);
        editor.commit();
    }
}
