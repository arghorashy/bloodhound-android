package com.mumbo.bloodhound;

import android.app.Activity;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;

import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

import java.util.List;

public class GoogleSheetsAPI {
    // See backend function definition here: https://script.google.com/home/projects/1utIOA5lWcz53UV1_rWB4iHGr12BhR2KfeJPvsQl5Nhr5XsHC8TZeYWs9/arg1?id=45&arg2=Afsheen&arg3=Ghorashy
    private static final String WRITE_URL = "https://script.google.com/macros/s/AKfycbyZ30BC-WzZiVwKN1M3s776DH7K8GeQEg7m5WhyTsr-BuxQDT62A71Z46SIBSON0GxD/exec";

    public static void writeRowToLogSheet(Activity activity, List<String> rowStrings) {
        Gson gson = new Gson();
        String fullURL = WRITE_URL + "?action=writeRow&row=" + gson.toJson(rowStrings);
        StringRequest req = new StringRequest(Request.Method.GET, fullURL, s -> Log.d("REQ", s), e -> Log.d("REQ", "Error!"));
        RequestQueue q = Volley.newRequestQueue(activity);
        q.add(req);
    }

}