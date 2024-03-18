package com.mumbo.bloodhound;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.common.collect.ImmutableCollection;

public class TabFragment extends Fragment {

    private static final String ARG_NAME = "arg_name";
    private String paramName;

    private ImmutableCollection<BloodhoundConfigRow> rows;

    public TabFragment() {
        // Required empty public constructor
    }

    public static TabFragment newInstance(String tabName) {
        TabFragment fragment = new TabFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, tabName);
        fragment.setArguments(args);
        return fragment;
    }

    public void setRows(ImmutableCollection<BloodhoundConfigRow> rows) {
        this.rows = rows;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramName = getArguments().getString(ARG_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Populate layout with buttons
        FragmentManager fragMan = getChildFragmentManager();
        for (BloodhoundConfigRow row : this.rows) {
            Fragment myFrag = ButtonFragment.newInstance(row);
            fragMan.beginTransaction()
                    .add(R.id.fragment_tab_layout, myFrag)
                    .setReorderingAllowed(true)
                    .commit();
        }
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_tab, container, false);
    }
}