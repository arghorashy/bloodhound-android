package com.mumbo.bloodhound;

import static com.mumbo.bloodhound.BloodhoundEnv.KRISTA;
import static com.mumbo.bloodhound.BloodhoundEnv.TEST;

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
    private static final String API_URL = "https://script.google.com/macros/s/AKfycbxS-s4k1_taX7fPF0HVre-G0hwhU3GdY1f80H5RYTo3uW0-NZx_i8nohZpXiFxs1Kgd/exec";

    private static final BloodhoundEnv ENV = KRISTA;

    public static void writeRowToLogSheet(Context context, List<String> rowStrings) {
        Gson gson = new Gson();
        String fullURL = GoogleSheetsAPI.buildURL("?action=writeRowToLogSheet&row=" + gson.toJson(rowStrings));
        StringRequest req = new StringRequest(
                Request.Method.GET,
                fullURL,
                s -> Log.d("REQ", "Wrote to log."),
                e -> Log.d("REQ", "Error encountered when writing to log!"));
        RequestQueue q = Volley.newRequestQueue(context);
        q.add(req);
    }

    public static CompletableFuture<ArrayList<BloodhoundConfigRow>> readFromConfigSheet(Context context) {
        String fullURL = GoogleSheetsAPI.buildURL("?action=readConfig");
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

    private static String buildURL(String queryParams) {
        String envQueryParam;
        switch (ENV) {
            case TEST:
                envQueryParam = "test";
                break;
            case KRISTA:
                envQueryParam = "krista";
                break;
            default:
                throw new IllegalArgumentException("Env not set.");
        }
        return API_URL + queryParams + "&env=" + envQueryParam;
    }

}