package com.mumbo.bloodhound;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import static com.google.common.collect.ImmutableSet.toImmutableSet;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Lifecycle;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.tabs.TabLayout;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableMultimap;
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
                Log.d("MainActivity", "Refreshing config...");
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
        // Stop showing loader
        findViewById(R.id.loader).setVisibility(GONE);
        Log.d("MainActivity", "Config fetched...");

        // Populate TabLayout
        ImmutableSet<String> tabNames = rows.stream().map(row -> row.tabName).collect(toImmutableSet());
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        tabLayout.removeAllTabs();
        for (String tabName: tabNames) {
            tabLayout.addTab(tabLayout.newTab().setText(tabName));
        }

        // Set up ViewPager2 for the tabs
        ViewStateAdapter stateAdapter = new ViewStateAdapter(getSupportFragmentManager(), getLifecycle());
        ImmutableMultimap.Builder<String, BloodhoundConfigRow> tabToRows = ImmutableMultimap.builder();
        for (BloodhoundConfigRow row : rows) {
            tabToRows.put(row.tabName, row);
        }
        stateAdapter.setTabToRowsMap(tabToRows.build());
        stateAdapter.setTabLayout(tabLayout);
        final ViewPager2 pager = findViewById(R.id.pager);
        pager.setAdapter(stateAdapter);

        // When the ViewPager2 is swiped, the tabs should change accordingly
        pager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });

        // When tab is selected, the pager should update
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                Log.d("tab", String.valueOf(tab.getText()));
                pager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}
            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
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
            findViewById(R.id.loader).setVisibility(GONE);
            return;
        }
        if (isLoadedProfileStale()) {
            title.setTitle("Bloodhound " + "(" + active.name + ")");
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

 class ViewStateAdapter extends FragmentStateAdapter {
    private ImmutableMultimap<String, BloodhoundConfigRow> tabToRows;
    TabLayout tabLayout;
    public ViewStateAdapter(@NonNull FragmentManager fragmentManager,
                            @NonNull Lifecycle lifecycle) {
        super(fragmentManager, lifecycle);
    }

    public void setTabToRowsMap(ImmutableMultimap<String, BloodhoundConfigRow> tabToRows) {
        this.tabToRows = tabToRows;
    }
    public void setTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        ArrayList<String> keys = new ArrayList(this.tabToRows.keySet());
        String tabName = keys.get(position);
        TabFragment tabFragment = TabFragment.newInstance(tabName);
        tabFragment.setRows(this.tabToRows.get(tabName));
        return tabFragment;
    }

    @Override
    public int getItemCount() {
        return this.tabToRows.keySet().size();
    }
}
