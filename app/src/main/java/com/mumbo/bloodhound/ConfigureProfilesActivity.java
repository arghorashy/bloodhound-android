package com.mumbo.bloodhound;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.textfield.TextInputLayout;

public class ConfigureProfilesActivity extends AppCompatActivity {

    private ProfileMgr profileMgr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_profiles);
        profileMgr = new ProfileMgr(this);
        ProfileArrayAdapter adapter = new ProfileArrayAdapter(this,
                profileMgr);
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        MaterialToolbar toolbar = findViewById(R.id.menu);
        toolbar.getMenu().findItem(R.id.add_config)
                .setOnMenuItemClickListener((MenuItem item) -> {
                    showAddProfileAlert();
                    return false;
                });
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
                    profileMgr.addProfile(nameInputString, urlInputString);
                    dialog.cancel();
                })
                .setNegativeButton("No", (DialogInterface dialog, int id) -> {
                    dialog.cancel();
                });

        AlertDialog alert = addProfileAlertDialog.create();
        alert.show();
    }

}
