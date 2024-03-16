package com.mumbo.bloodhound;

import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.Nullable;

import com.google.gson.Gson;

import org.checkerframework.checker.units.qual.A;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class ProfileMgr {
    private static String SHARED_PREFERENCES_KEY = "com.mumbo.bloodhound_shared_prefs";
    private static String PROFILES_SHARED_PREFERENCES_KEY = "com.mumbo.bloodhound_profiles_key";
    private Context context;

    public ArrayList<Profile> profiles;

    public ProfileMgr(Context context) {
        this.context = context;
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, 0);
        String json = pref.getString(PROFILES_SHARED_PREFERENCES_KEY, "");
        if (json.isEmpty()) {
            this.profiles = new ArrayList<>();
            return;
        }
        Gson gson = new Gson();
        Profile[] profiles = gson.fromJson(json, Profile[].class);

        this.profiles = new ArrayList<>(Arrays.asList(profiles));
    }

    public void addProfile(String name, String url) {
        for (Profile profile : profiles) {
            if (profile.name == name) {
                return;
            }
        }

        Profile activeProfile = getActiveProfile();
        profiles.add(new Profile(name, url, activeProfile == null));
        commitChange();
    }

    public void removeProfile(String name) {
        @Nullable Profile deletee = null;
        for (Profile profile : profiles) {
            if (profile.name == name) {
                deletee = profile;
                break;
            }
        }
        if (deletee == null) {
            return;
        }
        profiles.remove(deletee);
        commitChange();
    }

    @Nullable
    public static Profile getActiveProfileStatically(Context context) {
        ProfileMgr profileMgr = new ProfileMgr(context);
        return profileMgr.getActiveProfile();
    }

    public @Nullable Profile getActiveProfile() {
        for (Profile profile : profiles) {
            if (profile.active) {
                return profile;
            }
        }
        return null;
    }

    public void setActiveProfile(String name) {
        for (Profile profile : profiles) {
            profile.active = profile.name == name;
        }
        commitChange();
    }

    private void commitChange() {
        Gson gson = new Gson();
        String json = gson.toJson(profiles);
        SharedPreferences pref = context.getSharedPreferences(Constants.SHARED_PREFERENCES_KEY, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString(PROFILES_SHARED_PREFERENCES_KEY, json);
        editor.commit();
    }
}
