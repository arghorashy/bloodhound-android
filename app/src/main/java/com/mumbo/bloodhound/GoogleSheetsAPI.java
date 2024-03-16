package com.mumbo.bloodhound;

import android.content.Context;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class GoogleSheetsAPI {
    private static final String API_URL = "https://script.google.com/macros/s/AKfycbwBk2lq302eSJVUM8Qg7eywP4HdxWF-AUhBh_HzNmJ29vlB1XZVyntj-tjaSaUSYJzI/exec";

    public static void writeRowToLogSheet(Context context, Profile profile, List<String> rowStrings) {
        Gson gson = new Gson();
        String fullURL = GoogleSheetsAPI.buildURL(profile, "?action=writeRowToLogSheet&row=" + gson.toJson(rowStrings));
        StringRequest req = new StringRequest(
                Request.Method.GET,
                fullURL,
                s -> Log.d("REQ", "Wrote to log."),
                e -> Log.d("REQ", "Error encountered when writing to log!"));
        RequestQueue q = Volley.newRequestQueue(context);
        q.add(req);
    }

    public static CompletableFuture<ArrayList<BloodhoundConfigRow>> readFromConfigSheet(Context context, Profile profile) {
        String fullURL = GoogleSheetsAPI.buildURL(profile, "?action=readConfig");
        CompletableFuture<ArrayList<BloodhoundConfigRow>> res = new CompletableFuture<>();

        StringRequest req = new StringRequest(
                Request.Method.GET,
                fullURL,
                s -> {
                    Log.d("REQ", "Read from Config.");
                    res.complete(BloodhoundConfigRow.fromRawJsonArray(s));
                },
                e -> Log.d("REQ", "Encountered error when reading from config!"));
        RequestQueue q = Volley.newRequestQueue(context);
        q.add(req);
        return res;
    }

    private static String buildURL(Profile profile, String queryParams) {
        String hexUrl = encodeToHex(profile.url);
        return API_URL + queryParams + "&hexUrl=" + hexUrl;
    }

    private static String encodeToHex(String str) {
        String hex = str.chars().mapToObj(c ->
                Integer.toHexString(c)).collect(Collectors.joining());
        return hex;
    }
}