package com.mumbo.bloodhound;

import static android.view.View.GONE;
import static android.view.View.VISIBLE;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

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
                        .replace(R.id.outer_layout, reject).commit();
                fragMan.beginTransaction().remove(reject).commit();
                LinearLayout innerLayout = findViewById(R.id.outer_layout);
                setContentView(innerLayout);
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

        toolbar.getMenu().findItem(R.id.add_profile_dialog)
                .setOnMenuItemClickListener((MenuItem item) -> {
                    Log.d("MainActivity", "Ice Cream!!!");
showAddProfileAlert();

                    return false;
                });

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

    private void showAddProfileAlert() {
        AlertDialog.Builder addProfileAlertDialog = new AlertDialog.Builder(this);

        final View dialogLayout = getLayoutInflater().inflate(R.layout.configure_profiles_dialog, null);
        addProfileAlertDialog
                .setTitle("Add profile?")
                .setView(dialogLayout)
                .setCancelable(false)
                .setPositiveButton("Add", (DialogInterface dialog, int id) -> {
                    TextInputLayout name = dialogLayout.findViewById(R.id.profile_name_input);
                    String nameInputString = name.getEditText().getText().toString();
                    TextInputLayout url = dialogLayout.findViewById(R.id.spreadsheet_url_input);
                    String urlInputString = url.getEditText().getText().toString();
                    Toast.makeText(this,  nameInputString+ " " + urlInputString, Toast.LENGTH_SHORT).show();
                    dialog.cancel();
                })
                .setNegativeButton("No", (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                });

        AlertDialog alert = addProfileAlertDialog.create();
        alert.show();
    }

    private void populateLayoutWithButtons(ArrayList<BloodhoundConfigRow> rows) {
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
        // Render
        findViewById(R.id.loader).setVisibility(GONE);
        setContentView(findViewById(R.id.outer_layout));
    }
}