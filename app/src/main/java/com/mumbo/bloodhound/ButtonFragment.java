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
import com.google.android.material.slider.RangeSlider;

import java.util.ArrayList;

import javax.annotation.Nullable;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ButtonFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ButtonFragment extends Fragment {
    AlertDialog.Builder alertBuilder;
    MaterialButton button;
    private static final String ARG_NAME = "arg_name";
    private static final String ARG_SCALE_MAX = "arg_scale_max";

    private String paramName;
    private int paramScaleMax;

    public ButtonFragment() {
        // Required empty public constructor
    }

    public static ButtonFragment newInstance(String paramName, int paramScaleMax) {
        ButtonFragment fragment = new ButtonFragment();
        Bundle args = new Bundle();
        args.putString(ARG_NAME, paramName);
        args.putInt(ARG_SCALE_MAX, paramScaleMax);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramName = getArguments().getString(ARG_NAME);
            paramScaleMax = getArguments().getInt(ARG_SCALE_MAX);
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

            final View customLayout = getLayoutInflater().inflate(R.layout.dialog, null);
            RangeSlider slider = customLayout.findViewById(R.id.range_slider);

            if (paramScaleMax > 0) {
                slider.setValueTo(this.paramScaleMax);
                alertBuilder.setView(customLayout);
            }
            ArrayList<String> logArguments = new ArrayList<>();
            logArguments.add(String.valueOf(new java.util.Date()));
            logArguments.add(this.paramName);

            alertBuilder
                    .setTitle("Log " + this.paramName)
                    .setMessage("Do you wish to write this to the log?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", (DialogInterface dialog, int id) -> {
                        dialog.cancel();
                        logArguments.add(String.valueOf(slider.getValues().get(0)));
                        GoogleSheetsAPI.writeRowToLogSheet(this.getContext(), logArguments);
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