package com.mumbo.bloodhound;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.util.Log;

import com.google.android.material.button.MaterialButton;

import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    AlertDialog.Builder alertBuilder;
    MaterialButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("onCreate", "onCreate");

        setContentView(R.layout.activity_main);
        LinearLayout layout = findViewById(R.id.layout);
        int height = 3;
        int width = 4;
        int buttonTick = 0;
        for (int heightTick = 0; heightTick < height; heightTick++) {
            LinearLayout row = new LinearLayout(this);
            row.setLayoutParams(new LinearLayout.LayoutParams
                    (LinearLayout.LayoutParams.WRAP_CONTENT,
                            LinearLayout.LayoutParams.WRAP_CONTENT));
            for (int widthTick = 0; widthTick < width; widthTick++) {
                buttonTick++;
                MaterialButton btnTag = new MaterialButton(this);
                btnTag.setLayoutParams(new LinearLayout.LayoutParams
                        (LinearLayout.LayoutParams.WRAP_CONTENT,
                                LinearLayout.LayoutParams.MATCH_PARENT));
                btnTag.setText("#" + buttonTick);
                btnTag.setId(buttonTick);
                btnTag.setIcon(ContextCompat.getDrawable(this, R.drawable.baseline_pets_24));
                btnTag.setOnClickListener((View v) ->
                        Toast.makeText(getApplicationContext(), "clicked on button #" + v.getId(),
                                Toast.LENGTH_SHORT).show()
                );
                row.addView(btnTag);
            }
            layout.addView(row);
        }
        setContentView(layout);

        button = findViewById(R.id.high_paw_button);
        alertBuilder = new AlertDialog.Builder(this);
        button.setOnClickListener((View v) -> {
            Log.d("BUTTONS", "User tapped the high_paw_button");
            alertBuilder
                    .setTitle("You've high-pawed the Bloodhound!!!")
                    .setMessage("Bloodhound asks yes or no ??? ?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (DialogInterface dialog, int id) -> {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "huzzzaahh a YES!!!!",
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (DialogInterface dialog, int id) -> {
                        dialog.cancel();
                        Toast.makeText(getApplicationContext(), "NOooOOo how could you",
                                Toast.LENGTH_SHORT).show();
                    });
            AlertDialog alert = alertBuilder.create();
            alert.show();
        });
    }
}