package com.mumbo.bloodhound;

import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.appbar.MaterialToolbar;

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
        toolbar.getMenu().findItem(R.id.refresh_config)
                .setOnMenuItemClickListener((MenuItem item) -> {
                    profileMgr.addProfile("A", "B");
                    profileMgr.addProfile("B", "B");
                    profileMgr.addProfile("C", "B");
                    adapter.notifyDataSetChanged();
                    return false;
                });
    }

}
