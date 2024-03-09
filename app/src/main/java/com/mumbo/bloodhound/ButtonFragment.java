package com.mumbo.bloodhound;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ButtonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonFragment extends Fragment {
    AlertDialog.Builder alertBuilder;
    MaterialButton button;
    private static final String ARG_NAME = "arg_name";

    private String paramName;

    public ButtonFragment() {
        // Required empty public constructor
    }

    public static ButtonFragment newInstance(String paramName) {
        ButtonFragment fragment = new ButtonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, paramName);
        fragment.setArguments(args);
        return fragment;
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
        View view = inflater.inflate(R.layout.fragment_button, container, false);

        alertBuilder = new AlertDialog.Builder(view.getContext());

        button = view.findViewById(R.id.paw_button);
        button.setText(this.paramName);
        button.setOnClickListener((View v) -> {
            Log.d("ButtonFragment", "Click registered for button with name: " + this.paramName);
            ArrayList<String> logArguments = new ArrayList<>();
            logArguments.add(this.paramName);
            alertBuilder
                    .setTitle("Log " + this.paramName)
                    .setMessage("Do you wish to write this to the log?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (DialogInterface dialog, int id) -> {
                        GoogleSheetsAPI.writeRowToLogSheet(this.getContext(), logArguments);
                        dialog.cancel();
                        Toast.makeText(view.getContext(), "Written!!!!",
                                Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("No", (DialogInterface dialog, int id) -> {
                        dialog.cancel();
                        Toast.makeText(view.getContext(), "Did not log.",
                                Toast.LENGTH_SHORT).show();
                    });

            AlertDialog alert = alertBuilder.create();
            alert.show();
        });

        return view;
    }
}