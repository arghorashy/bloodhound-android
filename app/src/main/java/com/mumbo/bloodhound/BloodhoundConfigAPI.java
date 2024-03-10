package com.mumbo.bloodhound;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.CompletableFuture;

public class BloodhoundConfigAPI {
    private static String SHARED_PREFERENCES_KEY = "com.mumbo.bloodhound_shared_prefs";
    private static String CONFIG_SHARED_PREFERENCES_KEY = "com.mumbo.bloodhound_config_key";

    public static CompletableFuture<ArrayList<BloodhoundConfigRow>> genConfig(Context context) {
        BloodhoundConfigRow[] maybeConfig = BloodhoundConfigAPI.getCachedConfig(context);
        if (maybeConfig != null) {
            System.out.println("xxx genConfig, found cached.");
            CompletableFuture<ArrayList<BloodhoundConfigRow>> res = new CompletableFuture<>();
            res.complete(new ArrayList(Arrays.asList(maybeConfig)));
            return res;
        }
        System.out.println("xxx xgenConfig, did not find cached.");
        return BloodhoundConfigAPI.genFetchConfig(context);
    }

    public static CompletableFuture<ArrayList<BloodhoundConfigRow>> genFetchConfig(Context context) {
        CompletableFuture<ArrayList<BloodhoundConfigRow>> configFuture = GoogleSheetsAPI.readFromConfigSheet(context);
        configFuture.thenAccept(config -> BloodhoundConfigAPI.cacheConfig(context, config));

        return configFuture;
    }

    private static BloodhoundConfigRow[] getCachedConfig(Context context) {
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
        String json = pref.getString(CONFIG_SHARED_PREFERENCES_KEY, "");
        if (json.isEmpty()) {
            return null;
        }
        return BloodhoundConfigRow.fromJsonArray(json);
    }

    private static void cacheConfig(Context context, ArrayList<BloodhoundConfigRow> config) {
        Gson gson = new Gson();
        String json = gson.toJson(config);
        System.out.println("xxx cacheConfig");
        SharedPreferences pref = context.getSharedPreferences(SHARED_PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(CONFIG_SHARED_PREFERENCES_KEY, json);
        editor.commit();
    }
}
