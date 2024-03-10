package com.mumbo.bloodhound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentContainerView;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.google.android.material.appbar.MaterialToolbar;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fetchConfigAndPopulateLayout();

        MaterialToolbar toolbar = findViewById(R.id.menu);
        toolbar.getMenu().findItem(R.id.refresh_config)
            .setOnMenuItemClickListener((MenuItem item) -> {
                Log.d("MainActivity", "Refreshing config.");
                fetchConfigAndPopulateLayout();
                return false;
            });
    }

    private void fetchConfigAndPopulateLayout() {
        CompletableFuture<ArrayList<BloodhoundConfigRow>> response =
                GoogleSheetsAPI.readFromConfigSheet(this);
        response.thenAccept(this::populateLayoutWithButtons);
    }

    private void populateLayoutWithButtons(ArrayList<BloodhoundConfigRow> rows) {
        LinearLayout innerLayout = findViewById(R.id.outer_layout);
        Log.d("layout", innerLayout.toString());
        FragmentManager fragMan = getSupportFragmentManager();
        // Hack away any existing fragments
        ButtonFragment reject = new ButtonFragment();
        fragMan.beginTransaction()
                .replace(R.id.outer_layout, reject).commit();
        fragMan.beginTransaction().remove(reject).commit();
        // Add new fragments
        for (BloodhoundConfigRow row : rows) {
            Fragment myFrag = ButtonFragment.newInstance(row);
            fragMan.beginTransaction()
                .add(R.id.outer_layout, myFrag)
                .setReorderingAllowed(true)
                .commit();
        }
        setContentView(innerLayout);
    }
}