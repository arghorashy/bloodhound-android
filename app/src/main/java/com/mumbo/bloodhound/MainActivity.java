package com.mumbo.bloodhound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.app.FragmentTransaction;
import android.util.Log;

import com.google.android.material.button.MaterialButton;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import java.util.concurrent.CompletableFuture;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
      
        // Get config example code.
        CompletableFuture<BloodhoundConfigRow[]> res = GoogleSheetsAPI.readFromConfigSheet(this);
        res.thenAccept(value -> System.out.println(value[0].toString()));
      
        LinearLayout layout = findViewById(R.id.layout);
        FragmentManager fragMan = getSupportFragmentManager();
        int buttonCount = 7;
        for (int buttonTick = 1; buttonTick <= buttonCount; buttonTick++) {
            Fragment myFrag = ButtonFragment.newInstance("button" + buttonTick, 0);
            fragMan.beginTransaction()
                .add(R.id.layout, myFrag)
                .setReorderingAllowed(true)
                 .commit();
        }
        Fragment myFrag = ButtonFragment.newInstance("buttonScale",6);
        fragMan.beginTransaction()
                .add(R.id.layout, myFrag)
                .setReorderingAllowed(true)
                .commit();

        setContentView(layout);
    }
}