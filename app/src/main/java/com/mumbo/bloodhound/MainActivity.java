package com.mumbo.bloodhound;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.Application;
import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {
    Profile loadedProfile;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppState.init(this);

        setContentView(R.layout.activity_main);

        getConfigAndPopulateLayout();

        MaterialToolbar toolbar = findViewById(R.id.menu);

        toolbar.getMenu().findItem(R.id.refresh_config)
            .setOnMenuItemClickListener((MenuItem item) -> {
                Log.d("MainActivity", "Refreshing config.");
                // Hack away any existing fragments (buttons)
                clearButtons();
                // Show loader
                findViewById(R.id.loader).setVisibility(VISIBLE);
                // Fetch and render new buttons
                fetchConfigAndPopulateLayout();
                return false;
            });

        toolbar.getMenu().findItem(R.id.configure_profiles)
                .setOnMenuItemClickListener((MenuItem item) -> {
                    Intent intent = new Intent(this, ConfigureProfilesActivity.class);
                    this.startActivity(intent);
                    return false;
                });
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppState.init(this);
        maybeLoadConfigForProfile(false);
    }

    private void getConfigAndPopulateLayout() {
        maybeLoadConfigForProfile(false);
    }

    private void fetchConfigAndPopulateLayout() {
        loadConfigForProfile(true);
    }

    private void populateLayoutWithButtons(ArrayList<BloodhoundConfigRow> rows) {
        // Hack away any existing fragments
        clearButtons();
        // Add new fragments
        FragmentManager fragMan = getSupportFragmentManager();
        for (BloodhoundConfigRow row : rows) {
            Fragment myFrag = ButtonFragment.newInstance(row);
            fragMan.beginTransaction()
                .add(R.id.inner_layout, myFrag)
                .setReorderingAllowed(true)
                .commit();
        }
        // Render
        findViewById(R.id.loader).setVisibility(GONE);
    }

    private void clearButtons() {
        FragmentManager fragMan = getSupportFragmentManager();
        ButtonFragment reject = new ButtonFragment();
        fragMan.beginTransaction()
                .replace(R.id.inner_layout, reject).commit();
        fragMan.beginTransaction().remove(reject).commit();
    }

    private void maybeLoadConfigForProfile(boolean fetch) {
        Profile active = AppState.getActiveProfile();
        if (active == null || isLoadedProfileStale()) {
            loadConfigForProfile(fetch);
        }
    }

    private void loadConfigForProfile(boolean fetch) {
        Profile active = AppState.getActiveProfile();
        MaterialToolbar title = findViewById(R.id.menu);
        if (active == null) {
            title.setTitle("Bloodhound");
            clearButtons();
            findViewById(R.id.loader).setVisibility(GONE);
            return;
        }
        if (isLoadedProfileStale()) {
            title.setTitle("Bloodhound " + "(" + active.name + ")");
            clearButtons();
            findViewById(R.id.loader).setVisibility(VISIBLE);
            if (fetch) {
                CompletableFuture<ArrayList<BloodhoundConfigRow>> response =
                        BloodhoundConfigAPI.genFetchConfig(this, active);
                response.thenAccept(this::populateLayoutWithButtons);
            } else {
                CompletableFuture<ArrayList<BloodhoundConfigRow>> response =
                        BloodhoundConfigAPI.genConfig(this, active);
                response.thenAccept(this::populateLayoutWithButtons);
            }
        }
    }

    private boolean isLoadedProfileStale() {
        Profile active = AppState.getActiveProfile();
        return (loadedProfile == null && active != null) || (loadedProfile != null && loadedProfile.name != active.name);
    }
}
