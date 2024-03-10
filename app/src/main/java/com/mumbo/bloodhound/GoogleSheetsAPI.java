package com.mumbo.bloodhound;

import android.app.Activity;
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

public class GoogleSheetsAPI {
    // See backend function definition here: https://script.google.com/home/projects/1utIOA5lWcz53UV1_rWB4iHGr12BhR2KfeJPvsQl5Nhr5XsHC8TZeYWs9/arg1?id=45&arg2=Afsheen&arg3=Ghorashy
    private static final String API_URL = "https://script.google.com/macros/s/AKfycbxEnBZCkS6Xl2IdoJlFrD6jaqzVGb5Ad9EqZ6F19yoBHlO18UIhxnH4FKAM5dmWMQR2/exec";

    public static void writeRowToLogSheet(Context context, List<String> rowStrings) {
        Gson gson = new Gson();
        String fullURL = API_URL + "?action=writeRowToLogSheet&row=" + gson.toJson(rowStrings);
        StringRequest req = new StringRequest(
                Request.Method.GET,
                fullURL,
                s -> Log.d("REQ", "Wrote to log."),
                e -> Log.d("REQ", "Error encountered when writing to log!"));
        RequestQueue q = Volley.newRequestQueue(context);
        q.add(req);
    }

    public static CompletableFuture<ArrayList<BloodhoundConfigRow>> readFromConfigSheet(Context context) {
        String fullURL = API_URL + "?action=readConfig";
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

}