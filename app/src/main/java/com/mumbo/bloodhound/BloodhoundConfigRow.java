package com.mumbo.bloodhound;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.List;

public class BloodhoundConfigRow {
    public int id;
    public String buttonName;
    public String text;
    public String option1;
    public String option2;
    public String option3;
    public int scaleMax;
    public String tabName;

    public BloodhoundConfigRow(BloodhoundConfigRowRaw raw) {
        id = raw.id;
        buttonName = raw.buttonName;
        text = raw.text;
        option1 = raw.option1;
        option2 = raw.option2;
        option3 = raw.option3;
        scaleMax = raw.scaleMax.isEmpty() ? 0 : Integer.parseInt(raw.scaleMax);
        tabName = raw.tabName;
    }

    // The raw refers to the fact that in the JSON coming from the server, values that should be
    // non-strings might come back as strings, e.g., scaleMax.
    public static ArrayList<BloodhoundConfigRow> fromRawJsonArray(String json) {
        Gson gson = new Gson();
        System.out.println("xxx  " + json);
        BloodhoundConfigRowRaw[] rawConfig = gson.fromJson(json, BloodhoundConfigRowRaw[].class);
        ArrayList<BloodhoundConfigRow> config = new ArrayList<>();
        for (BloodhoundConfigRowRaw raw : rawConfig) {
            config.add(new BloodhoundConfigRow(raw));
        }
        return config;
    }

    public static BloodhoundConfigRow[] fromJsonArray(String json) {
        Gson gson = new Gson();
        BloodhoundConfigRow[] config = gson.fromJson(json, BloodhoundConfigRow[].class);
        return config;
    }

    public String toString() {
        List<String> strings = new ArrayList<>();
        strings.add(String.valueOf(id));
        strings.add(buttonName);
        strings.add(text);
        strings.add(option1);
        strings.add(option2);
        strings.add(option3);
        strings.add(String.valueOf(scaleMax));
        strings.add(tabName);
        return String.join(", ", strings);
    }
}
