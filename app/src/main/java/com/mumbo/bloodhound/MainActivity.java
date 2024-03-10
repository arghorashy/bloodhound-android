package com.mumbo.bloodhound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;
import android.widget.LinearLayout;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        CompletableFuture<BloodhoundConfigRow[]> response =
                GoogleSheetsAPI.readFromConfigSheet(this);
        response.thenAccept(this::populateLayoutWithButtons);
    }

    private void populateLayoutWithButtons(BloodhoundConfigRow[] rows) {
        LinearLayout layout = findViewById(R.id.layout);
        FragmentManager fragMan = getSupportFragmentManager();
        for (BloodhoundConfigRow row : rows) {
            Fragment myFrag = ButtonFragment.newInstance(row);
            fragMan.beginTransaction()
                .add(R.id.layout, myFrag)
                .setReorderingAllowed(true)
                .commit();
        }
        setContentView(layout);
    }
}