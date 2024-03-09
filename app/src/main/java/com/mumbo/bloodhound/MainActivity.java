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
import android.view.View;
import android.app.AlertDialog;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        LinearLayout layout = findViewById(R.id.layout);
        FragmentManager fragMan = getSupportFragmentManager();
        int buttonCount = 7;
        for (int buttonTick = 1; buttonTick <= buttonCount; buttonTick++) {
            Fragment myFrag = ButtonFragment.newInstance("button" + buttonTick);
            fragMan.beginTransaction()
                .add(R.id.layout, myFrag)
                .setReorderingAllowed(true)
                 .commit();
        }
        setContentView(layout);
    }
}