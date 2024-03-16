package com.mumbo.bloodhound;

import android.content.Context;

import androidx.annotation.Nullable;

public class AppState {
    public static ProfileMgr profileMgr;

    public static void init(Context context) {
        if (profileMgr == null) {
            profileMgr = new ProfileMgr(context);
        }
    }

    @Nullable
    public static Profile getActiveProfile() {
        return profileMgr.getActiveProfile();
    }
}
