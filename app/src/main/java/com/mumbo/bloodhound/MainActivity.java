package com.mumbo.bloodhound;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getConfigAndPopulateLayout();

        MaterialToolbar toolbar = findViewById(R.id.menu);

        toolbar.getMenu().findItem(R.id.refresh_config)
            .setOnMenuItemClickListener((MenuItem item) -> {
                Log.d("MainActivity", "Refreshing config.");
                // Hack away any existing fragments (buttons)
                FragmentManager fragMan = getSupportFragmentManager();
                ButtonFragment reject = new ButtonFragment();
                fragMan.beginTransaction()
                        .replace(R.id.inner_layout, reject).commit();
                fragMan.beginTransaction().remove(reject).commit();
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
        ProfileMgr profileMgr = new ProfileMgr(this);
        Profile profile = profileMgr.getActiveProfile();
        if (profile != null) {
            MaterialToolbar title = findViewById(R.id.menu);
            title.setTitle("Bloodhound " + "(" + profile.name + ")");
        }

    }

    private void getConfigAndPopulateLayout() {
        CompletableFuture<ArrayList<BloodhoundConfigRow>> response =
                BloodhoundConfigAPI.genConfig(this);
        response.thenAccept(this::populateLayoutWithButtons);
    }

    private void fetchConfigAndPopulateLayout() {
        CompletableFuture<ArrayList<BloodhoundConfigRow>> response =
                BloodhoundConfigAPI.genFetchConfig(this);
        response.thenAccept(this::populateLayoutWithButtons);
    }

    private void populateLayoutWithButtons(ArrayList<BloodhoundConfigRow> rows) {
        FragmentManager fragMan = getSupportFragmentManager();
        // Hack away any existing fragments
        ButtonFragment reject = new ButtonFragment();
        fragMan.beginTransaction()
                .replace(R.id.inner_layout, reject).commit();
        fragMan.beginTransaction().remove(reject).commit();
        // Add new fragments
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
}