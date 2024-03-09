package com.mumbo.bloodhound;

import androidx.appcompat.app.AppCompatActivity;

import android.util.Log;
import com.google.android.material.button.MaterialButton;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.app.AlertDialog;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    AlertDialog.Builder alertBuilder;
    MaterialButton button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d("onCreate", "onCreate");

        button = findViewById(R.id.high_paw_button);
        alertBuilder = new AlertDialog.Builder(this);

        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Log.d("BUTTONS", "User tapped the high_paw_button");
                alertBuilder
                        .setTitle("You've high-pawed the Bloodhound!!!")
                        .setMessage("Bloodhound asks yes or no ??? ?")
                        .setCancelable(false)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(),"huzzzaahh a YES!!!!",
                                        Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                                Toast.makeText(getApplicationContext(),"NOooOOo how could you",
                                        Toast.LENGTH_SHORT).show();
                            }
                        });
                AlertDialog alert = alertBuilder.create();
                alert.show();
            }
        });
    }
}