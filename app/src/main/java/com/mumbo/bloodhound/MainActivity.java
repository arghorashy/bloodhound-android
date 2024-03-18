package com.mumbo.bloodhound;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.ImmutableSet;

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
        Log.d("Response!!.", rows.toString());

        // Hack away any existing fragments
        clearButtons();
        // Add new fragments
        FragmentManager fragMan = getSupportFragmentManager();
        ImmutableSet<String> tabNames = rows.stream().map(row -> row.tabName).collect(toImmutableSet());
        Log.d("tabNames", tabNames.toString());

        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.removeAllTabs();
        for (String tabName: tabNames) {
            Log.d("tabName", tabName);
            tabLayout.addTab(tabLayout.newTab().setText(tabName));
        }

/*

        PlansPagerAdapter adapter = new PlansPagerAdapter
                (getSupportFragmentManager(), tab.getTabCount());
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(1);
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

 */

        for (BloodhoundConfigRow row : rows) {
            Fragment myFrag = ButtonFragment.newInstance(row);
            fragMan.beginTransaction()
                .add(R.id.inner_layout1, myFrag)
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
                .replace(R.id.inner_layout1, reject).commit();
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
