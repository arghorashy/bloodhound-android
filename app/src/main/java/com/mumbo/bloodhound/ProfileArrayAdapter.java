package com.mumbo.bloodhound;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
public class ProfileArrayAdapter extends ArrayAdapter<Profile> {
    final private static int LAYOUT_RESOURCE_ID = R.layout.fragment_profile_row;

    final private Context context;
    final private ProfileMgr profileMgr;
    public ProfileArrayAdapter(Context context, ProfileMgr profileMgr) {
        super(context, LAYOUT_RESOURCE_ID, profileMgr.profiles);
        this.context = context;
        this.profileMgr = profileMgr;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        ProfileRowHolder holder;

        if (row == null) {
            LayoutInflater inflater = ((Activity)context).getLayoutInflater();
            row = inflater.inflate(LAYOUT_RESOURCE_ID, parent, false);

            holder = new ProfileRowHolder();
            holder.nameField = (TextView)row.findViewById(R.id.profileName);
            holder.selectBtn=(ImageView) row.findViewById(R.id.selectBtn);
            holder.removeBtn=(ImageView) row.findViewById(R.id.removeBtn);

            row.setTag(holder);
        } else {
            holder = (ProfileRowHolder) row.getTag();
        }

        ArrayList<Profile> profiles = profileMgr.profiles;
        Profile profile = profiles.get(position);

        holder.nameField.setText(profile.name);
        if (profile.active) {
            holder.selectBtn.setImageResource(R.drawable.baseline_beenhere_24);
        } else {
            holder.selectBtn.setImageResource(R.drawable.outline_beenhere_24);
        }
        holder.removeBtn.setImageResource(R.drawable.baseline_delete_24);

        holder.removeBtn.setOnClickListener((View v) -> {
            System.out.println("xxx remove " + profile.name);
            profileMgr.removeProfile(profile.name);
            this.notifyDataSetChanged();
        });

        holder.selectBtn.setOnClickListener((View v) -> {
            System.out.println("xxx set " + profile.name);
            profileMgr.setActiveProfile(profile.name);
            this.notifyDataSetChanged();
        });

        return row;
    }

    static class ProfileRowHolder {
        TextView nameField;
        ImageView selectBtn;
        ImageView removeBtn;
    }
}
